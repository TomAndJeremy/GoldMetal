package com.juttec.goldmetal.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.adapter.EmoticonsGridAdapter;
import com.juttec.goldmetal.adapter.EmoticonsPagerAdapter;
import com.juttec.goldmetal.application.MyApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jeremy on 2015/10/22.
 */
public class EmojiUtil implements EmoticonsGridAdapter.KeyClickListener {


    //private View popUpView;

    private LinearLayout emoticonsCover;
    private PopupWindow popupWindow;

    private int keyboardHeight;
    private EditText content;

    private View parentLayout;

    private boolean isKeyBoardVisible;//键盘是否显示或隐藏

    private Bitmap[] emoticons;


    private static Context context;
    DisplayMetrics dm;
    private static final double STANDWIDTH = 1080;
    private static final double STANDHEIGHT = 1920;
    double imgWidthRate, imgHeightRate;

    public EmojiUtil(Context context) {

        this.context = context;

        // 方法2
        dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        imgWidthRate = dm.widthPixels / STANDWIDTH;
        imgHeightRate = dm.heightPixels / STANDHEIGHT;
    }

    public Bitmap[] readEmojiIcons() {
        emoticons = new Bitmap[MyApplication.ENUM];
        for (short i = 0; i < MyApplication.ENUM; i++) {
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


   /* private void changeKeyboardHeight(int height) {
        if (height > 100) {
            keyboardHeight = height;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, keyboardHeight);
            emoticonsCover.setLayoutParams(params);
        }
    }*/

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


    private List<String> getMatcher(String regex, String source) {
        String result = "";

        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

    public Editable getEditable(String content) {

        return getEditable(content, null);
    }

    public Editable getEditable(String content, Paint paint) {
        //String content = unicode2String(contentUnicode);
        Editable editable = new Editable.Factory().newEditable("");

        content.replace("\n", "");

/***************************************************************************/
        String regex = "\\[[\\u4e00-\\u9fa5]+\\]";//匹配“[‘汉字’]”
        List<String> emojis = getMatcher(regex, content);
        for (String s : emojis
                ) {
            content = content.replace(s, covert(s));
        }

        //之前表情的协议是`x.png`,现在的协议是[‘汉字’],此段代码将其转换为原先的协议（这样可以不用修改后面的代码）
/***************************************************************************/




        final String[] s = content.split("`");
        for (int i = 0; i < s.length; i++) {

            if (i < s.length && !s[i].contains(".png")) {//如果没有".png"就跳过后面的代码
                editable.append(s[i]);
                continue;
            }

            StringTokenizer st = new StringTokenizer(s[i], ".");


            int t = 0;
            try {
                t = Integer.parseInt(st.nextToken()) - 1;

                if (t < MyApplication.ENUM) {
                    final int finalI = i;
                    Spanned cs = Html.fromHtml("<img src ='" + s[finalI] + "'/>", getImageGetter(t), null);
//                  editable.append("\u2000");
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
        float tvWidth = 2 * 2 * context.getResources().getDisplayMetrics().density;
        int x = context.getResources().getDisplayMetrics().widthPixels;
        if (paint != null) {
            autoSplit(editable, paint, x - tvWidth);
        }

        return editable;
    }


    private Html.ImageGetter getImageGetter(final int t) {
        return new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {

                Drawable d = new BitmapDrawable(context.getResources(), emoticons[t]);

                d.setBounds(0, 0, (int) (d.getIntrinsicWidth() * imgWidthRate), (int) (d.getIntrinsicHeight() * imgHeightRate));
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


    private String covert(String s) {


        try {
            InputStream stream = context.getResources().getAssets().open("emojimap.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String s1 = null;
            int i = 0;
            while ((s1 = reader.readLine()) != null) {
                i++;
                if (s1.equals(s)) {
                    reader.close();

                    return "`" + i + ".png`";


                }
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return "";
    }

    public static String getEmojiText(int i) {
        try {
            InputStream stream = context.getResources().getAssets().open("emojimap.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String s1 = null;
            int j = 0;
            while ((s1 = reader.readLine()) != null) {
                j++;
                if (i == j) {

                    reader.close();

                    return s1;


                }
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    /**
     * 自动分割文本
     *
     * @param content 需要分割的文本
     * @param p       画笔，用来根据字体测量文本的宽度
     * @param width   最大的可显示像素（一般为控件的宽度）
     * @return 一个字符串数组，保存每行的文本
     */
    private Editable autoSplit(Editable content, Paint p, float width) {
        int length = content.length();
        float textWidth = p.measureText(content.toString());
        if (textWidth <= width) {
            return content;
        }
        int start = 0, end = 1, i = 0;
        int lines = (int) Math.ceil(textWidth / width); //计算行数
        Editable lineTexts = null;
        while (start < length) {
            if (p.measureText(content, start, end) > width) { //文本宽度超出控件宽度时

                end = end - 4;
                lineTexts = content.insert(end, "\n");
                start = end;
            }
            if (end == length) { //不足一行的文本
                break;
            }
            end += 1;
        }
        return lineTexts;
    }


}
