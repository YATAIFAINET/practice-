package com.example.nick.joeyi_android2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class contact2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayAdapter<String> listAdapter_sex;
    private Button save_btn,btn_contact_old,often_qa;
    private ImageView img_goto;
    private String ip,folder;
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    String config="",login_id;
    public static String DOMAIN ="http://maps.googleapis.com/maps/api/geocode/json";
    public String addressstr;
    private String[] contact_spinner;
    private Spinner spinner;
    private ArrayAdapter qaList;
    private TextView mTextView_Url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隱藏logo
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        setTitle("聯絡我們");
        load_config();
        config  = readFromFile("client_config");
        //create_thread_old("get_contact_old", "get_contact_old");
        TextView address = (TextView) findViewById(R.id.address);
        addressstr=address.getText().toString();

        mTextView_Url = (TextView) findViewById(R.id.TextView_Web_Url);
        mTextView_Url.setText(Html.fromHtml("<u>" + getResources().getString(R.string.ours_web_txv) + "</u>"));
        spinner = (Spinner) findViewById(R.id.contact_spinner);
        btn_contact_old=(Button)findViewById(R.id.btn_contact_old);
        often_qa=(Button)findViewById(R.id.often_qa);
        save_btn=(Button)findViewById(R.id.save_mbst);
        save_btn.setOnClickListener(save_btn_listener);
        btn_contact_old.setOnClickListener(btn_contact_old_listener);
        often_qa.setOnClickListener(often_qa_listener);
        create_thread_spinner("get_qa_kind", "get_qa_kind");
        mTextView_Url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri= Uri.parse("https://www.google.com.tw/");
                Intent i=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(i);
            }
        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditText contact_title = (EditText) findViewById(R.id.contact_title);
                EditText contact_main = (EditText) findViewById(R.id.contact_main);
                //birth_date
                String err_str = "";
                if (spinner.getSelectedItem().toString().length() == 0) {
                    err_str += " 留言主題";
                }
                /*if (contact_title.getText().toString().length() == 0) {
                    err_str += " 留言主題";
                }*/
                if (contact_main.getText().toString().length() == 0) {
                    err_str += " 留言內容";
                }

                if (err_str.length() > 0) {
                    Toast.makeText(getApplicationContext(), err_str + "未填", Toast.LENGTH_LONG).show();
                    return;
                }

                create_thread("contact_us", "contact_us");
                save_btn.setEnabled(false);
                Toast.makeText(getApplicationContext(), "傳送中請稍候", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        create_thread_old("get_contact_old", "get_contact_old");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = getLatLngByAddress(addressstr);
        //LatLng sydney2 = getLatLngByAddress("台北市信義區東興路20號2樓");
        mMap.addMarker(new MarkerOptions().position(sydney).title("家樂易"));
        //mMap.addMarker(new MarkerOptions().position(sydney2).title("愛飛翔2"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

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
    private Button.OnClickListener save_btn_listener = new Button.OnClickListener() {
        public void onClick(View v) {
            //EditText contact_title = (EditText) findViewById(R.id.contact_title);
            EditText contact_main = (EditText) findViewById(R.id.contact_main);
            //birth_date
            String err_str = "";
            if(spinner.getSelectedItem().toString().length()==0){
                err_str += " 留言主題";
            }
            /*if (contact_title.getText().toString().length() == 0) {
                err_str += " 留言主題";
            }*/
            if (contact_main.getText().toString().length() == 0) {
                err_str += " 留言內容";
            }

            if (err_str.length() > 0) {
                Toast.makeText(getApplicationContext(), err_str + "未填", Toast.LENGTH_LONG).show();
                return;
            }

            create_thread("contact_us", "contact_us");
            save_btn.setEnabled(false);

            Toast.makeText(getApplicationContext(), "傳送中請稍候", Toast.LENGTH_SHORT).show();
        }
    };
    private Button.OnClickListener btn_contact_old_listener = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(contact2.this, contact_reply.class);
            startActivity(intent);
        }
    };
    private Button.OnClickListener often_qa_listener = new Button.OnClickListener() {
        public void onClick(View v) {
            if(spinner.getSelectedItem().toString().length()==0){
                Toast.makeText(getApplicationContext(), "請選擇類別", Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("kind",spinner.getSelectedItem().toString());
                intent.putExtras(bundle);
                intent.setClass(contact2.this, contact_often_qa.class);
                startActivity(intent);
            }
        }
    };
    public void create_thread_old(final String cmd,String thread_name) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery_old(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        if (cmd == "get_contact_old") {
                            newlist_old(jsonString);
                        }
                    }
                });
            }
        });
    }

    private String executeQuery_old(String query)
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
    public final void newlist_old(String input)
    {
        btn_contact_old=(Button)findViewById(R.id.btn_contact_old);
        img_goto=(ImageView)findViewById(R.id.img_goto);
        if(input.equals("1\n")){    //有沒看過的
            btn_contact_old.setText("您有一則新回覆");
            btn_contact_old.setTextColor(0xFFeb0000);
           // img_goto.setBackground(getResources().getDrawable(R.drawable.contact_m, getTheme()));
            if(android.os.Build.VERSION.SDK_INT >= 21){
                // rBlack = getResources().getDrawable(R.drawable.rblack, getTheme());
                img_goto.setBackground(getResources().getDrawable(R.drawable.contact_m, getTheme()));
            } else {
                //rBlack = getResources().getDrawable(R.drawable.rblack);
                img_goto.setBackground(getResources().getDrawable(R.drawable.contact_m));
            }
        }else{  //全部都看過
            btn_contact_old.setText("查看客服回覆訊息");
            btn_contact_old.setTextColor(0xFFc13701);
            if(android.os.Build.VERSION.SDK_INT >= 21){
               // rBlack = getResources().getDrawable(R.drawable.rblack, getTheme());
                img_goto.setBackground(getResources().getDrawable(R.drawable.icon, getTheme()));
            } else {
                //rBlack = getResources().getDrawable(R.drawable.rblack);
                img_goto.setBackground(getResources().getDrawable(R.drawable.icon));
            }

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
                        if (cmd == "contact_us") {
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
        //TextView contact_title=(TextView)findViewById(R.id.contact_title);
        TextView contact_main=(TextView)findViewById(R.id.contact_main);
        try
        {
            login_id = new JSONObject(config).getString("login_id");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("mb_no",login_id));
            nameValuePairs.add(new BasicNameValuePair("title",spinner.getSelectedItem().toString()));
            nameValuePairs.add(new BasicNameValuePair("content",contact_main.getText().toString()));
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
        EditText contact_main=(EditText)findViewById(R.id.contact_main);
        if(input.equals("1\n")){

            Toast.makeText(getApplication(), "感謝您的留言，我們會儘快與您聯絡", Toast.LENGTH_SHORT).show();
            contact_main.setText("");
            save_btn.setEnabled(true);
        }else{
            Toast.makeText(getApplication(), "留言錯誤", Toast.LENGTH_SHORT).show();
            contact_main.setText("");
            save_btn.setEnabled(true);

        }

    }
    public void create_thread_spinner(final String cmd,String thread_name) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery_spinner(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        if (cmd == "get_qa_kind") {
                            newSpinner(jsonString);
                        }
                    }
                });
            }
        });
    }
    private String executeQuery_spinner(String query)
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
    public final void newSpinner(String input)
    {
        //Toast.makeText(getApplicationContext(),tab_status, Toast.LENGTH_SHORT).show();
        //tab_status="cus";
        spinner = (Spinner) findViewById(R.id.contact_spinner);
        try
        {
            JSONArray jsonArray = new JSONArray(input);
            contact_spinner = new String[jsonArray.length()];
            //WEEK_NO[0]="請選擇";
            for (int i = 0; i <jsonArray.length(); i++)
            {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                contact_spinner[i]=jsonData.getString("name");
            }
            qaList = new ArrayAdapter<String>(contact2.this,R.layout.spinnerstyle_qa, contact_spinner);
            spinner.setAdapter(qaList);
            spinner.setSelection(0, true);

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
    // 同步取得 LatLng
    public static LatLng getLatLngByAddress(String address) {
        LatLng latLng = null;
        AddressToLatLng ga = new AddressToLatLng(address);
        FutureTask<LatLng> future = new FutureTask<LatLng>(ga);
        new Thread(future).start();
        try {
            latLng = future.get();
        } catch (Exception e) {

        }
        return latLng;
    }
    // Address 轉 LatLng
    private static class AddressToLatLng implements Callable<LatLng> {
        private String queryURLString = DOMAIN
                + "?address=%s&sensor=false&language=zh_tw";
        private String address;

        AddressToLatLng(String address) {
            this.address = address;
        }

        @Override
        public LatLng call() {
            LatLng latLng = null;
            try {
                // 輸入地址得到緯經度(中文地址需透過URLEncoder編碼)
                latLng = getLocationByAddress(URLEncoder.encode(address,
                        "UTF-8"));
            } catch (UnsupportedEncodingException e) {
            }
            return latLng;
        }

        private LatLng getLocationByAddress(String address) {
            String urlString = String.format(queryURLString, address);
            LatLng latLng = null;
            try {
                // 取得 json string
                String jsonStr = HttpUtil.get(urlString);
                // 取得 json 根陣列節點 results
                JSONArray results = new JSONObject(jsonStr)
                        .getJSONArray("results");
                System.out.println("results.length() : " + results.length());
                if (results.length() >= 1) {
                    // 取得 results[0]
                    JSONObject jsonObject = results.getJSONObject(0);
                    // 取得 geometry --> location 物件
                    JSONObject laln = jsonObject.getJSONObject("geometry")
                            .getJSONObject("location");

                    latLng = new LatLng(Double.parseDouble(laln
                            .getString("lat")), Double.parseDouble(laln
                            .getString("lng")));
                }

            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
            return latLng;
        }
    }
    private static class HttpUtil {
        public static String get(String urlString) throws Exception {
            InputStream is = null;
            Reader reader = null;
            StringBuilder str = new StringBuilder();
            URL url = new URL(urlString);
            URLConnection URLConn = url.openConnection();
            URLConn.setRequestProperty("User-agent", "IE/6.0");
            is = URLConn.getInputStream();
            reader = new InputStreamReader(is, "UTF-8");
            char[] buffer = new char[1];
            while (reader.read(buffer) != -1) {
                str.append(new String(buffer));
            }
            return str.toString();
        }
    }
}
