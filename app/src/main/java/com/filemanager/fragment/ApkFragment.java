package com.filemanager.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filemanager.R;
import com.filemanager.adapter.ApkAdapter;
import com.filemanager.util.FileUtil;

import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ApkFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<File> mFiles;
    private ApkAdapter mAdapter;

    public ApkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_apk, container, false);
        mFiles = FileUtil.getSpecificTypeOfFile(getContext(),new String[]{".apk",".APK"});
        mRecyclerView = (RecyclerView) ret.findViewById(R.id.id_recyclerview);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter = new ApkAdapter(getContext(),mFiles));
        return ret;
    }

}
