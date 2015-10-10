package com.juttec.goldmetal.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.juttec.goldmetal.R;
import com.juttec.goldmetal.activity.FollowActivity;
import com.juttec.goldmetal.activity.MessageActivity;
import com.juttec.goldmetal.activity.MomentPersonalActivity;
import com.juttec.goldmetal.activity.PublishTopicActivity;
import com.juttec.goldmetal.adapter.MomentRecyclerViewAdapter;
import com.juttec.goldmetal.adapter.RecycleViewWithHeadAdapter;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.DynamicEntityList;
import com.juttec.goldmetal.bean.DynamicMsgBean;
import com.juttec.goldmetal.customview.CircleImageView;
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
import com.nostra13.universalimageloader.core.ImageLoader;

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


    private boolean isLoadingMore;
    View view;
    private int i = 1;

    //tabs
    private TextView dynamic, message, follow;


    ArrayList<DynamicEntityList> entityList;

    RecyclerView recyclerView;

    MomentRecyclerViewAdapter adapter;
    RecycleViewWithHeadAdapter myAdapter;

    View myHead;

    Gson gson;


    private CircleImageView mHeadPhoto;//头像

    private MyProgressDialog dialog_progress;//正在加载的进度框

    private static String path;//存放照相或者从相册选择的图片的路径

    private final static int REQUEST_CODE_CAMERA = 333;//照相的返回码
    private final static int REQUEST_CODE_ALBUM = 444;//相册的返回码
    SwipeRefreshLayout refreshLayout;

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
        app = (MyApplication) getActivity().getApplication();
        dialog_progress = new MyProgressDialog(getActivity());
        entityList = new ArrayList<DynamicEntityList>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_moment, container, false);

        initView(view);

        setRecyclerView(view);


        gson = new Gson();
        getInfo(i, MyApplication.DYNAMIC_TYPE_ALL);

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

        mHeadPhoto = (CircleImageView) myHead.findViewById(R.id.iv_head_photo);
        mHeadPhoto.setOnClickListener(this);
        if (!"null".equals(app.getUserInfoBean().getUserPhoto())) {
            ImageLoader.getInstance().displayImage(app.getImgBaseUrl() + app.getUserInfoBean().getUserPhoto(), mHeadPhoto);
        }


        //init tabs
        initTabs(myHead);


        /*初始化Recyclerview*/
        recyclerView = (RecyclerView) view.findViewById(R.id.moment_recyclerview);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                //lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 1 && dy > 0) {
                    if (!isLoadingMore) {

                        getInfo(i, MyApplication.DYNAMIC_TYPE_ALL);
                        isLoadingMore = true;
                    }
                }
            }
        });
