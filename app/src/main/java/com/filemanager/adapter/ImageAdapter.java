package com.filemanager.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.utils.FileUtils;
import com.bumptech.glide.Glide;
import com.filemanager.R;
import com.filemanager.util.ACache;
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
    private ACache mCache;


    public ImageAdapter(Context context, List<File> Data) {
        this.mDatas = Data;
        this.mContext = context;
        mHeights = new ArrayList<Integer>();
        try {
            mCache = ACache.get(mContext);
        }catch (Exception e){
            //子线程未销毁可能时执行
        }
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

            holder.tv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    final EditText userId = new EditText(mContext);
                    builder.setTitle("请输入新命名：")
                            .setCancelable(false)
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String newName = userId.getText().toString().trim();
                                    if (newName.equals("")) {
                                        Toast.makeText(mContext, "输入不能为空", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //重命名File
                                        FileUtils.rename(mDatas.get(position), newName + ".jpg");
                                        
                                        //更新显示数据
                                        String path = mDatas.get(position).getParent();
                                        File file = new File(path + "/" + newName + ".jpg");
                                        mDatas.remove(position);
                                        mDatas.add(position ,file);
                                        notifyDataSetChanged();
                                        Toast.makeText(mContext, "重命名文件成功", Toast.LENGTH_SHORT).show();
                                        
                                        //更新缓存
                                        String s = String.valueOf(position);
                                        String name = "{\"path\":\""+file.getAbsolutePath()+"\"}" ;
                                        mCache.put(s, name);

                                    }
                                }
                            })
                            .setView(userId, 150, 20, 70, 20)
                            .show();


                    return false;
                }
            });

            holder.item_image_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.tv, pos);
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("确认删除");
                    builder.setMessage("是否确认删除该文件");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeData(position);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

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

    private void removeData(int position) {

        String path = mDatas.get(position).getAbsolutePath();
        FileUtils.deleteFile(path);
        mDatas.remove(position);
        notifyItemRemoved(position);

        String s = String.valueOf(position);

        mCache.put(s, "null");
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

