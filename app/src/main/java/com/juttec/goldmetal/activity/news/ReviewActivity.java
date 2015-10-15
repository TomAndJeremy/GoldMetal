package com.juttec.goldmetal.activity.news;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.customview.listview.AutoLoadListView;
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
 * Created by Jeremy on 2015/10/15.
 */
public class ReviewActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    LoadMoreListView listView;
    SwipeRefreshLayout swipeLayout;
    private MyApplication app;
    int pageIndex;
    List<Map<String, String>> maps;
    MyAdapte myAdapte;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        app = (MyApplication) getApplication();
        init();
    }

    private void init() {

        swipeLayout = (SwipeRefreshLayout) this
                .findViewById(R.id.swipe_refresh);
        // 顶部刷新的样式
        swipeLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);


        swipeLayout.setOnRefreshListener(this);


        listView = (LoadMoreListView) this.findViewById(R.id.activity_review_listview);
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
                getData(pageIndex);
            }
        });


    }

    @Override
    public void onRefresh() {

        pageIndex = 1;
        getData(pageIndex);

    }

    public void getData(int i) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("pageIndex", i + "");
        new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getGetOrgReviewUrl(), requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                swipeLayout.setRefreshing(false);

                Map<String, String> map;
                try {
                    JSONObject object = new JSONObject(responseInfo.result.toString());
                    LogUtil.e(responseInfo.result.toString());
                    int pagenum = Integer.parseInt(object.getString("message1"));
                    if ("1".equals(object.getString("status"))) {

                        JSONArray jsonArray = object.getJSONArray("entityList");


                        if (pageIndex == 1) {
                            maps.clear();
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object1 = jsonArray.getJSONObject(i);
                            map = new HashMap<String, String>();
                            map.put("title", object1.getString("title"));
                            map.put("time", object1.getString("addTime"));
                            maps.add(map);
                        }


                        if (myAdapte == null) {
                            myAdapte = new MyAdapte(maps);
                            listView.setAdapter(myAdapte);
                        } else {
                            myAdapte.notifyDataSetChanged();
                        }


                        if (pageIndex == pagenum) {
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


    private class MyAdapte extends BaseAdapter {

        List<Map<String, String>> maps;


        public MyAdapte(List<Map<String, String>> maps) {
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
                convertView = getLayoutInflater().inflate(R.layout.news_review_item,
                        parent, false);

                viewHolder = new ViewHolder();
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.news_review_item_title);
                viewHolder.tvTime = (TextView) convertView.findViewById(R.id.news_review_item_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tvTitle.setText(maps.get(position).get("title"));
            viewHolder.tvTime.setText(maps.get(position).get("time"));
            return convertView;

        }

        private class ViewHolder {
            TextView tvTitle;
            TextView tvTime;
        }
    }
}
