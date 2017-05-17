package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class sign_up_check_code extends Activity {
    private String ip,folder;
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    String mb_no="";
    private EditText mEditText_code;
    private Button mButton_check, mButton_Pass;
    String click_temp = "-1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //消除狀態列

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隱藏logo
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        setContentView(R.layout.activity_sign_up_check_code);
        setTitle("認證碼確認");
        Bundle bundle =this.getIntent().getExtras();
        load_config();
        mb_no = bundle.getString("mb_no");

        mEditText_code = (EditText) findViewById(R.id.code);
        mButton_check = (Button) findViewById(R.id.btn_check_code);
        mButton_Pass = (Button) findViewById(R.id.btn_pass);
        mButton_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditText_code.getText().toString().length() == 0){
                    Toast.makeText(sign_up_check_code.this,"請輸入認證碼",Toast.LENGTH_LONG).show();
                }else{
                    click_temp = "0";
                    senddata();
                }
            }
        });
        mButton_Pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_temp = "1";
                senddata();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_normal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent i = new Intent(this,login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        startActivity(i);
        finish();

        return super.onOptionsItemSelected(item);
    }
    public void load_config() {
        try {
            InputStreamReader inputReader = new InputStreamReader( getResources().getAssets().open("config") );
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null){
                Result += line;

            }

            ip = new JSONObject(Result).getString("ip");
            folder = new JSONObject(Result).getString("folder");
            //Toast.makeText(getApplicationContext(),ip, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void senddata() {
        mThread = new HandlerThread("chk_code");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = senddata_php("chk_code");

                mUI_Handler.post(new Runnable() {
                    public void run() {

                        try {
                            senddata_res(jsonString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });
    }
    private String senddata_php(String cmd)
    {
        String result = "";
        EditText bossid=(EditText)findViewById(R.id.boss_id);
        EditText mb_name=(EditText)findViewById(R.id.mb_name);
        try
        {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",cmd));
            nameValuePairs.add(new BasicNameValuePair("code",mEditText_code.getText().toString()));


            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse httpResponse = httpClient.execute(post);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = bufReader.readLine()) != null)
            {
                builder.append(line + "\n");
            }
            inputStream.close();
            result = builder.toString();
        }
        catch (Exception e)
        {
            Log.e("log_tag", e.toString());
        }
        return result;
    }
    public final void senddata_res(String input) throws JSONException {
        Log.d("leo pwd",input);
        JSONArray jsonArray = new JSONArray(input);
        if(jsonArray.length()==0){
            if(click_temp.equals("0")){
                Toast.makeText(sign_up_check_code.this,"認證碼有誤",Toast.LENGTH_LONG).show();
            }else if(click_temp.equals("1")){
                mButton_check.setClickable(false);
                Bundle bundle = new Bundle();
                Intent intent = new Intent();
                bundle.putString("true_intro_no","");
                bundle.putString("from","login");
                intent.putExtras(bundle);
                intent.setClass(sign_up_check_code.this,join_data_qrcode.class);
                startActivity(intent);
                finish();
            }
            //Toast.makeText(sign_up_check_code.this,"識別碼錯誤!",Toast.LENGTH_LONG).show();
        }else{
            //Toast.makeText(sign_up_check_code.this,"驗證成功!",Toast.LENGTH_LONG).show();
            mButton_check.setClickable(false);
            Bundle bundle = new Bundle();
            Intent intent = new Intent();
            bundle.putString("true_intro_no",jsonArray.getJSONObject(0).getString("mb_no"));
            bundle.putString("from","login");
            intent.putExtras(bundle);
            intent.setClass(sign_up_check_code.this,join_data_qrcode.class);
            startActivity(intent);
            finish();
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent i = new Intent(sign_up_check_code.this, login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
