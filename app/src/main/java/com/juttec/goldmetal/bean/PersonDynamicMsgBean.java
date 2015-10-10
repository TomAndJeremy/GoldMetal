package com.juttec.goldmetal.bean;

import java.util.ArrayList;

/**
 * Created by Jeremy on 2015/9/24.
 *
 */
public class PersonDynamicMsgBean {
    private String status;
    private String promptInfor;
    private ArrayList<DynamicEntityList> entityList;

    private String message1;//动态页数
    private String message2;//是否被关注  0：关注 1：未关注，仅dyType为personal时有此字段

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPromptInfor(String promptInfor) {
        this.promptInfor = promptInfor;
    }

    public void setEntityList(ArrayList<DynamicEntityList> entityList) {
        this.entityList = entityList;
    }

    public ArrayList<DynamicEntityList> getEntityList() {

        return entityList;
    }

    public String getStatus() {
        return status;
    }

    public String getPromptInfor() {
        return promptInfor;
    }

    public String getMessage1() {
        return message1;
    }

    public void setMessage1(String message1) {
        this.message1 = message1;
    }

    public String getMessage2() {
        return message2;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }

    @Override
    public String toString() {
        return "DynamicMsgBean{" +
                "status='" + status + '\'' +
                ", promptInfor='" + promptInfor + '\'' +
                ", entityList=" + entityList +
                '}';
    }
}
