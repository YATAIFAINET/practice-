package com.example.nick.joeyi_android2;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

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

/**
 * Created by User on 2016/1/6.
 */
public class video_main extends YouTubeBaseActivity {

    public static final String DEVELOPER_KEY = "AIzaSyBXkANCdAfmRnAhBfIaDRT30sT0vcGfhUg";
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    String ip="",folder="";
    private String[] video_kind,youtube_string,youtube_title;
    //private YouTubeThumbnailView youtube_thumbnail;
    private ImageView youtube_thumbnail;
    public YouTubePlayerView youtube_view;
    private Context context;
    private Spinner spinner;
    private ArrayAdapter kindList;
    private ArrayList<HashMap<String,String>> list;
    private MySimpleAdapter adapter;
    private HashMap<String,String> item,item2;
    private TextView video_name;
    String vid = "",title="",kind="";
    private ListView listview;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_main);
        load_config();
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null)
        {
            vid = bundle.getString("vid");
            title = bundle.getString("title");
            kind = bundle.getString("kind");
        }
        spinner = (Spinner) findViewById(R.id.spinner);
        create_thread("get_video_kind", "get_video_kind");
        context = this;
        // Youtube VideoView
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);

        youtube_view = (YouTubePlayerView) findViewById(R.id.video);
        video_name=(TextView)findViewById(R.id.video_name);
        //player=(YouTubePlayer)findViewById(R.id.video);
        //youtube_view.initialize(DEVELOPER_KEY, new YoutubeOnInitializedListener());
        listview=(ListView)findViewById(R.id.listview2);
    }
    private class YoutubeOnInitializedListener implements YouTubePlayer.OnInitializedListener {
        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
            Toast.makeText(context, "Youtube onInitializationFailure !", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
            if (!wasRestored) {
                //player.loadVideo(vid);    //自動撥放
                player.cueVideo(vid);   //不自動撥放
                player.setShowFullscreenButton(false);//關閉全螢幕
            }
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected", "onOptionsItemSelected ");
        switch(item.getItemId()) {
            case android.R.id.home:
                Log.d("onOptionsItemSelected2", "onOptionsItemSelected2 ");
                Intent intent = new Intent();
                intent.setClass(video_main.this, test_page.class);
                finish();
                startActivity(intent);

                return true;
            default:
                break;

        }
        //Toast.makeText(context, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
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
                        if (cmd == "get_video_kind") {
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
    public final void newlist(String input)
    {
        Log.d("fainet","input"+input);
        try
        {
            JSONArray jsonArray = new JSONArray(input);
            video_kind = new String[jsonArray.length()];
            //WEEK_NO[0]="請選擇";
            int kind_no=0;
            for (int i = 0; i <jsonArray.length(); i++)
            {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                video_kind[i]=jsonData.getString("name");
                if(jsonData.getString("name").equals(kind)){
                    kind_no=i;
                }

            }
            kindList = new ArrayAdapter<String>(video_main.this,R.layout.spinnerstyle_video, video_kind);
            spinner.setAdapter(kindList);
            spinner.setSelection(kind_no, true);
            if(kind==null){
                kind = video_kind[0];
            }
            get_video_list("get_video_list", "get_video_list");

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    if(!(title==null&&vid==null)&&(title.length()>0&&vid.length()>0)) {
                        //Toast.makeText(getApplicationContext(), "你選的是" + video_kind[position], Toast.LENGTH_SHORT).show();
                        kind = video_kind[position];
                        //get_video_list("get_video_list", "get_video_list");
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), video_main.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("title", "");
                        bundle.putString("vid", "");
                        bundle.putString("kind", kind);
                        intent.putExtras(bundle);

                        //切換Activity
                        startActivity(intent);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });







        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Toast.makeText(getApplication(), "" + e.toString(), Toast.LENGTH_LONG).show();
            Log.e("json", e.toString());
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent();
            intent.setClass(video_main.this, test_page.class);
            finish();
            startActivity(intent);
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }
    public void get_video_list(final String cmd,String thread_name) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery_list(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        if (cmd == "get_video_list") {
                            newlist_list(jsonString);
                        }
                    }
                });
            }
        });
    }
    private String executeQuery_list(String query)
    {
        String result = "";
        try
        {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("kind_name",kind));
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
    public final void newlist_list(String input)
    {
        Log.d("fainet","input"+input);
        try
        {

            JSONArray jsonArray = new JSONArray(input);
            list = new ArrayList<HashMap<String,String>>();
            youtube_string=new String[jsonArray.length()];
            youtube_title=new String[jsonArray.length()];
            if(jsonArray.length()>0) {
                listview.setOnItemClickListener(listener);
                for (int i = 0; i <jsonArray.length(); i++)
                {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    item2 = new HashMap<String, String>();
                    if(i==0 && ((vid==null && title==null) || (vid.length()<1 && title.length()<1))){
                        vid=jsonData.getString("content");
                        title=jsonData.getString("title");
                    }
                    youtube_string[i]=jsonData.getString("content");
                    youtube_title[i]=jsonData.getString("title");
                    item2.put("video_name", jsonData.getString("title"));
                    item2.put("video_time", jsonData.getString("postTime"));
                    item2.put("video_youtube_id", jsonData.getString("content"));
                    list.add(item2);
                }
                video_name.setText(title);
                youtube_view.initialize(DEVELOPER_KEY, new YoutubeOnInitializedListener());
                //adapter = new MySimpleAdapter(getActivity(), list, R.layout.my_account_list,new String[]{ "detail_title"},new int[]{R.id.my_account_list});
                //setListAdapter(adapter);


                adapter = new MySimpleAdapter(this, list, R.layout.video_list,new String[]{ "video_name", "video_time", "video_youtube_id"},new int[]{R.id.list_video_name, R.id.list_video_time, R.id.list_video_youtube_id});
                listview.setAdapter(adapter);
            }/*else{
                TextView subtotal=(TextView)findViewById(R.id.subtotal);//獎金總額
                subtotal.setText("$0");
                TextView tax=(TextView)findViewById(R.id.tax);//所得稅
                tax.setText("0");

                TextView tax2=(TextView)findViewById(R.id.tax2);//營業稅
                tax2.setText("0");
                TextView tax3=(TextView)findViewById(R.id.tax3);//補充保費
                tax3.setText("0");
                TextView money_adm=(TextView)findViewById(R.id.money_adm);//溢撥追回
                money_adm.setText("0");
                TextView givemoney=(TextView)findViewById(R.id.givemoney);//未稅獎金
                givemoney.setText("$0");

                String[] detail_title = {"推薦獎金","組織獎金","消費分紅","對等獎金","報單津貼","產品紅利","500K分紅"};
                String[] each_money = {"$ 0","$ 0","$ 0 ","$ 0","$ 0 ","$ 0","$ 0 "};
                list = new ArrayList<HashMap<String,String>>();
                for (int i = 0; i < 7; i++) {
                    item2 = new HashMap<String, String>();
                    item2.put("detail_title", detail_title[i]);
                    item2.put("each_money", each_money[i]);
                    list.add(item2);
                }
                adapter = new SimpleAdapter(this, list, R.layout.money_list,new String[]{ "detail_title", "each_money"},new int[]{R.id.tv_money_name, R.id.tv_money});
                //setListAdapter(adapter);
            }*/

        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

        }
    }
    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,//parent就是ListView，view表示Item视图，position表示数据索引
                                long id) {
            vid=youtube_string[position];
            title=youtube_title[position];
            //video_name.setText(youtube_title[position]);
            //youtube_view.initialize(DEVELOPER_KEY, new YoutubeOnInitializedListener());
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), video_main.class);
            Bundle bundle = new Bundle();
            bundle.putString("title", title);
            bundle.putString("vid", vid);
            bundle.putString("kind", kind);
            intent.putExtras(bundle);

            //切換Activity
            startActivity(intent);
        }
    };
    private class MySimpleAdapter extends SimpleAdapter {
        private LayoutInflater mInflater;
        private String Paths;
        private Bitmap loadingIcon;
        private AsyncImageFileLoader asyncImageFileLoader;
        View mGridView = null;

        public MySimpleAdapter(Context context,
                               List<? extends Map<String, ?>> data, int resource,
                               String[] from, int[] to) {
            super(context, data, resource, from, to);
            mInflater = LayoutInflater.from(context);
            loadingIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.comingsoon);
            mGridView = mInflater.inflate(R.layout.shopping_layout_old, null);

            asyncImageFileLoader = new AsyncImageFileLoader();
            // TODO Auto-generated constructor stub
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            int screenHeight = getWindowManager().getDefaultDisplay().getHeight();  // 屏幕高（像素，如：800p）
            int screenWidth = getWindowManager().getDefaultDisplay().getWidth();  // 屏幕寬（像素，如：800p）


            Log.d("position", String.valueOf(position));
            final View v= super.getView(position, convertView, parent);
            if(screenHeight>screenWidth) {
                //list_video_youtube_id = (TextView) v.findViewById(R.id.list_video_youtube_id);

                ViewHolder mHolder;

                //Paths = "http://" + ip + "/afs_dale/ct/shoppingcart/prod_images/180180/" + youtube_string[position];
                Paths = "http://i3.ytimg.com/vi/"+youtube_string[position]+"/hqdefault.jpg";

                if (convertView == null) {
                    convertView = v;
                    mHolder = new ViewHolder();
                    mHolder.icon = (ImageView) convertView.findViewById(R.id.youtube_thumbnail);
                    convertView.setTag(mHolder);
                }else {
                    mHolder = (ViewHolder) convertView.getTag();
                }

                //設定此mHolder.icon的tag為檔名，讓之後的callback function可以針對此mHolder.icon替換圖片
                ImageView imageView = mHolder.icon;
                imageView.setTag(Paths);

                Bitmap cachedBitmap = asyncImageFileLoader.loadBitmap(Paths, 180, 180, new AsyncImageFileLoader.ImageCallback() {
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

                if (cachedBitmap != null) {
                    mHolder.icon.setImageBitmap(cachedBitmap);
                } else {
                    //mHolder.icon.setImageBitmap(loadingIcon); //顯示預設的圖片
                }

            }
            return v;
        }
        private class ViewHolder
        {
            ImageView icon;
        }
    }
}
