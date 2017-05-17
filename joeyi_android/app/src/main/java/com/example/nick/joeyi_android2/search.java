package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class search extends Activity {
    private ListView ls;
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Handler mUI_Handler = new Handler();
    private HashMap<String,String> item2;
    private ArrayList<HashMap<String,String>> list2;
    private EditText textView;
    String []pro_no;
    String ip = "", folder = "";
    LinearLayout go_shopping,go_kind,go_account,go_index,go_cart;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隱藏logo
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        getActionBar().setCustomView(R.layout.search_actionbar);

        View vv=getActionBar().getCustomView();
        load_config();
        textView = (EditText) vv.findViewById(R.id.search_txt);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textView.getText().toString().equals("請輸入商品關鍵字"))
                    textView.setText("");
            }
        });
        // textView.setHint("請輸入商品關鍵字");
        //textView.setHintTextColor(Color.parseColor("#c37b3a"));
        ls=(ListView)findViewById(R.id.list);
        textView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Log.d("performSearch", "onEditorAction ");
                    get_product();
                    return true;
                }
                return false;
            }
        });
        ImageView search =(ImageView)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_product();

            }
        });
        Bundle bundle = this.getIntent().getExtras();
        String from = bundle.getString("from");
        if(from==null){

        }else if(from.equals("0")){
            TextView a=(TextView)findViewById(R.id.shopping_txt);
            ImageView b=(ImageView)findViewById(R.id.shopping_icon);
            a.setTextColor(0xFF107ac1);
            b.setImageResource(R.drawable.icon_home2);

        }else if(from.equals("1")){
            TextView a=(TextView)findViewById(R.id.kind_txt);
            ImageView b=(ImageView)findViewById(R.id.kind_icon);
            a.setTextColor(0xFF107ac1);
            b.setImageResource(R.drawable.icon_type2);

        }
        go_shopping=(LinearLayout)findViewById(R.id.go_shopping);
        go_kind=(LinearLayout)findViewById(R.id.go_kind);
        go_account=(LinearLayout)findViewById(R.id.go_account);
        go_index=(LinearLayout)findViewById(R.id.go_index);
        go_cart=(LinearLayout)findViewById(R.id.go_cart);

        go_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), fra3_main.class);

                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle = new Bundle();
                bundle.putString("go_tab", "2");

                //將Bundle物件assign給intent
                intent.putExtras(bundle);

                //切換Activity
                startActivity(intent);            }
        });

        go_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), fra3_main.class);

                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle = new Bundle();
                bundle.putString("go_tab", "0");

                //將Bundle物件assign給intent
                intent.putExtras(bundle);

                //切換Activity
                startActivity(intent);
            }
        });
        go_kind.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), fra3_main.class);

                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle = new Bundle();
                bundle.putString("go_tab", "1");

                //將Bundle物件assign給intent
                intent.putExtras(bundle);

                //切換Activity
                startActivity(intent);

            }
        });
        go_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), fra3_main.class);

                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle = new Bundle();
                bundle.putString("go_tab", "3");

                //將Bundle物件assign給intent
                intent.putExtras(bundle);

                //切換Activity
                startActivity(intent);

            }
        });
        go_index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back_menu();

            }
        });
        get_product();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Itemid", String.valueOf(item.getItemId()));
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.search:
                get_product();
            default:
                break;

        }
        //Toast.makeText(context, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
    public void get_product() {
        mThread = new HandlerThread("get_kind_product");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_product_data();

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        renew_product(jsonString);

                    }
                });
            }
        });
    }
    private String get_product_data() {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            //HttpPost post = new HttpPost("http://220.135.161.128/leo/android_sql.php");
            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", "search_product"));

            if(textView.getText().toString().equals("請輸入商品關鍵字")){
                nameValuePairs.add(new BasicNameValuePair("query","" ));
            }else{
                nameValuePairs.add(new BasicNameValuePair("query",textView.getText().toString() ));
            }

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

    public final void renew_product(String input) {
        Log.d("renew_kind_product",input);
        try {
            JSONArray jsonArray = new JSONArray(input);

            list2 = new ArrayList<HashMap<String,String>>();
            ls.setOnItemClickListener(listener2);
            pro_no=new String[jsonArray.length()];
            MySimpleAdapter2 listAdapter2;
            if(jsonArray.length()>0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    item2 = new HashMap<String, String>();

                    pro_no[i] = jsonData.getString("prod_no");

                    item2.put("prod_name", "" + jsonData.getString("prod_name"));
                    item2.put("comp_price", "" + jsonData.getString("comp_price"));
                    item2.put("pv", "" + jsonData.getString("pv"));
                    item2.put("pic_name", "" + jsonData.getString("name"));
                    item2.put("prod_summary", "" + jsonData.getString("prod_summary"));

                    list2.add(item2);

                }
                listAdapter2 = new MySimpleAdapter2(this, list2, R.layout.product_list,new String[]{"prod_no", "prod_name", "comp_price", "pv", "pic_name","prod_summary"},new int[]{R.id.txv_product_id, R.id.txv_product_name, R.id.txv_product_money, R.id.txv_product_pv, R.id.img_name,R.id.prod_summary});
                ls.setAdapter(listAdapter2);
                ls.setVisibility(View.VISIBLE);
                findViewById(R.id.no_product).setVisibility(View.GONE);

            }else{
            /*    pro_no=new String[1];
                item2 = new HashMap<String, String>();
                pro_no[0] ="目前尚無產品";
                item2.put("prod_no", "目前尚無產品");
                list2.add(item2);
                listAdapter2 = new MySimpleAdapter2(this, list2, R.layout.product_list,new String[]{"prod_no"},new int[]{R.id.txv_product_name});
                ls.setAdapter(listAdapter2);*/
                findViewById(R.id.no_product).setVisibility(View.VISIBLE);
                ls.setVisibility(View.GONE);
                TextView txt=(TextView)findViewById(R.id.no_search);
                txt.setText("很抱歉,找不到「"+textView.getText().toString()+"」相關的商品");

            }

        } catch (JSONException e) {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

        }

    }
    private AdapterView.OnItemClickListener listener2 = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,//parent就是ListView，view表示Item视图，position表示数据索引
                                long id) {


            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            Log.d("pro_no[position]",pro_no[position]);
            if(pro_no[position]!="目前尚無產品"){
                bundle.putString("prod_no", pro_no[position] );
                bundle.putString("from","1");
                intent.putExtras(bundle);
                intent.setClass(search.this, product_detail_list.class);
                startActivity(intent);
            }

        }
    };
    private class MySimpleAdapter2 extends SimpleAdapter {
        private LayoutInflater mInflater;
        private String Paths;
        private Bitmap loadingIcon;
        private AsyncImageFileLoader asyncImageFileLoader;
        View mGridView = null;
        public MySimpleAdapter2(Context context,
                                List<? extends Map<String, ?>> data, int resource,
                                String[] from, int[] to) {
            super(context, data, resource, from, to);

            mInflater = LayoutInflater.from(context);
            loadingIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.coming_soon_b);
            mGridView = mInflater.inflate(R.layout.shopping_layout_old, null);

            asyncImageFileLoader = new AsyncImageFileLoader();
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            ViewHolder mHolder;
            final View v= super.getView(position, convertView, parent);

         /*   if(pro_no[0].equals("目前尚無產品")){
                v.findViewById(R.id.NTD).setVisibility(View.GONE);
                v.findViewById(R.id.PV).setVisibility(View.GONE);
                v.findViewById(R.id.imageView2).setVisibility(View.GONE);
                TextView a=(TextView)v.findViewById(R.id.txv_product_name);
                a.setTextSize(25);

            }*/
            TextView img_name=(TextView)v.findViewById(R.id.img_name);
            Paths = "http://"+ip+"/ct/shoppingcart/prod_images/180180/"+img_name.getText().toString();
            Log.d("now_path", Paths);
            if(convertView == null)
            {
                convertView = v;
                mHolder = new ViewHolder();
                mHolder.icon = (ImageView) convertView.findViewById(R.id.imageView2);
                convertView.setTag(mHolder);
            }
            else
            {
                mHolder = (ViewHolder) convertView.getTag();
            }

            //設定此mHolder.icon的tag為檔名，讓之後的callback function可以針對此mHolder.icon替換圖片
            ImageView imageView = mHolder.icon;
            imageView.setTag(Paths);

            Bitmap cachedBitmap = asyncImageFileLoader.loadBitmap(Paths, 200, 200, new AsyncImageFileLoader.ImageCallback() {
                @Override
                public void imageCallback(Bitmap imageBitmap, String imageFile) {
                    // 利用檔案名稱找尋當前mHolder.icon
                    ImageView imageViewByTag = (ImageView) v.findViewWithTag(imageFile);
                    if (imageViewByTag != null) {
                        if (imageBitmap != null)
                            imageViewByTag.setImageBitmap(imageBitmap);
                    }
                }
            });

            if(cachedBitmap != null){
                mHolder.icon.setImageBitmap(cachedBitmap);
            }else{
                mHolder.icon.setImageBitmap(loadingIcon); //顯示預設的圖片
            }
            return convertView;
        }
        private class ViewHolder
        {
            ImageView icon;
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
    void back_menu(){
        Intent i = new Intent(this,test_page.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        startActivity(i);
        finish();



    }

}