//        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//              int  visibleItemCount = layoutManager.getChildCount();
//               int totalItemCount = layoutManager.getItemCount();
//               int pastItems = layoutManager.findFirstVisibleItemPosition();
//
//                if (!isLoadingMore) {
//
//                    if ((pastItems + visibleItemCount) >= totalItemCount) {
//
//                        isLoadingMore = true;
//                        // load something new and set adapter notifyDatasetChanged
//                        // 记得在 load something 完了以后把 onLoading 赋值为 false
//                    }
//                }
//            }
//        });

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

                startActivity(new Intent(getActivity(), MessageActivity.class));
                break;
            case R.id.moment_tv_follow:

                startActivity(new Intent(getActivity(), FollowActivity.class));
                break;
            case R.id.right_text:
                startActivity(new Intent(getActivity(), PublishTopicActivity.class));
                break;

            case R.id.iv_head_photo:
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

                break;
        }
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
     * @param page 页数
     * @param type 类型 all：所有 attention：关注 personal：个人
     */
    private void getInfo(int page, String type) {
        RequestParams params = new RequestParams();

        params.addBodyParameter("userId", app.getUserInfoBean().getUserId());
        params.addBodyParameter("pageIndex", page + "");
        params.addBodyParameter("dyType", type);

        refreshLayout.setRefreshing(true);

        new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getGetDynamicUrl(), params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {


                        refreshLayout.setRefreshing(false);


                        DynamicMsgBean dynamicMsgBean = gson.fromJson(responseInfo.result.toString(), DynamicMsgBean.class);


                        if (i == 1) {
                            entityList.clear();
                        }
                        List<DynamicEntityList> dynamicEntityLists = dynamicMsgBean.getEntityList();
                        entityList.addAll(dynamicEntityLists);
                        i++;
                        if (adapter == null) {
                            adapter = new MomentRecyclerViewAdapter(entityList, getActivity(), app);
                            //添加回调事件

                            // 添加头部
                            myAdapter = new RecycleViewWithHeadAdapter<>(adapter);
                            myAdapter.addHeader(myHead);
                            // 设置Adapter
                            recyclerView.setAdapter(myAdapter);
                        } else {

                            adapter.notifyDataSetChanged();
                            myAdapter.notifyDataSetChanged();

                        }
                        isLoadingMore = false;
                        callBack();

                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        refreshLayout.setRefreshing(false);
                        refreshLayout.setRefreshing(false);
                        NetWorkUtils.showMsg(getActivity());

                    }


                }

        );

    }

    private void callBack() {
        //回调事件
        adapter.setOnMyClickListener(new MomentRecyclerViewAdapter.OnMyClickListener() {
                                         @Override
                                         public void onClick(View v, final int posion, final String rpliedName, final LinearLayout viewRoot) {


                                             switch (v.getId()) {
                                                 case R.id.recyclerview_item:
                                                     startActivity(new Intent(getActivity(), MomentPersonalActivity.class));
                                                     break;
                                                 case R.id.dynamic_item_reply:

                                                     LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                     final View contentview = inflater.inflate(R.layout.commonality_comments, null);
                                                     contentview.setFocusable(true); // 这个很重要
                                                     contentview.setFocusableInTouchMode(true);
                                                     final PopupWindow popupWindow = new PopupWindow(contentview, LinearLayout.LayoutParams.MATCH_PARENT, 250);
                                                     popupWindow.setFocusable(true);
                                                     popupWindow.setOutsideTouchable(false);
                                                     popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
                                                     popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
                                                     popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                                                     contentview.setOnKeyListener(new View.OnKeyListener() {
                                                         @Override
                                                         public boolean onKey(View v, int keyCode, KeyEvent event) {
                                                             if (keyCode == KeyEvent.KEYCODE_BACK) {
                                                                 popupWindow.dismiss();

                                                                 return true;
                                                             }
                                                             return false;
                                                         }
                                                     });
                                                     popupWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                     final EditText editText = (EditText) contentview.findViewById(R.id.comment_et_reply);

                                                     editText.setFocusable(true);
                                                     editText.setFocusableInTouchMode(true);
                                                     editText.requestFocus();
                                                     InputMethodManager inputMethodManager =
                                                             (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                                     inputMethodManager.toggleSoftInputFromWindow(editText.getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);


                                                     editText.setHint("回复" + entityList.get(posion).getUserName());


                                                     ImageButton imageButton = (ImageButton) contentview.findViewById(R.id.comment_ib_emoji);
                                                     Button button = (Button) contentview.findViewById(R.id.btn_send);


                                                     button.setOnClickListener(new View.OnClickListener() {
                                                         @Override
                                                         public void onClick(View v) {
                                                             if (!"".equals(editText.getText().toString()) || editText.getText() == null) {
                                                                 RequestParams param = new RequestParams();
                                                                 param.addBodyParameter("dyId", entityList.get(posion).getId());
                                                                 param.addBodyParameter("discussantId", app.getUserInfoBean().getUserId());
                                                                 param.addBodyParameter("discussantName", app.getUserInfoBean().getUserNickName());
                                                                 param.addBodyParameter("commentContent", editText.getText().toString());

                                                                 final Editable editable = editText.getText();
                                                                 new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getCommentUrl(), param, new RequestCallBack<String>() {

                                                                     @Override
                                                                     public void onSuccess(ResponseInfo<String> responseInfo) {
                                                                         try {
                                                                             JSONObject object = new JSONObject(responseInfo.result.toString());

                                                                             ToastUtil.showShort(getActivity(), object.getString("promptInfor"));
                                                                             if ("1".equals(object.getString("status"))) {
                                                                                 adapter.addReplyView(viewRoot, app.getUserInfoBean().getUserNickName(), null, editable);
                                                                                 popupWindow.dismiss();
                                                                             }

                                                                         } catch (JSONException e) {
                                                                             e.printStackTrace();
                                                                         }

                                                                     }

                                                                     @Override
                                                                     public void onFailure(HttpException error, String msg) {
                                                                         NetWorkUtils.showMsg(getActivity());
                                                                     }
                                                                 });
                                                             } else
                                                                 ToastUtil.showShort(getActivity(), "回复内容不能为空");
                                                             editText.setText("");

                                                         }
                                                     });


                                                     break;

                                             }
                                         }
                                     }

        );

    }
}
