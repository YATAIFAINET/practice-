package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class join_success extends Activity {
    private TextView success;
    private String str,mb_no;
    private Button index;
    private String from="";
    Handler handler=new Handler();
    Runnable runnable;
    protected void onStart(){
        super.onStart();
        net_check();

    }
    protected void onStop(){
        super.onStop();
        handler.removeCallbacks(runnable);
    }
    void net_check() {


        runnable=new Runnable(){
            @Override
            public void run() {

                // TODO Auto-generated method stub
                ConnectivityManager CM = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = CM.getActiveNetworkInfo();
                if (info == null || !info.isAvailable()) { //判斷是否有網路
                    handler.postDelayed(this, 2000000);
                    new AlertDialog.Builder(join_success.this)
                            .setTitle("錯誤!!")
                            .setMessage("網路連線異常!!")
                            .setCancelable(false)
                            .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    handler.removeCallbacks(runnable);
                                    finish();

                                }
                            }).show();

                }else{
                    handler.postDelayed(this, 2000);

                }

            }
        };
        handler.postDelayed(runnable, 500);//每兩秒執行一次runnable.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        setContentView(R.layout.activity_join_success);
        Bundle bundle =getIntent().getExtras();
        mb_no = bundle.getString("mb_no");
        from = bundle.getString("from");
        str="親愛的使用者您好，您已線上推薦申請成功。<br />";
        str+="請將申請紙本於申請後14天內寄至公司，經審核通過後即可成為正式會員，謝謝您。";
        str+="請記住您的會員編號(登入帳號)：";
        str+=mb_no;
        //str+="預設密碼為身份證字號或公司統編後四碼。<br />";
        //str+="請由會員專區登入後自行修改，謝謝您。";
        //str="親愛的使用者您好，您已正式加入為本公司會員。<br />";
        //str+="請記住您的會員編號(登入帳號)：";
        //str+=mb_no;
        str+="預設密碼為身份證字號或公司統編後四碼。<br />";
        str+="請由會員專區登入後自行修改，謝謝您。";
        success=(TextView)findViewById(R.id.success);
        success.setText(Html.fromHtml(str));
        index=(Button)findViewById(R.id.index);
        index.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(from.equals("login")){
                    Intent intent = new Intent(join_success.this, login.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(join_success.this, test_page.class);
                    Bundle bundle2 = new Bundle();

                    intent.putExtras(bundle2);
                    startActivity(intent);
                    finish();
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_normal, menu);
        return true;
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
