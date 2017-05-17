package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YTT1 on 2016/8/25.
 */
public class money_webview_detail extends Activity {

    public ProgressDialog mDialog;
    private WebView mWb;
    private String ip,folder,mb_no;
    String config="";

    private android.os.Handler mUI_Handler = new android.os.Handler();
    private HandlerThread mThread;
    private android.os.Handler mThreadHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.money_webview_detail);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        setTitle("會員獎金明細");
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        config  = readFromFile("client_config");
        load_config();
        InitialSetting();
    }

    public void InitialSetting(){
       mDialog=ProgressDialog.show(this, "獎金查詢", "讀取資料中...", true);
        mThread = new HandlerThread("CustomerDialog ");
        mThread.start();
        mThreadHandler = new android.os.Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String mWebhttp= WebHttpGet();
                Log.d("fac","web"+mWebhttp);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        mWb = (WebView) findViewById(R.id.money_webview_detail);
                        mWb.setWebViewClient(new MyWebViewClient());
                        mWb.getSettings().setJavaScriptEnabled(true);
                        mWb.requestFocus();
                        // mWb.loadUrl("http://newtw.gew2u.biz/hq/bonus/Bonus_week_tw.asp");
                        //http://twmembers.ganoeworldwide.com/hq/bonus/Bonus_week_tw.asp?mbno=  原本的html
                        //http://twmembers.ganoeworldwide.com/hq/bonus/Bonus_week_tw.php?mbno=TW0001023
                        //mWb.loadUrl("http://twmembers.ganoeworldwide.com/hq/bonus/Bonus_week_tw.php?mbno=" + mb_no);
                        Log.d("fac","ttt"+ mWebhttp);
                        Log.d("fac","ttt2"+ mb_no);
                        mWb.loadUrl(mWebhttp + mb_no);
                        //"http://twmembers.ganoeworldwide.com/hq/bonus/Bonus_week_tw.asp?mbno=<?php echo $_SESSION['mb']['mb_no'];?>" width="100%" height="100%" scrolling="yes" frameborder="0"
                        mDialog.dismiss();
                    }
                });
            }
        });
    }
    //--------------------------------------------------------------------
    private String WebHttpGet (){

        // 運行網路連線的程式
        //mid="YATAI";
        String result;
        result=sendPostDataToInternet_tmp("http://" + ip + "/" + folder + "/android_sql.php", "", "WebHttpGet");
        Log.d("fac",result+"asdsadsasda");
        result=result.substring(0, result.length() - 1);
        Log.d("fac", result + "result");
        Log.d("fac", "JsonARRAy init");

        return result;
    }

    //--------------------------------------------------------------------
    public String sendPostDataToInternet_tmp(String customer,String strResult,String cmd) {
        HttpPost httpRequest = new HttpPost(customer);
        HttpResponse httpResponse;
        HttpEntity httpEntity;
        List params = new ArrayList();
        params.add(new BasicNameValuePair("cmd", cmd));

        Log.d("fac","httppost params add finish");
        try
        {
            Log.d("fac","httprequest initial");
            /* 發出HTTP request */
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            /* 取得HTTP response */
            Log.d("fac","httprequest set"+customer);
            httpResponse = new DefaultHttpClient().execute(httpRequest);
            Log.d("fac","httpresponse start");
            /* 若狀態碼為200 ok */
            if (httpResponse.getStatusLine().getStatusCode() == 200)

            {
                httpEntity = httpResponse.getEntity();
                //strResult = EntityUtils.toString(httpResponse.getEntity());
                InputStream inputStream = httpEntity.getContent();
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = bufReader.readLine()) != null)
                {
                    builder.append(line + "\n");
                }
                inputStream.close();
                strResult = builder.toString();
                Log.d("fac",strResult+"ssss");
                Log.d("fac","aaaa");
                Log.d("fac","ttt");
            }

        }  catch (Exception e)

        {
            // Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Log.d("fac", "789");
        }
        return strResult;
    }

    private String readFromFile(String FILENAME) {
        try{
            FileInputStream fin = openFileInput(FILENAME);
            byte[] buff = new byte[fin.available()];
            fin.read(buff);
            String str = new String(buff);
            fin.close();
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
            mb_no = new JSONObject(config).getString("login_id");

            Log.d("money_webview_detail","ip"+ip);
            Log.d("money_webview_detail","folder"+folder);
            Log.d("money_webview_detail",mb_no);
            //Toast.makeText(getApplicationContext(),ip, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
