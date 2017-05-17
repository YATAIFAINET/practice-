package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.SimpleAdapter;

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
import java.util.HashMap;

/**
 * Created by Nick on 2015/11/24.
 */
public class product_shopping_noti extends Activity {

    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;

    private SimpleAdapter adapter;
    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> item;
    String config="";

    String ip="",folder="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_shopping_noti);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        load_config();

        create_thread("get_shopping_rule", "get_shopping_rule");
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
                        if (cmd == "get_shopping_rule") {
                            newPayname(jsonString);
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
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
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
    public final void newPayname(String input)
    {
        Log.d("fff",input);
        try
        {
            JSONArray jsonArray = new JSONArray(input);
            JSONObject jsonData = jsonArray.getJSONObject(0);
            list = new ArrayList<HashMap<String,String>>();
            if(jsonArray.length()<1 || jsonData.getString("content").length()<1 || jsonData.getString("content").equals("null")){
                WebView webv=(WebView)findViewById(R.id.product_shopping_noti);
                String htmldata="後臺建置中";//网页代码
                String targeturl="";//目标网址（具体）
                String baseurl="";//连接目标网址失败进入的默认网址
                webv.getSettings().setDefaultTextEncodingName("GB2312");
                webv.loadData(htmldata,"text/html","utf-8");
                webv.loadDataWithBaseURL(targeturl, htmldata, "text/html","utf-8", baseurl);
            }else{
                WebView webv=(WebView)findViewById(R.id.product_shopping_noti);



                String html1=jsonData.getString("content");
                html1=html1.replace("width", "width1");
                html1=html1.replace("height", "width1");
                html1=html1.replace(" style=\"", " style=\" width:100%; ");


                String targeturl="";//目标网址（具体）
                String baseurl="";//连接目标网址失败进入的默认网址
                webv.getSettings().setDefaultTextEncodingName("GB2312");
                webv.loadData(html1,"text/html","utf-8");
                webv.loadDataWithBaseURL(targeturl, html1, "text/html","utf-8", baseurl);
            }
            /*if(jsonArray.length()>0 && jsonData.getString("mprod_rule").length()>0) {
                WebView webv=(WebView)findViewById(R.id.product_standard);
                String htmldata=jsonData.getString("mprod_rule");//网页代码
                String targeturl="";//目标网址（具体）
                String baseurl="";//连接目标网址失败进入的默认网址
                webv.getSettings().setDefaultTextEncodingName("GB2312");
                webv.loadData(htmldata,"text/html","utf-8");
                webv.loadDataWithBaseURL(targeturl, htmldata, "text/html","utf-8", baseurl);

            }else{
                WebView webv=(WebView)findViewById(R.id.product_standard);
                String htmldata="目前尚無詳細說明";//网页代码
                String targeturl="";//目标网址（具体）
                String baseurl="";//连接目标网址失败进入的默认网址
                webv.getSettings().setDefaultTextEncodingName("GB2312");
                webv.loadData(htmldata,"text/html","utf-8");
                webv.loadDataWithBaseURL(targeturl, htmldata, "text/html","utf-8", baseurl);
            }*/

        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

        }
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
}
