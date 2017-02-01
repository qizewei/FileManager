package com.filemanager.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.utils.FileUtils;
import com.bumptech.glide.Glide;
import com.filemanager.R;
import com.filemanager.adapter.ImageAdapter;
import com.filemanager.util.ACache;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mRecyclerView;
    private List<File> mFiles;
    private ImageAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private Gson mGson;
    private ImageView mLoading;
    private TextView mLoadingText;
    private ACache mCatch;
    private SharedPreferences mPreferences;

    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mRecyclerView.setAdapter(mAdapter = new ImageAdapter(getContext(), mFiles));
                    mLoading.setVisibility(View.INVISIBLE);
                    mLoadingText.setVisibility(View.INVISIBLE);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    mAdapter.setOnItemClickLitener(new ImageAdapter.OnItemClickLitener() {
                        @Override
                        public void onItemClick(View view, int position) {
                        }

                        @Override
                        public void onItemLongClick(View view, int position) {
                        }
                    });
                    break;
            }
            super.handleMessage(msg);
        }
    };


    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_image, container, false);
        mLoading = (ImageView) ret.findViewById(R.id.loading_gif);
        mRecyclerView = (RecyclerView) ret.findViewById(R.id.id_recyclerview);
        mLoadingText = (TextView) ret.findViewById(R.id.loading_text);
        mRefreshLayout = (SwipeRefreshLayout) ret.findViewById(R.id.image_refresh);
        Glide.with(getContext()).load(R.drawable.loading)
                .asGif().into(mLoading);
        mFiles = new ArrayList<>();
        mGson = new Gson();
        mCatch = ACache.get(getContext());

        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mRefreshLayout.setOnRefreshListener(this);

        initData();
        return ret;
    }


    private void initData() {
        //开线程初始化数据

        new Thread(new Runnable() {

            @Override
            public void run() {
                judge();
                Message message = new Message();
                message.what = 1;
                myHandler.sendMessage(message);
            }
        }).start();
    }

    /**
     * 判断缓存是否存在，初始化数据
     */
    private void judge() {
        try {
            mPreferences = getContext().getSharedPreferences("table", Context.MODE_PRIVATE);
        } catch (Exception e) {
            //子线程未销毁可能时执行
        }
        boolean first = mPreferences.getBoolean("firstImage", true);
        int num = mPreferences.getInt("numImage", 0);
        if (!first) {
            for (int i = 0; i < num; i++) {
                Log.d("aaa", "judge: ");
                String s = String.valueOf(i);
                String string = mCatch.getAsString(s);
                if (!string.equals("null")) {
                    File file = mGson.fromJson(string, File.class);
                    mFiles.add(file);
                }

            }
        } else {

            mFiles = FileUtils.listFilesInDirWithFilter(Environment.getExternalStorageDirectory(), ".jpg");
            addCatch();
        }
    }

    /**
     * 添加缓存
     */
    public void addCatch() {

        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < mFiles.size(); i++) {
            String s = mGson.toJson(mFiles.get(i));
            strings.add(s);
        }
        for (int i = 0; i < strings.size(); i++) {
            String s = String.valueOf(i);
            mCatch.put(s, strings.get(i), ACache.TIME_DAY);
        }


        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putBoolean("firstImage", false);
        edit.putInt("numImage", strings.size());
        edit.commit();
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFiles = FileUtils.listFilesInDirWithFilter(Environment.getExternalStorageDirectory(), ".jpg");
                addCatch();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mAdapter.notifyDataSetChanged();
                        mRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), "刷新完成", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();

    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainScreen"); //统计页面，"MainScreen"为页面名称，可自定义
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainScreen");
    }

}