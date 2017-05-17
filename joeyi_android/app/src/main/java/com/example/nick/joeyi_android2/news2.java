package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
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
 * Created by User on 2016/4/11.
 */
public class news2 extends Activity{
    private ViewPager viewPager;//页卡内容
    private ImageView imageView;// 动画图片
    private TextView textView1,textView2,textView3;
    private List<View> views;// Tab页面列表
    ArrayList<View> viewContainter;
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度
    private int total_w;
    private int total_tab=2;
    public int pic_num;
    String ip = "", folder = "";
    private String Paths;
    View []view2=new View[total_tab];
    private String[] tab_title={"最新訊息","推撥訊息"};
    private Bitmap loadingIcon;
    private int complete_pic =0;
    ViewPager pager = null;
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Handler mUI_Handler = new Handler();
    private HashMap<String,String> item,item2;
    private ArrayList<HashMap<String,String>> list;
    private ArrayList<HashMap<String,String>> list2;
    private ListView listView;
    private ListView pro_listView;
    String []news_id;
    String []pro_no;
    int []times={0,0};
    private WebView webview;
    private TextView webview_txt;
    String config="",mb_no,login_id;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.news2);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        setTitle("最新訊息");


        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null && bundle.containsKey("mb_no"))
        {
            mb_no = bundle.getString("mb_no");

        }else{
            Log.d("reply", "reply ");
            mb_no = "no";
        }
        load_config();
        config  = readFromFile("client_config");
        InitImageView();
        InitTextView();
        InitViewPager();
        if(!mb_no.equals("no")) {
            viewPager.setCurrentItem(1);
        }
        // viewPager.setCurrentItem(1);
        // viewPager.setCurrentItem(0);

