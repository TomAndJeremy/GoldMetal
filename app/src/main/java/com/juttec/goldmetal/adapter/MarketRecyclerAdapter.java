package com.juttec.goldmetal.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.bean.MarketFormInfo;

import java.util.List;

/**
 * Created by Jeremy on 2015/10/15.
 */
public class MarketRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<MarketFormInfo.ResultEntity> datas;
    Context context;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private int itemNum;

    public MarketRecyclerAdapter(List<MarketFormInfo.ResultEntity> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    public MarketRecyclerAdapter(Context context) {

        this.context = context;
    }


    public void notifyData(List<MarketFormInfo.ResultEntity> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }


    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.fragment_market_rv_item, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        } else if (viewType == TYPE_FOOTER) {
            TextView view = new TextView(context);
            view.setText("正在加载下一页");



            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


            lp.gravity = Gravity.CENTER_HORIZONTAL;

            view.setLayoutParams(lp);
            view.setTextColor(Color.rgb(255,255,255));
            return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof MyViewHolder) {
            //短按事件注册
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.itemclickListener(v, position);
                }
            });


            //长按事件注册
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemLongClickListener.itemLongClickListener(v, position);

                    return true;
                }
            });


            if (position<datas.size()) {
                MarketFormInfo.ResultEntity resultEntity = datas.get(position);

                ((MyViewHolder)holder).tvType.setText(resultEntity.getName());
                ((MyViewHolder)holder).tvSale.setText(resultEntity.getSP1());
                ((MyViewHolder)holder).tvBuy.setText(resultEntity.getBP1());
                ((MyViewHolder)holder).tvHigh.setText(resultEntity.getHigh());
                ((MyViewHolder)holder).tvLow.setText(resultEntity.getLow());
                ((MyViewHolder)holder).tvIncrease.setText(resultEntity.getSP1());
                ((MyViewHolder)holder).tvScale.setText(resultEntity.getPriceChangeRatio());

                if (Double.parseDouble(resultEntity.getBP1()) - Double.parseDouble(resultEntity.getSP1()) < 0) {
                    ((MyViewHolder)holder).tvSale.setTextColor(Color.RED);
                    ((MyViewHolder)holder).tvBuy.setTextColor(Color.RED);
                    ((MyViewHolder)holder).tvIncrease.setBackgroundColor(Color.RED);
                } else {
                    ((MyViewHolder)holder).tvSale.setTextColor(Color.GREEN);
                    ((MyViewHolder)holder).tvBuy.setTextColor(Color.GREEN);
                    ((MyViewHolder)holder).tvIncrease.setBackgroundColor(Color.GREEN);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemNum;
    }

    public void addView(boolean b) {
        if (b) {
            itemNum = datas.size() + 1;
        }

    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View view) {
            super(view);
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvType;
        TextView tvSale;
        TextView tvBuy;
        TextView tvHigh;
        TextView tvLow;
        TextView tvIncrease;
        TextView tvScale;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvType = (TextView) itemView.findViewById(R.id.tv_type);
            tvSale = (TextView) itemView.findViewById(R.id.tv_sale);
            tvBuy = (TextView) itemView.findViewById(R.id.tv_buy);
            tvHigh = (TextView) itemView.findViewById(R.id.tv_high);
            tvLow = (TextView) itemView.findViewById(R.id.tv_low);
            tvIncrease = (TextView) itemView.findViewById(R.id.tv_increase);
            tvScale = (TextView) itemView.findViewById(R.id.tv_scale);

        }
    }


    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }


    //item的 短按 事件接口   进入到详情
    public interface OnItemClickListener {
        void itemclickListener(View view, int position);
    }

    //item的 长按事件接口  加入到自选 或从自选中移除
    public interface OnItemLongClickListener {
        void itemLongClickListener(View view, int position);
    }

    @Override
    public int getItemViewType(int position) {

        // 最后一个item设置为footerView
        if (position+1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }
}
