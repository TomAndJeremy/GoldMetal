package com.juttec.goldmetal.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.adapter.MomentRecyclerViewAdapter;
import com.juttec.goldmetal.adapter.RecycleViewWithHeadAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MomentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MomentFragment extends BaseFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    //tabs
    TextView dynamic, message, follow;


    // private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment BlankFragment3.
     */
    // TODO: Rename and change types and number of parameters
    public static MomentFragment newInstance(String param1) {
        MomentFragment fragment = new MomentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public MomentFragment() {
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
        View view = inflater.inflate(R.layout.fragment_moment, container, false);


        //下拉刷新
        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        refreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO: 2015/9/14  
                refreshLayout.setRefreshing(true);
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                },2000);
                Toast.makeText(getActivity(), "121312", Toast.LENGTH_LONG).show();

            }
        });



        /*初始化Recyclerview*/

        View myHead = View.inflate(getActivity(), R.layout.recycleview_head, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.moment_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        String[] dataset = new String[100];
        for (int i = 0; i < dataset.length; i++) {
            dataset[i] = "item" + i;
        }
        // 创建Adapter，并指定数据集

        MomentRecyclerViewAdapter adapter = new MomentRecyclerViewAdapter(dataset, getActivity());

        adapter.setOnItemClickListener(new MomentRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int posion) {
                Snackbar.make(v, "this is " + posion, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        RecycleViewWithHeadAdapter myAdapter = new RecycleViewWithHeadAdapter<>(adapter);

        myAdapter.addHeader(myHead);


        // 设置Adapter
        recyclerView.setAdapter(myAdapter);


        //init tabs
        // initTabs(view);


        return view;
    }

    private void initTabs(View view) {
        dynamic = (TextView) view.findViewById(R.id.moment_btn_dynamic);
        message = (TextView) view.findViewById(R.id.moment_btn_message);
        follow = (TextView) view.findViewById(R.id.moment_btn_follow);
        dynamic.setSelected(true);
        dynamic.setOnClickListener(this);
        message.setOnClickListener(this);
        follow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        cancelSelect();
        switch (v.getId()) {
            case R.id.moment_btn_dynamic:
                dynamic.setSelected(true);
                break;
            case R.id.moment_btn_message:
                message.setSelected(true);
                break;
            case R.id.moment_btn_follow:
                follow.setSelected(true);
                break;
        }
    }

    private void cancelSelect() {
        dynamic.setSelected(false);
        message.setSelected(false);
        follow.setSelected(false);
    }

    public boolean isViewTop(View view) {
        boolean apply = false;
        if (view != null && view instanceof RecyclerView) {

            RecyclerView.LayoutManager layout = ((RecyclerView) view).getLayoutManager();
            if (layout != null && layout instanceof LinearLayoutManager) {

                int orientation = ((LinearLayoutManager) layout).getOrientation();
                if (orientation == LinearLayoutManager.VERTICAL) {
                    View child = layout.getChildAt(0);
                    if (child != null) {
                        int position = ((RecyclerView) view).getChildPosition(child);
                        if (position == 0) {
                            apply = child.getTop() >= view.getTop();
                        }
                    } else {
                        apply = true;
                    }
                }
            }
        }
        return apply;
    }

}
