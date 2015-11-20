package com.juttec.goldmetal.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.activity.AccountActivity;
import com.juttec.goldmetal.activity.AnnouncementActivity;
import com.juttec.goldmetal.activity.ChartActivity;
import com.juttec.goldmetal.activity.CreateAccount.AccountNoticeActivity;
import com.juttec.goldmetal.activity.LoginActivity;
import com.juttec.goldmetal.activity.TodayStrategyActivity;
import com.juttec.goldmetal.adapter.MarketRecyclerAdapter;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.MarketFormInfo;
import com.juttec.goldmetal.bean.MyEntity;
import com.juttec.goldmetal.bean.OptionalStockBean;
import com.juttec.goldmetal.dialog.MyAlertDialog;
import com.juttec.goldmetal.utils.GetNetworkData;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class MarketFragment extends BaseFragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";

    //自选接口路径   symbol=OSXAU,OSXAG,OZAG20,OZAG50,OZAG100,OYXAG50KG,OYXAG150KG,NECLI0,OSUDI&u=qq3585&p=qq3771
    private String OPTIONAL_URL = "http://db2015.wstock.cn/wsDB_API/stock.php?r_type=2&symbol=";

    //返回某一市场全字段行情  默认返回50行
    //http://db2015.wstock.cn/wsDB_API/stock.php?market=SH&q_type=1

    //上证A股 接口 路径
    private String SHA_URL = "http://db2015.wstock.cn/wsDB_API/stock.php?market=SH6&r_type=2&num=200";
    //上证B股 接口 路径
    private String SHB_URL = "http://db2015.wstock.cn/wsDB_API/stock.php?market=SH9&r_type=2&num=200";

    //深证A股 接口 路径
    private String SZA_URL = "http://db2015.wstock.cn/wsDB_API/stock.php?market=SZ0&r_type=2&num=200";
    //深证B股 接口 路径
    private String SZB_URL = "http://db2015.wstock.cn/wsDB_API/stock.php?market=SZ2&r_type=2&num=200";



    //返回指定代码的指定字段的行情  默认（最多）返回50行
    //http://db2015.wstock.cn/wsDB_API/stock.php?symbol=SH000001,SH000002&query=Date,Symbol,NewPrice,Volume,Amount&q_type=2
   // http://db2015.wstock.cn/wsDB_API/stock.php?symbol=SZ000009&r_type=2
    private String SEARCH_URL = "http://db2015.wstock.cn/wsDB_API/stock.php?r_type=2";



    private String mParam1;

    private Realm myRealm;//轻量级数据库
    private List<String> mLists;

    private ImageView iv_search;//搜索
    private RecyclerView recyclerView;

    private TabLayout tabLayout;
    private List<MarketFormInfo.ResultEntity> datas;
    private Button btCreateAccount;//开户按钮
    private TextView strategy;
    private MyEntity myEntity;

    private static final int NEWEST = 0;  //加载最新数据的标识
    private static final int SEARCH_DATA = 1;  //加载搜索到的 个股数据的标识


    private MarketFormInfo marketFormInfo;
    private MarketRecyclerAdapter adapter;
    MyHandler myHandler;

    private MyApplication app;

    private MyAlertDialog myAlertDialog;//加入自选股 或从自选股移除的  对话框

    private boolean isOptional = true;//当前显示的数据是否是自选股  默认为：true

    public static MarketFragment newInstance(String param1) {
        MarketFragment fragment = new MarketFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public MarketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
//        myEntity = new MyEntity();
        marketFormInfo = new MarketFormInfo();
        myEntity = new MyEntity(marketFormInfo);

        myRealm =  Realm.getInstance(new RealmConfiguration.Builder(getActivity())
                                .name("optionalstock.realm")
                                .build()
                );

        mLists = new ArrayList<String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        app = (MyApplication) getActivity().getApplication();
        myAlertDialog = new MyAlertDialog(getActivity());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_market, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.market_tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("自选").setTag(1),true);
        tabLayout.addTab(tabLayout.newTab().setText("上证A股").setTag(2));
        tabLayout.addTab(tabLayout.newTab().setText("上证B股").setTag(3));
        tabLayout.addTab(tabLayout.newTab().setText("深证A股").setTag(4));
        tabLayout.addTab(tabLayout.newTab().setText("深证B股").setTag(5));
        tabLayout.addTab(tabLayout.newTab().setText("搜索个股").setTag(6));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch ((int) tab.getTag()) {
                    case 1:
                        //获取自选股数据
                        getOptionalData();
                        break;
                    case 2:
                        //上证A股
                        isOptional = false;
                        getData(SHA_URL);

                        break;
                    case 3:
                        //上证B股
                        isOptional = false;
                        getData(SHB_URL);
                        break;
                    case 4:
                        //深证A股
                        isOptional = false;
                        getData(SZA_URL);
                        break;
                    case 5:
                        //深证B股
                        isOptional = false;
                        getData(SZB_URL);
                        break;
                    case 6:
                        //搜索个股模块
                        datas.clear();
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }

                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        init(view);

        //获取自选股数据
        getOptionalData();

        return view;
    }


    /**
     * 获取自选股  数据
     */
    private void getOptionalData(){
        //查数据库  将股票代码放入list中
        queryData();
        //判断是否有自选股
        if(mLists.size()==0){
            ToastUtil.showShort(getActivity(),"您还没有自选股，快去添加吧");
            return;
        }
        //将股票代码 拼接成指定url
        String url = "";
        //自选
        for(int i=0;i<mLists.size();i++){
            if(i!=mLists.size()-1){
                url = url+mLists.get(i)+",";
            }else{
                url = url+mLists.get(i);
            }

        }
        //将 标志设置为：自选股
        isOptional = true;
        //调接口获取自选股数据
        getData(OPTIONAL_URL+url);
    }




    //初始化view
    private void init(View view) {
        iv_search = (ImageView) view.findViewById(R.id.market_search);

        ImageView rightImg = (ImageView) view.findViewById(R.id.right_img);
        ImageView leftImg = (ImageView) view.findViewById(R.id.left_img);

        strategy = (TextView) view.findViewById(R.id.fragment_market_strategy);
        strategy.setOnClickListener(this);

        rightImg.setOnClickListener(this);
        leftImg.setOnClickListener(this);

        iv_search.setOnClickListener(this);
        btCreateAccount = (Button) view.findViewById(R.id.fragment_market_bt_createaccount);
        btCreateAccount.setOnClickListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_market_recyclerview);

        datas = new ArrayList<>();
        myHandler = new MyHandler();
       // getData(OPTIONAL_URL);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SampleDivider(getActivity(), R.drawable.divider_shape));
    }


    @Override
    public void onResume() {
        super.onResume();
        if(myRealm==null){
            myRealm = Realm.getInstance(getActivity());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(myRealm!=null){
            myRealm.close();
        }
    }

    @Override
    public void onClick(View v) {
        final Intent intent;
        switch (v.getId()){
            case  R.id.market_search:
                //搜索个股
                final MyAlertDialog dialog = new MyAlertDialog(getActivity());

                dialog.builder()
                        .setTitle("搜索个股").setEditText("请输入个股代码 例如SH600016")
                        .setSingleButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.setEditType(EditorInfo.TYPE_CLASS_TEXT);
                                String et = dialog.getResult().trim();
                                if (et.length() == 8) {
                                    //开始搜素
                                    GetNetworkData.getKLineData(SEARCH_URL + "&symbol=" + et, myEntity, getActivity(), myHandler, NEWEST);
                                    tabLayout.getTabAt(5).select();

                                    dialog.dismiss();
                                } else {
                                    ToastUtil.showShort(getActivity(), "请输入完整正确的个股代码！");
                                }
                            }
                        }).show();

