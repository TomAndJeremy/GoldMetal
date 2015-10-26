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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.activity.AccountActivity;
import com.juttec.goldmetal.activity.AnnouncementActivity;
import com.juttec.goldmetal.activity.ChartActivity;
import com.juttec.goldmetal.activity.CreateAccount.AccountNoticeActivity;
import com.juttec.goldmetal.activity.TodayStrategyActivity;
import com.juttec.goldmetal.adapter.MarketRecyclerAdapter;
import com.juttec.goldmetal.bean.MarketFormInfo;
import com.juttec.goldmetal.bean.MyEntity;
import com.juttec.goldmetal.dialog.MyAlertDialog;
import com.juttec.goldmetal.utils.GetNetworkData;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;


public class MarketFragment extends BaseFragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";

    //自选接口路径
    private String OPTIONAL_URL = "http://db2015.wstock.cn/wsDB_API/stock.php?symbol=OSXAU,OSXAG,OZAG20,OZAG50,OZAG100,OYXAG50KG,OYXAG150KG,NECLI0,OSUDI&r_type=2&u=qq3585&p=qq3771";
    //现货 接口 路径
    private String SPOT_URL = "http://db2015.wstock.cn/wsDB_API/stock.php?symbol=OSXAU,OSXAG,OZPT,OZPD,OSHKG&r_type=2&u=qq3585&p=qq3771";

    private String mParam1;

    private ImageView iv_search;//搜索
    private RecyclerView recyclerView;
    private List<MarketFormInfo.ResultEntity> datas;
    private Button btCreateAccount;//开户按钮
    private TextView strategy;
    private MyEntity myEntity;
    private static final int NEWEST = 0;  //加载最新数据的标识
    private MarketFormInfo marketFormInfo;
    MarketRecyclerAdapter adapter;
    MyHandler myHandler;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_market, container, false);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.market_tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("自选").setTag(1), true);
        tabLayout.addTab(tabLayout.newTab().setText("现货").setTag(2));
        tabLayout.addTab(tabLayout.newTab().setText("股票").setTag(3));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch ((int) tab.getTag()) {
                    case 1:
                        //自选
                        getData(OPTIONAL_URL);
                        break;
                    case 2:
                        //现货
                        getData(SPOT_URL);

                        break;
                    case 3:
                        //股票
                        ToastUtil.showShort(getActivity(), "请稍等，稍候接入！");
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
        getData(OPTIONAL_URL);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SampleDivider(getActivity(), R.drawable.divider_shape));


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.market_search:
                //搜索个股
                final MyAlertDialog dialog = new MyAlertDialog(getActivity());
                dialog.builder()
                        .setTitle("搜索个股").setEditText("请输入个股代码")
                        .setSingleButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        }).show();

                break;

            case R.id.right_img:
                //个人账户
                startActivity(new Intent(getActivity(), AccountActivity.class));
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
                Intent intent = new Intent(getActivity(), TodayStrategyActivity.class);

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

                        recyclerView.setAdapter(adapter);
                        break;
                    }
            }
        }
    }
}
