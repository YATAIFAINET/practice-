package com.example.nick.joeyi_android2;

import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by Nick on 2015/11/12.
 */
public class fra3_main extends FragmentActivity {
    private SearchView mSearchView;
    private MenuItem searchItem;
    private int total_w,total_h;
    String go_tab;
    final int[] ICONS = new int[] {
            R.drawable.icon_home,
            R.drawable.icon_type,
            R.drawable.icon_cart,
            R.drawable.icon_account,
            R.drawable.icon_menu,
            R.drawable.icon_home2,
            R.drawable.icon_type2,
            R.drawable.icon_cart2,
            R.drawable.icon_account2,
            R.drawable.icon_menu2
    };
    private int last_tab;
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    String config="",mb_no,mb_country;
    private String ip="",folder="";
    private String from ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fra3_main);
        load_config();
        config  = readFromFile("client_config");
        try {
            mb_country = new JSONObject(config).getString("mb_country");
            mb_no = new JSONObject(config).getString("login_id");
            Log.e("fra_mb_no",mb_no.substring(0,2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Bundle bData = this.getIntent().getExtras();

        if(bData!=null) {
            go_tab = bData.getString("go_tab");
        }






        //隱藏logo
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#F29500"));
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        //getActionBar().setBackgroundDrawable(colorDrawable);


        //獲取TabHost控制元件
        final FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        //設定Tab頁面的顯示區域，帶入Context、FragmentManager、Container ID
        mTabHost.setup(this, getSupportFragmentManager(), R.id.container);
        final TabWidget tabWidget = mTabHost.getTabWidget();
        tabWidget.setBackground(colorDrawable);
        /**
         新增Tab結構說明 :
         首先帶入Tab分頁標籤的Tag資訊並可設定Tab標籤上顯示的文字與圖片，
         再來帶入Tab頁面要顯示連結的Fragment Class，最後可帶入Bundle資訊。
         **/

            mTabHost.addTab(mTabHost.newTabSpec("one")
                    .setIndicator(composeLayout("購物首頁", ICONS[0], 0))
                    , shopping_index.class, null);

            mTabHost.addTab(mTabHost.newTabSpec("two")
                    .setIndicator(composeLayout("商品類別", ICONS[1], 1))
                    , product_kind2.class, null);


            Bundle bundle = new Bundle();
            bundle.putString("form", "fragment");
            mTabHost.addTab(mTabHost.newTabSpec("three")
                    .setIndicator(composeLayout("購物車", ICONS[2], 2))
                    , fra_to_paylist.class, bundle);


            mTabHost.addTab(mTabHost.newTabSpec("four")
                    .setIndicator(composeLayout("我的帳戶", ICONS[3], 3))
                    , my_account.class, null);

            mTabHost.addTab(mTabHost.newTabSpec("five")
                    .setIndicator(composeLayout("回主選單", ICONS[4], 4))
                    , product_list_old.class, null);

        /*View first_view = tabWidget.getChildAt(0);
        first_view.setBackgroundColor(0xFFEE4000);*/
            for (int i = 0; i < tabWidget.getChildCount(); i++) {
                View vvv = tabWidget.getChildAt(i);
                if (mTabHost.getCurrentTab() == i) {
                    ImageView img=(ImageView)vvv.findViewById(i);
                    img.setImageResource(ICONS[i + 5]);
                    TextView txv=(TextView)vvv.findViewById(i+5);
                    txv.setTextColor(Color.BLACK);
                } else {
                    ImageView img=(ImageView)vvv.findViewById(i);
                    img.setImageResource(ICONS[i]);
                    TextView txv=(TextView)vvv.findViewById(i+5);
                    txv.setTextColor(Color.WHITE);
                }
            }

            if (bData!=null && bData.containsKey("go_tab")) {
                go_tab = bData.getString("go_tab");

                mTabHost.setCurrentTab(Integer.parseInt(go_tab));
                last_tab= Integer.parseInt(go_tab);

                for (int i = 0; i < tabWidget.getChildCount(); i++) {
                    View vvv = tabWidget.getChildAt(i);
                    if (mTabHost.getCurrentTab() == i) {
                        //vvv.setBackgroundColor(0xFF99ccff);
                        ImageView img=(ImageView)vvv.findViewById(i);
                        img.setImageResource(ICONS[i + 5]);
                        TextView txv=(TextView)vvv.findViewById(i+5);
                        txv.setTextColor(Color.BLACK);
                    } else {
                        //vvv.setBackgroundColor(0xFFE3E3E3);
                        ImageView img=(ImageView)vvv.findViewById(i);
                        img.setImageResource(ICONS[i]);
                        TextView txv=(TextView)vvv.findViewById(i+5);
                        txv.setTextColor(Color.WHITE);
                    }
                }
            }
            for(int i=0;i<5;i++){
                final int finalI = i;
                mTabHost.getTabWidget().getChildAt(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d("last_tab", String.valueOf(last_tab)+","+String.valueOf(finalI));
                        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
                        Log.d("backStackCount", String.valueOf(backStackCount));
                        if(last_tab==finalI){
                            if(finalI==0){
                                 shopping_index newFragment = new shopping_index();
                                 Bundle args = new Bundle();
                                 newFragment.setArguments(args);
                                 getSupportFragmentManager().beginTransaction().replace(R.id.container, newFragment).commit();
                            }
                            if(finalI==1){
                                product_kind2 newFragment = new product_kind2();
                                Bundle args = new Bundle();
                                newFragment.setArguments(args);
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, newFragment).commit();

                            }
                            if(finalI==2){
                                //    fra_to_paylist newFragment = new fra_to_paylist();
                                //    Bundle args = new Bundle();
                                //    newFragment.setArguments(args);
                                //    getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.container, newFragment).commit();
                            }
                            if(finalI==3){
                                my_account newFragment = new my_account();
                                Bundle args = new Bundle();
                                newFragment.setArguments(args);
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, newFragment).commit();
                                searchItem.setVisible(false);
                            }
                            if(finalI==4){
                                //  product_list_old newFragment = new product_list_old();
                                //  Bundle args = new Bundle();
                                //  newFragment.setArguments(args);
                                //  getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.container, newFragment).commit();
                            }
                            backStackCount = getSupportFragmentManager().getBackStackEntryCount();
                            //if(mTabHost.getCurrentTab()==1){

                        }else{
                            if(finalI==4){
                                back_menu();
                            }else{
                                if(finalI==3||finalI==2){
                                    searchItem.setVisible(false);
                                }else{
                                    searchItem.setVisible(true);
                                }
                                mTabHost.setCurrentTab(finalI);

                            }
                        }

                        last_tab=finalI;

                    }
                });
            }
            if(!mb_no.substring(0,2).equals("TW")&&!mb_no.substring(0,2).equals("CN")
              &&!mb_no.substring(0,2).equals("KR")&&!mb_no.substring(0,2).equals("JP")
              &&!mb_no.substring(0,2).equals("HK")){
                mTabHost.getTabWidget().getChildAt(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(fra3_main.this,"帳號國籍不支援",Toast.LENGTH_LONG).show();
                    }
                });
                mTabHost.getTabWidget().getChildAt(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(fra3_main.this,"帳號國籍不支援",Toast.LENGTH_LONG).show();
                    }
                });
                mTabHost.getTabWidget().getChildAt(2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(fra3_main.this,"帳號國籍不支援",Toast.LENGTH_LONG).show();
                    }
                });
            }


            mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                @Override
                public void onTabChanged(String tabId) {


                    if (tabId == "two") {
                        product_kind2 newFragment = new product_kind2();
                        Bundle args = new Bundle();
                        newFragment.setArguments(args);
                        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.container, newFragment).commit();

                    }
                    // TODO Auto-generated method stub
                    for (int i = 0; i < tabWidget.getChildCount(); i++) {
                        View vvv = tabWidget.getChildAt(i);
                        if (mTabHost.getCurrentTab() == i) {
                            //vvv.setBackgroundColor(0xFF99ccff);
                            ImageView img = (ImageView) vvv.findViewById(i);
                            img.setImageResource(ICONS[i + 5]);
                            TextView txv = (TextView) vvv.findViewById(i + 5);
                            txv.setTextColor(Color.BLACK);
                        } else {
                            //vvv.setBackgroundColor(0xFFE3E3E3);
                            ImageView img = (ImageView) vvv.findViewById(i);
                            img.setImageResource(ICONS[i]);
                            TextView txv = (TextView) vvv.findViewById(i + 5);
                            txv.setTextColor(Color.WHITE);
                        }
                    }
                    int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
                    //if(mTabHost.getCurrentTab()==1){
                    for (int i = 0; i < backStackCount; i++) {
                        getSupportFragmentManager().popBackStack();
                    }

