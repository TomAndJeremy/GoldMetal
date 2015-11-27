package com.juttec.goldmetal.activity;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.adapter.EmoticonsGridAdapter.KeyClickListener;
import com.juttec.goldmetal.adapter.EmoticonsPagerAdapter;
import com.juttec.goldmetal.adapter.PhotoGridAdapter;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.PhotoBean;
import com.juttec.goldmetal.customview.NoScrollGridView;
import com.juttec.goldmetal.dialog.MyProgressDialog;
import com.juttec.goldmetal.utils.FileUtil;
import com.juttec.goldmetal.utils.GetContentUrl;
import com.juttec.goldmetal.utils.ImgUtil;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 发布消息  界面
 */

public class PublishTopicActivity extends AppCompatActivity implements KeyClickListener, View.OnClickListener {


    public final static int REQUEST_CODE_CAMERA = 111;
    public final static int REQUEST_CODE_ALBUM = 222;




    private View popUpView;
    private LinearLayout emojiconsCover;
    private PopupWindow popupWindow;


    private int keyboardHeight;//键盘高度


    private EditText mContent;//发表的内容

    private RelativeLayout parentLayout;

    private Button mBtnPush;//发表按钮


    private List<String> photoList = new ArrayList<String>();//存放   上传图片后返回的路径

    private static String path;//照相后图片的路径

    // 存放拍照或从相册选择的图片的路径 的集合
    private List<PhotoBean> picPathList = new ArrayList<PhotoBean>();

    private NoScrollGridView mGridView;//展示图片的gridview
    private PhotoGridAdapter mAdapter;

    private int count = 0;//图片上传到了 第几张


    private boolean isKeyBoardVisible;//键盘是否显示或隐藏

    private Bitmap[] emoticons;


    private MyApplication app;

    private MyProgressDialog dialog_progress;//正在加载的  进度框

