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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.adapter.EmoticonsGridAdapter.KeyClickListener;
import com.juttec.goldmetal.adapter.EmoticonsPagerAdapter;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.utils.GetContentUrl;
import com.juttec.goldmetal.utils.ImgUtil;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.SnackbarUtil;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * 发布消息  界面
 */

public class PublishTopicActivity extends AppCompatActivity implements KeyClickListener, View.OnClickListener {



    public final static int REQUEST_CODE_CAMERA = 111;
    public final static int REQUEST_CODE_ALBUM = 222;


    private static final int EMOJI_NUM = 54;//表情数目

    private View popUpView;
    private LinearLayout emojiconsCover;
    private PopupWindow popupWindow;


    private int keyboardHeight;//键盘高度



    private EditText mContent;//发表的内容

    private RelativeLayout parentLayout;

    private Button mBtnPush;//发表按钮

    private ImageView iv_photo1,iv_photo2,iv_photo3;//上传的图片

    private List<String> photoList = new ArrayList<String>();//存放上传图片后返回的路径

    private String path;//照相后图片的路径
    // 存放图片的路径 的集合
    private  Map<Integer, String> maps_photopath = new HashMap<Integer, String>();

    private int count = 0;//图片上传到了 第几张


    private boolean isKeyBoardVisible;//键盘是否显示或隐藏

    private Bitmap[] emoticons;


