package com.example.ytt.crudtophp;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.example.ytt.crudtophp.prefs.LinkPref;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class MemberExist extends AppCompatActivity {
    Handler hnd=null;
    TextView show_userExist=null;
    EditText input_email=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_handler();
        setContentView(R.layout.activity_member_exist);
        init_views();
    }


    private void init_handler(){
        hnd=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                show_userExist.setText((String)msg.obj);

            }
        };
    }


    private void init_views(){
        show_userExist=(TextView)findViewById(R.id.v_show_user_exist);
        input_email=(EditText)findViewById(R.id.input_em_chkExt);

    }


    public void check_exist(View v){
            String em=input_email.getText().toString();
            Thread h1=new Thread(new AskServer(em));
            h1.start();
    }

    public void back_to_main(View v){
        Intent back_to_main=new Intent(this,MainActivity.class);
        startActivity(back_to_main);
    }


    private class AskServer implements Runnable{
        String em=null;
        public AskServer(String email){
            em=email;
        }

        @Override
        public void run() {
            try {
                URL service = new URL("http://192.168.1.99/leo/fainet/memberSystem.php");
                HttpURLConnection conn=(HttpURLConnection)service.openConnection();
                String params="action=3&email="+em;

                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                conn.connect();

                OutputStream ost=conn.getOutputStream();

                Log.d("sdggg",params);
                ost.write(params.getBytes());
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
