package com.juttec.goldmetal.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.adapter.EmoticonsGridAdapter;
import com.juttec.goldmetal.adapter.EmoticonsPagerAdapter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by jeremy on 2015/10/22.
 */
public class EmojiWindow implements EmoticonsGridAdapter.KeyClickListener {

    private static final int EMOJI_NUM = 54;

    private View popUpView;

    private LinearLayout emoticonsCover;
    private PopupWindow popupWindow;

    private int keyboardHeight;
    private EditText content;

    private View parentLayout;

    private boolean isKeyBoardVisible;//键盘是否显示或隐藏

    private Bitmap[] emoticons;




    Context context;

    public EmojiWindow(Context context) {

        this.context = context;
    }



    public EmojiWindow enablePopUpView(final EditText edit, View parent, LinearLayout cover) {


        parentLayout = parent;
        emoticonsCover = cover;
        content = edit;


        final float popUpheight = context.getResources().getDimension(
                R.dimen.keyboard_height);
        changeKeyboardHeight((int) popUpheight);



        popUpView = ((Activity) context).getLayoutInflater().inflate(R.layout.emoticons_popup, null);
        ViewPager pager = (ViewPager) popUpView.findViewById(R.id.emoticons_pager);
        pager.setOffscreenPageLimit(3);
        ArrayList<String> paths = new ArrayList<String>();

        for (short i = 1; i <= EMOJI_NUM; i++) {
            paths.add(i + ".png");
        }
        EmoticonsPagerAdapter adapter = new EmoticonsPagerAdapter((FragmentActivity) context, paths, this);
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
                content.dispatchKeyEvent(event);
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                emoticonsCover.setVisibility(LinearLayout.GONE);
            }
        });

        return this;
    }
    public void showOrHide(View parent) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getApplicationContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(content.getWindowToken(), 0); //隐藏
        LogUtil.e("1232343433333333333333");
        if (!popupWindow.isShowing()) {

            popupWindow.setHeight(keyboardHeight);

            if (isKeyBoardVisible) {
                emoticonsCover.setVisibility(View.GONE);
            } else {
                emoticonsCover.setVisibility(View.VISIBLE);
            }


            popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

        } else {
            popupWindow.dismiss();
        }
    }

    public  Bitmap[] readEmojiIcons( ) {
        emoticons = new Bitmap[EMOJI_NUM];
        for (short i = 0; i < EMOJI_NUM; i++) {
            emoticons[i] = getImage((i + 1) + ".png");
        }
        return emoticons;
    }

    /**
     * For loading smileys from assets
     */
    private Bitmap getImage(String path) {
        AssetManager mngr = context.getAssets();
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
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, keyboardHeight);
            emoticonsCover.setLayoutParams(params);
        }
    }

    @Override
    public void keyClickedIndex(final String index) {

        Html.ImageGetter imageGetter = new Html.ImageGetter() {
            public Drawable getDrawable(String source) {
                StringTokenizer st = new StringTokenizer(index, ".");
                Drawable d = new BitmapDrawable(context.getResources(), emoticons[Integer.parseInt(st.nextToken()) - 1]);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                return d;
            }
        };


        Spanned cs = Html.fromHtml("<img src ='" + index + "'/>", imageGetter, null);


        int cursorPosition = content.getSelectionStart();
        content.getText().insert(cursorPosition, cs);


    }

    public Editable getEditable(String contentUnicode) {
        String content = unicode2String(contentUnicode);

        Editable editable = new Editable.Factory().newEditable("");
        final String[] s = content.split("`");
        for (int i = 0; i < s.length; i++) {


            StringTokenizer st = new StringTokenizer(s[i], ".");


            int t = 0;
            try {
                t = Integer.parseInt(st.nextToken()) - 1;

                if (t < EMOJI_NUM) {
                    final int finalI = i;
                    Spanned cs = Html.fromHtml("<img src ='" + s[finalI] + "'/>", getImageGetter(t), null);
                    editable.append(cs);
                    i++;

                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (i < s.length) {
                editable.append(s[i]);
            }


        }
        return editable;
    }
    private Html.ImageGetter getImageGetter(final int t) {
        return new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {

                Drawable d = new BitmapDrawable(context.getResources(), emoticons[t]);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                return d;
            }
        };
    }
    /**
     * unicode 转字符串
     */
    private String unicode2String(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }


}
