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
import com.juttec.goldmetal.customview.listview.AutoLoadListView;
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
 * 深度解析  界面
 */

public class AnalysisActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{





    private SwipeRefreshLayout swipeLayout;
    private AutoLoadListView listView;

    MyApplication app;
    int pageIndex = 1;
    List<Map<String, String>> maps;
    MyAdapter myAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        app = (MyApplication) getApplication();


        swipeLayout = (SwipeRefreshLayout) this
                .findViewById(R.id.swipe_refresh);
        // 顶部刷新的样式
        swipeLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);


        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                pageIndex = 1;
                getData(pageIndex);

            }
        });
        swipeLayout.setOnRefreshListener(this);



        maps = new ArrayList<>();

        listView = (AutoLoadListView) this.findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int i = position - 1;
                Intent intent = new Intent(getApplicationContext(), NewsDetailActivity.class);
                intent.putExtra("title", maps.get(i).get("title"));
                intent.putExtra("time", maps.get(i).get("time"));
                intent.putExtra("id", maps.get(i).get("id"));
                intent.putExtra("type", "analysis");
                startActivity(intent);
            }
        });
        listView.setOnLoadNextListener(new AutoLoadListView.OnLoadNextListener() {

            @Override
            public void onLoadNext() {
                swipeLayout.setRefreshing(true);

                getData(pageIndex);
            }
        });
    }



    public void getData(int i) {

        RequestParams requestParams = new RequestParams();
        requestParams.addBodyParameter("pageIndex", i + "");
        new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getGetDepthAnalysisUrl(), requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                swipeLayout.setRefreshing(false);

                LogUtil.e(responseInfo.result.toString());

                Map<String, String> map;
                try {
                    JSONObject object = new JSONObject(responseInfo.result.toString());

                    int pageNum = Integer.parseInt(object.getString("message1"));
                    if ("1".equals(object.getString("status"))) {

                        JSONArray jsonArray = object.getJSONArray("entityList");


                        if (pageIndex == 1) {
                            maps.clear();
                        }


                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object1 = jsonArray.getJSONObject(i);
                            map = new HashMap<>();
                            map.put("id", object1.getString("id"));
                            map.put("title", object1.getString("title"));
                            map.put("titlePhoto", object1.getString("titlePhoto"));
                            map.put("briefDetails", object1.getString("briefDetails"));
                            map.put("time", object1.getString("addTime"));
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
                convertView = getLayoutInflater().inflate(R.layout.news_analysis_item,
                        parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title=(TextView) convertView.findViewById(R.id.news_analysis_item_title);
                viewHolder.summary = (TextView) convertView.findViewById(R.id.news_analysis_item_summary);
                viewHolder.time = (TextView) convertView.findViewById(R.id.news_analysis_item_time);
                viewHolder.img = (ImageView) convertView.findViewById(R.id.news_analysis_item_img);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.title.setText(maps.get(position).get("title"));
            viewHolder.summary.setText(maps.get(position).get("briefDetails"));
            viewHolder.time.setText(maps.get(position).get("time"));
            ImageLoader.getInstance().displayImage(MyApplication.ImgBASEURL + maps.get(position).get("titlePhoto"), viewHolder.img);

            return convertView;
        }

    }

    private static class ViewHolder {
        TextView title;
        TextView time;
        TextView summary;
        ImageView img;
    }





    @Override
    public void onRefresh() {

        pageIndex=1;
        getData(pageIndex);

    }



    @Override
    protected void onStop() {
        super.onStop();
        listView.stopFooterAnimition();
    }
}
