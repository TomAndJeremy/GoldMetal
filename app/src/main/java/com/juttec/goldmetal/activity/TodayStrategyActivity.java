package com.juttec.goldmetal.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.customview.HeadLayout;
import com.juttec.goldmetal.customview.listview.LoadMoreListView;
import com.juttec.goldmetal.customview.listview.LoadingFooter;
import com.juttec.goldmetal.fragment.NewsFragment;
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
 * 今日策略   快讯直播  界面
 */

public class TodayStrategyActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private String head_title = "今日策略";//标题栏
    LoadMoreListView listView;
    SwipeRefreshLayout swipeLayout;
    private MyApplication app;
    int pageIndex;
    List<Map<String, String>> maps;
    MyAdapter myAdapter;
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.general_listview_layout);
        app = (MyApplication) getApplication();

        head_title = getIntent().getStringExtra(NewsFragment.HEADTITLE);
        init();
    }

    private void init() {
        HeadLayout headLayout = (HeadLayout) findViewById(R.id.head_layout);
        TextView tv_title = (TextView) headLayout.findViewById(R.id.head_title);
        tv_title.setText(head_title);


        swipeLayout = (SwipeRefreshLayout) this
                .findViewById(R.id.refreshlayout);
        // 顶部刷新的样式
        swipeLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);


        swipeLayout.setOnRefreshListener(this);


        listView = (LoadMoreListView) this.findViewById(R.id.listview);
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

    public void getData(int i) {
        String url = null;
        if (head_title.equals(NewsFragment.BROADCAST)) {
            //快讯直播
            url = app.getGetNewsFlashUrl();
        } else if (head_title.equals("今日策略")) {
            url = app.getGetTodayStrategyUrl();
        }

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("pageIndex", i + "");
        new HttpUtils().send(HttpRequest.HttpMethod.POST, url, requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                swipeLayout.setRefreshing(false);
                Map<String, String> map;
                try {
                    JSONObject object = new JSONObject(responseInfo.result.toString());
                    LogUtil.e(responseInfo.result.toString());
                    int pageNum = Integer.parseInt(object.getString("message1"));
                    if (pageNum == 0) {
                        ToastUtil.showShort(TodayStrategyActivity.this, "还没有数据，请再等等");
                        return;
                    }
                    if ("1".equals(object.getString("status"))) {
                        JSONArray jsonArray = object.getJSONArray("entityList");
                        if (pageIndex == 1) {
                            maps.clear();
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object1 = jsonArray.getJSONObject(i);
                            map = new HashMap<String, String>();
                            map.put("id", object1.getString("id"));
                            if (object.get("title") != null) {

                                map.put("title", object1.getString("title"));
                            } else {
                                map.put("title", "");
                            }
                            String time = object1.getString("addTime");
                            time = time.replace(" ", "\n");
                            map.put("time", time);
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

    @Override
    public void onRefresh() {
        pageIndex = 1;
        listView.setState(LoadingFooter.State.Idle);
        getData(pageIndex);

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
                convertView = getLayoutInflater().inflate(R.layout.today_strategy_item,
                        parent, false);

                viewHolder = new ViewHolder();
                viewHolder.tvContent = (TextView) convertView.findViewById(R.id.today_strategy_item_content);
                viewHolder.tvTime = (TextView) convertView.findViewById(R.id.today_strategy_item_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (head_title.equals(NewsFragment.BROADCAST)) {
                //快讯直播  设置内容的背景
                viewHolder.tvContent.setBackgroundResource(R.drawable.exchange_notice_textview_bg);
            }
            viewHolder.tvContent.setText(maps.get(position).get("title"));
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