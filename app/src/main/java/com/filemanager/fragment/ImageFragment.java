package com.filemanager.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import com.blankj.utilcode.utils.FileUtils;
import com.bumptech.glide.Glide;
import com.filemanager.R;
import com.filemanager.adapter.ImageAdapter;
import com.google.gson.Gson;

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

    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_image, container, false);
        mLoading = (ImageView) ret.findViewById(R.id.loading_gif);
        Glide.with(getContext()).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1485447799071&di=638073d1829f47853c59e81b6d0bb1bd&imgtype=0&src=http%3A%2F%2Fwww.shejicool.com%2Fuploads%2Fallimg%2F201409%2F1PAV042-10.gif")
                .asGif().into(mLoading);
        mFiles = new ArrayList<>();
        mGson = new Gson();

        mRecyclerView = (RecyclerView) ret.findViewById(R.id.id_recyclerview);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));

        mRefreshLayout = (SwipeRefreshLayout) ret.findViewById(R.id.image_refresh);
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
                
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.setAdapter(mAdapter = new ImageAdapter(getContext(), mFiles));
                        mLoading.setVisibility(View.INVISIBLE);
                        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        mAdapter.setOnItemClickLitener(new ImageAdapter.OnItemClickLitener() {
                            @Override
                            public void onItemClick(View view, int position) {
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 判断缓存是否存在，初始化数据
     */
    private void judge() {
        SharedPreferences table = getContext().getSharedPreferences("table", Context.MODE_PRIVATE);
        boolean first = table.getBoolean("first", false);
        int num = table.getInt("num", 0);
        if (first) {
            for (int i = 0; i < num; i++) {
                String s = String.valueOf(i);
                String string = table.getString(s, null);
                if (string!=null) {
                    File file = mGson.fromJson(string, File.class);
                    mFiles.add(file);
                }
                else
                    Log.d("nullllll", "judge: ");
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
        SharedPreferences first = getActivity().getSharedPreferences("table", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = first.edit();

        for (int i = 0; i < mFiles.size(); i++) {
            String s = mGson.toJson(mFiles.get(i));
            strings.add(s);
        }

        edit.putBoolean("first", true);
        edit.putInt("num", strings.size());
        for (int i = 0; i < strings.size(); i++) {
            String s = String.valueOf(i);
            edit.putString(s, strings.get(i));
        }
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

}
