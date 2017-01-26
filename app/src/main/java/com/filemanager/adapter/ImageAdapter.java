package com.filemanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.utils.FileUtils;
import com.bumptech.glide.Glide;
import com.filemanager.R;
import com.filemanager.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 齐泽威 on 2016/12/6.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
    private List<File> mDatas;
    private Context mContext;
    private List<Integer> mHeights;
    private ImageAdapter.OnItemClickLitener mOnItemClickLitener;


    public ImageAdapter(Context context, List<File> Data) {
        this.mDatas = Data;
        this.mContext = context;
        mHeights = new ArrayList<Integer>();
        for (int i = 0; i < mDatas.size(); i++) {
            mHeights.add((int) (300 + Math.random() * 500));
        }
    }

    public void setOnItemClickLitener(ImageAdapter.OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate
                (R.layout.item_image, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        ViewGroup.LayoutParams lp = holder.tv.getLayoutParams();
        lp.height = mHeights.get(position);
        holder.tv.setLayoutParams(lp);
        Glide.with(mContext)
                .load(mDatas.get(position))
                .placeholder(R.mipmap.file_image)
                .error(R.mipmap.error)
                .into(holder.tv);

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.tv, pos);
                    String path = mDatas.get(pos).getPath();
                    Intent intent = FileUtil.openFile(path);
                    mContext.startActivity(intent);
                    
                }
            });

            holder.item_image_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.tv, pos);
                    removeData(pos);
                    synchronized (this) {
                        this.notify();
                    }
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void removeData(int position) {
        String path = mDatas.get(position).getAbsolutePath();
        FileUtils.deleteFile(path);
        mDatas.remove(position);
        notifyItemRemoved(position);
        
        SharedPreferences table = mContext.getSharedPreferences("table", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = table.edit();
        String s = String.valueOf(position);
        edit.putString(s,null);
        edit.commit();
        
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView tv;
        TextView item_image_delete;

        public MyViewHolder(View view) {
            super(view);
            tv = (ImageView) view.findViewById(R.id.id_num);
            item_image_delete = (TextView) view.findViewById(R.id.item_image_delete);
        }
    }
}

