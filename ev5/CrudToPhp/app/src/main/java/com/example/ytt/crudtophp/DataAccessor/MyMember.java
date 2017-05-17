package com.example.ytt.crudtophp.DataAccessor;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by ytt on 2017/5/16.
 */

public class MyMember {
    public static final int UID=0;
    public static final int NAME=1;
    public static final int EMAIL=2;
    public static final int TEL=3;
    public static final int ADDR=4;
    public static final int STARSIGN=5;
    public static final int BLOODTYPE=6;
    public static final int BMI=7;
    public static final int[] FIELDS={UID,EMAIL,NAME,TEL,ADDR,STARSIGN,BLOODTYPE,BMI};
    public static final String[] FIELDNAMES={"id","name","email","tel","addr","starsign","bloodtype","bmi"};
    private String[] info;
    private String placeHolder="Info not defined";

    private String name,email,tel,addr,stSign,btype;
    private int bmi=-1,id=-1;


    public MyMember (JSONArray data){
        int x=data.length();
        try{
            String[] xInfo=new String[x];
            for(int i=0;i<x;i++){
                xInfo[i]=data.getString(i);
            }
            info=xInfo;

        }catch(Exception e){
            Log.d("MyMember",e.getMessage());

        }



    }

    public MyMember(String[] params){
        info=params;
    }

    public MyMember(int userid,String username,String uEmail,String phone,String address,String bloodType,String startsign,int bodyMassInteger){
        id=userid;
        email=uEmail;
        name=username;
        tel=phone;
        addr=address;
        btype=bloodType;
        bmi=bodyMassInteger;
        stSign=startsign;

    }


    public MyMember(String id,String username,String uEmail){
        this.id=Integer.parseInt(id);
        name=username;
        email=uEmail;
    }

    public String getInfo(int index){
        return info[index];
    }

    public boolean containsInfo(int index){
        try{
            if(!info[index].isEmpty()||info[index]!=null){
                return true;
            }


        }catch(ArrayIndexOutOfBoundsException aoe){
                return false;
        }
        return false;
    }

    public String getInfoWithPlaceHolder(int index){
        try{
            if(info==null){return placeHolder;}
            else if(info[index]!=null&&!info[index].isEmpty()){
                return info[index];
            }
            else{return placeHolder;}
        }catch(ArrayIndexOutOfBoundsException aoe){
            return placeHolder;
        }catch(Exception ex){
            return placeHolder;
        }
        //return placeHolder;

    }

    public void showAllData(){
        Log.d("Info Length",Integer.toString(info.length));
        for(int i=0;i<FIELDS.length;i++){
            Log.d("show all:  "+FIELDNAMES[FIELDS[i]],getInfoWithPlaceHolder(FIELDS[i]));
        }



    }


    public String getUserName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getTel() {
        return tel;
    }

    public String getAddr() {
        return addr;
    }

    public String getstSign() {
        return stSign;
    }

    public String getBtype() {
        return btype;
    }

    public int getBmi() {
        return bmi;
    }

    public int getId() {
        return id;
    }

}