//                }
                    //product_kind f2 = product_kind.newInstance(b);
                    //getSupportFragmentManager().beginTransaction().show(f2).commit();

                }
            });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Itemid", String.valueOf(item.getItemId()));
        switch(item.getItemId()) {
            case android.R.id.home:
                Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("kind");
                if(currentFragment!=null){
                    if(getTitle().toString().equals("商品類別")){
                        Intent i = new Intent(this,test_page.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    }else{
                        actionKey(KeyEvent.KEYCODE_BACK);
                        Log.d("fromfrom", "1");
                    }
                    // getSupportFragmentManager().popBackStack();
                }else{
                    Intent i = new Intent(this,test_page.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }



                return true;
            case R.id.search:
                //獲取TabHost控制元件
                FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

                Log.d("aaaa", String.valueOf(mTabHost.getCurrentTab()));

                Intent i = new Intent(this,search.class);
                Bundle b= new Bundle();
                b.putString("from", String.valueOf(mTabHost.getCurrentTab()));
                i.putExtras(b);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);

            default:
                break;

        }
        //Toast.makeText(context, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
    /**
     * 方法權限設定為Public目的是可以讓Fragment取得內容 。
     */

    public View composeLayout(String s, int i,int a) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        total_w=dm.widthPixels;
        total_h=dm.heightPixels;
// 定义一个LinearLayout布局
        LinearLayout layout = new LinearLayout(this);
// 设置布局垂直显示
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        ImageView iv = new ImageView(this);
        iv.setId(a);
        //imageList.add(iv);
        iv.setImageResource(i);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                50*total_w/720,
                50*total_h/1280);
        layout.addView(iv, lp);
