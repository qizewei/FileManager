package com.filemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.filemanager.R;

import java.io.File;
import java.util.List;

/**
 * Created by 齐泽威 on 2016/12/7.
 */

public class ApkAdapter extends RecyclerView.Adapter<ApkAdapter.MyViewHolder> {
    private List<File> mDatas;
    private Context mContext;

    public ApkAdapter(Context context, List<File> Data) {
        this.mDatas = Data;
        this.mContext = context;
    }

    @Override
    public ApkAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ApkAdapter.MyViewHolder holder = new ApkAdapter.MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_apk, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ApkAdapter.MyViewHolder holder, final int position) {
        holder.tv.setText(mDatas.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.item_apk_filename);
        }
    }
}

