package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class intro_org extends Activity implements View.OnTouchListener{
    double scale=3.5;
    private String ip,folder;
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private String mb_no;
    private TextView module;
    private TextView Titlenumber;
    private TextView indate;
    ImageView[][] bt;
    String[][][] mbst;
    int [] level_count;
    int max_level,max_people;
    String session_mb_no,config;
    ImageButton top,up,scale_up,scale_down,home,mode,qa;
    FrameLayout.LayoutParams[][][] layoutParams;
    Boolean loadding_flag=false;
    int level_num=4;
    double px_scale;
    Handler handler=new Handler();
    Runnable runnable;
    LinearLayout go_top,go_back,go_mode;
    FrameLayout go_home;
    private Button button_left,button_right;  //極左極右
    private String get_left,get_right;
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
                    handler.postDelayed(this,2000000);
                    new AlertDialog.Builder(intro_org.this)
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro_org);
        Log.d("scale", String.valueOf(scale));
        load_config();
        config  = readFromFile("client_config");
        button_left = (Button)findViewById(R.id.button_left);
        button_right = (Button)findViewById(R.id.button_right);
        go_top=(LinearLayout)findViewById(R.id.go_top);
        go_back=(LinearLayout)findViewById(R.id.go_back);
        go_mode=(LinearLayout)findViewById(R.id.go_mode);
        go_home=(FrameLayout)findViewById(R.id.go_home);
        indate=(TextView)findViewById(R.id.indate);
        top=(ImageButton)findViewById(R.id.Top);
        up=(ImageButton)findViewById(R.id.up);
        scale_up=(ImageButton)findViewById(R.id.scale_up);
        scale_down=(ImageButton)findViewById(R.id.scale_down);
        home=(ImageButton)findViewById(R.id.home);
        mode=(ImageButton)findViewById(R.id.mode);
        qa=(ImageButton)findViewById(R.id.q);
        module = (TextView)findViewById(R.id.textView47);
        //module.setText("安置");
        String date ="" ;
        try {
            session_mb_no = new JSONObject(config).getString("login_id");
            date=(new JSONObject(config).getString("indate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(date.equals("")){
            indate.setText("");
        } else {
            indate.setText(date);
        }
        //session_mb_no="88888888";

        chk_loadding();
        qa.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                qa_dialog();

            }

        });
        button_left.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mb_no.equals(get_left)) {
                    Toast.makeText(getApplication(), "已到極左", Toast.LENGTH_LONG).show();

                } else {
                    mb_no = get_left;
                    get_org_intro_thread(String.valueOf(level_num));
                }
                //Toast.makeText(intro_org.this,"極左編號為:"+get_left,Toast.LENGTH_SHORT).show();
            }

        });
        button_right.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mb_no.equals(get_right)) {
                    Toast.makeText(getApplication(), "已到極右", Toast.LENGTH_LONG).show();

                } else {
                    mb_no = get_right;
                    get_org_intro_thread(String.valueOf(level_num));
                }
                //Toast.makeText(intro_org.this,"極右編號為:"+get_right,Toast.LENGTH_SHORT).show();
            }

        });

        go_mode.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(intro_org.this, intro_org2.class);
                Bundle bundle2 = new Bundle();
                intent.putExtras(bundle2);


                startActivity(intent);

                finish();


            }

        });
        mode.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(intro_org.this, intro_org2.class);
                Bundle bundle2 = new Bundle();
                intent.putExtras(bundle2);


                startActivity(intent);

                finish();


            }

        });
        home.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(intro_org.this, test_page.class);
                Bundle bundle2 = new Bundle();
                intent.putExtras(bundle2);


                startActivity(intent);

                finish();


            }

        });
        go_home.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(intro_org.this, test_page.class);
                Bundle bundle2 = new Bundle();
                intent.putExtras(bundle2);


                startActivity(intent);

                finish();


            }

        });


        go_top.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mb_no.equals(session_mb_no)) {
                    Toast.makeText(getApplication(), "已到最上方", Toast.LENGTH_LONG).show();

                } else {
                    mb_no = session_mb_no;
                    get_org_intro_thread(String.valueOf(level_num));
                }


            }

        });
        top.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mb_no.equals(session_mb_no)) {
                    Toast.makeText(getApplication(), "已到最上方", Toast.LENGTH_LONG).show();

                } else {
                    mb_no = session_mb_no;
                    get_org_intro_thread(String.valueOf(level_num));
                }


            }

        });
        go_back.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mb_no.equals(session_mb_no)) {
                    Toast.makeText(getApplication(), "已到最上方", Toast.LENGTH_LONG).show();

                } else {
                    get_intro_no_thread(String.valueOf(level_num));
                }

            }

        });
        up.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mb_no.equals(session_mb_no)) {
                    Toast.makeText(getApplication(), "已到最上方", Toast.LENGTH_LONG).show();

                } else {
                    get_intro_no_thread(String.valueOf(level_num));
                }

            }

        });
        scale_up.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(scale>=4.8){
                    scale=4.8;
                }else{
                    scale+=0.6;
                }
                level_num-=2;
                if(level_num<=4){
                    level_num=4;
                }
                Log.d("level_num", String.valueOf(level_num) + "," + scale);
                get_org_intro_thread(String.valueOf(level_num));

            }

        });
        scale_down.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (scale <= 2.4) {
                    scale = 2.4;
                } else {
                    scale -= 0.6;
                    if (scale <= 3) {

                        level_num += 2;
                        if (level_num >= 8) {
                            level_num = 8;
                        }

                    }
                    get_org_intro_thread(String.valueOf(level_num));
                }
                //level_num=(int)(scale/0.9);


                Log.d("level_num", String.valueOf(level_num) + "," + scale);


            }

        });
        // mb_no="TW0000583";//帶入登入者的MB_NO
        mb_no = session_mb_no;
        Get_Line_Intro_Left_Thread("get_intro_no_change_line", "3");



    }

    private void qa_dialog() {
        LinearLayout Top_bar = (LinearLayout)findViewById(R.id.Top_bar2);
        Dialog dialog = new Dialog(this,R.style.MyDialog);//指定自定義樣式
        dialog.setContentView(R.layout.qa);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        lp.y =Top_bar.getHeight(); // 新位置Y坐标
        lp.x = (int)(d.getWidth()/2-d.getWidth() * 0.25); // 新位置x坐标
        dialogWindow.setAttributes(lp);

        //  p.height = (int) (d.getHeight()*0.9); // 高度设置为屏幕的0.6
        //  p.width = (int) (d.getWidth() * 0.5); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);

        dialog.show();

    }

    private void chk_loadding() {
        FrameLayout Fralayout = (FrameLayout)findViewById(R.id.F1);
        LinearLayout LinearLayout = (android.widget.LinearLayout)findViewById(R.id.Top_bar);
        FrameLayout loadding=(FrameLayout)findViewById(R.id.FF1) ;
        button_left.setVisibility(View.INVISIBLE);
        button_right.setVisibility(View.INVISIBLE);
        if (loadding_flag == true){//讀好了
            LinearLayout.setVisibility(View.VISIBLE);
            Fralayout.setVisibility(View.VISIBLE);
            loadding.setVisibility(View.GONE);
           // button_left.setVisibility(View.VISIBLE);
           // button_right.setVisibility(View.VISIBLE);
        }else{
            Fralayout.setVisibility(View.GONE);
            loadding.setVisibility(View.VISIBLE);
            LinearLayout.setVisibility(View.GONE);
        }
    }

    public void get_intro_no_thread(final String cmd){
        loadding_flag=false;
        chk_loadding();
        mThread = new HandlerThread("get_intro_no_thread");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_intro_no(cmd);

                mUI_Handler.post(new Runnable() {
                    public void run() {

                        get_intro_no_res(jsonString);


                    }
                });
            }
        });
    }
    //---------
    public void Get_Line_Intro_Left_Thread(final String cmd,final String Line_Kind){
        mThread = new HandlerThread("");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = Get_Line_Intro_Left(cmd,Line_Kind);

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        Log.d("get_org_intro_res",jsonString);
                        Get_Line_Intro_Left_Res(jsonString,Line_Kind,cmd);
                    }
                });
            }
        });
    }
    public String Get_Line_Intro_Left(String cmd,String Line_Kind){

        String result = "";
        try
        {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            Log.d("fainet","mb"+session_mb_no);
            Log.d("fainet","line"+Line_Kind);
            nameValuePairs.add(new BasicNameValuePair("cmd", cmd));
            nameValuePairs.add(new BasicNameValuePair("mb_no",session_mb_no));
            nameValuePairs.add(new BasicNameValuePair("line",Line_Kind));

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
    public final void Get_Line_Intro_Left_Res(String input,String Lind_kind,String cmd)
    {
        Log.d("input", input);
        String Source_tmp [] = input.split("_");
        Log.d("fainet", String.valueOf(Source_tmp.length));
        if(Lind_kind.equals("3")){
            Get_Line_Intro_Left_Thread(cmd,"4");
            Log.d("fainet", "left");
            Log.d("fainet",Source_tmp[0]);
            get_left = Source_tmp[0];
        } else {
            Log.d("fainet","right");
            Log.d("fainet",Source_tmp[0]);
            get_right = Source_tmp[0];
            get_org_intro_thread(String.valueOf(level_num));
        }
    }
    //---------
    public void get_org_intro_thread(final String cmd){
        loadding_flag=false;
        chk_loadding();
        mThread = new HandlerThread("get_org_intro");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_org_intro(cmd);

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        Log.d("get_org_intro_res",jsonString);
                        get_org_intro_res(jsonString);


                    }
                });
            }
        });
    }
    public String get_org_intro(String cmd){

        String result = "";
        try
        {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/orgseq.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("orgseq","99"));
            nameValuePairs.add(new BasicNameValuePair("org_kind","0"));
            nameValuePairs.add(new BasicNameValuePair("mb_no",mb_no));
            nameValuePairs.add(new BasicNameValuePair("his","-1"));
            nameValuePairs.add(new BasicNameValuePair("true_mb_no",session_mb_no));
            String level= String.valueOf(level_num);
            nameValuePairs.add(new BasicNameValuePair("level_num", level));
            //nameValuePairs.add(new BasicNameValuePair("prevP","1"));





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
    public String get_intro_no(String cmd){
        String result = "";
        try
        {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/orgseq.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("orgseq","99"));
            nameValuePairs.add(new BasicNameValuePair("org_kind","0"));
            nameValuePairs.add(new BasicNameValuePair("mb_no",mb_no));
            nameValuePairs.add(new BasicNameValuePair("his","-1"));
            nameValuePairs.add(new BasicNameValuePair("true_mb_no",session_mb_no));
            nameValuePairs.add(new BasicNameValuePair("prevP","1"));
            String level= String.valueOf(level_num);
            nameValuePairs.add(new BasicNameValuePair("level_num",level));






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
    public final void get_org_intro_res(String input)
    {
        Log.d("input", input);
        try
        {


            JSONArray jsonArray = new JSONArray(input);

            max_level=jsonArray.length();
            max_people=1000;
            mbst = new String[max_level+1][max_people][18];
            level_count=new int [max_level+1];


            for (int i = 0;i < jsonArray.length(); i++)
            {
                JSONArray jsonArray2 = jsonArray.getJSONArray(i);
                level_count[i]=jsonArray2.length();
                if(i==0){
                    max_people=jsonArray2.length();
                }
                if(max_people<jsonArray2.length()){
                    max_people=jsonArray2.length();
                }


                for(int j=0;j<jsonArray2.length();j++){

                    JSONObject jsonData2 = jsonArray2.getJSONObject(j);
                    mbst[i][j][0]=jsonData2.getString("mb_no");//mb_no 會員編號
                    mbst[i][j][1]=jsonData2.getString("count_mb_col");//col_num 格數
                    Log.d("mbst[i][j][1]", mbst[i][j][0] + ":" + mbst[i][j][1]);
                    mbst[i][j][2]=jsonData2.getString("orgseq_no1");//orgseq_no1 推薦基因碼
                    mbst[i][j][3]=jsonData2.getString("down_count");//down_count
                    mbst[i][j][4]=jsonData2.getString("mb_name");//mb_name
                    mbst[i][j][5]=jsonData2.getString("down_intro");//推薦幾位
                    mbst[i][j][6]=jsonData2.getString("count_intro");//推薦總共幾位
                    mbst[i][j][7]=jsonData2.getString("grade_class");//職級
                    mbst[i][j][8]=jsonData2.getString("grade_name");//職級名
                    mbst[i][j][9]=jsonData2.getString("per_m");//業績
                    mbst[i][j][10]=jsonData2.getString("pg_date");//入會日
                    mbst[i][j][11]=jsonData2.getString("org_sum");//小組業績
                    mbst[i][j][12]=jsonData2.getString("true_intro_no");//推薦人編號
                    mbst[i][j][13]=jsonData2.getString("count_mb");//count_mb
                    mbst[i][j][16]=jsonData2.getString("per_sum");//個人累計
                    mbst[i][j][17]=jsonData2.getString("bgrade_class");//職級
                    Log.d("mbst[i][j][6]", mbst[i][j][6]);



                }








                // mbst[max_level-(this_level-level_no)][j][0]=jsonData.getString("mb_no");
                // mbst[max_level-(this_level-level_no)][j][1]=jsonData.getString("mb_name");
                // mbst[max_level-(this_level-level_no)][j][2]=jsonData.getString("level_no");
                // mbst[max_level-(this_level-level_no)][j][3]=jsonData.getString("orgseq_no");
                // mbst[max_level-(this_level-level_no)][j][6]=jsonData.getString("grade_class");
                // last_level=this_level-level_no;

                //   level_count[k]++;

            }

            mbst[max_level-1][0][3]="1";//線頭

            draw_org_intro();







        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Toast.makeText(getApplication(), "" + e.toString(), Toast.LENGTH_LONG).show();
            Log.e("json", e.toString());


        }




    }
    public final void get_intro_no_res(String input)
    {
        Log.d("input", input);
        try
        {

            Log.d("get_intro_no_res", input);
            JSONArray jsonArray = new JSONArray(input);

            max_level=jsonArray.length();
            max_people=1000;
            mbst = new String[max_level+1][max_people][18];
            level_count=new int [max_level+1];


            for (int i = 0;i < jsonArray.length(); i++)
            {
                JSONArray jsonArray2 = jsonArray.getJSONArray(i);
                level_count[i]=jsonArray2.length();
                if(i==0){
                    max_people=jsonArray2.length();
                }
                if(max_people<jsonArray2.length()){
                    max_people=jsonArray2.length();
                }


                for(int j=0;j<jsonArray2.length();j++){

                    JSONObject jsonData2 = jsonArray2.getJSONObject(j);
                    mbst[i][j][0]=jsonData2.getString("mb_no");//mb_no 會員編號
                    mbst[i][j][1]=jsonData2.getString("count_mb_col");//col_num 格數
                    mbst[i][j][2]=jsonData2.getString("orgseq_no1");//orgseq_no1 推薦基因碼
                    mbst[i][j][3]=jsonData2.getString("down_count");//down_count
                    mbst[i][j][4]=jsonData2.getString("mb_name");//mb_name
                    mbst[i][j][5]=jsonData2.getString("down_intro");//推薦幾位
                    mbst[i][j][6]=jsonData2.getString("count_intro");//推薦總共幾位
                    mbst[i][j][7]=jsonData2.getString("grade_class");//職級
                    mbst[i][j][8]=jsonData2.getString("grade_name");//職級名
                    mbst[i][j][9]=jsonData2.getString("per_m");//業績
                    mbst[i][j][10]=jsonData2.getString("pg_date");//入會日
                    mbst[i][j][11]=jsonData2.getString("org_sum");//小組業績
                    mbst[i][j][12]=jsonData2.getString("true_intro_no");//推薦人編號
                    mbst[i][j][13]=jsonData2.getString("count_mb");//count_mb
                    mbst[i][j][16]=jsonData2.getString("per_sum");//count_mb
                    mbst[i][j][17]=jsonData2.getString("bgrade_class");//count_mb

                    if(i==0){
                        mbst[i][j][5]="1";//推薦幾位
                    }else{
                        mbst[i][j][5]=jsonData2.getString("down_intro");//推薦幾位
                    }

                    mbst[i][j][6]=jsonData2.getString("count_intro");//推薦總共幾位
                    mbst[i][j][7]=jsonData2.getString("grade_class");//職級
                    Log.d("mbst[i][j][7]", String.valueOf(mbst[i][j][7]));








                }
            }

            mbst[max_level-1][0][3]="1";//線頭
            mb_no= mbst[max_level-1][0][0];
            draw_org_intro();







        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Toast.makeText(getApplication(), "" + e.toString(), Toast.LENGTH_LONG).show();
            Log.e("json", e.toString());


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    void draw_org_intro(){


        int max_count=max_level;

        double unit_button2;


        bt =new ImageView[max_level][max_people];
        TextView[][] name_img=new TextView[max_level][max_people];
        layoutParams=new FrameLayout.LayoutParams[max_level][max_people][4];
        /////////////////////////////////////////



        final FrameLayout Fralayout = (FrameLayout)findViewById(R.id.F1);
        Fralayout.removeAllViews();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        final int width=metrics.widthPixels;
        int height=metrics.heightPixels;
        px_scale=width/1794;

        if(px_scale>=0.7&&px_scale<1){
            px_scale=0.9;
        }
        if(px_scale<=0.7){
            px_scale=0.8;
        }


        int unit_button=(int) Math.ceil(Math.ceil(width / 36) * (scale * px_scale));
        int unit_button3=(int) Math.ceil(Math.ceil(width / 36) / 2 * (scale * px_scale));
        int x,y;


        if(max_count<=6){
            y=max_count*unit_button;

        }else{
            y=(int) Math.ceil(9.5 * unit_button);
            Log.d("yyyy", String.valueOf(y));
        }
        int x_2,y_2;
        y_2=y;
        for(int i=0;i<max_count;i++){
            x_2= width/max_people;
            for(int j=0;j<level_count[i];j++){
                int count_mb= Integer.parseInt(mbst[i][j][13]);
                x_2+=unit_button*(count_mb);

                int col= Integer.valueOf(mbst[i][j][1])-1;
                double aa=(double)col/2;
                double aaa=aa * unit_button;

                mbst[i][j][14]= String.valueOf(x_2 + (Integer.valueOf(mbst[i][j][1]) - 1) * unit_button - (int) Math.ceil(aaa));
                mbst[i][j][15]= String.valueOf(y_2);



            }
            y_2-=unit_button*13/10;

        }

        int move_x= width/2- Integer.parseInt(mbst[max_count - 1][0][14])-unit_button3;
        int move_y=0;
        Log.d("xxx,yyy", mbst[max_count - 1][0][14] + "," + mbst[max_count - 1][0][15]);
        Log.d("move_x", String.valueOf(move_x));
        if(move_x<0&&max_people!=1){
            move_x=0;
        }
        if(Integer.parseInt(mbst[max_count - 1][0][15])<0){
            move_y= Integer.parseInt(mbst[max_count - 1][0][15]);
        }

        ////////////////////////////////////////
        y-=move_y;
        for(int i=0;i<max_count;i++){

            x= width/max_people+move_x;
            Log.d("max_people", String.valueOf(x));
            // x=2*unit_button;
            for(int j=0;j<level_count[i];j++){


                int count_mb= Integer.parseInt(mbst[i][j][13]);
                x+=unit_button*(count_mb);



                bt[i][j]=new ImageView(this);//動態產生一個人頭
                final String this_mb_no=mbst[i][j][0];
                bt[i][j].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        /**震动服务*/
                        Vibrator vib = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);


                        mb_no = this_mb_no;
                        if (mb_no == mbst[max_level - 1][0][0] && !mb_no.equals(session_mb_no)) {
                            vib.vibrate(50);//只震动一秒，一次
                            get_intro_no_thread(String.valueOf(level_num));
                        } else if (mb_no.equals(session_mb_no)) {
                            Toast.makeText(getApplication(), "已到最上方", Toast.LENGTH_LONG).show();
                            return true;
                        } else {
                            vib.vibrate(50);//只震动一秒，一次
                            get_org_intro_thread(String.valueOf(level_num));
                        }

                        return true;
                    }


                });


                String grade_tmp;
                if(Integer.valueOf(mbst[i][j][17]) > Integer.valueOf(mbst[i][j][7])){
                    grade_tmp = mbst[i][j][17];
                }else{
                    grade_tmp = mbst[i][j][7];
                }


                if(grade_tmp.equals("0")){
                    bt[i][j].setImageDrawable(getResources().getDrawable(R.drawable.g003));
                }
                else if(grade_tmp.equals("5")){
                    bt[i][j].setImageDrawable(getResources().getDrawable(R.drawable.g005));
                }
                else if(grade_tmp.equals("10")){
                    bt[i][j].setImageDrawable(getResources().getDrawable(R.drawable.g008));
                }
                else if(grade_tmp.equals("15")){
                    bt[i][j].setImageDrawable(getResources().getDrawable(R.drawable.g004));
                }
                else if(grade_tmp.equals("20")){
                    bt[i][j].setImageDrawable(getResources().getDrawable(R.drawable.g023));
                }
                else if(grade_tmp.equals("25")){
                    bt[i][j].setImageDrawable(getResources().getDrawable(R.drawable.g025));
                }
                else if(grade_tmp.equals("30")){
                    bt[i][j].setImageDrawable(getResources().getDrawable(R.drawable.g021));
                }
                else if(grade_tmp.equals("35")){
                    bt[i][j].setImageDrawable(getResources().getDrawable(R.drawable.g026));
                }
                else if(grade_tmp.equals("40")){
                    bt[i][j].setImageDrawable(getResources().getDrawable(R.drawable.g027));
                }else{
                    bt[i][j].setImageDrawable(getResources().getDrawable(R.drawable.g_1));
                }

                bt[i][j].setScaleType(ImageView.ScaleType.FIT_XY);

                layoutParams[i][j][0]= new FrameLayout.LayoutParams(unit_button3,unit_button3);
                layoutParams[i][j][0].gravity = Gravity.LEFT| Gravity.TOP;
                layoutParams[i][j][0].topMargin =y;//設定人頭Y座標


                int col= Integer.valueOf(mbst[i][j][1])-1;
                double aa=(double)col/2;
                double aaa=aa * unit_button;

                layoutParams[i][j][0].leftMargin =x+(Integer.valueOf(mbst[i][j][1])-1)*unit_button-(int) Math.ceil(aaa);//設定人頭Ｘ座標
                int newx=x+(Integer.valueOf(mbst[i][j][1])-1)*unit_button-(int) Math.ceil(aaa);
                Log.d("pxy", x + (Integer.valueOf(mbst[i][j][1]) - 1) * unit_button - (int) Math.ceil(aaa) + "," + y);


                name_img[i][j]=new TextView(this);
                name_img[i][j].setTextColor(Color.argb(999, 55, 55, 55));
                name_img[i][j].setTextSize((int) Math.ceil(4 * (scale * px_scale)));


                //name_img[i][j].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                layoutParams[i][j][2]= new FrameLayout.LayoutParams(unit_button3*2,unit_button3*2/3);
                layoutParams[i][j][2].gravity = Gravity.LEFT| Gravity.TOP;
                layoutParams[i][j][2].topMargin =y+(int)(unit_button3);//設定名字Y座標
                //  name_img[i][j].setBackgroundColor(Color.DKGRAY);
                name_img[i][j].setLayoutParams(layoutParams[i][j][2]);
                name_img[i][j].setGravity(Gravity.CENTER);
                Bitmap newb = Bitmap.createBitmap(unit_button3 * 2, unit_button3, Bitmap.Config.ARGB_8888);
                Bitmap newb2 = Bitmap.createBitmap(unit_button3 * 2, unit_button3, Bitmap.Config.ARGB_8888);
                Canvas canvasTemp = new Canvas( newb );
                Canvas canvasTemp2 = new Canvas( newb2 );
                Paint paint = new Paint();
                Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
                paint.setTypeface(font);
                paint.setColor(Color.BLUE);
                paint.setTextSize((int) Math.ceil(10 * (scale * px_scale)));
                paint.setTextAlign(Paint.Align.CENTER);
                Paint.FontMetrics fontMetrics = paint.getFontMetrics();
// 计算文字高度
                float fontHeight = fontMetrics.bottom - fontMetrics.top;
// 计算文字baseline
                float textBaseY = unit_button3 - (unit_button3 - fontHeight) / 2 - fontMetrics.bottom;
                String temp_name;
                int len=0;
                char[] name = mbst[i][j][4].toCharArray();
                for(int l=0;l<mbst[i][j][4].length();l++){
                    Character.UnicodeBlock ub = Character.UnicodeBlock.of(name[l]);
                    if (   ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                            || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                        //|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                        //|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                        //|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                        //|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                            )
                    //|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION)
                    {
                        len++;
                    }else{
                        len+=0.5;
                    }
                }

                if(len>=4){
                    name_img[i][j].setTextSize((int) (4 * (scale*px_scale)));
                    temp_name=mbst[i][j][4].substring(0,4);
                }else{
                    name_img[i][j].setTextSize((int) (4 * (scale*px_scale)));
                    temp_name=mbst[i][j][4];
                }
                layoutParams[i][j][2].leftMargin =x+(Integer.valueOf(mbst[i][j][1])-1)*unit_button-(int)aaa-unit_button3/2;//設定名字Ｘ座標
                Log.d("mbname", temp_name + ":(" + newx + "," + y + ")");
                canvasTemp.drawText(temp_name, unit_button3 * 2 / 2, textBaseY, paint);
                //name_img[i][j].setImageBitmap(newb);
                name_img[i][j].setText(temp_name);
                name_img[i][j].setTypeface(name_img[i][j].getTypeface(), Typeface.NORMAL);
                ImageView up_line=new ImageView(this);
                ImageView down_line=new ImageView(this);
                TextView down_line_count=new TextView(this);
                down_line_count.setTextColor(Color.argb(999, 55, 55, 55));

                down_line_count.setTextSize((int) (4 * (scale * px_scale)));
                down_line_count.setTypeface(down_line_count.getTypeface(), Typeface.NORMAL);
                unit_button2=unit_button3/2;
                int yyy;
                yyy=unit_button3*3;
                if(i!=max_level-1){
                    if(mbst[i][j][3].equals("1")){//唯一
                        up_line.setImageDrawable(getResources().getDrawable(R.drawable.up));
                    }else if(mbst[i][j][3].equals("2")){//第一顆
                        up_line.setImageDrawable(getResources().getDrawable(R.drawable.lline2));
                    }else if(mbst[i][j][3].equals("3")){//第2顆
                        up_line.setImageDrawable(getResources().getDrawable(R.drawable.rline2));
                    }else{
                        up_line.setImageDrawable(getResources().getDrawable(R.drawable.cline2));
                    }
                }
                layoutParams[i][j][1]= new FrameLayout.LayoutParams(unit_button3,yyy);
                layoutParams[i][j][1].gravity = Gravity.LEFT| Gravity.TOP;

                layoutParams[i][j][1].topMargin =y-(int)(unit_button2*4.3);//設定畫往上線圖片Y座標

                if(mbst[i][j][3].equals("1")){//唯一
                    layoutParams[i][j][1].topMargin =y-(int)(unit_button2*4.5);//設定畫往上線圖片Y座標
                }
                layoutParams[i][j][1].leftMargin =x+(Integer.valueOf(mbst[i][j][1])-1)*unit_button-(int)aaa;//設定畫往上線圖片Ｘ座標
                int yy=0,yy2=0,text_x;
                down_line.setScaleType(ImageView.ScaleType.FIT_XY);
                text_x=unit_button3;
                if(Integer.valueOf(mbst[i][j][5])>0) {
                    down_line.setImageDrawable(getResources().getDrawable(R.drawable.downd));
                    yy=unit_button3*3/2;
                    down_line.setScaleType(ImageView.ScaleType.FIT_XY);
                    yy2=y+(int)(unit_button2*1.7);//設定畫往下線圖片Y座標  (|)
                }


                if(Integer.valueOf(mbst[i][j][5])>0&&i==0){
                    yy=unit_button3;
                    text_x=unit_button3*3/2;
                    if(mbst[i][j][8].length()<=3) {
                        down_line_count.setTextSize((int) (4 * (scale * px_scale)));
                    }else{
                        down_line_count.setTextSize((int) (4 * (scale * px_scale)));
                    }
                    Log.d("setTextSize", String.valueOf(paint.getTextSize()));
                    // canvasTemp2.drawText("(" + mbst[i][j][6] + ")", unit_button3 * 2 / 2, textBaseY, paint);
                    down_line.setImageBitmap(newb2);
                    int cou= Integer.parseInt(mbst[i][j][6]);
                    if((cou)>0){
                        down_line_count.setText("(" + mbst[i][j][6] + ")");
                    }
                    yy2=y+(int)(unit_button2*6/2);//設定畫往下線圖片Y座標   (8)
                }

                up_line.setScaleType(ImageView.ScaleType.FIT_XY);
                layoutParams[i][j][3]= new FrameLayout.LayoutParams(text_x,yy);
                layoutParams[i][j][3].gravity = Gravity.LEFT| Gravity.TOP;

                layoutParams[i][j][3].topMargin = yy2;//設定畫往下線圖片Y座標
                if(i!=0){
                    layoutParams[i][j][3].leftMargin =x+(Integer.valueOf(mbst[i][j][1])-1)*unit_button-(int)aaa;//設定畫往上線圖片Ｘ座標
                }else{
                    layoutParams[i][j][3].leftMargin =x+(Integer.valueOf(mbst[i][j][1])-1)*unit_button-(int)aaa-unit_button3/4;//設定畫往上線圖片Ｘ座標

                }

                bt[i][j].setLayoutParams(layoutParams[i][j][0]);



                up_line.setLayoutParams(layoutParams[i][j][1]);
                if(i!=0){
                    down_line.setLayoutParams(layoutParams[i][j][3]);

                }else{
                    down_line_count.setLayoutParams(layoutParams[i][j][3]);
                    //down_line_count.setBackgroundColor(Color.DKGRAY);
                    down_line_count.setGravity(Gravity.CENTER);
                }
                int x2=x;
                int x3=x;
                int last_num= Integer.valueOf(mbst[i][j][3]);
                if(last_num!=1&&last_num!=3&&i!=max_level-1){//線頭跟最後一顆不劃痕線

                    Log.d("unit_button3", String.valueOf(unit_button3));
                    // for(int k=0;k<1;k++){
                    //Log.d("mbst","tttt"+mbst[i][j + 1][1]);
                    if(mbst[i][j + 1][1]==null){
                        Toast.makeText(this,"組織圖異常請聯絡系統管理員將強制離開此頁面",Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                    for(int k=0;k< Integer.valueOf(mbst[i][j][1])+ Integer.valueOf(mbst[i][j + 1][1])-1;k++){
                        if(i==2){
                            Log.d("test", "draw_org_intro ");
                        }
                        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams((unit_button3), unit_button3*3);
                        layoutParams2.gravity = Gravity.LEFT| Gravity.TOP;
                        layoutParams2.topMargin =y-(int)(unit_button2)*43/10;
                        int linex,liney;
                        liney=y-(int)(unit_button2)*50/10;
                        layoutParams2.leftMargin =x2+(Integer.valueOf(mbst[i][j][1])-1)*unit_button-(int)aaa+unit_button3;//設定畫往上線圖片Ｘ座標
                        linex=x2+(Integer.valueOf(mbst[i][j][1])-1)*unit_button-(int)aaa+unit_button3;

                        Log.d("pxy", "從" + mbst[i][j][0] + "開始的線 第" + (k + 1) + "條 座標 (" + linex + "," + liney + ")");
                        ImageView up_line2=new ImageView(this);
                        up_line2.setScaleType(ImageView.ScaleType.FIT_XY);
                        up_line2.setImageDrawable(getResources().getDrawable(R.drawable.hline2));
                        up_line2.setLayoutParams(layoutParams2);
                        Fralayout.addView(up_line2);
                        x2+=unit_button3;

                    }
                }
                if(last_num!=1&&last_num!=2){
                /*    for(int k=0;k<(Integer.valueOf(mbst[i][j][1])+Integer.valueOf(mbst[i][j-1][1])-1);k++){
                        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams((int)(unit_button3*1.1), unit_button3*3);
                        Log.d("unit_button3*1.1", String.valueOf(unit_button3*1.1));
                        layoutParams2.gravity = Gravity.LEFT|Gravity.TOP;
                        layoutParams2.topMargin =y-(int)(unit_button2)*43/10;
                        //layoutParams2.leftMargin =x3+(Integer.valueOf(mbst[i][j][1])-1)*unit_button-(int)aaa-unit_button3;//設定畫往上線圖片Ｘ座標
                        layoutParams2.leftMargin =x3+(Integer.valueOf(mbst[i][j][1])-1)*unit_button-(int)aaa-unit_button3;//設定畫往上線圖片Ｘ座標
                        ImageView up_line2=new ImageView(this);
                        up_line2.setScaleType(ImageView.ScaleType.FIT_XY);
                        up_line2.setImageDrawable(getResources().getDrawable(R.drawable.hline2));
                        up_line2.setLayoutParams(layoutParams2);
                        Fralayout.addView(up_line2);
                        x3-=unit_button3;
                    }*/
                }



                x+= Integer.valueOf(mbst[i][j][1])*unit_button;





                Fralayout.addView(bt[i][j]);
                Fralayout.addView(down_line);
                Fralayout.addView(up_line);
                Fralayout.addView(down_line_count);
                Fralayout.addView(name_img[i][j]);

                final ImageView bt2=bt[i][j];
                final int t_i=i;
                final int t_j=j;
                bt[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        myListAlertDialog(bt2,width,Fralayout,t_i,t_j);



                    }
                });

            }
            y-=unit_button*13/10;


        }
        TextView tv1=(TextView)findViewById(R.id.textView27);
//        tv1.setText("   "+mb_no+" <推薦圖>   ");
        tv1.setText("推薦組織");
        loadding_flag=true;
        chk_loadding();

    }
    private void myListAlertDialog(ImageView btbt,int width,FrameLayout Fralayout, int i,int j) {


        LinearLayout Top_bar = (LinearLayout)findViewById(R.id.Top_bar2);
        Log.d("Top_bar.getHeight", String.valueOf(Top_bar.getHeight()));

        //  Dialog dialog = new Dialog(this);
        Dialog dialog = new Dialog(this,R.style.MyDialog);//指定自定義樣式

        // setContentView可以设置为一个View也可以简单地指定资源ID
        // LayoutInflater
        // li=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        // View v=li.inflate(R.layout.dialog_layout, null);
        // dialog.setContentView(v);
        dialog.setContentView(R.layout.alert);
        TextView mb_no_tv=(TextView)dialog.findViewById(R.id.textView17);
        TextView mb_name_tv=(TextView)dialog.findViewById(R.id.textView18);
        TextView mb_grade_tv=(TextView)dialog.findViewById(R.id.textView20);
        TextView mb_per_m_tv=(TextView)dialog.findViewById(R.id.textView22);
        TextView mb_org_m_tv=(TextView)dialog.findViewById(R.id.textView24);
        TextView mb_pgdate_tv=(TextView)dialog.findViewById(R.id.textView26);
        TextView mb_per_sum=(TextView)dialog.findViewById(R.id.textView108);
        mb_no_tv.setText(mbst[i][j][0]);
        mb_name_tv.setText(mbst[i][j][4]);
        mb_grade_tv.setText(mbst[i][j][8]);
        mb_per_m_tv.setText(mbst[i][j][9]);
        mb_org_m_tv.setText(mbst[i][j][11]);
        mb_pgdate_tv.setText(mbst[i][j][10]);
        mb_per_sum.setText(mbst[i][j][16]);


        /*
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置,
         * 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);

        /*
         * lp.x与lp.y表示相对于原始位置的偏移.
         * 当参数值包含Gravity.LEFT时,对话框出现在左边,所以lp.x就表示相对左边的偏移,负值忽略.
         * 当参数值包含Gravity.RIGHT时,对话框出现在右边,所以lp.x就表示相对右边的偏移,负值忽略.
         * 当参数值包含Gravity.TOP时,对话框出现在上边,所以lp.y就表示相对上边的偏移,负值忽略.
         * 当参数值包含Gravity.BOTTOM时,对话框出现在下边,所以lp.y就表示相对下边的偏移,负值忽略.
         * 当参数值包含Gravity.CENTER_HORIZONTAL时
         * ,对话框水平居中,所以lp.x就表示在水平居中的位置移动lp.x像素,正值向右移动,负值向左移动.
         * 当参数值包含Gravity.CENTER_VERTICAL时
         * ,对话框垂直居中,所以lp.y就表示在垂直居中的位置移动lp.y像素,正值向右移动,负值向左移动.
         * gravity的默认值为Gravity.CENTER,即Gravity.CENTER_HORIZONTAL |
         * Gravity.CENTER_VERTICAL.
         *
         * 本来setGravity的参数值为Gravity.LEFT | Gravity.TOP时对话框应出现在程序的左上角,但在
         * 我手机上测试时发现距左边与上边都有一小段距离,而且垂直坐标把程序标题栏也计算在内了,
         * Gravity.LEFT, Gravity.TOP, Gravity.BOTTOM与Gravity.RIGHT都是如此,据边界有一小段距离
         */
        int[] location = new int[2];
        btbt.getLocationOnScreen(location);
        if(location[0]>width/2){
            lp.x = 0; // 新位置X坐标
        }else{
            lp.x = width;
        }


        lp.y =Top_bar.getHeight(); // 新位置Y坐标
        // lp.width = 300; // 宽度
        // lp.height = 300; // 高度
        //lp.alpha = 0.7f; // 透明度

        // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
        // dialog.onWindowAttributesChanged(lp);
        dialogWindow.setAttributes(lp);

        /*
         * 将对话框的大小按屏幕大小的百分比设置
         */
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        // p.height = (int) (d.getHeight() * 0.4); // 高度设置为屏幕的0.6
        //  p.width = (int) (d.getWidth() * 0.2); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);
        dialog.show();

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d("onTouch", "onTouch ");

        return true;
    }
    private String readFromFile(String FILENAME) {
        try{
            FileInputStream fin = openFileInput(FILENAME);
            byte[] buff = new byte[fin.available()];
            fin.read(buff);
            String str = new String(buff);


            Log.d("readFromFile", str);
            return str;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}