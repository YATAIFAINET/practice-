package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Give_bonus_ListPage extends Activity {
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Handler mUI_Handler = new Handler();
    private Context context;
    String ip = "", folder = "",config="",login_id,from;
    private TextView mTextView_Total_Bonus;
    private String Total_bonus="";
    private ListView mListView_Exchange;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        load_config();
        getActionBar().setTitle("戶轉紀錄");
        Bundle bundle = this.getIntent().getExtras();
        Total_bonus = bundle.getString("can_use_bonus");
        config  = readFromFile("client_config");
        context = this;
        setContentView(R.layout.give_bonus_list_page);
        init();
        mTextView_Total_Bonus.setText(Total_bonus);
        Get_Exchange_thread();
    }

    //-----------------------------------------------------------------------
    public void Get_Exchange_thread() {
        mThread = new HandlerThread("Get_Exchange_thread");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery("get_e_cash_exchange");
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        try {
                            newlist(jsonString);
                        } catch (JSONException e) {
                            e.printStackTrace();
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
    public final void newlist(String input) throws JSONException {
        JSONArray jsonArray = new JSONArray(input);

        List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            String time_str = TimeStampToDate(Long.parseLong(jsonArray.getJSONObject(i).getString("date")));
            item.put("date", time_str);
            item.put("mb_no", jsonArray.getJSONObject(i).getString("mb_no"));
            item.put("to_mb_no", jsonArray.getJSONObject(i).getString("to_mb_no"));
            item.put("point", jsonArray.getJSONObject(i).getString("point"));
            items.add(item);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                items, R.layout.exchange_list, new String[]{"date", "mb_no", "to_mb_no", "point"},
                new int[]{R.id.date,R.id.mb_no, R.id.to_mb_no, R.id.point });
        mListView_Exchange.setAdapter(simpleAdapter);
    }
    //-------------------------------------------------------------------

    private void init(){
        mTextView_Total_Bonus = (TextView) findViewById(R.id.can_use_bonus);
        mListView_Exchange = (ListView) findViewById(R.id.listView_exchange);
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

    public String TimeStampToDate(long time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = df.format(time*1000);
        String[] temp = str.split("-");
        temp[2]=temp[2].substring(0,2);
        String tmp=temp[0]+"-"+temp[1]+"-"+temp[2];
        return tmp;
    }

}
