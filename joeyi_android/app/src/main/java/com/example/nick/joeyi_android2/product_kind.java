package com.example.nick.joeyi_android2;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class product_kind extends ListFragment {


    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> item;
    private String []menu_color;
    private int []menu_bk={R.drawable.cart_001,R.drawable.cart_002,R.drawable.cart_003,R.drawable.cart_004,R.drawable.cart_005,R.drawable.cart_006,R.drawable.cart_001};
    private String []menu_bk2;
    String config="";

    String ip="",folder="";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        load_config();
        getActivity().getActionBar().setTitle("商品類別");
        create_thread("get_product_kind", "get_product_kind");

    }

    public void onStart(){
        super.onStart();
        getActivity().getActionBar().setTitle("商品類別");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("prod_list_old", "onCreateView");

        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(Color.parseColor("#d5e4a8"));
        getActivity().getActionBar().setBackgroundDrawable(colorDrawable);
        getActivity().getActionBar().setTitle("商品類別");

        return inflater.inflate(R.layout.product_kind, container, false);
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
            JSONArray jsonArray = new JSONArray(input);

            list = new ArrayList<HashMap<String,String>>();
            menu_bk2=new String[jsonArray.length()];
            menu_color=new String[jsonArray.length()];
            if(jsonArray.length()>0) {
                for (int i = 0; i < jsonArray.length()-1; i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    item = new HashMap<String, String>();
                    String kind_name=jsonData.getString("kind_name");
                    kind_name=kind_name.replace(""," ");
                    item.put("level1", jsonData.getString("level1"));
                    item.put("kind_name", "" + kind_name);
                    menu_bk2[i]=jsonData.getString("app_img");
                    menu_color[i]=jsonData.getString("app_color");
                    list.add(item);
                }
                adapter = new MySimpleAdapter(getActivity(), list, R.layout.product_kind_list,new String[]{ "kind_name"},new int[]{R.id.prod_kind_name});
                setListAdapter(adapter);




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

    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        Bundle b = new Bundle();
        b.putString("level1", list.get(position).get("level1"));




        product_list_new newFragment = new product_list_new();

        Bundle args = new Bundle();
        args.putString("level1", list.get(position).get("level1"));
        newFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("kind").replace(R.id.container, newFragment, "kind").commit();

        if(list.get(position).get("level1")!=null) {
            //Toast.makeText(getActivity(), "你按下的" + list.get(position).get("level1"), Toast.LENGTH_SHORT).show();
        }
    }
    private class MySimpleAdapter extends SimpleAdapter{
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
            loadingIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.coming_soon_b);
            mGridView = mInflater.inflate(R.layout.product_kind_list, null);

            asyncImageFileLoader = new AsyncImageFileLoader();
            // TODO Auto-generated constructor stub
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub


            Log.d("position", String.valueOf(position));
            ViewHolder mHolder;
            final View v= super.getView(position, convertView, parent);

          //  v.findViewById(R.id.prod_kind_linear).setBackgroundResource(menu_bk[position]);
            v.findViewById(R.id.prod_kind_linear2).setBackgroundColor(Color.parseColor(menu_color[position]));
            v.findViewById(R.id.prod_kind_linear2).getBackground().setAlpha(179);



            Paths = "http://"+ip+"/ct/shoppingcart/prod_kind_images/"+menu_bk2[position];
            Log.d("now_path2222",Paths);

                convertView = v;
                mHolder = new ViewHolder();
                mHolder.icon = (LinearLayout) convertView.findViewById(R.id.prod_kind_linear);
                convertView.setTag(mHolder);


            //設定此mHolder.icon的tag為檔名，讓之後的callback function可以針對此mHolder.icon替換圖片
            LinearLayout imageView = mHolder.icon;
            imageView.setTag(Paths);

            Bitmap cachedBitmap = asyncImageFileLoader.loadBitmap(Paths, 500, 200, new AsyncImageFileLoader.ImageCallback() {
                @Override
                public void imageCallback(Bitmap imageBitmap, String imageFile) {
                    // 利用檔案名稱找尋當前mHolder.icon
                    LinearLayout imageViewByTag = (LinearLayout) v.findViewWithTag(imageFile);
                    if (imageViewByTag != null) {
                        if (imageBitmap != null){
                            Drawable dr = new BitmapDrawable(imageBitmap);
                            imageViewByTag.setBackgroundDrawable(dr);

                        }

                    }
                }
            });

            if(cachedBitmap != null){

                Drawable dr = new BitmapDrawable(cachedBitmap);
                mHolder.icon.setBackgroundDrawable(dr);





            }else{
                Drawable dr = new BitmapDrawable(loadingIcon);
               // mHolder.icon.setBackgroundDrawable(dr); //顯示預設的圖片

            }

            return convertView;
            }
            private class ViewHolder
            {
                LinearLayout icon;
            }




    }
    public void onPause() {
        super.onPause();
        Log.d("onPause", "onPause ");
        if(mThread!=null){
        mThread.getLooper();
        mThread.getLooper().quitSafely();
        }
        // .quit();
    }

}