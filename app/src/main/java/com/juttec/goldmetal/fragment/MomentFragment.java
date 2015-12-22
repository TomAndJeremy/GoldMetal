package com.juttec.goldmetal.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.juttec.goldmetal.R;
import com.juttec.goldmetal.activity.AccountActivity;
import com.juttec.goldmetal.activity.FollowActivity;
import com.juttec.goldmetal.activity.LoginActivity;
import com.juttec.goldmetal.activity.MessageActivity;
import com.juttec.goldmetal.activity.PublishTopicActivity;
import com.juttec.goldmetal.adapter.MomentRecyclerViewAdapter;
import com.juttec.goldmetal.adapter.RecycleViewWithHeadAdapter;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.DynamicEntityList;
import com.juttec.goldmetal.bean.DynamicMsgBean;
import com.juttec.goldmetal.broadcastreceiver.MyBroadcastReceiver;
import com.juttec.goldmetal.customview.CircleImageView;
import com.juttec.goldmetal.dialog.MyAlertDialog;
import com.juttec.goldmetal.dialog.MyProgressDialog;
import com.juttec.goldmetal.utils.FileUtil;
import com.juttec.goldmetal.utils.GetContentUrl;
import com.juttec.goldmetal.utils.ImgUtil;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.NetWorkUtils;
import com.juttec.goldmetal.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 交易圈fragment
 */


public class MomentFragment extends BaseFragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";


    private String mParam1;

    private MyApplication app;


    private boolean isLoadingMore;//判断是否加载
    View view;//显示界面
    private int i = 1;//加载第一页

    //tabs
    private TextView dynamic, message, follow;//动态，消息，关注


    private ArrayList<DynamicEntityList> entityList;//实体集合

    private RecyclerView recyclerView;

    private MomentRecyclerViewAdapter adapter;
    private RecycleViewWithHeadAdapter myAdapter;

    private View myHead;//头部

    private Gson gson;


    private CircleImageView mHeadPhoto;//头像

    private MyProgressDialog dialog_progress;//正在加载的进度框

    private static String path;//存放照相或者从相册选择的图片的路径


    private final static int REFRESH = 1111;//发表动态后刷新
    private final static int LOGIN = 2222;//登陆后刷新
    private final static int REQUEST_CODE_CAMERA = 333;//照相的返回码
    private final static int REQUEST_CODE_ALBUM = 444;//相册的返回码
    SwipeRefreshLayout refreshLayout;
    MyBroadcastReceiver myBroadcastReceiver;//同步点赞或者评论

    private MyAlertDialog mDialog;//对话框

    protected ImageLoader imageLoader;//图片加载工具
    private DisplayImageOptions options;//


    //初始化
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
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
                .build();//构建完成

