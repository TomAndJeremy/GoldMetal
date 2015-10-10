package com.juttec.goldmetal.customview.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.juttec.goldmetal.R;
import com.juttec.goldmetal.customview.CircleImageView;

public class PersonHeader {


	private View mHeader;

	private CircleImageView headPhoto;
	private Button btn_focus_or;



	private Context mContext;




	public PersonHeader(Context context) {
		mContext = context;
		mHeader = LayoutInflater.from(context).inflate(R.layout.header_persion, null);

		headPhoto = (CircleImageView) mHeader.findViewById(R.id.iv_head_photo);
		btn_focus_or = (Button) mHeader.findViewById(R.id.btn_focus_or);

	}


	//设置关注或取消关注的 按钮状态
	public void setFocusOr(boolean isFocus){
		if(isFocus){
			//关注了  则显示取消关注
			btn_focus_or.setSelected(false);
			btn_focus_or.setText("取消关注");


		}else{
			//未关注  则显示关注
			btn_focus_or.setSelected(true);
			btn_focus_or.setText("+关注");
		}
	}


	//设置头像
	public CircleImageView getPhotoView(){
		return headPhoto;
	}


	//关注与取消关注按钮
	public View getFocusView(){
		return btn_focus_or;
	}


	//当用户进入自己主页时  隐藏关注按钮
	public void hideFocusButton(){
		btn_focus_or.setVisibility(View.GONE);
	}





	public View getView() {
		return mHeader;
	}


}
