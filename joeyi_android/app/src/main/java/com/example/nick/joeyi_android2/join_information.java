package com.example.nick.joeyi_android2;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class join_information extends Activity {
    private String ip,folder;
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private WebView rele_text;
    private Button agree,not_agree;
    Handler handler=new Handler();
    Runnable runnable;
    protected void onStart(){
        super.onStart();
        net_check();

    }
    protected void onStop(){
        super.onStop();
        handler.removeCallbacks(runnable);
    }
    void net_check() {


        runnable=new Runnable(){
            @Override
            public void run() {

                // TODO Auto-generated method stub
                ConnectivityManager CM = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = CM.getActiveNetworkInfo();
                if (info == null || !info.isAvailable()) { //判斷是否有網路
                    handler.postDelayed(this, 2000000);
                    new AlertDialog.Builder(join_information.this)
                            .setTitle("錯誤!!")
                            .setMessage("網路連線異常!!")
                            .setCancelable(false)
                            .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    handler.removeCallbacks(runnable);
                                    finish();

                                }
                            }).show();



                }else{
                    handler.postDelayed(this, 2000);

                }

            }
        };
        handler.postDelayed(runnable, 500);//每兩秒執行一次runnable.
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final Drawable upArrow = getResources().getDrawable(R.drawable.home_2);
        getActionBar().setHomeAsUpIndicator(upArrow);
        setContentView(R.layout.activity_join_information);
        rele_text=(WebView)findViewById(R.id.rele_text);
        load_config();
        get_rule_thread();



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

        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;

        }

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
    public void get_rule_thread() {
        mThread = new HandlerThread("get_rule");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_rule_data("get_rule");

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
            String url="http://+ip+/+folder/join.php";
            Log.d("url", url);
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/join.php");

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


        //rele_text.setText(Html.fromHtml(input));
        //rele_text.setMovementMethod(ScrollingMovementMethod.getInstance());
        String htmldata=input;//网页代码
        String targeturl="";//目标网址（具体）
        String baseurl="";//连接目标网址失败进入的默认网址
        rele_text.getSettings().setDefaultTextEncodingName("GB2312");
        rele_text.loadData(htmldata,"text/html","utf-8");
        rele_text.loadDataWithBaseURL(targeturl, htmldata,"text/html","utf-8", baseurl);

    }





}

