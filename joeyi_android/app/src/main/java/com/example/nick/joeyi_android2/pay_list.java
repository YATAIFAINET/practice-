package com.example.nick.joeyi_android2;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class pay_list extends ListActivity {

    private String[] WEEK_NO;
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    public Spinner spinner_items;
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> item;
    ArrayAdapter<String> numList;
    String config="",login_no;
    int total_money;
    int cart_num=0;
    private Boolean loadding_flag=false;

    private DefaultHttpClient httpClient;
    private HttpPost httpPost;
    private HttpEntity httpEntity;
    private HttpResponse httpResponse;
    public String from,temp_mb_no="",temp_mb_name="",temp_mb_tel="";
    public static String PHPSESSID = null;
    String ip="",folder="";
    String []prod;
    Button go_shop;
    private int send_money=0;
    LinearLayout go_shopping,go_kind,go_account,go_index,go_cart,layout_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_list);
        load_config();
        Bundle bundle =this.getIntent().getExtras();
        from = bundle.getString("from");
        if (bundle != null && bundle.containsKey("temp_mb_no"))
        {
            temp_mb_no= bundle.getString("temp_mb_no");
        }
        if (bundle != null && bundle.containsKey("temp_mb_name"))
        {
            temp_mb_name= bundle.getString("temp_mb_name");
        }
        if (bundle != null && bundle.containsKey("temp_mb_tel"))
        {
            temp_mb_tel= bundle.getString("temp_mb_tel");
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayShowHomeEnabled(false);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        layout_bottom = (LinearLayout) findViewById(R.id.layout_bottom);
        if(!from.equals("join")) {
            layout_bottom.setVisibility(View.VISIBLE);
            go_shopping = (LinearLayout) findViewById(R.id.go_shopping);
            go_kind = (LinearLayout) findViewById(R.id.go_kind);
            go_account = (LinearLayout) findViewById(R.id.go_account);
            go_index = (LinearLayout) findViewById(R.id.go_index);
            go_cart = (LinearLayout) findViewById(R.id.go_cart);

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

        }else{
            layout_bottom.setVisibility(View.GONE);
        }
        go_shop=(Button)findViewById(R.id.go_shop_1);
        go_shop.setOnClickListener(go_shop_1);
        Global_cart globalVariable = (Global_cart)this.getApplicationContext();
        Enumeration e =  globalVariable.cart.keys();
        int run_num=0;
        String prod = "";
        while(e. hasMoreElements()){
            if(run_num>0){
                prod+=",";
            }
            String s= e.nextElement().toString();
            String s2 = globalVariable.cart.get(s).toString();
            prod+=s;
            run_num++;
            //Toast.makeText(getApplicationContext(), s + ":" + s2, Toast.LENGTH_SHORT).show();
        }

        create_thread("get_comp", "get_comp",prod);

        //create_thread("get_product", "get_product");
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
    private Button.OnClickListener go_shop_1 = new Button.OnClickListener() {
        public void onClick(View v) {
            if(cart_num>0){
                Intent Intentintent = new Intent();
                Bundle b=new Bundle();
                TextView t=(TextView)findViewById(R.id.order_total);
                TextView fee=(TextView)findViewById(R.id.fee);
                String total=t.getText().toString();
                String send_money=fee.getText().toString();
                total=total.replace("NT$","");
                send_money=send_money.replace("NT$","");
                b.putString("total",total);
                b.putString("send_money",send_money);
                b.putString("temp_mb_no",temp_mb_no);
                b.putString("temp_mb_name",temp_mb_name);
                b.putString("temp_mb_tel",temp_mb_tel);
                b.putString("from",from);
                Intentintent.putExtras(b);
                Intentintent.setClass(getApplicationContext(), shopping_step2.class);
                startActivity(Intentintent);
            }else{
                Toast.makeText(pay_list.this,"無選擇商品無法結帳", Toast.LENGTH_LONG).show();
            }


        }
    };
    public void create_thread(final String cmd,String thread_name, final String prod_no) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery(cmd,prod_no);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        if (cmd == "get_comp") {
                            newProduct(jsonString);
                        }
                    }
                });
            }
        });
    }
    private String executeQuery(String query,String prod_no)
    {
        String result = "";
        try
        {

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("prod_no",prod_no));
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
            //Log.d("newProduct",input);
            JSONArray jsonArray = new JSONArray(input);
            list = new ArrayList<HashMap<String,String>>();
            cart_num=jsonArray.length();
            prod=new String[jsonArray.length()];
            LinearLayout no_pro=(LinearLayout)findViewById(R.id.no_list);
            LinearLayout total_lay=(LinearLayout)findViewById(R.id.total_lay);


            if(jsonArray.length()>0) {
                total_money=0;
                no_pro.setVisibility(View.GONE);
                total_lay.setVisibility(View.VISIBLE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    item = new HashMap<String, String>();
                    item.put("prod_no", "【" + jsonData.getString("prod_no")+"】");
                    item.put("prod_name", "" + jsonData.getString("prod_name"));
                    item.put( "comp_price",""+jsonData.getString("comp_price"));
                    item.put( "img_name",""+jsonData.getString("name"));
                    item.put( "pv",""+jsonData.getString("pv"));
                    item.put( "mv",""+jsonData.getString("mv"));
                    Global_cart globalVariable = (Global_cart)this.getApplicationContext();
                    String prod_key=jsonData.getString("prod_no");
                    String aaa2=globalVariable.cart.get(prod_key).toString();
                    String []tmp=aaa2.split(",");
                    int aaa= Integer.parseInt(tmp[0]);//num
                    prod[i]=jsonData.getString("prod_no");
                    String prod_num = String.valueOf(aaa);
                    item.put( "prod_num",""+prod_num);
                    item.put("comp_total", "" + jsonData.getInt("comp_price") * Integer.valueOf(prod_num).intValue());
                    total_money+=jsonData.getInt("comp_price") * Integer.valueOf(prod_num).intValue();
                    list.add(item);

                }
                TextView order_money=(TextView)findViewById(R.id.order_money);
                TextView total=(TextView)findViewById(R.id.order_total);
                Log.d("total_money", String.valueOf(total_money));
                order_money.setText("NT$" + String.valueOf(total_money));
                total.setText("NT$"+String.valueOf(total_money+send_money));
                get_send_thread();
                adapter = new MySimpleAdapter(this, list, R.layout.pay_list2,new String[]{ "prod_no", "prod_name", "comp_price", "comp_total","prod_num","img_name","pv","mv"},new int[]{R.id.prod_id, R.id.prod_name, R.id.prod_comp, R.id.prod_total, R.id.prod_num,R.id.img_name,R.id.pv,R.id.mv});
                setListAdapter(adapter);

            }else{
                no_pro.setVisibility(View.VISIBLE);
                total_lay.setVisibility(View.INVISIBLE);

                TextView no_pro2=(TextView)findViewById(R.id.no_pro_tv);
                no_pro2.setText("目前購物車無商品");

                /*item = new HashMap<String, String>();
                item.put("detail_kind", "目前尚無資料");
                list.add(item);
                adapter = new SimpleAdapter(this, list, R.layout.money_list,new String[]{ "detail_kind"},new int[]{R.id.prod_id});
                setListAdapter(adapter);*/

            }
            loadding_flag=true;
            chk_loadding();


        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

        }
    }
    public void get_send_thread() {
        mThread = new HandlerThread("get_send_thread");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_send_data("get_send");

                mUI_Handler.post(new Runnable() {
                    public void run() {

                        get_send_data_res(jsonString);


                    }
                });
            }
        });
    }
    private String get_send_data(String cmd)
    {
        String result = "";
        try
        {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",cmd));
            String money;
            money=((TextView) findViewById(R.id.order_money)).getText().toString();
            money=money.replace("NT$","");
            nameValuePairs.add(new BasicNameValuePair("money",money));

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
    public final void get_send_data_res(String input)
    {
        try
        {
            Log.d("get_mb_data_res", input);
            JSONArray jsonArray = new JSONArray(input);
            TextView total=(TextView)findViewById(R.id.order_total);
            TextView send=(TextView)findViewById(R.id.fee);
            Global_cart globalVariable = (Global_cart) getApplicationContext().getApplicationContext();

            int total_m=0;
            for(int i=0;i<cart_num;i++){


                String aaa2=globalVariable.cart.get(prod[i]).toString();
                Log.d("aaa22",aaa2);
                String []tmp=aaa2.split(",");//數量 單價
                int num= Integer.parseInt(tmp[0]);//num
                if(tmp[1]==""){
                    tmp[1]="0";
                }

                int money= Integer.parseInt(tmp[1]);//money
                total_m+=money*num;

            }
            if(jsonArray.length()<=0){
                send.setText("NT$"+"0");
                String  send2= "0";
                send2=send2.replace("NT$","");

                total.setText("NT$"+ (total_m + Integer.parseInt(send2)));
            }
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                send.setText("NT$"+jsonData.getString("send_money"));
                String  send2= send.getText().toString();
                send2=send2.replace("NT$","");

                total.setText("NT$"+ (total_m + Integer.parseInt(send2)));


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
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        //add top-left icon click event deal
        switch(item.getItemId()){
            case android.R.id.home:
                Log.d("fromfrom",from);
               // if(from.equals("fragment")){
                    Log.d("fromfrom", "1");
                    Intent intent = new Intent();
                    intent.setClass(this, fra3_main.class);
                    startActivity(intent);
                    finish();
                //}
                /*else if(from.equals("join")){
                    Log.d("fromfrom","2");
                    Toast.makeText(pay_list.this, "會員尚未註冊完成，請重新填寫資料", Toast.LENGTH_SHORT).show();
                    Global_cart globalVariable = (Global_cart) this.getApplicationContext();
                    globalVariable.cart.clear();

                    Intent intent = new Intent();
                    intent.setClass(this, fra3_main.class);
                    startActivity(intent);
                    finish();
                    return true;

                }*/
                //else{
                //    Log.d("fromfrom","3");
                //    finish();
                //}
                //     finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵

                Intent intent = new Intent();
                intent.setClass(this, fra3_main.class);
                startActivity(intent);
                finish();

        }
        return true;
    }
    private class MySimpleAdapter extends SimpleAdapter{
        private LayoutInflater mInflater;
        private String Paths;
        private Bitmap loadingIcon;
        private AsyncImageFileLoader asyncImageFileLoader;
        View mGridView = null;
        View view2 = null;
        View []cv=new View[cart_num];
        ViewGroup []par=new ViewGroup[cart_num];
        int []fg= new int[cart_num];

        public MySimpleAdapter(Context context,
                               List<? extends Map<String, ?>> data, int resource,
                               String[] from, int[] to) {
            super(context, data, resource, from, to);
            // TODO Auto-generated constructor stub
            mInflater = LayoutInflater.from(context);
            loadingIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.coming_soon_b);
            mGridView = mInflater.inflate(R.layout.pay_list, null);
            // view2=mInflater.inflate(R.layout.pay_list2, null);
            asyncImageFileLoader = new AsyncImageFileLoader();
        }


        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder mHolder;
            final View v= super.getView(position, convertView, parent);
            View view = convertView;
            TextView tv=(TextView)v.findViewById(R.id.textView53);

            Log.d("1215", String.valueOf(tv.getText()));
            Log.d("position", String.valueOf(position));
            TextView img_name=(TextView)v.findViewById(R.id.img_name);
            TextView txv_num=(TextView)v.findViewById(R.id.prod_num);

            TextView txv_total=(TextView)v.findViewById(R.id.prod_total);
            TextView prod_no=(TextView)v.findViewById(R.id.prod_id);
            String [] num1 = new String[99];
            for (int i =0;i<num1.length;i++){
                num1[i] = String.valueOf(i+1);
            }
            final Spinner Spinner=(Spinner)v.findViewById(R.id.prod_num_spinner);
            numList=new ArrayAdapter<String>(pay_list.this,R.layout.myspinner2, num1);
            numList.setDropDownViewResource(R.layout.myspinner2);
            Spinner.setAdapter(numList);
            Spinner.setSelection(Integer.valueOf(txv_num.getText().toString()).intValue() - 1);
            Spinner.setTag(position);

            txv_total.setTag("tatal_" + position);


            ImageView del=(ImageView)v.findViewById(R.id.delete);

            del.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {


                    del(position);







                    // adapter.notifyDataSetChanged();

                    //onCreate(null);

                }
            });


            Spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                    if (fg[Integer.parseInt(Spinner.getTag().toString())] == 0) {

                        fg[Integer.parseInt(Spinner.getTag().toString())]++;
                        return;
                    }


                    //Toast.makeText(pay_list.this, "您選擇" + adapterView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                    TextView prod_comp = (TextView) v.findViewById(R.id.prod_comp);
                    TextView prod_total = (TextView) v.findViewById(R.id.prod_total);
                    TextView prod_no = (TextView) v.findViewById(R.id.prod_id);
                    TextView prod_pv = (TextView) v.findViewById(R.id.pv);
                    TextView prod_mv = (TextView) v.findViewById(R.id.mv);

                    int total = Integer.valueOf(prod_comp.getText().toString()).intValue() * Integer.valueOf(adapterView.getSelectedItem().toString()).intValue();
                    prod_total.setText(String.valueOf(total));
                    Global_cart globalVariable = (Global_cart) getApplicationContext().getApplicationContext();
                    String pp = prod_no.getText().toString();
                    pp = pp.replace("【", "");
                    pp = pp.replace("】", "");
                    globalVariable.cart.remove(pp);
                    globalVariable.cart.put(pp, adapterView.getSelectedItem().toString() + "," + prod_comp.getText().toString()+","+prod_pv.getText().toString()+","+prod_mv.getText().toString());

                    int total_m = 0;
                    for (int i = 0; i < cart_num; i++) {
                        Log.d("aaaa", String.valueOf(i));
                        String aaa2 = globalVariable.cart.get(prod[i]).toString();
                        String[] tmp = aaa2.split(",");//數量 單價
                        int num = Integer.parseInt(tmp[0]);//num
                        int money = Integer.parseInt(tmp[1]);//money
                        total_m += money * num;

                    }
                    ((TextView) findViewById(R.id.order_money)).setText("NT$" + String.valueOf(total_m));
                    ((TextView) findViewById(R.id.order_total)).setText("NT$" + String.valueOf(total_m + send_money));
                    get_send_thread();
                    //   TextView tv=(TextView)getView(position, cv, par).findViewById(R.id.prod_total);
                    //  int m=total_money-Integer.parseInt((tv.getText().toString()))+total;


                }

                public void onNothingSelected(AdapterView arg0) {
                    Toast.makeText(pay_list.this, "請選擇數量", Toast.LENGTH_LONG).show();
                }
            });


            Paths = "http://"+ip+"/ct/shoppingcart/prod_images/180180/"+img_name.getText().toString();
            Log.d("now_path",Paths);
            if(convertView == null)
            {
                convertView = v;
                mHolder = new ViewHolder();
                mHolder.icon = (ImageView) convertView.findViewById(R.id.imageView16);
                convertView.setTag(mHolder);
            }
            else
            {
                mHolder = (ViewHolder) convertView.getTag();
            }

            //設定此mHolder.icon的tag為檔名，讓之後的callback function可以針對此mHolder.icon替換圖片
            ImageView imageView = mHolder.icon;
            Log.d("leo_test_1210", Paths);
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
    private void chk_loadding() {
        FrameLayout Fralayout = (FrameLayout) findViewById(R.id.paylist_loadding);
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
    void del(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("是否刪除此商品?")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 左方按鈕方法

                        String join_prod_no = list.get(position).get("prod_no").toString().replace("【", "");
                        join_prod_no = join_prod_no.replace("】", "");
                        if (join_prod_no.equals(placard_detail_mes.join_prod_no_static)) {
                            Toast.makeText(pay_list.this,"入會商品不可刪除",Toast.LENGTH_LONG).show();
                            return;
                     } else {
                            Global_cart globalVariable = (Global_cart) getApplicationContext().getApplicationContext();
                            globalVariable.cart.remove(prod[position]);
                            Log.d("delete", String.valueOf(position));
                            cart_num--;
                            for (int i = position; i < cart_num; i++) {
                                prod[i] = prod[i + 1];     //0  1

                            }
                            list.remove(position);
                            adapter = new MySimpleAdapter(getApplicationContext(), list, R.layout.pay_list2, new String[]{"prod_no", "prod_name", "comp_price", "comp_total", "prod_num", "img_name"}, new int[]{R.id.prod_id, R.id.prod_name, R.id.prod_comp, R.id.prod_total, R.id.prod_num, R.id.img_name});
                            setListAdapter(adapter);


                            for (int i = 0; i < cart_num; i++) {
                                // Log.d("1215",prod[i]);

                            }

                            Toast.makeText(pay_list.this, "已刪除", Toast.LENGTH_LONG).show();

                            Global_cart globalVariable2 = (Global_cart) getApplicationContext().getApplicationContext();
                            Enumeration e = globalVariable2.cart.keys();
                            int run_num = 0;
                            String prod = "";
                            while (e.hasMoreElements()) {
                                if (run_num > 0) {
                                    prod += ",";
                                }
                                String s = e.nextElement().toString();
                                String s2 = globalVariable2.cart.get(s).toString();
                                prod += s;
                                run_num++;
                                //Toast.makeText(getApplicationContext(), s + ":" + s2, Toast.LENGTH_SHORT).show();
                            }

                            create_thread("get_comp", "get_comp", prod);
                        }
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 右方按鈕方法

                    }
                });
        AlertDialog about_dialog = builder.create();
        about_dialog.show();




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