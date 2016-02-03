package com.juttec.goldmetal.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.activity.AccountActivity;
import com.juttec.goldmetal.activity.ImagePagerActivity;
import com.juttec.goldmetal.activity.LoginActivity;
import com.juttec.goldmetal.activity.MomentPersonalActivity;
import com.juttec.goldmetal.application.MyApplication;
import com.juttec.goldmetal.bean.DyCommentReplyBean;
import com.juttec.goldmetal.bean.DyReplyInfoBean;
import com.juttec.goldmetal.bean.DySupportInfoBean;
import com.juttec.goldmetal.bean.DynamicEntityList;
import com.juttec.goldmetal.bean.PhotoBean;
import com.juttec.goldmetal.customview.CircleImageView;
import com.juttec.goldmetal.customview.NoScrollGridView;
import com.juttec.goldmetal.dialog.MyAlertDialog;
import com.juttec.goldmetal.dialog.ReplyPopupWindow;
import com.juttec.goldmetal.utils.EmojiUtil;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeremy on 2015/9/14.
 */
public class MomentRecyclerViewAdapter extends RecyclerView.Adapter<MomentRecyclerViewAdapter.ViewHolder> {


    private ReplyPopupWindow replyPopupWindow;

    private final static int LOGIN = 2222;//登陆后刷新

    // 数据集
    ArrayList<DynamicEntityList> entityList;
    Context context;
    MyApplication app;
    RecycleViewWithHeadAdapter recycleViewWithHeadAdapter;

    private MyAlertDialog mDialog;//对话框

    private EmojiUtil readEmoji;

    private Fragment mFragment;

    protected ImageLoader imageLoader;//图片加载工具
    private DisplayImageOptions options;//

    //初始化
    public MomentRecyclerViewAdapter(ArrayList<DynamicEntityList> entityList, Context context, MyApplication app, Fragment mFragment) {
        super();
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .showImageOnLoading(R.mipmap.content_moment_pho_others)          // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.content_moment_pho_others)  // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.content_moment_pho_others)       // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();//构建完成

        this.entityList = entityList;

        this.mFragment = mFragment;
        this.context = context;
        this.app = app;
        replyPopupWindow = new ReplyPopupWindow(context);
        mDialog = new MyAlertDialog(context);
        //表情显示的工具
        readEmoji = new EmojiUtil(context);
        readEmoji.readEmojiIcons();

    }

