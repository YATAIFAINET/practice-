package com.example.nick.joeyi_android2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

public class my_account extends ListFragment {
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> item;
    String config="",login_id;
    Boolean loadding_flag=false;
    String ip="",folder="";
    private Button mButton_Give_Bonus, mButton_Bonus_to_Money;
    private TextView mTextView_Left_Count, mTextView_Right_Count;
    private LinearLayout mLinearLayout_Update_Info, mLinearLayout_Fix_Pwd, mLinearLayout_Search_Order, mLinearLayout_Bonus;

    String can_use_bonus = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        load_config();
        config  = readFromFile("client_config");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("prod_list_old", "onCreateView");
//        ColorDrawable colorDrawable = new ColorDrawable();
//        colorDrawable.setColor(Color.parseColor("#fce0cd"));
//        getActivity().getActionBar().setBackgroundDrawable(colorDrawable);
        View view = inflater.inflate(R.layout.my_account,
                container, false);
        mLinearLayout_Update_Info = (LinearLayout) view.findViewById(R.id.LinearLayout_Update_Info);
        mLinearLayout_Fix_Pwd = (LinearLayout) view.findViewById(R.id.LinearLayout_Fix_Pwd);
        mLinearLayout_Search_Order = (LinearLayout) view.findViewById(R.id.LinearLayout__Search_Order);
        mLinearLayout_Bonus = (LinearLayout) view.findViewById(R.id.LinearLayout_Bonus);

