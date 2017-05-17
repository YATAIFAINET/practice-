package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;

public class Activity_Start_Page_Select extends Activity {
    private Button mButton_Login , mButton_Use;
    private Intent intent = new Intent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //消除標題列
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //消除狀態列
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_activity_start_page_select);

        Init_Setting();

    }
    private void Init_Setting(){
        mButton_Login = (Button) findViewById(R.id.Button_Login);
        mButton_Use = (Button) findViewById(R.id.Button_Use);

        mButton_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setClass(Activity_Start_Page_Select.this,login.class);
                startActivity(intent);
                finish();
            }
        });

        mButton_Use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("login_id", "none");   //登入帳號
                    obj.put("login_pwd", "none");   //登入帳號
                    obj.put("id_remember", "0");   //是否記住帳號
                    obj.put("mb_country", "none");   //是否記住帳號
                    obj.put("indate", "none");
                    writeToFile("client_config", String.valueOf(obj));
                    //create_thread_country("get_mb_country_s", "get_mb_country_s");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                intent.setClass(Activity_Start_Page_Select.this, test_page.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void writeToFile(String FILENAME, String string) {
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
        }catch (Exception e){
        }
    }
}
