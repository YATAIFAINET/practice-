package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;

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

/**
 * Created by YTT1 on 2016/8/25.
 */
public class placard_detail_mes extends Activity {
    private String config="";
    private String login_id;
    private WebView wb;
    private String ip,folder;
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private String[] join_ini_array;
    public static String join_prod_no_static = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.placard_detail_mes);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        setTitle("最新公告");
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        config = readFromFile("client_config");
        try {
            login_id = new JSONObject(config).getString("login_id");
        } catch (Exception e) {

        }
        wb=(WebView)findViewById(R.id.placard_detail_web);
        load_config();
        get_rule_thread();
        get_join_ini_thread();
    }

    private String readFromFile(String FILENAME) {
        try{

            FileInputStream fin = this.openFileInput(FILENAME);
            byte[] buff = new byte[fin.available()];
            fin.read(buff);
            String str = new String(buff);



            return str;
        }catch (Exception e){
            e.printStackTrace();
            return null;
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
    //------------------------------------------------------------------------------
    public void get_join_ini_thread() {
        mThread = new HandlerThread("get_join_ini");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_join_ini_data("get_join_ini");

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        get_join_ini_res(jsonString);
                    }
                });
            }
        });
    }
    private String get_join_ini_data(String cmd)
    {
        String result = "";
        try
        {
            HttpClient httpClient = new DefaultHttpClient();
            String url="http://"+ip+"/"+folder+"/android_sql.php";
            Log.d("url", url);
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",cmd));
            nameValuePairs.add(new BasicNameValuePair("mb_no",login_id));
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
    public final void get_join_ini_res(String input)
    {
        Log.d("ff",input);
        join_ini_array = input.split("%%");
        join_ini_array[2]=join_ini_array[2].substring(0,join_ini_array[2].length()-1);
        for(int i =0;i<join_ini_array.length;i++){
            Log.e("sheng",join_ini_array[i]);
        }
        if(join_ini_array[0].equals("0")) {
            Global_cart globalVariable = (Global_cart) this.getApplicationContext();
            globalVariable.cart.put(join_ini_array[1],"1"+","+join_ini_array[2]+","+0+","+0);
            join_prod_no_static = join_ini_array[1];
            Log.e("sheng","已加入購物車");
        }
    }
    //------------------------------------------------------------------------------
    public void get_rule_thread() {
        mThread = new HandlerThread("get_top_news");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_rule_data("get_top_news");

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        get_rule_res(jsonString);
                    }
                });
            }
        });
    }
    private String get_rule_data(String cmd)
    {
        String result = "";
        try
        {
            HttpClient httpClient = new DefaultHttpClient();
            String url="http://"+ip+"/"+folder+"/android_sql.php";
            Log.d("url", url);
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",cmd));
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
    public final void get_rule_res(String input)
    {
        Log.d("ff",input);
                String html1=input;
                html1=html1.replace("width", "width1");
                html1=html1.replace("height", "width1");
                html1=html1.replace(" style=\"", " style=\" width:100%; ");

                Log.d("html1", html1);
                String htmldata=html1;//网页代码
                String targeturl="";//目标网址（具体）
                String baseurl="";//连接目标网址失败进入的默认网址
                wb.getSettings().setDefaultTextEncodingName("GB2312");
                wb.loadData(htmldata,"text/html","utf-8");
                wb.loadDataWithBaseURL(targeturl, htmldata, "text/html","utf-8", baseurl);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        finish();
        Intent Intentintent = new Intent();
        Bundle b=new Bundle();
        Intentintent.putExtras(b);
        Intentintent.setClass(getApplicationContext(), test_page.class);
        startActivity(Intentintent);
        return super.onOptionsItemSelected(item);
    }

    //--------------------------------------------------------------------
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent();
            intent.setClass(this,test_page.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
