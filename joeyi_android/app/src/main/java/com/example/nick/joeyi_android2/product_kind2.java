package com.example.nick.joeyi_android2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

/**
 * Created by root on 12/9/15.
 */
public class product_kind2 extends Fragment {
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> item;
    private String []menu_color;
    private int []menu_bk={R.drawable.cart_001,R.drawable.cart_002,R.drawable.cart_003,R.drawable.cart_004,R.drawable.cart_005,R.drawable.cart_006};
    private String []menu_bk2;
    private String []menu_level1;
    private String []menu_kind_name;
    private Boolean loadding_flag=false;
    String config="";

    String ip="",folder="";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        load_config();
        getActivity().getActionBar().setTitle("商品類別");

        Log.d("onCreateonCreateonCreate", "onCreate ");

    }

    public void onStart(){
        super.onStart();
        getActivity().getActionBar().setTitle("商品類別");
        create_thread("get_product_kind", "get_product_kind");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("onCreateViewonCreateViewonCreateView", "onCreateView ");
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(Color.parseColor("#F29500"));
        getActivity().getActionBar().setBackgroundDrawable(colorDrawable);

        return inflater.inflate(R.layout.product_kind2, container, false);
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
                        if (cmd == "get_product_kind") {
                            newKind(jsonString);
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
    public final void newKind(String input)
    {
        try
        {
            Log.d("newkind",input);
            JSONArray jsonArray = new JSONArray(input);
            menu_level1=new String[jsonArray.length()];
            menu_kind_name=new String[jsonArray.length()];
            menu_bk2=new String[jsonArray.length()];
            menu_color=new String[jsonArray.length()];
            if(jsonArray.length()>0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);

                    String kind_name=jsonData.getString("kind_name");
                    kind_name=kind_name.replace("", " ");
                    menu_level1[i]=jsonData.getString("level1");
                    menu_kind_name[i]=kind_name;
                    menu_bk2[i]=jsonData.getString("app_img");
                    menu_color[i]=jsonData.getString("app_color");

                }
                renew_img_data();

            }else{

            }

        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

        }
    }
    private Bitmap loadingIcon;
    private int pic_num;
    private String Paths;
    private int complete_pic =0;
    private LinearLayout kind_menu;

    public final void renew_img_data() {
        Log.d("renew_img_data","renew_img_data");
        if(getActivity()==null){
            return;
        }
            loadingIcon = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.comingsoon);
            pic_num=menu_bk2.length;
            final Bitmap []img=new Bitmap[menu_bk2.length];
            Log.d("renew_img_data", String.valueOf(menu_bk2.length));
            if (menu_bk2.length > 0) {
                complete_pic=0;
                for (int i = 0; i < menu_bk2.length; i++) {

                    Paths = "http://"+ip+"/ct/shoppingcart/prod_kind_images/"+menu_bk2[i];

                    Log.d("Paths",Paths);
                    pic_num=menu_bk2.length;

                    final String Paths2=Paths;
                    final int finalI = i;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Bitmap mBitmap = getBitmapFromURL(Paths2);
                            if( getActivity()==null){
                                return;
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mBitmap != null) {
                                        img[finalI]=mBitmap;
                                        complete_pic++;

                                    } else {
                                        img[finalI]=loadingIcon;
                                        complete_pic++;
                                    }
                                    if (complete_pic == pic_num) {

                                        kind_menu=(LinearLayout) getActivity().findViewById(R.id.kind_layout);
                                        if(kind_menu==null){
                                            return;
                                        }
                                        kind_menu.removeAllViews();
                                        for (int i = 0; i < pic_num; i++) {
                                            Log.d("run_i", String.valueOf(i));
                                            View b= LayoutInflater.from(getActivity()).inflate(R.layout.product_kind_list, null);

                                            //(b.findViewById(R.id.prod_kind_linear2)).setBackgroundColor(Color.parseColor(menu_color[i]));
                                            //b.findViewById(R.id.prod_kind_linear2).getBackground().setAlpha(179);
                                            Drawable dr = new BitmapDrawable(img[i]);
                                            (b.findViewById(R.id.prod_kind_linear)).setBackgroundDrawable(dr);
                                            TextView tv= (TextView)(b.findViewById(R.id.prod_kind_name));
                                            tv.setText(menu_kind_name[i]);
                                            tv.setTag(i);
                                            b.setTag(i);
                                            kind_menu.addView(b);

                                            set_onclick(b, i);


                                        }
                                        loadding_flag=true;
                                        chk_loadding();

                                    }
                                }
                            });
                        }
                    }).start();
                }

            }

    }
    void set_onclick(View b,int i){
        b.setOnClickListener(btn_listener);
    }
    private Button.OnClickListener btn_listener = new Button.OnClickListener() {
        public void onClick(View v) {

            int i= Integer.parseInt(v.findViewById(R.id.prod_kind_name).getTag().toString());
            Log.d("gotoi",String.valueOf(i));
            product_list_new newFragment = new product_list_new();
            Bundle args = new Bundle();
            args.putString("level1",menu_level1[i]);
            newFragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("kind").replace(R.id.container, newFragment, "kind").commit();

        }
    };
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
    private void chk_loadding() {
        FrameLayout Fralayout = (FrameLayout) getActivity().findViewById(R.id.kind_loadding);
        ProgressBar p=(ProgressBar)getActivity().findViewById(R.id.progressBar2);

        // LinearLayout LinearLayout = (android.widget.LinearLayout)getView().findViewById(R.id.Top_bar);
        // FrameLayout loadding=(FrameLayout)getView().findViewById(R.id.FF1) ;
        if (loadding_flag == true){//讀好了
            //  LinearLayout.setVisibility(View.VISIBLE);
            Fralayout.setVisibility(View.GONE);
            p.setVisibility(View.GONE);
            //   loadding.setVisibility(View.GONE);
        }else{
            Fralayout.setVisibility(View.VISIBLE);
            p.setVisibility(View.VISIBLE);

            //  loadding.setVisibility(View.VISIBLE);
            // LinearLayout.setVisibility(View.GONE);
        }
    }
    public void onDestroy() {
        super.onDestroy();
        Log.d("kindonDestroy", "kindonDestroy ");
        if(mThread!=null)
        mThread.getLooper();
        mThread.getLooper().quitSafely();
        // .quit();
    }
}
