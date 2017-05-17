package com.example.nick.joeyi_android2;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class test_page extends Activity implements View.OnClickListener {

    private Button btn_shopping,btn_moneypv,join,org_intro;
    private String ip="",folder="";
    private SimpleAdapter adapter;
    private HashMap<String,String> item;
    private LinearLayout list_menu;
    private String []title_ct={"獎金查詢","組織查詢","購物專區","線上推薦","我的帳戶","最新訊息","影片介紹","MY QRCODE","聯絡我們"};
    private String []title_en={"BONUS","ORGANIZATION","ORDER ONLINE","RECOMMENDED","MY ACCOUNT","LATEST NEWS","VIDEOS","","CONTACT US"};
    private String []menu_color={"#dea447","#5f90d1","#c29bce","#a9c186","#f79951","#be91cd","#47a9de","#96c564","#e69b3a"};
    private int []menu_bk={R.drawable.btn_001,R.drawable.btn_002,R.drawable.btn_003,R.drawable.btn_004,R.drawable.btn_005,R.drawable.btn_006,R.drawable.btn_007,R.drawable.btn_008,R.drawable.btn_009};
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private FrameLayout mFrameLayout_moneypv,mFrameLayout_org_intro,mFrameLayout_shopping,mFrameLayout_join,mFrameLayout_account,mFrameLayout_news,mFrameLayout_video,mFrameLayout_ours
            ,mFrameLayout_Qrcode;
    ImageView img;
    private ImageButton mImageButton ;
    private ArrayList<String> mArrayList_Mb_No = new ArrayList<String>();
    String config="",mb_no,mb_country,boss_id,login_pwd,id_remember,indate;
    int flag=0;
    int go_tab;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.index_menu_new);
        load_config();
        config = readFromFile("client_config");
        try {
            mb_country = new JSONObject(config).getString("mb_country");
            mb_no = new JSONObject(config).getString("login_id");
            boss_id = new JSONObject(config).getString("boss_id");
            login_pwd = new JSONObject(config).getString("login_pwd");
            id_remember = new JSONObject(config).getString("id_remember");
            indate = new JSONObject(config).getString("indate");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("X5","123"+mb_no);
        img=(ImageView)findViewById(R.id.imageView5);
        mFrameLayout_moneypv = (FrameLayout) findViewById(R.id.moneypv);
        mFrameLayout_org_intro = (FrameLayout) findViewById(R.id.org_intro);
        mFrameLayout_shopping = (FrameLayout) findViewById(R.id.shopping);
        mFrameLayout_join = (FrameLayout) findViewById(R.id.join);
        mFrameLayout_account = (FrameLayout) findViewById(R.id.account);
        mFrameLayout_news = (FrameLayout) findViewById(R.id.news);
        mFrameLayout_video = (FrameLayout) findViewById(R.id.video);
        mFrameLayout_ours = (FrameLayout) findViewById(R.id.ours);
        mFrameLayout_Qrcode = (FrameLayout) findViewById(R.id.Qrcode);
        mImageButton = (ImageButton) findViewById(R.id.btn_chg_mb_no);
        Get_Many_Mb();

/*
        GradientDrawable bgShape1 = (GradientDrawable)mFrameLayout_moneypv.getBackground();
        GradientDrawable bgShape2 = (GradientDrawable)mFrameLayout_org_intro.getBackground();
        GradientDrawable bgShape3 = (GradientDrawable)mFrameLayout_shopping.getBackground();
        GradientDrawable bgShape4 = (GradientDrawable)mFrameLayout_join.getBackground();
        GradientDrawable bgShape5 = (GradientDrawable)mFrameLayout_account.getBackground();
        GradientDrawable bgShape6 = (GradientDrawable)mFrameLayout_news.getBackground();
        GradientDrawable bgShape7 = (GradientDrawable)mFrameLayout_video.getBackground();
//        GradientDrawable bgShape8 = (GradientDrawable)mFrameLayout_qrcode.getBackground();
        GradientDrawable bgShape9 = (GradientDrawable)mFrameLayout_ours.getBackground();

        bgShape1.setColor(getResources().getColor(R.color.moneypv));
        bgShape2.setColor(getResources().getColor(R.color.org));
        bgShape3.setColor(getResources().getColor(R.color.shopping));
        bgShape4.setColor(getResources().getColor(R.color.join));
        bgShape5.setColor(getResources().getColor(R.color.account));
        bgShape6.setColor(getResources().getColor(R.color.news));
        bgShape7.setColor(getResources().getColor(R.color.video));
        //bgShape8.setColor(getResources().getColor(R.color.qrcode));
        bgShape9.setColor(getResources().getColor(R.color.ours));
*/
        mFrameLayout_moneypv.setOnClickListener(btn_moneypv_listener);
        mFrameLayout_org_intro.setOnClickListener(btn_org_intro_listener);
        mFrameLayout_shopping.setOnClickListener(btn_shopping_listener);
        mFrameLayout_join.setOnClickListener(btn_join_listener);
        mFrameLayout_account.setOnClickListener(btn_shopping_listener3);
        mFrameLayout_news.setOnClickListener(btn_news_listener);
        mFrameLayout_video.setOnClickListener(btn_video_listener);
        mFrameLayout_Qrcode.setOnClickListener(btn_qrcode_listener);
        mFrameLayout_ours.setOnClickListener(btn_shopping_listener4);

    }

    void set_onclick(View b,int i){

        if(i==0){
            b.setOnClickListener(btn_moneypv_listener);
        }else if(i==1){
            b.setOnClickListener(btn_org_intro_listener);
        }else if(i==2){
            b.setOnClickListener(btn_shopping_listener);
        }else if(i==3){
            b.setOnClickListener(btn_join_listener);
        }else if(i==4){
            b.setOnClickListener(btn_shopping_listener3);
        }else if(i==5){
            b.setOnClickListener(btn_news_listener);
        }else if(i==6){
            b.setOnClickListener(btn_video_listener);
        }else if(i==7){
            b.setOnClickListener(btn_qrcode_listener);
        }else if(i==8){
            b.setOnClickListener(btn_shopping_listener4);
        }

    }

    private void listDialog(){
        new AlertDialog.Builder(test_page.this)
                .setItems(mArrayList_Mb_No.toArray(new String[mArrayList_Mb_No.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = mArrayList_Mb_No.get(which);
                        JSONObject obj=new JSONObject();
                        try {
                            obj.put("boss_id",boss_id);   //登入帳號
                            obj.put("login_id",name);   //會員編號
                            obj.put("login_pwd",login_pwd);   //登入帳號
                            obj.put("id_remember",id_remember);   //是否記住帳號
                            obj.put("mb_country",mb_country);   //是否記住帳號
                            obj.put("indate",indate);
                            writeToFile("client_config", String.valueOf(obj));
                            //create_thread_country("get_mb_country_s", "get_mb_country_s");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), "切換成功，目前會員編號："+name, Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private Button.OnClickListener btn_org_intro_listener = new Button.OnClickListener() {
        public void onClick(View v) {
            if(!mb_no.equals("none")){
                Intent intent = new Intent(test_page.this, intro_org.class);
                Bundle bundle2 = new Bundle();
                intent.putExtras(bundle2);
                startActivity(intent);
            }else{
                Toast.makeText(getApplication(), "登入後開放", Toast.LENGTH_LONG).show();
            }
        }
    };
    private Button.OnClickListener btn_join_listener = new Button.OnClickListener() {
        public void onClick(View v) {
//            Intent intent = new Intent(test_page.this, join_data.class);
//            Bundle bundle2 = new Bundle();
//            intent.putExtras(bundle2);
//            startActivity(intent);
            Toast.makeText(getApplication(), "尚未開放，敬請期待", Toast.LENGTH_LONG).show();


        }
    };

    private Button.OnClickListener btn_shopping_listener = new Button.OnClickListener() {
        public void onClick(View v) {
//            if(!mb_no.equals("none")){
//                Intent intent = new Intent();
//                intent.setClass(getApplicationContext(), fra3_main.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("go_tab","0");
//                intent.putExtras(bundle);
//                startActivity(intent);
//                finish();
//            }else{
//                Toast.makeText(getApplication(), "登入後開放", Toast.LENGTH_LONG).show();
//            }
            Intent ie = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.joygogo.com.tw/"));
            startActivity(ie);
        }
    };
    private Button.OnClickListener btn_news_listener = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(test_page.this, news2.class);
            Bundle bundle2 = new Bundle();
            intent.putExtras(bundle2);

            startActivity(intent);
        }
    };
    private Button.OnClickListener btn_video_listener = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(test_page.this, video_main.class);
            Bundle bundle2 = new Bundle();
            intent.putExtras(bundle2);

            startActivity(intent);
        }
    };
    private Button.OnClickListener btn_shopping_listener3 = new Button.OnClickListener() {
        public void onClick(View v) {
            if(!mb_no.equals("none")){
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), fra3_main.class);
                Bundle bundle = new Bundle();
                bundle.putString("go_tab", "3");
                intent.putExtras(bundle);
                startActivity(intent);
                //Toast.makeText(getApplication(), "尚未開放，敬請期待", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplication(), "登入後開放", Toast.LENGTH_LONG).show();
            }

        }
    };
    private Button.OnClickListener btn_shopping_listener4 = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(test_page.this, contact2.class);
            startActivity(intent);
        }
    };
    private Button.OnClickListener btn_moneypv_listener = new Button.OnClickListener() {
        public void onClick(View v) {
            if(!mb_no.equals("none")){
                Intent Intentintent = new Intent();
                Intentintent.setClass(getApplicationContext(), money_main.class);
                startActivity(Intentintent);
                //Toast.makeText(getApplication(), "尚未開放，敬請期待", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplication(), "登入後開放", Toast.LENGTH_LONG).show();
            }
        }
    };
    private Button.OnClickListener btn_qrcode_listener = new Button.OnClickListener() {
        public void onClick(View v) {
            /*if(!mb_no.equals("none")){
                Intent intent = new Intent(test_page.this, qr_new.class);
                Bundle bundle2 = new Bundle();
                intent.putExtras(bundle2);
                startActivity(intent);
            }else{*/
                Toast.makeText(getApplication(), "尚未開放，敬請期待", Toast.LENGTH_LONG).show();
            //}

        }
    };
    private Button.OnClickListener Click_Change_Mb_No = new Button.OnClickListener() {
        public void onClick(View v) {
            listDialog();
            Log.e("****","****");
        }
    };
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
                        if (cmd == "get_hisreport") {
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
            mb_no = new JSONObject(config).getString("login_id");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("mb_no",mb_no));
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
        if(input.equals("1\n")){
            Intent Intentintent = new Intent();
            Intentintent.setClass(getApplicationContext(), money_main.class);
            startActivity(Intentintent);
        }else{
            Toast.makeText(getApplication(), " 目前尚無獎金設定", Toast.LENGTH_LONG).show();
        }

    }

    public void Get_Many_Mb() {
        mThread = new HandlerThread("Get_Many_Mb");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = Get_Many_Mb_Data("get_many_mb");
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        try {
                            Get_Many_Mb_Res(jsonString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    private String Get_Many_Mb_Data(String query)
    {
        String result = "";
        try
        {
            mb_no = new JSONObject(config).getString("login_id");
            String boss_id = new JSONObject(config).getString("boss_id");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("boss_id",boss_id));
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
    public final void Get_Many_Mb_Res(String input) throws JSONException {
        Log.e("Get_Many_Mb_Res",input);
        JSONArray jsonArray = new JSONArray(input);
        for(int i =0;i<jsonArray.length();i++){
            mArrayList_Mb_No.add(jsonArray.getJSONObject(i).getString("mb_no"));
        }
        mImageButton.setOnClickListener(Click_Change_Mb_No);
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
    private void writeToFile(String FILENAME, String string) {
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
        }catch (Exception e){
        }
    }

    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Qrcode :
                try {
                    mb_no = new JSONObject(config).getString("login_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(test_page.this, qrcode_main_act.class);
                Bundle bundle2 = new Bundle();
                bundle2.putString("mb_no",mb_no);
                Log.d("Fainet","mb_no+"+mb_no);
                intent.putExtras(bundle2);
                startActivity(intent);
                finish();
                break;

        }
    }
}