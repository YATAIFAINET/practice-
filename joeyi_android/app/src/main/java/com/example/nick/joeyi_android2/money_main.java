package com.example.nick.joeyi_android2;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class money_main extends ListActivity {

    private String[] WEEK_NO,WEEK_NO_DAY;
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Spinner spinner,spinner_day;
    private ArrayAdapter lunchList,lunchList_day;
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> item,item2;
    String config="",login_no;



    private DefaultHttpClient httpClient;
    private HttpPost httpPost;
    private HttpEntity httpEntity;
    private HttpResponse httpResponse;
    public static String PHPSESSID = null;
    String ip="",folder="";
    String week,week_day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.money_main);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner_day = (Spinner) findViewById(R.id.spinner_day);
        load_config();
        config  = readFromFile("client_config");
        create_thread("get_week", "get_week");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle("獎金查詢");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
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
                        if (cmd == "get_week") {
                            newSpinner(jsonString);
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
            login_no = new JSONObject(config).getString("login_id");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("mb_no",login_no));
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
    public final void newSpinner(String input)
    {
        //Toast.makeText(getApplicationContext(),tab_status, Toast.LENGTH_SHORT).show();
        //tab_status="cus";
        try
        {
            Log.e("***++--",input);
            JSONArray jsonArray = new JSONArray(input);
            if(jsonArray.length() == 0){
                Toast.makeText(money_main.this,"目前無可查詢期別",Toast.LENGTH_LONG).show();
                return;
            }
            WEEK_NO = new String[jsonArray.length()+1];
            WEEK_NO[0]="請選擇";
            for (int i = 0; i <jsonArray.length(); i++)
            {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                WEEK_NO[i+1]=jsonData.getString("yymm");
                Log.e("yymm",WEEK_NO[i+1]);
            }
            lunchList = new ArrayAdapter<String>(money_main.this,R.layout.spinnerstyle, WEEK_NO);
            spinner.setAdapter(lunchList);
            spinner.setSelection(0, true);
            //get_week_sum_thread(WEEK_NO[0]);
            week=WEEK_NO[0];
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    //Toast.makeText(getApplicationContext(), "你選的是" + WEEK_NO[position], Toast.LENGTH_SHORT).show();
                    Get_Day_Data_Thread("week_no","week_no",WEEK_NO[position]);
                    week=WEEK_NO[position];
                    Log.d("weekweek", "onItemSelected ");
/*
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("week_no", WEEK_NO[position]);
                        intent.putExtras(bundle);
                        intent.setClass(MainActivity.this, MainActivity.class);
                        startActivity(intent);*/

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });

        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Toast.makeText(getApplication(), "" + e.toString(), Toast.LENGTH_LONG).show();
            Log.e("json", e.toString());
        }

    }
    //------------------------------------
    public void Get_Day_Data_Thread(final String cmd,String thread_name, final String weeken) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = Get_Day_Data(cmd,weeken);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        if (cmd == "week_no") {
                            Get_Day_Data_Res(jsonString);
                        }
                    }
                });
            }
        });
    }
    private String Get_Day_Data(String query,String weekend)
    {
        String result = "";
        try
        {
            login_no = new JSONObject(config).getString("login_id");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("mb_no",login_no));
            nameValuePairs.add(new BasicNameValuePair("week_no",weekend));
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
    public final void Get_Day_Data_Res(String input)
    {
        //Toast.makeText(getApplicationContext(),tab_status, Toast.LENGTH_SHORT).show();
        //tab_status="cus";
        try
        {
            Log.e("***++--",input);
            JSONArray jsonArray = new JSONArray(input);
            if(jsonArray.length() == 0){
                Toast.makeText(money_main.this,"目前無可查詢期別",Toast.LENGTH_LONG).show();
                return;
            }
            WEEK_NO_DAY = new String[jsonArray.length()+1];
            WEEK_NO_DAY[0]="請選擇";
            for (int i = 0; i <jsonArray.length(); i++)
            {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                WEEK_NO_DAY[i+1]=jsonData.getString("yymm");
                Log.e("yymm",WEEK_NO_DAY[i+1]);
            }
            lunchList_day = new ArrayAdapter<String>(money_main.this,R.layout.spinnerstyle, WEEK_NO_DAY);
            spinner_day.setAdapter(lunchList_day);
            spinner_day.setSelection(0, true);
            //get_week_sum_thread(WEEK_NO[0]);
            week_day=WEEK_NO_DAY[0];
            spinner_day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    //Toast.makeText(getApplicationContext(), "你選的是" + WEEK_NO[position], Toast.LENGTH_SHORT).show();
                    get_week_sum_thread(WEEK_NO_DAY[position]);
                    week_day=WEEK_NO_DAY[position];
                    Log.d("weekweek", "onItemSelected ");
/*
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("week_no", WEEK_NO[position]);
                        intent.putExtras(bundle);
                        intent.setClass(MainActivity.this, MainActivity.class);
                        startActivity(intent);*/

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });

        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Toast.makeText(getApplication(), "" + e.toString(), Toast.LENGTH_LONG).show();
            Log.e("json", e.toString());
        }

    }
    //---------------------------

    public void get_week_sum_thread(final String week_no) {
        mThread = new HandlerThread("get_money_sum");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = send_week_sum_data(week_no);

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        list_data(jsonString);
                    }
                });
            }
        });
    }
    private String send_week_sum_data(String week_no)
    {
        String result = "";
        try
        {
            login_no = new JSONObject(config).getString("login_id");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd","get_money_sum"));
            nameValuePairs.add(new BasicNameValuePair("week_no",week_no));
            nameValuePairs.add(new BasicNameValuePair("mb_no",login_no));
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
    public final void list_data(String input)
    {
        try
        {

            JSONArray jsonArray = new JSONArray(input);
            list = new ArrayList<HashMap<String,String>>();
            if(jsonArray.length()>0) {
                Log.d("list_data_for",input);
                JSONObject jsonData = jsonArray.getJSONObject(0);
                //item = new HashMap<String, String>();
                TextView grade_name=(TextView)findViewById(R.id.grade_name);
                grade_name.setText(jsonData.getString("grade_name"));
                ImageView grade=(ImageView)findViewById(R.id.grade_img);
                if(jsonData.getString("grade_class").equals("0")){
                    grade.setImageDrawable(getResources().getDrawable(R.drawable.g003));
                } else if(jsonData.getString("grade_class").equals("5")){
                    grade.setImageDrawable(getResources().getDrawable(R.drawable.g005));

                } else if(jsonData.getString("grade_class").equals("10")){
                    grade.setImageDrawable(getResources().getDrawable(R.drawable.g008));

                } else if(jsonData.getString("grade_class").equals("15")){
                    grade.setImageDrawable(getResources().getDrawable(R.drawable.g004));
                } else if(jsonData.getString("grade_class").equals("20")){
                    grade.setImageDrawable(getResources().getDrawable(R.drawable.g023));
                } else if(jsonData.getString("grade_class").equals("25")){
                    grade.setImageDrawable(getResources().getDrawable(R.drawable.g025));
                } else if(jsonData.getString("grade_class").equals("30")){
                    grade.setImageDrawable(getResources().getDrawable(R.drawable.g021));
                } else if(jsonData.getString("grade_class").equals("35")){
                    grade.setImageDrawable(getResources().getDrawable(R.drawable.g026));
                } else if(jsonData.getString("grade_class").equals("40")){
                    grade.setImageDrawable(getResources().getDrawable(R.drawable.g027));
                } else {
                    grade.setImageDrawable(getResources().getDrawable(R.drawable.g_1));
                }
                TextView subtotal=(TextView)findViewById(R.id.subtotal);//獎金總額
                subtotal.setText("$"+jsonData.getString("subtotal")+" (USD)");
                TextView tax=(TextView)findViewById(R.id.tax);//所得稅
                tax.setText("$"+jsonData.getString("tax")+ " (NTD)");

                TextView tax2=(TextView)findViewById(R.id.tax2);//營業稅
                tax2.setText("$"+jsonData.getString("tax2")+ " (NTD)");
                TextView tax3=(TextView)findViewById(R.id.tax3);//補充保費
                tax3.setText("$"+jsonData.getString("tax3")+ " (NTD)");
                TextView money_adm=(TextView)findViewById(R.id.money_adm);//溢撥追回
                money_adm.setText("$"+jsonData.getString("money_adm")+" (USD)");
                TextView givemoney=(TextView)findViewById(R.id.givemoney);//未稅獎金
                givemoney.setText("$"+jsonData.getString("givemoney")+ " (NTD)");

                String Source_PV = jsonData.getString("Pv_Data");
                String Array_Source_Pv [] = Source_PV.split("@@");

                TextView money_left_first=(TextView)findViewById(R.id.money_left_first);//左線前期
                money_left_first.setText(Array_Source_Pv[0]+" CV");
                TextView money_right_first=(TextView)findViewById(R.id.money_right_first);//右線前期
                money_right_first.setText(Array_Source_Pv[1]+" CV");
                TextView money_left_this=(TextView)findViewById(R.id.money_left_this);//左線本期新增
                money_left_this.setText(Array_Source_Pv[2]+" CV");
                TextView money_right_this=(TextView)findViewById(R.id.money_right_this);//右線本期新增
                money_right_this.setText(Array_Source_Pv[3]+" CV");
                TextView money_left_count=(TextView)findViewById(R.id.money_left_count);//左線剩餘
                money_left_count.setText(Array_Source_Pv[4]+" CV");
                TextView money_right_count=(TextView)findViewById(R.id.money_right_count);//右線剩餘
                money_right_count.setText(Array_Source_Pv[5]+" CV");
                TextView circle_plus_circle_minus=(TextView)findViewById(R.id.circle_plus_circle_minus);//獎金調整
                circle_plus_circle_minus.setText("$"+jsonData.getString("circle_plus-circle_minus")+" (USD)");

                TextView a_line_sub_old = (TextView) findViewById(R.id.a_line_sub_old);
                a_line_sub_old.setText(Array_Source_Pv[6]+" CV");
                TextView b_line_sub_old = (TextView) findViewById(R.id.b_line_sub_old);
                b_line_sub_old.setText(Array_Source_Pv[7]+" CV");
                TextView a_line_sub_new = (TextView) findViewById(R.id.a_line_sub_new);
                a_line_sub_new.setText(Array_Source_Pv[8]+" CV");
                TextView b_line_sub_new = (TextView) findViewById(R.id.b_line_sub_new);
                b_line_sub_new.setText(Array_Source_Pv[9]+" CV");
                TextView a_line_sub = (TextView) findViewById(R.id.a_line_sub);
                a_line_sub.setText(Array_Source_Pv[10]+" CV");
                TextView b_line_sub = (TextView) findViewById(R.id.b_line_sub);
                b_line_sub.setText(Array_Source_Pv[11]+" CV");

                TextView a_line_sum = (TextView) findViewById(R.id.a_line_sum);
                TextView b_line_sum = (TextView) findViewById(R.id.b_line_sum);
                if(Array_Source_Pv[12].equals("none")){
                    a_line_sum.setText("0 CV");
                    b_line_sum.setText("0 CV");
                }else{
                    String tmp = Array_Source_Pv[12].substring(6);
                    String[] array = tmp.split("；");
                    array[0] = array[0].replace("L：","");
                    array[1] = array[1].replace("R：","");
                    array[0] = array[0].replace(",","");
                    array[1] = array[1].replace(",","");
                    a_line_sum.setText(array[0]+" CV");
                    b_line_sum.setText(array[1]+" CV");
                }



                //item.put("yymm", "" + jsonData.getString("yymm"));
                //item.put("intro_money", "" + jsonData.getString("intro_money"));
                //item.put("org_money", "" + jsonData.getString("org_money"));
                //item.put("red_money", "" + jsonData.getString("red_money"));
                //item.put("red1_money", "" + jsonData.getString("red1_money"));
                //item.put("red2_money", "" + jsonData.getString("red2_money"));
                //item.put("ab_money", "" + jsonData.getString("ab_money"));
                //item.put( "level_money",""+jsonData.getString("level_money"));
                //item.put( "subtotal",""+jsonData.getString("subtotal"));
                //item.put( "tax",""+jsonData.getString("tax"));
                //item.put( "tax2",""+jsonData.getString("tax2"));
                //item.put( "tax3",""+jsonData.getString("tax3"));
                //item.put( "money_adm",""+jsonData.getString("money_adm"));
                //item.put( "givemoney",""+jsonData.getString("givemoney"));
                //item.put("grade_class", "" + jsonData.getString("grade_class"));
                //item.put( "grade_name",""+jsonData.getString("grade_name"));
                //list.add(item);

                String[] detail_title = {"推薦獎金","層碰獎金","對碰獎金","直推輔導","交叉輔導","領導分紅"};
                String[] each_money = {"$ "+jsonData.getString("intro_money"),"$ "+jsonData.getString("org_money"),"$ "+jsonData.getString("ab_money"),"$ "+jsonData.getString("red_money"),"$ "+jsonData.getString("red3_money"),"$"+jsonData.getString("red7_money")};
                list = new ArrayList<HashMap<String,String>>();
                for (int i = 0; i < detail_title.length; i++) {
                    item2 = new HashMap<String, String>();
                    item2.put("detail_title", detail_title[i]);
                    item2.put("each_money", each_money[i]);
                    list.add(item2);
                }

                //adapter = new MySimpleAdapter(getActivity(), list, R.layout.my_account_list,new String[]{ "detail_title"},new int[]{R.id.my_account_list});
                //setListAdapter(adapter);


                adapter = new SimpleAdapter(this, list, R.layout.money_list,new String[]{ "detail_title", "each_money"},new int[]{R.id.tv_money_name, R.id.tv_money});
                setListAdapter(adapter);
            }else{
                /*if(jsonData.getString("grade_class").equals("0")){
                    grade.setImageDrawable(getResources().getDrawable(R.drawable.g0));
                }
                if(jsonData.getString("grade_class").equals("5")){
                    grade.setImageDrawable(getResources().getDrawable(R.drawable.g1));

                }
                if(jsonData.getString("grade_class").equals("10")){
                    grade.setImageDrawable(getResources().getDrawable(R.drawable.g2));

                }
                if(jsonData.getString("grade_class").equals("15")){
                    grade.setImageDrawable(getResources().getDrawable(R.drawable.g3));

                }*/
                TextView subtotal=(TextView)findViewById(R.id.subtotal);//獎金總額
                subtotal.setText("$0");
                TextView tax=(TextView)findViewById(R.id.tax);//所得稅
                tax.setText("$0 (NTD)");

                TextView tax2=(TextView)findViewById(R.id.tax2);//營業稅
                tax2.setText("$0 (NTD)");
                TextView tax3=(TextView)findViewById(R.id.tax3);//補充保費
                tax3.setText("$0 (NTD)");
                TextView money_adm=(TextView)findViewById(R.id.money_adm);//溢撥追回
                money_adm.setText("$0 (USD)");
                TextView givemoney=(TextView)findViewById(R.id.givemoney);//未稅獎金
                givemoney.setText("$0 (NTD)");

                TextView money_left_first=(TextView)findViewById(R.id.money_left_first);//左線前期
                money_left_first.setText("0"+" CV");
                TextView money_right_first=(TextView)findViewById(R.id.money_right_first);//右線前期
                money_right_first.setText("0"+" CV");
                TextView money_left_this=(TextView)findViewById(R.id.money_left_this);//左線本期新增
                money_left_this.setText("0"+" CV");
                TextView money_right_this=(TextView)findViewById(R.id.money_right_this);//右線本期新增
                money_right_this.setText("0"+" CV");
                TextView money_left_count=(TextView)findViewById(R.id.money_left_count);//左線剩餘
                money_left_count.setText("0"+" CV");
                TextView money_right_count=(TextView)findViewById(R.id.money_right_count);//右線剩餘
                money_right_count.setText("0"+" CV");
                TextView circle_plus_circle_minus=(TextView)findViewById(R.id.circle_plus_circle_minus);//獎金調整
                circle_plus_circle_minus.setText("$0 (USD)");

                String[] detail_title = {"推薦獎金","層碰獎金","對碰獎金","直推輔導","交叉輔導","領導分紅"};
                String[] each_money = {"$ 0","$ 0","$ 0 ","$ 0","$ 0 ","$ 0"};
                list = new ArrayList<HashMap<String,String>>();
                for (int i = 0; i < detail_title.length; i++) {
                    item2 = new HashMap<String, String>();
                    item2.put("detail_title", detail_title[i]);
                    item2.put("each_money", each_money[i]);
                    list.add(item2);
                }
                adapter = new SimpleAdapter(this, list, R.layout.money_list,new String[]{ "detail_title", "each_money"},new int[]{R.id.tv_money_name, R.id.tv_money});
                setListAdapter(adapter);
            }

        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

        }

    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        String[] detail_kind = {"[推薦獎金]","[層碰獎金]","[對碰獎金]","[直推輔導]","[交叉輔導]","[領導分紅]"};
        String temp=list.get(position).get("each_money");
        temp=temp.replace("$","");
        //  temp=temp.replace("]","");
        double t= Double.parseDouble(temp);
        if(t>0){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            //bundle.putString("money_field", list.get(position).get("money_field"));
            bundle.putString("detail_kind", detail_kind[position]);
            bundle.putString("week_no",week_day);
            intent.putExtras(bundle);
            intent.setClass(this, money_detail.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "無此獎金明細", Toast.LENGTH_SHORT).show();

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
}