//                intent = new Intent(getActivity(), SearchStockActivity.class);
//                startActivity(intent);
                break;

            case R.id.right_img:
                //个人账户
                if(app.getUserInfoBean()!=null){
                    startActivity(new Intent(getActivity(), AccountActivity.class));
                }else{
                    //如果没有登录
                    final MyAlertDialog mdialog = new MyAlertDialog(getActivity());
                    mdialog.builder().setTitle("提示")
                            .setMsg("您还没有登录，请先登录后再执行操作！")
                            .setSingleButton("前去登录", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                    mdialog.dismiss();
                                }
                            }).show();

                }

                break;

            case R.id.left_img:
                //公告
                startActivity(new Intent(getActivity(), AnnouncementActivity.class));
                break;


            case R.id.fragment_market_bt_createaccount:
                //开户界面
                startActivity(new Intent(getActivity(), AccountNoticeActivity.class));
                break;
            case R.id.fragment_market_strategy:
                //今日策略
                intent = new Intent(getActivity(), TodayStrategyActivity.class);

                startActivity(intent);
                break;

        }


    }


    //RecycleView的 item间的分割线
    class SampleDivider extends RecyclerView.ItemDecoration {
        private Drawable mDrawable;

        public SampleDivider(Context context, int resId) {
            //在这里我们传入作为Divider的Drawable对象
            mDrawable = context.getResources().getDrawable(resId);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                //以下计算主要用来确定绘制的位置
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDrawable.getIntrinsicHeight();
                mDrawable.setBounds(left, top, right, bottom);
                mDrawable.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, int position, RecyclerView parent) {
            outRect.set(0, 0, 0, mDrawable.getIntrinsicWidth());
        }
    }


    private void getData(String url) {
        LogUtil.d("股票URL-------------"+url);
        GetNetworkData.getKLineData(url, myEntity, getActivity(), myHandler, NEWEST);
    }

    /**
     * 自定义Handler
     */
    class MyHandler extends Handler {

        public MyHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            marketFormInfo = (MarketFormInfo) myEntity.getObject();
            switch (msg.what) {
                case NEWEST:
                    datas = marketFormInfo.getResult();
                    if (datas.size() == 0)
                    {
                        ToastUtil.showShort(getActivity(), "没有数据...");
                        //pd.dismiss();
                        break;
                    } else {
                        ToastUtil.showShort(getActivity(), "填充数据...");
                        adapter = new MarketRecyclerAdapter(datas, getActivity());

                        //item的短按事件   进入分时图
                        adapter.setOnItemClickListener(new MarketRecyclerAdapter.OnItemClickListener() {
                            @Override
                            public void itemclickListener(View view, int position) {
                                Intent intent = new Intent(getActivity(), ChartActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("name", datas.get(position).getName());
                                bundle.putString("symbol", datas.get(position).getSymbol());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });

                        //item的长按事件   加入或移除  自选股
                        adapter.setOnItemLongClickListener(new MarketRecyclerAdapter.OnItemLongClickListener() {
                            @Override
                            public void itemLongClickListener(View view, final int position) {
                                if(!isOptional&&mLists.contains(datas.get(position).getSymbol())){
                                    ToastUtil.showShort(getActivity(),datas.get(position).getName()+"已加入到自选股");
                                    return ;
                                }

                                myAlertDialog.builder().setTitle(isOptional?"移除自选股":"添加自选股")
                                        .setMsg(isOptional?"你要将 "+datas.get(position).getName()+" 从自选股中移除吗？":"你要将 "+datas.get(position).getName()+" 添加到自选股吗？")
                                        .setPositiveButton("确定", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(isOptional){
                                                    deleteData(datas.get(position).getSymbol());
                                                    datas.remove(position);
                                                    adapter.notifyDataSetChanged();
                                                }else{
                                                    saveData(datas.get(position).getSymbol());
                                                }
                                                ToastUtil.showShort(getActivity(),isOptional?"移除成功":"添加成功");
                                            }
                                        }).setNegativeButton("取消", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        myAlertDialog.dismiss();
                                    }
                                }).show();

                            }
                        });

                        recyclerView.setAdapter(adapter);
                        break;
                    }
            }
        }
    }


    /**
     * 往数据库中保存数据
     */
    private void saveData(String symbol_save){
        myRealm.beginTransaction();
        // Create an object
        OptionalStockBean bean = myRealm.createObject(OptionalStockBean.class);

        // Set its fields
        bean.setSymbol(symbol_save);
        myRealm.commitTransaction();

        //同时将数据加入到list中
        mLists.add(symbol_save);
    }



    /**
     * 从数据库中查询数据
     */
    private void queryData(){
        //先将list中的数据清空
        mLists.clear();
        RealmResults<OptionalStockBean> results =
                myRealm.where(OptionalStockBean.class).findAll();

        for(OptionalStockBean c:results) {
            mLists.add(c.getSymbol());
        }

        LogUtil.d("从数据库检索到的数据："+mLists.toString());
    }


    /**
     * 从数据库中删除数据
     */
    private void deleteData(String symbol_del){

        myRealm.beginTransaction();

        RealmResults<OptionalStockBean> results =
                myRealm.where(OptionalStockBean.class).findAll();

        for(int i=0;i<results.size();i++){
            if(results.get(i).getSymbol().equals(symbol_del)){
                results.remove(i);
                //同时将list中的数据删除
                mLists.remove(i);
            }
        }
        myRealm.commitTransaction();
    }




}
