package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
 * Created by User on 2016/1/19.
 */

public class qr_only_favo extends Activity {
    private TextView tv;
    private Button add;
    private String[] kind_data={"食","衣","住","行","育","樂"};
    private String[] kind_data_value={"1","2","3","4","5","6"};
    private ArrayAdapter<String> kind_dataAdapter;
    private Spinner kind;
    private int kind_select=0;
    String ip = "", folder = "",config="",login_id;
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Handler mUI_Handler = new Handler();
    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> item;
    private SimpleAdapter adapter;
    private ListView list_view;
    int kind_select2=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        setContentView(R.layout.qr_only_favo);
        setTitle("我的最愛");
        load_config();
        config  = readFromFile("client_config");
        try {
            login_id = new JSONObject(config).getString("login_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        kind_dataAdapter = new ArrayAdapter<String>(this,R.layout.myspinner,kind_data);
        kind_dataAdapter.setDropDownViewResource(R.layout.myspinner);
        kind = (Spinner) findViewById(R.id.kind);
        kind.setAdapter(kind_dataAdapter);

        kind.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                //讀取第一個下拉選單是選擇第幾個
                int pos = kind.getSelectedItemPosition();
                kind_select = pos;
                get_favo_data("get_favo_data","get_favo_data");



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Intent intent=new Intent(qr_only_favo.this,qrcode.class);
                startActivity(intent);
                finish();
                return true;
            default:
                break;

        }
        //Toast.makeText(context, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    public void get_favo_data(final String cmd,String thread_name) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_favo_data_q(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {

                        get_favo_data_res(jsonString);
                    }

                });
            }
        });
    }
    private String get_favo_data_q(String query)
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
            nameValuePairs.add(new BasicNameValuePair("kind",kind_data_value[kind_select]));

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
    public final void get_favo_data_res(String input)
    {
        Log.d("input0106", input);
        list_view=(ListView)findViewById(R.id.favo_list);
        try
        {
            JSONArray jsonArray = new JSONArray(input);
            list = new ArrayList<HashMap<String,String>>();
            Log.d("input0106", String.valueOf(jsonArray.length()));
            if(jsonArray.length()>0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    item = new HashMap<String, String>();
                    item.put("url",jsonData.getString("url"));
                    item.put("tittle",jsonData.getString("tittle"));
                    list.add(item);
                }

                adapter = new MySimpleAdapter(getApplication(), list, R.layout.qr_code_favo_list,new String[]{ "url","tittle"},new int[]{R.id.url,R.id.tittle});
                list_view.setAdapter(adapter);
                list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView url_tv = (TextView) view.findViewById(R.id.url);
                        TextView title_tv = (TextView) view.findViewById(R.id.tittle);

                        String tittle = title_tv.getText().toString();
                        String tmpurl = url_tv.getText().toString();
                        // tmpurl=tmpurl.replace("http://","");
                        //tmpurl=tmpurl.replace("https://","");
                        //tmpurl="https://"+tmpurl;
                        Uri uri = Uri.parse(tmpurl);

                        Log.d("uri", String.valueOf(uri));
                          /*  Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);*/
                        //finish();
                        shareTo(tmpurl, tittle);


                    }
                });
                list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                    public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                                   int index, long arg3) {
                        TextView url_tv = (TextView) v.findViewById(R.id.url);
                        String tmpurl = url_tv.getText().toString();

                        Uri uri = Uri.parse(tmpurl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        finish();
                        return false;
                    }
                });

            }else{
                item = new HashMap<String, String>();
                item.put("url", "目前此類別無記錄");
                list.add(item);
                adapter = new MySimpleAdapter(getApplication(), list, R.layout.qrcode_list,new String[]{ "url"},new int[]{R.id.url});
                list_view.setAdapter(adapter);
            }
        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            //Toast.makeText(getApplication(), "" + e.toString(), Toast.LENGTH_LONG).show();
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


            Log.d("position", String.valueOf(position));

            final View v= super.getView(position, convertView, parent);
            return v;
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
    private void shareTo(String url,String title) {

        String shareText = "分享網址:"; //
        shareText+=title;
        shareText+=url;
        //Uri imageUri = Uri.parse("android.resource://" + getPackageName() + "/drawable/" + "ic_launcher"); //分享圖片至gmail、twitter可，line、facebook不行
        //Log.i("imageUri:", imageUri + "");
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain"); //文字檔類型
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText); //傳送文字
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "分享"));
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent();
            intent.setClass(qr_only_favo.this, qrcode.class);
            finish();
            startActivity(intent);
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }

}