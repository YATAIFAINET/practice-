package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.SpannableString;
import android.text.method.ReplacementTransformationMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nick.joeyi_android2.GCMD.Picture_GCMD;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class login extends Activity {

    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Button btn_login,btn_explan;
    private EditText login_id,login_pwd;
    private CheckBox chk_remember_id;
    private LinearLayout linear_max;
    private String id_remember,id_remember_org;
    private TextView app_version;

    SharedPreferences preferences ;
    String ip="",folder="",rid="";
    String config="",version="";
    String str;
    private Picture_GCMD mGCMD  = new Picture_GCMD();
    public login () {
        mGCMD.FileCreate();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //消除標題列
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //消除狀態列
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //读取SharedPreferences中需要的数据
        preferences = getSharedPreferences( "count" , MODE_WORLD_READABLE );
        int count = preferences.getInt("count", 0);
//        if (count == 0) {
//            Intent Intentintent = new Intent();
//            Intentintent.setClass(getApplicationContext(),use_page. class );
//            startActivity(Intentintent);
//            finish();
//        }
        SharedPreferences.Editor editor = preferences .edit();
        //存入数据
        editor.putInt("count", ++count);
        //提交修改
        editor.commit();
        config  = readFromFile("client_config");
        if(config==null){
            JSONObject obj=new JSONObject();
            try {
                obj.put("id_remember","0");//預設不通知
                obj.put("login_id","0");//預設不通知
                obj.put("country","");//預設不通知
                obj.put("indate","");//預設不通知
                writeToFile("client_config", String.valueOf(obj));

            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            config  = readFromFile("client_config");

        }
        setContentView(R.layout.login_layout);
        ConnectivityManager conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);//先取得此service
        NetworkInfo networInfo = conManager.getActiveNetworkInfo();       //在取得相關資訊

        if (networInfo == null || !networInfo.isAvailable()){ //判斷是否有網路
            new AlertDialog.Builder(this).setTitle("網路異常").setMessage("請先連上網路，再繼續使用！").setPositiveButton("確定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            }).show();
        }
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = packInfo.versionName;
        app_version = (TextView) findViewById(R.id.app_version);
        app_version.setText(version);

        load_config();
        Button textView = (Button)findViewById(R.id.txv_get_pwd);
        SpannableString content = new SpannableString("忘記密碼");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

        Button textView_sign = (Button)findViewById(R.id.txv_sign_up);
        SpannableString content1 = new SpannableString("註冊");
        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
        textView_sign.setText(content1);

        create_thread_version("get_version", "get_version");
        chk_remember_id = (CheckBox) findViewById(R.id.chk_remember_id);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_explan = (Button) findViewById(R.id.btn_explan);
        login_id = (EditText) findViewById(R.id.login_id);
        login_pwd = (EditText) findViewById(R.id.login_pwd);
        linear_max = (LinearLayout) findViewById(R.id.linear_max);


        btn_login.setOnClickListener(btn_login_listener);
        btn_explan.setOnClickListener(btn_explan_listener);
        linear_max.setOnClickListener(linear_max_listener);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_pwd();
            }
        });

        textView_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_sign();
            }
        });

        try {
            id_remember_org = new JSONObject(config).getString("id_remember");
            if(id_remember_org.equals("1")){
                id_remember=id_remember_org;
                chk_remember_id.setChecked(true);
                login_id.setText(new JSONObject(config).getString("boss_id"));
                login_pwd.setText(new JSONObject(config).getString("login_pwd"));
            }else{
                id_remember="0";
            }
        } catch (JSONException e) {
        }
        login_id.setTransformationMethod(new AllCapTransformationMethod());
    }
    public class AllCapTransformationMethod extends ReplacementTransformationMethod {

        @Override
        protected char[] getOriginal() {
            char[] aa = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z' };
            return aa;
        }

        @Override
        protected char[] getReplacement() {
            char[] cc = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z' };
            return cc;
        }

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
    public void create_thread(final String cmd,String thread_name) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        if (cmd == "get_login2") {
                            newlist(jsonString);
                        }
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
            Log.d("fac", "1");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            Log.e("acc",login_id.getText().toString());
            nameValuePairs.add(new BasicNameValuePair("boss_id",login_id.getText().toString().toUpperCase()));
            nameValuePairs.add(new BasicNameValuePair("login_pwd",login_pwd.getText().toString()));
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
    public final void newlist(String input)
    {
        //Toast.makeText(getApplication(), input, Toast.LENGTH_LONG).show();

        Log.d("leoleo0506", input);
        String[] temp = input.split("%%%");
        if(temp[0].equals("1")){

            JSONObject obj=new JSONObject();
            try {
                if(chk_remember_id.isChecked()){
                    id_remember="1";
                }else{
                    id_remember="0";
                }
                obj.put("boss_id",login_id.getText().toString().toUpperCase());   //登入帳號
                obj.put("login_id",temp[4].trim());   //會員編號
                obj.put("login_pwd",login_pwd.getText().toString());   //登入帳號
                obj.put("id_remember",id_remember);   //是否記住帳號
                obj.put("mb_country",temp[1]);   //是否記住帳號
                obj.put("indate",temp[2]);
                writeToFile("client_config", String.valueOf(obj));
                //create_thread_country("get_mb_country_s", "get_mb_country_s");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
/*
               Intent intent = new Intent();
               intent.setClass(this,MainActivity.class);
               startActivity(intent);
               finish();*/
            Log.d("fac_noooo", temp[3]);
            if(temp[3].equals("1")){
                Log.d("fac_noooo", "是1沒錯阿");
                MagicLenGCM magicLenGCM = new MagicLenGCM(this);
                magicLenGCM.openGCM();
                // while(rid.equals("")){
                rid=magicLenGCM.getRegistrationId();
                // }

                if(!rid.equals("")){
                    save_rid_thread("save_rid","save_rid");
                }

                Log.d("N_regId",rid);
                Intent intent = new Intent();
                intent.setClass(this,test_page.class);//loginuser_page
                startActivity(intent);
                finish();
            }else{
                Log.d("N_regId",rid);
                Intent intent = new Intent();
                intent.setClass(this,fac_qrcode.class);
                startActivity(intent);
                finish();
            }

        }else{
            Toast.makeText(getApplication(), "請輸入正確的帳號及密碼", Toast.LENGTH_LONG).show();
        }

    }
    public void create_thread_version(final String cmd,String thread_name) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery_version(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        newlist_version(jsonString);
                    }
                });
            }
        });
    }
    private String executeQuery_version(String query)
    {
        String result = "";
        try
        {
            Log.d("fac", "1");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            Log.d("LOGINIP", "http://" + ip + "/" + folder + "/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
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
    public final void newlist_version(String input)
    {
        Log.d("leoleo0506", input);
        String[] temp = input.split("%%%");
        if(!(temp[0].equals(version))&&temp[0].length()>0){
            btn_login.setVisibility(View.INVISIBLE);
            new AlertDialog.Builder(this).setTitle("版本更新").setCancelable(false)
                    .setMessage("目前版本：" + version + "\n" + "最新版本：" + temp[0] + "\n請先更新至最新版本，再繼續使用！").setPositiveButton("確定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String tmpurl = "https://play.google.com/store/apps/details?id=com.ytt.joeyi_android";

                    Uri uri = Uri.parse(tmpurl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    finish();
                    System.exit(0);
                }
            }).show();
        }
    }
    private LinearLayout.OnClickListener linear_max_listener = new Button.OnClickListener() {
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(login_id.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(login_pwd.getWindowToken(), 0);

        }
    };
    private Button.OnClickListener btn_login_listener = new Button.OnClickListener() {
        public void onClick(View v) {

            if (login_id.getText().toString().equals("") || login_pwd.getText().toString().equals("")) {
                Toast.makeText(getApplication(), "請輸入帳號及密碼", Toast.LENGTH_LONG).show();
            } else {
                create_thread("get_login2", "get_login2");
                Log.d("fac", "3");
            }

        }
    };
    private Button.OnClickListener btn_explan_listener = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent Intentintent = new Intent();
            Intentintent.setClass(getApplicationContext(),use_page.class );
            startActivity(Intentintent);
            finish();
        }
    };
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

    private void writeToFile(String FILENAME, String string) {
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
        }catch (Exception e){
        }
    }
    void go_pwd(){
        if(login_id.getText().toString().length()>0){
            Intent intent = new Intent();
            intent.setClass(this, pwd_ser_activity.class);
            Bundle b=new Bundle();
            b.putString("mb_no", login_id.getText().toString());
            intent.putExtras(b);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(login.this,"請輸入帳號",Toast.LENGTH_LONG).show();
        }
    }
    void go_sign(){
//        Intent intent = new Intent();
//        intent.setClass(this, sign_up_check_code.class);
//        Bundle b=new Bundle();
//        b.putString("mb_no", login_id.getText().toString());
//        intent.putExtras(b);
//        startActivity(intent);
//        finish();
        Toast.makeText(login.this,"尚未開放，敬請期待",Toast.LENGTH_LONG).show();
    }
    public void save_rid_thread(final String cmd,String thread_name) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery_rid(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        Log.d("abcabc", jsonString);
                    }
                });
            }
        });
    }
    private String executeQuery_rid(String query)
    {
        String result = "";
        try
        {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("login_id",login_id.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("rid", rid));
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
    public void create_thread_country(final String cmd,String thread_name) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery_mb(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        if (cmd == "get_mb_country_s") {
                            newlist_s(jsonString);
                        }
                    }
                });
            }
        });
    }
    private String executeQuery_mb(String query)
    {
        String result = "";
        try
        {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("mb_no",login_id.getText().toString()));
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
    public final void newlist_s(String input)
    {
        //Toast.makeText(getApplication(), input, Toast.LENGTH_SHORT).show();
        JSONObject obj=new JSONObject();
        try {
            obj.put("mb_country",input);   //登入帳號
            writeToFile("client_config", String.valueOf(obj));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }

}