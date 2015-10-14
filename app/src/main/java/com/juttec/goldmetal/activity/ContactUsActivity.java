package com.juttec.goldmetal.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.dialog.MyProgressDialog;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.NetWorkUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
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

public class ContactUsActivity extends AppCompatActivity {

    MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MyApplication) getApplication();
        setContentView(R.layout.activity_contact_us);
        final List<Map<String, String>> maps = new ArrayList<>();
        final RecyclerView recyclerView = (RecyclerView) this.findViewById(R.id.contact_us_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.builder().show();
        new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getContactUsUrl(), new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                try {
                    JSONObject outObject = new JSONObject(responseInfo.result.toString());
                    String status = outObject.getString("status");
                    if ("1".equals(status)) {

                        progressDialog.dismiss();

                        JSONObject object1 = outObject.getJSONObject("entityList");
                        JSONArray jsonArray = object1.getJSONArray("phone");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            Map<String, String> map = new HashMap<String, String>();

                            map.put("img", "phone");

                            map.put("text", jsonArray.getJSONObject(i).getString("contactWay"));
                            maps.add(map);
                        }
                        JSONArray jsonArray1 = object1.getJSONArray("qq");
                        for (int i = 0; i < jsonArray1.length(); i++) {
                            Map<String, String> map = new HashMap<String, String>();

                            map.put("img", "qq");


                            map.put("text", jsonArray1.getJSONObject(i).getString("contactWay"));
                            maps.add(map);
                        }
                        recyclerView.setAdapter(new MyAdapter(maps));
                    } else {
                        LogUtil.e(outObject.getString("promptInfor"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(HttpException error, String msg) {
                NetWorkUtils.showMsg(ContactUsActivity.this);

            }
        });


    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        List<Map<String, String>> maps;

        public MyAdapter(List<Map<String, String>> maps) {
            this.maps = maps;
            for (Map<String, String> map : maps
                    ) {
                LogUtil.e("dfd" + map.get("text"));
            }
        }

        @Override

        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(ContactUsActivity.this).inflate(R.layout.phone_qq_adapter_item, parent, false);
            // 创建一个ViewHolder
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {

            holder.textView.setText(maps.get(position).get("text"));
            if ("phone".equals(maps.get(position).get("img"))) {

                holder.imageView.setImageDrawable(getDrawable(R.mipmap.content_account_icon_phone));
            } else {
                holder.imageView.setImageDrawable(getDrawable(R.mipmap.content_account_icon_qq));
            }

        }

        @Override
        public int getItemCount() {
            return maps.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.contact_us_adapter_img);
                textView = (TextView) itemView.findViewById(R.id.contact_us_adapter_tv);

            }
        }
    }

}
