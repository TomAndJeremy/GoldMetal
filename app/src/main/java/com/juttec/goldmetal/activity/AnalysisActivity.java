package com.juttec.goldmetal.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.customview.listview.AutoLoadListView;
import com.juttec.goldmetal.customview.listview.LoadingFooter;

public class AnalysisActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{


    // 加载更多
    public static final int MSG_LOAD_MORE = 0;
    // 刷新
    public static final int MSG_REFRESH = 1;



    private SwipeRefreshLayout swipeLayout;
    private AutoLoadListView listView;

    private  MyAdapter adapter;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_LOAD_MORE:

                    if (adapter.count < 60) {
                        adapter.count += 3;
                        adapter.notifyDataSetChanged();
                        listView.setState(LoadingFooter.State.Idle);
                    } else {
                        listView.setState(LoadingFooter.State.TheEnd);
                    }

                    break;
                case MSG_REFRESH:
                    swipeLayout.setRefreshing(false);
                    adapter.count = 3;
                    listView.smoothScrollToPosition(0);

                    adapter.notifyDataSetChanged();
                    listView.setState(LoadingFooter.State.Idle);
                    break;
                default:
                    break;
            }
        };
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);



        swipeLayout = (SwipeRefreshLayout) this
                .findViewById(R.id.swipe_refresh);
        // 顶部刷新的样式
        swipeLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);


        swipeLayout.setOnRefreshListener(this);

        listView = (AutoLoadListView) this.findViewById(R.id.listview);

        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.setOnLoadNextListener(new AutoLoadListView.OnLoadNextListener() {

            @Override
            public void onLoadNext() {
                handler.sendEmptyMessageDelayed(MSG_LOAD_MORE, 3000);
            }
        });
    }






    /**
     * 普通的适配器
     */
    private class MyAdapter extends BaseAdapter {

        public int count = 3;

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item,
                        parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tv = (TextView) convertView.findViewById(R.id.tv);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tv.setText("I'm" + position);
            return convertView;
        }

    }

    private static class ViewHolder {
        TextView tv;
    }





    @Override
    public void onRefresh() {

        handler.sendEmptyMessageDelayed(MSG_REFRESH, 3000);
    }
}
