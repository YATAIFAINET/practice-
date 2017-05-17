package com.example.nick.joeyi_android2;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 2015/11/25.
 */
public class my_account_e_cash extends ListActivity {
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Handler mUI_Handler = new Handler();
    private Context context;
    private Button mButton_Give_Bonus, mButton_Bonus_to_Money;

    String ip = "", folder = "",config="",login_id,from;
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> item;
    String []eff;

    TextView can_use_bonus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        load_config();
        Bundle bundle = this.getIntent().getExtras();
        from = bundle.getString("from");
        if(from.equals("e_cash")){
            getActionBar().setTitle("購物紅利");
        }else if(from.equals("e_cash2")){
            getActionBar().setTitle("報單積分");
        }else if(from.equals("bonus")){
            getActionBar().setTitle("重消積分");
        }else if(from.equals("bonus2")){
            getActionBar().setTitle("產品紅利積分");
        }else if(from.equals("e_gold")){
            getActionBar().setTitle("小金庫");
        }else if(from.equals("e_pv")){
            getActionBar().setTitle("購物積分");
        }
        config  = readFromFile("client_config");
        context = this;
        setContentView(R.layout.my_account_e_cash);
        create_thread("get_ecash", "get_ecash");
        create_thread2("get_ecash_detail", "get_ecash_detail");
        init();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        create_thread("get_ecash", "get_ecash");
        create_thread2("get_ecash_detail", "get_ecash_detail");
    }

    private void init(){
        mButton_Give_Bonus = (Button) findViewById(R.id.give_bonus);
        mButton_Bonus_to_Money = (Button) findViewById(R.id.bonus_to_money);

        mButton_Give_Bonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(my_account_e_cash.this,"!"+can_use_bonus.getText().toString(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("can_use_bonus",can_use_bonus.getText().toString());
                intent.putExtras(bundle);
                intent.setClass(my_account_e_cash.this,Give_bonus.class);
                startActivity(intent);
            }
        });

        mButton_Bonus_to_Money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(my_account_e_cash.this,"@"+can_use_bonus.getText().toString(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("can_use_bonus",can_use_bonus.getText().toString());
                intent.putExtras(bundle);
                intent.setClass(my_account_e_cash.this,Ecash_To_Money.class);
                startActivity(intent);
            }
        });
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
    public void create_thread(final String cmd,String thread_name) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        if (cmd == "get_ecash") {
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
            login_id = new JSONObject(config).getString("login_id");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("mb_no",login_id));
            nameValuePairs.add(new BasicNameValuePair("from",from));
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
    public final void newlist(String input)
    {
        //Toast.makeText(getApplicationContext(),tab_status, Toast.LENGTH_SHORT).show();
        //tab_status="cus";
        Log.d("newlist2", input);
        try
        {

            JSONArray jsonArray = new JSONArray(input);
            JSONObject jsonData = jsonArray.getJSONObject(0);
            can_use_bonus=(TextView)findViewById(R.id.can_use_bonus);
            TextView used_bonus=(TextView)findViewById(R.id.used_bonus);

            TextView total_bonus=(TextView)findViewById(R.id.total_bonus);
            TextView eff_bonus=(TextView)findViewById(R.id.eff_bonus);

            total_bonus.setText(jsonData.getString("sum_point"));
            eff_bonus.setText(jsonData.getString("eff_point"));
            used_bonus.setText(jsonData.getString("use_point"));
            Double can_use=Double.parseDouble(eff_bonus.getText().toString())-Double.parseDouble(used_bonus.getText().toString());
            can_use=(double)(Math.round(can_use*100)/100.0);
            if(can_use<0){
                can_use=0.0;
            }
            if(String.valueOf(can_use).substring(String.valueOf(can_use).length() - 2).equals(".0")){
                String tmp;
                tmp=String.valueOf(can_use).replace(".0", "");
                can_use_bonus.setText(String.valueOf(tmp));

            }else{
                can_use_bonus.setText(String.valueOf(can_use));
            }



        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Toast.makeText(getApplication(), "" + e.toString(), Toast.LENGTH_LONG).show();
            Log.e("json", e.toString());
        }

    }
    public void create_thread2(final String cmd,String thread_name) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery2(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        if (cmd == "get_ecash_detail") {
                            newlist2(jsonString);
                        }
                    }
                });
            }
        });
    }
    private String executeQuery2(String query)
    {
        String result = "";
        try
        {
            login_id = new JSONObject(config).getString("login_id");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("mb_no",login_id));
            nameValuePairs.add(new BasicNameValuePair("from",from));
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
    public final void newlist2(String input)
    {
        //Toast.makeText(getApplicationContext(),tab_status, Toast.LENGTH_SHORT).show();
        //tab_status="cus";
        try
        {
            JSONArray jsonArray = new JSONArray(input);
            list = new ArrayList<HashMap<String,String>>();
            eff=new String[jsonArray.length()];
            if(jsonArray.length()>0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    item = new HashMap<String, String>();
//                    item.put("eff_date",jsonData.getString("eff_date"));
                    if(jsonData.getString("week_no").equals("null")){
                        item.put("eff_date","未生效週別");//改週期
                    }else{
                        item.put("eff_date",jsonData.getString("week_no"));//改週期
                    }

                    item.put("ps", jsonData.getString("ps"));
                    item.put("bonus",jsonData.getString("point"));
                    list.add(item);
                    Date date = new Date();
                    SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyyMMdd");
                    String str = bartDateFormat.format(date);


                    String date2=jsonData.getString("eff_date");
                    date2=date2.replace("-","");
                    Log.d("test1218", str);
                    int todate= Integer.parseInt(str);
                    int effdate= Integer.parseInt(date2);

                    if(todate>=effdate){//已生效
                        eff[i]="1";
                    }else{
                        eff[i]="0";
                    }
                    //
                }
                Log.d("product2", String.valueOf(list));
                adapter = new MySimpleAdapter(getApplication(), list, R.layout.my_account_e_cash_list,new String[]{ "eff_date", "ps", "bonus"},new int[]{R.id.eff_date, R.id.ps, R.id.bonus});
                setListAdapter(adapter);

            }else{
               /* item = new HashMap<String, String>();
                item.put("prod_no", "目前尚無產品");
                list.add(item);
                adapter = new MySimpleAdapter(getActivity(), list, R.layout.product_list,new String[]{ "prod_no"},new int[]{R.id.txv_product_name});
                setListAdapter(adapter);*/
            }
        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Toast.makeText(getApplication(), "" + e.toString(), Toast.LENGTH_LONG).show();
            Log.e("json", e.toString());
        }

    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
    }
    private class MySimpleAdapter extends SimpleAdapter{

        public MySimpleAdapter(Context context,
                               List<? extends Map<String, ?>> data, int resource,
                               String[] from, int[] to) {
            super(context, data, resource, from, to);
            // TODO Auto-generated constructor stub
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub


            Log.d("position", String.valueOf(position));
            final View v= super.getView(position, convertView, parent);
            TextView eff_date=(TextView) v.findViewById(R.id.eff_date);
            TextView ps=(TextView) v.findViewById(R.id.ps);

            TextView bonus=(TextView) v.findViewById(R.id.bonus);


            LinearLayout eff_layout=(LinearLayout)v.findViewById(R.id.eff_layout);

            if(eff[position].equals("0")){//未生效
                Log.d("pos",eff[position]);
                eff_layout.setBackgroundColor(Color.parseColor("#eeeeee"));
                eff_date.setTextColor(Color.parseColor("#6f6f6f"));
                ps.setTextColor(Color.parseColor("#6f6f6f"));

                bonus.setTextColor(Color.parseColor("#6f6f6f"));

            }else{
                eff_layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                eff_date.setTextColor(Color.parseColor("#555555"));
                ps.setTextColor(Color.parseColor("#555555"));

                bonus.setTextColor(Color.parseColor("#555555"));

            }

            return v;
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
}