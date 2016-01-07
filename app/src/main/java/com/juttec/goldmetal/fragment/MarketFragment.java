package com.juttec.goldmetal.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.activity.AccountActivity;
import com.juttec.goldmetal.activity.AnnouncementActivity;
import com.juttec.goldmetal.activity.ChartActivity;
import com.juttec.goldmetal.activity.CreateAccount.AccountNoticeActivity;
import com.juttec.goldmetal.activity.LoginActivity;
import com.juttec.goldmetal.activity.TodayStrategyActivity;
import com.juttec.goldmetal.adapter.MarketListAdapter;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.MarketFormInfo;
import com.juttec.goldmetal.bean.MyEntity;
import com.juttec.goldmetal.bean.OptionalStockBean;
import com.juttec.goldmetal.customview.PullToRefreshLayout;
import com.juttec.goldmetal.dialog.MyAlertDialog;
import com.juttec.goldmetal.utils.GetNetworkData;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class MarketFragment extends BaseFragment implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener {

    GetNetworkData getNetWorkData;
    private static final String ARG_PARAM1 = "param1";

    //自选接口路径   symbol=OSXAU,OSXAG,OZAG20,OZAG50,OZAG100,OYXAG50KG,OYXAG150KG,NECLI0,OSUDI&u=qq3585&p=qq3771
    private String OPTIONAL_URL = "http://db2015.wstock.cn/wsDB_API/stock.php?r_type=2&symbol=";


    //上证A股 接口 路径
    private String SHA_URL = "http://db2015.wstock.cn/wsDB_API/stock.php?market=SH6&r_type=2&num=20&page=";
    //上证B股 接口 路径
    private String SHB_URL = "http://db2015.wstock.cn/wsDB_API/stock.php?market=SH9&r_type=2&num=20&page=";

    //深证A股 接口 路径
    private String SZA_URL = "http://db2015.wstock.cn/wsDB_API/stock.php?market=SZ0&r_type=2&num=20&page=";
    //深证B股 接口 路径
    private String SZB_URL = "http://db2015.wstock.cn/wsDB_API/stock.php?market=SZ2&r_type=2&num=20&page=";


    //返回指定代码的指定字段的行情  默认（最多）返回50行
    //http://db2015.wstock.cn/wsDB_API/stock.php?symbol=SH000001,SH000002&query=Date,Symbol,NewPrice,Volume,Amount&q_type=2
    // http://db2015.wstock.cn/wsDB_API/stock.php?symbol=SZ000009&r_type=2
    private String SEARCH_URL = "http://db2015.wstock.cn/wsDB_API/stock.php?r_type=2";


    private String mParam1;

    private Realm myRealm;//轻量级数据库
    private List<String> mLists;

    private ImageView iv_search;//搜索
    private TabLayout tabLayout;
    private List<MarketFormInfo.ResultEntity> datas;
    private Button btCreateAccount;//开户按钮
    private TextView strategy;
    private MyEntity myEntity;

    private static final int NEWEST = 0;  //加载最新数据的标识
    private static final int SEARCH_DATA = 1;  //加载搜索到的 个股数据的标识


    private MarketFormInfo marketFormInfo;
    private MarketListAdapter adapter;
    MyHandler myHandler;

    private MyApplication app;

    private MyAlertDialog myAlertDialog;//加入自选股 或从自选股移除的  对话框

    private boolean isFirst = true;//第一次进入时  在onResume()方法中 加载自选股数据
    private boolean isOptional = true;//当前显示的数据是否是自选股  默认为：true
    private boolean isSearch = false;//是否点击了搜索个股tab 默认为：false
    private String resetURL;//保存最后的url


    private int page = 1;//默认加载第一页的数据
    private int lastPage = 1;//与page比较判断当前加载的是否是同一页


    private ListView listView;
    private PullToRefreshLayout ptrl;
    private boolean isFirstIn = true;


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

        marketFormInfo = new MarketFormInfo();
        myEntity = new MyEntity(marketFormInfo);

        myRealm = Realm.getInstance(new RealmConfiguration.Builder(getActivity())
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
        getNetWorkData = new GetNetworkData();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_market, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.market_tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("自选").setTag(1), true);
        tabLayout.addTab(tabLayout.newTab().setText("上证A股").setTag(2));
        tabLayout.addTab(tabLayout.newTab().setText("上证B股").setTag(3));
        tabLayout.addTab(tabLayout.newTab().setText("深证A股").setTag(4));
        tabLayout.addTab(tabLayout.newTab().setText("深证B股").setTag(5));
        tabLayout.addTab(tabLayout.newTab().setText("搜索个股").setTag(6));


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //当点击tab时  将加载的页数设置为  加载第一页
                page = 1;
                switch ((int) tab.getTag()) {
                    case 1:
                        //获取自选股数据
                        getOptionalData();
                        break;
                    case 2:
                        //上证A股
                        isOptional = false;
                        isSearch = false;
                        getData(SHA_URL + page);
                        break;
                    case 3:
                        //上证B股
                        isOptional = false;
                        isSearch = false;
                        getData(SHB_URL + page);
                        break;
                    case 4:
                        //深证A股
                        isOptional = false;
                        isSearch = false;
                        getData(SZA_URL + page);
                        break;
                    case 5:
                        //深证B股
                        isOptional = false;
                        isSearch = false;
                        getData(SZB_URL + page);
                        break;
                    case 6:
                        //搜索个股模块
                        datas.clear();
                        //将resetURL置空
                        resetURL = "";
                        //停止网络访问
                        getNetWorkData.stop();
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        //搜索个股
                        showSearchStock();
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


        return view;
    }


    /**
     * 获取自选股  数据
     */
    private void getOptionalData() {
        //将 标志设置为：自选股
        isOptional = true;
        isSearch = false;
        //查数据库  将股票代码放入list中
        queryData();
        //判断是否有自选股
        if (mLists.size() == 0) {
            ToastUtil.showShort(getActivity(), "您还没有自选股，快去添加吧");
            //将股票数据清空
            datas.clear();
            //将resetURL置空
            resetURL = "";
            //停止网络访问
            getNetWorkData.stop();

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            return;
        }
        //将股票代码 拼接成指定url
        String url = "";
        //自选
        for (int i = 0; i < mLists.size(); i++) {
            if (i != mLists.size() - 1) {
                url = url + mLists.get(i) + ",";
            } else {
                url = url + mLists.get(i);
            }

        }

        //调接口获取自选股数据
        getData(OPTIONAL_URL + url);
    }


    //初始化view
    private void init(View view) {


        ptrl = (PullToRefreshLayout) view.findViewById(R.id.refresh_view);
        ptrl.setOnRefreshListener(this);

        listView = (ListView) view.findViewById(R.id.fragment_market_ListView);

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


//        //监听 recycleview的触摸事件
//        recyclerView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
//                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
//                int totalItemCount = layoutManager.getItemCount();
//
//                if (firstVisibleItem == 0 || lastVisibleItem >= totalItemCount - 1) {
//                    switch (event.getAction()) {
//
//                        case MotionEvent.ACTION_MOVE:
//                            if (n == 1) {
//                                firstY = event.getY();
//                                LogUtil.d("firstY-----:" + firstY);
//                                n++;
//                            }
//                            break;
//
//                        case MotionEvent.ACTION_UP:
//                            //将n重新设置为1
//                            n = 1;
//                            LogUtil.d("MotionEvent.ACTION_UP-----:" + event.getY());
//                            LogUtil.d("MotionEvent.ACTION_UP======" + page + ":" + firstVisibleItem + ":" + (firstY - event.getY()));
//                            //下拉 加载上一页数据
//                            //当前不是第一页数据 当前显示的第一条数据的position为0  向下滑动的手势距离大于20
//                         /*   if (page != 1 && firstY - event.getY() < -200) {
//                                //下拉  加载上一页数据
//                                if (!isLoadingMore) {
//                                    page--;
//                                    LogUtil.d("下拉  加载上一页数据-----page:"+page);
//                                    //
//                                    isNoMore = false;
//                                    loadData(page);
//                                }
//                            }*/
//                            //上拉 加载下一页数据
//                            //当前显示的最后一条数据的position==totalItemCount的position  向上滑动的手势距离大于30
//
//                            if (firstY - event.getY() > screenHigh / 5) {
//                                //上拉 加载下一页数据
//                                if (!isLoadingMore) {
//                                    if (!isNoMore) {
//                                        progressDialog.show();
//
//                                        page++;
//                                        LogUtil.d("上拉 加载下一页数据-----page:" + page);
//                                        loadData(page);
//                                    }
//                                }
//                            }
//                            break;
//                    }
//                }
//
//                return false;
//            }
//        });


        datas = new ArrayList<>();
        myHandler = new MyHandler();

    }

    /**
     * 加载上一页 或者 下一页 数据
     *
     * @param page 第几页的数据
     */
    private void loadData(int page) {
        switch (tabLayout.getSelectedTabPosition()) {
            case 1:
                //上证A股
                getData(SHA_URL + page);
                break;
            case 2:
                //上证B股
                getData(SHB_URL + page);
                break;
            case 3:
                //深证A股
                getData(SZA_URL + page);
                break;
            case 4:
                //深证B股
                getData(SZB_URL + page);
                break;

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        myRealm = Realm.getInstance(getActivity());

        if (isFirst) {
            //第一次进入  默认加载自选股数据
            //获取自选股数据
            getOptionalData();
            isFirst = false;
        }
        if (resetURL != null) {
            getData(resetURL);//重新获取最后显示的数据
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (myRealm != null) {
            myRealm.close();//关闭数据库
        }
        getNetWorkData.stop();//停止网络访问
    }

    @Override
    public void onClick(View v) {
        final Intent intent;
        switch (v.getId()) {
            case R.id.market_search:
                //搜索个股
                showSearchStock();

//                intent = new Intent(getActivity(), SearchStockActivity.class);
//                startActivity(intent);
                break;

            case R.id.right_img:
                //个人账户
                if (app.getUserInfoBean() != null) {
                    startActivity(new Intent(getActivity(), AccountActivity.class));
                } else {
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
                LogUtil.d("trim:" + "   111    111   ".trim());
                break;


            case R.id.fragment_market_bt_createaccount:
                //开户界面
                if (app.isLogin()) {
                    startActivity(new Intent(getActivity(), AccountNoticeActivity.class));
                } else {
                    //如果没有登录
                    final MyAlertDialog mdialog = new MyAlertDialog(getActivity());
                    mdialog.builder().setTitle("提示")
                            .setMsg("您还没有登录，请先登录后再进行操作！")
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
            case R.id.fragment_market_strategy:
                //今日策略
                intent = new Intent(getActivity(), TodayStrategyActivity.class);
                intent.putExtra(NewsFragment.HEADTITLE, "今日策略");
                startActivity(intent);
                break;
        }
    }


    /**
     * 搜索个股
     */
    private void showSearchStock() {
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
                            getData(SEARCH_URL + "&symbol=" + et);
//                                    getNetWorkData.setUrl(SEARCH_URL + "&symbol=" + et);
                            tabLayout.getTabAt(5).select();
                            isSearch = true;
                            isOptional = false;
                            dialog.dismiss();
                        } else {
                            ToastUtil.showShort(getActivity(), "请输入完整正确的个股代码！");
                        }
                    }
                }).show();
        //设置Edittext值只能输入 大写字母和数字
        dialog.getEt().setKeyListener(new DigitsKeyListener() {
            @Override
            public int getInputType() {
                return InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
            }

            @Override
            protected char[] getAcceptedChars() {
                char[] data = getResources().getString(R.string.only_can_input).toCharArray();
                return data;
            }
        });
        dialog.getEt().setMaxEms(8);
        dialog.getEt().setSingleLine(true);
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {

        if (page != 1) {
            page--;
            loadData(page);
        } else {
            ptrl.refreshFinish(PullToRefreshLayout.SUCCEED, true);

        }
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        loadData(++page);
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


    //根据制定URL  获取股票数据
    private void getData(String url) {
        LogUtil.d("股票URL-------------" + url);
        if (url != null && !TextUtils.isEmpty(url)) {

            resetURL = url;
            if (getNetWorkData.isAlive()) {
                getNetWorkData.setUrl(url);
            } else {
                getNetWorkData.getKLineData(url, myEntity, getActivity(), myHandler, NEWEST);
            }
        }
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

                    if (marketFormInfo.getResult().size() == 0) {

                        page--;
                        loadData(page);
                        LogUtil.d("当前页：-----------" + page);
                        ToastUtil.showShort(getActivity(), "数据已全部加载...");
                        break;
                    } else {
                        datas = marketFormInfo.getResult();

                        //如果当前 选中的是自选股并且 mlist中没有数据   则直接return
                        if (isOptional && mLists.size() == 0) {
                            return;
                        }
                        //当前点击了搜索个股Tab  并且issearch为false 则直接退出
                        if (tabLayout.getSelectedTabPosition() == 5 && !isSearch) {
                            return;
                        }

//                        ToastUtil.showShort(getActivity(), "加载更多数据...");
                        if (page % 2 == 0) {
                            listView.setBackgroundColor(Color.rgb(34, 34, 34));
                        } else {
                            listView.setBackgroundColor(Color.rgb(0, 0, 0));
                        }
                        if (adapter == null) {
                            adapter = new MarketListAdapter(datas, getActivity());
                            listView.setAdapter(adapter);
                        } else {
                            adapter.notifyData(datas);

                            if (lastPage != page) {

                                listView.setSelection(0);
                            }
                            lastPage = page;
                        }


                        //item的短按事件   进入分时图
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent = new Intent(getActivity(), ChartActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("name", datas.get(i).getName());
                                bundle.putString("symbol", datas.get(i).getSymbol());
                                intent.putExtras(bundle);
                                startActivity(intent);

                            }
                        });

                        //item的长按事件   加入或移除  自选股
                        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                                if (!isOptional && mLists.contains(datas.get(position).getSymbol())) {
                                    ToastUtil.showShort(getActivity(), datas.get(position).getName() + "已加入到自选股");
                                    return true;
                                }

                                myAlertDialog.builder().setTitle(isOptional ? "移除自选股" : "添加自选股")
                                        .setMsg(isOptional ? "你要将 " + datas.get(position).getName() + " 从自选股中移除吗？" : "你要将 " + datas.get(position).getName() + " 添加到自选股吗？")
                                        .setPositiveButton("确定", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (isOptional) {
                                                    deleteData(datas.get(position).getSymbol());
                                                    datas.remove(position);
                                                    adapter.notifyDataSetChanged();
                                                } else {
                                                    saveData(datas.get(position).getSymbol());
                                                }
                                                ToastUtil.showShort(getActivity(), isOptional ? "移除成功" : "添加成功");
                                            }
                                        }).setNegativeButton("取消", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        myAlertDialog.dismiss();
                                    }
                                }).show();

                                return true;
                            }
                        });

                        ptrl.refreshFinish(PullToRefreshLayout.SUCCEED);

                        ptrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        break;
                    }
            }
        }
    }


    /**
     * 往数据库中保存数据
     */
    private void saveData(String symbol_save) {
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
    private void queryData() {
        //先将list中的数据清空
        mLists.clear();
        RealmResults<OptionalStockBean> results =
                myRealm.where(OptionalStockBean.class).findAll();

        for (OptionalStockBean c : results) {
            mLists.add(c.getSymbol());
        }

        LogUtil.d("从数据库检索到的数据：" + mLists.toString());
    }


    /**
     * 从数据库中删除数据
     */
    private void deleteData(String symbol_del) {

        myRealm.beginTransaction();
        RealmResults<OptionalStockBean> results =
                myRealm.where(OptionalStockBean.class).findAll();

        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).getSymbol().equals(symbol_del)) {
                results.remove(i);
                //同时将list中的数据删除
                mLists.remove(i);
            }
        }
        myRealm.commitTransaction();

        if (mLists.size() == 0) {
            //将resetURL置空
            resetURL = "";
            getNetWorkData.stop();//停止网络访问
        } else {
            //将股票代码 拼接成指定url
            String url = "";
            //自选
            for (int j = 0; j < mLists.size(); j++) {
                if (j != mLists.size() - 1) {
                    url = url + mLists.get(j) + ",";
                } else {
                    url = url + mLists.get(j);
                }
            }
            //调接口获取自选股数据
            getData(OPTIONAL_URL + url);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getNetWorkData.stop();//停止网络访问
    }
}
