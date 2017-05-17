package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Menu;
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
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ecash_cost_qr extends Activity {
    String ip = "", folder = "",config="",login_id;
    private WebView qr_view;
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Handler mUI_Handler = new Handler();
    private TextView eff_date;
    private MyCount mc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        setContentView(R.layout.activity_ecash_cost_qr);
        //设置屏幕方向为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        load_config();
        eff_date=(TextView)findViewById(R.id.eff_time);
        config  = readFromFile("client_config");
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null && bundle.containsKey("mb_no"))
        {
            login_id = bundle.getString("mb_no");

        }else{

            try {
                login_id = new JSONObject(config).getString("login_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        get_qr_data();
        setTitle("電子錢包付款");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ecash_cost_qr, menu);
        return true;
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
    public void get_qr_data() {
        final String cmd="get_qr_data";
        mThread = new HandlerThread("get_qr_data");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_qr_data_q(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {

                        get_qr_data_res(jsonString);
                    }

                });
            }
        });
    }
    private String get_qr_data_q(String query)
    {
        String result = "";
        try
        {
            login_id = new JSONObject(config).getString("login_id");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd","get_ecash_qrcode"));
            nameValuePairs.add(new BasicNameValuePair("mb_no",login_id));

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
    public final void get_qr_data_res(String input)
    {
        Log.d("fainet","input"+input);
        if(input.equals("{0}\n")){
            qr_view=(WebView)findViewById(R.id.qr_view);


            String htmldata="餘額不足";//网页代码
            String targeturl="";//目标网址（具体）
            String baseurl="";//连接目标网址失败进入的默认网址
            qr_view.getSettings().setDefaultTextEncodingName("GB2312");
            qr_view.loadData(htmldata, "text/html", "utf-8");
            qr_view.loadDataWithBaseURL(targeturl, htmldata, "text/html", "utf-8", baseurl);
            eff_date.setText("00:00");
        }else{

            Log.d("input0523", input);

            String qr_text="";
            qr_text=input;
            String[] temp = input.split("@@");
            TextView text=(TextView)findViewById(R.id.can_point);
            text.setText(temp[3]);
            qr_text=xorEnc(8,qr_text);
            qr_text=qr_text.replace("%","ytt");
            qr_view=(WebView)findViewById(R.id.qr_view);
            String url2 = "https://chart.googleapis.com/chart?chs=" + "265x265" + "&cht=qr&chl=" + "yatai$@"+qr_text + "&choe=UTF-8&chld=M|2";
            String html1="<div align=\"center\"><img style='width:100%;' alt='Your QRcode' src='"+url2+"' /></div>";

            Log.d("html1", html1);
            String htmldata=html1;//网页代码
            String targeturl="";//目标网址（具体）
            String baseurl="";//连接目标网址失败进入的默认网址
            qr_view.getSettings().setDefaultTextEncodingName("GB2312");
            qr_view.loadData(htmldata, "text/html", "utf-8");
            qr_view.loadDataWithBaseURL(targeturl, htmldata, "text/html", "utf-8", baseurl);
            //CountDownTimer
            mc = new MyCount(300000, 1000);
            mc.start();

        }




    }
    /*定义一个倒计时的内部类*/
    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onFinish() {
            eff_date.setText("已失效,請重新開啟");
        }
        @Override
        public void onTick(long millisUntilFinished) {
            String time="";
            time=get_time((millisUntilFinished));
            eff_date.setText(time);
        }
    }
    public String get_time(long mss){

        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        return minutes + "：" + seconds;

    }
    public String xorEnc(int encKey, String toEnc) {
    /*
        Usage: str = xorEnc(integer_key,string_to_encrypt);
        Created by Matthew Shaffer (matt-shaffer.com)
    */
        int t=0;
        String s1="";
        String tog="";
        if(encKey>0) {
            while(t < toEnc.length()) {
                int a=toEnc.charAt(t);
                int c=a ^ encKey;
                char d=(char)c;
                tog=tog+d;
                t++;
            }

        }
        return tog;
    }

}