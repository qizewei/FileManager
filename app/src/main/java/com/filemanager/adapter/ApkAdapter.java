package com.fileManager.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.utils.FileUtils;
import com.blankj.utilcode.utils.TimeUtils;
import com.fileManager.R;
import com.fileManager.util.ACache;
import com.fileManager.util.ApkDetial;
import com.fileManager.util.FileUtil;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 齐泽威 on 2016/12/7.
 */

public class ApkAdapter extends RecyclerView.Adapter<ApkAdapter.MyViewHolder> {
    private List<File> mDatas;
    private Context mContext;
    private Gson mGson;
    private ApkAdapter.OnItemClickLitener mOnItemClickLitener;
    private ACache mCache;

    public ApkAdapter(Context context, List<File> Data) {
        this.mDatas = Data;
        this.mContext = context;
        this.mGson = new Gson();
        try {
            mCache = ACache.get(mContext);
        } catch (Exception e) {
            //子线程未销毁可能时执行
        }
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
    public void onBindViewHolder(final ApkAdapter.MyViewHolder holder, int position) {
        String name = mDatas.get(position).getName();

        ApkDetial detial = apkInfo(mDatas.get(position).getAbsolutePath(), mContext);
        if (detial.getName()!=null) {
            holder.tv.setText(detial.getName());
        }else 
        holder.tv.setText(mDatas.get(position).getName());
        Drawable icon = detial.getIcon();
        if (icon!=null) {
            holder.icon.setImageDrawable(icon);
        }

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
                    final String items[] = {"重命名文件", "文件详情", "分享"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);  //先得到构造器  
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //dialog.dismiss();  
                            if (which == 0) {
                                ReName(holder.getAdapterPosition());
                            } else if (which == 1)
                                ShowDetial(holder.getAdapterPosition());
                            else if (which == 2) {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("application/vnd.android.package-archive");
                                Uri uri = Uri.fromFile(mDatas.get(holder.getAdapterPosition()));
                                intent.putExtra(Intent.EXTRA_STREAM, uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(Intent.createChooser(intent, "分享到"));
                            }
                        }
                    });
                    builder.create().show();
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
                            removeData(holder.getAdapterPosition());
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

        for (int i = 0; i < mDatas.size(); i++) {
            String s = String.valueOf(i);
            mCache.remove(s+"apk");
        }
        
        mDatas.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, position + 1);
        
        //reset all catch
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < mDatas.size(); i++) {
            String s = mGson.toJson(mDatas.get(i));
            Log.d("aaa", "removeData: "+ s);
            strings.add(s);
        }
        for (int i = 0; i < strings.size(); i++) {
            String s = String.valueOf(i);
            mCache.put(s + "apk", strings.get(i), ACache.TIME_DAY);
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * 获取apk包的信息：版本号，名称，图标等
     *
     * @param absPath apk包的绝对路径
     * @param context
     */
    public ApkDetial apkInfo(String absPath, Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath, PackageManager.GET_ACTIVITIES);
        ApkDetial detial = new ApkDetial();
        if (pkgInfo != null) {
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
            /* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
            appInfo.sourceDir = absPath;
            appInfo.publicSourceDir = absPath;
            detial.setName(pm.getApplicationLabel(appInfo).toString());// 得到应用名 
            detial.setVersion(pkgInfo.versionName); // 得到版本信息
            detial.setIcon(pm.getApplicationIcon(appInfo));// 得到图标信息

        }
        return detial;
    }

    private void ShowDetial(int position) {
        File file = mDatas.get(position);
        String size = FileUtils.getFileSize(file);
        String name = file.getName();
        String path = file.getAbsolutePath();
        String time = TimeUtils.milliseconds2String(file.lastModified());

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("文件属性")
                .setCancelable(false)
                .setNegativeButton("确定", null)
                .setMessage("\n" + "文件名：" + name + "\n\n" + "文件大小：" + size + "\n\n" + "文件路径：" +
                        path + "\n\n" + "时间：" + time)
                .show();

    }

    private void ReName(final int position) {
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
                            FileUtils.rename(mDatas.get(position), newName + ".zip");

                            //更新显示数据
                            String path = mDatas.get(position).getParent();
                            File file = new File(path + "/" + newName + ".zip");
                            mDatas.remove(position);
                            mDatas.add(position, file);
                            notifyDataSetChanged();
                            Toast.makeText(mContext, "重命名文件成功", Toast.LENGTH_SHORT).show();

                            //更新缓存
                            String s = String.valueOf(position);
                            String name = "{\"path\":\"" + file.getAbsolutePath() + "\"}";
                            mCache.put(s + "zip", name);

                        }
                    }
                })
                .setView(userId, 150, 20, 70, 20)
                .show();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        LinearLayout Linear;
        TextView item_apk_delete;
        ImageView icon;

        MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.item_apk_filename);
            Linear = (LinearLayout) view.findViewById(R.id.apk_linear);
            item_apk_delete = (TextView) view.findViewById(R.id.item_apk_delete);
            icon = (ImageView) view.findViewById(R.id.apk_icon);
        }
    }
}

