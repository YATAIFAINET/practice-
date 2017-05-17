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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;


public class qr_prod_step2 extends Activity {
    String ip="",folder="",order_no_success="";
    TextView pay_money_tv,prod_name_tv;

    private Context context;
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private android.os.Handler mThreadHandler;
    private TextView e_cash_point,e_pv_point;
    String config="";
    String login_id;
    int has_data=0;
    int area_data=-1;
    int city_data=-1;
    String rate;
    int t,run_num;
    RadioGroup send_method;
    RadioButton card,atm,no_pay,send_method_self,send_method_send;
    private Button go_sucess;
    private int pay_way=0;
    private TextView money_us;
    String pay_money,prod,unit_num_str,unit_pv_str,unit_price_str,trade,temp_mb_no="",temp_mb_name="",temp_mb_tel="";
    LinearLayout go_shopping,go_kind,go_account,go_index,go_cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_prod_step2);
        setTitle("選擇付款方式");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayShowHomeEnabled(false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        load_config();
        readprod();

        config = readFromFile("client_config");
        try {
            login_id = new JSONObject(config).getString("login_id");
        } catch (Exception e) {

        }

        context = this;
        Bundle bundle = this.getIntent().getExtras();
        pay_money = bundle.getString("total");
        String prod_name2="";
        if (bundle != null && bundle.containsKey("prod_name")) {
            prod_name2 = bundle.getString("prod_name");
        }

        prod_name_tv=(TextView)findViewById(R.id.prod_name);
        prod_name_tv.setText(prod_name2);
        trade = bundle.getString("send_money");
        temp_mb_no = bundle.getString("temp_mb_no");
        if (bundle != null && bundle.containsKey("temp_mb_name")) {
            temp_mb_name = bundle.getString("temp_mb_name");
        }
        if (bundle != null && bundle.containsKey("temp_mb_tel")) {
            temp_mb_tel = bundle.getString("temp_mb_tel");
        }
        pay_money_tv = (TextView) findViewById(R.id.pay_money);
        pay_money_tv.setText(pay_money);
        go_sucess = (Button) findViewById(R.id.go_sucess);
        money_us = (TextView) findViewById(R.id.total_us);
        go_sucess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goto_sucess();
            }
        });
        e_cash_point = (TextView) findViewById(R.id.e_cash_point);
        e_pv_point = (TextView) findViewById(R.id.e_pv_point);





















        get_mb_data_thread();

    }



    private void readprod() {
        Global_cart globalVariable = (Global_cart)this.getApplicationContext();
        Enumeration e =  globalVariable.cart.keys();

        run_num=0;
        prod = "";
        while(e. hasMoreElements()){
            if(run_num>0){
                prod+=",";
            }
            String s= e.nextElement().toString();
            prod+=s;
            run_num++;
            //Toast.makeText(getApplicationContext(), s + ":" + s2, Toast.LENGTH_SHORT).show();
        }
    }
    private String readFromFile(String FILENAME) {
        try{

            FileInputStream fin = this.openFileInput(FILENAME);
            byte[] buff = new byte[fin.available()];
            fin.read(buff);
            String str = new String(buff);



            return str;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }





    public void get_mb_data_thread() {
        mThread = new HandlerThread("get_country");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_mb_data("get_mb_data");

                mUI_Handler.post(new Runnable() {
                    public void run() {

                        get_mb_data_res(jsonString);


                    }
                });
            }
        });
    }
    private String get_mb_data(String cmd)
    {
        String result = "";

        try
        {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",cmd));
            nameValuePairs.add(new BasicNameValuePair("mb_no",login_id));
            nameValuePairs.add(new BasicNameValuePair("temp_mb_no",temp_mb_no));
            /*if(temp_mb_no.length()>0){
                nameValuePairs.add(new BasicNameValuePair("mbst","web_mbst"));
            }else{

                nameValuePairs.add(new BasicNameValuePair("mbst","mbst"));
            }*/
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
    public final void get_mb_data_res(String input)
    {
        try
        {
            Log.d("get_mb_data_res",input);
            JSONArray jsonArray = new JSONArray(input);


            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                e_cash_point.setText(jsonData.getString("e_cash_point"));
                e_pv_point.setText(jsonData.getString("e_pv_point"));
                rate=jsonData.getString("cash_rate");
                int pay_money2= Integer.parseInt(pay_money);
                Double money_u;
                Double rat= Double.valueOf(rate);
                money_u=Math.rint(pay_money2/rat*100)/100;
                money_us.setText("(美金"+String.valueOf(money_u)+")");




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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_normal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Intent intent=new Intent();
                intent.setClass(qr_prod_step2.this, qr_new.class);
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
    void goto_sucess(){

        CheckBox ecash_use=(CheckBox)findViewById(R.id.eca);
        CheckBox epv_use=(CheckBox)findViewById(R.id.epv);
        EditText ecash_value_ed=(EditText)findViewById(R.id.ecash_value);
        EditText epv_value_ed=(EditText)findViewById(R.id.epv_value);
        double rate2= Double.parseDouble(rate);
        String err="";
        int pay_money= Integer.valueOf(pay_money_tv.getText().toString());
        int pay_point=0;
        Double per= Double.parseDouble(rate);
        if(epv_use.isChecked()){
            pay_point+=Integer.parseInt(epv_value_ed.getText().toString());
        }
        if(ecash_use.isChecked()){
            pay_point+=Integer.parseInt(ecash_value_ed.getText().toString());
        }
        if(!epv_use.isChecked()&&!ecash_use.isChecked()){

            err+=" 付款方式";
        }
        if(err.length()>0) {
            err+=" 未填";
        }



        //pay_way  1  2  3  4 刷卡 匯款 貨到付款 產品紅利

        if(err.length()>0) {
            Toast.makeText(getApplication(),err, Toast.LENGTH_LONG).show();
        }

        if(epv_use.isChecked()&&epv_value_ed.getText().toString().equals("0")){
            Toast.makeText(getApplication(),"選擇購車點數付款,抵扣點數必須大於零", Toast.LENGTH_LONG).show();
            return;

        }

        int epv_int= Integer.parseInt(epv_value_ed.getText().toString());
        double max_epv_int= Double.parseDouble((e_pv_point.getText().toString()));

        if(epv_use.isChecked()&&epv_int>max_epv_int){
            Toast.makeText(getApplication(),"選擇購車點數付款,抵扣點數必須小於可用點數", Toast.LENGTH_LONG).show();
            return;

        }
        int ecash_int= Integer.parseInt(ecash_value_ed.getText().toString());
        double max_ecash_int= Double.parseDouble(e_cash_point.getText().toString());

        if(ecash_use.isChecked()&&ecash_int>max_ecash_int){
            Toast.makeText(getApplication(),"選擇電子錢包付款,抵扣點數必須小於可用點數", Toast.LENGTH_LONG).show();
            return;
        }

        if(ecash_use.isChecked()&&ecash_value_ed.getText().toString().equals("0")){
            Toast.makeText(getApplication(),"選擇電子錢包付款,抵扣點數必須大於零", Toast.LENGTH_LONG).show();
            return;
        }
        int total_point=0;
        if(ecash_use.isChecked()){
            total_point+=ecash_int;
        }
        if(epv_use.isChecked()){
            total_point+=epv_int;
        }
        if(total_point*rate2>pay_money){
            Toast.makeText(getApplication(),"付款點數大於總金額", Toast.LENGTH_LONG).show();
            return;
        }
        if(total_point*rate2<pay_money){
            Toast.makeText(getApplication(),"付款點數不足總金額", Toast.LENGTH_LONG).show();
            return;
        }

        if(err.length()<=0) {

            mThread = new HandlerThread("go_sucess");
            mThread.start();
            mThreadHandler = new Handler(mThread.getLooper());
            mThreadHandler.post(new Runnable() {
                public void run() {
                    final String jsonString = go_order("go_sucess");

                    mUI_Handler.post(new Runnable() {
                        public void run() {

                            go_order_res(jsonString);
                            go_sucess.setEnabled(false);
                            Toast.makeText(getApplication(), "訂單送出中,請稍候", Toast.LENGTH_LONG).show();


                        }
                    });
                }
            });


        }
    }
    private String go_order(String cmd)
    {
        String result = "";

        CheckBox epv_use=(CheckBox)findViewById(R.id.epv);
        CheckBox ecash_use=(CheckBox)findViewById(R.id.eca);
        EditText epv_value_ed=(EditText)findViewById(R.id.epv_value);
        EditText ecash_value_ed=(EditText)findViewById(R.id.ecash_value);




        Global_cart globalVariable = (Global_cart)this.getApplicationContext();
        Enumeration e =  globalVariable.cart.keys();

        String []pro;
        String pro_num;
        pro_num= String.valueOf(run_num);
        pro=new String[run_num];
        String []pro_unit_num=new String[run_num];
        String []pro_unit_price=new String[run_num];
        String []pro_unit_pv=new String[run_num];

        String []tmp=new String[run_num];
        for(int i=0;i<run_num;i++){
            tmp=prod.split(",");
            pro[i]=tmp[i];//產品

        }
        int total_pv=0,total_money=0;
        for(int i=0;i<run_num;i++){


            String aaa2=globalVariable.cart.get(pro[i]).toString();
            String []tmp2=aaa2.split(",");//數量 單價
            pro_unit_num[i]= tmp2[0];//num
            if(tmp2[1]==""){
                tmp2[1]="0";
            }
            pro_unit_pv[i]=tmp2[2];//pv
            pro_unit_price[i]=tmp2[1];//money
            total_pv+= Integer.parseInt(pro_unit_pv[i])*Integer.parseInt(pro_unit_num[i]);
            total_money+= Integer.parseInt(pro_unit_price[i])*Integer.parseInt(pro_unit_num[i]);

        }
        unit_num_str="";
        unit_price_str="";
        unit_pv_str="";
        for(int i=0;i<run_num;i++){
            if(i>0){
                unit_num_str+=",";
                unit_price_str+=",";
                unit_pv_str+=",";
            }
            unit_num_str+=pro_unit_num[i];
            unit_price_str+=pro_unit_price[i];
            unit_pv_str+=pro_unit_pv[i];

        }

        int trade_money=0;




        try
        {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd","send_order_qr"));

            nameValuePairs.add(new BasicNameValuePair("mb_no",login_id));

            nameValuePairs.add(new BasicNameValuePair("sub_pv",String.valueOf(total_pv)));
            nameValuePairs.add(new BasicNameValuePair("order_total_money",String.valueOf(total_money)));
            if(epv_use.isChecked()){
                nameValuePairs.add(new BasicNameValuePair("use_e_pv",epv_value_ed.getText().toString()));
            }
            if(ecash_use.isChecked()){
                nameValuePairs.add(new BasicNameValuePair("use_e_cash",ecash_value_ed.getText().toString()));
            }


            nameValuePairs.add(new BasicNameValuePair("paper_no",""));
            nameValuePairs.add(new BasicNameValuePair("paper_title",""));




            nameValuePairs.add(new BasicNameValuePair("pay_way",String.valueOf(pay_way)));

            nameValuePairs.add(new BasicNameValuePair("cart_num", pro_num));
            nameValuePairs.add(new BasicNameValuePair("prod", prod));
            nameValuePairs.add(new BasicNameValuePair("unit_num_str", unit_num_str));
            nameValuePairs.add(new BasicNameValuePair("unit_price_str", unit_price_str));
            nameValuePairs.add(new BasicNameValuePair("unit_pv_str",unit_pv_str));


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
        catch (Exception ee)
        {
            Log.e("log_tag", ee.toString());
        }
        return result;
    }
    public final void go_order_res(String input)
    {
        Log.d("leo2016052701",input);
        Global_cart globalVariable = (Global_cart) context.getApplicationContext();
        globalVariable.cart.clear();


        //new一個intent物件，並指定Activity切換的class
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), shopping_res.class);

        //new一個Bundle物件，並將要傳遞的資料傳入
        Bundle bundle = new Bundle();
        bundle.putString("res", input);

        //將Bundle物件assign給intent
        intent.putExtras(bundle);
        finish();
        String[] res = input.split(",");

        if (res[0].equals("success")) {
            //切換Activity
            startActivity(intent);
        } else {
            Toast.makeText(getApplication(), input, Toast.LENGTH_SHORT).show();
        }


    }


}