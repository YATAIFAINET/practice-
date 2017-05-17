package com.example.nick.joeyi_android2;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class contact_often_qa extends Activity {
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Handler mUI_Handler = new Handler();
    private ListView listView;
    private String ip = "", folder = "";
    private ArrayList<HashMap<String,String>> list;
    private String []qa_number;
    private HashMap<String,String> item;
    String config="",kind;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayShowHomeEnabled(false);
        Bundle bundle = this.getIntent().getExtras();
        kind = bundle.getString("kind");
        setTitle(kind);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);

        load_config();
        config  = readFromFile("client_config");
        get_news_thread();
    }


    private String readFromFile(String FILENAME) {
        try{
            FileInputStream fin = getApplication().openFileInput(FILENAME);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_normal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;

        }
        //Toast.makeText(context, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
    public void get_news_thread() {
        mThread = new HandlerThread("get_contact_often");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_news_data();

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        get_news_data_res(jsonString);

                    }
                });
            }
        });
    }
    private String get_news_data() {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            //HttpPost post = new HttpPost("http://220.135.161.128/leo/android_sql.php");
            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", "get_contact_often"));
            nameValuePairs.add(new BasicNameValuePair("kind",kind));
            //post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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
    public final void get_news_data_res(String input) {
        Log.d("input",input);
        try {
            JSONArray jsonArray = new JSONArray(input);

            list = new ArrayList<HashMap<String,String>>();
            listView = (ListView)findViewById(R.id.listView_new);
            listView.setOnItemClickListener(listener);
            qa_number=new String[jsonArray.length()];
            //title=new String[jsonArray.length()];
            //post_time=new String[jsonArray.length()];
            //content=new String[jsonArray.length()];
            MySimpleAdapter listAdapter;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                item = new HashMap<String, String>();

                qa_number[i]=jsonData.getString("qa_number");
                // title[i]=jsonData.getString("title");
                //  post_time[i]=jsonData.getString("post_time");
                //  content[i]=jsonData.getString("content");
                // item.put("postTime", "【" +jsonData.getString("postTime") +"】");
                //Log.d("postTime",jsonData.getString("postTime"));
                item.put("title",jsonData.getString("ques_text"));
                //item.put("id",jsonData.getString("id"));

                list.add(item);

            }
            listAdapter = new MySimpleAdapter(this, list, R.layout.dis_news,new String[]{"title"},new int[]{R.id.dis_title});
            listView.setAdapter(listAdapter);




        } catch (JSONException e) {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

        }

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
    private class MySimpleAdapter extends SimpleAdapter {

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
    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,//parent就是ListView，view表示Item视图，position表示数据索引
                                long id) {

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("qa_number",qa_number[position]);
            //bundle.putString("title",title[position]);
            // bundle.putString("post_time",post_time[position]);
            //bundle.putString("content",content[position]);
            intent.putExtras(bundle);
            intent.setClass(contact_often_qa.this, contact_often_qa_detail.class);
            startActivity(intent);
        }
    };
}
