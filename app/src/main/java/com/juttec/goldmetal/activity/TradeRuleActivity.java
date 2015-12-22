package com.juttec.goldmetal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;
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

public class TradeRuleActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    MyApplication app;
    TextView  tvTitle;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_rule);

        app = (MyApplication) getApplication();
        Intent intent = getIntent();
        final String id = intent.getStringExtra("id");
        String tradeOrg = intent.getStringExtra("tradeOrg");
        tvTitle = (TextView) this.findViewById(R.id.trade_rule_title);
//        content = (TextView) this.findViewById(R.id.trade_rule_summary);
        mWebView = (WebView) findViewById(R.id.webview);
        tvTitle.setText(tradeOrg);
        swipeRefreshLayout = new SwipeRefreshLayout(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                RequestParams requestParams = new RequestParams();
                requestParams.addBodyParameter("id", id);
                new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getGetTradeRuleUrl(), requestParams, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        swipeRefreshLayout.setRefreshing(false);
                        try {

                            JSONObject object = new JSONObject(responseInfo.result.toString());
                            if ("1".equals(object.getString("status"))) {
//                                content.setText(object.getString("message1"));
                                String str = object.getString("message1").trim();
                                str = str.replaceAll("\\n","</p><p>");
                                String ht =  "<html><head></head><body><p>"+str+"</p></body></html>";
                                LogUtil.d("html:"+ht);
                                mWebView.loadDataWithBaseURL("", ht, "text/html", "utf-8", "");
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
