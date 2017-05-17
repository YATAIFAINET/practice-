package com.example.nick.joeyi_android2;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableRow;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 2015/11/26.
 */
public class shopping_res extends ListActivity {
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Handler mUI_Handler = new Handler();
    private Context context;
    public String ord_no;
    String ip = "", folder = "";
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> item;
    String login_id;
    String config="";
    String salesdate="",invoiceno="",membercode="",warehousecode="",stockistcode="",reference="",productname="",dealerprice="",quantity="",status="",matchingvalue="",pointvalue="";
    String res_str="";
    String my_rod_no="";
    String total_paid_money="";
    String total_money2="";
    public ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        setContentView(R.layout.my_account_order_list2);
        load_config();
        setTitle("購物完成");
        context = this;
        Bundle bundle = this.getIntent().getExtras();
        String aaa=bundle.getString("res");
        String []res=aaa.split(",");

        config = readFromFile("client_config");
        try {
            login_id = new JSONObject(config).getString("login_id");
        } catch (Exception e) {

        }

        if(res[0].equals("success")){
            ord_no =res[1].substring(0,12);

            create_thread("get_order_detail", "get_order_detail"); //取得訂單付款資訊、取貨資訊

        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        //new一個intent物件，並指定Activity切換的class
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), fra3_main.class);

        //new一個Bundle物件，並將要傳遞的資料傳入
        Bundle bundle = new Bundle();
        //將Bundle物件assign給intent
        intent.putExtras(bundle);
        finish();
        //切換Activity
        startActivity(intent);

        //Toast.makeText(context, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
    private String readFromFile(String FILENAME) {
        try{

            FileInputStream fin = this.openFileInput(FILENAME);
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
                        if (cmd == "get_order_detail") {
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
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("ord_no",ord_no));
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
        try
        {
            TextView ord_no = (TextView) findViewById(R.id.ord_no);
            TextView ord_date2 = (TextView) findViewById(R.id.ord_date2);
            TextView paytype = (TextView) findViewById(R.id.paytype);
            TextView total_money = (TextView) findViewById(R.id.total_money);
            TextView name_send = (TextView) findViewById(R.id.name_send);
            TextView tel_send = (TextView) findViewById(R.id.tel_send);
            TextView add_send = (TextView) findViewById(R.id.add_send);
            TextView send_method = (TextView) findViewById(R.id.send_method);
            TableRow tel = (TableRow) findViewById(R.id.tel);
            TableRow add = (TableRow) findViewById(R.id.add);
            JSONArray jsonArray = new JSONArray(input);
            String paytype_str="";
            String paid_money_str="";
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);

                total_paid_money=jsonData.getString("total_paid_money");
                total_money2=jsonData.getString("total_money");
                ord_no.setText(jsonData.getString("ord_no"));
                ord_date2.setText(jsonData.getString("ord_date2"));
                if(i>0){
                    paytype_str+=",";
                }
                paytype_str+=jsonData.getString("paytype");
                if(i>0){
                    paid_money_str+=",";
                }
                paid_money_str+=jsonData.getString("paid_money");

                name_send.setText(jsonData.getString("name_send"));
                send_method.setText(jsonData.getString("send_name"));
                if (jsonData.getString("send_method").equals("1")) {
                    tel.setVisibility(View.GONE);
                    add.setVisibility(View.GONE);
                } else {
                    tel_send.setText(jsonData.getString("tel_send"));
                    add_send.setText(jsonData.getString("add_send"));
                }
            }
            paytype.setText(paytype_str);
            total_money.setText(paid_money_str);
            create_thread2("get_order_detail2", "get_order_detail2"); //取得付款明細

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
                        if (cmd == "get_order_detail2") {
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
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("ord_no",ord_no));
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
            if(jsonArray.length()>0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    item = new HashMap<String, String>();
                    item.put("prod_name",jsonData.getString("prod_name"));
                    item.put("qty", jsonData.getString("qty"));
                    item.put("price","$ "+jsonData.getString("price"));
                    item.put("unit_pv",jsonData.getString("unit_pv"));
                    item.put("sub_money","$ "+jsonData.getString("sub_money"));
                    item.put("sub_pv",jsonData.getString("sub_pv"));
                    list.add(item);
                }
                adapter = new MySimpleAdapter(getApplication(), list, R.layout.my_account_order_list3,new String[]{ "prod_name", "qty", "price", "unit_pv", "sub_money", "sub_pv"},new int[]{R.id.prod_name, R.id.qty, R.id.price, R.id.unit_pv,R.id.total_price, R.id.total_pv});
                setListAdapter(adapter);
//                if(total_paid_money.equals(total_money2)){
//                    mDialog=ProgressDialog.show(this, "訂單同步", "訂單同步中...", true);
//                    create_thread3();//同步訂單
//                }

            }else{

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



    //----------
    public void create_thread3() {
        mThread = new HandlerThread("test");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery3("Synchronize_to_my");
                mUI_Handler.post(new Runnable() {
                    public void run() {

                            newlist3(jsonString);

                    }
                });
            }
        });
    }
    private String executeQuery3(String query)
    {
        String result = "";
        try
        {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("ord_no",ord_no));
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
    public final void newlist3(String input)
    {
        Log.d("leo20161201",input);
        //Toast.makeText(getApplicationContext(),tab_status, Toast.LENGTH_SHORT).show();
        //tab_status="cus";
        try
        {
            JSONArray jsonArray = new JSONArray(input);
            list = new ArrayList<HashMap<String,String>>();
            if(jsonArray.length()>0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    salesdate=jsonData.getString("salesdate");
                    invoiceno=jsonData.getString("invoiceno");
                    membercode=jsonData.getString("membercode");
                    warehousecode=jsonData.getString("warehousecode");
                    stockistcode=jsonData.getString("stockistcode");
                    reference=jsonData.getString("reference");
                    productname=jsonData.getString("productname");
                    dealerprice=jsonData.getString("dealerprice");
                    quantity=jsonData.getString("quantity");
                    status=jsonData.getString("status");
                    matchingvalue=jsonData.getString("matchingvalue");
                    pointvalue=jsonData.getString("pointvalue");
                    //
                    create_thread4();

                }

            }else{

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
    public void create_thread4() {
        mThread = new HandlerThread("test");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery4("Synchronize_to_my");
                mUI_Handler.post(new Runnable() {
                    public void run() {

                        newlist4(jsonString);

                    }
                });
            }
        });
    }
    private String executeQuery4(String query)
    {
        String result = "";
        try
        {


            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://twmembers.ganoeworldwide.com/xml/saveorder.asp");//正式
            //HttpPost post = new HttpPost("http://newtest321.ganoeworldwide.com/xml/saveorder.asp");//測試
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("salesdate",salesdate));
            nameValuePairs.add(new BasicNameValuePair("invoiceno",invoiceno));
            nameValuePairs.add(new BasicNameValuePair("membercode",membercode));
            nameValuePairs.add(new BasicNameValuePair("warehousecode",warehousecode));
            nameValuePairs.add(new BasicNameValuePair("stockistcode",stockistcode));
            nameValuePairs.add(new BasicNameValuePair("reference",reference));
            nameValuePairs.add(new BasicNameValuePair("productname",productname));
            nameValuePairs.add(new BasicNameValuePair("dealerprice",dealerprice));
            nameValuePairs.add(new BasicNameValuePair("quantity",quantity));
            nameValuePairs.add(new BasicNameValuePair("status",status));
            nameValuePairs.add(new BasicNameValuePair("matchingvalue",matchingvalue));
            nameValuePairs.add(new BasicNameValuePair("pointvalue",pointvalue));
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
    public final void newlist4(String input)
    {
        Log.d("leo20161201", input);
        res_str=input;
        create_thread5();


    }
    public void create_thread5() {//add_to_log
        mThread = new HandlerThread("test");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery5("add_to_log");
                mUI_Handler.post(new Runnable() {
                    public void run() {

                        newlist5(jsonString);

                    }
                });
            }
        });
    }
    private String executeQuery5(String query)
    {
        String result = "";
        try
        {


            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            String[] tmp=res_str.split("||");

            nameValuePairs.add(new BasicNameValuePair("cmd","save_to_log"));
            nameValuePairs.add(new BasicNameValuePair("ord_no",ord_no));
            nameValuePairs.add(new BasicNameValuePair("status",status));
            nameValuePairs.add(new BasicNameValuePair("mb_no",membercode));
            nameValuePairs.add(new BasicNameValuePair("text",res_str));


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
    public final void newlist5(String input)
    {
        Log.d("leo20161201", input);
        String[] tmp=input.split("\\|\\|");
        Log.d("leo20161201", tmp[0]);
        if(tmp[0].equals("1")){//成功
            my_rod_no=tmp[2];
            create_thread6();//UPDATE MY ORD_NO
        }else{

            Toast.makeText(shopping_res.this, "同步失敗", Toast.LENGTH_LONG).show();
            mDialog.dismiss();
        }



    }

    public void create_thread6() {//add_to_log
        mThread = new HandlerThread("test");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery6("add_to_log");
                mUI_Handler.post(new Runnable() {
                    public void run() {

                        newlist6(jsonString);

                    }
                });
            }
        });
    }
    private String executeQuery6(String query)
    {
        String result = "";
        try
        {


            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            String[] tmp=res_str.split("||");

            nameValuePairs.add(new BasicNameValuePair("ord_no",ord_no));
            nameValuePairs.add(new BasicNameValuePair("ord_no_my",my_rod_no));
            nameValuePairs.add(new BasicNameValuePair("cmd","edit_ord_no_my"));


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
    public final void newlist6(String input)
    {
        Log.d("leo20161201",input);
        //關閉dilog
        mDialog.dismiss();


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
            return v;
        }
    }

}
