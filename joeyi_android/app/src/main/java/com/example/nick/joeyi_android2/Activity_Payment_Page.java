package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Activity_Payment_Page extends Activity {
    WebView mWebView_Payment;
    String aaa; //on_dro;
    String bbb; //yenom_latot;
    String ccc; //mb_no
    String ddd; //tel3
    String eee; //name
    String fff; //email
    String ggg; //pay_Way
    String ip="",folder="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);
        setTitle("付款頁面");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayShowHomeEnabled(false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        }
        load_config();
        Bundle bundle =this.getIntent().getExtras();
        aaa = bundle.getString("ord_no");
        bbb = bundle.getString("total_money");
        ccc = bundle.getString("mb_no");
        ddd = bundle.getString("tel3");
        eee = bundle.getString("name");
        fff = bundle.getString("email");
        ggg = bundle.getString("pay_way");

        mWebView_Payment = (WebView) findViewById(R.id.WebView_Payment);
        WebSettings settings = mWebView_Payment.getSettings();
        settings.setJavaScriptEnabled(true);
        //settings.setDomStorageEnabled(true);
        mWebView_Payment.setWebChromeClient(new WebChromeClient());
        //mWebView_Payment.setWebViewClient(new SSLTolerentWebViewClient());
        mWebView_Payment.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        mWebView_Payment.addJavascriptInterface(new IJavascriptHandler(), "android");
        mWebView_Payment.loadUrl("http://" + ip + "/ct/shoppingcart/product/epayment/payNow_for_app.php?all_paid=" + bbb + "&ord_no=" + aaa + "&mb_no=" + ccc + "&tel3=" + ddd + "&mb_name=" + eee + "&email=" + fff + "&pay_way=" + ggg);

        Log.e("1231231231","123123132132");
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
    final class IJavascriptHandler {
        IJavascriptHandler() {}
        @JavascriptInterface
        public void LeoText(String text) {
            Log.e("sheng**", "showwww");
            if(text.trim().equals("0")){
                Toast.makeText(getApplicationContext(),"刷卡失敗，請與喬易國際聯繫",Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), shopping_res.class);
                Bundle bundle = new Bundle();
                bundle.putString("res","success,"+aaa);
                intent.putExtras(bundle);
                finish();
                startActivity(intent);
            }else if(text.trim().equals("1")){
                Toast.makeText(getApplicationContext(),"刷卡成功，請確認訂單",Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), shopping_res.class);
                Bundle bundle = new Bundle();
                bundle.putString("res","success,"+aaa);
                intent.putExtras(bundle);
                finish();
                startActivity(intent);
            }
            //Toast.makeText(getApplicationContext(),"show",Toast.LENGTH_SHORT).show();
        }
    }

    private class SSLTolerentWebViewClient extends WebViewClient {
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {

            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Payment_Page.this);
            AlertDialog alertDialog = builder.create();
            String message = "SSL Certificate error.";
            switch (error.getPrimaryError()) {
                case SslError.SSL_UNTRUSTED:
                    message = "The certificate authority is not trusted.";
                    break;
                case SslError.SSL_EXPIRED:
                    message = "The certificate has expired.";
                    break;
                case SslError.SSL_IDMISMATCH:
                    message = "The certificate Hostname mismatch.";
                    break;
                case SslError.SSL_NOTYETVALID:
                    message = "The certificate is not yet valid.";
                    break;
            }

            message += " Do you want to continue anyway?";
            alertDialog.setTitle("SSL Certificate Error");
            alertDialog.setMessage(message);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Ignore SSL certificate errors
                    handler.proceed();
                }
            });

            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    handler.cancel();
                }
            });
            alertDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_normal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Toast.makeText(getApplication(), "訂單已成立", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), shopping_res.class);
        Bundle bundle = new Bundle();
        bundle.putString("res","success,"+aaa);
        intent.putExtras(bundle);
        finish();
        startActivity(intent);
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Toast.makeText(getApplication(), "訂單已成立", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), shopping_res.class);
            Bundle bundle = new Bundle();
            bundle.putString("res","success,"+aaa);
            intent.putExtras(bundle);
            finish();
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
