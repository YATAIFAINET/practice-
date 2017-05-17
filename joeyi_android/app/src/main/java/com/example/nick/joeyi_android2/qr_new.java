package com.example.nick.joeyi_android2;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
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

public class qr_new extends ListActivity {
    String ip = "", folder = "",config="",login_id;
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> item;
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Handler mUI_Handler = new Handler();
    private Button go_ecash_qr,go_epv_qr,go_qr_scan;
    String go_from="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        //设置屏幕方向为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_qr_new);
        load_config();
        setTitle("QRcode");

        config  = readFromFile("client_config");

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null && bundle.containsKey("mb_no"))
        {
            login_id = bundle.getString("mb_no");
            if (bundle != null && bundle.containsKey("go_from")) {

                go_from=bundle.getString("go_from");

            }
        }else{
            try {
                login_id = new JSONObject(config).getString("login_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        create_thread("get_cost_list", "get_cost_list");
        go_ecash_qr=(Button)findViewById(R.id.e_cash_co);
        go_epv_qr=(Button)findViewById(R.id.e_pv_co);
        go_qr_scan=(Button)findViewById(R.id.sw_qr);

        go_ecash_qr.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(go_from.equals("")){
                    Intent intent = new Intent(qr_new.this, ecash_cost_qr.class);
                    Bundle bundle2 = new Bundle();
                    intent.putExtras(bundle2);

                    startActivity(intent);

                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"請先登入", Toast.LENGTH_SHORT).show();

                }



            }

        });
        go_epv_qr.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(go_from.equals("")){
                    Intent intent = new Intent(qr_new.this, epv_cost_qr.class);
                    Bundle bundle2 = new Bundle();
                    intent.putExtras(bundle2);


                    startActivity(intent);

                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"請先登入", Toast.LENGTH_SHORT).show();

                }


            }

        });
        go_qr_scan.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(qr_new.this, qrcode.class);
                Bundle bundle2 = new Bundle();
                intent.putExtras(bundle2);


                startActivity(intent);

                finish();


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
        switch(item.getItemId()) {
            case android.R.id.home:
                if(go_from.equals("push")){
                    Intent intent = new Intent();
                    intent.setClass(qr_new.this, login.class);
                    finish();
                    startActivity(intent);
                }else{
                    finish();
                }

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
    public void create_thread(final String cmd,String thread_name) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        if (cmd == "get_cost_list") {
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
        Log.d("leo0523",input);

        try
        {
            JSONArray jsonArray = new JSONArray(input);
            list = new ArrayList<HashMap<String,String>>();
            if(jsonArray.length()>0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    item = new HashMap<String, String>();
                    item.put("order_no",jsonData.getString("ord_no"));
                    item.put("cost_point", jsonData.getString("cost_point"));
                    item.put("creat_fac",jsonData.getString("creat_fac"));
                    list.add(item);






                }
                adapter = new MySimpleAdapter(getApplication(), list, R.layout.cost_list,new String[]{ "order_no", "cost_point", "creat_fac"},new int[]{R.id.order_no, R.id.cost_point, R.id.creat_fac});
                setListAdapter(adapter);

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



            return v;
        }
    }

}
