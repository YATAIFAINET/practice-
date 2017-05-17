package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by root on 11/25/15.
 */
public class news_detail extends Activity {
    private String ip = "", folder = "";
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Handler mUI_Handler = new Handler();
    private String news_no;
    private TextView title_tv,post_date_tv;
    private WebView context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隱藏logo
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        setContentView(R.layout.dis_new_detail);
        setTitle("最新訊息");
        load_config();
        Bundle bundle = this.getIntent().getExtras();
        news_no = bundle.getString("news_no");
        title_tv=(TextView)findViewById(R.id.dis_title_detail);
        post_date_tv=(TextView)findViewById(R.id.post_date_detail);
        context=(WebView)findViewById(R.id.context);
        get_discount_thread();

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
    public void get_discount_thread() {
        mThread = new HandlerThread("get_prod_no_detail");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = send_discount_data();

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        renew_data(jsonString);

                    }
                });
            }
        });
    }

    private String send_discount_data() {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            //HttpPost post = new HttpPost("http://220.135.161.128/leo/android_sql.php");
            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", "send_news_data_detail"));
            nameValuePairs.add(new BasicNameValuePair("news_no",news_no));
            Log.e("*********------",""+news_no);

            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpClient.execute(post);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = bufReader.readLine()) != null) {
                builder.append(line + "\n");
            }
            inputStream.close();
            result = builder.toString();
        } catch (Exception e) {
            Log.e("log_tag", e.toString());
        }
        return result;
    }

    public final void renew_data(String input) {
        Log.d("input",input);
        try {
            JSONArray jsonArray = new JSONArray(input);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                String title=jsonData.getString("title");
                Log.e("******title", "" + title);
                String post_date=jsonData.getString("postTime");
                String img_str=jsonData.getString("img_str");


                title_tv.setText(title);
                post_date_tv.setText(post_date);


                String html1=jsonData.getString("content");

                html1=html1.replace("width", "width1");
                html1=html1.replace("height", "width1");
                html1=html1.replace(" style=\"", " style=\" width:100%; ");
                String tmp = img_str+html1;
                Log.d("html1", tmp);

                String htmldata="<style>\n" +
                        "body {line-height:30px}\n"+
                        "</style>"+tmp;//网页代码




                String targeturl="";//目标网址（具体）
                String baseurl="";//连接目标网址失败进入的默认网址
                context.getSettings().setDefaultTextEncodingName("GB2312");
                context.loadData(htmldata,"text/html","utf-8");
                context.loadDataWithBaseURL(targeturl, htmldata,"text/html","utf-8", baseurl);

            }







        } catch (JSONException e) {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

        }

    }
}