    private MyApplication app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_topic);
        app = (MyApplication) getApplication();
        LogUtil.d("onCreate------------");

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
        LogUtil.d("onDestroy------------");
    }



    private void initView() {
        parentLayout = (RelativeLayout) this.findViewById(R.id.rl_pta_parent);
        emojiconsCover = (LinearLayout) this.findViewById(R.id.content_for_emoticons);
        popUpView = getLayoutInflater().inflate(R.layout.emoticons_popup, null);


        mContent = (EditText) this.findViewById(R.id.publis_topic_et);
        mContent.setOnClickListener(this);


        mBtnPush = (Button) findViewById(R.id.publis_topic_bt_push);
        mBtnPush.setOnClickListener(this);


        ImageButton selectPic = (ImageButton) this.findViewById(R.id.publis_topic_bt_pic);
        selectPic.setOnClickListener(this);


        final float popUpheight = getResources().getDimension(
                R.dimen.keyboard_height);
        changeKeyboardHeight((int) popUpheight);

        ImageButton btEmoji = (ImageButton) this.findViewById(R.id.publis_topic_bt_emoji);
        btEmoji.setOnClickListener(this);

        iv_photo1 = (ImageView) findViewById(R.id.iv_photo1);

        readEmojiIcons();
        enablePopUpView();
        //checkKeyboardHeight(parentLayout);

    }


    int previousHeightDiffrence = 0;

    private void checkKeyboardHeight(final LinearLayout parentLayout) {
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
                        if (heightDifference > 100) {

                            isKeyBoardVisible = true;
                            changeKeyboardHeight(heightDifference);

                        } else {

                            isKeyBoardVisible = false;

                        }

                    }
                });


    }

    private void enablePopUpView() {
        ViewPager pager = (ViewPager) popUpView.findViewById(R.id.emoticons_pager);
        pager.setOffscreenPageLimit(3);
        ArrayList<String> paths = new ArrayList<String>();

        for (short i = 1; i <= EMOJI_NUM; i++) {
            paths.add(i + ".png");
        }
        EmoticonsPagerAdapter adapter = new EmoticonsPagerAdapter(this, paths, this);
        pager.setAdapter(adapter);


        // Creating a pop window for emoticons keyboard
        popupWindow = new PopupWindow(popUpView, LinearLayout.LayoutParams.MATCH_PARENT,
                keyboardHeight, false);

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
        emoticons = new Bitmap[EMOJI_NUM];
        for (short i = 1; i <= EMOJI_NUM; i++) {
            emoticons[i-1] = getImage((i) + ".png");
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
                Drawable d = new BitmapDrawable(getResources(), emoticons[Integer.parseInt(st.nextToken())-1]);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                return d;
            }
        };

        Spanned cs = Html.fromHtml("<img src ='" + index + "'/>", imageGetter, null);

        int cursorPosition = mContent.getSelectionStart();
        mContent.getText().insert(cursorPosition, cs);


    }

    /**
     * Overriding onKeyDown for dismissing keyboard on key down
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
                popupWindow.dismiss();
                break;


            case R.id.publis_topic_bt_push:
                //发表话题
                String content = mContent.getText().toString();
                if(TextUtils.isEmpty(content)||"".equals(content)){
                    ToastUtil.showShort(PublishTopicActivity.this,"发表的内容不能为空");
                    return;
                }

                LogUtil.d("发表的内容为："+mContent.getText().toString());


                for(int j=0;j<maps_photopath.size();j++){

                    upLoadPhoto(maps_photopath.get(j+1));
                }



                break;


            case R.id.publis_topic_bt_pic:

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
                if (!popupWindow.isShowing()) {

                    popupWindow.setHeight(keyboardHeight);

                    if (isKeyBoardVisible) {
                        emojiconsCover.setVisibility(LinearLayout.GONE);
                    } else {
                        emojiconsCover.setVisibility(LinearLayout.VISIBLE);
                    }
                    popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);

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
        File file = new File(getAlbumStorageDir(getApplicationContext(), "Picture"), getPhotoFileName());
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, request);

    }


    // 相机拍摄照片时:使用系统当前日期加以调整作为照片的名称
    public String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    public File getAlbumStorageDir(Context context, String albumName) {
// Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                LogUtil.d("Directory not created");
            }
        }

        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
        // 当选择拍照时调用
            case REQUEST_CODE_CAMERA:

                if (resultCode == RESULT_OK) {


                } else {
                    SnackbarUtil.showLong(this, "亲，拍照失败");
                }
                break;

            // 从手机相册返回
            case REQUEST_CODE_ALBUM:
                Uri selectedImage = GetContentUrl.geturi(data, getApplicationContext());
                String[] filePathColumns = { MediaStore.Images.Media.DATA };
                Cursor c = this.getContentResolver().query(selectedImage,
                        filePathColumns, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePathColumns[0]);
                String picturePath= c.getString(columnIndex);
                maps_photopath.put(1,picturePath);
                LogUtil.d("选取相册中图片的路径：" + picturePath);
                c.close();
                setImg();
                break;
        }

    }


    //设置图片
    private void setImg() {
        Bitmap bitmap = null;// 根据路径 获取图片
        if(maps_photopath.size()>0){
            Set<Integer> mapSet =  maps_photopath.keySet();	//获取所有的key值 为set的集合
            Iterator<Integer> itor =  mapSet.iterator();//获取key的Iterator便利
            while(itor.hasNext()){//存在下一个值
                int key = itor.next();//当前key值
                if(key==1){
                    bitmap =ImgUtil.getBitmap( maps_photopath.get(1), 200, 200);
                    iv_photo1.setImageBitmap(bitmap);

                }else if(key==2){

                }
            }
        }
    }




    //上传图片接口
    private void upLoadPhoto(String path){

        RequestParams params = new RequestParams();
        params.addBodyParameter("dyPhotoFile",getBitmapString(path));
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
                        if(count==maps_photopath.size()){
                            //调用发表动态接口
                            postDynamic();
                        }

                    } else {
                        photoList.clear();
                        ToastUtil.showShort(PublishTopicActivity.this, "上传图片失败");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                ToastUtil.showShort(PublishTopicActivity.this, "请检查网络是否正常连接");
            }
        });

    }



    //发表动态接口
    private void postDynamic(){

        RequestParams params = new RequestParams();
        params.addBodyParameter("userId",app.getUserInfoBean().getUserId());
        params.addBodyParameter("userName", app.getUserInfoBean().getUserNickName());
        params.addBodyParameter("dyContent", mContent.getText().toString());
        for(int i=0;i<photoList.size();i++){
            if(i==0) {
                params.addBodyParameter("dyPhotoOne", photoList.get(0));
            }else if(i==1){
                params.addBodyParameter("dyPhotoTwo", photoList.get(1));
            }else if(i==2){
                params.addBodyParameter("dyPhotoThree", photoList.get(2));
            }
        }

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, app.PostDynamicUrl(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
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
                ToastUtil.showShort(PublishTopicActivity.this, "请检查网络是否正常连接");
            }
        });

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
