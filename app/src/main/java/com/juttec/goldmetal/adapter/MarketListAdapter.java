package com.juttec.goldmetal.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.bean.MarketFormInfo;
import com.juttec.goldmetal.bean.MessageBean;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Jeremy on 2015/10/15.
 */
public class MarketListAdapter extends BaseAdapter {
    List<MarketFormInfo.ResultEntity> datas;
    Context context;


    public MarketListAdapter(List<MarketFormInfo.ResultEntity> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    public void notifyData(List<MarketFormInfo.ResultEntity> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.fragment_market_rv_item, viewGroup, false);

            holder = new ViewHolder();

            holder.tvType = (TextView) view.findViewById(R.id.tv_type);
            holder.tvSale = (TextView) view.findViewById(R.id.tv_sale);
            holder.tvBuy = (TextView) view.findViewById(R.id.tv_buy);
            holder.tvHigh = (TextView) view.findViewById(R.id.tv_high);
            holder.tvLow = (TextView) view.findViewById(R.id.tv_low);
            holder.tvIncrease = (TextView) view.findViewById(R.id.tv_increase);
            holder.tvScale = (TextView) view.findViewById(R.id.tv_scale);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        MarketFormInfo.ResultEntity resultEntity = datas.get(i);
        holder.tvType.setText(resultEntity.getName());
        holder.tvSale.setText(resultEntity.getSP1());
        holder.tvBuy.setText(resultEntity.getBP1());
        holder.tvHigh.setText(resultEntity.getHigh());
        holder.tvLow.setText(resultEntity.getLow());
        holder.tvIncrease.setText(resultEntity.getSP1());
        holder.tvScale.setText(resultEntity.getPriceChangeRatio());

        if (Double.parseDouble(resultEntity.getBP1()) - Double.parseDouble(resultEntity.getSP1()) < 0) {
            holder.tvSale.setTextColor(Color.RED);
            holder.tvBuy.setTextColor(Color.RED);
            holder.tvIncrease.setBackgroundColor(Color.RED);
        } else {
            holder.tvSale.setTextColor(Color.GREEN);
            holder.tvBuy.setTextColor(Color.GREEN);
            holder.tvIncrease.setBackgroundColor(Color.GREEN);
        }



        return view;
    }

    class ViewHolder {

        TextView tvType;
        TextView tvSale;
        TextView tvBuy;
        TextView tvHigh;
        TextView tvLow;
        TextView tvIncrease;
        TextView tvScale;


    }

}
