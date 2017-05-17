package com.example.nick.joeyi_android2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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

public class product_list_old extends ListFragment {

    private String[] WEEK_NO;
    public String kind,query_name;
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Spinner spinner;
    private ArrayAdapter lunchList;
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> item;
    private Button btn_my_shopping,btn_product_kind,btn_product_ser;
    String config="",login_no;



    private DefaultHttpClient httpClient;
    private HttpPost httpPost;
    private HttpEntity httpEntity;
    private HttpResponse httpResponse;
    public static String PHPSESSID = null;
    String ip="",folder="";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle!=null && bundle.containsKey("level1")) {
            kind = bundle.getString("level1");
            query_name="-1";
        }else if(bundle!=null && bundle.containsKey("query")){
            kind="-1";
            query_name = bundle.getString("query");
        }else{
            kind="-1";
            query_name="-1";
        }
        load_config();
        create_thread("get_product", "get_product");
        // btn_my_shopping = (Button) findViewById(R.id.btn_my_shopping);
        //  btn_product_kind = (Button) findViewById(R.id.btn_product_kind);
        // btn_product_ser = (Button) findViewById(R.id.btn_product_ser);

        //btn_my_shopping.setOnClickListener(btn_my_shopping_listener);
        // btn_product_kind.setOnClickListener(btn_product_kind_listener);
        // btn_product_ser.setOnClickListener(btn_product_ser_listener);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("prod_list_old", "onCreateView");
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(Color.parseColor("#dfcae7"));
        getActivity().getActionBar().setBackgroundDrawable(colorDrawable);
        return inflater.inflate(R.layout.shopping_layout_old, container, false);
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
    private Button.OnClickListener btn_my_shopping_listener = new Button.OnClickListener() {
        public void onClick(View v) {

            Intent intent = new Intent();
            intent.setClass(getActivity(), pay_list.class);
            startActivity(intent);

        }
    };
    private Button.OnClickListener btn_product_kind_listener = new Button.OnClickListener() {
        public void onClick(View v) {

            Intent intent = new Intent();
            intent.setClass(getActivity(), product_list.class);
            startActivity(intent);
        }
    };
    private Button.OnClickListener btn_product_ser_listener = new Button.OnClickListener() {
        public void onClick(View v) {

        }
    };

    public void create_thread(final String cmd,String thread_name) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        if (cmd == "get_product") {
                            newProduct(jsonString);
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
            nameValuePairs.add(new BasicNameValuePair("level1",kind));
            nameValuePairs.add(new BasicNameValuePair("query",query_name));
            Log.d("query_namequery_name",query_name);
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
    public final void newProduct(String input)
    {
        Log.d("newProductnewProduct",input);
        try
        {
            JSONArray jsonArray = new JSONArray(input);
            list = new ArrayList<HashMap<String,String>>();
            if(jsonArray.length()>0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    item = new HashMap<String, String>();
                    item.put("prod_no",  jsonData.getString("prod_no"));
                    item.put("prod_name", "" + jsonData.getString("prod_name"));
                    item.put( "comp_price",""+jsonData.getString("comp_price"));
                    item.put("pv", "" + jsonData.getString("pv"));
                    item.put("standard", "" + jsonData.getString("standard"));
                    item.put("pic_name", "" + jsonData.getString("name"));
                    item.put("prod_summary", "" + jsonData.getString("prod_summary"));



                    list.add(item);
                    Log.d("product", jsonData.getString("prod_no"));
                }
                Log.d("product2", String.valueOf(list));
                if(getActivity()==null){
                    return;
                }
                adapter = new MySimpleAdapter(getActivity(), list, R.layout.product_list,new String[]{ "prod_no", "prod_name", "comp_price", "pv", "pic_name","prod_summary"},new int[]{R.id.txv_product_id, R.id.txv_product_name, R.id.txv_product_money, R.id.txv_product_pv, R.id.img_name,R.id.prod_summary});
                setListAdapter(adapter);

            }else{
                item = new HashMap<String, String>();
                item.put("prod_no", "目前尚無產品");
                list.add(item);
                adapter = new MySimpleAdapter(getActivity(), list, R.layout.product_list,new String[]{ "prod_no"},new int[]{R.id.txv_product_name});
                setListAdapter(adapter);
            }

        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Log.e("json", e.toString());

        }
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        if(list.get(position).get("prod_no")!=null && list.get(position).get("prod_no")!="目前尚無產品") {
            //Toast.makeText(getActivity(), "你按下的" +list.get(position).get("mb_name"), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("prod_no", list.get(position).get("prod_no"));
            intent.putExtras(bundle);
            intent.setClass(getActivity(), product_detail_list.class);
            startActivity(intent);
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
            loadingIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.comingsoon);
            mGridView = mInflater.inflate(R.layout.shopping_layout_old, null);

            asyncImageFileLoader = new AsyncImageFileLoader();
            // TODO Auto-generated constructor stub
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Log.d("getview", String.valueOf(position));
            // TODO Auto-generated method stub
            final View v = super.getView(position, convertView, parent);

            ViewHolder mHolder;

            TextView img_name = (TextView) v.findViewById(R.id.img_name);
            Paths = "http://" + ip + "/ct/shoppingcart/prod_images/180180/" + img_name.getText().toString();

            if (convertView == null) {
                convertView = v;
                mHolder = new ViewHolder();
                mHolder.icon = (ImageView) convertView.findViewById(R.id.imageView2);
                convertView.setTag(mHolder);
            } else {
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
                mHolder.icon.setImageBitmap(loadingIcon); //顯示預設的圖片
            }

            return convertView;

        }
        private class ViewHolder
        {
            ImageView icon;
        }

    }

}