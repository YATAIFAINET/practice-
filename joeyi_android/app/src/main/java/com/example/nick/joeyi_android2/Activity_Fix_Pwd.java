package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Activity_Fix_Pwd extends Activity {
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    String ip="",folder="";
    String config="",login_no;

    private EditText mEditText_Mb_Pwd_Old, mEditText_Mb_Pwd_New, mEditText_Mb_Pwd_Check, mEditText_Pwd2_Old
            , mEditText_Pwd2_New, mEditText_Pwd2_Check;
    private Button mButton_Mb_Pwd_Send, mButton_Pwd2_Send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fix_pwd);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        setTitle("修改密碼");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        }
        load_config();
        config  = readFromFile("client_config");
        Init();
    }

    private void Init(){
        mEditText_Mb_Pwd_Old = (EditText) findViewById(R.id.edittext_mb_pwd_old);
        mEditText_Mb_Pwd_New = (EditText) findViewById(R.id.edittext_mb_pwd_new);
        mEditText_Mb_Pwd_Check = (EditText) findViewById(R.id.edittext_mb_pwd_check);

        mEditText_Pwd2_Old = (EditText) findViewById(R.id.edittext_pwd2_old);
        mEditText_Pwd2_New = (EditText) findViewById(R.id.edittext_pwd2_new);
        mEditText_Pwd2_Check = (EditText) findViewById(R.id.edittext_pwd2_check);

        mButton_Mb_Pwd_Send = (Button) findViewById(R.id.button_mb_pwd_send);
        mButton_Pwd2_Send = (Button) findViewById(R.id.button_pwd2_send);

        mButton_Mb_Pwd_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditText_Mb_Pwd_Old.getText().toString().equals("") || mEditText_Mb_Pwd_New.getText().toString().equals("") || mEditText_Mb_Pwd_Check.getText().toString().equals("")){
                    Toast.makeText(Activity_Fix_Pwd.this,"尚未輸入完整資訊",Toast.LENGTH_LONG).show();
                }else{
                    if(mEditText_Mb_Pwd_New.getText().toString().equals(mEditText_Mb_Pwd_Check.getText().toString())){
                        Fix_Pwd_thread("mb_pwd");
                    }else{
                        Toast.makeText(Activity_Fix_Pwd.this,"確認密碼與新密碼不一致",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        mButton_Pwd2_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditText_Pwd2_Old.getText().toString().equals("") || mEditText_Pwd2_New.getText().toString().equals("") || mEditText_Pwd2_Check.getText().toString().equals("")){
                    Toast.makeText(Activity_Fix_Pwd.this,"尚未輸入完整資訊",Toast.LENGTH_LONG).show();
                }else{
                    if(mEditText_Pwd2_New.getText().toString().equals(mEditText_Pwd2_Check.getText().toString())){
                        Fix_Pwd_thread("password2");
                    }else{
                        Toast.makeText(Activity_Fix_Pwd.this,"確認密碼與新密碼不一致",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected", "onOptionsItemSelected ");
        switch(item.getItemId()) {
            case android.R.id.home:
                Log.d("onOptionsItemSelected2", "onOptionsItemSelected2 ");
                finish();
                return true;
            default:
                break;

        }
        //Toast.makeText(context, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    //thread
    public void Fix_Pwd_thread(final String type) {
        mThread = new HandlerThread("fix_mb_pwd");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = Fix_Pwd_Send("fix_mb_pwd",type);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        Fix_Pwd_Res(jsonString);
                    }
                });
            }
        });
    }
    private String Fix_Pwd_Send(String query,String type_tmp)
    {
        String result = "";
        try
        {
            login_no = new JSONObject(config).getString("login_id");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("mb_no",login_no));
            nameValuePairs.add(new BasicNameValuePair("type",type_tmp));
            if(type_tmp.equals("mb_pwd")){
                nameValuePairs.add(new BasicNameValuePair("old_pwd",mEditText_Mb_Pwd_Old.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("new_pwd",mEditText_Mb_Pwd_New.getText().toString()));
            }else if(type_tmp.equals("password2")){
                nameValuePairs.add(new BasicNameValuePair("old_pwd",mEditText_Pwd2_Old.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("new_pwd",mEditText_Pwd2_New.getText().toString()));
            }
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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
    public final void Fix_Pwd_Res(String input) {
        Log.e("@@@@@",input);
        if(input.trim().equals("success")){
            Toast.makeText(Activity_Fix_Pwd.this,"修改成功",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(Activity_Fix_Pwd.this,"舊密碼有誤",Toast.LENGTH_LONG).show();
        }
    }
}
