package com.juttec.goldmetal.activity;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
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
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
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
import com.juttec.goldmetal.utils.GetContentUrl;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.SnackbarUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class PublishTopicActivity extends AppCompatActivity implements
        KeyClickListener, View.OnClickListener {


    public final static int REQUEST_CODE_CAMERA = 111;
    public final static int REQUEST_CODE_ALBUM = 222;


    private static final int EMOJI_NUM = 54;
    private View popUpView;
    private LinearLayout emojiconsCover;
    private PopupWindow popupWindow;

    private int keyboardHeight;
    private EditText content;

    private RelativeLayout parentLayout;

    private boolean isKeyBoardVisible;

    private Bitmap[] emoticons;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_topic);
        initView();
    }

    private void initView() {
        parentLayout = (RelativeLayout) this.findViewById(R.id.rl_pta_parent);
        emojiconsCover = (LinearLayout) this.findViewById(R.id.content_for_emoticons);
        popUpView = getLayoutInflater().inflate(R.layout.emoticons_popup, null);


        content = (EditText) this.findViewById(R.id.publis_topic_et);
        content.setOnClickListener(this);


        final Button push = (Button) findViewById(R.id.publis_topic_bt_push);
        push.setOnClickListener(this);


        ImageButton selectPic = (ImageButton) this.findViewById(R.id.publis_topic_bt_pic);
        selectPic.setOnClickListener(this);


        final float popUpheight = getResources().getDimension(
                R.dimen.keyboard_height);
        changeKeyboardHeight((int) popUpheight);

        ImageButton btEmoji = (ImageButton) this.findViewById(R.id.publis_topic_bt_emoji);
        btEmoji.setOnClickListener(this);

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
                content.dispatchKeyEvent(event);
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
        for (short i = 0; i < EMOJI_NUM; i++) {
            emoticons[i] = getImage((i) + ".png");
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

        int cursorPosition = content.getSelectionStart();
        content.getText().insert(cursorPosition, cs);


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

                c.close();
                break;

        }

    }

}
