package com.juttec.goldmetal.activity.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
 * Created by Jeremy on 2015/10/15.
 * 机构评论界面   财经头条界面
 */
public class ReviewActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    LoadMoreListView listView;
    SwipeRefreshLayout swipeLayout;
    private MyApplication app;
    int pageIndex;
    List<Map<String, String>> maps;
    MyAdapter myAdapter;

    private String headtitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_listview_layout);

        app = (MyApplication) getApplication();
        headtitle = getIntent().getStringExtra(NewsFragment.HEADTITLE);
        init();
    }

    private void init() {
        HeadLayout headLayout = (HeadLayout) findViewById(R.id.head_layout);
        headLayout.setHeadTitle(headtitle);

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
                Intent intent = new Intent(getApplicationContext(), NewsDetailActivity.class);
                intent.putExtra("title", maps.get(position).get("title"));
                intent.putExtra("time", maps.get(position).get("time"));
                intent.putExtra("id", maps.get(position).get("id"));
                intent.putExtra("type", headtitle);

                startActivity(intent);
            }
        });

        listView.setOnLoadNextListener(new LoadMoreListView.OnLoadNextListener() {
            @Override
            public void onLoadNext() {

                getData(pageIndex);
                LogUtil.d("onLoadNext()----------");
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
        LogUtil.d("onRefresh()----------");
    }

    public void getData(int i) {
        String url = null;
        if(headtitle.equals(NewsFragment.HEADTLINES)){
            //财经头条接口
            url = app.getGetFinanceInforUrl();

        }else if(headtitle.equals(NewsFragment.REVIEW)){
            //机构评论接口
            url = app.getGetOrgReviewUrl();
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
                    int pagenum = Integer.parseInt(object.getString("message1"));
                    if(pagenum==0){
                        ToastUtil.showShort(ReviewActivity.this,"还没有数据，请再等等");
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
                            map.put("title", object1.getString("title"));
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
                convertView = getLayoutInflater().inflate(R.layout.news_review_item,
                        parent, false);

                viewHolder = new ViewHolder();
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.news_review_item_title);
                viewHolder.tvTimeHead = (TextView) convertView.findViewById(R.id.tv_time_headlines);
                viewHolder.tvTimeReview= (TextView) convertView.findViewById(R.id.tv_time_review);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tvTitle.setText(maps.get(position).get("title"));
            if(headtitle.equals(NewsFragment.HEADTLINES)){
                //财经头条界面
                viewHolder.tvTimeReview.setVisibility(View.GONE);
                viewHolder.tvTimeHead.setText(maps.get(position).get("time"));

            }else if(headtitle.equals(NewsFragment.REVIEW)){
                //机构评论界面
                viewHolder.tvTimeHead.setVisibility(View.GONE);
                viewHolder.tvTimeReview.setText(maps.get(position).get("time"));
            }

            return convertView;

        }

        private class ViewHolder {
            TextView tvTitle;
            TextView tvTimeHead;//财经头条的时间
            TextView tvTimeReview;//机构评论的时间

        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        listView.stopFooterAnimition();
    }
}
