package com.juttec.goldmetal.bean;

import java.util.ArrayList;

/**
 * Created by Jeremy on 2015/9/24.
 *
 */
public class DynamicMsgBean {
    private String status;
    private String promptInfor;
    private ArrayList<DynamicEntityList> entityList;
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



    @Override
    public String toString() {
        return "DynamicMsgBean{" +
                "status='" + status + '\'' +
                ", promptInfor='" + promptInfor + '\'' +
                ", entityList=" + entityList +
                '}';
    }
}
