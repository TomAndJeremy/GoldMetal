package com.juttec.goldmetal.activity.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.customview.HeadLayout;
import com.juttec.goldmetal.fragment.NewsFragment;
import com.juttec.goldmetal.utils.LogUtil;
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
 * 机构评论详情   深度解析详情  投资结构详情  财经头条详情  界面
 */
public class NewsDetailActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    MyApplication app;
//    TextView content;
    WebView mWebView;
    TextView tvTitle, tvTime;
    String url;

    String headTitle;

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

        headTitle = intent.getStringExtra("type");


        if (headTitle.equals(NewsFragment.REVIEW)) {
            headLayout.setHeadTitle(headTitle+"详情");
            url = app.getGetOrgReviewDetailsUrl();
        } else if (headTitle.equals(NewsFragment.ANALYSIS)) {
            headLayout.setHeadTitle(headTitle+"详情");
            url = app.getGetDepthAnalysisDetailsUrl();
        } else if (headTitle.equals(NewsFragment.INSTITUTION)) {
            headLayout.setHeadTitle(headTitle+"详情");
            url = app.getGetInvestmentOrgDetailsUrl();
        }else if (headTitle.equals(NewsFragment.HEADTLINES)) {
            headLayout.setHeadTitle("");
            url = app.getGetFinanceInforDetailsUrl();
        }


        tvTitle = (TextView) this.findViewById(R.id.new_detail_title);
        tvTime = (TextView) this.findViewById(R.id.new_detail_time);
//        content = (TextView) this.findViewById(R.id.new_detail_content);
        mWebView = (WebView) findViewById(R.id.webview);

        tvTime.setText(time);
        tvTitle.setText(title);

        swipeRefreshLayout = new SwipeRefreshLayout(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                RequestParams requestParams = new RequestParams();
                requestParams.addBodyParameter("id", id);
                new HttpUtils().send(HttpRequest.HttpMethod.POST, url, requestParams, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            LogUtil.d(headTitle+"详情为："+responseInfo.result.toString());
                            JSONObject object = new JSONObject(responseInfo.result.toString());
                            if ("1".equals(object.getString("status"))) {

                                if (url.equals(app.getGetInvestmentOrgDetailsUrl())) {
                                    LogUtil.d("投资机构详情"+id);
                                    JSONObject object1 = object.getJSONObject("entityList");
                                    tvTitle.setText(object1.getString("orgName"));
                                    tvTime.setText(object1.getString("addTime"));
//                                    content.setText(object1.getString("details"));
                                    String str = object1.getString("details").trim();
                                    str = str.replaceAll("\\n","</p><p>");
                                    String ht =  "<html><head></head><body><p>"+str+"</p></body></html>";
                                    LogUtil.d("html:"+ht);
                                    mWebView.loadDataWithBaseURL("", ht, "text/html", "utf-8", "");


                                } else {
//                                    content.setText(object.getString("message1"));
                                    String str = object.getString("message1").trim();
                                    str = str.replaceAll("\\n","</p><p>");
                                    String ht =  "<html><head></head><body><p>"+str+"</p></body></html>";
                                    LogUtil.d("html:"+ht);
                                    mWebView.loadDataWithBaseURL("", ht, "text/html", "utf-8", "");
                                }

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
