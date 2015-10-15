package com.juttec.goldmetal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.bean.MarketFormInfo;

import java.util.List;

/**
 * Created by Jeremy on 2015/10/15.
 */
public class MarketRecyclerAdapter extends RecyclerView.Adapter<MarketRecyclerAdapter.ViewHolder> {

   List<MarketFormInfo> marketFormInfos;
    Context context;
    private OnItemClickListener mOnItemClickListener;

    public MarketRecyclerAdapter(List<MarketFormInfo> marketFormInfos, Context context) {
        this.marketFormInfos = marketFormInfos;
        this.context = context;
    }
    public MarketRecyclerAdapter( Context context) {

        this.context = context;
    }


    @Override

    public MarketRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.fragment_market_rv_item, null);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MarketRecyclerAdapter.ViewHolder holder, final int position) {

      holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.itemclickListener(v, position);
            }
        });

        holder.imgRise.setVisibility(View.GONE);
        holder.imgFall.setVisibility(View.GONE);

        holder.tvType.setText(marketFormInfos.get(position).getType());

        holder.tvSale.setText(marketFormInfos.get(position).getSale());
        holder.tvBuy.setText(marketFormInfos.get(position).getBuy());
        holder.tvHigh.setText(marketFormInfos.get(position).getHigh());
        holder.tvLow.setText(marketFormInfos.get(position).getLow());
        holder.tvIncrease.setText(marketFormInfos.get(position).getIncrease());
        holder.tvScale.setText(marketFormInfos.get(position).getScale());
        String riseOrFal = marketFormInfos.get(position).getRiseOrFall();
        if ("1".equals(riseOrFal)) {
            holder.imgRise.setVisibility(View.VISIBLE);

        } else {
            holder.imgFall.setVisibility(View.VISIBLE);

        }



    }

    @Override
    public int getItemCount() {
        return marketFormInfos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvType;
        TextView tvSale;
        TextView tvBuy;
        TextView tvHigh;
        TextView tvLow;
        TextView tvIncrease;
        TextView tvScale;
        ImageView imgRise;
        ImageView imgFall;

        public ViewHolder(View itemView) {
            super(itemView);
             tvType= (TextView) itemView.findViewById(R.id.tv_type);
             tvSale= (TextView) itemView.findViewById(R.id.tv_sale);
             tvBuy= (TextView) itemView.findViewById(R.id.tv_buy);
             tvHigh= (TextView) itemView.findViewById(R.id.tv_high);
             tvLow= (TextView) itemView.findViewById(R.id.tv_low);
             tvIncrease= (TextView) itemView.findViewById(R.id.tv_increase);
             tvScale= (TextView) itemView.findViewById(R.id.tv_scale);
             imgRise= (ImageView) itemView.findViewById(R.id.iv_rise);
             imgFall= (ImageView) itemView.findViewById(R.id.iv_fall);

        }
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener{
         void itemclickListener(View view ,int position);
    }
}
