package com.juttec.goldmetal.activity.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.customview.HeadLayout;
import com.juttec.goldmetal.utils.NetWorkUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jeremy on 2015/10/16.
 */
public class NewsDetail extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    MyApplication app;
    TextView content;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        app = (MyApplication) getApplication();

        HeadLayout headLayout = (HeadLayout) this.findViewById(R.id.head_layout);


        Intent intent = getIntent();
        final String id = intent.getStringExtra("id");
        String title = intent.getStringExtra("title");
        String time = intent.getStringExtra("time");

        String type = intent.getStringExtra("type");


        if ("review".equals(type)) {
            headLayout.setHeadTitle(getResources().getString(R.string.detail_news_review));
            url = app.getGetOrgReviewDetailsUrl();
        }else if("analysis".equals(type)){
            headLayout.setHeadTitle(getResources().getString(R.string.detail_news_analysis));
            url = app.getGetDepthAnalysisDetailsUrl();
        }else if ("institution".equals(type)) {
            headLayout.setHeadTitle(getResources().getString(R.string.detail_news_institution));
            url = app.getGetInvestmentOrgDetailsUrl();
        }


        TextView tvTitle = (TextView) this.findViewById(R.id.new_detail_title);
        TextView tvTime = (TextView) this.findViewById(R.id.new_detail_time);
        content = (TextView) this.findViewById(R.id.new_detail_content);

        tvTime.setText(time);
        tvTitle.setText(title);

        swipeRefreshLayout = new SwipeRefreshLayout(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                RequestParams requestParams = new RequestParams();
                requestParams.addBodyParameter("id", id);
                new HttpUtils().send(HttpRequest.HttpMethod.POST,url, requestParams, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        swipeRefreshLayout.setRefreshing(false);
                        try {

                            JSONObject object = new JSONObject(responseInfo.result.toString());
                            if ("1".equals(object.getString("status"))) {
                                content.setText(object.getString("message1"));
                            }
                        } catch (JSONException e) {


                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        swipeRefreshLayout.setRefreshing(false);
                        NetWorkUtils.showMsg(getApplicationContext());

                    }
                });
            }
        });

    }
}