// 定义TextView
        TextView tv = new TextView(this);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setSingleLine(true);
        tv.setText(s);
        tv.setTextColor(Color.WHITE);
        tv.setId(a + 5);
        tv.setTextSize(10);
        layout.addView(tv, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        return layout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        searchItem = menu.findItem(R.id.search);
        if(last_tab==2||last_tab==3){
            searchItem.setVisible(false);
        }

        if(mSearchView == null) {
            return true;
        }
        int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) mSearchView.findViewById(id);
        textView.setTextColor(Color.BLACK);

        textView.setHint("請輸入產品名稱或編號");
        textView.setHintTextColor(Color.parseColor("#CCCCCC"));

       /* mSearchView.setIconifiedByDefault(true);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.onActionViewExpanded();
        mSearchView.setBackgroundColor(0x22ff00ff);
        mSearchView.setIconifiedByDefault(true);*/
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(getApplicationContext(), "" + query, Toast.LENGTH_SHORT).show();
                product_list_old newFragment = new product_list_old();
                Bundle args = new Bundle();
                args.putString("query", query);
                newFragment.setArguments(args);
                fra3_main.this.getSupportFragmentManager().beginTransaction().replace(R.id.container, newFragment).commit();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Toast.makeText(getApplicationContext(), "" +newText, Toast.LENGTH_SHORT).show();
                if (newText.equals("")) {
                    product_list_old newFragment = new product_list_old();
                    fra3_main.this.getSupportFragmentManager().beginTransaction().replace(R.id.container, newFragment).commit();
                }
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d("leoonClose", "onClose ");
                return false;
            }
        });

        return true;
    }

    public void actionKey(final int keyCode) {

        new Thread () {

            public void run () {

                try {

                    Instrumentation inst=new Instrumentation();

                    inst.sendKeyDownUpSync(keyCode);

                } catch(Exception e) {

                    e.printStackTrace();
                }

            }

        }.start();

    }
    void back_menu(){
        Intent i = new Intent(this,test_page.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(i);
        finish();

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
                Intent intent = new Intent();
                intent.setClass(this, test_page.class);
                startActivity(intent);
                finish();
        }
        return true;
    }

}