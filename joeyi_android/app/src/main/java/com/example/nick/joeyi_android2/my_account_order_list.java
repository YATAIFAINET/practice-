package com.example.nick.joeyi_android2;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nick on 2015/11/26.
 */
public class my_account_order_list extends ListActivity {
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Handler mUI_Handler = new Handler();
    private Context context;
    public String ord_no;
    String ip = "", folder = "";
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        load_config();
        context = this;
        Bundle bundle = this.getIntent().getExtras();
        ord_no = bundle.getString("ord_no");
        create_thread("get_order_detail", "get_order_detail"); //取得訂單付款資訊、取貨資訊
        create_thread2("get_order_detail2", "get_order_detail2"); //取得付款明細
        setContentView(R.layout.my_account_order_list2);

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
        Log.d("fainettext","input"+input);
        //Toast.makeText(getApplicationContext(),tab_status, Toast.LENGTH_SHORT).show();
        //tab_status="cus";
        try
        {
            JSONArray jsonArray = new JSONArray(input);
            JSONObject jsonData = jsonArray.getJSONObject(0);
            TextView ord_no=(TextView)findViewById(R.id.ord_no);
            TextView ord_date2=(TextView)findViewById(R.id.ord_date2);
            TextView paytype=(TextView)findViewById(R.id.paytype);
            TextView total_money=(TextView)findViewById(R.id.total_money);
            TextView name_send=(TextView)findViewById(R.id.name_send);
            TextView tel_send=(TextView)findViewById(R.id.tel_send);
            TextView add_send=(TextView)findViewById(R.id.add_send);
            TextView send_method=(TextView)findViewById(R.id.send_method);
            TableRow tel=(TableRow)findViewById(R.id.tel);
            TableRow add=(TableRow)findViewById(R.id.add);
            TextView creat_fac=(TextView)findViewById(R.id.creat_fac);
            LinearLayout creat_fac_li=(LinearLayout)findViewById(R.id.creat_fac_li);
            creat_fac_li.setVisibility(View.GONE);
            ord_no.setText(jsonData.getString("ord_no"));
            ord_date2.setText(jsonData.getString("ord_date2"));
            paytype.setText(jsonData.getString("paytype"));
            total_money.setText(jsonData.getString("total_money"));
            name_send.setText(jsonData.getString("name_send"));
            send_method.setText(jsonData.getString("send_name"));
            if(jsonData.getString("send_method").equals("1")){
                tel.setVisibility(View.GONE);
                add.setVisibility(View.GONE);
            }else{
                tel_send.setText(jsonData.getString("tel_send"));
                add_send.setText(jsonData.getString("add_send"));
            }
            /*if(jsonData.getString("creat_fac").equals("-1")){
                creat_fac_li.setVisibility(View.GONE);
            }else{
                creat_fac_li.setVisibility(View.VISIBLE);
                creat_fac.setText(jsonData.getString("fac_name"));
            }*/
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
                    item.put("unit_mv",jsonData.getString("unit_mv"));
                    item.put("sub_money","$ "+jsonData.getString("sub_money"));
                    item.put("sub_pv",jsonData.getString("sub_pv"));
                    item.put("sub_mv",jsonData.getString("sub_mv"));
                    list.add(item);
                }
                adapter = new MySimpleAdapter(getApplication(), list, R.layout.my_account_order_list3,new String[]{ "prod_name", "qty", "price", "unit_pv","unit_mv", "sub_money", "sub_pv","sub_mv"},new int[]{R.id.prod_name, R.id.qty, R.id.price, R.id.unit_pv,R.id.unit_mv,R.id.total_price, R.id.total_pv,R.id.total_mv});
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
            return v;
        }
    }

}
