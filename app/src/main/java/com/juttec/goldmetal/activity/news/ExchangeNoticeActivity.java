package com.juttec.goldmetal.activity.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易所公告详情界面
 */

public class ExchangeNoticeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    LoadMoreListView listView;
    SwipeRefreshLayout swipeLayout;
    private MyApplication app;
    int pageIndex;
    List<Map<String, String>> maps;
    MyAdapter myAdapter;
    String id;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_listview_layout);

        Intent intent = getIntent();
        String headtitle = intent.getStringExtra("exchangeName") + "公告";

        id = intent.getStringExtra("id");
        HeadLayout headLayout = (HeadLayout) findViewById(R.id.head_layout);

        headLayout.setHeadTitle(headtitle);


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
        listView.setSelected(false);
        listView.setDividerHeight(0);

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

    @Override
    public void onRefresh() {
        pageIndex = 1;
        listView.setState(LoadingFooter.State.Idle);
        getData(pageIndex);

    }

    public void getData(int i) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("pageIndex", i + "");
        requestParams.addBodyParameter("exchangeId", id);
        new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getGetExchangeNoticeUrl(), requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                swipeLayout.setRefreshing(false);

                Map<String, String> map;
                try {
                    JSONObject object = new JSONObject(responseInfo.result.toString());
                    LogUtil.e(responseInfo.result.toString());
                    int pageNum = Integer.parseInt(object.getString("message1"));
                    if ("1".equals(object.getString("status"))) {

                        JSONArray jsonArray = object.getJSONArray("entityList");


                        if (pageIndex == 1) {
                            maps.clear();
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object1 = jsonArray.getJSONObject(i);
                            map = new HashMap<String, String>();
                            map.put("details", object1.getString("details"));
                            map.put("time", object1.getString("addTime"));
                            map.put("id", object1.getString("id"));
                            maps.add(map);
                        }


                        if (myAdapter == null) {
                            myAdapter = new MyAdapter(maps);
                            listView.setAdapter(myAdapter);
                        } else {
                            myAdapter.notifyDataSetChanged();
                        }


                        if (pageIndex == pageNum) {
                            listView.setState(LoadingFooter.State.TheEnd);
                        }
                        ++pageIndex;
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
                convertView = getLayoutInflater().inflate(R.layout.exchange_notice_item,
                        parent, false);

                viewHolder = new ViewHolder();
                viewHolder.tvContent = (TextView) convertView.findViewById(R.id.exchange_notice_item_content);
                viewHolder.tvTime = (TextView) convertView.findViewById(R.id.exchange_notice_item_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tvContent.setText(maps.get(position).get("details"));
            viewHolder.tvTime.setText(maps.get(position).get("time"));
            return convertView;

        }

        private class ViewHolder {
            TextView tvContent;
            TextView tvTime;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        listView.stopFooterAnimition();
    }
}
