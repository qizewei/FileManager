package com.fileManager.fragment;


import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.utils.FileUtils;
import com.bumptech.glide.Glide;
import com.fileManager.R;
import com.fileManager.adapter.FileNameAdapter;
import com.fileManager.util.FileUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FileNameFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mRecyclerView;
    private List<File> mFiles;
    private FileNameAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private ImageView mLoading;
    private TextView mLoadingText;
    private String filename;
    private TextView noSearch;
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mRecyclerView.setAdapter(mAdapter = new FileNameAdapter(getContext(), mFiles));
                    mLoading.setVisibility(View.INVISIBLE);
                    mLoadingText.setVisibility(View.INVISIBLE);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    mAdapter.setOnItemClickLitener(new FileNameAdapter.OnItemClickLitener() {
                        @Override
                        public void onItemClick(View view, int position) {
                        }

                        @Override
                        public void onItemLongClick(View view, int position) {
                        }
                    });
                    break;
                case  0:
                    mLoading.setVisibility(View.INVISIBLE);
                    mLoadingText.setVisibility(View.INVISIBLE);
                    noSearch.setVisibility(View.VISIBLE);
                    break;
            }
            super.handleMessage(msg);
        }
    };


    public FileNameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_file_name, container, false);
        mLoading = (ImageView) ret.findViewById(R.id.loading_gif_1);
        mRecyclerView = (RecyclerView) ret.findViewById(R.id.id_recyclerview);
        mLoadingText = (TextView) ret.findViewById(R.id.loading_text);
        mRecyclerView = (RecyclerView) ret.findViewById(R.id.id_recyclerview);
        mRefreshLayout = (SwipeRefreshLayout) ret.findViewById(R.id.filename_refresh);
        noSearch = (TextView)ret.findViewById(R.id.no_search); 
        Glide.with(getContext()).load(R.drawable.loading)
                .asGif().into(mLoading);
        mFiles = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mRefreshLayout.setOnRefreshListener(this);
        initDate();
        return ret;
    }

    private void initDate() {
        //开线程初始化数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                filename = getActivity().getIntent().getStringExtra("filename");
                mFiles = FileUtil.searchFileInDir(Environment.getExternalStorageDirectory(),filename);
                Log.d("aaa", "run: "+ mFiles.size());
               
                Message message = new Message();
                if (mFiles.size()==0) {
                    message.what = 0;
                }else 
                    message.what = 1;
                myHandler.sendMessage(message);
            }
        }).start();
    }

    
    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFiles = FileUtils.listFilesInDirWithFilter(Environment.getExternalStorageDirectory(), filename);
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
        MobclickAgent.onPageStart("NameSearch_Fragment"); //统计页面，"MainScreen"为页面名称，可自定义
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("NameSearch_Fragmenn");
    }
}
