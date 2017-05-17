package com.example.nick.joeyi_android2;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class product_list_new extends Fragment {
    private ViewPager viewPager;//页卡内容
    private ImageView imageView;// 动画图片
    private TextView textView1,textView2,textView3;
    private List<View> views;// Tab页面列表
    ArrayList<View> viewContainter;
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度
    private int total_w;
    private int total_tab;
    public int pic_num;
    String ip = "", folder = "";
    private String Paths;
    View []view2;
    private String[] tab_title;
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
    String kind;
    View v;
    String []new_kind;

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        // do not call super.onSaveInstanceState()
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        load_config();
        Bundle bundle = getArguments();
        kind = bundle.getString("level1");

        Log.d("level1level1level1",kind);
        get_kind();

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
        v = inflater.inflate(R.layout.product_list_bykind, container, false);
        get_kind();

        return v;
    }
    private void InitViewPager(View v) {
        viewPager=(ViewPager) v.findViewById(R.id.vPager);

        views=new ArrayList<View>();
        view2=new View[total_tab];
        int layout=R.layout.hot_list;
        for(int i=0;i<total_tab;i++){

            view2[i] = LayoutInflater.from(getActivity()).inflate(layout, null);
            views.add(view2[i]);

            ListView ls=(ListView)view2[i].findViewById(R.id.pro_list);
            int []bk={R.drawable.bk001,R.drawable.bk002};
            if(kind.equals("87")){
                ls.setBackgroundResource(bk[0]);
            }
            if(kind.equals("86")){
                ls.setBackgroundResource(bk[1]);

            }
            if(kind.equals("84")){
                //ls.setBackgroundResource(bk[0]);
            }

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
            Log.d("III", String.valueOf(i));
            TextView tv = new TextView(getActivity());
            tv.setLayoutParams(txtParams);
            tv.setTextSize(14);
            tv.setText(tab_title[i]);
            tv.setGravity(Gravity.CENTER);
            tv.setOnClickListener(new MyOnClickListener(i));
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





        getActivity().getActionBar().setTitle(tab_title[0]);


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
                10);
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
            get_kind_product();
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

            get_kind_product();
            //if(currIndex==1){//熱銷

            // }

        }

    }


    public void get_kind() {
        mThread = new HandlerThread("get_kind");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_kind_data();

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        renew_kind_name(jsonString);

                    }
                });
            }
        });
    }

    public void get_kind_product() {
        mThread = new HandlerThread("get_kind_product");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_kind_product_data();

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        renew_kind_product(jsonString);

                    }
                });
            }
        });
    }

    private String get_kind_product_data() {
        Log.d("1111111", "1111111");
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            //HttpPost post = new HttpPost("http://220.135.161.128/leo/android_sql.php");
            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", "get_product"));
            nameValuePairs.add(new BasicNameValuePair("level1",kind));
            Log.d("level1level1",kind);

            if(currIndex!=0) {
                nameValuePairs.add(new BasicNameValuePair("level2", new_kind[currIndex]));
            }
            Log.d("tab_kind", new_kind[currIndex]);
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
    private String get_kind_data() {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            //HttpPost post = new HttpPost("http://220.135.161.128/leo/android_sql.php");
            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", "get_kind"));
            nameValuePairs.add(new BasicNameValuePair("kind_no",kind));
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







    public final void renew_kind_product(String input) {
        Log.d("renew_kind_product_tmp",input);
        try {
            JSONArray jsonArray = new JSONArray(input);

            list2 = new ArrayList<HashMap<String,String>>();
            pro_listView = (ListView)view2[currIndex].findViewById(R.id.pro_list);
            pro_listView.setOnItemClickListener(listener2);
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
                    item2.put("mv", "" + jsonData.getString("mv"));
                    item2.put("pic_name", "" + jsonData.getString("name"));
                    item2.put("prod_summary", "" + jsonData.getString("prod_summary"));

                    list2.add(item2);

                }
                listAdapter2 = new MySimpleAdapter2(getActivity(), list2, R.layout.product_list,new String[]{"prod_no", "prod_name", "comp_price", "pv","mv", "pic_name","prod_summary"},new int[]{R.id.txv_product_id, R.id.txv_product_name, R.id.txv_product_money, R.id.txv_product_pv,R.id.txv_product_mv, R.id.img_name,R.id.prod_summary});
                pro_listView.setAdapter(listAdapter2);
            }else{
                pro_no=new String[1];
                item2 = new HashMap<String, String>();
                pro_no[0] ="目前尚無產品";
                item2.put("prod_no", "目前尚無產品");
                list2.add(item2);
                listAdapter2 = new MySimpleAdapter2(getActivity(), list2, R.layout.product_list,new String[]{"prod_no"},new int[]{R.id.txv_product_name});
                pro_listView.setAdapter(listAdapter2);
            }





        } catch (JSONException e) {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

        }

    }
    public final void renew_kind_name(String input) {
        Log.d("renew_kind_name",input);
        try {
            JSONArray jsonArray = new JSONArray(input);
            tab_title=new String[jsonArray.length()];
            total_tab=jsonArray.length();
            new_kind=new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                tab_title[i]=jsonData.getString("kind_name");
                new_kind[i]=jsonData.getString("kind_no");
            }
            InitImageView(v);
            InitTextView(v);
            InitViewPager(v);
            // viewPager.setCurrentItem(total_tab-1);
            // viewPager.setCurrentItem(0);




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
            if(img_name.getText().length()>0&&img_name.getText()!=null){
                Paths = "http://"+ip+"/ct/shoppingcart/prod_images/180180/"+img_name.getText().toString();
            }else{
                Paths = "http://"+ip+"/ct/shoppingcart/prod_images/180180/coming_soon_b.gif";
            }
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
            Log.d("pro_no[position]",pro_no[position]);
            if(pro_no[position]!="目前尚無產品"){
                bundle.putString("prod_no", pro_no[position] );
                bundle.putString("from","1");
                intent.putExtras(bundle);
                intent.setClass(getActivity(), product_detail_list.class);
                startActivity(intent);
                getActivity().finish();
            }

        }
    };
}