    private Map<Integer, String> map = new HashMap<>();


//    private HeadLayout mHeadLayout;//标题栏
//    private ImageView iv_back;//返回按钮


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_topic);
        app = (MyApplication) getApplication();
        LogUtil.d("onCreate------------");

        dialog_progress = new MyProgressDialog(this);

        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d("onStop------------");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清除数据
        clearData();
        LogUtil.d("onDestroy------------");
    }


    private void initView() {

//        mHeadLayout = (HeadLayout) findViewById(R.id.head_layout);
//        iv_back = (ImageView) mHeadLayout.findViewById(R.id.left_img);
//        iv_back.setOnClickListener(this);


        parentLayout = (RelativeLayout) this.findViewById(R.id.rl_pta_parent);
        emojiconsCover = (LinearLayout) this.findViewById(R.id.content_for_emoticons);
        popUpView = getLayoutInflater().inflate(R.layout.emoticons_popup, null);


        mContent = (EditText) this.findViewById(R.id.publis_topic_et);
        mContent.setOnClickListener(this);


        mBtnPush = (Button) findViewById(R.id.publis_topic_bt_push);

        mBtnPush.setOnClickListener(this);

        //edittext监听内容变化   更改发送按钮的背景
        mContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               /* LogUtil.e("start  " + start);
                LogUtil.e("before  " + before);
                LogUtil.e("s  " + s);
                LogUtil.e("count  " + count);
                LogUtil.e("aaa    " + s.subSequence(start, s.length()));
                LogUtil.e("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
*/
                String content = mContent.getText().toString();
                if (TextUtils.isEmpty(content) || "".equals(content) || content.trim().length() <= 0) {
                    mBtnPush.setSelected(false);
                } else {
                    mBtnPush.setSelected(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        ImageButton selectPic = (ImageButton) this.findViewById(R.id.publis_topic_bt_pic);
        selectPic.setOnClickListener(this);


        final float popUpheight = getResources().getDimension(
                R.dimen.keyboard_height);
        changeKeyboardHeight((int) popUpheight);

        ImageButton btEmoji = (ImageButton) this.findViewById(R.id.publis_topic_bt_emoji);
        btEmoji.setOnClickListener(this);

        mGridView = (NoScrollGridView) findViewById(R.id.gridview);
        mAdapter = new PhotoGridAdapter(PublishTopicActivity.this, picPathList);
        mGridView.setAdapter(mAdapter);


        readEmojiIcons();
        enablePopUpView();
        // checkKeyboardHeight(parentLayout);

    }


    int previousHeightDiffrence = 0;

    private void checkKeyboardHeight(final RelativeLayout parentLayout) {
        parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        parentLayout.getWindowVisibleDisplayFrame(r);


                        int screenHeight = parentLayout.getRootView()
                                .getHeight();
                        int heightDifference = screenHeight - (r.bottom);

                        if (previousHeightDiffrence - heightDifference > 50) {
                            popupWindow.dismiss();
                        }

                        previousHeightDiffrence = heightDifference;
                        //changeKeyboardHeight(heightDifference);
                        isKeyBoardVisible = heightDifference > 100;

                    }
                });


    }

    private void enablePopUpView() {
        ViewPager pager = (ViewPager) popUpView.findViewById(R.id.emoticons_pager);
        pager.setOffscreenPageLimit(3);
        ArrayList<String> paths = new ArrayList<String>();

        for (short i = 1; i <= MyApplication.ENUM; i++) {
            paths.add(i + ".png");
        }
        EmoticonsPagerAdapter adapter = new EmoticonsPagerAdapter(this, paths, this);
        pager.setAdapter(adapter);


        // Creating a pop window for emoticons keyboard
        popupWindow = new PopupWindow(popUpView, LinearLayout.LayoutParams.MATCH_PARENT,
                keyboardHeight, false);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        TextView backSpace;
        backSpace = (TextView) popUpView.findViewById(R.id.back);
        backSpace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mContent.dispatchKeyEvent(event);
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                emojiconsCover.setVisibility(LinearLayout.GONE);
            }
        });

    }


    private void readEmojiIcons() {
        emoticons = new Bitmap[MyApplication.ENUM];
        for (short i = 0; i < MyApplication.ENUM; i++) {
            emoticons[i] = getImage((i + 1) + ".png");
        }
    }

    /**
     * For loading smileys from assets
     */
    private Bitmap getImage(String path) {
        AssetManager mngr = getAssets();
        InputStream in = null;
        try {
            in = mngr.open("emoticons/" + path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap temp = BitmapFactory.decodeStream(in, null, null);
        return temp;
    }


    private void changeKeyboardHeight(int height) {
        if (height > 100) {
            keyboardHeight = height;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, keyboardHeight);
            emojiconsCover.setLayoutParams(params);
        }
    }

    @Override
    public void keyClickedIndex(final String index) {

        Html.ImageGetter imageGetter = new Html.ImageGetter() {
            public Drawable getDrawable(String source) {
                StringTokenizer st = new StringTokenizer(index, ".");
                Drawable d = new BitmapDrawable(getResources(), emoticons[Integer.parseInt(st.nextToken()) - 1]);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                return d;
            }
        };


        Spanned cs = Html.fromHtml("<img src ='" + index + "'/>", imageGetter, null);
        int cursorPosition = mContent.getSelectionStart();
        mContent.getText().insert(cursorPosition, cs);

        map.put(cursorPosition, index);
    }


    /**
     * 监听返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
            return false;
        } else {

            return super.onKeyDown(keyCode, event);
        }
    }


    public static String string2Unicode(String string) {

        StringBuffer unicode = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {

            // 取出每一个字符
            char c = string.charAt(i);

            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }

        return unicode.toString();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.publis_topic_et:
                //发表内容框
                popupWindow.dismiss();
                break;


            case R.id.publis_topic_bt_push:
                //发表话题 按钮
                String content = mContent.getText().toString();
                if (TextUtils.isEmpty(content) || "".equals(content) || content.trim().length() <= 0) {
                    ToastUtil.showShort(PublishTopicActivity.this, "发表的内容不能为空");
                    return;
                }

                LogUtil.d("发表的内容为：" + mContent.getText().toString());


                dialog_progress.builder().setMessage("努力发表中~").show();
                if (picPathList.size() == 0) {
                    //如果没有图片 直接调发表动态的接口
                    postDynamic();
                } else {
                    //如果有图片 先调上传图片的接口
                    for (int j = 0; j < picPathList.size(); j++) {

                        upLoadPhoto(picPathList.get(j).getDyPhoto());
                    }
                }

                break;


            case R.id.publis_topic_bt_pic:
                //判断图片数量是否超过3张
                if (picPathList.size() == 3) {
                    ToastUtil.showShort(this, "图片数量已达到上限");
                    return;
                }

                final Dialog dialog = new Dialog(PublishTopicActivity.this, R.style.AlertDialogStyle);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                dialog.getWindow().setContentView(R.layout.select_pic_dialog);

                dialog.getWindow().findViewById(R.id.camera_shooting).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        mobileTakePic(REQUEST_CODE_CAMERA);
                    }
                });
                dialog.getWindow().findViewById(R.id.photo_album).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent picture = new Intent(
                                Intent.ACTION_PICK,
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
            case R.id.publis_topic_bt_emoji:

                InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().
                        getSystemService(Context.INPUT_METHOD_SERVICE);


                inputMethodManager.hideSoftInputFromWindow(mContent.getWindowToken(), 0); //隐藏

                if (!popupWindow.isShowing()) {

                    popupWindow.setHeight(keyboardHeight);

                    if (isKeyBoardVisible) {
                        emojiconsCover.setVisibility(LinearLayout.GONE);
                    } else {
                        emojiconsCover.setVisibility(LinearLayout.VISIBLE);
                    }
                    popupWindow.showAtLocation(PublishTopicActivity.this.getCurrentFocus(), Gravity.BOTTOM, 0, 0);

                } else {
                    popupWindow.dismiss();
                }
                break;


        }

    }

    /**
     * 手机拍照 参数:对应的请求码
     */
    private void mobileTakePic(int request) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(FileUtil.getAlbumStorageDir(getApplicationContext(), "Picture"), FileUtil.getPhotoFileName());
        path = file.getAbsolutePath();
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, request);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // 当选择拍照时调用
            case REQUEST_CODE_CAMERA:

                if (resultCode == RESULT_OK) {
                    LogUtil.d("拍照图片的路径：" + path);
                    PhotoBean photoBean = new PhotoBean();
                    photoBean.setDyPhoto(path);
                    photoBean.setIsDelete(false);
                    picPathList.add(photoBean);
                    setImg();
                } else {
//                    SnackbarUtil.showLong(this, "亲，拍照失败");
                }
                break;

            // 从手机相册返回
            case REQUEST_CODE_ALBUM:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = GetContentUrl.geturi(data, getApplicationContext());
                    String[] filePathColumns = {MediaStore.Images.Media.DATA};
                    Cursor c = this.getContentResolver().query(selectedImage,
                            filePathColumns, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePathColumns[0]);
                    String picturePath = c.getString(columnIndex);
                    PhotoBean photoBean = new PhotoBean();
                    photoBean.setDyPhoto(picturePath);
                    photoBean.setIsDelete(false);
                    picPathList.add(photoBean);
                    LogUtil.d("选取相册中图片的路径：" + picturePath);
                    c.close();
                    setImg();
                }
                break;
        }

    }


    //设置图片
    private void setImg() {
        //将删除图标 全部隐藏
        for (int i = 0; i < picPathList.size(); i++) {
            picPathList.get(i).setIsDelete(false);
        }
        mAdapter.notifyDataSetChanged();
    }


    //上传图片接口

    private void upLoadPhoto(String path) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("dyPhotoFile", getBitmapString(path));
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.getUploadPhotoUrl(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtil.d("上传图片接口返回结果:" + responseInfo.result.toString());
                JSONObject object = null;
                try {
                    object = new JSONObject(responseInfo.result.toString());
                    if ("1".equals(object.getString("status"))) {
                        photoList.add(object.getString("message1"));
                        count++;
                        if (count == picPathList.size()) {
                            //调用发表动态接口
                            postDynamic();
                        }

                    } else {
                        dialog_progress.dismiss();
                        photoList.clear();
                        ToastUtil.showShort(PublishTopicActivity.this, "上传图片失败");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog_progress.dismiss();
                ToastUtil.showShort(PublishTopicActivity.this, "请检查网络是否正常连接");
            }
        });

    }


    //发表动态接口
    private void postDynamic() {

        RequestParams params = new RequestParams();
        params.addBodyParameter("userId", app.getUserInfoBean().getUserId());
        params.addBodyParameter("userName", app.getUserInfoBean().getUserNickName());

        String content = mContent.getText().toString();
        for (Map.Entry entry : map.entrySet()) {

            Integer key = (Integer) entry.getKey();
            String value = (String) entry.getValue();

            content = content.replaceFirst("￼", "`" + value + "`");
//             mContent.getText().replace(key, key + 1, "`" + value+"`" );

        }
        params.addBodyParameter("dyContent",content);
        //params.addBodyParameter("dyContent", string2Unicode(content));
        // params.addBodyParameter("dyContent", string2Unicode(toSend));

        for (int i = 0; i < photoList.size(); i++) {
            if (i == 0) {
                params.addBodyParameter("dyPhotoOne", photoList.get(0));
            } else if (i == 1) {
                params.addBodyParameter("dyPhotoTwo", photoList.get(1));
            } else if (i == 2) {
                params.addBodyParameter("dyPhotoThree", photoList.get(2));
            }
        }

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.PostDynamicUrl(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog_progress.dismiss();
                LogUtil.d(responseInfo.result.toString());
                JSONObject object = null;
                try {
                    object = new JSONObject(responseInfo.result.toString());
                    String status = object.getString("status");
                    String promptInfor = object.getString("promptInfor");
                    if ("1".equals(status)) {
                        finish();
                    } else {

                    }

                    ToastUtil.showShort(PublishTopicActivity.this, promptInfor);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                dialog_progress.dismiss();
                ToastUtil.showShort(PublishTopicActivity.this, "请检查网络是否正常连接");
            }
        });

    }


    //清楚数据
    private void clearData() {
        path = null;
        if (picPathList != null) {
            if (picPathList.size() > 0) {
                picPathList.clear();
            }
            picPathList = null;
        }
        count = 0;
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


}
