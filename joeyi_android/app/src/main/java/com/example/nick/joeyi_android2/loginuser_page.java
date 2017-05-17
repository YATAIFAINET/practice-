package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by User on 2016/4/27.
 */
public class loginuser_page extends Activity {

    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;

    String ip="",folder="",login_id="";
    String config="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        setTitle("組織最新動態");

        setContentView(R.layout.loginuser_page);
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        Log.d("MaxTAG", "Max memory is " + maxMemory + "KB");
        ConnectivityManager conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);//先取得此service
        NetworkInfo networInfo = conManager.getActiveNetworkInfo();       //在取得相關資訊

        if (networInfo == null || !networInfo.isAvailable()){ //判斷是否有網路
            new AlertDialog.Builder(this).setTitle("網路異常").setMessage("請先連上網路，再繼續使用！").setPositiveButton("確定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            }).show();
        }

        load_config();
        config  = readFromFile("client_config");
        load_data_thread("get_data", "get_data");
        String aaa;
        aaa=getAvailMemory();
        Log.d("testleoleo", aaa);

        ImageView image3=(ImageView)findViewById(R.id.imageView3);
        LinearLayout big_img=(LinearLayout)findViewById(R.id.big_bk);
        LinearLayout bg2=(LinearLayout)findViewById(R.id.bgbg2);
        LinearLayout circle1=(LinearLayout)findViewById(R.id.circle1);
        LinearLayout circle2=(LinearLayout)findViewById(R.id.circle2);
        LinearLayout circle3=(LinearLayout)findViewById(R.id.circle3);
        LinearLayout circle4=(LinearLayout)findViewById(R.id.circle4);
        load_bimap(image3,"http://220.135.161.128/afs_dale/login_up.png");

        load_bimap_layout(circle1, "http://220.135.161.128/afs_dale/login_new.png");
        load_bimap_layout(circle2, "http://220.135.161.128/afs_dale/login_total.png");
        load_bimap_layout(circle3, "http://220.135.161.128/afs_dale/login_left.png");
        load_bimap_layout(circle4,"http://220.135.161.128/afs_dale/login_right.png");
        load_bimap_layout(big_img, "http://220.135.161.128/afs_dale/login_back.png");
        load_bimap_layout(bg2,"http://220.135.161.128/afs_dale/bg2.jpg");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected", "onOptionsItemSelected ");
        switch(item.getItemId()) {
            case android.R.id.home:
                Log.d("onOptionsItemSelected2", "onOptionsItemSelected2 ");
                Intent intent = new Intent();
                intent.setClass(loginuser_page.this, test_page.class);

                LinearLayout big_bk=(LinearLayout)findViewById(R.id.all_view);

                ImageView image3=(ImageView)findViewById(R.id.imageView3);
                LinearLayout big_img=(LinearLayout)findViewById(R.id.big_bk);
                LinearLayout bg2=(LinearLayout)findViewById(R.id.bgbg2);
                LinearLayout circle1=(LinearLayout)findViewById(R.id.circle1);
                LinearLayout circle2=(LinearLayout)findViewById(R.id.circle2);
                LinearLayout circle3=(LinearLayout)findViewById(R.id.circle3);
                LinearLayout circle4=(LinearLayout)findViewById(R.id.circle4);






                image3.setImageDrawable(null);
                big_img.setBackground(null);
                bg2.setBackground(null);
                circle1.setBackground(null);
                circle2.setBackground(null);
                circle3.setBackground(null);
                circle4.setBackground(null);
                big_bk.setBackground(null);
                big_bk.removeAllViews();

                finish();

                startActivity(intent);

                return true;
            default:
                break;

        }
        //Toast.makeText(context, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
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
    public void load_data_thread(final String cmd,String thread_name) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        if (cmd == "get_data") {
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
            nameValuePairs.add(new BasicNameValuePair("login_id",login_id));
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
    public final void newlist(String input)
    {
        try
        {
            JSONArray jsonArray = new JSONArray(input);
            JSONObject jsonData = jsonArray.getJSONObject(0);
            TextView new_subtotal = (TextView)findViewById(R.id.new_subtotal);
            TextView sum_subtotal = (TextView)findViewById(R.id.sum_subtotal);
            TextView a_line_num = (TextView)findViewById(R.id.a_line_num);
            TextView b_line_num = (TextView)findViewById(R.id.b_line_num);
            Integer new_i=(int)(Math.floor(Double.parseDouble(jsonData.getString("new_subtotal"))));
            Integer sum_i=(int)(Math.floor(Double.parseDouble(jsonData.getString("sum_subtotal"))));
            String new_subtotal_a=Integer.toString(new_i);
            String sum_subtotal_a=Integer.toString(sum_i);
            new_subtotal.setText(new_subtotal_a);
            sum_subtotal.setText(sum_subtotal_a);
            a_line_num.setText(jsonData.getString("a_line_num"));
            b_line_num.setText(jsonData.getString("b_line_num"));
            if(new_subtotal_a.length()>7){
                new_subtotal.setTextSize(20);
            }else{
                new_subtotal.setTextSize(25);
            }
            if(sum_subtotal_a.length()>7){
                sum_subtotal.setTextSize(20);
            }else{
                sum_subtotal.setTextSize(25);
            }
            if(jsonData.getString("a_line_num").length()>7){
                a_line_num.setTextSize(20);
            }else{
                a_line_num.setTextSize(25);
            }
            if(jsonData.getString("b_line_num").length()>7){
                b_line_num.setTextSize(20);
            }else{
                b_line_num.setTextSize(25);
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
    private String readFromFile(String FILENAME) {
        try{
            FileInputStream fin = openFileInput(FILENAME );
            byte[] buff = new byte[fin.available()];
            fin.read(buff);
            String str = new String(buff);

            return str;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private void writeToFile(String FILENAME, String string) {
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
        }catch (Exception e){
        }
    }

    @Override
    public void onPause() {
        //移除畫面上的所有圖

        super.onPause();
    }
    private String getAvailMemory() {// 獲取android當前可用記憶體大小

        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
//mi.availMem; 當前系統的可用記憶體

        return Formatter.formatFileSize(getBaseContext(), mi.availMem);// 將獲取的記憶體大小規格化
    }

    private void load_bimap(final ImageView img,String url){
        //建立一個AsyncTask執行緒進行圖片讀取動作，並帶入圖片連結網址路徑


        new AsyncTask<String, Void, Bitmap>(){
            @Override
            protected Bitmap doInBackground(String... params)
            {
                String url = params[0];
                return getBitmapFromURL(url);
            }

            @Override
            protected void onPostExecute(Bitmap result)
            {
                img. setImageBitmap (result);
                super.onPostExecute(result);
            }
        }.execute(url);


    }


    //讀取網路圖片，型態為Bitmap
    private static Bitmap getBitmapFromURL(String imageUrl) {
        try
        {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;

            return bitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    private void load_bimap_layout(final LinearLayout img,String url){
        //建立一個AsyncTask執行緒進行圖片讀取動作，並帶入圖片連結網址路徑


        new AsyncTask<String, Void, Bitmap>(){
            @Override
            protected Bitmap doInBackground(String... params)
            {
                String url = params[0];
                return getBitmapFromURL(url);
            }

            @Override
            protected void onPostExecute(Bitmap result)
            {
                BitmapDrawable ob = new BitmapDrawable(getResources(), result);
                img.setBackground(ob);
                super.onPostExecute(result);
            }
        }.execute(url);


    }
}