        mTextView_Left_Count = (TextView) view.findViewById(R.id.textView_left_count);
        mTextView_Right_Count = (TextView) view.findViewById(R.id.textView_right_count);
        mButton_Give_Bonus = (Button) view.findViewById(R.id.give_bonus);
        mButton_Bonus_to_Money = (Button) view.findViewById(R.id.bonus_to_money);
        mButton_Give_Bonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(my_account_e_cash.this,"!"+can_use_bonus.getText().toString(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("can_use_bonus", can_use_bonus);
                intent.putExtras(bundle);
                intent.setClass(getActivity(), Give_bonus.class);
                startActivity(intent);
            }
        });

        mButton_Bonus_to_Money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(my_account_e_cash.this,"@"+can_use_bonus.getText().toString(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("can_use_bonus", can_use_bonus);
                intent.putExtras(bundle);
                intent.setClass(getActivity(), Ecash_To_Money.class);
                startActivity(intent);
            }
        });

        mLinearLayout_Update_Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), Activity_Fix_Mb_Info.class);
                startActivity(intent);
            }
        });

        mLinearLayout_Fix_Pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), Activity_Fix_Pwd.class);
                startActivity(intent);
            }
        });

        mLinearLayout_Search_Order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), my_account_order.class);
                startActivity(intent);
            }
        });

        mLinearLayout_Bonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("from", "e_cash");
                intent.putExtras(bundle);
                intent.setClass(getActivity(), my_account_e_cash.class);
                startActivity(intent);
            }
        });

        create_thread("my_account", "my_account");
       // create_thread2();

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        create_thread("my_account", "my_account");
        create_thread2();
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
    public void create_thread(final String cmd,String thread_name) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        if (cmd == "my_account") {
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

    public final void newlist(String input)
    {
        //Toast.makeText(getApplicationContext(),tab_status, Toast.LENGTH_SHORT).show();
        //tab_status="cus";
        Log.d("newlist", input);
        try
        {
            JSONArray jsonArray = new JSONArray(input);
            JSONObject jsonData = jsonArray.getJSONObject(0);
            if(getActivity()==null){
                return;
            }
            TextView login_name=(TextView)getActivity().findViewById(R.id.login_name);
            TextView grade_name=(TextView)getActivity().findViewById(R.id.grade_name);
            TextView intro_num=(TextView)getActivity().findViewById(R.id.intro_num);
            TextView rebuy_num=(TextView)getActivity().findViewById(R.id.rebuy_num);
            TextView rebuy_state=(TextView)getActivity().findViewById(R.id.rebuy_state);
            TextView a_line_subs_old=(TextView)getActivity().findViewById(R.id.a_line_subs_old);
            TextView b_line_subs_old=(TextView)getActivity().findViewById(R.id.b_line_subs_old);
            TextView a_line_subs_new=(TextView)getActivity().findViewById(R.id.a_line_subs_new);
            TextView b_line_subs_new=(TextView)getActivity().findViewById(R.id.b_line_subs_new);
            TextView a_line_subs_sum=(TextView)getActivity().findViewById(R.id.a_line_subs_sum);
            TextView b_line_subs_sum=(TextView)getActivity().findViewById(R.id.b_line_subs_sum);

            if(getActivity()==null){
                return;
            }

            ImageView mb_photo=(ImageView)getActivity().findViewById(R.id.mb_photo);
            if(mb_photo==null){
                return;
            }
            if(jsonData.getString("grade_class").equals("0")){
                mb_photo.setBackgroundResource(R.drawable.g003);
            }
            if(jsonData.getString("grade_class").equals("5")){
                mb_photo.setBackgroundResource(R.drawable.g005);

            }
            if(jsonData.getString("grade_class").equals("10")){
                mb_photo.setBackgroundResource(R.drawable.g008);

            }
            if(jsonData.getString("grade_class").equals("15")){
                mb_photo.setBackgroundResource(R.drawable.g004);

            }
            if(jsonData.getString("grade_class").equals("20")){
                mb_photo.setBackgroundResource(R.drawable.g023);

            }
            if(jsonData.getString("grade_class").equals("25")){
                mb_photo.setBackgroundResource(R.drawable.g025);

            }
            if(jsonData.getString("grade_class").equals("30")){
                mb_photo.setBackgroundResource(R.drawable.g021);

            }
            if(jsonData.getString("grade_class").equals("35")){
                mb_photo.setBackgroundResource(R.drawable.g026);

            }
            if(jsonData.getString("grade_class").equals("40")){
                mb_photo.setBackgroundResource(R.drawable.g027);

            }
            login_name.setText(jsonData.getString("mb_name"));
            grade_name.setText(jsonData.getString("grade_name"));
            rebuy_num.setText(jsonData.getString("rebuy_num")+" CV");
           /* if(Integer.parseInt(jsonData.getString("rebuy_num"))>=20){
                rebuy_state.setText("合格");
            }else{
                rebuy_state.setText("不合格");
            }*/
            rebuy_state.setText(jsonData.getString("pg_date2"));
            //a_line_subs_sum.setText(jsonData.getString("a_line_subs_sum")+" PV");
            //b_line_subs_sum.setText(jsonData.getString("b_line_subs_sum")+" PV");
            intro_num.setText(jsonData.getString("intro_sum"));
            a_line_subs_old.setText(jsonData.getString("a_line_subs")+" CV"); //左線前期剩餘
            b_line_subs_old.setText(jsonData.getString("b_line_subs")+" CV"); //右線前期剩餘
            a_line_subs_new.setText(jsonData.getString("per_sum")+" CV"); //個人業績累積
            b_line_subs_new.setText(jsonData.getString("org_m")+" CV"); //組織業積
            mTextView_Left_Count.setText(jsonData.getString("left_intro_sum"));
            mTextView_Right_Count.setText(jsonData.getString("right_intro_sum"));
            loadding_flag=true;
            chk_loadding();

        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Toast.makeText(getActivity(), "" + e.toString(), Toast.LENGTH_LONG).show();
            Log.e("json", e.toString());
        }

    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        Log.d("whereeeeeee", String.valueOf(position));
        if(position==0){
            Intent intent = new Intent();
            intent.setClass(getActivity(), my_account_order.class);
            startActivity(intent);

        }else if(position==1){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("from", "e_cash");
            intent.putExtras(bundle);
            intent.setClass(getActivity(), my_account_e_cash.class);
            startActivity(intent);

        }/*else if(position==2){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("from", "bonus");
            intent.putExtras(bundle);
            intent.setClass(getActivity(), my_account_e_cash.class);
            startActivity(intent);
        }else if(position==3){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("from", "bonus2");
            intent.putExtras(bundle);
            intent.setClass(getActivity(), my_account_e_cash.class);
            startActivity(intent);
        }else if(position==4){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("from", "e_cash2");
            intent.putExtras(bundle);
            intent.setClass(getActivity(), my_account_e_cash.class);
            startActivity(intent);
        }else if(position==5){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("from", "e_gold");
            intent.putExtras(bundle);
            intent.setClass(getActivity(), my_account_e_cash.class);
            startActivity(intent);
        }else if(position==6){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("from", "e_pv");
            intent.putExtras(bundle);
            intent.setClass(getActivity(), my_account_e_cash.class);
            startActivity(intent);
        }*/
        super.onListItemClick(l, v, position, id);

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
            TextView can_tv=(TextView)v.findViewById(R.id.can_tv);
            TextView cant_tv=(TextView)v.findViewById(R.id.cant_tv);

            if(position==0){
                can_tv.setVisibility(View.GONE);
                cant_tv.setVisibility(View.GONE);

            }
            return v;
        }
    }
    private String readFromFile(String FILENAME) {
        try{

            FileInputStream fin = getActivity().openFileInput(FILENAME);
            byte[] buff = new byte[fin.available()];
            fin.read(buff);
            String str = new String(buff);

            return str;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    private void chk_loadding() {
        FrameLayout Fralayout = (FrameLayout)getView().findViewById(R.id.acount_loadding);
        // LinearLayout LinearLayout = (android.widget.LinearLayout)getView().findViewById(R.id.Top_bar);
        // FrameLayout loadding=(FrameLayout)getView().findViewById(R.id.FF1) ;
        if (loadding_flag == true){//讀好了
            //  LinearLayout.setVisibility(View.VISIBLE);
            Fralayout.setVisibility(View.GONE);
            Log.d("Fralayout","finish");
            //   loadding.setVisibility(View.GONE);
        }else{
            Fralayout.setVisibility(View.VISIBLE);
            Log.d("Fralayout" , "error");
            //  loadding.setVisibility(View.VISIBLE);
            // LinearLayout.setVisibility(View.GONE);
        }
    }
    public void create_thread2() {
        mThread = new HandlerThread("123");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery2("get_ecash_point");
                mUI_Handler.post(new Runnable() {
                    public void run() {

                        newlist2(jsonString);

                    }
                });
            }
        });
    }
    private String executeQuery2(String query)
    {
        String result = "";
        try
        {
            login_id = new JSONObject(config).getString("login_id");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd","get_bonus_ios"));
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
    public final void newlist2(String input)
    {
        try
        {
            Log.d("sddsdsfdsffds",input);
            JSONArray jsonArray = new JSONArray(input);
            JSONObject jsonData = jsonArray.getJSONObject(0);
            //mTextView_Bonus.setText(jsonData.getString("eff_point"));
        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());
        }

    }

}