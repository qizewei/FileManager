package com.filemanager.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filemanager.R;
import com.filemanager.adapter.ZipAdapter;
import com.filemanager.util.FileUtil;

import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ZipFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<File> mFiles;
    private ZipAdapter mAdapter;


    public ZipFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_zip, container, false);
        mFiles = FileUtil.getSpecificTypeOfFile(getContext(),new String[]{".zip",".rar"});
        mRecyclerView = (RecyclerView) ret.findViewById(R.id.id_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter = new ZipAdapter(getContext(),mFiles));
        return ret;
    }

}
