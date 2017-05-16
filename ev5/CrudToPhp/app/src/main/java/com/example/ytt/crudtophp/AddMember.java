package com.example.ytt.crudtophp;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddMember extends AppCompatActivity {
    EditText uName=null;
    EditText uEmail=null;
    Handler hnd=null;
    TextView result=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_handler();
        setContentView(R.layout.activity_add_member);
        init_views();
    }

    private void init_views(){
        uName=(EditText)findViewById(R.id.input_add_user_name);
        uEmail=(EditText)findViewById(R.id.input_add_user_email);
        result=(TextView)findViewById(R.id.show_reg_result);
    }

    private void init_handler(){
        hnd=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                result.setText((String)msg.obj);
            }
        };
    }



    public void addMember(View v){
        Thread h1=new Thread(new Reg(uName.getText().toString(),uEmail.getText().toString()));
        h1.start();

    }

    private class Reg implements Runnable{
        String name=null;
        String em=null;
        StringBuilder s=null;
        public Reg(String username,String email){
            name=username;
            em=email;
            s=new StringBuilder("");
            s.append("action=1&");
            s.append("name="+username+"&");
            s.append("email="+em);
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
                ost.write(s.toString().getBytes());
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
