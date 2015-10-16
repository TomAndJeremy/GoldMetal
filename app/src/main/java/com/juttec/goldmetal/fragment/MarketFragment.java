package com.juttec.goldmetal.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.activity.AccountActivity;
import com.juttec.goldmetal.activity.AnnouncementActivity;
import com.juttec.goldmetal.activity.ChartActivity;
import com.juttec.goldmetal.activity.CreateAccount.AccountNoticeActivity;
import com.juttec.goldmetal.adapter.MarketRecyclerAdapter;
import com.juttec.goldmetal.bean.MarketFormInfo;
import com.juttec.goldmetal.dialog.MyAlertDialog;

import java.util.ArrayList;
import java.util.List;


public class MarketFragment extends BaseFragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private ImageView iv_search;//搜索
    private RecyclerView recyclerView;
    private List<MarketFormInfo> datas;
    private Button btCreateAccount;//开户按钮
    
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_market, container, false);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.market_tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("自选"));
        tabLayout.addTab(tabLayout.newTab().setText("现货"));
        tabLayout.addTab(tabLayout.newTab().setText("股票"));

        initView(view);
        return view;
    }




    private void initView(View view){
        iv_search = (ImageView) view.findViewById(R.id.market_search);

        ImageView rightImg = (ImageView) view.findViewById(R.id.right_img);
        ImageView leftImg = (ImageView) view.findViewById(R.id.left_img);
        rightImg.setOnClickListener(this);
        leftImg.setOnClickListener(this);
        
        iv_search.setOnClickListener(this);
        btCreateAccount = (Button) view.findViewById(R.id.fragment_market_bt_createaccount);
        btCreateAccount.setOnClickListener(this) ;

        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_market_recyclerview);

        initRecyclerView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.market_search:
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
                startActivity(new Intent(getActivity(), AccountActivity.class));
                break;

            case R.id.left_img:
                startActivity(new Intent(getActivity(), AnnouncementActivity.class));
                break;

          
            case R.id.fragment_market_bt_createaccount:
                //开户界面
                startActivity(new Intent(getActivity(), AccountNoticeActivity.class));
                break;
        }


    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SampleDivider(getActivity(), R.drawable.divider_shape));

        datas = new ArrayList<>();
        MarketFormInfo bean = new MarketFormInfo("黄金现货", "1203.98", "10086.22", "1200.69", "10089.87", "0.88", "0.45%", "1");
        datas.add(bean);
        bean = new MarketFormInfo("黄金白银", "803.98", "8086.22", "800.69", "8089.87", "0.68", "0.35%", "1");
        datas.add(bean);
        bean = new MarketFormInfo("原油", "1203.98", "10086.22", "1200.69", "10089.87", "0.88", "0.45%", "2");
        datas.add(bean);
        bean = new MarketFormInfo("鲁商银", "3201.98", "30086.22", "3200.62", "3089.82", "0.80", "0.25%", "2");
        datas.add(bean);
        bean = new MarketFormInfo("华交银", "3203.98", "3086.22", "3100.69", "3032.87", "0.88", "0.45%", "1");
        datas.add(bean);
        bean = new MarketFormInfo("现货铂金", "1203.98", "10086.22", "1200.69", "10089.87", "0.88", "0.45%", "1");
        datas.add(bean);
        bean = new MarketFormInfo("铜", "103.98", "1086.22", "100.69", "1089.87", "0.88", "0.45%", "1");
        datas.add(bean);
        bean = new MarketFormInfo("现货钯金", "1803.98", "18086.22", "1800.69", "18089.87", "0.88", "0.40%", "2");
        datas.add(bean);
        bean = new MarketFormInfo("美元指数", "203.98", "2086.22", "200.69", "2089.87", "0.38", "0.15%", "2");
        datas.add(bean);
        bean = new MarketFormInfo("镍", "3203.98", "30086.22", "3200.69", "30089.87", "1.88", "0.85%", "1");
        datas.add(bean);
        MarketRecyclerAdapter adapter=new MarketRecyclerAdapter(datas, getActivity());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MarketRecyclerAdapter.OnItemClickListener() {
            @Override
            public void itemclickListener(View view, int position) {
                Intent intent = new Intent(getActivity(), ChartActivity.class);
                startActivity(intent);
            }
        });

    }

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
}
