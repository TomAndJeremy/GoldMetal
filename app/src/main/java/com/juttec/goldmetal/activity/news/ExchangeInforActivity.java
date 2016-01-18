package com.juttec.goldmetal.activity.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.customview.HeadLayout;
import com.juttec.goldmetal.customview.listview.LoadMoreListView;
import com.juttec.goldmetal.customview.listview.LoadingFooter;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.NetWorkUtils;
import com.juttec.goldmetal.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jeremy on 2015/10/16.
 *
 * 交易所公告 界面
 */
public class ExchangeInforActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    LoadMoreListView listView;
    SwipeRefreshLayout swipeLayout;
    private MyApplication app;
    int pageIndex;
    List<Map<String, String>> maps;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_listview_layout);
        HeadLayout headLayout = (HeadLayout) this.findViewById(R.id.head_layout);
        headLayout.setHeadTitle(getResources().getString(R.string.news_announcement));

        app = (MyApplication) getApplication();
        init();

    }

    private void init() {
        swipeLayout = (SwipeRefreshLayout) this
                .findViewById(R.id.refreshlayout);
        // 顶部刷新的样式
        swipeLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);


        swipeLayout.setOnRefreshListener(this);


        listView = (LoadMoreListView) this.findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ExchangeNoticeActivity.class);
                intent.putExtra("exchangeName", maps.get(position).get("exchangeName"));
                intent.putExtra("id", maps.get(position).get("id"));
                startActivity(intent);
            }
        });
        listView.setOnLoadNextListener(new LoadMoreListView.OnLoadNextListener() {
            @Override
            public void onLoadNext() {
                getData(pageIndex);

            }
        });
        maps = new ArrayList<>();
        pageIndex = 1;
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {

                swipeLayout.setRefreshing(true);
                getData(pageIndex);
            }
        });

    }

    public void getData(int i) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("pageIndex", i + "");
        new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getGetExchangeInforUrl(), requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                swipeLayout.setRefreshing(false);

                Map<String, String> map;
                try {
                    JSONObject object = new JSONObject(responseInfo.result.toString());
                    LogUtil.e(responseInfo.result.toString());
                    int pagenum = Integer.parseInt(object.getString("message1"));
                    if(pagenum==0){
                        ToastUtil.showShort(ExchangeInforActivity.this,"还没有数据，请再等等");
                        return;
                    }

                    if ("1".equals(object.getString("status"))) {

                        JSONArray jsonArray = object.getJSONArray("entityList");


                        if (pageIndex == 1) {
                            LogUtil.d("清除数据------------");
                            maps.clear();
                        }
                        LogUtil.d("加载数据------------"+pageIndex+pagenum);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object1 = jsonArray.getJSONObject(i);
                            map = new HashMap<String, String>();
                            map.put("id", object1.getString("id"));
                            map.put("exchangeName", object1.getString("exchangeName"));
                            map.put("exchangePhoto", object1.getString("exchangePhoto"));
                            map.put("exchangeIntro", object1.getString("exchangeIntro"));

                            maps.add(map);
                        }


                        if (myAdapter == null) {
                            myAdapter = new MyAdapter(maps);
                            listView.setAdapter(myAdapter);
                        } else {
                            myAdapter.notifyDataSetChanged();
                        }


                        if (pageIndex == pagenum) {
                            listView.setState(LoadingFooter.State.TheEnd);
                        }else{
                            ++pageIndex;
                        }

                    } else {
                        ToastUtil.showShort(getApplicationContext(), object.getString("promptInfor"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(HttpException error, String msg) {

                swipeLayout.setRefreshing(false);
                NetWorkUtils.showMsg(getApplicationContext());
            }
        });
    }

    @Override
    public void onRefresh() {
        pageIndex = 1;
        listView.setState(LoadingFooter.State.Idle);
        getData(pageIndex);

    }


    /**
     * 普通的适配器
     */
    private class MyAdapter extends BaseAdapter {

        List<Map<String, String>> maps;

        public MyAdapter(List<Map<String, String>> maps) {
            this.maps = maps;

        }

        @Override

        public int getCount() {
            return maps.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.news_exchangeinfor_item,
                        parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title=(TextView) convertView.findViewById(R.id.news_exchange_item_name);
                viewHolder.summary = (TextView) convertView.findViewById(R.id.news_exchange_item_infor);
                viewHolder.img = (ImageView) convertView.findViewById(R.id.news_exchange_item_img);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }



            viewHolder.title.setText(maps.get(position).get("exchangeName"));
            viewHolder.summary.setText(maps.get(position).get("exchangeIntro"));
            ImageLoader.getInstance().displayImage(MyApplication.ImgBASEURL + maps.get(position).get("exchangePhoto"), viewHolder.img);

            return convertView;
        }

    }

    private static class ViewHolder {
        TextView title;
        TextView summary;
        ImageView img;
    }

}
