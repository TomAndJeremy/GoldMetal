package com.juttec.goldmetal.bean;

/**
 * Created by Administrator on 2015/10/9.
 * 图片实体类
 */
public class PhotoBean {


    private String dyPhoto;//图片路径

    private boolean isDelete;//是否显示删除 图标   true:显示  false：隐藏

    public void setDyPhoto(String dyPhoto) {
        this.dyPhoto = dyPhoto;
    }

    public String getDyPhoto() {

        return dyPhoto;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }
}
