package com.example.ytt.crudtophp.DataAccessor;

/**
 * Created by ytt on 2017/5/15.
 */

public class UserAccessor {
    private Integer uid=new Integer(0);
    private String uName=null;
    private String uEmail=null;

    public UserAccessor(int id,String name,String email){
        uid=id;
        uName=name;
        uEmail=email;
    }

    public Integer getUid() {
        return uid;
    }

    public String getuName() {
        return uName;
    }

    public String getuEmail() {
        return uEmail;
    }
}
