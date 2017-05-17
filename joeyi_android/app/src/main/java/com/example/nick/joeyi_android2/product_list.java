package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class product_list extends Activity {

    private String[] WEEK_NO;
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Spinner spinner;
    private ArrayAdapter lunchList;
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> item;
    public TextView hide_prod_no;
    String config="",login_no;
    GridLayout gridLayout;


    private DefaultHttpClient httpClient;
    private HttpPost httpPost;
    private HttpEntity httpEntity;
    private HttpResponse httpResponse;
    public static String PHPSESSID = null;
    public String click_link[];
    String ip="",folder="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_layout);
        load_config();
        create_thread("get_product", "get_product");

        /*
        btn_my_shopping = (Button) findViewById(R.id.btn_my_shopping);
        btn_product_kind = (Button) findViewById(R.id.btn_product_kind);
        btn_product_ser = (Button) findViewById(R.id.btn_product_ser);

        btn_my_shopping.setOnClickListener(btn_my_shopping_listener);
        btn_product_kind.setOnClickListener(btn_product_kind_listener);
        btn_product_ser.setOnClickListener(btn_product_ser_listener);*/


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
    private Button.OnClickListener btn_my_shopping_listener = new Button.OnClickListener() {
        public void onClick(View v) {

            Intent intent = new Intent();
            intent.setClass(product_list.this, pay_list.class);
            startActivity(intent);

        }
    };
    private Button.OnClickListener btn_product_kind_listener = new Button.OnClickListener() {
        public void onClick(View v) {

            Intent intent = new Intent();
            intent.setClass(product_list.this, product_list.class);
            startActivity(intent);
        }
    };
    private Button.OnClickListener btn_product_ser_listener = new Button.OnClickListener() {
        public void onClick(View v) {

        }
    };
    public void create_thread(final String cmd,String thread_name) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        if (cmd == "get_product") {
                            newProduct(jsonString);
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
    public final void newProduct(String input)
    {
        try
        {
            JSONArray jsonArray = new JSONArray(input);
            list = new ArrayList<HashMap<String,String>>();
            click_link=new String[jsonArray.length()];
            if(jsonArray.length()>0) {
                gridLayout = (GridLayout) findViewById(R.id.root);
                gridLayout.removeAllViews();
                gridLayout.setRowCount((jsonArray.length()/2)+1);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    LayoutInflater inflater = LayoutInflater.from(this);
                    View v=inflater.inflate(R.layout.product_list_2, null);
                    v.setPadding(3, 3, 3, 10);

                    LinearLayout product_list_2 = (LinearLayout)v.findViewById(R.id.product_list_2);

                    hide_prod_no = (TextView)v.findViewById(R.id.hide_prod_no);
                    TextView txv_product_name = (TextView)v.findViewById(R.id.txv_product_name);
                    TextView txv_product_money = (TextView)v.findViewById(R.id.txv_product_money);
                    TextView txv_product_pv = (TextView)v.findViewById(R.id.txv_product_pv);
                    TextView txv_product_standard = (TextView)v.findViewById(R.id.txv_product_standard);
                    TextView txv_product_no = (TextView)v.findViewById(R.id.txv_product_no);


                    hide_prod_no.setText(jsonData.getString("prod_no"));
                    txv_product_no.setText("【" + jsonData.getString("prod_no") + "】");
                    txv_product_name.setText(jsonData.getString("prod_name"));
                    txv_product_money.setText(jsonData.getString("comp_price"));
                    txv_product_pv.setText(jsonData.getString("pv"));
                    txv_product_standard.setText(jsonData.getString("standard"));

                    click_link[i]=jsonData.getString("prod_no");

                    //?置?按?的字?大小
               /*     bn.setWidth(115);
                    bn.setHeight(130);*/
                    //指定??件所在的行
                    GridLayout.Spec rowSpec = GridLayout.spec((i/2));
                    //指定??件所在的列
                    GridLayout.Spec columnSpec = GridLayout.spec(i%2);
                    String msg ="rowSpec:"+(i / 4+2)+" - columnSpec:"+(i%2);
                    Log.d("tag", msg);
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec,columnSpec);
                    //指定??件占?容器
                    params.setGravity(Gravity.CENTER);
                    gridLayout.addView(v, params);
                    v.setId(i);
                    v.setOnClickListener(gridLayout_listener);
/*
                    item = new HashMap<String, String>();
                    item.put("prod_no", "【" + jsonData.getString("prod_no")+"】");
                    item.put("prod_name", "" + jsonData.getString("prod_name"));
                    item.put( "comp_price",""+jsonData.getString("comp_price"));
                    item.put("pv", "" + jsonData.getString("pv"));
                    item.put("standard", "" + jsonData.getString("standard"));

                    list.add(item);*/
                }

            }
        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

        }
    }
    private Button.OnClickListener gridLayout_listener = new Button.OnClickListener() {
        public void onClick(View v) {
            //Toast.makeText(getApplicationContext(), click_link[v.getId()], Toast.LENGTH_LONG).show();

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("prod_no",click_link[v.getId()]);
            intent.putExtras(bundle);
            intent.setClass(product_list.this, product_detail_list.class);
            startActivity(intent);


        }
    };

}
