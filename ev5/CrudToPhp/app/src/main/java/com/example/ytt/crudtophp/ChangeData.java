package com.example.ytt.crudtophp;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChangeData extends AppCompatActivity {
    Handler hnd=null;
    EditText input_data=null;
    TextView result=null;
    EditText input_id=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_handler();
        setContentView(R.layout.activity_change_data);
        init_views();
    }

    private void init_views(){
        input_data=(EditText)findViewById(R.id.input_change_name);
        input_id=(EditText)findViewById(R.id.input_changedata_userid);
        result=(TextView)findViewById(R.id.view_result_changeData);
    }


    private void init_handler() {
        hnd=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                result.setText((String)msg.obj);
            }
        };
    }


    public void changeData(View v){
        Thread h1=new Thread(new Change(input_id.getText().toString(),input_data.getText().toString()));
        h1.start();

    }

    private class Change implements Runnable{

        StringBuilder param=null;
        public Change(String userid,String username){
            param=new StringBuilder("action=2&");
            param.append("name="+username+"&");
            param.append("memberid="+userid);
        }
        @Override
        public void run() {
            try {
                URL service = new URL("http://192.168.1.99/leo/fainet/memberSystem.php");
                HttpURLConnection conn=(HttpURLConnection)service.openConnection();


                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                conn.connect();

                OutputStream ost=conn.getOutputStream();
                ost.write(param.toString().getBytes());
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
                Log.d("result",result);
                Message msg=Message.obtain();
                msg.obj=result;
                hnd.sendMessage(msg);

            }
            catch(Exception e){
                Log.e("Connection ",e.getMessage());
            }

        }
    }


}
