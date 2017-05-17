package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.suntech.api.PaypageMainactivity;

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


public class shopping_step2 extends Activity {
    private static final int SunTechSwipy=1;
    String ip="",folder="",order_no_success="";
    TextView pay_money_tv;
    EditText mb_name_ed,cellphone_ed,email_ed,add1_ed;
    private Spinner spinner;
    private ArrayAdapter<String> listAdapter;
    private String[] bank_list_name;
    private String[] bank_list_id;
    private Context context;
    private Spinner city1,area1,paper1;
    private String[] paper1_text=new String[]{"二聯式","三聯式"};
    private int paper1_select;
    private EditText paper_no_ed,paper_tittle;
    private EditText service_no;
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private android.os.Handler mThreadHandler;
    private int bank_select=0;
    private String[] city_text=new String[]{"基隆市","台北市","新北市","桃園市","新竹市","新竹縣","苗栗縣","台中市","彰化縣","南投縣","雲林縣","嘉義市","嘉義縣","台南市","高雄市","屏東縣","台東縣","花蓮縣","宜蘭縣","澎湖縣","金門縣","連江縣"};
    private String[][] area=new String[][]{{"仁愛區","信義區","中正區","中山區","安樂區","暖暖區","七堵區"},{"中正區","大同區","中山區","松山區","大安區","萬華區","信義區","士林區","北投區","內湖區","南港區","文山區"},{"萬里區","金山區","板橋區","汐止區","深坑區","石碇區","瑞芳區","平溪區","雙溪區","貢寮區","新店區","坪林區","烏來區","永和區","中和區","土城區","三峽區","樹林區","鶯歌區","三重區","新莊區","泰山區","林口區","蘆洲區","五股區","八里區","淡水區","三芝區","石門區"},{"中壢區","平鎮區","龍潭區","楊梅區","新屋區","觀音區","桃園區","龜山區","八德區","大溪區","復興區","大園區","蘆竹區"},{"東區","香山區","北區"},{"竹北市","湖口鄉","新豐鄉","新埔鎮","關西鎮","芎林鄉","寶山鄉","竹東鎮","五峰鄉","橫山鄉","尖石鄉","北埔鄉","峨眉鄉"},{"竹南鎮","頭份鎮","三灣鄉","南庄鄉","獅潭鄉","後龍鎮","通霄鎮", "苑裡鎮","苗栗市","造橋鄉","頭屋鄉","公館鄉","大湖鄉","泰安鄉","鉰鑼鄉","三義鄉",
            "西湖鄉","卓蘭鎮"},{"中區","東區","南區","西區","北區","北屯區","西屯區","南屯區","太平區","大里區","霧峰區","烏日區","豐原區","后里區","石岡區", "東勢區","和平區","新社區","潭子區","大雅區","神岡區","大肚區","沙鹿區","龍井區", "梧棲區","清水區","大甲區","外圃區","大安區"},{"彰化市","芬園鄉","花壇鄉","秀水鄉","鹿港鎮","福興鄉","線西鄉", "和美鎮","伸港鄉","員林鎮","社頭鄉","永靖鄉","埔心鄉","溪湖鎮","大村鄉","埔鹽鄉", "田中鎮","北斗鎮","田尾鄉","埤頭鄉","溪州鄉","竹塘鄉","二林鎮","大城鄉","芳苑鄉", "二水鄉"},{"南投市","中寮鄉","草屯鎮","國姓鄉","埔里鎮","仁愛鄉","名間鄉", "集集鎮","水里鄉","魚池鄉","信義鄉","竹山鎮","鹿谷鄉"},{"斗南鎮","大埤鄉","虎尾鎮","土庫鎮","褒忠鄉","東勢鄉","臺西鄉",
            "崙背鄉","麥寮鄉","斗六市","林內鄉","古坑鄉","莿桐鄉","西螺鎮","二崙鄉","北港鎮",
            "水林鄉","口湖鄉","四湖鄉","元長鄉"},{"東區","西區"},{"番路鄉","梅山鄉","竹崎鄉","阿里山鄉","中埔鄉","大埔鄉",
            "水上鄉","鹿草鄉","太保市","朴子市","東石鄉","六腳鄉","新港鄉","民雄鄉","大林鎮","溪口鄉","義竹鄉","布袋鎮"},{"中西區","東區","南區","北區","安平區",
            "安南區","永康區","歸仁區","新化區","左鎮區","玉井區","楠西區","南化區",
            "仁德區","關廟區","龍崎區","官田區","麻豆區","佳里區","西港區","七股區","將軍區",
            "學甲區","北門區","新營區","後壁區","白河區","東山區","六甲區","下營區","柳營區",
            "鹽水區","善化區","大內區","山上區","新市區","安定區"},{"新興區","前金區","苓雅區","鹽埕區","鼓山區",
            "旗津區","前鎮區","三民區","楠梓區","小港區","左營區","仁武區","大社區","岡山區","路竹區","阿蓮區","田寮區","燕巢區",
            "橋頭區","梓官區","彌陀區","永安區","湖內區","鳳山區","大寮區","林園區","鳥松區",
            "大樹區","旗山區","美濃區","六龜區","內門區","杉林區","甲仙區","桃源區","三民區","那瑪夏區","茂林區","茄萣區"},{"屏東市","三地門鄉","霧臺鄉","瑪家鄉","九如鄉","里港鄉","高樹鄉",
            "鹽埔鄉","長治鄉","麟洛鄉","竹田鄉","內埔鄉","萬丹鄉","潮州鎮","泰武鄉","來義鄉",
            "萬巒鄉","嵌頂鄉","新埤鄉","南州鄉","林邊鄉","東港鎮","琉球鄉","佳冬鄉","新園鄉",
            "枋寮鄉", "枋山鄉","春日鄉","獅子鄉","車城鄉","牡丹鄉","恆春鎮","滿州鄉"},{"臺東市","綠島鄉","蘭嶼鄉","延平鄉","卑南鄉","鹿野鄉","關山鎮",
            "海端鄉","池上鄉","東河鄉","成功鎮","長濱鄉","太麻里鄉","金峰鄉","大武鄉","達仁鄉"},{"花蓮市","新城鄉","秀林鄉","吉安鄉","壽豐鄉","鳳林鎮","光復鄉",
            "豐濱鄉","瑞穗鄉","萬榮鄉","玉里鎮","卓溪鄉","富里鄉"},{"宜蘭市","頭城鎮","礁溪鄉","壯圍鄉","員山鄉","羅東鎮","三星鄉","大同鄉","五結鄉","冬山鄉","蘇澳鎮","南澳鄉"},{"馬公市","西嶼鄉","望安鄉","七美鄉","白沙鄉","湖西鄉"},{"金沙鎮","金湖鎮","金寧鄉","金城鎮","烈嶼鄉","烏坵鄉"},{"南竿鄉","北竿鄉","莒光鄉","東引"}};
    private String[] area_defult={"仁愛區","信義區","中正區","中山區","安樂區","暖暖區","七堵區"};
    private int[][] area_value={{1,2,3,4,5,6,7},{8,9,10,11,12,13,14,15,16,17,18,19},{20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48},{49,50,51,52,53,54,55,56,57,58,59,60,61},{62,63,64},{65,66,67,68,69,70,71,72,73,74,75,76,77},{78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95},{96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124},{125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150},{151,152,153,154,155,156,157,158,159,160,161,162,163},{164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183},{184,185},{186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203},{204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231,232,233,234,235,236,237,238,239,240},{241,242,243,244,245,246,247,248,249,250,251,252,253,254,255,256,257,258,259,260,261,262,263,264,265,266,267,268,269,270,271,272,273,274,275,276,277,278},{279,280,281,282,283,284,285,286,287,288,289,290,291,292,293,294,295,296,297,298,299,300,301,302,303,304,305,306,307,308,309,310,311},{312,313,314,315,316,317,318,319,320,321,322,323,324,325,326,327},{328,329,330,331,332,333,334,335,336,337,338,339,340},{341,342,343,344,345,346,347,348,349,350,351,352},{353,354,355,356,357,358},{359,360,361,362,363,364},{365,366,367,368}};
    private int[] city_value={1,2,3,4,5,6,7,8,10,11,12,13,14,15,17,19,20,21,22,23,24,25};
    ArrayAdapter<String> city1_adapter,area1_adapter,paper1_adapter ;
    private int city1_select,area1_select;
    private TextView e_cash_point,e_cash2_point,bonus_point,service_name,bonus2_point,e_gold_point, txt_total_pv;
    private LinearLayout layout_bouns,layout_e_cash2,layout_e_cash,layout_bonus2,layout_e_gold;
    String config="";
    String login_id;
    int has_data=0;
    int area_data=-1;
    int city_data=-1;
    String rate;
    int t,run_num;
    RadioGroup send_method;
    RadioButton card,atm,no_pay,send_method_self,send_method_send , card_paynow , web_atm , vir_account;
    private Button go_sucess;
    private int pay_way=1;
    private TextView money_us;
    String pay_money,prod,unit_num_str,unit_pv_str,unit_mv_str,unit_price_str,trade,temp_mb_no="",temp_mb_name="",temp_mb_tel="",from="";
    LinearLayout go_shopping,go_kind,go_account,go_index,go_cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_shopping_step2);
        setTitle("填寫資料");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayShowHomeEnabled(false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        }
        load_config();
        readprod();
        get_pv();

        config = readFromFile("client_config");
        try {
            login_id = new JSONObject(config).getString("login_id");
        } catch (Exception e) {

        }

        context = this;
        Bundle bundle = this.getIntent().getExtras();
        pay_money = bundle.getString("total");
        trade = bundle.getString("send_money");
        temp_mb_no = bundle.getString("temp_mb_no");
        from = bundle.getString("from");
        //Toast.makeText(shopping_step2.this, ""+temp_mb_no, Toast.LENGTH_SHORT).show();
        if (bundle != null && bundle.containsKey("temp_mb_name")) {
            temp_mb_name = bundle.getString("temp_mb_name");
        }
        if (bundle != null && bundle.containsKey("temp_mb_tel")) {
            temp_mb_tel = bundle.getString("temp_mb_tel");
        }
        paper_no_ed = (EditText) findViewById(R.id.paper_no);
        service_no = (EditText) findViewById(R.id.service_no);
        service_name = (TextView) findViewById(R.id.service_name);
        pay_money_tv = (TextView) findViewById(R.id.pay_money);
        pay_money_tv.setText(pay_money);
        paper_tittle = (EditText) findViewById(R.id.paper_tittle);
        spinner = (Spinner) findViewById(R.id.give_method);
        go_sucess = (Button) findViewById(R.id.go_sucess);
        money_us = (TextView) findViewById(R.id.total_us);
        go_sucess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goto_sucess();
            }
        });
        mb_name_ed = (EditText) findViewById(R.id.mb_name);
        //mb_name_ed.setFocusable(false);
        cellphone_ed = (EditText) findViewById(R.id.cellphone);
        //cellphone_ed.setFocusable(false);
        email_ed = (EditText) findViewById(R.id.email);
        // email_ed.setFocusable(false);
        e_cash_point = (TextView) findViewById(R.id.e_cash_point);
        e_cash2_point = (TextView) findViewById(R.id.e_cash2_point);
        bonus_point = (TextView) findViewById(R.id.bonus_point);
        bonus2_point = (TextView) findViewById(R.id.bonus2_point);
        e_gold_point = (TextView) findViewById(R.id.e_gold_point);

        city1_adapter = new ArrayAdapter<String>(this, R.layout.myspinner, city_text);
        city1_adapter.setDropDownViewResource(R.layout.myspinner);
        city1 = (Spinner) findViewById(R.id.city1);
        city1.setAdapter(city1_adapter);
        area1_adapter = new ArrayAdapter<String>(this, R.layout.myspinner, area_defult);
        area1_adapter.setDropDownViewResource(R.layout.myspinner);
        area1 = (Spinner) findViewById(R.id.area1);
        area1.setAdapter(area1_adapter);

        paper1_adapter = new ArrayAdapter<String>(this, R.layout.myspinner, paper1_text);
        paper1_adapter.setDropDownViewResource(R.layout.myspinner);
        paper1 = (Spinner) findViewById(R.id.paper1);
        paper1.setAdapter(paper1_adapter);
        go_shopping = (LinearLayout) findViewById(R.id.go_shopping);
        go_kind = (LinearLayout) findViewById(R.id.go_kind);
        go_account = (LinearLayout) findViewById(R.id.go_account);
        go_index = (LinearLayout) findViewById(R.id.go_index);
        go_cart = (LinearLayout) findViewById(R.id.go_cart);
        layout_bonus2 = (LinearLayout) findViewById(R.id.layout_bonus2);  //產品紅利
        layout_bouns = (LinearLayout) findViewById(R.id.layout_bouns);  //重消點數
        layout_e_cash2 = (LinearLayout) findViewById(R.id.layout_e_cash2);  //報單積分
        layout_e_cash = (LinearLayout) findViewById(R.id.layout_e_cash);    //電子錢包
        layout_e_gold = (LinearLayout) findViewById(R.id.layout_e_gold);    //小金庫
        card=(RadioButton)findViewById(R.id.card);
        atm=(RadioButton)findViewById(R.id.atm);
        send_method=(RadioGroup)findViewById(R.id.send_method);

        card_paynow=(RadioButton)findViewById(R.id.card_paynow);
        web_atm=(RadioButton)findViewById(R.id.web_atm);
        vir_account=(RadioButton)findViewById(R.id.vir_account);


        if(temp_mb_no.length()<1) {
            create_thread("get_prod_kind","get_prod_kind");
            go_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            go_shopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), fra3_main.class);

                    //new一個Bundle物件，並將要傳遞的資料傳入
                    Bundle bundle = new Bundle();
                    bundle.putString("go_tab", "0");

                    //將Bundle物件assign給intent
                    intent.putExtras(bundle);

                    //切換Activity
                    startActivity(intent);
                }
            });
            go_kind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), fra3_main.class);

                    //new一個Bundle物件，並將要傳遞的資料傳入
                    Bundle bundle = new Bundle();
                    bundle.putString("go_tab", "1");

                    //將Bundle物件assign給intent
                    intent.putExtras(bundle);

                    //切換Activity
                    startActivity(intent);

                }
            });
            go_account.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), fra3_main.class);

                    //new一個Bundle物件，並將要傳遞的資料傳入
                    Bundle bundle = new Bundle();
                    bundle.putString("go_tab", "3");

                    //將Bundle物件assign給intent
                    intent.putExtras(bundle);

                    //切換Activity
                    startActivity(intent);

                }
            });
            go_index.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    back_menu();

                }
            });
        }else{
            layout_bouns.setVisibility(View.GONE);
            layout_e_cash.setVisibility(View.GONE);
            layout_e_cash2.setVisibility(View.GONE);
            layout_bonus2.setVisibility(View.GONE);
            layout_e_gold.setVisibility(View.GONE);
            card.setVisibility(View.VISIBLE);
        }
        city1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                //讀取第一個下拉選單是選擇第幾個
                int pos = city1.getSelectedItemPosition();
                city1_select = pos;
                //重新產生新的Adapter，用的是二維陣列type2[pos]
                area1_adapter = new ArrayAdapter<String>(context, R.layout.myspinner, area[pos]);
                area1_adapter.setDropDownViewResource(R.layout.myspinner);

                //載入第二個下拉選單Spinner
                area1.setAdapter(area1_adapter);

                if (has_data == 1) {
                    for (int j = 0; j < area_value[city_data].length; j++) {
                        if (area_value[city_data][j] == area_data) {
                            area_data = j;
                        }
                    }

                    area1.setSelection(area_data);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        area1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                //讀取第一個下拉選單是選擇第幾個
                int pos = area1.getSelectedItemPosition();
                area1_select = pos;
//                if (t > 2) {
//                    has_data = 0;
//                }
//                if (has_data == 1 && area1_select != area_data) {
//                    area1.setSelection(area_data);
//                    has_data = 0;
//                }
                Log.d("area1_select", String.valueOf(area1_select) + ":" + String.valueOf(has_data));
                t++;

            }
        });

        paper1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                //讀取第一個下拉選單是選擇第幾個
                int pos = paper1.getSelectedItemPosition();
                paper1_select = pos;
                Log.d("paper1_select", String.valueOf(paper1_select));

                LinearLayout paper_no_lay = (LinearLayout) findViewById(R.id.paper_no_lay);
                LinearLayout paper_tittle_lay = (LinearLayout) findViewById(R.id.paper_tittle_lay);
                if (paper1_select == 0) {
                    paper_no_lay.setVisibility(View.GONE);
                    paper_tittle_lay.setVisibility(View.GONE);
                } else {
                    paper_no_lay.setVisibility(View.VISIBLE);
                    paper_tittle_lay.setVisibility(View.VISIBLE);
                }

            }
        });
        service_no.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && service_no.length() > 0) {
                    get_service_thread();
                } else if (!hasFocus && service_no.length() == 0) {
                    service_name.setText("");
                }

            }
        });
        send_method.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                Log.d("chk", "id" + checkedId);
                pay_money_tv = (TextView) findViewById(R.id.pay_money);
                if (checkedId == R.id.self) {
                    //some code
                    int pay = Integer.parseInt(pay_money) - Integer.parseInt(trade);
                    pay_money_tv.setText(Integer.toString(pay));
                    //int pay_money2= Integer.parseInt(pay);
                    Double money_u;
                    Double rat = Double.valueOf(rate);
                    money_u = Math.rint(pay / rat * 100) / 100;
                    // money_us.setText("(美金" + String.valueOf(money_u) + ")");
                    money_us.setText(String.valueOf(money_u));
                } else if (checkedId == R.id.send) {
                    //some code
                    pay_money_tv.setText(pay_money);
                    int pay_money2 = Integer.parseInt(pay_money);
                    Double money_u;
                    Double rat = Double.valueOf(rate);
                    money_u = Math.rint(pay_money2 / rat * 100) / 100;
                    // money_us.setText("(美金" + String.valueOf(money_u) + ")");
                    money_us.setText(String.valueOf(money_u));
                }

            }

        });
        send_method_self=(RadioButton)findViewById(R.id.self);
        send_method_send=(RadioButton)findViewById(R.id.send);
        no_pay=(RadioButton)findViewById(R.id.no_pay);
        card.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LinearLayout bank = (LinearLayout) findViewById(R.id.bank_layout);

                if (card.isChecked()) {
                    atm.setChecked(false);
                    no_pay.setChecked(false);
                    card_paynow.setChecked(false);
                    web_atm.setChecked(false);
                    vir_account.setChecked(false);
                    bank.setVisibility(View.GONE);
                    pay_way = 1;

                }
            }
        });
        atm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LinearLayout bank = (LinearLayout) findViewById(R.id.bank_layout);
                if (atm.isChecked()) {
                    card.setChecked(false);
                    no_pay.setChecked(false);
                    card_paynow.setChecked(false);
                    web_atm.setChecked(false);
                    vir_account.setChecked(false);
                    bank.setVisibility(View.VISIBLE);
                    pay_way = 2;

                }
            }
        });

        no_pay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LinearLayout bank = (LinearLayout) findViewById(R.id.bank_layout);

                if (no_pay.isChecked()) {
                    card.setChecked(false);
                    atm.setChecked(false);
                    card_paynow.setChecked(false);
                    web_atm.setChecked(false);
                    vir_account.setChecked(false);
                    bank.setVisibility(View.GONE);
                    pay_way = 3;

                }
            }
        });
        card_paynow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //立吉富
                LinearLayout bank = (LinearLayout) findViewById(R.id.bank_layout);

                if (card_paynow.isChecked()) {
                    card.setChecked(false);
                    atm.setChecked(false);
                    no_pay.setChecked(false);
                    web_atm.setChecked(false);
                    vir_account.setChecked(false);
                    bank.setVisibility(View.GONE);
                    pay_way = 4;
                }
            }
        });
        web_atm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //WEB_ATM
                LinearLayout bank = (LinearLayout) findViewById(R.id.bank_layout);

                if (web_atm.isChecked()) {
                    card.setChecked(false);
                    atm.setChecked(false);
                    no_pay.setChecked(false);
                    card_paynow.setChecked(false);
                    vir_account.setChecked(false);
                    bank.setVisibility(View.GONE);
                    pay_way = 5;
                }
            }
        });
        vir_account.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //虛擬帳號
                LinearLayout bank = (LinearLayout) findViewById(R.id.bank_layout);

                if (vir_account.isChecked()) {
                    card.setChecked(false);
                    atm.setChecked(false);
                    no_pay.setChecked(false);
                    card_paynow.setChecked(false);
                    web_atm.setChecked(false);
                    bank.setVisibility(View.GONE);
                    pay_way = 6;
                }
            }
        });
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                bank_select = position;
                Log.d("country_select", String.valueOf(bank_select));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        if(temp_mb_no.length()>1){
            mb_name_ed=(EditText)findViewById(R.id.mb_name);
            cellphone_ed=(EditText)findViewById(R.id.cellphone);
            mb_name_ed.setText(temp_mb_name);
            cellphone_ed.setText(temp_mb_tel);
        }
        get_mb_data_thread();
        get_bank_thread();
        if(login_id.substring(0,2).equals("TW")){
            if(login_id.substring(0,4).equals("TWCN")){
                city1.setVisibility(View.INVISIBLE);
                area1.setVisibility(View.INVISIBLE);
            }
        } else {
            city1.setVisibility(View.INVISIBLE);
            area1.setVisibility(View.INVISIBLE);
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
                        if (cmd == "get_prod_kind") {
                            prod_kind(jsonString);
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
            nameValuePairs.add(new BasicNameValuePair("prod",prod));
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
    public final void prod_kind(String input)
    {
        if(input.equals("1\n")){    //是產品紅利
            layout_bouns.setVisibility(View.GONE);
            layout_e_cash.setVisibility(View.GONE);
            layout_e_cash2.setVisibility(View.GONE);
            layout_bonus2.setVisibility(View.VISIBLE);
            card.setVisibility(View.GONE);
        }else{  //不是產品紅利
            layout_bouns.setVisibility(View.GONE);
            layout_e_cash.setVisibility(View.GONE);
            layout_e_cash2.setVisibility(View.GONE);
            layout_bonus2.setVisibility(View.GONE);
            card.setVisibility(View.VISIBLE);
        }

    }
    private void readprod() {
        Global_cart globalVariable = (Global_cart)this.getApplicationContext();
        Enumeration e =  globalVariable.cart.keys();

        run_num=0;
        prod = "";
        while(e. hasMoreElements()){
            if(run_num>0){
                prod+=",";
            }
            String s= e.nextElement().toString();
            prod+=s;
            run_num++;
            //Toast.makeText(getApplicationContext(), s + ":" + s2, Toast.LENGTH_SHORT).show();
        }
    }
    private String readFromFile(String FILENAME) {
        try{

            FileInputStream fin = this.openFileInput(FILENAME);
            byte[] buff = new byte[fin.available()];
            fin.read(buff);
            String str = new String(buff);



            return str;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public void get_bank_thread() {
        mThread = new HandlerThread("get_bank");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_bank_data("get_bank");

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        get_bank_data_res(jsonString);
                    }
                });
            }
        });
    }
    private String get_bank_data(String cmd)
    {
        String result = "";
        try
        {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",cmd));
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
    public final void get_bank_data_res(String input)
    {
        try
        {
            Log.d("get_mb_data_res", input);
            JSONArray jsonArray = new JSONArray(input);
            bank_list_name = new String[jsonArray.length()];
            bank_list_id = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                bank_list_name[i]=jsonData.getString("give_method");
                bank_list_id[i]=jsonData.getString("give_method_no");


            }
            listAdapter = new ArrayAdapter<String>(this,R.layout.pay_myspin,bank_list_name);
            listAdapter.setDropDownViewResource(R.layout.myspinner);

            spinner.setAdapter(listAdapter);





        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Toast.makeText(getApplication(), "" + e.toString(), Toast.LENGTH_LONG).show();
            Log.e("json", e.toString());


        }




    }


    public void get_mb_data_thread() {
        mThread = new HandlerThread("get_country");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_mb_data("get_mb_data");

                mUI_Handler.post(new Runnable() {
                    public void run() {

                        get_mb_data_res(jsonString);

                    }
                });
            }
        });
    }
    private String get_mb_data(String cmd)
    {
        String result = "";

        try
        {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",cmd));
            nameValuePairs.add(new BasicNameValuePair("mb_no",login_id));
            nameValuePairs.add(new BasicNameValuePair("temp_mb_no",temp_mb_no));
            /*if(temp_mb_no.length()>0){
                nameValuePairs.add(new BasicNameValuePair("mbst","web_mbst"));
            }else{

                nameValuePairs.add(new BasicNameValuePair("mbst","mbst"));
            }*/
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
    public final void get_mb_data_res(String input)
    {
        try
        {
            Log.d("get_mb_data_res1",input);
            JSONArray jsonArray = new JSONArray(input);


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                //   country_list_name[i]=jsonData.getString("name");
                //  country_list_id[i]=jsonData.getString("code");
                city1 = (Spinner) findViewById(R.id.city1);
                area1 = (Spinner) findViewById(R.id.area1);
                add1_ed = (EditText) findViewById(R.id.add1);
                mb_name_ed = (EditText) findViewById(R.id.mb_name);
                cellphone_ed = (EditText) findViewById(R.id.cellphone);
                email_ed = (EditText) findViewById(R.id.email);
                int a, b;
                if (temp_mb_no.length() < 1) {
                    mb_name_ed.setText(jsonData.getString("mb_name"));
                    cellphone_ed.setText(jsonData.getString("tel3"));
                    email_ed.setText(jsonData.getString("email"));
                }
                e_cash_point.setText(jsonData.getString("e_cash_point"));
                //e_cash2_point.setText(jsonData.getString("e_cash2_point"));
                // bonus_point.setText(jsonData.getString("bonus_point"));
                // bonus2_point.setText(jsonData.getString("bonus2_point"));
                Log.d("leotttest", jsonData.getString("e_cash_point"));
                Log.d("leotttest", jsonData.getString("mb_name"));
                Log.d("leotttest", jsonData.getString("tel3"));
                Log.d("leotttest", jsonData.getString("email"));
                //e_gold_point.setText(jsonData.getString("e_gold_point"));
                rate = jsonData.getString("cash_rate");
                int pay_money2 = Integer.parseInt(pay_money);
                Double money_u;
                Double rat = Double.valueOf(rate);
                money_u = Math.rint(pay_money2 / rat * 100) / 100;
                // money_us.setText("(美金"+String.valueOf(money_u)+")");
                money_us.setText(String.valueOf(money_u));
                a = Integer.parseInt(jsonData.getString("city3"));

                for (int j = 0; j < city_value.length; j++) {
                    if (city_value[j] == a) {
                        city1.setSelection(j);
                        a = j;
                    }
                }
                city_data = a;

                area_data = Integer.parseInt(jsonData.getString("area3"));
                if (area_data != -1) {
                    has_data = 1;
                }
                //
                add1_ed.setText(jsonData.getString("add3"));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_normal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        finish();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.home) {

            return true;
        }

        return super.onOptionsItemSelected(item);
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
    void goto_sucess(){

        String mb_name=mb_name_ed.getText().toString();
        String cellphone=cellphone_ed.getText().toString();
        String email=email_ed.getText().toString();
        String city= String.valueOf(city_value[city1_select]);
        String area= String.valueOf(area_value[area1_select]);
        String add=add1_ed.getText().toString();
        String paper= String.valueOf(paper1_select);
        String paper_no=paper_no_ed.getText().toString();
        String paper_title =paper_tittle.getText().toString();
        CheckBox bonus_use=(CheckBox)findViewById(R.id.bonu);
        CheckBox bonus2_use=(CheckBox)findViewById(R.id.bonus2);
        CheckBox ecash_use=(CheckBox)findViewById(R.id.eca);
        CheckBox ecash2_use=(CheckBox)findViewById(R.id.e_cash2);
        CheckBox egold_use=(CheckBox)findViewById(R.id.ego);

        EditText bonus_value_ed=(EditText)findViewById(R.id.bonus_value);
        EditText ecash_value_ed=(EditText)findViewById(R.id.ecash_value);
        EditText ecash2_value_ed=(EditText)findViewById(R.id.e_cash2_value);
        EditText ego_value_ed=(EditText)findViewById(R.id.egold_value);
        EditText bank_last_no_ed=(EditText)findViewById(R.id.bank_last_no);
        double rate2= Double.parseDouble(rate);

        String err="";
        String err2="";
        int pay_money= Integer.valueOf(pay_money_tv.getText().toString());
        if(mb_name.length()==0){
            err+=" 收件人";

        }
        if(cellphone.length()==0){
            err+=" 行動電話";

        }
        /*if(email.length()==0){
            err+=" 電子信箱";

        }*/
        if(add.length()==0 && send_method_send.isChecked()){
            err+=" 送貨地址";
        }
        if(paper.equals("1")){

            if(paper_no.length()==0){
                err+=" 統一編號";
            }
            if(paper_title.length()==0){
                err+=" 發票抬頭";
            }

        }

        int pay_point=0;
        Double per= Double.parseDouble(rate);
        if(bonus_use.isChecked()){
            pay_point+=Integer.parseInt(bonus_value_ed.getText().toString());
        }
        if(ecash_use.isChecked()){
            pay_point+=Integer.parseInt(ecash_value_ed.getText().toString());
        }
        if(ecash2_use.isChecked()){
            pay_point+=Integer.parseInt(ecash2_value_ed.getText().toString());
        }
        if(egold_use.isChecked()){
            pay_point+=Integer.parseInt(ego_value_ed.getText().toString());
        }
        Log.d("lettestt", String.valueOf(pay_point));
        Log.d("lettestt", String.valueOf(per));
        Log.d("lettestt", String.valueOf(pay_money));
        if((pay_way<=0&&!bonus_use.isChecked()&&!ecash_use.isChecked()&&!ecash2_use.isChecked()&&!bonus2_use.isChecked()&&!egold_use.isChecked())||(pay_way<=0&&pay_point*per<pay_money&&!bonus2_use.isChecked())){

            err+=" 付款方式";
        }
        if(pay_way==2){
            if(bank_last_no_ed.getText().toString().length()<=0){
                err+=" 匯款後五碼";
            }
        }
        if(err.length()>0) {
            err+=" 未填";
        }
        if(cellphone.length()>0 && cellphone.length()!=10){
            err2+=" 行動電話";
        }
        if(email.length()>0){
            if(email.indexOf("@")==-1 && email.indexOf(".")==-1){
                err2+=" 電子信箱";
            }
        }
        if(err2.length()>0) {
            //Toast.makeText(getApplication(),String.valueOf(err2.length()), Toast.LENGTH_LONG).show();
            err2+=" 錯誤";
        }
        //pay_way  1  2  3  4 刷卡 匯款 貨到付款 產品紅利

        if(err.length()>0 || err2.length()>0) {
            Toast.makeText(getApplication(), err + err2, Toast.LENGTH_LONG).show();
        }

        if(bonus_use.isChecked()&&bonus_value_ed.getText().toString().equals("0")){
            Toast.makeText(getApplication(),"選擇重消積分付款,抵扣點數必須大於零", Toast.LENGTH_LONG).show();
            return;

        }
        Log.d("testte",bonus_point.getText().toString());

        int bonus_int= Integer.parseInt(bonus_value_ed.getText().toString());
        double max_bonus_int= Double.parseDouble((bonus_point.getText().toString()));

        if(bonus_use.isChecked()&&bonus_int>max_bonus_int){
            Toast.makeText(getApplication(),"選擇重消積分付款,抵扣點數必須小於可用點數", Toast.LENGTH_LONG).show();
            return;

        }
        int ecash_int= Integer.parseInt(ecash_value_ed.getText().toString());
        double max_ecash_int= Double.parseDouble(e_cash_point.getText().toString());

        if(ecash_use.isChecked()&&ecash_int>max_ecash_int){
            Toast.makeText(getApplication(),"選擇電子錢包付款,抵扣點數必須小於可用點數", Toast.LENGTH_LONG).show();
            return;
        }

        if(ecash_use.isChecked()&&ecash_value_ed.getText().toString().equals("0")){
            Toast.makeText(getApplication(),"選擇電子錢包付款,抵扣點數必須大於零", Toast.LENGTH_LONG).show();
            return;
        }
        if(ecash_use.isChecked()&&temp_mb_no.length()>0&&ecash_int>(pay_money/rate2/2)){
            Toast.makeText(getApplication(),"電子錢包付款點數不可大於應付金額一半", Toast.LENGTH_LONG).show();
            return;
        }
        int max_bonus2_int= Integer.parseInt(bonus2_point.getText().toString());
        if(bonus2_use.isChecked()&&(pay_money/rate2)>max_bonus2_int){
            Toast.makeText(getApplication(),"產品紅利不足", Toast.LENGTH_LONG).show();
            return;
        }
        int ecash2_int= Integer.parseInt(ecash2_value_ed.getText().toString());
        double max_ecash2_int= Double.parseDouble(e_cash2_point.getText().toString());

        if(ecash2_use.isChecked()&&ecash2_int>max_ecash2_int){
            Toast.makeText(getApplication(),"選擇報單積分付款,抵扣點數必須小於可用點數", Toast.LENGTH_LONG).show();
            return;

        }

        if(ecash2_use.isChecked()&&ecash2_value_ed.getText().toString().equals("0")){
            Toast.makeText(getApplication(),"選擇報單積分付款,抵扣點數必須大於零", Toast.LENGTH_LONG).show();
            return;
        }

        int egold_int= Integer.parseInt(ego_value_ed.getText().toString());
        double max_egold_int= Double.parseDouble((e_gold_point.getText().toString()));

        if(egold_use.isChecked()&&egold_int>max_egold_int){
            Toast.makeText(getApplication(),"選擇小金庫付款,抵扣點數必須小於可用點數", Toast.LENGTH_LONG).show();
            return;

        }

        if(egold_use.isChecked()&&ego_value_ed.getText().toString().equals("0")){
            Toast.makeText(getApplication(),"選擇小金庫付款,抵扣點數必須大於零", Toast.LENGTH_LONG).show();
            return;
        }


        int total_point=0;
        if(ecash_use.isChecked()){
            total_point+=ecash_int;

        }
        if(ecash2_use.isChecked()){
            total_point+=ecash2_int;

        }
        if(bonus_use.isChecked()){
            total_point+=bonus_int;

        }
        if(egold_use.isChecked()){
            total_point+=egold_int;

        }
        Log.d("go_order_res", String.valueOf(pay_money));
        Log.d("go_order_res", String.valueOf(total_point));

        if(total_point*rate2>pay_money){
            Toast.makeText(getApplication(),"付款點數大於總金額", Toast.LENGTH_LONG).show();
            return;
        }

        if(err.length()<=0 && err2.length()<=0) {
            go_sucess.setEnabled(false);
            if(card.isChecked()){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("進入刷卡頁面後，姓名、手機號碼及Email將無法修改，是否繼續前往？");
                builder.setCancelable(false);

                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mThread = new HandlerThread("go_sucess");
                        mThread.start();
                        mThreadHandler = new Handler(mThread.getLooper());
                        mThreadHandler.post(new Runnable() {
                            public void run() {
                                final String jsonString = go_order("go_sucess");

                                mUI_Handler.post(new Runnable() {
                                    public void run() {

                                        go_order_res(jsonString);
                                        Toast.makeText(getApplication(), "訂單送出中,請稍候", Toast.LENGTH_LONG).show();


                                    }
                                });
                            }
                        });
                    }
                });

                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        go_sucess.setEnabled(true);
                    /*mThread = new HandlerThread("go_sucess");
                    mThread.start();
                    mThreadHandler = new Handler(mThread.getLooper());
                    mThreadHandler.post(new Runnable() {
                        public void run() {
                            final String jsonString = go_order("go_sucess");

                            mUI_Handler.post(new Runnable() {
                                public void run() {

                                    go_order_res(jsonString);
                                    go_sucess.setEnabled(false);
                                    Toast.makeText(getApplication(), "訂單送出中,請稍候", Toast.LENGTH_LONG).show();


                                }
                            });
                        }
                    });*/
                    }
                });
                //finish();
                AlertDialog alert = builder.create();
                alert.show();
            }else {
                mThread = new HandlerThread("go_sucess");
                mThread.start();
                mThreadHandler = new Handler(mThread.getLooper());
                mThreadHandler.post(new Runnable() {
                    public void run() {
                        final String jsonString = go_order("go_sucess");

                        mUI_Handler.post(new Runnable() {
                            public void run() {

                                go_order_res(jsonString);
                                go_sucess.setEnabled(false);
                                Toast.makeText(getApplication(), "訂單送出中,請稍候", Toast.LENGTH_LONG).show();


                            }
                        });
                    }
                });
            }

        }
    }
    //到付款頁
    //private void startPayIntent(String order_no,String prod_name,String prod_num,String prod_price,String total_money)
    private void startPayIntent(String order_no,String total_money)
    {
        try
        {
            order_no_success=order_no;
            //Toast.makeText(getApplication(), "1", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent("com.bbpos.cswiper.CALL_STATE").setPackage("com.bbpos.cswiper");
            Intent intent=new Intent();
            Bundle bundle = new Bundle();
            intent.setClass(shopping_step2.this, PaypageMainactivity.class);
            //intent.setClass(com.example.nick.afs_demo.shopping_step2.this, com.suntech.api.PaypageMainactivity.class);
            //intent.setClassName("com.bbpos.cswiper","CALL_STATE");

            bundle.putInt("MainLayout", R.layout.paypage_layout);	//Paypage Layout
            bundle.putInt("Paypage", R.id.Paypage_Layout);		//Paypage Layout It
            bundle.putInt("Direction", 2);	//設定app方向（0：可轉向，1：是橫，2：是直
            bundle.putInt("Language", 0);  //語系﹣ 0：手機預設 1 ：繁體中文  2：英文   3：簡體中文

            //Page setting
            bundle.putInt("PaypageColor", 0xffdcdcdc);  //Paypage's background color
            bundle.putInt("TB_Color", 0xffc9c9c9); //Title Banner's background color
            bundle.putInt("TB_TextColor", 0xff000000); //title banner word's color
            bundle.putInt("TB_ButtonColor", 0xff000000); //title banner btn word's color
            bundle.putInt("Img_textColor", 0xff0000ff); //Img text color
            bundle.putInt("BorderColor", 0xffcccccc); //border color
            bundle.putInt("TitleColor", 0xff999999); //titleTv color---and redsun word's color
            bundle.putInt("ContentBgColor", 0xffffffff); //content background color
            bundle.putInt("ContentDescColor", 0xff000000); //content title text color
            bundle.putInt("ContentTvColor", 0xff000000);  //content content text color
            bundle.putInt("BtnBgColor", 0xff00ff00);  //pay btn background color
            bundle.putInt("BtnColor", 0xff000000);  //pay btn word's color
            bundle.putInt("VisaImg", R.drawable.visaimg);  //visa img drawable
            bundle.putInt("writePadBgColor", 0xffcccccc);  //writePad background color
            bundle.putInt("writePadAlpha", 175);  //writePad Alpha
            bundle.putInt("writePadTitleColor", 0xffffffff); //writePad title word's color
            bundle.putInt("writePadBtnColor", 0xffcccccc); //writePad Btn's color
            bundle.putInt("MITReceiveAnim", R.anim.mitreceiveanim); 	//MIT Receive Animation
            bundle.putInt("MITSuccessMedia", R.raw.aldebaran);		//MIT Success Media

            //Parameters setting
            //Swipy、payCode、barCode、atm_account
            //bundle.putString("web","S1502020223");						//商家代碼  正式:S1504100197   測試:S1512319011
           // bundle.putString("TransPwd","pp3258es");      				//交易密碼(非登入密碼)

            bundle.putString("web","S1703290476");						//商家代碼  正式:S1504100197   測試:S1512319011
            bundle.putString("TransPwd","a1234567");      				//交易密碼(非登入密碼)

            //bundle.putString("MN", pay_money_tv.getText().toString());  //交易金額---payCode最低35元；barCode最低25元；atm_account最低11元
            bundle.putString("MN", total_money);  //交易金額---payCode最低35元；barCode最低25元；atm_account最低11元
            bundle.putString("Term", "");
            bundle.putString("OrderInfo", "");  //交易商品備註
            bundle.putString("Td", order_no);  //訂單編號

            bundle.putString("CustomerName", mb_name_ed.getText().toString());  //消費者姓名
            bundle.putString("Mobile", cellphone_ed.getText().toString());  //消費者電話
            bundle.putString("Email", email_ed.getText().toString());  //消費者Email
            bundle.putBoolean("isProduction", true);  //是否為正式模式
            bundle.putString("Note1", "");	  //商家自行定義，長度最多100字元，不可有特殊符號
            bundle.putString("Note2", "");  //商家自行定義，長度最多100字元，不可有特殊符號
            bundle.putBoolean("isPhone", true);  //是否為手機
            bundle.putBoolean("needSign", false);	  //是否需要簽名
            bundle.putBoolean("orderNoChk", false);
            bundle.putInt("timeoutSec", Integer.parseInt("600"));	  //倒數計時時間 sec  先設10分鐘
            ////
            bundle.putString("PayType", "credit");  //付款方式（credit: 信用卡, MIT_AppPayment: 感應收款, paycode: 超商代碼繳費, barcode: 超商條碼繳費, atm_account: ATM轉帳繳款）
            bundle.putString("ProductName", "交易總額");  //商品名稱，如有多項商品則以全形「；」分隔（商品名稱不可包含;符號），最少一項商品，最多十項（條碼與ATM轉帳為必填）
            bundle.putString("ProductPrice", total_money);	 //	商品金額（單價），如有多項商品則以全形「；」分隔，最少一項商品，最多十項（條碼與ATM轉帳為必填）
            bundle.putString("ProductQuantity", "1");  //商品數量，如有多項商品則以全形「；」分隔，最少一項商品，最多十項（條碼與ATM轉帳為必填）
            bundle.putString("DueDate", "");  //繳費期限（格式YYYYMMDD），超商代碼繳費、超商條碼繳費與ATM轉帳繳款為必填
            bundle.putString("UserNo", "");  //用戶編號（非必填）
            bundle.putString("BillDate", "");  //列帳日期（格式YYYYMMDD）（非必填）
            ////
            //bundle.putBoolean("needWheel", true);	//是否需要滾輪
            Log.d("boudle", "details=" + bundle2string(bundle));
            intent.putExtras(bundle);
            //Toast.makeText(getApplication(), "2", Toast.LENGTH_SHORT).show();
            startActivityForResult(intent, SunTechSwipy);
            //startActivity(intent);
        }
        catch (Exception e)
        {
            // TODO: handle exception
            Log.i("log", e.getMessage());
        }

    }
    public static String bundle2string(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        String string = "Bundle{";
        for (String key : bundle.keySet()) {
            string += " " + key + " => " + bundle.get(key) + ";";
        }
        string += " }Bundle";
        return string;
    }
    //startActivityForResult的回傳
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(getApplication(),"requestCode:"+String.valueOf(requestCode)+"resultCode:"+String.valueOf(resultCode), Toast.LENGTH_LONG).show();
        try
        {
            switch (requestCode)
            {
                case SunTechSwipy:
                    try
                    {
                        if( data != null )
                        {
                            if( resultCode == 10 &&data.getExtras().getString("RespCode").equals("00"))
                            {
                                /*Toast.makeText(getApplication(),"RespCode："+data.getExtras().getString("RespCode")+" BuySafeNo："+data.getExtras().getString("BuySafeNo")+"Td："+data.getExtras().getString("Td")
                                        +"web："+data.getExtras().getString("web")+"MN："+data.getExtras().getString("MN")+"ErrorMessage："+data.getExtras().getString("ErrorMessage")+"ApproveCode："+data.getExtras().getString("ApproveCode")
                                        +"EXT："+data.getExtras().getString("EXT")+"ChkValue："+data.getExtras().getString("ChkValue")+"PayType："+data.getExtras().getString("PayType")+"PayCode："+data.getExtras().getString("PayCode"), Toast.LENGTH_LONG).show();*/
                                create_thread_credit("update_order_monry","update_order_monry",data.getExtras().getString("Td"),data.getExtras().getString("MN"));
                                //訂單單號,交易金額
                                Log.e("RespCode", data.getExtras().getString("RespCode"));
                                Log.e("BuySafeNo",data.getExtras().getString("BuySafeNo"));
                                Log.e("Td",data.getExtras().getString("Td"));
                                Log.e("web",data.getExtras().getString("web"));
                                Log.e("MN",data.getExtras().getString("MN"));
                                Log.e("ErrorMessage",data.getExtras().getString("ErrorMessage"));
                                Log.e("ApproveCode",data.getExtras().getString("ApproveCode"));
                                Log.e("note1",data.getExtras().getString("note1"));
                                Log.e("note2",data.getExtras().getString("note2"));
                                Log.e("Last4Cardno",data.getExtras().getString("Last4Cardno"));
                                Log.e("EXT",data.getExtras().getString("EXT"));
                                Log.e("ChkValue",data.getExtras().getString("ChkValue"));
                                ////
                                Log.e("PayType",data.getExtras().getString("PayType"));
                                Log.e("barcodeA",data.getExtras().getString("barcodeA"));
                                Log.e("barcodeB",data.getExtras().getString("barcodeB"));
                                Log.e("barcodeC",data.getExtras().getString("barcodeC"));
                                Log.e("PayCode",data.getExtras().getString("PayCode"));
                                Log.e("StoreType",data.getExtras().getString("StoreType"));
                                Log.e("ATMBankID",data.getExtras().getString("ATMBankID"));
                                Log.e("ATMAccount",data.getExtras().getString("ATMAccount"));

                            }

                        }else{
                            Toast.makeText(getApplication(),"訂單已成立，但付款尚未完成，請與家樂易聯絡", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(), shopping_res.class);

                            //new一個Bundle物件，並將要傳遞的資料傳入
                            Bundle bundle = new Bundle();
                            bundle.putString("res","success,"+order_no_success);

                            //將Bundle物件assign給intent
                            intent.putExtras(bundle);
                            finish();
                            //切換Activity
                            startActivity(intent);

                        }

                    }
                    catch (Exception e)
                    {
                        // TODO: handle exception
                        Log.i("log", e.getMessage());
                    }

                    break;

                default:
                    break;
            }

        }
        catch (Exception e)
        {
            // TODO: handle exception
            Log.i("log", e.getMessage());

        }

    }

    private void get_pv(){
        txt_total_pv = (TextView) findViewById(R.id.total_pv);
        Global_cart globalVariable = (Global_cart)this.getApplicationContext();
        Enumeration e =  globalVariable.cart.keys();

        //String bank_name=bank_list_id[bank_select];
        //=bank_list_id[bank_select];
        // int run_num=0;
        String []pro;
        //prod = "";
        String pro_num;
        //while(e. hasMoreElements()){
        //  if(run_num>0){
        //   prod+=",";
        // }
        // String s= e.nextElement().toString();
        // String s2 = globalVariable.cart.get(s).toString();
        //prod+=s;
        //run_num++;
        //Toast.makeText(getApplicationContext(), s + ":" + s2, Toast.LENGTH_SHORT).show();
        //}
        pro_num= String.valueOf(run_num);
        pro=new String[run_num];
        String []pro_unit_num=new String[run_num];
        String []pro_unit_price=new String[run_num];
        String []pro_unit_pv=new String[run_num];
        String []pro_unit_mv=new String[run_num];

        String []tmp=new String[run_num];
        for(int i=0;i<run_num;i++){
            tmp=prod.split(",");
            pro[i]=tmp[i];//產品
        }
        int total_pv=0,total_money=0,total_mv=0;
        for(int i=0;i<run_num;i++) {

            String aaa2 = globalVariable.cart.get(pro[i]).toString();
            String[] tmp2 = aaa2.split(",");//數量 單價
            pro_unit_num[i] = tmp2[0];//num
            if (tmp2[1] == "") {
                tmp2[1] = "0";
            }
            pro_unit_pv[i] = tmp2[2];//pv
            pro_unit_mv[i] = tmp2[3];
            pro_unit_price[i] = tmp2[1];//money
            total_pv += Integer.parseInt(pro_unit_pv[i]) * Integer.parseInt(pro_unit_num[i]);
            Log.e("shenggg", "" + total_pv);
            total_mv += Integer.parseInt(pro_unit_mv[i]) * Integer.parseInt(pro_unit_num[i]);
            total_money += Integer.parseInt(pro_unit_price[i]) * Integer.parseInt(pro_unit_num[i]);
        }
        txt_total_pv.setText(""+total_pv);

    }

    private String go_order(String cmd)
    {
        String result = "";
        String mb_name=mb_name_ed.getText().toString();
        String cellphone=cellphone_ed.getText().toString();
        String email=email_ed.getText().toString();
        String city = String.valueOf(city_value[city1_select]);
        String area = String.valueOf(area_value[area1_select]);
        String add =add1_ed.getText().toString();
        String paper= String.valueOf(paper1_select);
        String paper_no=paper_no_ed.getText().toString();
        String paper_title =paper_tittle.getText().toString();
        String str_service_no ="";
        if(service_no.getText().toString().length()>0){
            str_service_no = service_no.getText().toString();
        }
        CheckBox bonus_use=(CheckBox)findViewById(R.id.bonu);
        CheckBox bonus2_use=(CheckBox)findViewById(R.id.bonus2);
        CheckBox ecash_use=(CheckBox)findViewById(R.id.eca);
        CheckBox ecash2_use=(CheckBox)findViewById(R.id.e_cash2);
        CheckBox egold_use=(CheckBox)findViewById(R.id.ego);

        EditText bonus_value_ed=(EditText)findViewById(R.id.bonus_value);
        EditText ecash_value_ed=(EditText)findViewById(R.id.ecash_value);
        EditText ecash2_value_ed=(EditText)findViewById(R.id.e_cash2_value);
        EditText egold_value_ed=(EditText)findViewById(R.id.egold_value);
        EditText Step2_Memo = (EditText)findViewById(R.id.step2_Memo);


        Global_cart globalVariable = (Global_cart)this.getApplicationContext();
        Enumeration e =  globalVariable.cart.keys();

        String bank_name=bank_list_id[bank_select];
        //=bank_list_id[bank_select];
        // int run_num=0;
        String []pro;
        //prod = "";
        String pro_num;
        //while(e. hasMoreElements()){
        //  if(run_num>0){
        //   prod+=",";
        // }
        // String s= e.nextElement().toString();
        // String s2 = globalVariable.cart.get(s).toString();
        //prod+=s;
        //run_num++;
        //Toast.makeText(getApplicationContext(), s + ":" + s2, Toast.LENGTH_SHORT).show();
        //}
        pro_num= String.valueOf(run_num);
        pro=new String[run_num];
        String []pro_unit_num=new String[run_num];
        String []pro_unit_price=new String[run_num];
        String []pro_unit_pv=new String[run_num];
        String []pro_unit_mv=new String[run_num];

        String []tmp=new String[run_num];
        for(int i=0;i<run_num;i++){
            tmp=prod.split(",");
            pro[i]=tmp[i];//產品
        }
        int total_pv=0,total_money=0,total_mv=0;
        for(int i=0;i<run_num;i++){

            String aaa2=globalVariable.cart.get(pro[i]).toString();
            String []tmp2=aaa2.split(",");//數量 單價
            pro_unit_num[i]= tmp2[0];//num
            if(tmp2[1]==""){
                tmp2[1]="0";
            }
            pro_unit_pv[i]=tmp2[2];//pv
            pro_unit_mv[i]=tmp2[3];
            pro_unit_price[i]=tmp2[1];//money
            total_pv+= Integer.parseInt(pro_unit_pv[i])*Integer.parseInt(pro_unit_num[i]);
            Log.e("shenggg",""+total_pv);
            total_mv+= Integer.parseInt(pro_unit_mv[i])*Integer.parseInt(pro_unit_num[i]);
            total_money+= Integer.parseInt(pro_unit_price[i])*Integer.parseInt(pro_unit_num[i]);

        }
        unit_num_str="";
        unit_price_str="";
        unit_pv_str="";
        unit_mv_str="";
        for(int i=0;i<run_num;i++){
            if(i>0){
                unit_num_str+=",";
                unit_price_str+=",";
                unit_pv_str+=",";
                unit_mv_str+=",";
            }
            unit_num_str+=pro_unit_num[i];
            unit_price_str+=pro_unit_price[i];
            unit_pv_str+=pro_unit_pv[i];
            unit_mv_str+=pro_unit_mv[i];

        }
        Log.d("testleo", String.valueOf(total_mv));
        Log.d("testleo",prod);
        Log.d("testleo",unit_num_str);
        Log.d("testleo",unit_price_str);
        Log.d("testleo",unit_pv_str);
        Log.d("testleo",unit_mv_str);

        int trade_money=0;
        if(send_method_send.isChecked()){
            trade_money=Integer.parseInt(pay_money)-total_money;
        }

        EditText bank_last_no_ed=(EditText)findViewById(R.id.bank_last_no);

        try
        {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd","send_order"));
            if(temp_mb_no.length()>1) {
                nameValuePairs.add(new BasicNameValuePair("shopping_type", "join"));
                nameValuePairs.add(new BasicNameValuePair("join_mbno",temp_mb_no));
            }else{
                nameValuePairs.add(new BasicNameValuePair("shopping_type", "shopping"));
                nameValuePairs.add(new BasicNameValuePair("mb_no",login_id));
            }
            nameValuePairs.add(new BasicNameValuePair("sub_pv",String.valueOf(total_pv)));
            nameValuePairs.add(new BasicNameValuePair("sub_mv",String.valueOf(total_mv)));
            nameValuePairs.add(new BasicNameValuePair("order_total_money",String.valueOf(total_money)));
            nameValuePairs.add(new BasicNameValuePair("trade_money",String.valueOf(trade_money)));
            if(bonus_use.isChecked()){
                nameValuePairs.add(new BasicNameValuePair("use_bonus",bonus_value_ed.getText().toString()));
            }
            if(ecash_use.isChecked()){
                nameValuePairs.add(new BasicNameValuePair("use_e_cash",ecash_value_ed.getText().toString()));
            }
            if(bonus2_use.isChecked()){
                Double money_u;
                Double rat = Double.valueOf(rate);
                money_u = Math.rint(total_money / rat * 100) / 100;
                nameValuePairs.add(new BasicNameValuePair("use_bonus2",String.valueOf(money_u)));
            }
            if(ecash2_use.isChecked()){
                nameValuePairs.add(new BasicNameValuePair("use_e_cash2",ecash2_value_ed.getText().toString()));
            }
            if(egold_use.isChecked()){
                nameValuePairs.add(new BasicNameValuePair("use_e_gold",egold_value_ed.getText().toString()));
            }

            if(send_method_send.isChecked()){
                nameValuePairs.add(new BasicNameValuePair("send_method","1"));
            }else if(send_method_self.isChecked()){
                nameValuePairs.add(new BasicNameValuePair("send_method","2"));
            }
            nameValuePairs.add(new BasicNameValuePair("send_name",mb_name));
            nameValuePairs.add(new BasicNameValuePair("cellphone",cellphone));
            nameValuePairs.add(new BasicNameValuePair("email",email));
            nameValuePairs.add(new BasicNameValuePair("area",String.valueOf(area_value[city1_select][area1_select])));
            nameValuePairs.add(new BasicNameValuePair("city",String.valueOf(city_value[city1_select])));
            nameValuePairs.add(new BasicNameValuePair("add", add));
            nameValuePairs.add(new BasicNameValuePair("step2_memo",Step2_Memo.getText().toString()));
            if(paper_no.length()<=0){
                paper_no="";
            }
            if(paper.equals("1")){
                nameValuePairs.add(new BasicNameValuePair("paper_no",paper_no));
                nameValuePairs.add(new BasicNameValuePair("paper_title",paper_title));
            }else{
                nameValuePairs.add(new BasicNameValuePair("paper_no",""));
                nameValuePairs.add(new BasicNameValuePair("paper_title",""));
            }

            nameValuePairs.add(new BasicNameValuePair("pay_way",String.valueOf(pay_way)));
            nameValuePairs.add(new BasicNameValuePair("bank_name",bank_name));
            if(pay_way==2){
                nameValuePairs.add(new BasicNameValuePair("last5code",bank_last_no_ed.getText().toString()));
            }
            nameValuePairs.add(new BasicNameValuePair("cart_num", pro_num));
            nameValuePairs.add(new BasicNameValuePair("prod", prod));
            nameValuePairs.add(new BasicNameValuePair("unit_num_str", unit_num_str));
            nameValuePairs.add(new BasicNameValuePair("unit_price_str", unit_price_str));
            nameValuePairs.add(new BasicNameValuePair("unit_pv_str",unit_pv_str));
            nameValuePairs.add(new BasicNameValuePair("unit_mv_str",unit_mv_str));
            nameValuePairs.add(new BasicNameValuePair("service_no",str_service_no));

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
        catch (Exception ee)
        {
            Log.e("log_tag", ee.toString());
        }
        return result;
    }
    public final void go_order_res(String input)
    {
        Log.d("leo_testt",input);
        Global_cart globalVariable = (Global_cart) context.getApplicationContext();
        globalVariable.cart.clear();
        String[] order_credit = input.split(",");

        if(order_credit[1].equals("credit")){
            //Toast.makeText(getApplication(), "按下刷卡紐", Toast.LENGTH_LONG).show();
            //0:success 1:訂單編號  2:產品名稱  3.產品數量  4.單價  5.總金額
            //0:success 1:訂單編號 2.總金額
            if(order_credit[0].equals("success")){
                if(pay_way == 1){
                    startPayIntent(order_credit[2],order_credit[3]);
                }else{
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("ord_no",order_credit[2]);
                    bundle.putString("total_money",order_credit[3]);
                    bundle.putString("mb_no",login_id);
                    bundle.putString("tel3",cellphone_ed.getText().toString());
                    bundle.putString("name",mb_name_ed.getText().toString());
                    bundle.putString("email",email_ed.getText().toString());
                    bundle.putString("pay_way",pay_way+"");
                    intent.putExtras(bundle);
                    intent.setClass(shopping_step2.this,Activity_Payment_Page.class);
                    startActivity(intent);
                }

            }else{
                Toast.makeText(getApplication(), "刷卡錯誤", Toast.LENGTH_LONG).show();
            }

        }else {
            if (from.equals("join")){
                Bundle bundle =new Bundle();
                bundle.putString("mb_no",temp_mb_no);
                bundle.putString("from", from);
                Intent intent =new Intent();
                intent.putExtras(bundle);
                intent.setClass(this, join_success.class);
                finish();
                String[] res = input.split(",");

                if (res[0].equals("success")) {
                    //切換Activity
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplication(), input, Toast.LENGTH_SHORT).show();
                }
            }else{
                //new一個intent物件，並指定Activity切換的class
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), shopping_res.class);

                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle = new Bundle();
                bundle.putString("res", input);

                //將Bundle物件assign給intent
                intent.putExtras(bundle);
                finish();
                String[] res = input.split(",");

                if (res[0].equals("success")) {
                    //切換Activity
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplication(), input, Toast.LENGTH_SHORT).show();
                }
            }

        }

    }
    public void get_service_thread() {
        mThread = new HandlerThread("get_service");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_service_data("get_service");

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        get_service_data_res(jsonString);
                    }
                });
            }
        });
    }
    private String get_service_data(String cmd)
    {
        String result = "";
        try
        {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",cmd));
            nameValuePairs.add(new BasicNameValuePair("service_no",service_no.getText().toString()));
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
    public final void get_service_data_res(String input)
    {
        try
        {
            if(input.equals("1\n")){
                Toast.makeText(getApplication(), "查無此收單中心，請確認後重新輸入", Toast.LENGTH_SHORT).show();
                service_name.setText("");
                service_no.setText("");
            }else{
                Log.d("get_mb_data_res", input);
                JSONArray jsonArray = new JSONArray(input);
                JSONObject jsonData = jsonArray.getJSONObject(0);
                service_name.setText(jsonData.getString("service_name"));
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
    void back_menu(){
        Intent i = new Intent(this,test_page.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        startActivity(i);
        finish();

    }
    public void create_thread_credit(final String cmd,String thread_name, final String order_no, final String pay_money) {
        mThread = new HandlerThread(thread_name);
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = executeQuery_credit(cmd,order_no,pay_money);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        if (cmd == "update_order_monry") {
                            newcredit(jsonString);
                        }
                    }
                });
            }
        });
    }
    private String executeQuery_credit(String query,String order_no,String pay_money)
    {
        String result = "";
        try
        {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/android_sql.php");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd",query));
            nameValuePairs.add(new BasicNameValuePair("order_no",order_no));
            nameValuePairs.add(new BasicNameValuePair("pay_money",pay_money));
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
    public final void newcredit(String input)
    {
        if(input.equals("1\n")){
            Toast.makeText(getApplication(), "刷卡已完成", Toast.LENGTH_LONG).show();
            Bundle bundle =new Bundle();
            bundle.putString("mb_no",temp_mb_no);
            bundle.putString("from", from);
            Intent intent =new Intent();
            intent.putExtras(bundle);
            intent.setClass(this, join_success.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(getApplication(), "寫入錯誤", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), shopping_res.class);

            //new一個Bundle物件，並將要傳遞的資料傳入
            Bundle bundle = new Bundle();
            bundle.putString("res","success,"+order_no_success);

            //將Bundle物件assign給intent
            intent.putExtras(bundle);
            finish();
            //切換Activity
            startActivity(intent);
        }

    }
}
