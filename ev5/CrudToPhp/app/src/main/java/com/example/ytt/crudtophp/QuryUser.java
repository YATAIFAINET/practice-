package com.example.ytt.crudtophp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ytt.crudtophp.Adaptors.BriefNameAdaptor;
import com.example.ytt.crudtophp.Adaptors.BriefNameFlexAdaptor;
import com.example.ytt.crudtophp.DataAccessor.MyMember;
import com.example.ytt.crudtophp.DataAccessor.NetAccessor;

import org.json.JSONArray;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class QuryUser extends AppCompatActivity {
    Handler hnd=null;
    EditText input_name=null;
    EditText input_tel=null;
    EditText input_email=null;
    ListView show_result=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_handler();
        setContentView(R.layout.activity_change_data);
        init_views();
    }

    private void init_views(){
        input_name=(EditText)findViewById(R.id.input_change_name);
        input_email=(EditText)findViewById(R.id.input_changedata_email);
        input_tel=(EditText)findViewById(R.id.input_changedata_userTel);;

    }


    private void init_handler() {
        final Activity ee=this;

        hnd=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                BriefNameFlexAdaptor bn;
                MyMember[] members=null;
                super.handleMessage(msg);
                String src=(String)msg.obj;
                if(msg.arg1== NetAccessor.QURY_USER){
                    try{
                        JSONArray s=new JSONArray(src);
                        int count=s.length();
                        Log.d("a",src);
                        members=new MyMember[count];
                        for(int i=0;i<count;i++){
                            members[i]=new MyMember(s.getJSONArray(i));
                        }

                    }catch(Exception e){
                        Log.d("FLEX_INFO",e.getMessage());
                    }


                }

                bn=new BriefNameFlexAdaptor(ee,members);
                show_result.setAdapter(bn);




            }
        };
    }


    public void changeData(View v){
        String[] sd=new String[3];
        sd[0]=input_email.getText().toString();
        sd[1]=input_name.getText().toString();
        sd[2]=input_tel.getText().toString();
        Log.d("PARAMS 1",sd[0]);
        Log.d("PARAMS 2",sd[1]);
        Log.d("PARAMS 3",sd[2]);
        Thread h1=new Thread(new NetAccessor(hnd,NetAccessor.QURY_USER,sd));
        h1.start();


    }


}
