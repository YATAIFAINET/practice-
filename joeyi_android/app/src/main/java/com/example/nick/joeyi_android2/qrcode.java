package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Vibrator;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class qrcode extends Activity
{


    //相机
    private Camera mCamera;
    //预览视图
    private CameraPreview mPreview;
    //自动聚焦
    private Handler mAutoFocusHandler;
    //图片扫描器
    private ImageScanner mScanner;
    //弹出窗口
    private PopupWindow mPopupWindow;
    //是否扫描完毕
    private boolean IsScanned = false;
    //是否处于预览状态
    private boolean IsPreview = true;
    //是否显示弹出层
    private boolean IsShowPopup=false;
    String ip = "", folder = "",config="",login_id;
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private Handler mUI_Handler = new Handler();
    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> item;
    private SimpleAdapter adapter;
    private ListView list_view;
    String url="";
    private String[] kind_data={"食","衣","住","行","育","樂"};
    private String[] kind_data_value={"1","2","3","4","5","6"};
    private ArrayAdapter<String> kind_dataAdapter;
    private Spinner kind;
    private int kind_select=0;
    private Button my_favo;
    private WebView qr_view;
    private Context context;
    private String prod_no="";
    String go_from="";
    //加载iconvlib
    static
    {
        System.loadLibrary("iconv");
    }
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //去除标题栏
        //  requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        setContentView(R.layout.activity_qrcode);

        //设置屏幕方向为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        load_config();
        config  = readFromFile("client_config");
        try {
            login_id = new JSONObject(config).getString("login_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        create_thread("get_qrcode_history","get_qrcode_history");
        list_view=(ListView)findViewById(R.id.histroy_list);
        my_favo=(Button)findViewById(R.id.my_favo);
        my_favo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(qrcode.this, qr_only_favo.class);
                startActivity(intent);
                finish();
            }

        });


        /*kind_dataAdapter = new ArrayAdapter<String>(this,R.layout.myspinner,kind_data);
        kind_dataAdapter.setDropDownViewResource(R.layout.myspinner);
        kind = (Spinner) findViewById(R.id.kind);
        kind.setAdapter(kind_dataAdapter);
        kind.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                //讀取第一個下拉選單是選擇第幾個
                int pos = kind.getSelectedItemPosition();
                kind_select = pos;
                get_favo_data("get_favo_data", "get_favo_data");


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
        /*
        qr_view=(WebView)findViewById(R.id.qr_view);
        String url2 = "https://chart.googleapis.com/chart?chs=" + "265x265" + "&cht=qr&chl=" + login_id + "&choe=UTF-8&chld=M|2";
        String html1="<div align=\"center\"><img style='height:90%' alt='Your QRcode' src='"+url2+"' /></div>";
        qr_view.requestFocus();
        qr_view.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        WebSettings setting = qr_view.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setSupportZoom(true);
        setting.setBuiltInZoomControls(true);
        setting.setAllowFileAccess(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setAppCachePath("/data/data/" + getPackageName() + "/app_path/");
        setting.setAppCacheEnabled(true);
        setting.setLoadsImagesAutomatically(true);
        setting.setSavePassword(true);
        setting.setLightTouchEnabled(true);
        setting.setPluginState(WebSettings.PluginState.ON);
        setting.setUserAgentString("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_4; zh-tw) AppleWebKit/533.16 (KHTML, like Gecko) Version/5.0 Safari/533.16");
        setting.setDomStorageEnabled(true);
        qr_view.setWebViewClient(new WebViewClient());



        Log.d("html1", html1);
        String htmldata=html1;//网页代码
        String targeturl="";//目标网址（具体）
        String baseurl="";//连接目标网址失败进入的默认网址
        qr_view.getSettings().setDefaultTextEncodingName("GB2312");
        qr_view.loadData(htmldata,"text/html","utf-8");
        qr_view.loadDataWithBaseURL(htmldata, htmldata, "text/html","utf-8", htmldata);
*/

        //自动聚焦线程
        mAutoFocusHandler = new Handler();
        //获取相机实例
        mCamera = getCameraInstance();
        if(mCamera == null)
        {
            //在这里写下获取相机失败的代码
            AlertDialog.Builder mBuilder=new AlertDialog.Builder(this);
            mBuilder.setTitle("ZBar4Android");
            mBuilder.setMessage("ZBar4Android获取相机失败，请重试！");
            mBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface mDialogInterface, int mIndex)
                {
                    qrcode.this.finish();
                }
            });
            AlertDialog mDialog=mBuilder.create();
            mDialog.show();
        }
        //实例化Scanner
        mScanner = new ImageScanner();
        mScanner.setConfig(0, 0, 3);
        mScanner.setConfig(0, 0, 3);
        //设置相机预览视图
        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
        if (IsScanned)
        {
            IsScanned = false;
            mCamera.setPreviewCallback(previewCb);
            mCamera.startPreview();
            IsPreview = true;
            mCamera.autoFocus(autoFocusCB);
        }

    }
    public void get_favo_data(final String cmd,String thread_name) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_favo_data_q(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {

                        get_favo_data_res(jsonString);
                    }

                });
            }
        });
    }
    private String get_favo_data_q(String query)
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
            nameValuePairs.add(new BasicNameValuePair("kind",kind_data_value[kind_select]));

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
    public final void get_favo_data_res(String input)
    {
        Log.d("input0106", input);
        list_view=(ListView)findViewById(R.id.favo_list);
        try
        {
            JSONArray jsonArray = new JSONArray(input);
            list = new ArrayList<HashMap<String,String>>();
            Log.d("input0106", String.valueOf(jsonArray.length()));
            if(jsonArray.length()>0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    item = new HashMap<String, String>();
                    item.put("url",jsonData.getString("url"));
                    item.put("tittle",jsonData.getString("tittle"));
                    list.add(item);
                }

                adapter = new MySimpleAdapter(getApplication(), list, R.layout.qr_code_favo_list,new String[]{ "url","tittle"},new int[]{R.id.url,R.id.tittle});
                list_view.setAdapter(adapter);
                list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView url_tv = (TextView) view.findViewById(R.id.url);
                        TextView title_tv = (TextView) view.findViewById(R.id.tittle);

                        String tittle=title_tv.getText().toString();
                        String tmpurl=url_tv.getText().toString();
                        // tmpurl=tmpurl.replace("http://","");
                        //tmpurl=tmpurl.replace("https://","");
                        //tmpurl="https://"+tmpurl;
                        Uri uri = Uri.parse(tmpurl);

                        Log.d("uri", String.valueOf(uri));
                          /*  Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);*/
                        //finish();
                        shareTo(tmpurl,tittle);




                    }
                });
                list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                    public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                                   int index, long arg3) {
                        TextView url_tv = (TextView) v.findViewById(R.id.url);
                        String tmpurl = url_tv.getText().toString();

                        Uri uri = Uri.parse(tmpurl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        finish();
                        return false;
                    }
                });

            }else{
                item = new HashMap<String, String>();
                item.put("url", "目前此類別無記錄");
                list.add(item);
                adapter = new MySimpleAdapter(getApplication(), list, R.layout.qrcode_list,new String[]{ "url"},new int[]{R.id.url});
                list_view.setAdapter(adapter);
            }
        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            //Toast.makeText(getApplication(), "" + e.toString(), Toast.LENGTH_LONG).show();
            Log.e("json", e.toString());
        }

    }
    private void shareTo(String url,String title) {

        String shareText = "分享網址:"; //
        shareText+=title;
        shareText+=url;
        //Uri imageUri = Uri.parse("android.resource://" + getPackageName() + "/drawable/" + "ic_launcher"); //分享圖片至gmail、twitter可，line、facebook不行
        //Log.i("imageUri:", imageUri + "");
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain"); //文字檔類型
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText); //傳送文字
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "分享"));
    }
    //实现Pause方法
    public void onPause()
    {
        super.onPause();
        close_camara();
    }
    //获取照相机的方法
    public static Camera getCameraInstance()
    {
        Camera mCamera = null;
        try
        {
            mCamera = Camera.open();
            //没有后置摄像头，尝试打开前置摄像头*******************
            if (mCamera == null)
            {
                Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
                int cameraCount = Camera.getNumberOfCameras();
                for (int camIdx = 0; camIdx < cameraCount; camIdx++)
                {
                    Camera.getCameraInfo(camIdx, mCameraInfo);
                    if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                    {
                        mCamera = Camera.open(camIdx);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return mCamera;
    }

    //释放照相机
    private void releaseCamera()
    {
        if (mCamera != null)
        {
            IsPreview = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable()
    {
        public void run()
        {
            if (IsPreview)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback()
    {
        public void onPreviewFrame(byte[] data, Camera camera)
        {
            Camera.Parameters parameters = camera.getParameters();
            //获取扫描图片的大小
            Camera.Size mSize = parameters.getPreviewSize();
            //构造存储图片的Image
            Image mResult = new Image(mSize.width, mSize.height, "Y800");//第三个参数不知道是干嘛的
            //设置Image的数据资源
            mResult.setData(data);
            //获取扫描结果的代码
            int mResultCode = mScanner.scanImage(mResult);
            //如果代码不为0，表示扫描成功
            if (mResultCode != 0)
            {
                //停止扫描
                IsPreview = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                //开始解析扫描图片
                SymbolSet Syms = mScanner.getResults();
                for (Symbol mSym : Syms)
                {

                    //mSym.getType()方法可以获取扫描的类型，ZBar支持多种扫描类型,这里实现了条形码、二维码、ISBN码的识别
                    if (mSym.getType() == Symbol.CODE128 || mSym.getType() == Symbol.QRCODE ||
                            mSym.getType() == Symbol.CODABAR ||	mSym.getType() == Symbol.ISBN10 ||
                            mSym.getType() == Symbol.ISBN13|| mSym.getType()==Symbol.DATABAR ||
                            mSym.getType()==Symbol.DATABAR_EXP || mSym.getType()==Symbol.I25)

                    {
                        //添加震动效果，提示用户扫描完成
                        Vibrator mVibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);
                        mVibrator.vibrate(400);

                        url=mSym.getData();
                        if(mSym.getData().startsWith("http")==true){
                            insert_url("insert_url","insert_url");

                        }else{

                            if(mSym.getData().startsWith("qr_code_product")==true){//加入購物車 導向購物車畫面

                                String[] temp = url.split("@@");
                                if(temp[0].equals("qr_code_product")){
                                    if(!temp[1].equals("")){
                                        prod_no=temp[1];
                                        get_qr_product("get_qr_product","get_qr_product");
                                    }

                                }



                            }else{
                                Intent intent=new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString("url",url);
                                intent.putExtras(bundle);
                                intent.setClass(qrcode.this, qrcode_res.class);
                                finish();
                                startActivity(intent);
                            }

                        }


                        //这里需要注意的是，getData方法才是最终返回识别结果的方法
                        //但是这个方法是返回一个标识型的字符串，换言之，返回的值中包含每个字符串的含义
                        //例如N代表姓名，URL代表一个Web地址等等，其它的暂时不清楚，如果可以对这个进行一个较好的分割
                        //效果会更好，如果需要返回扫描的图片，可以对Image做一个合适的处理

                        IsScanned = true;
                    }
                    else
                    {
                        //否则继续扫描
                        IsScanned = false;
                        mCamera.setPreviewCallback(previewCb);
                        mCamera.startPreview();
                        IsPreview = true;
                        mCamera.autoFocus(autoFocusCB);
                    }
                }
            }
        }
    };

    //用于刷新自动聚焦的方法
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback()
    {
        public void onAutoFocus(boolean success, Camera camera)
        {
            mAutoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    //根据返回的代码值来返回相应的格式化数据
    public String GetResultByCode(int CodeType)
    {
        String mResult="";
        switch(CodeType)
        {
            //条形码
            case Symbol.CODABAR:
                mResult="条形码";
                break;
            //128编码格式二维码)
            case Symbol.CODE128:
                mResult="二维码";
                break;
            //QR码二维码
            case Symbol.QRCODE:
                mResult="二维码";
                break;
            //ISBN10图书查询
            case Symbol.ISBN10:
                mResult="图书ISBN号";
                break;
            //ISBN13图书查询
            case Symbol.ISBN13:
                mResult="图书ISBN号";
                break;
        }
        return mResult;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;

        }
        //Toast.makeText(context, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
    public void load_config() {
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open("config"));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null) {
                Result += line;

            }

            ip = new JSONObject(Result).getString("ip");
            folder = new JSONObject(Result).getString("folder");
            //Toast.makeText(getApplicationContext(),ip, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void insert_url(final String cmd,String thread_name) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = insert_url_q(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {

                        insert_url_res(jsonString);
                    }

                });
            }
        });
    }
    private String insert_url_q(String query)
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
            nameValuePairs.add(new BasicNameValuePair("url",url));
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
    public final void insert_url_res(String input)
    {
        Log.d("test0105",input);
        //Toast.makeText(getApplicationContext(),tab_status, Toast.LENGTH_SHORT).show();
        //tab_status="cus";
        Intent intent=new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("url",url);
        intent.putExtras(bundle);
        intent.setClass(qrcode.this, qrcode_res.class);
        finish();
        startActivity(intent);



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

                        newlist(jsonString);
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
        Log.d("test1231",input);
        //Toast.makeText(getApplicationContext(),tab_status, Toast.LENGTH_SHORT).show();
        //tab_status="cus";
        try
        {
            JSONArray jsonArray = new JSONArray(input);
            list = new ArrayList<HashMap<String,String>>();
            if(jsonArray.length()>0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    item = new HashMap<String, String>();
                    item.put("url",jsonData.getString("url"));

                    list.add(item);
                }
                Log.d("product2", String.valueOf(list));
                adapter = new MySimpleAdapter(getApplication(), list, R.layout.qrcode_list,new String[]{ "url"},new int[]{R.id.url});
                list_view.setAdapter(adapter);
                list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    boolean a=true;
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView url_tv = (TextView) view.findViewById(R.id.url);

                        if(a==true){
                         /*   Intent intent=new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString("url",url_tv.getText().toString());
                            intent.putExtras(bundle);
                            intent.setClass(qrcode.this,web_view.class);
                            startActivity(intent);
                            finish();*/
                            String tmpurl=url_tv.getText().toString();
                            //tmpurl=tmpurl.replace("http://","");
                            //tmpurl=tmpurl.replace("https://","");
                            //tmpurl="https://"+tmpurl;
                            Uri uri = Uri.parse(tmpurl);

                            Log.d("uri", String.valueOf(uri));
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                            finish();

                        }


                    }
                });

            }else{
                item = new HashMap<String, String>();
                item.put("url", "目前無掃描記錄");
                list.add(item);
                adapter = new MySimpleAdapter(getApplication(), list, R.layout.qrcode_list,new String[]{ "url"},new int[]{R.id.url});
                list_view.setAdapter(adapter);
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
    public void get_qr_product(final String cmd,String thread_name) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_qr_product_query(cmd);
                mUI_Handler.post(new Runnable() {
                    public void run() {

                        get_qr_product_res(jsonString);
                    }

                });
            }
        });
    }
    private String get_qr_product_query(String query)
    {
        String result = "";
        try
        {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("prod_no",prod_no));
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
    public final void get_qr_product_res(String input)
    {
        Log.d("leo0525leo",input);
        String cart_prod_no="";
        String cart_prod_name="";
        String cart_prod_price="";
        String cart_prod_pv="";
        try
        {
            JSONArray jsonArray = new JSONArray(input);
            list = new ArrayList<HashMap<String,String>>();
            if(jsonArray.length()>0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);

                    cart_prod_no=jsonData.getString("prod_no");
                    cart_prod_name=jsonData.getString("prod_name");
                    cart_prod_price=jsonData.getString("comp_price");
                    cart_prod_pv=jsonData.getString("pv");

                }
                context=this;
                Global_cart globalVariable = (Global_cart) context.getApplicationContext();
                globalVariable.cart.clear();
                Enumeration e = globalVariable.cart.keys();
                String price=cart_prod_price;
                String pv=cart_prod_pv;
                globalVariable.cart.put(cart_prod_no,"1"+","+price+","+pv);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("from", "qr_shop");
                bundle.putString("total", cart_prod_price);
                bundle.putString("prod_name", cart_prod_name);
                intent.putExtras(bundle);
                intent.setClass(context, qr_prod_step2.class);
                startActivity(intent);

            }else{
                Toast.makeText(getApplication(), "" + "查無此商品", Toast.LENGTH_LONG).show();
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
    void open_camara(){
        IsScanned = false;
        IsPreview = true;
        //自动聚焦线程
        mAutoFocusHandler = new Handler();
        //获取相机实例
        mCamera = getCameraInstance();
        if(mCamera == null) {
            //在这里写下获取相机失败的代码
            AlertDialog.Builder mBuilder=new AlertDialog.Builder(this);
            mBuilder.setTitle("ZBar4Android");
            mBuilder.setMessage("ZBar4Android获取相机失败，请重试！");
            mBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface mDialogInterface, int mIndex)
                {
                    qrcode.this.finish();
                }
            });
            AlertDialog mDialog=mBuilder.create();
            mDialog.show();
        }
        //实例化Scanner
        mScanner = new ImageScanner();
        mScanner.setConfig(0, 0, 3);
        mScanner.setConfig(0, 0, 3);
        //设置相机预览视图
        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
        if (IsScanned)
        {
            IsScanned = false;
            mCamera.setPreviewCallback(previewCb);
            mCamera.startPreview();
            IsPreview = true;
            mCamera.autoFocus(autoFocusCB);
        }

    }
    void close_camara(){

        releaseCamera();
        mPreview=null;
        mScanner=null;
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.removeAllViews();
    }

    public void onStart()
    {
        super.onStart();
        close_camara();
        open_camara();
    }

}