    public void setHeadAdapter(RecycleViewWithHeadAdapter recycleViewWithHeadAdapter) {
        this.recycleViewWithHeadAdapter = recycleViewWithHeadAdapter;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.dynamic_item, parent, false);
        // 创建一个ViewHolder
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        //点击评论按钮
        holder.replyIMB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查个人信息是否完善
                if (!checkNameAndPhoto()) {
                    return;
                }
                replyPopupWindow.create().show(v);
                replyPopupWindow.setHint(0, entityList.get(position).getUserName());
                replyPopupWindow.setOnClickSendListener(new ReplyPopupWindow.OnClickSendListener() {
                    @Override
                    public void onClickSend(String content, Map<Integer, Integer> map) {

                        comment(position, replyPopupWindow, entityList.get(position).getId(), content,map);

                    }
                });
            }
        });


        holder.name.setText(entityList.get(position).getUserName());//设置用户名
        holder.time.setText(entityList.get(position).getAddTime());//时间
        if (entityList.get(position).getDyContent() != null) {
            holder.content.setText(readEmoji.getEditable((entityList.get(position).getDyContent())));// 正文
        }


        imageLoader.displayImage(MyApplication.ImgBASEURL + entityList.get(position).getUserPhoto(), holder.headPortrait, options);

        //图片的集合
        final ArrayList<PhotoBean> photoBeanList = entityList.get(position).getDyPhoto();
        holder.gridView.setAdapter(new NoScrollGridAdapter(context, photoBeanList));
        // 点击回帖九宫格，查看大图
        holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                ArrayList<String> imageUrls = new ArrayList<String>();
                for (int i = 0; i < photoBeanList.size(); i++) {
                    imageUrls.add(photoBeanList.get(i).getDyPhoto());
                }
                imageBrower(position, imageUrls);
            }
        });

        //清除上次的动态添加的内容内容
        holder.clean();


        addCommentView(holder.comment, position);//添加评论

        addSupportView(holder.supportName, getsuportGuy(position, holder));

        //点赞的点击事件
        holder.thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.thumb.setClickable(false);


                //检查个人信息是否完善
                if (!checkNameAndPhoto()) {
                    holder.thumb.setClickable(true);
                    return;
                }
                RequestParams params = new RequestParams();
                params.addBodyParameter("dyId", entityList.get(position).getId());
                params.addBodyParameter("userId", app.getUserInfoBean().getUserId());
                params.addBodyParameter("userName", app.getUserInfoBean().getUserNickName());
                params.addBodyParameter("status", holder.thumb.isSelected() ? "1" : "0");//如果已点赞 则取消赞   如果没点赞 则点赞


                if (holder.thumb.isSelected()) {//如果已点赞

                    params.addBodyParameter("status", "1");//取消赞
                    new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getAddOrCancelSupportUrl(), params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {

                            try {
                                JSONObject object = new JSONObject(responseInfo.result.toString());
                                if ("1".equals(object.getString("status"))) {
                                    for (int i = 0; i < entityList.get(position).getDySupport().size(); i++) {

                                        if (entityList.get(position).getDySupport().get(i).getUserId().equals(app.getUserInfoBean().getUserId())) {
                                            entityList.get(position).getDySupport().remove(i);
                                            break;
                                        }

                                    }

                                    recycleViewWithHeadAdapter.notifyDataSetChanged();
                                    holder.thumb.setSelected(false);


                                } else if ("0".equals(object.getString("status"))) {
                                    ToastUtil.showShort(context, object.getString("promptInfor"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                holder.thumb.setClickable(true);
                            }

                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            NetWorkUtils.showMsg(context);
                            holder.thumb.setClickable(true);
                        }
                    });
                } else {
                    new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getAddOrCancelSupportUrl(), params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {

                            try {
                                JSONObject object = new JSONObject(responseInfo.result.toString());
                                if ("1".equals(object.getString("status"))) {


                                    DySupportInfoBean dySupportInfoBean = new DySupportInfoBean();
                                    dySupportInfoBean.setId(object.getString("message1"));
                                    dySupportInfoBean.setUserId(app.getUserInfoBean().getUserId());
                                    dySupportInfoBean.setUserName(app.getUserInfoBean().getUserNickName());
                                    entityList.get(position).getDySupport().add(0, dySupportInfoBean);


                                    recycleViewWithHeadAdapter.notifyDataSetChanged();
                                    holder.thumb.setSelected(true);
                                } else if ("0".equals(object.getString("status"))) {
                                    ToastUtil.showShort(context, object.getString("promptInfor"));


                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                holder.thumb.setClickable(true);
                            }

                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            NetWorkUtils.showMsg(context);
                            holder.thumb.setClickable(true);
                        }
                    });

                }
            }


        });


        //点击用户头像，用户名
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查个人信息是否完善
                if (!checkNameAndPhoto()) {
                    return;
                }
                Intent intent = new Intent(context, MomentPersonalActivity.class);
                intent.putExtra("userId", entityList.get(position).getUserId());
                intent.putExtra("userName", entityList.get(position).getUserName());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return entityList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView name;//用户名
        TextView time;//时间
        CircleImageView headPortrait;//头像
        TextView content;//内容

        ImageButton thumb;//点赞按钮
        ImageButton replyIMB;//回复按钮

        LinearLayout support;//点赞的父布局
        LinearLayout supportName;
        LinearLayout comment;//回复父布局

        RelativeLayout relativeLayout;//用户名，头像，点击跳转到该用户主页


        //展示图片的 GridView
        NoScrollGridView gridView;

        public ViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.dynamic_item_user_name);
            time = (TextView) view.findViewById(R.id.dynamic_item_user_time);
            headPortrait = (CircleImageView) view.findViewById(R.id.dynamic_item_head_portrait);
            content = (TextView) view.findViewById(R.id.dynamic_item_content);
            thumb = (ImageButton) view.findViewById(R.id.dynamic_item_thumb);
            replyIMB = (ImageButton) view.findViewById(R.id.dynamic_item_reply);

            gridView = (NoScrollGridView) view.findViewById(R.id.gridview);

            support = (LinearLayout) view.findViewById(R.id.item_support);

            supportName = (LinearLayout) view.findViewById(R.id.item_support_name);
            comment = (LinearLayout) view.findViewById(R.id.item_comment_content);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.meg_detail_info);


        }

        private void clean() {

            if (supportName.getChildCount() > 0)
                supportName.removeAllViews();
            if (comment.getChildCount() > 0)
                comment.removeAllViews();

            thumb.setSelected(false);

        }
    }


    //得到点赞的人
    private ArrayList<Map<String, String>> getsuportGuy(int position, ViewHolder viewHolder) {
        ArrayList<Map<String, String>> surposes = new ArrayList<Map<String, String>>();

        int n = entityList.get(position).getDySupport().size();
        for (int i = 0; i < n; i++) {
            Map<String, String> map = new HashMap<String, String>();
            String name = entityList.get(position).getDySupport().get(i).getUserName();
            //判断返回的点赞名单中是否有自己
            if (app.isLogin() && name.equals(app.getUserInfoBean().getUserNickName())) {
                viewHolder.thumb.setSelected(true);
            }
            map.put("name", name);
            map.put("id", entityList.get(position).getDySupport().get(i).getUserId());
            surposes.add(map);
        }


        //有人点赞就显示，没有人点赞就不显示
        if (n > 0) {
            viewHolder.support.setVisibility(View.VISIBLE);
        } else {
            viewHolder.support.setVisibility(View.GONE);
        }
        return surposes;//返回点赞的名单
    }

    //添加点赞的人名
    private void addSupportView(LinearLayout viewRoot, ArrayList<Map<String, String>> s) {

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < s.size(); i++) {
            if (i > 10) {

                return;
            }
            TextView tv = new TextView(context);
            tv.setTextColor(Color.rgb(48, 52, 136));
            tv.setLayoutParams(lp);

            String name = s.get(i).get("name");
            String id = s.get(i).get("id");
            if (i == s.size() - 1)
                tv.setText(name);//最后一个不加“、”号
            else
                tv.setText(s.get(i).get("name") + "、");
            viewRoot.addView(tv);
            viewRoot.setVisibility(View.VISIBLE);
            clickName(tv, id, name);

        }

    }




    //添加评论布局
    private void addCommentView(LinearLayout viewRoot, final int position) {
        for (int i = 0; i < entityList.get(position).getDyCommentReply().size(); i++) {
            //获得评论人的姓名与评论内容并设置显示
            final String commentName = entityList.get(position).getDyCommentReply().get(i).getDiscussantName() /*+ ":"*/;//
            String commentContent = ": "+(entityList.get(position).getDyCommentReply().get(i).getCommentContent());
            final int finalI = i;
          /*  TextView comment = (TextView) LayoutInflater.from(context).inflate(R.layout.item_comment_msg, null);
            SpannableString string = new SpannableString(commentName);
            final String commentUserId = entityList.get(position).getDyCommentReply().get(i).getDiscussantId();
            setPartClick(string, commentUserId, commentName, 0, commentName.length(), comment);
            comment.append(string);
            comment.setMovementMethod(LinkMovementMethod.getInstance());
            comment.append(readEmoji.getEditable(commentContent));




            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //检查个人信息是否完善
                    if (!checkNameAndPhoto()) {
                        return;
                    }
                    if (!entityList.get(position).getDyCommentReply().get(finalI).getDiscussantId().equals(app.getUserInfoBean().getUserId())) {
                        replyPopupWindow.create().show(v);
                        replyPopupWindow.setHint(1, entityList.get(position).getDyCommentReply().get(finalI).getDiscussantName());
                        replyPopupWindow.setOnClickSendListener(new ReplyPopupWindow.OnClickSendListener() {
                            @Override
                            public void onClickSend(String content) {
                                reply(position, finalI, content);
                            }
                        });
                    }
                }
            });

            //将一条评论添加到父布局中
            viewRoot.addView(comment);
*/
            String commentUserId = entityList.get(position).getDyCommentReply().get(i).getDiscussantId();
            View commentView = LayoutInflater.from(context).inflate(R.layout.item_comment_msg, null);
            TextView tvCommentName = (TextView) commentView.findViewById(R.id.comment_name);
            final TextView tvCommentContent = (TextView) commentView.findViewById(R.id.comment_content);

            tvCommentName.setText(commentName);
            clickName(tvCommentName, commentUserId, commentName);
            //填补空格
            String blank = MyApplication.getBlank(commentName , tvCommentName.getTextSize());

           // tvCommentContent.append(blank);



            Paint paint = tvCommentContent.getPaint();//用于测量字符串长度
            /*float sWidth = paint.measureText(blank + commentContent);
            float tvWidth = context.getResources().getDisplayMetrics().widthPixels - 2 * 2 * context.getResources().getDisplayMetrics().density;
            tvCommentContent.setMaxLines((int) (sWidth / tvWidth));
           */



            //设置评论
            tvCommentContent.append(readEmoji.getEditable(blank+commentContent,paint));


            commentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //检查个人信息是否完善
                    if (!checkNameAndPhoto()) {
                        return;
                    }
                    if (!entityList.get(position).getDyCommentReply().get(finalI).getDiscussantId().equals(app.getUserInfoBean().getUserId())) {
                        replyPopupWindow.create().show(v);
                        replyPopupWindow.setHint(1, entityList.get(position).getDyCommentReply().get(finalI).getDiscussantName());
                        replyPopupWindow.setOnClickSendListener(new ReplyPopupWindow.OnClickSendListener() {
                            @Override
                            public void onClickSend(String content, Map<Integer, Integer> map) {
                                reply(position, finalI, content);
                            }
                        });
                    }
                }
            });

            //添加到父布局中
            viewRoot.addView(commentView);

            //添加评论中的回复（操作与上面基本一致）
            int size = entityList.get(position).getDyCommentReply().get(i).getDyReply().size();
            final LinearLayout replyRoot = new LinearLayout(context);
            replyRoot.setOrientation(LinearLayout.VERTICAL);
            for (int j = 0; j < size; j++) {
               /*   final String userName = entityList.get(position).getDyCommentReply().get(i).getDyReply().get(j).getUserName();
                String repliedName = entityList.get(position).getDyCommentReply().get(i).getDyReply().get(j).getRepliedName();

                final String userId = entityList.get(position).getDyCommentReply().get(i).getDyReply().get(j).getUserId();
                String repliedId = entityList.get(position).getDyCommentReply().get(i).getDyReply().get(j).getRepliedId();

                String replyContent = entityList.get(position).getDyCommentReply().get(i).getDyReply().get(j).getReplyContent();


              // TextView tvReply = (TextView) LayoutInflater.from(context).inflate(R.layout.item_comment_msg, null);
                CBAlignTextView tvReply = (CBAlignTextView) LayoutInflater.from(context).inflate(R.layout.item_comment_msg, null);


                SpannableString userReply = new SpannableString(userName);
                setPartClick(userReply, userId, userName, 0, userName.length(), tvReply);

                SpannableString userReplied = new SpannableString(repliedName + ":");
                setPartClick(userReplied, repliedId, repliedName, 0, repliedName.length(), tvReply);

                tvReply.append(userReply);
                tvReply.append("回复");
                tvReply.append(userReplied);

                tvReply.setMovementMethod(LinkMovementMethod.getInstance());
                tvReply.append(readEmoji.getEditable(replyContent));
                replyRoot.addView(tvReply);

                final int finalJ = j;
                tvReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //检查个人信息是否完善
                        if (!checkNameAndPhoto()) {
                            return;
                        }

                        if (!app.getUserInfoBean().getUserId().equals(userId)) {

                            replyPopupWindow.create().show(v);
                            replyPopupWindow.setHint(1, userName);

                            replyPopupWindow.setOnClickSendListener(new ReplyPopupWindow.OnClickSendListener() {
                                @Override
                                public void onClickSend(String content) {
                                    reply(position, finalI, finalJ, content);
                                }
                            });
                        }
                    }


                });
*/
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                View replyMsg = LayoutInflater.from(context).inflate(R.layout.item_comment_msg, null);
                replyMsg.setLayoutParams(lp);




                TextView tvReplyName = (TextView) replyMsg.findViewById(R.id.reply_name);
                TextView hint = (TextView) replyMsg.findViewById(R.id.hint_reply);
                TextView tvRepliedName = (TextView) replyMsg.findViewById(R.id.comment_name);
                TextView tvReplyContent = (TextView) replyMsg.findViewById(R.id.comment_content);
                tvReplyName.setVisibility(View.VISIBLE);
                hint.setVisibility(View.VISIBLE);

                final String userName = entityList.get(position).getDyCommentReply().get(i).getDyReply().get(j).getUserName();
                String repliedName = entityList.get(position).getDyCommentReply().get(i).getDyReply().get(j).getRepliedName();

                final String userId = entityList.get(position).getDyCommentReply().get(i).getDyReply().get(j).getUserId();
                String repliedId = entityList.get(position).getDyCommentReply().get(i).getDyReply().get(j).getRepliedId();

                String replyContent = ": "+entityList.get(position).getDyCommentReply().get(i).getDyReply().get(j).getReplyContent();


                tvReplyName.setText(userName);
                tvRepliedName.setText(repliedName);


                //得到对应长度的空格
                String toBlank = MyApplication.getBlank(tvReplyName.getText().toString(), tvReplyName.getTextSize());
                toBlank += MyApplication.getBlank(hint.getText().toString(), hint.getTextSize());
                toBlank += MyApplication.getBlank(tvRepliedName.getText().toString(), tvRepliedName.getTextSize());

               /* tvReplyContent.append(toBlank);*/
                Paint p = tvReplyContent.getPaint();
                tvReplyContent.append(readEmoji.getEditable(toBlank +replyContent ,p));


                replyRoot.addView(replyMsg);

                clickName(tvReplyName,userId, userName);
                clickName(tvRepliedName,repliedId,repliedName);


                final int finalJ = j;
                replyMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //检查个人信息是否完善
                        if (!checkNameAndPhoto()) {
                            return;
                        }

                        if (!app.getUserInfoBean().getUserId().equals(entityList.get(position).getDyCommentReply().get(finalI).getDyReply().get(finalJ).getUserId())) {

                            replyPopupWindow.create().show(v);
                            replyPopupWindow.setHint(1, entityList.get(position).getDyCommentReply().get(finalI).getDyReply().get(finalJ).getUserName());

                            replyPopupWindow.setOnClickSendListener(new ReplyPopupWindow.OnClickSendListener() {
                                @Override
                                public void onClickSend(String content, Map<Integer, Integer> map) {
                                    reply(position, finalI, finalJ, content);
                                }
                            });
                        }
                    }


                });

            }
            viewRoot.addView(replyRoot);
        }

    }



    /**
     * 显示登录的dialog
     */
    private void showLoginDialog(){
        mDialog.builder().setTitle("提示")
                .setMsg("您还没有登录，请先登录后再执行操作")
                .setSingleButton("前去登录", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        mFragment.startActivityForResult(new Intent(context, LoginActivity.class), LOGIN);
                    }
                }).show();
    }


    //判断用户是否编辑了个人的昵称和头像
    private boolean checkNameAndPhoto() {
        if (!app.isLogin()) {
            showLoginDialog();
            return false;
        }

        if ("".equals(app.getUserInfoBean().getUserNickName())) {
            //设置昵称
            mDialog.builder()
                    .setTitle("提示").setMsg("您还没有昵称，请至账号界面设置后再操作，谢谢！")
                    .setSingleButton("前去设置", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, AccountActivity.class));
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
                            mDialog.dismiss();
                        }
                    }).show();
            return false;
        }
        return true;
    }


    /**
     * 打开图片查看器
     */
    protected void imageBrower(int position, ArrayList<String> urls2) {
        Intent intent = new Intent(context, ImagePagerActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        context.startActivity(intent);
    }


    //点击姓名跳转
    private void clickName(View v, final String userID, final String userName) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(userID, userName);
            }
        });
    }

    private void startActivity(String userID, String userName) {
        Intent intent = new Intent(context, MomentPersonalActivity.class);
        intent.putExtra("userId", userID);
        intent.putExtra("userName", userName.replace(":", ""));
        context.startActivity(intent);
    }


    //发送评论
    private void comment(final int position, final ReplyPopupWindow popupWindow, String dyId, final String content, final Map<Integer, Integer> map) {

        final String discussantId = app.getUserInfoBean().getUserId();
        final String discussantName = app.getUserInfoBean().getUserNickName();
        RequestParams param = new RequestParams();
        param.addBodyParameter("dyId", dyId);
        param.addBodyParameter("discussantId", discussantId);
        param.addBodyParameter("discussantName", discussantName);


        String text = content.replace("\n", "");
        LogUtil.e("text   " + text);
        param.addBodyParameter("commentContent", text);


        new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getCommentUrl(), param, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    JSONObject object = new JSONObject(responseInfo.result.toString());

                    ToastUtil.showShort(context, object.getString("promptInfor"));
                    if ("1".equals(object.getString("status"))) {

                        map.clear();

                        DyCommentReplyBean dyCommentReplyBean = new DyCommentReplyBean(discussantId, discussantName, content, new ArrayList<DyReplyInfoBean>());


                        entityList.get(position).getDyCommentReply().add(dyCommentReplyBean);
                        notifyDataSetChanged();
                        recycleViewWithHeadAdapter.notifyDataSetChanged();

                        popupWindow.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(HttpException error, String msg) {
                NetWorkUtils.showMsg(context);
            }
        });


    }

    //回复评论
    private void reply(final int position, final int i, final String content) {

        reply(position, i, -1, content);


    }

    //回复回复
    private void reply(final int position, final int i, final int j, final String content) {
        RequestParams param = new RequestParams();

        String dyId = entityList.get(position).getId();
        final String commentId = entityList.get(position).getDyCommentReply().get(i).getId();
        final String userId = app.getUserInfoBean().getUserId();
        final String userName = app.getUserInfoBean().getUserNickName();
        final String repliedId;
        final String repliedName;
        if (j == -1) {
            repliedId = entityList.get(position).getDyCommentReply().get(i).getDiscussantId();
            repliedName = entityList.get(position).getDyCommentReply().get(i).getDiscussantName();
        } else {
            repliedId = entityList.get(position).getDyCommentReply().get(i).getDyReply().get(j).getUserId();
            repliedName = entityList.get(position).getDyCommentReply().get(i).getDyReply().get(j).getUserName();
        }


        param.addBodyParameter("dyId", dyId);
        param.addBodyParameter("commentId", commentId);
        param.addBodyParameter("userId", userId);
        param.addBodyParameter("userName", userName);
        param.addBodyParameter("repliedId", repliedId);
        param.addBodyParameter("repliedName", repliedName);
        param.addBodyParameter("replyContent", content);

        new HttpUtils().send(HttpRequest.HttpMethod.POST, app.getReplyUrl(), param, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    JSONObject object = new JSONObject(responseInfo.result.toString());

                    ToastUtil.showShort(context, object.getString("promptInfor"));

                    if ("1".equals(object.getString("status"))) {
                        DyReplyInfoBean dyReplyInfoBean = new DyReplyInfoBean(userId, userName, repliedId, repliedName, content);
                        ArrayList<DyReplyInfoBean> dyReplyInfoBeans = (ArrayList<DyReplyInfoBean>) entityList.get(position).getDyCommentReply().get(i).getDyReply();
                        dyReplyInfoBeans.add(dyReplyInfoBean);
                        entityList.get(position).getDyCommentReply().get(i).setDyReply(dyReplyInfoBeans);
                        notifyDataSetChanged();
                        recycleViewWithHeadAdapter.notifyDataSetChanged();
                        replyPopupWindow.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                NetWorkUtils.showMsg(context);
            }
        });
    }


    /**
     * 设置部分文字的点击事件与颜色
     *
     * @param string
     * @param commentUserId
     * @param commentName
     * @param start
     * @param end
     */
    private void setPartClick(SpannableString string, final String commentUserId, final String commentName, int start, int end, View view) {
        //设置点击事件
        string.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                //取消textview的点击事件  避免点击昵称时调起回复的操作
                view.setOnClickListener(null);

                //跳转到个人主页
                startActivity(commentUserId, commentName);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.rgb(59, 85, 226));    //设置颜色
                ds.setUnderlineText(false);//删除下划线
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}

