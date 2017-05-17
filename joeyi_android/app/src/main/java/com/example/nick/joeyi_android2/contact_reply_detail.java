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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by root on 11/25/15.
 */
public class contact_reply_detail extends Activity {
    private String ip = "", folder = "";
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Handler mUI_Handler = new Handler();
    private String news_no,title,post_time,content;
    private TextView title_tv,post_date_tv,post_contact_tv;
    private WebView context;
    String config="",login_id;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隱藏logo
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        setContentView(R.layout.dis_contact_detail);
        load_config();
        config  = readFromFile("client_config");
        Bundle bundle = this.getIntent().getExtras();
        news_no = bundle.getString("news_no");
        //title = bundle.getString("title");
        //content = bundle.getString("content");
       // post_time = bundle.getString("post_time");
        title_tv=(TextView)findViewById(R.id.dis_title_detail);
        post_contact_tv=(TextView)findViewById(R.id.post_contact_detail);
        post_date_tv=(TextView)findViewById(R.id.post_date_detail);
        context=(WebView)findViewById(R.id.context);
        get_title_thread();
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
    private String readFromFile(String FILENAME) {
        try{
            FileInputStream fin = getApplication().openFileInput(FILENAME);
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
    public void get_title_thread() {
        mThread = new HandlerThread("get_contact_data");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = title_data();

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        renew_title_data(jsonString);

                    }
                });
            }
        });
    }

    private String title_data() {
        String result = "";
        try {
            login_id = new JSONObject(config).getString("login_id");
            HttpClient httpClient = new DefaultHttpClient();
            //HttpPost post = new HttpPost("http://220.135.161.128/leo/android_sql.php");
            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", "get_contact_data"));
            nameValuePairs.add(new BasicNameValuePair("id",news_no));
            nameValuePairs.add(new BasicNameValuePair("mb_no",login_id));
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

    public final void renew_title_data(String input) {
        Log.d("input",input);
        try {

            JSONArray jsonArray = new JSONArray(input);
            JSONObject jsonData = jsonArray.getJSONObject(0);
            title_tv.setText(jsonData.getString("title"));
            post_date_tv.setText(jsonData.getString("post_time"));
            post_contact_tv.setText(jsonData.getString("content"));

        } catch (JSONException e) {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

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
            nameValuePairs.add(new BasicNameValuePair("cmd", "send_contact_data_detail"));
            nameValuePairs.add(new BasicNameValuePair("id",news_no));
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
            //title_tv.setText(title);
            //post_date_tv.setText(post_time);
            //post_contact_tv.setText(content);
            if(input.equals("1\n")){

                String html1="目前尚未回覆";
                html1=html1.replace("width", "width1");
                html1=html1.replace("height", "width1");
                html1=html1.replace(" style=\"", " style=\" width:100%; ");

                Log.d("html1", html1);

                String htmldata="<style>\n" +
                        "body {line-height:30px}\n"+
                        "</style>"+html1;//网页代码

                String targeturl="";//目标网址（具体）
                String baseurl="";//连接目标网址失败进入的默认网址
                context.getSettings().setDefaultTextEncodingName("GB2312");
                context.loadData(htmldata,"text/html","utf-8");
                context.loadDataWithBaseURL(targeturl, htmldata,"text/html","utf-8", baseurl);

            }else {
                JSONArray jsonArray = new JSONArray(input);


                String html1 = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    if((i%2)==1){
                        html1 = html1 + "  <tr>\n" +
                                "    <td>\n" +
                                "    客服回覆:<br />" +jsonData.getString("content")+
                                "    <div id=\"ti\">回覆日期："+jsonData.getString("post_time")+"</div>\n" +
                                "    </td>\n" +
                                "  </tr>";
                        //background-color:#FFBB73;
                    }else{
                        html1 = html1 + "  <tr class=\"rowStyle\">\n" +
                                "    <td>\n" +
                                "    客服回覆:<br />" +jsonData.getString("content")+
                                "    <div id=\"ti\">回覆日期："+jsonData.getString("post_time")+"</div>\n" +
                                "    </td>\n" +
                                "  </tr>";
                    }

                    //html1 = html1 + jsonData.getString("content")+"</br><span style=\'float:right;\' >回覆時間："+jsonData.getString("post_time")+"&nbsp; </span></br>";
                   // html1 = html1.replace("width", "width1");
                   // html1 = html1.replace("height", "width1");
                   // html1 = html1.replace(" style=\"", " style=\" width:100%; ");
                   // if((i%2)==1){
                     //   html1 = html1 + "</div>";
                   // }
                    Log.d("html1", html1);
                }
                    String htmldata = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head>\n" +
                            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                            "<title>Untitled Document</title>\n" +
                            "<style type=\"text/css\">\n" +
                            "<!--\n" +
                            "body,div,dl,dt,dd,ul,ol,li,h1,h2,h3,h4,h5,h6,pre,form,fieldset,input,p,blockquote,th,td{margin:0;padding:0;}\n" +
                            "\n" +
                            "\n" +
                            "table.sev {\n" +
                            "\tborder-collapse:collapse;\n" +
                            "\twidth: 100%;\n" +
                            "\tfont: 15px  arial, Microsoft JhengHei;\n" +
                            "\n" +
                            "\t\n" +
                            "}\n" +
                            "\n" +
                            "table.sev th{\n" +
                            "\tpadding:4px 10px 4px 10px;\n" +
                            "  \ttext-align:left;\n" +
                            "\tcolor:#543c0a;\n" +
                            "\tbackground-color: #f6e4be;\n" +
                            "\tline-height: 30px;\n" +
                            "\tborder: 0px;\n" +
                            "\tborder-bottom:1px #d2c5a8 solid;\n" +
                            "\t\n" +
                            "}\n" +
                            "\n" +
                            "\n" +
                            "table.sev td{\n" +
                            "\tpadding:4px 10px 4px 10px;\n" +
                            "  \ttext-align:left;\n" +
                            "\tcolor:#555555;\n" +
                            "\tline-height: 30px;\n" +
                            "\tborder: 0px;\n" +
                            "\tborder-bottom:1px #cccccc solid;\n" +
                            "}\n" +
                            "\n" +
                            "table.sev tr.rowStyle{\n" +
                            "\tbackground:#fffbe5;\n" +
                            "}\n" +
                            "\n" +
                            "#ti{\n" +
                            "\ttext-align:right;\n" +
                            "\tmargin-right:8px;\n" +
                            "\t}\n" +
                            "\n" +
                            "-->\n" +
                            "</style>\n" +
                            "\n" +
                            "</head>\n" +
                            "\n" +
                            "<body><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"sev\">" + html1+"</table>\n</html>" +
                            "\n" +
                            "</body>";//网页代码


                    String targeturl = "";//目标网址（具体）
                    String baseurl = "";//连接目标网址失败进入的默认网址
                context.getSettings().setDefaultTextEncodingName("GB2312");
                    //context.getSettings().setUseWideViewPort(true);
                    //context.getSettings().setLoadWithOverviewMode(true);
                    context.loadData(htmldata, "text/html", "utf-8");
                    context.loadDataWithBaseURL(targeturl, htmldata, "text/html", "utf-8", baseurl);


            }

        } catch (JSONException e) {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

        }

    }
}