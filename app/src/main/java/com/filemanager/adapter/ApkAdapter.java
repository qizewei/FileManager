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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.utils.FileUtils;
import com.filemanager.R;
import com.filemanager.util.ACache;
import com.filemanager.util.FileUtil;

import java.io.File;
import java.util.List;

/**
 * Created by 齐泽威 on 2016/12/7.
 */

public class ApkAdapter extends RecyclerView.Adapter<ApkAdapter.MyViewHolder> {
    private List<File> mDatas;
    private Context mContext;
    private ApkAdapter.OnItemClickLitener mOnItemClickLitener;
    private ACache mCache;

    public ApkAdapter(Context context, List<File> Data) {
        this.mDatas = Data;
        this.mContext = context;
        mCache = ACache.get(mContext);
    }

    @Override
    public ApkAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ApkAdapter.MyViewHolder holder = new ApkAdapter.MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_apk, parent,
                false));
        return holder;
    }

    public void setOnItemClickLitener(ApkAdapter.OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }


    @Override
    public void onBindViewHolder(final ApkAdapter.MyViewHolder holder, final int position) {
        holder.tv.setText(mDatas.get(position).getName());

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.Linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.Linear, pos);

                    String path = mDatas.get(pos).getPath();
                    Intent intent = FileUtil.openFile(path);
                    mContext.startActivity(intent);

                }
            });
            holder.Linear.setOnLongClickListener(new View.OnLongClickListener() {
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
                                        FileUtils.rename(mDatas.get(position), newName + ".apk");

                                        //更新显示数据
                                        String path = mDatas.get(position).getParent();
                                        File file = new File(path + "/" + newName + ".apk");
                                        mDatas.remove(position);
                                        mDatas.add(position, file);
                                        notifyDataSetChanged();
                                        Toast.makeText(mContext, "重命名文件成功", Toast.LENGTH_SHORT).show();

                                        //更新缓存
                                        String s = String.valueOf(position);
                                        String name = "{\"path\":\"" + file.getAbsolutePath() + "\"}";
                                        mCache.put(s + "apk", name);

                                    }
                                }
                            })
                            .setView(userId, 150, 20, 70, 20)
                            .show();


                    return false;
                }
            });

            holder.item_apk_delete.setOnClickListener(new View.OnClickListener() {
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

    private void removeData(int position) {
        String path = mDatas.get(position).getAbsolutePath();
        FileUtils.deleteFile(path);
        mDatas.remove(position);
        notifyItemRemoved(position);

        String s = String.valueOf(position);
        mCache.put(s + "apk", "null");
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        LinearLayout Linear;
        TextView item_apk_delete;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.item_apk_filename);
            Linear = (LinearLayout) view.findViewById(R.id.apk_linear);
            item_apk_delete = (TextView) view.findViewById(R.id.item_apk_delete);

        }
    }
}

