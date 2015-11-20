
package com.juttec.goldmetal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.bean.StockSearchBean;
import com.juttec.goldmetal.dialog.MyAlertDialog;
import com.juttec.goldmetal.dialog.MyProgressDialog;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.NetWorkUtils;
import com.juttec.goldmetal.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索股票的  Activity
 */

public class SearchStockActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private EditText et_search;//搜索框
    private Button btn_search;//搜索按钮

    private ListView mListView;//显示搜索结果的listview
    private SearchAdapter mAdapter;//适配器

    private String symbol;//要搜索的股票代码

    private String BaseURL = "http://db2015.wstock.cn/wsDB_API/pinyin.php?num=100&page=1&r_type=2&u=qq3585&p=qq3771";

    private MyProgressDialog dialog_progress;//正在加载 进度框
    private MyAlertDialog myAlertDialog;//是否加入自选股 的对话框

    private List<StockSearchBean> mList = new ArrayList<StockSearchBean>();//放搜索到股票数据的集合

    private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_stock);

        dialog_progress = new MyProgressDialog(this);
        myAlertDialog = new MyAlertDialog(this);


        //初始化控件
        initView();

        symbol = getIntent().getStringExtra("symbol");
        et_search.setText(symbol);
    }


    //初始化控件
    private void initView() {
        et_search = (EditText) findViewById(R.id.et_search);
        btn_search = (Button) findViewById(R.id.btn_search);

        mListView = (ListView) findViewById(R.id.listview);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        //edittext监听内容变化   更改按钮的背景
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = et_search.getText().toString();
                if (TextUtils.isEmpty(content) || "".equals(content) || content.trim().length() <= 0) {
                    btn_search.setSelected(false);
                } else {
                    btn_search.setSelected(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        //搜索股票按钮的 点击事件
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                symbol = et_search.getText().toString();
                if (TextUtils.isEmpty(symbol) || "".equals(symbol) || symbol.trim().length() <= 0) {
                    ToastUtil.showShort(SearchStockActivity.this, "请输入个股代码或简拼！");
                } else {
                    searchStock(symbol);
                }
            }
        });
    }


    //调接口 搜索指定的股票
    private void searchStock(String content) {
        dialog_progress.builder().setMessage("正在搜索~").show();
        HttpUtils httpUtils = new HttpUtils();
        LogUtil.d("搜索股票URL：" + BaseURL + "&mixed=" + symbol);
        httpUtils.send(HttpRequest.HttpMethod.GET, BaseURL + "&mixed=" + symbol, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog_progress.dismiss();

                mList.clear();//先清除上一次搜索到的数据
                try {
                    LogUtil.d("搜索股票 返回结果：" + responseInfo.result.toString());

                    JSONArray array = new JSONArray(responseInfo.result.toString());

                    for (int i = 0; i < array.length(); i++) {
                        //{"pinyin":"meiyuanyoulianxu","letter":"myylx","symbol":"NECLA0","name":"美原油连续"}
                        StockSearchBean bean = new StockSearchBean();
                        JSONObject object = array.getJSONObject(i);
                        bean.setPinyin(object.getString("pinyin"));
                        bean.setLetter(object.getString("letter"));
                        bean.setSymbol(object.getString("symbol"));
                        bean.setName(object.getString("name"));
                        mList.add(bean);

                        mAdapter = new SearchAdapter();
                        mListView.setAdapter(mAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.showShort(SearchStockActivity.this, "没有搜索到结果");
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog_progress.dismiss();
                NetWorkUtils.showMsg(SearchStockActivity.this);
            }
        });

    }


    //item的短按事件   进入到分时图
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(SearchStockActivity.this, ChartActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", mList.get(position).getName());
        bundle.putString("symbol", mList.get(position).getSymbol());
        intent.putExtras(bundle);
        startActivity(intent);

        finish();//销毁这个界面
    }


    //item的长按事件  加入到自选股
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        myAlertDialog.builder().setTitle("添加自选股")
                .setMsg("你要将 " + mList.get(position).getName() + " 添加到自选股吗？")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ToastUtil.showShort(SearchStockActivity.this, "添加成功");
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAlertDialog.dismiss();
            }
        }).show();

        return true;
    }


    //自定义数据适配器
    class SearchAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.from(SearchStockActivity.this).inflate(R.layout.item_search_stock, null);
                holder.tv_symbol = (TextView) convertView.findViewById(R.id.tv_symbol);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_letter = (TextView) convertView.findViewById(R.id.tv_letter);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv_symbol.setText(mList.get(position).getSymbol());
            holder.tv_name.setText(mList.get(position).getName());
            holder.tv_letter.setText(mList.get(position).getLetter());

            return convertView;
        }


        class ViewHolder {
            TextView tv_symbol;//代码
            TextView tv_name;//名称
            TextView tv_letter;//名称拼音首字母
        }
    }

}
