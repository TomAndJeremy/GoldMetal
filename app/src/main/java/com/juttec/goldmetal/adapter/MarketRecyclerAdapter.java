package com.juttec.goldmetal.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.bean.MarketFormInfo;

import java.util.List;

/**
 * Created by Jeremy on 2015/10/15.
 */
public class MarketRecyclerAdapter extends RecyclerView.Adapter<MarketRecyclerAdapter.ViewHolder> {

    List<MarketFormInfo.ResultEntity> datas ;
    Context context;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;


    public MarketRecyclerAdapter(List<MarketFormInfo.ResultEntity> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }
    public MarketRecyclerAdapter( Context context) {

        this.context = context;
    }


    @Override

    public MarketRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_market_rv_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MarketRecyclerAdapter.ViewHolder holder, final int position) {

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




        MarketFormInfo.ResultEntity resultEntity = datas.get(position);

        holder.tvType.setText(resultEntity.getName());
        holder.tvSale.setText(resultEntity.getSP1());
        holder.tvBuy.setText(resultEntity.getBP1());
        holder.tvHigh.setText(resultEntity.getHigh());
        holder.tvLow.setText(resultEntity.getLow());
        holder.tvIncrease.setText(resultEntity.getSP1());
        holder.tvScale.setText(resultEntity.getPriceChangeRatio());

        if(Double.parseDouble(resultEntity.getBP1())-Double.parseDouble(resultEntity.getSP1())<0){
            holder.tvSale.setTextColor(Color.RED);
            holder.tvBuy.setTextColor(Color.RED);
            holder.tvIncrease.setBackgroundColor(Color.RED);
        } else {
            holder.tvSale.setTextColor(Color.GREEN);
            holder.tvBuy.setTextColor(Color.GREEN);
            holder.tvIncrease.setBackgroundColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvType;
        TextView tvSale;
        TextView tvBuy;
        TextView tvHigh;
        TextView tvLow;
        TextView tvIncrease;
        TextView tvScale;

        public ViewHolder(View itemView) {
            super(itemView);
             tvType= (TextView) itemView.findViewById(R.id.tv_type);
             tvSale= (TextView) itemView.findViewById(R.id.tv_sale);
             tvBuy= (TextView) itemView.findViewById(R.id.tv_buy);
             tvHigh= (TextView) itemView.findViewById(R.id.tv_high);
             tvLow= (TextView) itemView.findViewById(R.id.tv_low);
             tvIncrease= (TextView) itemView.findViewById(R.id.tv_increase);
             tvScale= (TextView) itemView.findViewById(R.id.tv_scale);

        }
    }





    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.mOnItemLongClickListener = listener;
    }


    //item的 短按 事件接口   进入到详情
    public interface OnItemClickListener{
         void itemclickListener(View view ,int position);
    }

    //item的 长按事件接口  加入到自选 或从自选中移除
    public interface  OnItemLongClickListener{
        void itemLongClickListener(View view,int position);
    }
}
