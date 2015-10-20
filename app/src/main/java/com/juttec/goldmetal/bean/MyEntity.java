package com.juttec.goldmetal.bean;

/**
 * Created by jeremy on 2015/10/20.
 */
public class MyEntity {
    private Object object ;

    public MyEntity() {
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public MyEntity(Object object) {

        this.object = object;
    }
}
