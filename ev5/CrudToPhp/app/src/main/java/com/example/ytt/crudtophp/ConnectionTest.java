package com.example.ytt.crudtophp;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ytt.crudtophp.DataAccessor.NetAccessor;

public class ConnectionTest extends AppCompatActivity {

    Handler hnd=null;
    EditText command=null;
    EditText uName=null;
    EditText uEmail=null;
    EditText memberid=null;
    TextView show_result=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_handler();
        setContentView(R.layout.activity_connection_test);
        init_views();
    }

    private void init_handler(){
        hnd=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                show_result.setText((String)msg.obj);
            }
        };
    }

    private void init_views(){
        command=(EditText)findViewById(R.id.input_test_code);
        uName=(EditText)findViewById(R.id.input_test_name);
        uEmail=(EditText)findViewById(R.id.input_test_email);
        memberid=(EditText)findViewById(R.id.input_test_memberid);
        show_result=(TextView)findViewById(R.id.show_test_connResult);
    }

    public void backtomain(View v){
        startActivity(new Intent(this,MainActivity.class));
    }


    public void testConnect(View v){
        Log.d("input","touched");
        Thread th1=null;
        String name=uName.getText().toString();
        String email=uEmail.getText().toString();
        String uid=memberid.getText().toString();
        int cmd=Integer.parseInt(command.getText().toString());
        switch(cmd){
            case NetAccessor.ADD_MEMBER:
                th1=new Thread(new NetAccessor(hnd,cmd,name,email));
                break;
            case NetAccessor.CHANGE_DATA:
                th1=new Thread(new NetAccessor(hnd,cmd,uid,name));
                break;
            case NetAccessor.MEMBER_EXIST:
                th1=new Thread(new NetAccessor(hnd,cmd,uid));
                break;
            case NetAccessor.DELETE_USER:
                th1=new Thread(new NetAccessor(hnd,cmd,uid));
                break;
            case NetAccessor.PAIR_DATA:
                th1=new Thread(new NetAccessor(hnd,cmd,null));
                break;
            case NetAccessor.FULL_DATA:
                th1=new Thread(new NetAccessor(hnd,cmd,uid));
                break;
        }
        th1.start();
    }



}
