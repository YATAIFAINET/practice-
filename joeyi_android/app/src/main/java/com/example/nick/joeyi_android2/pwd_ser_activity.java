package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class pwd_ser_activity extends Activity {
    private Spinner sex_spinner;

    private String[] sex={"男","女"};
    private String[] sex_value={"1","0"};
    private ArrayAdapter<String> listAdapter_sex;
    private int sex_select=0;
    private EditText birth_date;
    private Button save_btn;
    private String ip,folder;
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    String mb_no="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //消除狀態列

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隱藏logo
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        setContentView(R.layout.activity_pwd_ser_activity);
        setTitle("忘記密碼");
        Bundle bundle =this.getIntent().getExtras();
        load_config();
        mb_no = bundle.getString("mb_no");
        sex_spinner= (Spinner)findViewById(R.id.sex);
        listAdapter_sex = new ArrayAdapter<String>(this,R.layout.myspinner,sex);
        listAdapter_sex.setDropDownViewResource(R.layout.myspinner);
        sex_spinner.setAdapter(listAdapter_sex);

        //性別
        sex_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                sex_select = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        birth_date=(EditText)findViewById(R.id.birth_date);
        birth_date = (EditText) findViewById(R.id.birth_date);
        birth_date.setInputType(InputType.TYPE_NULL);
        birth_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (hasFocus) {
                    Calendar c = Calendar.getInstance();
                    new DatePickerDialog(pwd_ser_activity.this, new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // TODO Auto-generated method stub
                            String mm;
                            if(monthOfYear + 1<10){
                                mm="0"+(monthOfYear + 1);
                            }else{
                                mm= String.valueOf(monthOfYear + 1);

                            }
                            String dd;

                            if(dayOfMonth<10){
                                dd="0"+(dayOfMonth);
                            }else{
                                dd= String.valueOf(dayOfMonth);

                            }
                            birth_date.setText(year + "-" + String.valueOf(mm) + "-" + String.valueOf(dd));                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

                }
            }
        });

        birth_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(pwd_ser_activity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        String mm;
                        if(monthOfYear + 1<10){
                            mm="0"+(monthOfYear + 1);
                        }else{
                            mm= String.valueOf(monthOfYear + 1);

                        }
                        String dd;

                        if(dayOfMonth<10){
                            dd="0"+(dayOfMonth);
                        }else{
                            dd= String.valueOf(dayOfMonth);

                        }
                        birth_date.setText(year + "-" + String.valueOf(mm) + "-" + String.valueOf(dd));
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        save_btn=(Button)findViewById(R.id.save_mbst);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText bossid=(EditText)findViewById(R.id.boss_id);
                EditText mb_name=(EditText)findViewById(R.id.mb_name);
                //birth_date
                String err_str="";
                if(bossid.getText().toString().length()<10){
                    err_str+=" 身分證";
                }
                if(mb_name.getText().toString().length()==0){
                    err_str+=" 姓名";
                }
                if(birth_date.getText().toString().length()==0){
                    err_str+=" 生日";
                }

                if(err_str.length()>0){

                    Toast.makeText(getApplicationContext(),err_str+"有誤", Toast.LENGTH_LONG).show();
                    return;
                }

                senddata();
                save_btn.setEnabled(false);
                Toast.makeText(getApplicationContext(),"傳送中請稍候", Toast.LENGTH_LONG).show();




            }
        });




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
        Intent i = new Intent(this,login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        startActivity(i);
        finish();

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
    public void senddata() {
        mThread = new HandlerThread("get_country");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = senddata_php("get_pwd");

                mUI_Handler.post(new Runnable() {
                    public void run() {

                        senddata_res(jsonString);


                    }
                });
            }
        });
    }
    private String senddata_php(String cmd)
    {
        String result = "";
        EditText bossid=(EditText)findViewById(R.id.boss_id);
        EditText mb_name=(EditText)findViewById(R.id.mb_name);
        try
        {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",cmd));
            nameValuePairs.add(new BasicNameValuePair("boss_id",bossid.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("mb_no",mb_no));
            nameValuePairs.add(new BasicNameValuePair("mb_name",mb_name.getText().toString()));
            String a= String.valueOf(sex_value[sex_select]);
            nameValuePairs.add(new BasicNameValuePair("sex",a));
            nameValuePairs.add(new BasicNameValuePair("birthday",birth_date.getText().toString()));


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
    public final void senddata_res(String input)
    {
        Log.d("leo pwd",input);
        String []res=input.split(",");
        if(res[0].equals("ok")){

            Toast.makeText(getApplicationContext(),"親愛的使用者您好,您的密碼已發送至您註冊的信箱:"+res[1], Toast.LENGTH_LONG).show();
            save_btn.setEnabled(true);


        }else{
            Toast.makeText(getApplicationContext(),"傳送失敗,請聯繫客服", Toast.LENGTH_LONG).show();
            save_btn.setEnabled(true);

        }

    }
}
