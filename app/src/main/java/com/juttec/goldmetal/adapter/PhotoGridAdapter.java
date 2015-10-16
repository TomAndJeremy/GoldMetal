package com.juttec.goldmetal.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.bean.PhotoBean;
import com.juttec.goldmetal.dialog.MyAlertDialog;
import com.juttec.goldmetal.utils.ImgUtil;
import com.juttec.goldmetal.utils.LogUtil;

import java.util.List;

/**
 * 发表动态 界面    展示拍照 或是 从相册选择图片的 adapter
 */

public class PhotoGridAdapter extends BaseAdapter {

    /**
     * 上下文
     */
    private Context ctx;
    /**
     * 图片Url集合
     */
    private List<PhotoBean> imageBeans;

    //弹出提示框
    private MyAlertDialog dialog ;

    public PhotoGridAdapter(Context ctx, List<PhotoBean> urls) {
        this.ctx = ctx;
        this.imageBeans = urls;
        dialog = new MyAlertDialog(ctx);
    }

    @Override
    public int getCount() {
        return imageBeans == null ? 0 : imageBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return imageBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LogUtil.d("展示图片---------------------------");

        View view = View.inflate(ctx, R.layout.item_photo, null);
        final ImageView imageView = (ImageView) view.findViewById(R.id.iv_image);
        final ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);

        imageView.setImageBitmap(ImgUtil.getBitmap(imageBeans.get(position).getDyPhoto(), 200, 200));
        if(imageBeans.get(position).isDelete()){
            iv_delete.setVisibility(View.VISIBLE);
        }else{
            iv_delete.setVisibility(View.GONE);
        }


        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                for (int i= 0;i<imageBeans.size();i++){
                    imageBeans.get(i).setIsDelete(true);
                }
                notifyDataSetChanged();
                return true;
            }
        });


        //
        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.builder().setTitle("移除提示")
                        .setMsg("放弃上传这张照片")
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                imageBeans.remove(position);

                                notifyDataSetChanged();
                            }
                        }).setNegativeButton("取消", new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           dialog.dismiss();
                       }
                }).show();

            }
        });

        return view;
    }

}
