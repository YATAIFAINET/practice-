package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

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

/**
 * Created by YTT1 on 2016/11/17.
 */
//----------------------------------------------------------------------------
public class qrcode_main_act extends Activity implements View.OnClickListener {

    private String TAG = "Fainet";
    private ImageView mQrCode_Img ;
    private TextView mmb_sta ;
    private String mb_no="",intro_code="";
    String ip="",folder="";
    private QrCode_Option mQrCode_Option=new QrCode_Option();

    private ProgressDialog mDialog;
    private android.os.Handler mUI_Handler = new android.os.Handler();
    private HandlerThread mThread;
    private android.os.Handler mThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
       // this.requestWindowFeature(Window.FEATURE_NO_TITLE);

       // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
       //         WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.qrcode_main_act);

        Bundle bundle =this.getIntent().getExtras();

        if (bundle != null && bundle.containsKey("mb_no"))
        {
            mb_no= bundle.getString("mb_no");
            Log.d(TAG,"mb_no"+mb_no);
        }

        IniTialSettiing();
        Log.d(TAG, "onCreate initial finish");
    }
    //----------------------------------------------------------------------------
    private void IniTialSettiing(){

        mmb_sta = (TextView)findViewById(R.id.mb_sta);
        mQrCode_Img = (ImageView)findViewById(R.id.Qrcode_img);
        load_config();
        Log.d(TAG, "ip" + ip);
        Log.d(TAG, "folder" + folder);
        CreateRandomIntro_Code();
        Qrcode_Check_Thread("get_qrcode_introl","get_qrcode_introl");

    }
    //----------------------------------------------------------------------------
    private void  CreateRandomIntro_Code () {
        int bool_Sta ;
        for (int i =0 ;i<6;i++){
            bool_Sta =(int)(Math.random()*2);
            Log.d(TAG,"bool_Sta"+bool_Sta);
            intro_code=GetRandomString(bool_Sta,intro_code);
        }

    }
    //----------------------------------------------------------------------------
    private String GetRandomString (int status,String Source_Str){
        String Target_Str=Source_Str;
        String math = "0123456789";
        String letter = "abcdefghijklmnopqrstuvwxyz";

        if(status==0){
            int tmpletter;
            tmpletter =(int)(Math.random()*letter.length());
            Log.d(TAG,"tmpletter"+tmpletter);
            Target_Str += letter.substring(tmpletter,tmpletter+1);
            Log.d(TAG,"Target_Str"+Target_Str);
        }else {
            int tmpmath;
            tmpmath =(int)(Math.random()*math.length());
            Log.d(TAG,"tmpmath"+tmpmath);
            Target_Str += math.substring(tmpmath,tmpmath+1);
            Log.d(TAG,"Target_Str"+Target_Str);
        }
        return Target_Str;
    }

    //----------------------------------------------------------------------------

    @Override
    public void onClick(View v) {

    }

    //----------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                QrCode_Return_Act();
                return true;
            default:
                break;
        }
        //Toast.makeText(context, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
    //----------------------------------------------------------------------------
    //--------------------------------------------------------------------
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            //按返回鍵，則執行退出確認
            QrCode_Return_Act();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //--------------------------------------------------------------------------
    public void  QrCode_Return_Act(){
        Intent intent = new Intent(qrcode_main_act.this, test_page.class);
        Bundle bundle2 = new Bundle();
        intent.putExtras(bundle2);
        startActivity(intent);
        finish();
    }


    //====
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
    //--------------------------------------------------------------------------
    public void Qrcode_Check_Thread(final String cmd,String thread_name) {
        mDialog = ProgressDialog.show(this, "Qrcode", "產生中...", true);
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = Qrcode_Check_Thread_Data(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                            mDialog.dismiss();
                            Qrcode_Check_Thread_Data_Res(jsonString);
                    }
                });
            }
        });
    }

    //--------------------------------------------------------------------------
    private String Qrcode_Check_Thread_Data(String query)
    {
        String result = "";
        try
        {
            Log.d("fac", "1");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("mb_no",mb_no));
            nameValuePairs.add(new BasicNameValuePair("intro_code",intro_code));
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
    //--------------------------------------------------------------------------
    public final void Qrcode_Check_Thread_Data_Res(String input)
    {
        Log.d(TAG, "input" + input);
            intro_code=input;
            mmb_sta.setText(intro_code);
        String path ="http://"+ip+"/"+"ct/mbst/app.php?intro_code=";
        mQrCode_Option.QrCheckInit_GetRid(mQrCode_Img, qrcode_main_act.this,intro_code,path);

    }
}
