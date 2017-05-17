package com.example.nick.joeyi_android2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class shopping_index extends Fragment {
    private ViewPager viewPager;//页卡内容
    private ImageView imageView;// 动画图片
    private TextView textView1,textView2,textView3;
    private List<View> views;// Tab页面列表
    ArrayList<View> viewContainter;
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度
    private int total_w;
    private int total_tab=3;
    public int pic_num;
    String ip = "", folder = "";
    private String Paths;
    View []view2=new View[total_tab];
    private String[] tab_title={"每日好康","熱銷商品","優惠訊息"};
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
    int []times={0,0,0};
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        // do not call super.onSaveInstanceState()
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        load_config();

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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("onCreate2", "onCreate ");

        View v = inflater.inflate(R.layout.shopping_index, container, false);
        InitImageView(v);
        InitTextView(v);
        InitViewPager(v);
        getActivity().getActionBar().setTitle("每日好康");
        // viewPager.setCurrentItem(1);
        // viewPager.setCurrentItem(0);

        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(Color.parseColor("#F29500"));
        getActivity().getActionBar().setBackgroundDrawable(colorDrawable);

        return v;
    }
    private void InitViewPager(View v) {
        viewPager=(ViewPager) v.findViewById(R.id.vPager);
        views=new ArrayList<View>();

        int []layout={R.layout.banner,R.layout.hot_list,R.layout.discount_message};
        for(int i=0;i<total_tab;i++){

            view2[i] = LayoutInflater.from(getActivity()).inflate(layout[i], null);
            views.add(view2[i]);

        }
        viewPager.setAdapter(new MyViewPagerAdapter(views));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }
    /**
     *  初始化头标
     */

    private void InitTextView(View v) {
        LinearLayout ll = (LinearLayout)v.findViewById(R.id.linearLayout1);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        total_w=dm.widthPixels;
        Log.d("total_w", String.valueOf(total_w));

        LinearLayout.LayoutParams txtParams = new LinearLayout.LayoutParams(
                total_w/total_tab-3,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        FrameLayout.LayoutParams lineParams = new FrameLayout.LayoutParams(
                3,
                ViewGroup.LayoutParams.FILL_PARENT);



        for(int i=0;i<total_tab;i++){

            TextView tv = new TextView(getActivity());
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

            View line=new View(getActivity());
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

    private void InitImageView(View v) {
        imageView= (ImageView) v.findViewById(R.id.cursor);

        // bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a).getWidth();// 获取图片宽度

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        int screenW = dm.widthPixels;// 获取分辨率宽度
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                (screenW / total_tab)-3,
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

    public class MyViewPagerAdapter extends PagerAdapter{
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
            if(position==0){//每日
              //  if(times[0]==0){
                    get_news_banner();
             //       times[0]++;

              //  }

            }
            if(position==1){//熱銷
                get_hot_product();
                times[1]++;
            }
            if(position==2){//優惠訊息
                get_discount_thread();
                times[2]++;
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
    public void get_hot_product() {
        mThread = new HandlerThread("get_prod_no_detail");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_hot_product_data();
               // Log.d("get_hot_product","get_hot_product"+jsonString);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        renew_hot_product(jsonString);

                    }
                });
            }
        });
    }
    public void get_news_banner() {
        Log.d("get_news_banner", "get_news_banner ");
        mThread = new HandlerThread("get_news_banner");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_news_banner_data();
                Log.d("get_news_banner","get_news_banner"+jsonString);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        renew_img_data(jsonString);
                    }
                });
            }
        });
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
                getActivity().getActionBar().setTitle("每日好康");
            }
            if(currIndex==1){
                getActivity().getActionBar().setTitle("熱銷商品");
            }
            if(currIndex== 2){
                getActivity().getActionBar().setTitle("優惠訊息");
            }


        }

    }


    private String get_hot_product_data() {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            //HttpPost post = new HttpPost("http://220.135.161.128/leo/android_sql.php");
            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", "get_hot_product_data"));
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

    private String get_news_banner_data() {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            //HttpPost post = new HttpPost("http://220.135.161.128/leo/android_sql.php");
            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", "get_day_news"));
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

    private String send_discount_data() {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            //HttpPost post = new HttpPost("http://220.135.161.128/leo/android_sql.php");
            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", "send_discount_data"));
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

            list = new ArrayList<HashMap<String,String>>();
            listView = (ListView)view2[2].findViewById(R.id.discount_news);
            listView.setOnItemClickListener(listener);
            news_id=new String[jsonArray.length()];
            MySimpleAdapter listAdapter;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                item = new HashMap<String, String>();

                news_id[i]=jsonData.getString("news_no");
                // item.put("postTime", "【" +jsonData.getString("postTime") +"】");
                Log.d("postTime",jsonData.getString("postTime"));
                item.put("title", "" + jsonData.getString("title"));

                list.add(item);

            }
            if(getActivity()==null){
                return;
            }
            listAdapter = new MySimpleAdapter(getActivity(), list, R.layout.dis_news,new String[]{"title"},new int[]{R.id.dis_title});
            listView.setAdapter(listAdapter);




        } catch (JSONException e) {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

        }

    }
    public final void renew_hot_product(String input) {
        Log.d("renew_hot_product",input);
        try {
            JSONArray jsonArray = new JSONArray(input);

            list2 = new ArrayList<HashMap<String,String>>();
            pro_listView = (ListView)view2[1].findViewById(R.id.pro_list);
            pro_listView.setOnItemClickListener(listener2);
            pro_no=new String[jsonArray.length()];
            MySimpleAdapter2 listAdapter2;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                item2 = new HashMap<String, String>();

                pro_no[i]=jsonData.getString("prod_no");

                item2.put("prod_name", "" + jsonData.getString("prod_name"));
                item2.put( "comp_price",""+jsonData.getString("comp_price"));
                item2.put("pv", "" + jsonData.getString("pv"));
                item2.put("mv",""+jsonData.getString("mv"));
                item2.put("pic_name", "" + jsonData.getString("name"));
                item2.put("prod_summary", "" + jsonData.getString("prod_summary"));
                list2.add(item2);

            }
            if(getActivity()==null){
                return;
            }
            listAdapter2 = new MySimpleAdapter2(getActivity(), list2, R.layout.product_list,new String[]{"prod_no", "prod_name", "comp_price", "pv","mv", "pic_name","prod_summary"},new int[]{R.id.txv_product_id, R.id.txv_product_name, R.id.txv_product_money, R.id.txv_product_pv,R.id.txv_product_mv, R.id.img_name,R.id.prod_summary});
            pro_listView.setAdapter(listAdapter2);




        } catch (JSONException e) {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

        }

    }
    public final void renew_img_data(String input) {
        Log.d("logloglog", input);
        try {
            //loadingIcon = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.coming_soon_b);
            JSONArray jsonArray = new JSONArray(input);
            //   pager = (ViewPager) view2[0].findViewById(R.id.viewPager_banner);
            final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
            final FrameLayout.LayoutParams laParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT);
            pic_num=jsonArray.length();
            final LinearLayout []li_array=new LinearLayout[jsonArray.length()];
            final LinearLayout banner_layout=(LinearLayout)view2[0].findViewById(R.id.banner_layout);
            banner_layout.removeAllViews();
           // complete_pic=0;
           // viewContainter = new ArrayList<View>();
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);

                    Paths = "http://" + ip + "/ct/ad_images/" + jsonData.getString("img");
                    Log.d("Paths",Paths);
                    pic_num=jsonArray.length();
                    //if(pic_num==1){
                    //    pic_num++;
                    //}
                    if(getActivity()==null){
                        return;
                    }
                    LinearLayout li = new LinearLayout(getActivity());
                    li.setLayoutParams(layoutParams);
                    Log.d("WebView", "WebView");

                    WebView imgv = new WebView(getActivity());
                    imgv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                    imgv.setLayoutParams(laParams);
                    try
                    {
                        Method method = imgv.getClass().getDeclaredMethod("setVerticalThumbDrawable", Drawable.class);
                        method.setAccessible(true);
                        method.invoke(imgv, getResources().getDrawable(R.drawable.scrollbar_vertical_track));

                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    imgv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//不加上，会显示白边

                 //   imgv.getSettings().settings.setUseWideViewPort(true);
                  //  imgv.getSettings().settings.setLoadWithOverviewMode(true);
                   // String htmldata="<img style='width:105.2%; margin:-10;' src ='"+Paths+"'>";//网页代码
                    String htmldata="<style>body {margin: 0;padding: 0;}</style>";
                     htmldata+="<body><img style='width:100%;' src ='"+Paths+"'> </body>";//网页代码
                    String targeturl="";//目标网址（具体）
                    String baseurl="";//连接目标网址失败进入的默认网址
                    imgv.getSettings().setDefaultTextEncodingName("GB2312");
                    imgv.loadData(htmldata, "text/html", "utf-8");
                    imgv.loadDataWithBaseURL(targeturl, htmldata, "text/html", "utf-8", baseurl);
                    banner_layout.addView(imgv);



                /*    Log.d("li_array", String.valueOf("原來"+li_array[0]));
                    Log.d("li_array", String.valueOf("原來"+li_array[1]));

                    final String Paths2=Paths;
                    final int finalI = i;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Bitmap mBitmap = getBitmapFromURL(Paths2);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mBitmap != null) {
                                        LinearLayout li = new LinearLayout(getActivity());
                                        li.setLayoutParams(layoutParams);

                                        ImageView imgv = new ImageView(getActivity());
                                        imgv.setLayoutParams(laParams);
                                        imgv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imgv.setImageBitmap(mBitmap);
                                        li.addView(imgv);
                                        li_array[finalI] = li;
                                        complete_pic++;


                                    } else {
                                        LinearLayout li = new LinearLayout(getActivity());
                                        li.setLayoutParams(layoutParams);

                                        ImageView imgv = new ImageView(getActivity());
                                        imgv.setImageBitmap(loadingIcon);
                                        li.addView(imgv);
                                        li_array[finalI] = li;
                                        complete_pic++;

                                    }
                                    if (complete_pic == pic_num) {
                                        for (int i = 0; i < pic_num; i++) {
                                            banner_layout.addView(li_array[i]);

                                        }
                                        //pager.setAdapter(pagerAdapter);
                                    }
                                }
                            });
                        }
                    }).start();*/
                }


            }
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            /*else{

                LinearLayout li = new LinearLayout(getActivity());
                li.setLayoutParams(layoutParams);

                ImageView imgv = new ImageView(getActivity());
                imgv.setImageBitmap(loadingIcon);
                li.addView(imgv);
                banner_layout.addView(li);
                //viewContainter.add(li);
                // pager.setAdapter(pagerAdapter);
            }*/

        } catch (JSONException e) {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

        }
      //  mThread.getLooper().quit();
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
    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,//parent就是ListView，view表示Item视图，position表示数据索引
                                long id) {



            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("news_no",news_id[position]);
            intent.putExtras(bundle);
            intent.setClass(getActivity(), dis_new_detail.class);
            startActivity(intent);
        }
    };


    private class MySimpleAdapter2 extends SimpleAdapter{
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
            TextView img_name=(TextView)v.findViewById(R.id.img_name);
            Paths = "http://"+ip+"/ct/shoppingcart/prod_images/180180/"+img_name.getText().toString();
            Log.d("now_path",Paths);
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
    private AdapterView.OnItemClickListener listener2 = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,//parent就是ListView，view表示Item视图，position表示数据索引
                                long id) {



            Intent intent = new Intent();
            Bundle bundle = new Bundle();

            bundle.putString("prod_no", pro_no[position]);
            bundle.putString("from","0");
            intent.putExtras(bundle);
            intent.setClass(getActivity(), product_detail_list.class);
            startActivity(intent);
        }
    };

    public void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "onDestroy ");
        if(mThread!=null){
        mThread.getLooper();
        mThread.getLooper().quitSafely();
        }
        // .quit();
    }
}