//        ColorDrawable colorDrawable = new ColorDrawable();
//        colorDrawable.setColor(Color.parseColor("#F29500"));
//        getActionBar().setBackgroundDrawable(colorDrawable);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_normal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mb_no.equals("no")) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    return true;
                default:
                    break;

            }
        }else{
            switch (item.getItemId()) {
                case android.R.id.home:
                    Intent intent = new Intent();
                    intent.setClass(news2.this, login.class);
                    finish();
                    startActivity(intent);
                    return true;
                default:
                    break;

            }
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
    private void InitViewPager() {
        viewPager=(ViewPager) findViewById(R.id.vPager);
        views=new ArrayList<View>();

        int []layout={R.layout.activity_news,R.layout.activity_news};
        for(int i=0;i<total_tab;i++){
            view2[i] = LayoutInflater.from(this).inflate(layout[i], null);
            views.add(view2[i]);
        }
        viewPager.setAdapter(new MyViewPagerAdapter(views));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }
    /**
     *  初始化头标
     */

    private void InitTextView() {
        LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout1);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        total_w=dm.widthPixels;
        Log.d("total_w", String.valueOf(total_w));

        LinearLayout.LayoutParams txtParams = new LinearLayout.LayoutParams(
                total_w/total_tab-2,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        FrameLayout.LayoutParams lineParams = new FrameLayout.LayoutParams(
                2,
                ViewGroup.LayoutParams.FILL_PARENT);



        for(int i=0;i<total_tab;i++){

            TextView tv = new TextView(this);
            tv.setLayoutParams(txtParams);
            tv.setTextSize(16);
            tv.setText(tab_title[i]);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(Color.parseColor("#555555"));
            tv.setBackgroundColor(Color.parseColor("#FFFFFF"));
            tv.setOnClickListener(new MyOnClickListener(i));

            //tv.setPadding(0,0,0,-10);
        /*    <View
            android:layout_height="match_parent"
            android:background="#D9D9D9"
            android:layout_width="0.5dip"/> */

            View line=new View(this);
            line.setLayoutParams(lineParams);
            line.setBackgroundColor(Color.parseColor("#D9D9D9"));
            ll.addView(tv);

            if(i<total_tab-1){
                ll.addView(line);

            }

        }
        // 將 TextView 加入到 LinearLayout 中


        //  textView1.setOnClickListener(new MyOnClickListener(0));
        //  textView2.setOnClickListener(new MyOnClickListener(1));
        //  textView3.setOnClickListener(new MyOnClickListener(2));
    }

    /**
     2      * 初始化动画，这个就是页卡滑动时，下面的横线也滑动的效果，在这里需要计算一些数据
     3 */

    private void InitImageView() {
        imageView= (ImageView) findViewById(R.id.cursor);

        // bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a).getWidth();// 获取图片宽度

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int screenW = dm.widthPixels;// 获取分辨率宽度
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                (screenW / total_tab)-2,
                5);
        imageView.setLayoutParams(lineParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        offset = (screenW / total_tab - bmpW) / (2);// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset,0);
        imageView.setImageMatrix(matrix);// 设置动画初始位置
    }
    /**
     *
     * 头标点击监听 3 */
    private class MyOnClickListener implements View.OnClickListener {
        private int index=0;
        public MyOnClickListener(int i){
            index=i;
        }
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }

    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;

        public MyViewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)   {
            container.removeView(mListViews.get(position));
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mListViews.get(position), 0);
            if(position==0){//最新消息
                get_discount_thread();
            }
            if(position==1){//推撥訊息
                get_news_thread();
                times[1]++;
            }

            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return  mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==arg1;
        }
    }
    public void get_news_thread() {
        mThread = new HandlerThread("get_contact_data");
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
            if(mb_no.equals("no")) {
                login_id = new JSONObject(config).getString("login_id");
            }else{
                login_id = mb_no;
            }
            Log.e("+++++++++++",login_id);
            HttpClient httpClient = new DefaultHttpClient();
            //HttpPost post = new HttpPost("http://220.135.161.128/leo/android_sql.php");
            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", "get_push_data"));
            nameValuePairs.add(new BasicNameValuePair("mb_no",login_id));
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
    public final void get_news_data_res(String input) {
        Log.d("input",input);

        try {
            list = new ArrayList<HashMap<String, String>>();
            listView = (ListView) view2[1].findViewById(R.id.listView_new);
            MySimpleAdapter2 listAdapter;
            if (input.equals("1\n")) {
                item = new HashMap<String, String>();
                item.put("title", "【目前尚無推撥訊息】");
                item.put("web_view", "");
                list.add(item);

                listAdapter = new MySimpleAdapter2(this, list, R.layout.dis_contact, new String[]{"title"}, new int[]{R.id.dis_title});
                listView.setAdapter(listAdapter);
            }else{

                JSONArray jsonArray = new JSONArray(input);

                //news_id = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    item = new HashMap<String, String>();

                    //news_id[i] = jsonData.getString("id");
                    item.put("title", "【" + jsonData.getString("title") + "】");
                    item.put("web_view", jsonData.getString("web_view"));

                    list.add(item);

                }
                listAdapter = new MySimpleAdapter2(this, list, R.layout.dis_contact, new String[]{"title", "web_view"}, new int[]{R.id.dis_title, R.id.webview_txt});
                listView.setAdapter(listAdapter);
            }

        } catch (JSONException e) {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

        }

    }
    public void get_discount_thread() {
        mThread = new HandlerThread("get_prod_no_detail");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = send_discount_data();

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        renew_data(jsonString);

                    }
                });
            }
        });
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
        int two = one * 2;// 页卡1 -> 页卡3 偏移量
        public void onPageScrollStateChanged(int arg0) {


        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {


        }

        public void onPageSelected(int arg0) {

            Animation animation = new TranslateAnimation(one*currIndex, one*arg0, 0, 0);//显然这个比较简洁，只有一行代码。
            currIndex = arg0;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            imageView.startAnimation(animation);

            if(currIndex==0){
                getActionBar().setTitle("最新訊息");
            }
            if(currIndex==1){
                getActionBar().setTitle("推撥訊息");
            }

        }

    }

    private String send_discount_data() {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            //HttpPost post = new HttpPost("http://220.135.161.128/leo/android_sql.php");
            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", "get_news_data"));
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
        Log.d("input",input);
        try {
            JSONArray jsonArray = new JSONArray(input);
            MySimpleAdapter listAdapter;
            list = new ArrayList<HashMap<String,String>>();
            listView = (ListView)view2[0].findViewById(R.id.listView_new);
            if (jsonArray.length()==0) {
                item = new HashMap<String, String>();
                item.put("title", "【目前尚無最新訊息】");
                list.add(item);

                listAdapter = new MySimpleAdapter(this, list, R.layout.dis_news,new String[]{"title"},new int[]{R.id.dis_title});
                listView.setAdapter(listAdapter);
            }else{
                listView.setOnItemClickListener(listener);
                news_id=new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    item = new HashMap<String, String>();

                    news_id[i]=jsonData.getString("news_no");
                    Log.e("new_no",news_id[i]);
                    // item.put("postTime", "【" +jsonData.getString("postTime") +"】");
                    Log.d("postTime",jsonData.getString("postTime"));
                    item.put("title", "" + jsonData.getString("title"));
                    Log.e("sheng123",jsonData.getString("title"));

                    list.add(item);

                }

                listAdapter = new MySimpleAdapter(this, list, R.layout.dis_news,new String[]{"title"},new int[]{R.id.dis_title});
                listView.setAdapter(listAdapter);
            }
        } catch (JSONException e) {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

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
    private class MySimpleAdapter2 extends SimpleAdapter {

        public MySimpleAdapter2(Context context,
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
            webview = (WebView)v.findViewById(R.id.context);
            webview_txt = (TextView)v.findViewById(R.id.webview_txt);
            String targeturl="";//目标网址（具体）
            String baseurl="";//连接目标网址失败进入的默认网址
            webview.getSettings().setDefaultTextEncodingName("GB2312");
            webview.loadData(webview_txt.getText().toString(), "text/html", "utf-8");
            webview.loadDataWithBaseURL(targeturl, webview_txt.getText().toString(), "text/html", "utf-8", baseurl);

            return v;
        }

    }
    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,//parent就是ListView，view表示Item视图，position表示数据索引
                                long id) {

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("news_no",news_id[position]);
            Log.e("send_no",news_id[position]);
            intent.putExtras(bundle);
            intent.setClass(news2.this, news_detail.class);
            startActivity(intent);
        }
    };

    public void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "onDestroy ");
        mThread.getLooper();
        mThread.getLooper().quitSafely();
        // .quit();
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
}
