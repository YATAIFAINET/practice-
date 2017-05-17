package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Activity_Fix_Mb_Info extends Activity {
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    String ip="",folder="";
    String config="",login_no;

    //UI Object
    private TextView mTextView_Mb_No, mTextView_Country, mTextView_Person, mTextView_Boss_Id, mTextView_Mb_Name, mTextView_Sex, mTextView_Birth, mTextView_True_Intro_no, mTextView_True_Intro_Name, mTextView_Intro_no, mTextView_Intro_Name, mTextView_Bank, mTextView_Ac_name, mTextView_Bank_Ac, mTextView_City1, mTextView_Area1, mTextView_Add1;
    private EditText mEditText_CellPhone, mEditText_Email, mEditText_Add2, mEditText_Add3 ;
    private Spinner mSpinner_City2, mSpinner_Area2, mSpinner_City3, mSpinner_Area3;
    private Button mButton_Save;

    //Array Value
    private String[] Array_City_No;
    private String[] Array_City_Name;
    private String[] Array_Area_No;
    private String[] Array_Area_Name;

    String[] tmp_Array_Area2_No = new String[0];
    String[] tmp_Array_Area2_Name = new String[0];
    String[] tmp_Array_Area3_No = new String[0];
    String[] tmp_Array_Area3_Name = new String[0];

    //select city area
    private int city2_select = -1;
    private int area2_select = -1;
    private int city3_select = -1;
    private int area3_select = -1;

    private String tmp_area2;
    private String tmp_area3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fix_mb_info);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        setTitle("修改資料");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        }
        load_config();
        config  = readFromFile("client_config");
        try {
            login_no = new JSONObject(config).getString("login_id");
            Log.e("login_no",login_no);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UI_Initial();
        Get_City_thread();
    }

    private void UI_Initial(){
        mTextView_Mb_No = (TextView) findViewById(R.id.txv_mb_no);
        mTextView_Country = (TextView) findViewById(R.id.country);
        mTextView_Person = (TextView) findViewById(R.id.person);
        mTextView_Boss_Id = (TextView) findViewById(R.id.boss_id);
        mTextView_Mb_Name = (TextView) findViewById(R.id.mb_name);
        mTextView_Sex = (TextView) findViewById(R.id.sex);
        mTextView_Birth = (TextView) findViewById(R.id.birth_date);
        mTextView_True_Intro_no = (TextView) findViewById(R.id.true_intro_no);
        mTextView_True_Intro_Name = (TextView) findViewById(R.id.true_intro_name);
        mTextView_Intro_no = (TextView) findViewById(R.id.intro_no);
        mTextView_Intro_Name = (TextView) findViewById(R.id.intro_name);
        mTextView_Bank = (TextView) findViewById(R.id.bank);
        mTextView_Ac_name = (TextView) findViewById(R.id.ac_name);
        mTextView_Bank_Ac = (TextView) findViewById(R.id.bank_ac);
        mTextView_City1 = (TextView) findViewById(R.id.city1);
        mTextView_Area1 = (TextView) findViewById(R.id.area1);
        mTextView_Add1 = (TextView) findViewById(R.id.add1);

        mEditText_CellPhone = (EditText) findViewById(R.id.cellphone);
        mEditText_Email = (EditText) findViewById(R.id.Join_Data_Email_Edit);
        mEditText_Add2 = (EditText) findViewById(R.id.add2);
        mEditText_Add3 = (EditText) findViewById(R.id.add3);

        mSpinner_City2 = (Spinner) findViewById(R.id.city2);
        mSpinner_Area2 = (Spinner) findViewById(R.id.area2);
        mSpinner_City3 = (Spinner) findViewById(R.id.city3);
        mSpinner_Area3 = (Spinner) findViewById(R.id.area3);

        mButton_Save = (Button) findViewById(R.id.save_mbst);

        mButton_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update_Info_thread();
            }
        });

        //action
        mSpinner_Area2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                int pos = mSpinner_Area2.getSelectedItemPosition();
                area2_select = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSpinner_Area3.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                int pos = mSpinner_Area3.getSelectedItemPosition();
                area3_select = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected", "onOptionsItemSelected ");
        switch(item.getItemId()) {
            case android.R.id.home:
                Log.d("onOptionsItemSelected2", "onOptionsItemSelected2 ");
                finish();
                return true;
            default:
                break;

        }
        //Toast.makeText(context, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    //thread
    public void Get_Mb_Info_thread() {
        mThread = new HandlerThread("Get_Mb_Info_thread");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = Get_Mb_Info_Data("get_mb_info");

                mUI_Handler.post(new Runnable() {
                    public void run() {

                        Get_Mb_Info_Res(jsonString);
                    }
                });
            }
        });
    }

    private String Get_Mb_Info_Data(String cmd) {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/android_sql.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", cmd));
            nameValuePairs.add(new BasicNameValuePair("mb_no", login_no));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

    public final void Get_Mb_Info_Res(String input) {
        try {
            mSpinner_City2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                    int pos = mSpinner_City2.getSelectedItemPosition();
                    city2_select = pos;
                    Select_City_Get_Area_thread(city2_select, mSpinner_Area2, tmp_area2);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            mSpinner_City3.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                    int pos = mSpinner_City3.getSelectedItemPosition();
                    city3_select = pos;
                    Select_City_Get_Area_thread(city3_select,mSpinner_Area3,tmp_area3);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            Log.d("Get_Mb_Info_Res", input);
            JSONArray jsonArray = new JSONArray(input);
            JSONObject jsonObject = jsonArray.getJSONObject(0);

//            mTextView_Country.setText(jsonObject.getString("country_name"));
//            if(jsonObject.getString("id_kind").equals("1")){
//                mTextView_Person.setText("個人");
//            }else if(jsonObject.getString("id_kind").equals("2")){
//                mTextView_Person.setText("法人");
//            }else{
//                mTextView_Person.setText("外人");
//            }

            mTextView_Mb_No.setText(login_no);
            mTextView_Boss_Id.setText(jsonObject.getString("boss_id"));
            mTextView_Mb_Name.setText(jsonObject.getString("mb_name"));
            if(jsonObject.getString("sex").equals("0")){
                mTextView_Sex.setText("女");
            }else{
                mTextView_Sex.setText("男");
            }
            mTextView_Birth.setText(jsonObject.getString("birthday2"));
            mEditText_CellPhone.setText(jsonObject.getString("tel3"));
            mEditText_Email.setText(jsonObject.getString("email"));
            mTextView_Bank.setText(jsonObject.getString("bank_name"));
            mTextView_Bank_Ac.setText(jsonObject.getString("bank_ac"));
            mTextView_Ac_name.setText(jsonObject.getString("ac_name"));
            mTextView_True_Intro_no.setText(jsonObject.getString("true_intro_no"));
            mTextView_True_Intro_Name.setText(jsonObject.getString("true_intro_name"));
            mTextView_Intro_no.setText(jsonObject.getString("intro_no"));
            mTextView_Intro_Name.setText(jsonObject.getString("intro_name"));
            tmp_area2 = jsonObject.getString("area2");
            tmp_area3 = jsonObject.getString("area3");
            for(int i=0;i<Array_City_No.length;i++){
                if(jsonObject.getString("city2").equals(Array_City_No[i])){
                    city2_select = i;
                    mSpinner_City2.setSelection(i);
                    break;
                }else{
                    city2_select = 0;
                    mSpinner_City2.setSelection(0);
                }
            }

            for(int i=0;i<Array_City_No.length;i++){
                if(jsonObject.getString("city3").equals(Array_City_No[i])){
                    city3_select = i;
                    mSpinner_City3.setSelection(i);
                    break;
                }else{
                    mSpinner_City3.setSelection(0);
                    city3_select = -1;
                }
            }

            mEditText_Add2.setText(jsonObject.getString("add2"));
            mEditText_Add3.setText(jsonObject.getString("add3"));

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("json", e.toString());
        }
    }

    public void Get_City_thread() {
        mThread = new HandlerThread("Get_City_thread");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = Get_City_Data("get_city");

                mUI_Handler.post(new Runnable() {
                    public void run() {

                        Get_City_Res(jsonString);
                        Get_Mb_Info_thread();
                        //Get_Area_thread();
                    }
                });
            }
        });
    }

    private String Get_City_Data(String cmd) {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", cmd));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

    public final void Get_City_Res(String input) {
        try {
            Log.e("Get_City_Res", input);
            JSONArray jsonArray = new JSONArray(input);
            Array_City_No = new String[jsonArray.length()+1];
            Array_City_Name = new String[jsonArray.length()+1];
            Array_City_No[0] = "-1";
            Array_City_Name[0] = "請選擇";
            for(int i = 1;i<jsonArray.length();i++){
                Array_City_No[i] = jsonArray.getJSONObject(i).getString("sn");
                Array_City_Name[i] = jsonArray.getJSONObject(i).getString("name");
                //Log.e("array_city_name",Array_City_Name[i]);
            }
            ArrayAdapter city2_adapter = new ArrayAdapter<String>(this, R.layout.myspinner, Array_City_Name);
            city2_adapter.setDropDownViewResource(R.layout.myspinner);
            mSpinner_City2.setAdapter(city2_adapter);
            mSpinner_City3.setAdapter(city2_adapter);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("json", e.toString());
        }
    }

    public void Get_Area_thread() {
        mThread = new HandlerThread("Get_Area_thread");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = Get_Area_Data("get_area_all");

                mUI_Handler.post(new Runnable() {
                    public void run() {

                        Get_Area_Res(jsonString);
                        Get_Mb_Info_thread();
                    }
                });
            }
        });
    }

    private String Get_Area_Data(String cmd) {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", cmd));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

    public final void Get_Area_Res(String input) {
        try {
            Log.d("Get_Area_Res", input);
            JSONArray jsonArray = new JSONArray(input);
            Array_Area_No = new String[jsonArray.length()];
            Array_Area_Name = new String[jsonArray.length()];
            for(int i = 0;i<jsonArray.length();i++){
                Array_Area_No[i] = jsonArray.getJSONObject(i).getString("sn");
                Array_Area_Name[i] = jsonArray.getJSONObject(i).getString("name");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("json", e.toString());
        }
    }

    //select city get area
    public void Select_City_Get_Area_thread(final int select, final Spinner tmp_spinner,final String tmp_area) {
        mThread = new HandlerThread("Select_City_Get_Area_thread");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = Select_City_Get_Area_Data("get_area", select);

                mUI_Handler.post(new Runnable() {
                    public void run() {

                        Select_City_Get_Area_Data_Res(jsonString, tmp_spinner, tmp_area);
                    }
                });
            }
        });
    }

    private String Select_City_Get_Area_Data(String cmd,int select) {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", cmd));
            nameValuePairs.add(new BasicNameValuePair("city", Array_City_No[select]));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

    public final void Select_City_Get_Area_Data_Res(String input,Spinner tmp_spinner,String tmp_area) {
        try {
            Log.d("Get_Area_Res", input);
            JSONArray jsonArray = new JSONArray(input);
            if(tmp_spinner.equals(mSpinner_Area2)){
                tmp_Array_Area2_No = new String[jsonArray.length()];
                tmp_Array_Area2_Name = new String[jsonArray.length()];
                for(int i = 0;i<jsonArray.length();i++){
                    tmp_Array_Area2_Name[i] = jsonArray.getJSONObject(i).getString("name");
                    tmp_Array_Area2_No[i] = jsonArray.getJSONObject(i).getString("sn");
                }
                ArrayAdapter area_adapter = new ArrayAdapter<String>(this, R.layout.myspinner, tmp_Array_Area2_Name);
                area_adapter.setDropDownViewResource(R.layout.myspinner);
                tmp_spinner.setAdapter(area_adapter);
                for(int i=0;i<tmp_Array_Area2_No.length;i++){
                    if(tmp_area.equals(tmp_Array_Area2_No[i])){
                        tmp_spinner.setSelection(i);
                        break;
                    }else{
                        tmp_spinner.setSelection(0);
                    }
                }

            }else if(tmp_spinner.equals(mSpinner_Area3)){
                tmp_Array_Area3_No = new String[jsonArray.length()];
                tmp_Array_Area3_Name = new String[jsonArray.length()];
                for(int i = 0;i<jsonArray.length();i++){
                    tmp_Array_Area3_Name[i] = jsonArray.getJSONObject(i).getString("name");
                    tmp_Array_Area3_No[i] = jsonArray.getJSONObject(i).getString("sn");
                }
                ArrayAdapter area_adapter = new ArrayAdapter<String>(this, R.layout.myspinner, tmp_Array_Area3_Name);
                area_adapter.setDropDownViewResource(R.layout.myspinner);
                tmp_spinner.setAdapter(area_adapter);

                for(int i=0;i<tmp_Array_Area3_No.length;i++){
                    if(tmp_area.equals(tmp_Array_Area3_No[i])){
                        tmp_spinner.setSelection(i);
                        break;
                    }else{
                        tmp_spinner.setSelection(0);
                    }
                }
            }




        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("json", e.toString());
        }
    }

    public void Update_Info_thread() {
        mThread = new HandlerThread("Update_Info_thread");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = Update_Info_Data("update_mb_info");

                mUI_Handler.post(new Runnable() {
                    public void run() {

                        Update_Info_Res(jsonString);
                    }
                });
            }
        });
    }

    private String Update_Info_Data(String cmd) {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", cmd));
            nameValuePairs.add(new BasicNameValuePair("mb_no", login_no));
            nameValuePairs.add(new BasicNameValuePair("tel3", mEditText_CellPhone.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("mail", mEditText_Email.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("city2", Array_City_No[city2_select]));
            if(city2_select == 0){
                nameValuePairs.add(new BasicNameValuePair("area2", "-1"));
            }else{
                nameValuePairs.add(new BasicNameValuePair("area2", tmp_Array_Area2_No[area2_select]));
            }

            nameValuePairs.add(new BasicNameValuePair("city3", Array_City_No[city3_select]));
            if(city3_select == 0){
                nameValuePairs.add(new BasicNameValuePair("area3", "-1"));
            }else{
                nameValuePairs.add(new BasicNameValuePair("area3", tmp_Array_Area3_No[area3_select]));
            }
            nameValuePairs.add(new BasicNameValuePair("add2", mEditText_Add2.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("add3", mEditText_Add3.getText().toString()));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

    public final void Update_Info_Res(String input) {
        Log.e("Update_Info_Res", input);
        if(input.trim().equals("success")){
            Toast.makeText(Activity_Fix_Mb_Info.this,"修改成功",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(Activity_Fix_Mb_Info.this,"修改失敗，請稍後再試",Toast.LENGTH_LONG).show();
        }
    }

}
