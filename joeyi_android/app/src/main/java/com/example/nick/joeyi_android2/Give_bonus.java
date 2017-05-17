package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Give_bonus extends Activity {
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Handler mUI_Handler = new Handler();
    private Context context;
    String ip = "", folder = "",config="",login_id,from;
    TextView mTextView_Total_Bonus;
    String Total_bonus="";
    private EditText mEditText_Pwd2 , mEditText_To_Mb , mEditText_Point;
    private Button mButton_Send,mButton_List;
    String status = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        load_config();
        getActionBar().setTitle("會員互轉");
        Bundle bundle = this.getIntent().getExtras();
        Total_bonus = bundle.getString("can_use_bonus");
        //Toast.makeText(Give_bonus.this,""+bundle.getString("can_use_bonus"),Toast.LENGTH_LONG).show();
        config  = readFromFile("client_config");
        context = this;
        setContentView(R.layout.give_bonus);
        Init();
        mTextView_Total_Bonus.setText(Total_bonus);
    }

    //-----------------------------------------------------------------------
    public void Give_Bonus_thread() {
        mThread = new HandlerThread("Give_Bonus_thread");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery("Give_Bonus");
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        newlist(jsonString);
                        killActivity();
                    }
                });
            }
        });
    }
    private String executeQuery(String query)
    {
        String result = "";
        try
        {
            login_id = new JSONObject(config).getString("login_id");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("mb_no",login_id));
            nameValuePairs.add(new BasicNameValuePair("to_mb_no",mEditText_To_Mb.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("password2",mEditText_Pwd2.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("to_money",mEditText_Point.getText().toString()));
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
    public final void newlist(String input)
    {
        try
        {
            JSONArray jsonArray = new JSONArray(input);
            JSONObject jsonData = jsonArray.getJSONObject(0);
            if(jsonData.getString("code").equals("成功發送")){
                Toast.makeText(Give_bonus.this,jsonData.getString("code"),Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(Give_bonus.this,jsonData.getString("code"),Toast.LENGTH_LONG).show();
            }
        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());
        }

    }
    //-------------------------------------------------------------------

    private void killActivity() {
        finish();
    }
    private void Init(){
        mTextView_Total_Bonus = (TextView) findViewById(R.id.total_bonus);
        mEditText_Pwd2 = (EditText) findViewById(R.id.pwd2);
        mEditText_To_Mb = (EditText) findViewById(R.id.to_mb_no);
        mEditText_Point = (EditText) findViewById(R.id.give_money);
        mButton_Send = (Button) findViewById(R.id.send_btn);
        mButton_List = (Button) findViewById(R.id.list_btn);

        mButton_List.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("can_use_bonus",Total_bonus);
                intent.putExtras(bundle);
                intent.setClass(Give_bonus.this,Give_bonus_ListPage.class);
                startActivity(intent);
            }
        });

        mButton_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditText_Pwd2.getText().toString().equals("") || mEditText_Point.getText().toString().equals("") || mEditText_To_Mb.getText().toString().equals("")){
                    Toast.makeText(Give_bonus.this,"欄位不可為空",Toast.LENGTH_LONG).show();
                }else{
                    if( Integer.valueOf(mEditText_Point.getText().toString())==0){
                        Toast.makeText(Give_bonus.this,"點數不可為零",Toast.LENGTH_LONG).show();
                    }else{
                        if(Integer.valueOf(Total_bonus)>=Integer.valueOf(mEditText_Point.getText().toString())){
                            Give_Bonus_thread();
                        }else{
                            Toast.makeText(Give_bonus.this,"可用點數不足",Toast.LENGTH_LONG).show();
                        }
                    }
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;

        }
        //Toast.makeText(context, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    public void load_config() {
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open("config"));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null) {
                Result += line;

            }

            ip = new JSONObject(Result).getString("ip");
            folder = new JSONObject(Result).getString("folder");
            //Toast.makeText(getApplicationContext(),ip, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String readFromFile(String FILENAME) {
        try{
            FileInputStream fin = openFileInput(FILENAME);
            byte[] buff = new byte[fin.available()];
            fin.read(buff);
            String str = new String(buff);



            return str;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