//                .showImageOnLoading(R.drawable.ic_launcher) //设置图片在下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.ic_launcher)//设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.ic_launcher)  //设置图片加载/解码过程中错误时候显示的图片
//                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
//                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
//                .decodingOptions(android.graphics.BitmapFactory.Options decodingOptions)//设置图片的解码配置
//.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
//设置图片加入缓存前，对bitmap进行设置
//.preProcessor(BitmapProcessor preProcessor)
//                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
//                .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少


        app = (MyApplication) getActivity().getApplication();
        mDialog = new MyAlertDialog(getActivity());
        dialog_progress = new MyProgressDialog(getActivity());
        entityList = new ArrayList<DynamicEntityList>();


        myBroadcastReceiver = new MyBroadcastReceiver(entityList, app);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.juttec.goldmetal.addsupport");
        filter.addAction("com.juttec.goldmetal.cancelsupport");
        filter.addAction("com.juttec.goldmetal.comment");
        //将BroadcastReceiver对象注册到系统当中
        getActivity().registerReceiver(myBroadcastReceiver, filter);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_moment, container, false);

        initView(view);//初始化控件

        setRecyclerView(view);//初始化化recycleview


        gson = new Gson();


        return view;
    }

    private void initView(View view) {
        // 头部
        RelativeLayout head = (RelativeLayout) view.findViewById(R.id.head_layout);
        TextView rightText = (TextView) head.findViewById(R.id.right_text);
        rightText.setOnClickListener(this);


        //下拉刷新
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        refreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.GREEN);

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                getInfo(1, MyApplication.DYNAMIC_TYPE_ALL);
            }
        });//初次进入加载


        //下拉监听
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO: 2015/9/14
                refreshLayout.setRefreshing(true);
                i = 1;
                getInfo(i, MyApplication.DYNAMIC_TYPE_ALL);

            }
        });
    }


    private void setRecyclerView(View view) {
        //recyclerview 头部
        myHead = View.inflate(getActivity(), R.layout.recycleview_head, null);//头布局
        myHead.findViewById(R.id.moment_btn_cancel).setVisibility(View.GONE);
        myHead.findViewById(R.id.moment_btn_follow).setVisibility(View.GONE);

        //头像
        mHeadPhoto = (CircleImageView) myHead.findViewById(R.id.iv_head_photo);
        mHeadPhoto.setOnClickListener(this);
        if (app.isLogin()) {//如果用户已经登陆
            if (!"null".equals(app.getUserInfoBean().getUserPhoto())) {
               imageLoader.displayImage(app.getImgBaseUrl() + app.getUserInfoBean().getUserPhoto(), mHeadPhoto, options);
            }
        }


        //init tabs
        initTabs(myHead);


        /*初始化Recyclerview*/
        recyclerView = (RecyclerView) view.findViewById(R.id.moment_recyclerview);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        //设置上拉加载
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                //lastVisibleItem >= totalItemCount - 1 表示剩下1个item自动加载，各位自由选择
                // dy>0 表示上拉
                if (lastVisibleItem >= totalItemCount - 1 && dy > 0) {
                    if (!isLoadingMore) {
                        getInfo(i, MyApplication.DYNAMIC_TYPE_ALL);
                        isLoadingMore = true;
                    }
                }
            }
        });

    }

    private void initTabs(View view) {
        dynamic = (TextView) view.findViewById(R.id.moment_tv_dynamic);
        message = (TextView) view.findViewById(R.id.moment_tv_message);
        follow = (TextView) view.findViewById(R.id.moment_tv_follow);
        dynamic.setSelected(true);
        dynamic.setOnClickListener(this);
        message.setOnClickListener(this);
        follow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.moment_tv_dynamic:
                dynamic.setSelected(true);
                break;
            case R.id.moment_tv_message:
                //消息界面  先判断用户信息是否完善
                if (checkNameAndPhoto()) {
                    startActivity(new Intent(getActivity(), MessageActivity.class));
                }

                break;
            case R.id.moment_tv_follow:
                //我的关注界面  先判断用户信息是否完善
                if (checkNameAndPhoto()) {
                    startActivity(new Intent(getActivity(), FollowActivity.class));
                }

                break;
            case R.id.right_text:
                //发表动态  先判断用户信息是否完善
                if (checkNameAndPhoto()) {
                    startActivityForResult(new Intent(getActivity(), PublishTopicActivity.class), REFRESH);
                }

                break;

            case R.id.iv_head_photo:
                //设置头像
                if (app.isLogin()) {
                    setHeadPhoto();
                } else {
                    ToastUtil.showShort(getActivity(), "请先登录再进行操作");
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), LOGIN);

                }

                break;
        }
    }


    //设置头像  弹出从从相册选择或是相机拍摄
    private void setHeadPhoto() {
        //头像的点击事件
        final Dialog dialog = new Dialog(getActivity(), R.style.AlertDialogStyle);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.getWindow().setContentView(R.layout.select_pic_dialog);

        dialog.getWindow().findViewById(R.id.camera_shooting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拍照
                dialog.dismiss();
                mobileTakePic(REQUEST_CODE_CAMERA);
            }
        });
        dialog.getWindow().findViewById(R.id.photo_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从相册选择
                dialog.dismiss();
                Intent picture = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(picture, REQUEST_CODE_ALBUM);
            }
        });
        dialog.getWindow().findViewById(R.id.btn_single).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

    }


    /**
     * 手机拍照 参数:对应的请求码
     */
    private void mobileTakePic(int request) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(FileUtil.getAlbumStorageDir(getActivity(), "Picture"), FileUtil.getPhotoFileName());
        path = file.getAbsolutePath();
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, request);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // 当选择拍照时调用
            case REQUEST_CODE_CAMERA:

                if (resultCode == Activity.RESULT_OK) {
                    LogUtil.d("拍照图片的路径：" + path);

                    //上传头像
                    uploadUserPhoto();
                } else {
//                    SnackbarUtil.showLong(this, "亲，拍照失败");
                }
                break;

            // 从手机相册返回
            case REQUEST_CODE_ALBUM:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = GetContentUrl.geturi(data, getActivity());
                    String[] filePathColumns = {MediaStore.Images.Media.DATA};
                    Cursor c = getActivity().getContentResolver().query(selectedImage,
                            filePathColumns, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePathColumns[0]);
                    path = c.getString(columnIndex);
                    LogUtil.d("选取相册中图片的路径：" + path);
                    c.close();
                    //上传头像
                    uploadUserPhoto();
                }
                break;
            case REFRESH:
                i = 1;
                getInfo(i, MyApplication.DYNAMIC_TYPE_ALL);

                recyclerView.scrollToPosition(0);

                break;
            case LOGIN://登陆后设置头像,刷新数据
                if (app.isLogin()) {
                    adapter.notifyDataSetChanged();
                    myAdapter.notifyDataSetChanged();

                    if (!"null".equals(app.getUserInfoBean().getUserPhoto())) {
                        imageLoader.displayImage(app.getImgBaseUrl() + app.getUserInfoBean().getUserPhoto(), mHeadPhoto, options);
                    }
                }
                break;
        }

    }


    //设置头像
    private void setImg() {
        if (path != null) {
            mHeadPhoto.setImageBitmap(ImgUtil.getBitmap(path, 200, 200));
        }
    }

    // 根据路径 取图片 将图片变成字符串
    private String getBitmapString(String p) {
        Bitmap bitmap = ImgUtil.getBitmap(p, 200, 200);
        // 根据路径 获取图片
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();
        return Base64.encodeToString(appicon, Base64.DEFAULT);
    }


    //判断用户是否编辑了个人的昵称和头像
    private boolean checkNameAndPhoto() {
        if (!app.isLogin()) {
            startActivityForResult(new Intent(getActivity(), LoginActivity.class), LOGIN);
            return false;
        }
        if ("".equals(app.getUserInfoBean().getUserNickName()) || null == (app.getUserInfoBean().getUserNickName())) {

            //设置昵称
            mDialog.builder()
                    .setTitle("提示").setMsg("您还没有昵称，请至账号界面设置后再操作，谢谢！")
                    .setSingleButton("前去设置", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getActivity(), AccountActivity.class));
                            mDialog.dismiss();
                        }
                    }).show();
            return false;

        } else if ("null".equals(app.getUserInfoBean().getUserPhoto())) {
            //设置头像
            mDialog.builder()
                    .setTitle("提示").setMsg("您还没有个人头像，请当前界面设置后再操作，谢谢！")
                    .setSingleButton("好的", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setHeadPhoto();
                            mDialog.dismiss();
                        }
                    }).show();
            return false;
        }
        return true;
    }


    //上传用户头像  的接口
    private void uploadUserPhoto() {

        dialog_progress.builder().setMessage("努力上传中~").show();
        RequestParams params = new RequestParams();
        params.addBodyParameter("userPhotoFile", getBitmapString(path));
        params.addBodyParameter("userId", app.getUserInfoBean().getUserId());

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.getUploadUserPhotoUrl(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog_progress.dismiss();
                LogUtil.d("上传图片接口返回结果:" + responseInfo.result.toString());
                JSONObject object = null;
                try {
                    object = new JSONObject(responseInfo.result.toString());
                    if ("1".equals(object.getString("status"))) {
                        setImg();
                        //修改用户头像的路径
                        app.getUserInfoBean().setUserPhoto(object.getString("message1"));
                        ToastUtil.showShort(getActivity(), "上传成功");

                    } else {
                        ToastUtil.showShort(getActivity(), "上传失败");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog_progress.dismiss();
                ToastUtil.showShort(getActivity(), "请检查网络是否正常连接");
            }
        });

    }


    //获取用户头像的  接口
    private void getUserPhoto() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("userId", app.getUserInfoBean().getUserId());

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.getGetUserPhotoUrl(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtil.d("获取用户头像接口返回结果:" + responseInfo.result.toString());
                JSONObject object = null;
                try {
                    object = new JSONObject(responseInfo.result.toString());
                    if ("1".equals(object.getString("status"))) {

                        ToastUtil.showShort(getActivity(), "获取头像成功");

                    } else {
                        ToastUtil.showShort(getActivity(), "获取头像失败");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                ToastUtil.showShort(getActivity(), "请检查网络是否正常连接");
            }
        });

    }


    /**
     * 获取动态
     *
     * @param page 页数
     * @param type 类型 all：所有 attention：关注 personal：个人
     */
    private void getInfo(int page, String type) {
        LogUtil.d("交易圈 首页  current page:"+page);
        RequestParams params = new RequestParams();



        if (app.isLogin()) {
            params.addBodyParameter("userId", app.getUserInfoBean().getUserId());
        } else {
            params.addBodyParameter("userId", "1");
        }
        params.addBodyParameter("pageIndex", page + "");
        params.addBodyParameter("dyType", type);

        refreshLayout.setRefreshing(true);

        new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getGetDynamicUrl(), params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {

//                        try {
//                            JSONObject object = new JSONObject(responseInfo.result.toString());
//                            LogUtil.d("交易圈 首页  total page:"+object.getString("message1"));//message1
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }


                        refreshLayout.setRefreshing(false);


                        DynamicMsgBean dynamicMsgBean = gson.fromJson(responseInfo.result.toString(), DynamicMsgBean.class);//解析数据
                        if (i == 1) {
                            entityList.clear();//刷新时先清空集合
                        }
                        List<DynamicEntityList> dynamicEntityLists = dynamicMsgBean.getEntityList();


                        try {
                            entityList.addAll(dynamicEntityLists);//向集合中添加数据
                            i++;//每次加载后页数加一
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtil.e("交易圈异常："+ e.toString());
                        }
                        if (adapter == null) {
                            adapter = new MomentRecyclerViewAdapter(entityList, getActivity(), app, MomentFragment.this);


                            // 添加头部
                            myAdapter = new RecycleViewWithHeadAdapter<>(adapter);
                            myAdapter.addHeader(myHead);
                            adapter.setHeadAdapter(myAdapter);

                            // 设置Adapter
                            recyclerView.setAdapter(myAdapter);
                            myBroadcastReceiver.setMyAdapter(myAdapter);

                        } else {
                            adapter.notifyDataSetChanged();
                            myAdapter.notifyDataSetChanged();
                        }

                        isLoadingMore = false;
                        //添加回调事件
                        // callBack();

                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        refreshLayout.setRefreshing(false);
                        NetWorkUtils.showMsg(getActivity());

                    }


                }

        );

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(myBroadcastReceiver);

    }
}
