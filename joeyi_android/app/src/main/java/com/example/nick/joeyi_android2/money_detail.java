package com.example.nick.joeyi_android2;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class money_detail extends ListActivity {

    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private ArrayAdapter lunchList;
    private MySimpleAdapter adapter;
    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> item;
    private double sumtotal=0;
    String config="",login_id;
    public String detail_kind,week_no;
    String ip="",folder="";
    TextView money_k;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.money_detail);
        load_config();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        config  = readFromFile("client_config");
        Bundle bundle =this.getIntent().getExtras();
        detail_kind = bundle.getString("detail_kind");
        money_k=(TextView)findViewById(R.id.money_kind);
        String tmp=detail_kind;
        tmp=tmp.replace("[", "");
        tmp=tmp.replace("]","");
        setTitle("獎金查詢");


        money_k.setText(tmp);
        week_no = bundle.getString("week_no");
        Log.d("week_no",week_no);
        //ColorDrawable colorDrawable = new ColorDrawable();
        //colorDrawable.setColor(Color.parseColor("#fce0cd"));
        //getActionBar().setBackgroundDrawable(colorDrawable);
        create_thread("get_money_detail", "get_money_detail");

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
                        if (cmd == "get_money_detail") {
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
            nameValuePairs.add(new BasicNameValuePair("detail_kind",detail_kind));
            nameValuePairs.add(new BasicNameValuePair("week_no",week_no));
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
    public final void newlist(String input)
    {
        //Toast.makeText(getApplicationContext(),tab_status, Toast.LENGTH_SHORT).show();
        //tab_status="cus";
        try
        {
            JSONArray jsonArray = new JSONArray(input);
            list = new ArrayList<HashMap<String,String>>();

            for (int i = 0; i < jsonArray.length(); i++) {
                Log.d("list_data_for", "1");
                JSONObject jsonData = jsonArray.getJSONObject(i);
                item = new HashMap<String, String>();
                item.put("yymm", "" + jsonData.getString("yymm"));
                item.put("detail_kind", "" + jsonData.getString("detail_kind"));
                item.put("details", "" + jsonData.getString("details"));
                item.put( "under_mb_no",""+jsonData.getString("under_mb_no"));
                item.put( "under_mb_name",""+jsonData.getString("under_mb_name"));
                item.put( "subtotal","$ "+jsonData.getString("subtotal"));
                item.put( "detail_kind",""+jsonData.getString("detail_kind"));
                sumtotal+=jsonData.getDouble("subtotal");

                list.add(item);
            }
            adapter = new MySimpleAdapter(this, list, R.layout.money_detail_list,new String[]{ "under_mb_no", "details", "under_mb_name", "subtotal"},new int[]{R.id.tv_money_from_no, R.id.tv_money_detail, R.id.tv_money_from_name, R.id.tv_money_total});
            setListAdapter(adapter);
            TextView tv_money_sum_total=(TextView)findViewById(R.id.tv_money_sum_total);
            tv_money_sum_total.setText("$ "+String.format("%.1f",sumtotal));
        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Toast.makeText(getApplication(), "" + e.toString(), Toast.LENGTH_LONG).show();
            Log.e("json", e.toString());
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

            final View v= super.getView(position, convertView, parent);

            Log.d("position", String.valueOf(position));

            if(position%2==0){
                (v.findViewById(R.id.money_bk_lay)).setBackgroundColor(Color.parseColor("#ffffff"));

            }else{
                (v.findViewById(R.id.money_bk_lay)).setBackgroundColor(Color.parseColor("#fdf3eb"));

            }


            return v;
        }
    }

}
