package com.example.ytt.crudtophp.DataAccessor;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class NetAccessor implements Runnable {
    private static final String Url="http://192.168.1.99/leo/fainet/memberSystem.php";
    private android.os.Handler hnd;
    public static final int ADD_MEMBER=1;
    public static final int CHANGE_DATA=2;
    public static final int MEMBER_EXIST=3;
    public static final int DELETE_USER=4;
    public static final int PAIR_DATA=5;
    public static final int FULL_DATA=6;
    public static final int FLEX_DATA=7;
    public static final int FLEX_JSON=8;
    private String post_request="";
    private int cmd=-1;
    private String[] params;


    public NetAccessor(Handler callbackhandler,int command,String... parameters){
        hnd=callbackhandler;
        params=parameters;
        cmd=command;
        switch(command){
            case ADD_MEMBER:setParam_AddMember();break;
            case CHANGE_DATA:setParam_ChangeData();break;
            case MEMBER_EXIST:setParam_MemberExist();break;
            case DELETE_USER:setParam_DeleteUser();break;
            case PAIR_DATA:setParam_PairData();break;
            case FULL_DATA:setParam_FullData();break;
            case FLEX_DATA:setParam_FlexData();break;
            case FLEX_JSON:setParam_FlexJSON();break;
        }



    }

    @Override
    public void run() {
        try {
            URL service = new URL(Url);
            HttpURLConnection conn=(HttpURLConnection)service.openConnection();


            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn.connect();

            OutputStream ost=conn.getOutputStream();
            ost.write(post_request.getBytes());
            ost.flush();
            ost.close();
            InputStreamReader isr=new InputStreamReader(conn.getInputStream());

            int x=0;
            char[] buf=new char[6];
            String result="";
            while (x!=-1) {
                for(int i=0;i<x;i++){
                    result+=String.valueOf(buf[i]);
                }
                x = isr.read(buf);

            }

            Message msg=Message.obtain();
            result=result.trim();
            msg.obj=result;
            msg.arg1=cmd;

            hnd.sendMessage(msg);


        }
        catch(Exception e){
            Log.e("Connection ",e.getMessage());
        }


    }

    private void setParam_AddMember(){
        //params[name,email]

        post_request+="action=1&name="+params[0]+"&email="+params[1];
    }

    private void setParam_ChangeData(){
        //params[id,name]
        post_request+="action=2&name="+params[1]+"&memberid="+params[0];
    }

    private void setParam_MemberExist(){
        //params[id]
        post_request+="action=3&memberid="+params[0];
    }

    private void setParam_DeleteUser(){
        //params[id]
        post_request+="action=4&memberid="+params[0];
    }

    private void setParam_PairData(){
        //params non exist
        post_request+="action=5";
    }

    private void setParam_FullData(){
        //params[id]
        post_request+="action=6&memberid="+params[0];
    }

    private void setParam_FlexData(){
        //params non exist
        post_request+="action=7";
    }
    private void setParam_FlexJSON(){
        //params non exist
        post_request+="action=8";
    }



}
