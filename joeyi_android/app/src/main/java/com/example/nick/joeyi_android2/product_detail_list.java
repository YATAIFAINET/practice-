package com.example.nick.joeyi_android2;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class product_detail_list extends ListActivity {
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Handler mUI_Handler = new Handler();
    private Context context;
    private String Paths;
    private Button add_cart, btn_youtube, btn_return_product_list, btn_go_pay;
    private AsyncImageFileLoader asyncImageFileLoader;
    private Bitmap loadingIcon;
    private FrameLayout.LayoutParams laParams;
    ViewPager pager = null;
    ArrayList<View> viewContainter = new ArrayList<View>();
    private TextView txv_product_name, txv_product_id, txv_product_money, txv_youtube_url, txv_product_pv,txv_product_mv,txv_product_summary,txv_product_standard;
    View mGridView = null;
    public String prod_no;
    public int pic_num;
    String ip = "", folder = "";
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> item;
    private int complete_pic =0;
    private Boolean loadding_flag=false;
    LinearLayout go_shopping,go_kind,go_account,go_index,go_cart;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        setContentView(R.layout.product_detail_list);
        load_config();
        context = this;
        Bundle bundle = this.getIntent().getExtras();
        String from = bundle.getString("from");
        if(from==null){

        }else if(from.equals("0")){
            TextView a=(TextView)findViewById(R.id.shopping_txt);
            ImageView b=(ImageView)findViewById(R.id.shopping_icon);
            a.setTextColor(Color.parseColor("#000000"));
            b.setImageResource(R.drawable.icon_home2);

        }else if(from.equals("1")){
            TextView a=(TextView)findViewById(R.id.kind_txt);
            ImageView b=(ImageView)findViewById(R.id.kind_icon);
            a.setTextColor(Color.parseColor("#000000"));
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
                finish();
            }
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
        go_kind.setOnClickListener(new View.OnClickListener() {
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
        prod_no = bundle.getString("prod_no");
        get_product_detail_thread(prod_no);
        get_product_img_thread(prod_no);

        add_cart = (Button) findViewById(R.id.add_cart);
        //btn_youtube = (Button) findViewById(R.id.btn_youtube);
        //btn_return_product_list = (Button) findViewById(R.id.btn_return_product_list);
        btn_go_pay = (Button) findViewById(R.id.btn_go_pay);
        txv_product_name = (TextView) findViewById(R.id.txv_product_name);
        txv_product_id = (TextView) findViewById(R.id.txv_product_id);
        txv_product_money = (TextView) findViewById(R.id.txv_product_money);
        txv_product_pv = (TextView) findViewById(R.id.txv_product_pv);
        txv_product_mv = (TextView) findViewById(R.id.txv_product_mv);
        txv_youtube_url = (TextView) findViewById(R.id.txv_youtube_url);
        txv_product_summary= (TextView) findViewById(R.id.prod_summary);
        txv_product_standard=(TextView) findViewById(R.id.standard);
        add_cart.setOnClickListener(add_cart_listener);
        //btn_youtube.setOnClickListener(btn_youtube_listener);
        //btn_return_product_list.setOnClickListener(btn_return_product_list_listener);
        btn_go_pay.setOnClickListener(btn_go_pay_listener);
        String[] detail_title = {"商品詳細說明","付款及交貨方式","購物須知"};
        list = new ArrayList<HashMap<String,String>>();
        for (int i = 0; i < 3; i++) {
            item = new HashMap<String, String>();
            item.put("detail_title",detail_title[i]);
            list.add(item);
            Log.d("detail_title", detail_title[i]);
        }
        adapter = new MySimpleAdapter(this, list, R.layout.product_detail_list2,new String[]{ "detail_title"},new int[]{R.id.product_detail_list2});
        setListAdapter(adapter);
        Log.d("detail_title1", String.valueOf(list));

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Log.d("sadadsa", "sasddsa");
                Intent intent = new Intent();
                intent.setClass(product_detail_list.this,fra3_main.class);
                startActivity(intent);
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

    private Button.OnClickListener add_cart_listener = new Button.OnClickListener() {
        public void onClick(View v) {
            String prod_no_arr=readprod();
            create_thread("get_prod_kind","get_prod_kind","add_cart_listener",prod_no_arr);
        }
    };
    private Button.OnClickListener btn_go_pay_listener = new Button.OnClickListener() {
        public void onClick(View v) {
            String prod_no_arr=readprod();
            create_thread("get_prod_kind", "get_prod_kind", "btn_go_pay_listener",prod_no_arr);
        }
    };
    public void create_thread(final String cmd,String thread_name, final String from, final String prod_no_arr) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery(cmd,prod_no_arr);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        if (cmd == "get_prod_kind") {
                            newProduct(jsonString,from);
                        }
                    }
                });
            }
        });
    }
    private String executeQuery(String query,String prod_no_arr)
    {
        String result = "";
        try
        {

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("prod",prod_no_arr));
            nameValuePairs.add(new BasicNameValuePair("prod_now",prod_no));
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
    public final void newProduct(String input,String from)
    {
        Global_cart globalVariable = (Global_cart) context.getApplicationContext();
        Enumeration e = globalVariable.cart.keys();
        if(input.equals("1\n")){
            Toast.makeText(getApplicationContext(), "產品紅利商品不可與其他類別一起購買", Toast.LENGTH_SHORT).show();
        }else{
            if(from.equals("add_cart_listener")){

                // Global_cart globalVariable = (Global_cart) context.getApplicationContext();
               /* Set<String> keySet =globalVariable.cart.keySet();
                Iterator<String> it = keySet.iterator();*/
                /*while(it.hasNext())
                {
                    String num = it.next();
                    int value = globalVariable.cart.get(num);
                    //System.out.println(num+":"+value);
                    Toast.makeText(getApplicationContext(),num+":"+value, Toast.LENGTH_SHORT).show();
                }*/
                //Enumeration e = globalVariable.cart.keys();
                if (globalVariable.cart.containsKey(prod_no)) {
                    //String s = e.nextElement().toString();
                    // String price=txv_product_money.getText().toString();
                    // price=price.replace("$","");
                    // globalVariable.cart.put(prod_no,"1"+","+price);

                } else {
                    String price=txv_product_money.getText().toString();
                    price=price.replace("NT$","");
                    String pv=txv_product_pv.getText().toString();
                    String mv=txv_product_mv.getText().toString();
                    pv=pv.replace(" SV", "");
                    pv=pv.replace(" PV","");
                    mv=mv.replace(" MV","");

                    globalVariable.cart.put(prod_no,"1"+","+price+","+pv+","+mv);
                }

                    /*while(e. hasMoreElements()){
                    String s= e.nextElement().toString();
                    String s2 = globalVariable.cart.get(s).toString();
                    Toast.makeText(getApplicationContext(),s+":"+s2, Toast.LENGTH_SHORT).show();
                    }*/
                Toast.makeText(getApplicationContext(), "已加入購物車", Toast.LENGTH_SHORT).show();

            }else if(from.equals("btn_go_pay_listener")){

                //Global_cart globalVariable = (Global_cart) context.getApplicationContext();
                //Enumeration e = globalVariable.cart.keys();
                if (!globalVariable.cart.containsKey(prod_no)) {
                    String price=txv_product_money.getText().toString();
                    price=price.replace("NT$","");
                    String pv=txv_product_pv.getText().toString();
                    String mv=txv_product_mv.getText().toString();
                    pv=pv.replace(" SV", "");
                    pv=pv.replace(" PV","");
                    mv=mv.replace(" MV","");

                    globalVariable.cart.put(prod_no,"1"+","+price+","+pv+","+mv);
                }
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("from", "detail");
                intent.putExtras(bundle);
                intent.setClass(context, pay_list.class);
                startActivity(intent);
                /*Global_cart globalVariable = (Global_cart)context.getApplicationContext();
                Enumeration e =  globalVariable.cart.keys();
                while(e. hasMoreElements()){
                    String s= e.nextElement().toString();
                    String s2 = globalVariable.cart.get(s).toString();
                    Toast.makeText(getApplicationContext(),s+":"+s2, Toast.LENGTH_SHORT).show();
                }*/
            }else{
                Toast.makeText(getApplicationContext(), "加入購物車異常", Toast.LENGTH_SHORT).show();
            }

        }
    }
    private String readprod() {
        Global_cart globalVariable = (Global_cart)this.getApplicationContext();
        Enumeration e =  globalVariable.cart.keys();

        int run_num=0;
        String prod = "";
        while(e. hasMoreElements()){
            if(run_num>0){
                prod+=",";
            }
            String s= e.nextElement().toString();
            prod+=s;
            run_num++;
            //Toast.makeText(getApplicationContext(), s + ":" + s2, Toast.LENGTH_SHORT).show();
        }
        return prod;
    }
    public void get_product_detail_thread(final String prod_no) {
        mThread = new HandlerThread("get_prod_no_detail");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = send_product_detail_data(prod_no);

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        renew_data(jsonString);

                    }
                });
            }
        });
    }

    private String send_product_detail_data(String prod_no) {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            //HttpPost post = new HttpPost("http://220.135.161.128/leo/android_sql.php");
            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", "get_prod_no_detail"));
            nameValuePairs.add(new BasicNameValuePair("prod_no", prod_no));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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

    public final void renew_data(String input) {
        Log.d("log_test_1212",input);
        try {
            JSONArray jsonArray = new JSONArray(input);
            JSONObject jsonData = jsonArray.getJSONObject(0);

            String prod_no = jsonData.getString("prod_no");
            String prod_name = jsonData.getString("prod_name");
            String comp_price = jsonData.getString("comp_price");
            String pv = jsonData.getString("pv");
            String mv = jsonData.getString("mv");
            String standard = jsonData.getString("standard");
            String youtube_url = jsonData.getString("youtube_url");
            String kind_name = jsonData.getString("kind_name");
            String pro_summary = jsonData.getString("prod_summary");
            getActionBar().setTitle(kind_name);
            if(standard.equals("")){
                standard="無";
            }
            txv_product_standard.setText(" "+standard);
            txv_product_summary.setText(pro_summary);
            txv_product_name.setText(prod_name);
            txv_product_id.setText(prod_no);
            txv_product_money.setText("NT$" + comp_price);
            txv_product_pv.setText(pv);
            txv_product_mv.setText(mv);
            txv_youtube_url.setText(youtube_url);
            ((ScrollView)findViewById(R.id.scroll1)).scrollTo(0,0) ;
        } catch (JSONException e) {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

        }

    }

    public void get_product_img_thread(final String prod_no) {
        mThread = new HandlerThread("get_prod_no_img");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = send_product_img_data(prod_no);

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        renew_img_data(jsonString);

                    }
                });
            }
        });
    }

    private String send_product_img_data(String prod_no) {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            //HttpPost post = new HttpPost("http://220.135.161.128/leo/android_sql.php");
            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", "get_prod_no_img"));
            nameValuePairs.add(new BasicNameValuePair("prod_no", prod_no));
            Log.d("testprod_no",prod_no);
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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

    public final void renew_img_data(String input) {
        Log.d("img_input",input);
        try {
            loadingIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.comingsoon);
            JSONArray jsonArray = new JSONArray(input);
            pager = (ViewPager) this.findViewById(R.id.viewPager);
            final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            final FrameLayout.LayoutParams laParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
            pic_num=jsonArray.length();
            final LinearLayout []li_array=new LinearLayout[jsonArray.length()];
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
             /*       Paths = "http://" + ip + "/aifya/ct/shoppingcart/prod_images/800800/" + jsonData.getString("name");

                    final Bitmap mBitmap = getBitmapFromURL(Paths);*/
                    Paths = "http://" + ip + "/ct/shoppingcart/prod_images/800800/" + jsonData.getString("name");
                    pic_num=jsonArray.length();



                    final String Paths2=Paths;
                    final int finalI = i;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Bitmap mBitmap = getBitmapFromURL(Paths2);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mBitmap!=null){
                                        LinearLayout li = new LinearLayout(getApplicationContext());
                                        li.setLayoutParams(layoutParams);

                                        ImageView imgv = new ImageView(getApplicationContext());
                                        imgv.setLayoutParams(laParams);
                                        //imgv.setBackgroundColor(0xFFff9966);
                                        imgv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                        imgv.setImageBitmap(mBitmap);
                                        li.addView(imgv);
                                        li_array[finalI]=li;
                                        complete_pic++;

                                    }else{
                                        LinearLayout li = new LinearLayout(getApplicationContext());
                                        li.setLayoutParams(layoutParams);

                                        ImageView imgv = new ImageView(getApplicationContext());
                                        imgv.setLayoutParams(laParams);
                                        imgv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                        imgv.setImageBitmap(loadingIcon);
                                        li.addView(imgv);
                                        li_array[finalI] = li;
                                        complete_pic++;
                                    }
                                    if(complete_pic==pic_num){
                                        for(int i=0;i<pic_num;i++){
                                            viewContainter.add(li_array[i]);
                                        }
                                        pager.setAdapter(pagerAdapter);
                                        loadding_flag=true;
                                        chk_loadding();
                                    }
                                }
                            });
                        }
                    }).start();
                }

            }else{

                LinearLayout li = new LinearLayout(getApplicationContext());
                li.setLayoutParams(layoutParams);

                ImageView imgv = new ImageView(getApplicationContext());
                imgv.setLayoutParams(laParams);
                imgv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imgv.setImageBitmap(loadingIcon);
                li.addView(imgv);
                viewContainter.add(li);
                pager.setAdapter(pagerAdapter);
            }

        } catch (JSONException e) {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

        }

    }

    public static Bitmap getBitmapFromURL(String imageUrl){

        try{

            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        }catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

    }
    PagerAdapter pagerAdapter = new PagerAdapter() {

        //viewpager中的?件?量
        @Override
        public int getCount() {
            Log.d("getcount", Integer.toString(viewContainter.size()));
            return viewContainter.size();

        }

        //滑?切?的?候???前的?件
        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            ((ViewPager) container).removeView(viewContainter.get(position));
        }

        //每次滑?的?候生成的?件
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(viewContainter.get(position));
            return viewContainter.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            if (viewContainter.size()>0)
            {
                return POSITION_NONE;
            }
            else
            {
                // 資料還沒準備好，此頁面維持不變
                return POSITION_UNCHANGED;
            }
            //return super.getItemPosition(object);
        }
    };
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        if(position==0){
            //Toast.makeText(context,"商品詳細說明", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("prod_no",prod_no);
            intent.putExtras(bundle);
            intent.setClass(context, product_standard.class);
            startActivity(intent);
        }else if(position==1){
            //Toast.makeText(context, "付款及交貨方式", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(context, product_payway.class);
            startActivity(intent);
        }else if(position==2){
            //Toast.makeText(context,"購物須知", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(context, product_shopping_noti.class);
            startActivity(intent);
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
    private void chk_loadding() {
        FrameLayout Fralayout = (FrameLayout) findViewById(R.id.detail_loadding);
        // LinearLayout LinearLayout = (android.widget.LinearLayout)getView().findViewById(R.id.Top_bar);
        // FrameLayout loadding=(FrameLayout)getView().findViewById(R.id.FF1) ;
        if (loadding_flag == true){//讀好了
            //  LinearLayout.setVisibility(View.VISIBLE);
            Fralayout.setVisibility(View.GONE);
            //   loadding.setVisibility(View.GONE);
        }else{
            Fralayout.setVisibility(View.VISIBLE);
            //  loadding.setVisibility(View.VISIBLE);
            // LinearLayout.setVisibility(View.GONE);
        }
    }
    void back_menu(){
        Intent i = new Intent(this,test_page.class);
        Bundle bundle = new Bundle();
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        bundle.putString("from", "prod_detail");
        i.putExtras(bundle);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent();
            intent.setClass(product_detail_list.this,fra3_main.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}