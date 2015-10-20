package com.juttec.goldmetal.activity.news;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
 * 投资机构界面
 */

public class InvestmentOrgActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    LoadMoreListView listView;
    SwipeRefreshLayout swipeLayout;
    private MyApplication app;
    int pageIndex=1;
    List<Map<String, String>> maps;
    MyAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_listview_layout);

        app = (MyApplication) getApplication();
        init();

    }

    private void init() {

        HeadLayout headLayout = (HeadLayout) this.findViewById(R.id.head_layout);
        headLayout.setHeadTitle(getResources().getString(R.string.news_institution));

        swipeLayout = (SwipeRefreshLayout) this
                .findViewById(R.id.refreshlayout);
        // 顶部刷新的样式
        swipeLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);

        swipeLayout.post(new Runnable() {
            @Override
            public void run() {

                swipeLayout.setRefreshing(true);
                pageIndex = 1;
                getData(pageIndex);
            }
        });

        swipeLayout.setOnRefreshListener(this);
        maps = new ArrayList<>();

        listView = (LoadMoreListView) this.findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), NewsDetailActivity.class);

                intent.putExtra("id", maps.get(position).get("id"));
                intent.putExtra("type", "institution");
                startActivity(intent);
            }
        });
        listView.setOnLoadNextListener(new LoadMoreListView.OnLoadNextListener() {
            @Override
            public void onLoadNext() {

                getData(pageIndex);

            }
        });



    }

    private void getData(int i) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("pageIndex", i + "");
        new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getGetInvestmentOrgUrl(), requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                swipeLayout.setRefreshing(false);

                Map<String, String> map;
                try {
                    JSONObject object = new JSONObject(responseInfo.result.toString());

                    int pageNum = Integer.parseInt(object.getString("message1"));


                    if ("1".equals(object.getString("status"))) {

                        JSONArray jsonArray = object.getJSONArray("entityList");


                        if (pageIndex == 1) {
                            maps.clear();
                        }
                        LogUtil.e(responseInfo.result.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object1 = jsonArray.getJSONObject(i);
                            map = new HashMap<>();
                            map.put("id", object1.getString("id"));
                            map.put("orgName", object1.getString("orgName"));
                            map.put("photo", object1.getString("photo"));

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
                convertView = getLayoutInflater().inflate(R.layout.news_invesrment_org_item,
                        parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title=(TextView) convertView.findViewById(R.id.news_investment_org_name);
                viewHolder.img = (ImageView) convertView.findViewById(R.id.news_investment_org_img);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }



            viewHolder.title.setText(maps.get(position).get("orgName"));
            ImageLoader.getInstance().displayImage(MyApplication.ImgBASEURL + maps.get(position).get("photo"), viewHolder.img);

            return convertView;
        }

    }

    private static class ViewHolder {
        TextView title;
        ImageView img;
    }
    @Override
    protected void onStop() {
        super.onStop();
        listView.stopFooterAnimition();
    }
}

