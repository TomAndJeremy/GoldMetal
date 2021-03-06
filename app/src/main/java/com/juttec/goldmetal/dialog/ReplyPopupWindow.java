package com.juttec.goldmetal.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.adapter.EmoticonsGridAdapter;
import com.juttec.goldmetal.adapter.EmoticonsPagerAdapter;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.utils.EmojiUtil;
import com.juttec.goldmetal.utils.LogUtil;
import com.juttec.goldmetal.utils.ToastUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * Created by Administrator on 2015/10/11.
 * 用于评论 和 回复 显示的 popupwindow
 */
public class ReplyPopupWindow implements EmoticonsGridAdapter.KeyClickListener {

    private Context mContext;
    //评论或回复的输入框
    private EditText mEditText;
    //表情按钮
    private ImageButton mImageButton;
    //发送按钮
    private Button mSend;

    private PopupWindow popupWindow;

    View contentview;

    LinearLayout popupLayout;


    private Bitmap[] emoticons;


    private int keyboardHeight;

    String ss;//临时记录文本框中的文字


    private Map<Integer, Integer> map = new TreeMap<>();
    //private List<Integer> list = new ArrayList<>();//顺序存储图片编号

    public ReplyPopupWindow(Context context) {

        mContext = context;

    }


    public ReplyPopupWindow create() {
        contentview = LayoutInflater.from(mContext).inflate(R.layout.commonality_comments, null);


        popupLayout = (LinearLayout) contentview.findViewById(R.id.emotion_popup);
        popupLayout.setVisibility(View.GONE);


        final float popUpheight = mContext.getResources().getDimension(
                R.dimen.keyboard_height);
        changeKeyboardHeight((int) popUpheight);


        contentview.setFocusable(true); // 这个很重要
        contentview.setFocusableInTouchMode(true);
        popupWindow = new PopupWindow(contentview, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        contentview.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
                    popupWindow.dismiss();

                    return true;
                }
                return false;
            }
        });


        mEditText = (EditText) contentview.findViewById(R.id.comment_et_reply);
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();


        //键盘弹出
        InputMethodManager imm = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
        InputMethodManager inputMethodManager =
                (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(mEditText.getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);


        //设置监听事件 隐藏表情键盘
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupLayout.setVisibility(View.GONE);
                return false;
            }
        });
        mImageButton = (ImageButton) contentview.findViewById(R.id.comment_ib_emoji);
        readEmojiIcons();//读取表情
        enablePopUpView(popupLayout);


        //表情按钮点击事件
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!popupLayout.isShown()) {
                    popupLayout.setVisibility(View.VISIBLE);
                    InputMethodManager inputMethodManager = (InputMethodManager) mContext.getApplicationContext().
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0); //隐藏
                } else {
                    popupLayout.setVisibility(View.GONE);
                }
            }
        });

        //发送
        mSend = (Button) contentview.findViewById(R.id.btn_send);

        //发送按钮的点击事件
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSend.setClickable(false);

                String content = mEditText.getText().toString();
                for (Map.Entry entry : map.entrySet()) {

                    Integer key = (Integer) entry.getKey();
                    Integer value = (Integer) entry.getValue();

                    content = content.replaceFirst("￼", EmojiUtil.getEmojiText(value));//图片在字符串中会变为￼，每次都把第一个￼字符替换掉

                }

               /* for (int i = 0; i < list.size(); i++) {
                    content = content.replaceFirst("￼", EmojiUtil.getEmojiText(list.get(i)));//图片在字符串中会变为￼，每次都把第一个￼字符替换掉

                }
                list.clear();//发送完成清除数据，防止影响下一次*/
                if (TextUtils.isEmpty(content) || "".equals(content) || content.trim().length() <= 0) {
                    mSend.setClickable(true);
                    ToastUtil.showShort(mContext, "内容不能为空");
                    return;
                }
                mOnClickSendListener.onClickSend(content,map);
            }
        });


        //edittext监听内容变化   更改发送按钮的背景
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() < 1) {
                    return;
                }

                if (String.valueOf(s.charAt(s.length() - 1)).equals("￼") && after == 0) {//如果删除掉一个表情之后就从集合中去掉
                    map.remove(start);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = mEditText.getText().toString();
                if (TextUtils.isEmpty(content) || "".equals(content) || content.trim().length() <= 0) {
                    mSend.setSelected(false);
                } else {
                    mSend.setSelected(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });


        return this;
    }


    //popupwindowde 的显示
    public void show(View view) {
        popupWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //popupwindowde 的消失
    public void dismiss() {
        mSend.setClickable(true);
        popupWindow.dismiss();
    }


    //设置edittext中 提示 type:0 表示评论   1：回复
    public void setHint(int type, String name) {

        if (type == 0) {
            mEditText.setHint("评论" + name);
        } else {
            mEditText.setHint("回复" + name);
        }
    }


    //表情键盘事件
    @Override
    public void keyClickedIndex(final String index) {
        Html.ImageGetter imageGetter = new Html.ImageGetter() {
            public Drawable getDrawable(String source) {
                StringTokenizer st = new StringTokenizer(index, ".");
                Drawable d = new BitmapDrawable(mContext.getResources(), emoticons[Integer.parseInt(st.nextToken()) - 1]);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                return d;
            }
        };


        Spanned cs = Html.fromHtml("<img src ='" + index + "'/>", imageGetter, null);


        int cursorPosition = mEditText.getSelectionStart();
        mEditText.getText().insert(cursorPosition, cs);
        String position = index.replace(".png", "");//将X.png转变为X
         map.put(cursorPosition, Integer.parseInt(position));
        //list.add(Integer.parseInt(position));
    }


    //接口  作用：发送按钮的点击事件
    public interface OnClickSendListener {
        void onClickSend(String content, Map<Integer, Integer> map);
    }


    private OnClickSendListener mOnClickSendListener;

    public void setOnClickSendListener(OnClickSendListener listener) {
        mOnClickSendListener = listener;
    }

    private void enablePopUpView(View view) {
        ViewPager pager = (ViewPager) view.findViewById(R.id.emoticons_pager);
        pager.setOffscreenPageLimit(3);
        ArrayList<String> paths = new ArrayList<String>();

        for (short i = 1; i <= MyApplication.ENUM; i++) {
            paths.add(i + ".png");
        }
        EmoticonsPagerAdapter adapter = new EmoticonsPagerAdapter((Activity) mContext, paths, this);
        pager.setAdapter(adapter);

        TextView backSpace;//删除按钮
        backSpace = (TextView) view.findViewById(R.id.back);
        backSpace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mEditText.dispatchKeyEvent(event);
            }
        });
    }


    //表情键盘高度
    private void changeKeyboardHeight(int height) {
        if (height > 100) {
            keyboardHeight = height;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, keyboardHeight);
            popupLayout.setLayoutParams(params);
        }
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
        AssetManager mngr = mContext.getAssets();
        InputStream in = null;
        try {
            in = mngr.open("emoticons/" + path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap temp = BitmapFactory.decodeStream(in, null, null);
        return temp;
    }

}
