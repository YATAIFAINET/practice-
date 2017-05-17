package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.method.ReplacementTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nick.joeyi_android2.GCMD.Picture_GCMD;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class join_data_qrcode extends Activity implements View.OnClickListener {
    //new Add Object On Fainet include Morning Night Phone and CopyPhone and email and address On WentToaddress
    private final String TAG = "fac";
    ArrayAdapter<String> city1_adapter, city2_adapter, city3_adapter,bank_adapter;
    ArrayAdapter<String> area1_adapter, area2_adapter, area3_adapter;
    Handler handler = new Handler();
    Runnable runnable;
    String config = "";
    String login_id;
    String way = "0", join_prod_no_select;
    private ProgressDialog mDialog;
    private Spinner spinner, person_data_spinner, sex_spinner, join_grade_spinner, join_prod_spinner;
    private Spinner city1, city2, area1, area2, city3, area3,bank;
    private LinearLayout join_prod_lin;
    private String[] city_text = new String[]{"請選擇", "基隆市", "台北市", "新北市", "桃園市", "新竹市", "新竹縣", "苗栗縣", "台中市", "彰化縣", "南投縣", "雲林縣", "嘉義市", "嘉義縣", "台南市", "高雄市", "屏東縣", "台東縣", "花蓮縣", "宜蘭縣", "澎湖縣", "金門縣", "連江縣"};
    private String[][] area = new String[][]{{"請選擇"}, {"仁愛區", "信義區", "中正區", "中山區", "安樂區", "暖暖區", "七堵區"}, {"中正區", "大同區", "中山區", "松山區", "大安區", "萬華區", "信義區", "士林區", "北投區", "內湖區", "南港區", "文山區"}, {"萬里區", "金山區", "板橋區", "汐止區", "深坑區", "石碇區", "瑞芳區", "平溪區", "雙溪區", "貢寮區", "新店區", "坪林區", "烏來區", "永和區", "中和區", "土城區", "三峽區", "樹林區", "鶯歌區", "三重區", "新莊區", "泰山區", "林口區", "蘆洲區", "五股區", "八里區", "淡水區", "三芝區", "石門區"}, {"中壢區", "平鎮區", "龍潭區", "楊梅區", "新屋區", "觀音區", "桃園區", "龜山區", "八德區", "大溪區", "復興區", "大園區", "蘆竹區"}, {"東區", "香山區", "北區"}, {"竹北市", "湖口鄉", "新豐鄉", "新埔鎮", "關西鎮", "芎林鄉", "寶山鄉", "竹東鎮", "五峰鄉", "橫山鄉", "尖石鄉", "北埔鄉", "峨眉鄉"}, {"竹南鎮", "頭份鎮", "三灣鄉", "南庄鄉", "獅潭鄉", "後龍鎮", "通霄鎮", "苑裡鎮", "苗栗市", "造橋鄉", "頭屋鄉", "公館鄉", "大湖鄉", "泰安鄉", "鉰鑼鄉", "三義鄉",
            "西湖鄉", "卓蘭鎮"}, {"中區", "東區", "南區", "西區", "北區", "北屯區", "西屯區", "南屯區", "太平區", "大里區", "霧峰區", "烏日區", "豐原區", "后里區", "石岡區", "東勢區", "和平區", "新社區", "潭子區", "大雅區", "神岡區", "大肚區", "沙鹿區", "龍井區", "梧棲區", "清水區", "大甲區", "外圃區", "大安區"}, {"彰化市", "芬園鄉", "花壇鄉", "秀水鄉", "鹿港鎮", "福興鄉", "線西鄉", "和美鎮", "伸港鄉", "員林鎮", "社頭鄉", "永靖鄉", "埔心鄉", "溪湖鎮", "大村鄉", "埔鹽鄉", "田中鎮", "北斗鎮", "田尾鄉", "埤頭鄉", "溪州鄉", "竹塘鄉", "二林鎮", "大城鄉", "芳苑鄉", "二水鄉"}, {"南投市", "中寮鄉", "草屯鎮", "國姓鄉", "埔里鎮", "仁愛鄉", "名間鄉", "集集鎮", "水里鄉", "魚池鄉", "信義鄉", "竹山鎮", "鹿谷鄉"}, {"斗南鎮", "大埤鄉", "虎尾鎮", "土庫鎮", "褒忠鄉", "東勢鄉", "臺西鄉",
            "崙背鄉", "麥寮鄉", "斗六市", "林內鄉", "古坑鄉", "莿桐鄉", "西螺鎮", "二崙鄉", "北港鎮",
            "水林鄉", "口湖鄉", "四湖鄉", "元長鄉"}, {"東區", "西區"}, {"番路鄉", "梅山鄉", "竹崎鄉", "阿里山鄉", "中埔鄉", "大埔鄉",
            "水上鄉", "鹿草鄉", "太保市", "朴子市", "東石鄉", "六腳鄉", "新港鄉", "民雄鄉", "大林鎮", "溪口鄉", "義竹鄉", "布袋鎮"}, {"中西區", "東區", "南區", "北區", "安平區",
            "安南區", "永康區", "歸仁區", "新化區", "左鎮區", "玉井區", "楠西區", "南化區",
            "仁德區", "關廟區", "龍崎區", "官田區", "麻豆區", "佳里區", "西港區", "七股區", "將軍區",
            "學甲區", "北門區", "新營區", "後壁區", "白河區", "東山區", "六甲區", "下營區", "柳營區",
            "鹽水區", "善化區", "大內區", "山上區", "新市區", "安定區"}, {"新興區", "前金區", "苓雅區", "鹽埕區", "鼓山區",
            "旗津區", "前鎮區", "三民區", "楠梓區", "小港區", "左營區", "仁武區", "大社區", "岡山區", "路竹區", "阿蓮區", "田寮區", "燕巢區",
            "橋頭區", "梓官區", "彌陀區", "永安區", "湖內區", "鳳山區", "大寮區", "林園區", "鳥松區",
            "大樹區", "旗山區", "美濃區", "六龜區", "內門區", "杉林區", "甲仙區", "桃源區", "三民區", "那瑪夏區", "茂林區", "茄萣區"}, {"屏東市", "三地門鄉", "霧臺鄉", "瑪家鄉", "九如鄉", "里港鄉", "高樹鄉",
            "鹽埔鄉", "長治鄉", "麟洛鄉", "竹田鄉", "內埔鄉", "萬丹鄉", "潮州鎮", "泰武鄉", "來義鄉",
            "萬巒鄉", "嵌頂鄉", "新埤鄉", "南州鄉", "林邊鄉", "東港鎮", "琉球鄉", "佳冬鄉", "新園鄉",
            "枋寮鄉", "枋山鄉", "春日鄉", "獅子鄉", "車城鄉", "牡丹鄉", "恆春鎮", "滿州鄉"}, {"臺東市", "綠島鄉", "蘭嶼鄉", "延平鄉", "卑南鄉", "鹿野鄉", "關山鎮",
            "海端鄉", "池上鄉", "東河鄉", "成功鎮", "長濱鄉", "太麻里鄉", "金峰鄉", "大武鄉", "達仁鄉"}, {"花蓮市", "新城鄉", "秀林鄉", "吉安鄉", "壽豐鄉", "鳳林鎮", "光復鄉",
            "豐濱鄉", "瑞穗鄉", "萬榮鄉", "玉里鎮", "卓溪鄉", "富里鄉"}, {"宜蘭市", "頭城鎮", "礁溪鄉", "壯圍鄉", "員山鄉", "羅東鎮", "三星鄉", "大同鄉", "五結鄉", "冬山鄉", "蘇澳鎮", "南澳鄉"}, {"馬公市", "西嶼鄉", "望安鄉", "七美鄉", "白沙鄉", "湖西鄉"}, {"金沙鎮", "金湖鎮", "金寧鄉", "金城鎮", "烈嶼鄉", "烏坵鄉"}, {"南竿鄉", "北竿鄉", "莒光鄉", "東引"}};
    //private String[] area_defult={"仁愛區","信義區","中正區","中山區","安樂區","暖暖區","七堵區"};
    private String[] area_defult = {"請選擇"};
    private int[][] area_value = {{-1}, {1, 2, 3, 4, 5, 6, 7}, {8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19}, {20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48}, {49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61}, {62, 63, 64}, {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77}, {78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95}, {96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124}, {125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150}, {151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163}, {164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183}, {184, 185}, {186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203}, {204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240}, {241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255, 256, 257, 258, 259, 260, 261, 262, 263, 264, 265, 266, 267, 268, 269, 270, 271, 272, 273, 274, 275, 276, 277, 278}, {279, 280, 281, 282, 283, 284, 285, 286, 287, 288, 289, 290, 291, 292, 293, 294, 295, 296, 297, 298, 299, 300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311}, {312, 313, 314, 315, 316, 317, 318, 319, 320, 321, 322, 323, 324, 325, 326, 327}, {328, 329, 330, 331, 332, 333, 334, 335, 336, 337, 338, 339, 340}, {341, 342, 343, 344, 345, 346, 347, 348, 349, 350, 351, 352}, {353, 354, 355, 356, 357, 358}, {359, 360, 361, 362, 363, 364}, {365, 366, 367, 368}};
    private int[] city_value = {-1, 1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 17, 19, 20, 21, 22, 23, 24, 25};
    private String[] country_list_name;
    private String[] country_list_id;
    private String[] join_grade_list_name;
    private String[] join_grade_list_id;
    //private String[] person_data={"個人","法人","外國人"};
    private String[] person_data = {"個人", "法人"};
    private String[] person_data_value = {"1", "2", "3"};
    private String[] sex = {"男", "女"};
    private String[] sex_value = {"1", "0"};
    private ArrayAdapter<String> listAdapter;
    private ArrayAdapter<String> listAdapter_person_data, listAdapter_sex;
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String, String>> list;
    private HashMap<String, String> item;
    private Context mContext;
    private String ip, folder;
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private EditText birth_date, boss_id, mb_name, cellphone, add1, add2, add3, bank_no, bank_ac;
    private ImageView phone_info;
    private TextView  true_intro_name , intro_name, line_kind;
    private EditText true_intro_no;
    private EditText intro_no;
    private Button auto_set_linel, auto_set_liner, save_mbst;
    private String auto_true_intro_no, chk_boss_id_res = "0";
    private int country_select = -1, Bonus_Arr_Title_Select = 0, person_data_select = -1, sex_select = -1, city1_select, city2_select, city3_select, area1_select, area2_select, area3_select, join_grade_select = -1;
    private CheckBox same_add1, same_add2, same_add3;
    private Context context;
    private TextView see_rule, join_prod_comp, join_prod_pv, join_prod_memo;
    private String[] prod_no_list, prod_name_list, prod_price_list, prod_pv_list, prod_memo_list;
    private ArrayAdapter prod_list;
    private String[] Bonus_Arr_Title = {"e錢包", "匯款"};
    private EditText mJoin_Data_Email_Edit;
    private Spinner mJoin_Bonus_Spinner;
    private ArrayAdapter<String> mJoin_Bonus_Spinner_Adapter;
    private Button mAuto_Sort;
    private String Lind_kind_tmp;
    private String from = "";
    private LinearLayout mLinearLyaout_1,mLinearLyaout_2,mLinearLyaout_3,mLinearLyaout_4, mLinearLyaout_bank_table;
    String[] bank_id;
    final Calendar c = Calendar.getInstance();
    final int year_ago_18 = c.get(Calendar.YEAR)-18;
    int mb_years_old = 0;

    boolean chk_boss_id = false;
    boolean chk_boss_id3 = false;

    TextView mTextView_Join_Prod_First , mTextView_Join_Prod_First_PV , mTextView_Join_Prod_First_Price;
    String Join_Prod_No_First = "";
    String Join_Prod_PV_First = "";
    String Join_Prod_Price_First = "";

    //---imagedialog on fainet
    private Picture_GCMD mGCMD = new Picture_GCMD();
    private Uri photoUri;
    private ImageView Dialogtmp;
    private boolean bank_boolean = false;
    private boolean bir_boolean = false ;
    private boolean  mChange_Option_Dialoge = false;
    private ImageView bank_img;
    private ImageView birthday_img;
    private String mPath="";

    public join_data_qrcode () {
        mGCMD.FileCreate();
    }

    protected void onStart() {
        super.onStart();
        net_check();

    }

    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    void net_check() {
        runnable = new Runnable() {
            @Override
            public void run() {

                // TODO Auto-generated method stub
                ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = CM.getActiveNetworkInfo();
                if (info == null || !info.isAvailable()) { //判斷是否有網路
                    handler.postDelayed(this, 2000000);
                    new AlertDialog.Builder(join_data_qrcode.this)
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
                } else {
                    handler.postDelayed(this, 2000);
                }
            }
        };
        handler.postDelayed(runnable, 500);//每兩秒執行一次runnable.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final Drawable upArrow = getResources().getDrawable(R.drawable.home_2);
        getActionBar().setHomeAsUpIndicator(upArrow);
        setContentView(R.layout.activity_join_data_qrcode);
        config = readFromFile("client_config");
        try {
            login_id = new JSONObject(config).getString("login_id");
        } catch (Exception e) {

        }
        Bundle bundle =this.getIntent().getExtras();
        if(bundle.getString("true_intro_no") !=null ){
            login_id = bundle.getString("true_intro_no");
            from = bundle.getString("from");
        }
        Log.d("login_id", login_id);

        InitialSetting();
        see_rule.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(join_data_qrcode.this, join_information.class);
                Bundle bundle2 = new Bundle();
                intent.putExtras(bundle2);
                startActivity(intent);
            }
        });
        load_config();
        get_country_thread();
        get_join_grade_thread();
        get_true_intro_nmae();

        get_bank_thread();

        bank_ac =(EditText) findViewById(R.id.bank_ac);
        bank_no =(EditText) findViewById(R.id.bank_no);

        mLinearLyaout_1 = (LinearLayout) findViewById(R.id.layout_intro_1_qr);
        mLinearLyaout_2 = (LinearLayout) findViewById(R.id.layout_intro_2_qr);
        mLinearLyaout_3 = (LinearLayout) findViewById(R.id.layout_intro_3_qr);
        mLinearLyaout_4 = (LinearLayout) findViewById(R.id.layout_intro_4_qr);
        mLinearLyaout_bank_table = (LinearLayout) findViewById(R.id.bank_table);

        listAdapter_person_data = new ArrayAdapter<String>(this, R.layout.myspinner, person_data);
        listAdapter_person_data.setDropDownViewResource(R.layout.myspinner);
        person_data_spinner.setAdapter(listAdapter_person_data);
        listAdapter_sex = new ArrayAdapter<String>(this, R.layout.myspinner, sex);
        listAdapter_sex.setDropDownViewResource(R.layout.myspinner);
        sex_spinner.setAdapter(listAdapter_sex);


        city1_adapter = new ArrayAdapter<String>(this, R.layout.myspinner, city_text);
        city1_adapter.setDropDownViewResource(R.layout.myspinner);
        city1 = (Spinner) findViewById(R.id.city1);
        city1.setAdapter(city1_adapter);
        area1_adapter = new ArrayAdapter<String>(this, R.layout.myspinner, area_defult);
        area1_adapter.setDropDownViewResource(R.layout.myspinner);
        area1 = (Spinner) findViewById(R.id.area1);
        area1.setAdapter(area1_adapter);
        city1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                //讀取第一個下拉選單是選擇第幾個
                int pos = city1.getSelectedItemPosition();
                city1_select = pos;
                //重新產生新的Adapter，用的是二維陣列type2[pos]
                area1_adapter = new ArrayAdapter<String>(context, R.layout.myspinner, area[pos]);
                //載入第二個下拉選單Spinner
                area1.setAdapter(area1_adapter);

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
            }
        });

        //=====//
        city2_adapter = new ArrayAdapter<String>(this, R.layout.myspinner, city_text);
        city2_adapter.setDropDownViewResource(R.layout.myspinner);
        city2 = (Spinner) findViewById(R.id.city2);
        city2.setAdapter(city2_adapter);
        area2_adapter = new ArrayAdapter<String>(this, R.layout.myspinner, area_defult);
        area2_adapter.setDropDownViewResource(R.layout.myspinner);
        area2 = (Spinner) findViewById(R.id.area2);
        area2.setAdapter(area2_adapter);
        city2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                if(!chk_boss_id){
                    //讀取第一個下拉選單是選擇第幾個
                    int pos = city2.getSelectedItemPosition();
                    city2_select = pos;
                    //重新產生新的Adapter，用的是二維陣列type2[pos]
                    area2_adapter = new ArrayAdapter<String>(context, R.layout.myspinner, area[pos]);
                    //載入第二個下拉選單Spinner
                    area2.setAdapter(area2_adapter);
                    if (same_add1.isChecked() && city1_select == city2_select) {
                        area2.setSelection(area1_select);
                    }
                } else {
                    chk_boss_id = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        area2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                //讀取第一個下拉選單是選擇第幾個
                int pos = area2.getSelectedItemPosition();
                area2_select = pos;
            }
        });
        same_add1 = (CheckBox) findViewById(R.id.same_add1);
        same_add1.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //判斷CheckBox是否有勾選，同mCheckBox.isChecked()
                if (isChecked) {
                    if (city1_select == city2_select) {
                        area2.setSelection(area1_select);
                    }

                    city2.setSelection(city1_select);
                    add2.setText(add1.getText().toString());
                }
            }
        });
        same_add2 = (CheckBox) findViewById(R.id.same_add2);
        same_add3 = (CheckBox) findViewById(R.id.same_add3);
        same_add2.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //判斷CheckBox是否有勾選，同mCheckBox.isChecked()
                if (isChecked) {
                    same_add3.setChecked(false);
                    if (city1_select == city3_select) {
                        area3.setSelection(area1_select);
                    }
                    city3.setSelection(city1_select);
                    add3.setText(add1.getText().toString());
                }
            }
        });

        same_add3.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //判斷CheckBox是否有勾選，同mCheckBox.isChecked()
                if (isChecked) {
                    same_add2.setChecked(false);
                    if (city2_select == city3_select) {
                        area3.setSelection(area2_select);
                    }
                    city3.setSelection(city2_select);

                    add3.setText(add2.getText().toString());
                }
            }
        });

        birth_date = (EditText) findViewById(R.id.birth_date);
        birth_date.setInputType(InputType.TYPE_NULL);
        birth_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                // TODO Auto-generated method stub
                if (hasFocus) {
                    new DatePickerDialog(join_data_qrcode.this, new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // TODO Auto-generated method stub
                            if(year_ago_18 >= year){
                                Log.e("YEARS:",""+year);
                                if(year_ago_18 == year){
                                    if(c.get(Calendar.MONTH) >= monthOfYear){
                                        if(c.get(Calendar.DAY_OF_MONTH) >= dayOfMonth){
                                            String mm;
                                            if (monthOfYear + 1 < 10) {
                                                mm = "0" + (monthOfYear + 1);
                                            } else {
                                                mm = String.valueOf(monthOfYear + 1);

                                            }
                                            String dd;

                                            if (dayOfMonth < 10) {
                                                dd = "0" + (dayOfMonth);
                                            } else {
                                                dd = String.valueOf(dayOfMonth);

                                            }
                                            birth_date.setText(year + "-" + String.valueOf(mm) + "-" + String.valueOf(dd));
                                        }else{
                                            Toast.makeText(join_data_qrcode.this,"年齡須為18歲以上",Toast.LENGTH_LONG).show();
                                        }
                                    }else{
                                        Toast.makeText(join_data_qrcode.this,"年齡須為18歲以上",Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    String mm;
                                    if (monthOfYear + 1 < 10) {
                                        mm = "0" + (monthOfYear + 1);
                                    } else {
                                        mm = String.valueOf(monthOfYear + 1);

                                    }
                                    String dd;

                                    if (dayOfMonth < 10) {
                                        dd = "0" + (dayOfMonth);
                                    } else {
                                        dd = String.valueOf(dayOfMonth);

                                    }
                                    mb_years_old = c.get(Calendar.YEAR) - year;
                                    Toast.makeText(join_data_qrcode.this,""+mb_years_old,Toast.LENGTH_LONG).show();
                                    birth_date.setText(year + "-" + String.valueOf(mm) + "-" + String.valueOf(dd));
                                }
                            }else{
                                Toast.makeText(join_data_qrcode.this,"年齡須為18歲以上",Toast.LENGTH_LONG).show();
                            }
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

                }
            }
        });

        birth_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                new DatePickerDialog(join_data_qrcode.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        if(year_ago_18 >= year){
                            Log.e("YEARS:",""+year);
                            if(year_ago_18 == year){
                                if(c.get(Calendar.MONTH) >= monthOfYear){
                                    if(c.get(Calendar.DAY_OF_MONTH) >= dayOfMonth){
                                        String mm;
                                        if (monthOfYear + 1 < 10) {
                                            mm = "0" + (monthOfYear + 1);
                                        } else {
                                            mm = String.valueOf(monthOfYear + 1);

                                        }
                                        String dd;

                                        if (dayOfMonth < 10) {
                                            dd = "0" + (dayOfMonth);
                                        } else {
                                            dd = String.valueOf(dayOfMonth);

                                        }
                                        birth_date.setText(year + "-" + String.valueOf(mm) + "-" + String.valueOf(dd));
                                    }else{
                                        Toast.makeText(join_data_qrcode.this,"年齡須為18歲以上",Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    Toast.makeText(join_data_qrcode.this,"年齡須為18歲以上",Toast.LENGTH_LONG).show();
                                }
                            }else{
                                String mm;
                                if (monthOfYear + 1 < 10) {
                                    mm = "0" + (monthOfYear + 1);
                                } else {
                                    mm = String.valueOf(monthOfYear + 1);

                                }
                                String dd;

                                if (dayOfMonth < 10) {
                                    dd = "0" + (dayOfMonth);
                                } else {
                                    dd = String.valueOf(dayOfMonth);

                                }
                                mb_years_old = c.get(Calendar.YEAR) - year;
                                Toast.makeText(join_data_qrcode.this,""+mb_years_old,Toast.LENGTH_LONG).show();
                                birth_date.setText(year + "-" + String.valueOf(mm) + "-" + String.valueOf(dd));
                            }
                        }else{
                            Toast.makeText(join_data_qrcode.this,"年齡須為18歲以上",Toast.LENGTH_LONG).show();
                        }

                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        phone_info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "※無手機號碼者，請填入可聯絡之電話含區碼9~10碼。輸入格式不含()、及 # 。例：0223456789", Toast.LENGTH_LONG).show();
            }
        });
        auto_set_linel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //line_kind.setText("左");
                //Lind_kind_tmp="1";)
                if(!intro_no.getText().toString().equals("")){
                    Chk_line_kind_thread("左","1");
                } else {
                    Toast.makeText(join_data_qrcode.this,"安置人編號為空值請重新輸入",Toast.LENGTH_SHORT).show();
                }

                //way = "1";
                //get_line_kind_thread();
            }
        });
        auto_set_liner.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //line_kind.setText("右");
                //Lind_kind_tmp="2";
                if(!intro_no.getText().toString().equals("")){
                    Chk_line_kind_thread("右","2");
                } else {
                    Toast.makeText(join_data_qrcode.this,"安置人編號為空值請重新輸入",Toast.LENGTH_SHORT).show();
                }
                //way = "2";
                //get_line_kind_thread();
            }
        });
        save_mbst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_mbst_s();
            }
        });
        //入會職級
        join_grade_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                join_grade_select = position;
               // get_join_prod_thread();
                Log.d("country_select", String.valueOf(country_select));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        //國別
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                country_select = position;
                if (spinner.getSelectedItem().toString().equals("台灣")) {
                    city1.setVisibility(View.VISIBLE);
                    city2.setVisibility(View.VISIBLE);
                    area1.setVisibility(View.VISIBLE);
                    city2.setVisibility(View.VISIBLE);
                    area2.setVisibility(View.VISIBLE);
                    city3.setVisibility(View.VISIBLE);
                    area3.setVisibility(View.VISIBLE);
                    mLinearLyaout_bank_table.setVisibility(View.VISIBLE);
                } else {
                    city1.setVisibility(View.GONE);
                    city2.setVisibility(View.GONE);
                    area1.setVisibility(View.GONE);
                    area2.setVisibility(View.GONE);
                    city3.setVisibility(View.GONE);
                    area3.setVisibility(View.GONE);
                    mLinearLyaout_bank_table.setVisibility(View.VISIBLE);
                    city1.setSelection(0);
                    city2.setSelection(0);
                    area1.setSelection(0);
                    area2.setSelection(0);
                    city3.setSelection(0);
                    area3.setSelection(0);

                }
                //get_join_prod_thread();
                Log.d("country_select", String.valueOf(country_select));

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        //身份別

        person_data_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {

                TextView boss_title = (TextView) findViewById(R.id.boss_title);
                TextView name_title = (TextView) findViewById(R.id.name_title);
                person_data_select = position;

                if (person_data_select == 0) {//個人
                    boss_title.setText("身分證號");
                    name_title.setText("姓名");
                }

                if (person_data_select == 1) {//法人
                    boss_title.setText("統一編號");
                    name_title.setText("公司名");

                }

                /*if(person_data_select==2){//外國人
                    boss_title.setText("身分證號");
                    name_title.setText("姓名");

                }*/

                Log.d("person_data_select", String.valueOf(person_data_select));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        //性別
        sex_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                sex_select = position;
                Log.d("person_data_select", String.valueOf(person_data_select));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        InitialSettingLayoutConnect();

    }

    private void InitialSettingLayoutConnect() {

        mAuto_Sort = (Button)findViewById(R.id.auto_sort);
        mJoin_Data_Email_Edit = (EditText) findViewById(R.id.Join_Data_Email_Edit);
        mJoin_Bonus_Spinner_Adapter = new ArrayAdapter<String>(this, R.layout.myspinner, Bonus_Arr_Title);
        mJoin_Bonus_Spinner_Adapter.setDropDownViewResource(R.layout.myspinner);
        mJoin_Bonus_Spinner = (Spinner) findViewById(R.id.Join_Bonus_Spinner);
        mJoin_Bonus_Spinner.setAdapter(mJoin_Bonus_Spinner_Adapter);

        mAuto_Sort.setOnClickListener(this);
        SpinnerOnSelectInit();

    }

    private void SpinnerOnSelectInit() {
        mJoin_Bonus_Spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {

                Bonus_Arr_Title_Select = position;
                Log.d(TAG, String.valueOf(mJoin_Bonus_Spinner_Adapter.getItem(position)));
                Log.d(TAG,"Bonus_Arr_Title_Select"+Bonus_Arr_Title_Select);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        //-------
        city3_adapter = new ArrayAdapter<String>(this, R.layout.myspinner, city_text);
        city3_adapter.setDropDownViewResource(R.layout.myspinner);
        city3 = (Spinner) findViewById(R.id.city3);
        city3.setAdapter(city3_adapter);
        area3_adapter = new ArrayAdapter<String>(this, R.layout.myspinner, area_defult);
        area3_adapter.setDropDownViewResource(R.layout.myspinner);
        area3 = (Spinner) findViewById(R.id.area3);
        area3.setAdapter(area3_adapter);
        city3.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                if (!chk_boss_id3) {
                    //讀取第一個下拉選單是選擇第幾個
                    int pos = city3.getSelectedItemPosition();
                    city3_select = pos;
                    //重新產生新的Adapter，用的是二維陣列type2[pos]
                    area3_adapter = new ArrayAdapter<String>(context, R.layout.myspinner, area[pos]);
                    //載入第二個下拉選單Spinner
                    area3.setAdapter(area3_adapter);
                    if (same_add2.isChecked() && city1_select == city3_select) {
                        area3.setSelection(area1_select);
                    }
                    if (same_add3.isChecked() && city2_select == city3_select) {
                        area3.setSelection(area2_select);
                    }
                } else {
                    chk_boss_id3 = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        area3.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                //讀取第一個下拉選單是選擇第幾個
                int pos = area3.getSelectedItemPosition();
                area3_select = pos;
            }
        });
        //-----
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.auto_sort:
                Auto_SortClick();
                Log.d(TAG,"Sort");
                break;
            case R.id.bank_img:
                mChange_Option_Dialoge = mGCMD.Dialog_Bank ;
                if(!bank_boolean){
                    AlertDialog_Upload_Camera(join_data_qrcode.this, bank_img);
                    Log.d(mGCMD.TAG,"bank_img");
                } else {
                    AlertDialog_Upload_Camera(join_data_qrcode.this, mGCMD.Upload_Picture_Menu_tmp_Bitmap,bank_img);
                }
                break;
            case R.id.birthday_img:
                mChange_Option_Dialoge = mGCMD.Dialog_Birthday ;
                if(!bir_boolean){
                    AlertDialog_Upload_Camera(join_data_qrcode.this, birthday_img);
                    Log.d(mGCMD.TAG,"bank_img");
                } else {
                    AlertDialog_Upload_Camera(join_data_qrcode.this, mGCMD.Upload_Picture_Menu_Bir_tmp_Bitmap,birthday_img);
                }
                Log.d(mGCMD.TAG,"birthday_img");
                break;
        }
    }

    private void Auto_SortClick(){
        get_line_kind_thread();
    }

    private void InitialSetting() {
        context = this;
        mContext = this.getApplicationContext();

        mTextView_Join_Prod_First = (TextView) findViewById(R.id.textview_join_main);
        mTextView_Join_Prod_First_PV = (TextView) findViewById(R.id.textview_join_main_pv);
        mTextView_Join_Prod_First_Price = (TextView) findViewById(R.id.textView_join_main_price);

        join_prod_lin = (LinearLayout) findViewById(R.id.join_prod);
        join_prod_spinner = (Spinner) findViewById(R.id.join_prod_name);
        spinner = (Spinner) findViewById(R.id.country);
        join_grade_spinner = (Spinner) findViewById(R.id.join_grade);
        person_data_spinner = (Spinner) findViewById(R.id.person);
        sex_spinner = (Spinner) findViewById(R.id.sex);
        birth_date = (EditText) findViewById(R.id.birth_date);
        boss_id = (EditText) findViewById(R.id.boss_id);
        boss_id.setTransformationMethod(new AllCapTransformationMethod());
        mb_name = (EditText) findViewById(R.id.mb_name);
        cellphone = (EditText) findViewById(R.id.cellphone);
        phone_info = (ImageView) findViewById(R.id.phone_info);
        join_prod_comp = (TextView) findViewById(R.id.join_prod_comp);
        join_prod_memo = (TextView) findViewById(R.id.join_prod_memo);
        join_prod_pv = (TextView) findViewById(R.id.join_prod_pv);
        true_intro_no = (EditText) findViewById(R.id.true_intro_no);
        true_intro_name = (TextView) findViewById(R.id.true_intro_name);
        intro_no = (EditText) findViewById(R.id.intro_no);
        intro_no.setTransformationMethod(new AllCapTransformationMethod());
        intro_name = (TextView) findViewById(R.id.intro_name);
        line_kind = (TextView) findViewById(R.id.line_kind);
        auto_set_linel = (Button) findViewById(R.id.auto_set_linel);
        auto_set_liner = (Button) findViewById(R.id.auto_set_liner);
        add1 = (EditText) findViewById(R.id.add1);
        add2 = (EditText) findViewById(R.id.add2);
        add3 = (EditText) findViewById(R.id.add3);
        save_mbst = (Button) findViewById(R.id.save_mbst);
        see_rule = (TextView) findViewById(R.id.see_rule);

        bank_img = (ImageView) findViewById(R.id.bank_img);
        bank_img.setOnClickListener(this);
        birthday_img = (ImageView) findViewById(R.id.birthday_img);
        birthday_img.setOnClickListener(this);
        InitImgOnDialogOption();


        true_intro_no.setTransformationMethod(new AllCapTransformationMethod());

        true_intro_no.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (true_intro_no.getText().toString().length() != 0) {
                    Log.d("true_intro", "setOnEditorActionListener");
                    get_true_intro_no();
                }
                return false;
            }
        });

        true_intro_no.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    if (true_intro_no.getText().toString().length() != 0) {
                        Log.d("true_intro", "setOnFocusChangeListener");
                        get_true_intro_no();
                    }
                }
            }
        });

        intro_no.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(intro_no.getText().toString().length() != 0){
                    Log.d("ddasd", "setOnEditorActionListener");
                    get_intro_no();
                }
                return false;
            }
        });
        intro_no.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus == false){
                    if(intro_no.getText().toString().length() != 0){
                        Log.d("ddasd", "setOnFocusChangeListener");
                        get_intro_no();
                    }
                }
            }
        });
        boss_id.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(intro_no.getText().toString().length() != 0){
                    Log.d("ddasd", "setOnEditorActionListener");
                    chk_boss_id_thread();
                }
                return false;
            }
        });
        boss_id.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus == false){
                    if(intro_no.getText().toString().length() != 0){
                        Log.d("ddasd", "setOnFocusChangeListener");
                        chk_boss_id_thread();
                    }
                }
            }
        });
        KeyboardVisibilityEvent.setEventListener(this,new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (!isOpen) {
                            if (intro_no.getText().toString().length() != 0) {
                                Log.d("ddasd", "setEventListener");
                                get_intro_no();
                            }
                            if (true_intro_no.getText().toString().length() != 0) {
                                get_true_intro_no();
                            }
                            if (boss_id.isEnabled() && boss_id.getText().toString().length() != 0) {
                                chk_boss_id_thread();
                            }
                        }
                    }
                });
    }

    //------------------------------------------------------------------------sheng

    public void get_true_intro_no() {
        mThread = new HandlerThread("get_true_intro_no");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_true_intro_no_data("get_true_intro_no");

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        try {
                            get_true_intro_no_res(jsonString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private String get_true_intro_no_data(String cmd) {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/join.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", cmd));
            nameValuePairs.add(new BasicNameValuePair("true_intro_no", true_intro_no.getText().toString().toUpperCase()));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

    public final void get_true_intro_no_res(String input) throws JSONException {
        Log.d("get_intro_no", input);
        if(input.substring(0,4).equals("none")){
            Toast.makeText(join_data_qrcode.this,"查無此會員編號",Toast.LENGTH_LONG).show();
            mLinearLyaout_1.setVisibility(View.GONE);
            mLinearLyaout_2.setVisibility(View.GONE);
            mLinearLyaout_3.setVisibility(View.GONE);
            mLinearLyaout_4.setVisibility(View.GONE);
            true_intro_no.setText("");
            true_intro_name.setText("");
        }else{
            JSONObject JSObject = new JSONObject(input);
            true_intro_name.setText(JSObject.getString("mb_name"));
            mLinearLyaout_1.setVisibility(View.VISIBLE);
            mLinearLyaout_2.setVisibility(View.VISIBLE);
            mLinearLyaout_3.setVisibility(View.VISIBLE);
            mLinearLyaout_4.setVisibility(View.VISIBLE);
        }
    }

    //------------------------------------------------------------------------sheng

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
        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("若返回上一頁，您輸入的資料將被清除，確定前往嗎?");
                builder.setCancelable(false);

                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (from.equals("login")) {
                            Intent i = new Intent(join_data_qrcode.this, login.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        } else {
                            finish();
                        }
                    }
                });

                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                //finish();
                AlertDialog alert = builder.create();
                alert.show();
                break;

            default:
                break;

        }

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
            //Toast.makeText(getApplicationContext(),ip, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void get_country_thread() {
        mThread = new HandlerThread("get_join_country");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_country_data("get_join_country");

                mUI_Handler.post(new Runnable() {
                    public void run() {

                        get_country_res(jsonString);
                        get_join_prod_thread();
                        get_join_prod_first_thread();
                    }
                });
            }
        });
    }

    public void get_join_grade_thread() {
        mThread = new HandlerThread("get_join_grade");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_join_grade_data("get_join_grade");

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        get_join_grade_res(jsonString);
                    }
                });
            }
        });
    }

    public void get_join_prod_thread() {
        mThread = new HandlerThread("get_join_prod");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_join_prod_data("get_join_prod");

                mUI_Handler.post(new Runnable() {
                    public void run() {

                        get_join_prod_res(jsonString);
                    }
                });
            }
        });
    }

    public void get_true_intro_nmae() {
        mThread = new HandlerThread("get_true_intro_nmae");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_true_intro_nmae_data("get_true_intro_nmae");

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        get_true_intro_nmae_res(jsonString);
                        get_true_intro_no();
                    }
                });
            }
        });
    }

    //------------------------------------------------------------------------sheng
    public void get_intro_no() {
        mThread = new HandlerThread("get_intro_no");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_intro_no_data("get_intro_no");

                mUI_Handler.post(new Runnable() {
                    public void run() {
                        try {
                            get_intro_no_res(jsonString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private String get_intro_no_data(String cmd) {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/join.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", cmd));
            nameValuePairs.add(new BasicNameValuePair("intro_no", intro_no.getText().toString().toUpperCase()));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

    public final void get_intro_no_res(String input) throws JSONException {
        Log.d("get_intro_no", input);
        if(input.substring(0,4).equals("none")){
            Toast.makeText(join_data_qrcode.this,"查無此會員編號",Toast.LENGTH_LONG).show();
            intro_no.setText("");
            intro_name.setText("");
            line_kind.setText("");
        }else{
            JSONObject JSObject = new JSONObject(input);
            intro_name.setText(""+JSObject.getString("mb_name"));
            if(JSObject.getString("intro_line").equals("1")){
                line_kind.setText("左");
            }else if(JSObject.getString("intro_line").equals("2")){
                line_kind.setText("右");
            }
        }
    }

    //------------------------------------------------------------------------sheng
    private String get_country_data(String cmd) {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/join.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", cmd));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

    private String get_join_grade_data(String cmd) {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/join.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", cmd));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

    private String get_join_prod_data(String cmd) {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/join.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", cmd));
            nameValuePairs.add(new BasicNameValuePair("join_grade", "0"));
            nameValuePairs.add(new BasicNameValuePair("country", "tw"));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

    private String get_true_intro_nmae_data(String cmd) {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/join.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", cmd));
            nameValuePairs.add(new BasicNameValuePair("mb_no", login_id));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

    public final void get_true_intro_nmae_res(String input) {
        Log.d("get_true_intro_nmae_res", input);
        TextView true_intro_no = (TextView) findViewById(R.id.true_intro_no);
        TextView true_intro_name = (TextView) findViewById(R.id.true_intro_name);
        true_intro_name.setText(input);
        true_intro_no.setText(login_id);

    }

    public final void get_country_res(String input) {
        try {
            Log.d("get_country_res", input);
            JSONArray jsonArray = new JSONArray(input);

            country_list_name = new String[jsonArray.length()];
            country_list_id = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                country_list_name[i] = jsonData.getString("name");
                country_list_id[i] = jsonData.getString("code");
            }
            listAdapter = new ArrayAdapter<String>(this, R.layout.myspinner, country_list_name);
            listAdapter.setDropDownViewResource(R.layout.myspinner);

            spinner.setAdapter(listAdapter);

        } catch (JSONException e) {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Toast.makeText(getApplication(), "" + e.toString(), Toast.LENGTH_LONG).show();
            Log.e("json", e.toString());
        }
    }

    public final void get_join_grade_res(String input) {
        try {
            Log.d("get_join_grade_res", input);
            JSONArray jsonArray = new JSONArray(input);

            join_grade_list_name = new String[jsonArray.length() + 1];
            join_grade_list_id = new String[jsonArray.length() + 1];
            join_grade_list_name[0] = "請選擇";
            join_grade_list_id[0] = "-1";
            for (int i = 1; i < jsonArray.length() + 1; i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i - 1);
                join_grade_list_name[i] = jsonData.getString("name");
                join_grade_list_id[i] = jsonData.getString("no");
            }
            listAdapter = new ArrayAdapter<String>(this, R.layout.myspinner, join_grade_list_name);
            listAdapter.setDropDownViewResource(R.layout.myspinner);

            join_grade_spinner.setAdapter(listAdapter);


        } catch (JSONException e) {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Toast.makeText(getApplication(), "" + e.toString(), Toast.LENGTH_LONG).show();
            Log.e("json", e.toString());

        }

    }

    public final void get_join_prod_res(String input) {
        //Log.d("account",input);
       /* try
        {
            if(input.equals("1\n")) {
                join_prod_lin.setVisibility(View.GONE);
            }else{
                join_prod_lin.setVisibility(View.VISIBLE);
                JSONArray jsonArray = new JSONArray(input);

                list = new ArrayList<HashMap<String, String>>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    item = new HashMap<String, String>();
                    item.put("prod_name", jsonData.getString("prod_name"));
                    item.put("prod_pv",  jsonData.getString("pv"));
                    item.put("prod_price", jsonData.getString("comp_price") );
                    list.add(item);
                }
                if (getApplication() == null) {
                    return;
                }
                adapter = new MySimpleAdapter(getApplication(), list, R.layout.join_prod_list, new String[]{"prod_name", "prod_pv", "prod_price"}, new int[]{R.id.join_prod_name, R.id.join_prod_pv, R.id.join_prod_price});
                setListAdapter(adapter);
            }
        }
        catch (JSONException e)
        {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();

            Log.e("json", e.toString());
        }*/

        try {
            if (input.equals("1\n")) {
                join_prod_lin.setVisibility(View.GONE);
            } else {
                join_prod_lin.setVisibility(View.VISIBLE);
                JSONArray jsonArray = new JSONArray(input);
                prod_no_list = new String[jsonArray.length()];
                prod_name_list = new String[jsonArray.length()];
                prod_pv_list = new String[jsonArray.length()];
                prod_price_list = new String[jsonArray.length()];
                prod_memo_list = new String[jsonArray.length()];
                //WEEK_NO[0]="請選擇";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    if (i == 0) {
                        join_prod_comp.setText(jsonData.getString("comp_price"));
                        join_prod_pv.setText(jsonData.getString("pv"));
                        join_prod_no_select = jsonData.getString("prod_no");
                        if (jsonData.getString("join_memo").length() > 0) {
                            join_prod_memo.setVisibility(View.VISIBLE);
                            join_prod_memo.setText(jsonData.getString("join_memo"));
                        } else {
                            join_prod_memo.setVisibility(View.GONE);
                        }
                    }

                    prod_no_list[i] = jsonData.getString("prod_no");
                    prod_name_list[i] = jsonData.getString("prod_name");
                    prod_pv_list[i] = jsonData.getString("pv");
                    prod_price_list[i] = jsonData.getString("comp_price");
                    prod_memo_list[i] = jsonData.getString("join_memo");
                }
                prod_list = new ArrayAdapter<String>(join_data_qrcode.this, R.layout.join_prod_list2, prod_name_list);
                join_prod_spinner.setAdapter(prod_list);
                join_prod_spinner.setSelection(0, true);

                join_prod_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        join_prod_no_select = prod_no_list[position];
                        join_prod_comp.setText(prod_price_list[position]);
                        join_prod_pv.setText(prod_pv_list[position]);
                        if (prod_memo_list[position].length() > 0) {
                            join_prod_memo.setVisibility(View.VISIBLE);
                            join_prod_memo.setText(prod_memo_list[position]);
                        } else {
                            join_prod_memo.setVisibility(View.GONE);
                        }
                        Log.d("weekweek", "onItemSelected ");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });
            }
        } catch (JSONException e) {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Toast.makeText(getApplication(), "" + e.toString(), Toast.LENGTH_LONG).show();
            Log.e("json", e.toString());
        }

    }

    //----------------------------------------------------------------------------------------
    public void get_bank_thread() {
        mThread = new HandlerThread("get_bank_thread");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_bank_data("get_bank_spinner");
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        try {
                            get_bank_res(jsonString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
            HttpPost post = new HttpPost("http://"+ip+"/"+folder+"/join.php");
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
    public final void get_bank_res(String input) throws JSONException {
        JSONArray jsonArray = new JSONArray(input);
        String[] bank_name = new String[jsonArray.length()];
        bank_id = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jsonData = jsonArray.getJSONObject(i);
            bank_name[i]=jsonData.getString("give_method");
            bank_id[i]=jsonData.getString("give_method_no");
        }

        bank_adapter = new ArrayAdapter<String>(this,R.layout.myspinner,bank_name);
        bank_adapter.setDropDownViewResource(R.layout.myspinner);
        bank = (Spinner) findViewById(R.id.bank) ;
        bank.setAdapter(bank_adapter);
        //Toast.makeText(join_data.this,""+bank.getSelectedItemPosition(),Toast.LENGTH_LONG).show();
    }
    //------------------------------------------------------------------

    public void get_line_kind_thread() {
        mThread = new HandlerThread("get_line_kind");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_line_kind_data("get_line_kind");

                mUI_Handler.post(new Runnable() {
                    public void run() {

                        get_line_kind_res(jsonString);
                    }
                });
            }
        });
    }

    private String get_line_kind_data(String cmd) {
        //int
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/join.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", cmd));
            auto_true_intro_no = true_intro_no.getText().toString();
            Log.d("auto_true_intro_no", auto_true_intro_no);
            nameValuePairs.add(new BasicNameValuePair("auto_true_intro_no", auto_true_intro_no));
            nameValuePairs.add(new BasicNameValuePair("way", way));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

    public final void get_line_kind_res(String input) {
        Log.d(TAG, "get_line_kind_res" + input);
        try {
            JSONArray jsonArray = new JSONArray(input);

            String line_kind2 = "", intro_no2 = "", intro_name2 = "";
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                line_kind2 = jsonData.getString("line_kind");
                intro_no2 = jsonData.getString("no");
                intro_name2 = jsonData.getString("name");
            }
            if (line_kind2.equals("1")) {
                line_kind.setText("左");
                Lind_kind_tmp="1";
            }
            if (line_kind2.equals("2")) {
                line_kind.setText("右");
                Lind_kind_tmp="2";
            }
            line_kind.setTag(line_kind2);
            intro_no.setText(intro_no2);
            intro_name.setText(intro_name2);

        } catch (JSONException e) {
            // TODO 自動產生的 catch 區塊
            e.printStackTrace();
            Toast.makeText(getApplication(), "" + e.toString(), Toast.LENGTH_LONG).show();
            Log.e("json", e.toString());
        }
    }

    //----------------------------------------------------------------------------
    public void Chk_line_kind_thread(final String tmp,final String line) {
        mDialog = ProgressDialog.show(this, "線上入會", "判斷中", true);
        mThread = new HandlerThread("chk_line_kind");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = Chk_line_kind_data("chk_line_kind", line);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        Chk_line_kind_res(jsonString,tmp,line);
                        mDialog.dismiss();
                    }
                });
            }
        });
    }

    private String Chk_line_kind_data(String cmd,String linetmp) {
        //int
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/join.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", cmd));
            auto_true_intro_no = intro_no.getText().toString().toUpperCase();
            Log.d("intro_no", auto_true_intro_no);
            nameValuePairs.add(new BasicNameValuePair("intro_no", auto_true_intro_no));
            nameValuePairs.add(new BasicNameValuePair("line", linetmp));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

    public final void Chk_line_kind_res(String input,String tmp,String line) {
        Log.d(TAG,"Chk_line_kind_res"+input);
        input = input.substring(0,input.length()-1);
        if(input.equals("full")||intro_name.getText().toString().equals("")){
            Toast.makeText(join_data_qrcode.this,"安置人此線滿額 或者 未有此安置人編號",Toast.LENGTH_SHORT).show();
            intro_no.setText("");
            intro_name.setText("");
            line_kind.setText("");
        } else {
            line_kind.setText(tmp);
            Lind_kind_tmp = line;
        }
    }

    void save_mbst_s() {
        Log.d("teeee","save_mbst_s");
        String err_str = "";
        CheckBox agree = (CheckBox) findViewById(R.id.agree);
       /* if (join_grade_spinner.getSelectedItem().toString().equals("請選擇")) {
            err_str += "入會職級 ";
        }*/
        if (boss_id.getText().toString().equals("")) {
            err_str += "身分證 ";

        }
        if (mb_name.getText().toString().equals("")) {
            err_str += "姓名 ";

        }
        if (birth_date.getText().toString().equals("")) {
            err_str += "生日 ";
        }
        /*if(add1.getText().toString().equals("")){
            err_str+="戶籍地址 ";
        }*/
        if (add2.getText().toString().equals("")) {
            err_str += "通訊地址 ";
        }
        if (add3.getText().toString().equals("")) {
            err_str += "送貨地址 ";
        }
        if (cellphone.getText().toString().equals("")) {
            err_str += "行動電話 ";
        }
        /*if(bank_ac.getText().toString().equals("") && country_select ==0){
            err_str+="帳戶名 ";
        }
        if(bank_no.getText().toString().equals("") && country_select ==0){
            err_str+="個人帳號 ";
        }*/
        if (mJoin_Data_Email_Edit.getText().toString().equals("")) {
            err_str += "Email ";
        }
        if (intro_no.getText().toString().equals("")) {
            err_str += "安置人編號 ";
        }
        String [] tmp = mb_name.getText().toString().split("-");
        if (mGCMD.Upload_Picture_Menu_Path_array == null && !tmp[0].equals(bank_ac.getText().toString())){
            err_str += "薪資不同切結 ";
        }
        if (mGCMD.Upload_Picture_Menu_Bir_Path_array == null && mb_years_old<=20){
            err_str += "法定代理人同意 ";
        }
        if (!agree.isChecked()) {
            err_str += "同意約定條款 ";
        }
        if (err_str != "") {
            Toast.makeText(getApplicationContext(), err_str + "未填", Toast.LENGTH_LONG).show();
        }
        /*if ((!spinner.getSelectedItem().toString().equals("台灣")&&Bonus_Arr_Title_Select==1)) {
            err_str +="獎金發放不可選取匯款";
            Toast.makeText(getApplicationContext(), "國別不是台灣的 獎金發放不可選取匯款", Toast.LENGTH_LONG).show();
        }*/

        if(intro_no.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "請按自動排線", Toast.LENGTH_LONG).show();
        }
        if (err_str.equals("")) {
            //chk_boss_id_thread();
            save_mbst.setEnabled(false);
            spinner.setEnabled(false);
            person_data_spinner.setEnabled(false);
            boss_id.setEnabled(false);
            mb_name.setEnabled(false);
            sex_spinner.setEnabled(false);
            birth_date.setEnabled(false);
            city1.setEnabled(false);
            city2.setEnabled(false);
            area1.setEnabled(false);
            area2.setEnabled(false);
            add1.setEnabled(false);
            add2.setEnabled(false);
            cellphone.setEnabled(false);
            auto_set_linel.setEnabled(false);
            auto_set_liner.setEnabled(false);
            bank_ac.setEnabled(false);
            bank_no.setEnabled(false);
            Toast.makeText(getApplicationContext(), "資料儲存中,請勿離開畫面...", Toast.LENGTH_SHORT).show();
            save_mbst_true();
        }

    }

    public void chk_boss_id_thread() {
        mThread = new HandlerThread("chk_boss_id");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = chk_boss_id_data("chk_boss_id");

                mUI_Handler.post(new Runnable() {
                    public void run() {

                        try {
                            chk_boss_id_res(jsonString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private String chk_boss_id_data(String cmd) {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/join.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", cmd));
            String boss = boss_id.getText().toString();
            nameValuePairs.add(new BasicNameValuePair("boss_id", boss));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

    public final void chk_boss_id_res(String input) throws JSONException {
        Log.e("input res",input);
        JSONArray jsonArray = new JSONArray(input);
        Log.e("error_dec",jsonArray.getJSONObject(0).getString("error_dec"));

        if(jsonArray.getJSONObject(0).getString("error_dec").equals("can_use")){
            if(jsonArray.getJSONObject(0).length()>1){
                boss_id.setEnabled(false);
                //國別
                for(int i = 0;i<country_list_id.length;i++){
                    if(jsonArray.getJSONObject(0).getString("country").equals(country_list_id[i])){
                        country_select = i;
                        break;
                    }
                }
                spinner.setSelection(country_select);
                //spinner.setEnabled(false);

                //身分別
                for(int i = 0;i<person_data_value.length;i++){
                    if(jsonArray.getJSONObject(0).getString("id_kind").equals(person_data_value[i])){
                        person_data_select = i;
                        break;
                    }
                }
                person_data_spinner.setSelection(person_data_select);
                person_data_spinner.setEnabled(false);

                //性別
                for(int i = 0;i<sex_value.length;i++){
                    if(jsonArray.getJSONObject(0).getString("sex").equals(sex_value[i])){
                        sex_select = i;
                        break;
                    }
                }
                sex_spinner.setSelection(sex_select);

                //會員姓名
                mb_name.setText(jsonArray.getJSONObject(0).getString("mb_name"));

                //生日
                birth_date.setText(jsonArray.getJSONObject(0).getString("birthday2"));

                //行動電話
                cellphone.setText(jsonArray.getJSONObject(0).getString("tel3"));

                //EMAIL
                mJoin_Data_Email_Edit.setText(jsonArray.getJSONObject(0).getString("email"));

                //通訊地址
                chk_boss_id = true;
                for(int i = 0;i<city_value.length;i++){
                    if(jsonArray.getJSONObject(0).getInt("city2")==city_value[i]){
                        city2_select = i;
                        break;
                    }
                }
                city2.setSelection(city2_select);
                area2_adapter = new ArrayAdapter<String>(context, R.layout.myspinner, area[city2_select]);
                area2.setAdapter(area2_adapter);

                for(int i = 0;i<area_value.length;i++){
                    if(jsonArray.getJSONObject(0).getInt("area2")  == area_value[city2_select][i]){
                        area2_select = i;
                        Log.e("area2",area2_select+"");
                        break;
                    }
                }
                area2.setSelection(area2_select);
                add2.setText(jsonArray.getJSONObject(0).getString("add2"));


                //送貨地址
                chk_boss_id3 = true;
                for(int i = 0;i<city_value.length;i++){
                    if(jsonArray.getJSONObject(0).getInt("city3")==city_value[i]){
                        city3_select = i;
                        break;
                    }
                }
                city3.setSelection(city3_select);
                area3_adapter = new ArrayAdapter<String>(context, R.layout.myspinner, area[city3_select]);
                area3.setAdapter(area3_adapter);

                for(int i = 0;i<area_value.length;i++){
                    if(jsonArray.getJSONObject(0).getInt("area3")  == area_value[city3_select][i]){
                        area3_select = i;
                        Log.e("area3",area3_select+"");
                        break;
                    }
                }
                area3.setSelection(area3_select);
                add3.setText(jsonArray.getJSONObject(0).getString("add3"));

                //個人帳戶
                for(int i = 0;i<bank_id.length;i++){
                    if(jsonArray.getJSONObject(0).getString("give_method").equals(bank_id[i])){
                        bank.setSelection(i);
                        break;
                    }
                }
                bank_no.setText(jsonArray.getJSONObject(0).getString("bank_no"));
                bank_ac.setText(jsonArray.getJSONObject(0).getString("bank_ac"));

            }else{
                person_data_spinner.setEnabled(true);
            }
        }else if(jsonArray.getJSONObject(0).getString("error_dec").equals("full_3")){
            Toast.makeText(join_data_qrcode.this,"此會員已滿三顆球",Toast.LENGTH_LONG).show();
            person_data_spinner.setEnabled(true);
        }else if(jsonArray.getJSONObject(0).getString("error_dec").equals("full_7")){
            Toast.makeText(join_data_qrcode.this,"此會員已滿七顆球",Toast.LENGTH_LONG).show();
            person_data_spinner.setEnabled(true);
        }else if(jsonArray.getJSONObject(0).getString("error_dec").equals("repeat")){
            Toast.makeText(join_data_qrcode.this,"生分證已存在",Toast.LENGTH_LONG).show();
            person_data_spinner.setEnabled(true);
        }
    }

    public void save_mbst_true() {
        mDialog = ProgressDialog.show(this, "線上推薦", "資料送出中...", true);
        mThread = new HandlerThread("save_mbst_true");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = save_mbst_true_data("save_mbst_true");
                String[] Up_tmp = new String[2];
                Up_tmp[0] = mGCMD.Upload_Picture_Menu_Path_array;
                Up_tmp[1] = mGCMD.Upload_Picture_Menu_Bir_Path_array;
                mGCMD.upload(join_data_qrcode.this, Up_tmp);
                mUI_Handler.post(new Runnable() {
                    public void run() {
                        save_mbst_true_res(jsonString);
                        mDialog.dismiss();
                    }
                });
            }
        });
    }

    private String save_mbst_true_data(String cmd) {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/join.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", cmd));
            String country = country_list_id[country_select];
           // String join_grade = join_grade_list_id[join_grade_select];
            String person = person_data_value[person_data_select];
            String boss = boss_id.getText().toString();
            String name = mb_name.getText().toString();
            String sex2 = sex_value[sex_select];
            String birth = birth_date.getText().toString();
            String phone = cellphone.getText().toString();
            String email = mJoin_Data_Email_Edit.getText().toString();
            String true_intro_no1 = true_intro_no.getText().toString();
            String intro_no1 = intro_no.getText().toString();

            Log.d(TAG,"intro_no1"+intro_no1);
            //String line_kind1 = line_kind.getTag().toString();
            String line_kind1 = Lind_kind_tmp;
            String area1 = String.valueOf(area_value[city1_select][area1_select]);
            String city1 = String.valueOf(city_value[city1_select]);
            String area2 = String.valueOf(area_value[city2_select][area2_select]);
            String city2 = String.valueOf(city_value[city2_select]);
            String area3 = String.valueOf(area_value[city3_select][area3_select]);
            String city3 = String.valueOf(city_value[city3_select]);
            String add1_res = add1.getText().toString();
            String add2_res = add2.getText().toString();
            String add3_res = add3.getText().toString();

            String Bonus_tmp = String.valueOf(Bonus_Arr_Title_Select);
            Log.d(TAG, "Bonus_Arr_Title" + Bonus_Arr_Title[Bonus_Arr_Title_Select]);
            Log.d(TAG, "Bonus_Arr_Title_Select" + Bonus_Arr_Title_Select);

            nameValuePairs.add(new BasicNameValuePair("country", country));
            //nameValuePairs.add(new BasicNameValuePair("join_grade", join_grade));
            nameValuePairs.add(new BasicNameValuePair("id_kind", person));
            nameValuePairs.add(new BasicNameValuePair("boss_id", boss));
            nameValuePairs.add(new BasicNameValuePair("mb_name", name));
            nameValuePairs.add(new BasicNameValuePair("sex", sex2));
            nameValuePairs.add(new BasicNameValuePair("birth", birth));
            nameValuePairs.add(new BasicNameValuePair("phone", phone));
            nameValuePairs.add(new BasicNameValuePair("email", email));
//            nameValuePairs.add(new BasicNameValuePair("city1", city1));
//            nameValuePairs.add(new BasicNameValuePair("area1", area1));
            nameValuePairs.add(new BasicNameValuePair("city2", city2));
            nameValuePairs.add(new BasicNameValuePair("area2", area2));
            nameValuePairs.add(new BasicNameValuePair("city3", city3));
            nameValuePairs.add(new BasicNameValuePair("area3", area3));
//            nameValuePairs.add(new BasicNameValuePair("add1", add1_res));
            nameValuePairs.add(new BasicNameValuePair("add2", add2_res));
            nameValuePairs.add(new BasicNameValuePair("add3", add3_res));
//            nameValuePairs.add(new BasicNameValuePair("bonus_chk", Bonus_tmp));
            nameValuePairs.add(new BasicNameValuePair("true_intro_no", true_intro_no1));
            nameValuePairs.add(new BasicNameValuePair("intro_no", intro_no1));
            nameValuePairs.add(new BasicNameValuePair("line_kind", line_kind1));
            nameValuePairs.add(new BasicNameValuePair("give_method",bank_id[bank.getSelectedItemPosition()])); //give_method 12/14 加入
            nameValuePairs.add(new BasicNameValuePair("bank_no",bank_no.getText().toString())); //bank_no 12/14 加入
            nameValuePairs.add(new BasicNameValuePair("bank_ac",bank_ac.getText().toString())); //bank_ac 12/14 加入

            nameValuePairs.add(new BasicNameValuePair("upload_id2",mGCMD.Upload_Picture_Menu_Path_array_tmp));
            nameValuePairs.add(new BasicNameValuePair("upload_id4",mGCMD.Upload_Picture_Menu_Bir_Path_array_tmp));

            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

    public final void save_mbst_true_res(String input) {

        String[] temp;
        Log.d("tmpTTT",input);
        if (input != null) {
            temp = input.split("%%");
            if (temp[0].equals("1")) {
            //入會成功
                //Toast.makeText(getApplicationContext(), "入會成功", Toast.LENGTH_LONG).show();
                /*Intent intent= new Intent(join_data.this, join_success.class);
                Bundle bundle2 = new Bundle();
                bundle2.putString("mb_no",temp[1]);
                intent.putExtras(bundle2);
                startActivity(intent);

                finish();*/

              // 原本的頁面
                Global_cart globalVariable = (Global_cart) context.getApplicationContext();
                globalVariable.cart.clear();
                Enumeration e = globalVariable.cart.keys();

                String price = join_prod_comp.getText().toString();
                //price=price.replace("NT$","");
                String pv = join_prod_pv.getText().toString();
                //pv=pv.replace(" PV","");

                globalVariable.cart.put(Join_Prod_No_First,"1"+","+Join_Prod_Price_First+","+Join_Prod_PV_First+","+0);
                globalVariable.cart.put(join_prod_no_select, "1" + "," + price + "," + pv+","+0);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("from", "join");
                bundle.putString("temp_mb_no", temp[1]);
                bundle.putString("temp_mb_name", temp[2]);
                bundle.putString("temp_mb_tel", temp[3]);
                intent.putExtras(bundle);
                intent.setClass(context, pay_list.class);
                startActivity(intent);
               /* Log.d(TAG, "tmpppp" + temp[1]);
                Log.d(TAG, "NAME" + temp[2]);
                Bundle bundle =new Bundle();
                bundle.putString("mb_no",temp[1]);
                bundle.putString("from",from);
                Intent intent =new Intent();
                intent.putExtras(bundle);
                intent.setClass(this,join_success.class);
                startActivity(intent);
                finish();*/

            } else {
                Toast.makeText(getApplicationContext(), input, Toast.LENGTH_LONG).show();
                Log.d("save_mbst_true_res", input);
            }
        }
        save_mbst.setEnabled(true);
        spinner.setEnabled(true);
        person_data_spinner.setEnabled(true);
        boss_id.setEnabled(true);
        mb_name.setEnabled(true);
        sex_spinner.setEnabled(true);
        birth_date.setEnabled(true);
        cellphone.setEnabled(true);
        auto_set_linel.setEnabled(true);
        auto_set_liner.setEnabled(true);
        city1.setEnabled(true);
        city2.setEnabled(true);
        area1.setEnabled(true);
        area2.setEnabled(true);
        city3.setEnabled(true);
        area3.setEnabled(true);
        add1.setEnabled(true);
        add2.setEnabled(true);
        bank_ac.setEnabled(true);
        bank_no.setEnabled(true);
    }

    public void get_join_prod_first_thread() {
        mThread = new HandlerThread("get_join_prod_first");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                final String jsonString = get_join_prod_first_data("get_join_prod_first");

                mUI_Handler.post(new Runnable() {
                    public void run() {

                        get_join_prod_first_res(jsonString);
                    }
                });
            }
        });
    }

    private String get_join_prod_first_data(String cmd) {
        String result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://" + ip + "/" + folder + "/join.php");

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cmd", cmd));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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

    public final void get_join_prod_first_res(String input) {
        Log.e("OKKKKKKKK123123123",input);
        try {
            JSONArray jsonArray = new JSONArray(input);
            Join_Prod_No_First = jsonArray.getJSONObject(0).getString("prod_no");
            Join_Prod_PV_First = jsonArray.getJSONObject(0).getString("pv");
            Join_Prod_Price_First = jsonArray.getJSONObject(0).getString("comp_price");
            mTextView_Join_Prod_First.setText(jsonArray.getJSONObject(0).getString("prod_name"));
            mTextView_Join_Prod_First_PV.setText(jsonArray.getJSONObject(0).getString("pv"));
            mTextView_Join_Prod_First_Price.setText(jsonArray.getJSONObject(0).getString("comp_price"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String readFromFile(String FILENAME) {
        try {

            FileInputStream fin = this.openFileInput(FILENAME);
            byte[] buff = new byte[fin.available()];
            fin.read(buff);
            String str = new String(buff);

            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private class MySimpleAdapter extends SimpleAdapter {

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

            final View v = super.getView(position, convertView, parent);
            // TextView can_tv=(TextView)v.findViewById(R.id.can_tv);
            //TextView cant_tv=(TextView)v.findViewById(R.id.cant_tv);

            if (position == 0) {
                // can_tv.setVisibility(View.GONE);
                //  cant_tv.setVisibility(View.GONE);
            }
            return v;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("若返回上一頁，您輸入的資料將被清除，確定前往嗎?");
            builder.setCancelable(false);

            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (from.equals("login")) {
                        Intent i = new Intent(join_data_qrcode.this, login.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    } else {
                        finish();
                    }
                }
            });

            builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class AllCapTransformationMethod extends ReplacementTransformationMethod {

        @Override
        protected char[] getOriginal() {
            char[] aa = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z' };
            return aa;
        }

        @Override
        protected char[] getReplacement() {
            char[] cc = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z' };
            return cc;
        }

    }
        //===================fainet Diy dialog change camera and changeimage ====
        public void AlertDialog_Upload_Camera(Activity tmp,ImageView Imgtmp) {

            Dialogtmp=Imgtmp;
            ContextThemeWrapper cw = new ContextThemeWrapper(tmp, R.style.AlertDialogTheme );
            AlertDialog.Builder MyListAlertDialog = new AlertDialog.Builder(cw);
            MyListAlertDialog.setCancelable(false);
            // 建立List的事件
            DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case 0:
                            EnterOnClick();
                            dialog.cancel();
                            Log.d(mGCMD.TAG,"pleasure_img_camera_option2");
                            break;
                        case 1:
                            Changeimage();
                            dialog.cancel();
                            Log.d(mGCMD.TAG,"pleasure_img_upload_option");
                            break;
                    }
                    Toast.makeText(join_data_qrcode.this, mGCMD.CameraList[which],// 顯示所點選的選項
                            Toast.LENGTH_LONG).show();
                }
            };
            // 建立按下取消什麼事情都不做的事件
            DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            };
            MyListAlertDialog.setItems(mGCMD.CameraList, ListClick);
            MyListAlertDialog.setNeutralButton("取消", OkClick);
            MyListAlertDialog.show();
        }
        //---
        //---------------------------------------------------------------
        private void EnterOnClick() {

            SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, filename);
            photoUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, mGCMD.CAMERA);

        }
        //--------------------------------------------------------------------
        private void Changeimage(){

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(intent, mGCMD.PHOTO);
            Log.d(mGCMD.TAG, "ChangeImage finish");

        }
        //--------------------------------------------------------------------
        @Override
        protected void onActivityResult(int request, int result,Intent mData) {
            Log.d(mGCMD.TAG, String.valueOf(request));
            Log.d(mGCMD.TAG,"result"+result+"tmp");
            Log.d(mGCMD.TAG, "cmd" + request);
            Log.d(mGCMD.TAG, String.valueOf(request));
            if(request ==mGCMD.CAMERA && result ==RESULT_CANCELED){

                String[] mMediaDATA = { MediaStore.Images.Media.DATA };
                Cursor mCursor = managedQuery(photoUri,mMediaDATA,null,null,null);
                int mImageIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                mCursor.moveToFirst();
                String tmpPath = mCursor.getString(mImageIndex);

                File fdelete = new File(tmpPath);
                boolean deleted = fdelete.delete();
                Log.d(mGCMD.TAG,"delete"+deleted);
                Log.d(mGCMD.TAG,"fdeletetmp"+tmpPath);
                if (deleted) {
                    Log.d(mGCMD.TAG,tmpPath);
                } else {
                    Log.d(mGCMD.TAG, "not_delete"+tmpPath);
                }
            }

            if (( mData!=null&& request == mGCMD.PHOTO &&result ==RESULT_OK )||(request == mGCMD.CAMERA &&result == RESULT_OK)){
                Log.d(mGCMD.TAG, "sadasdasdssad");
                //get Image path
                DisplayMetrics mPhone;
                mPhone = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(mPhone);
                Bitmap mImgbitmap=null;
                mImgbitmap=mGCMD.Bitmaprecyle(mImgbitmap);
                Uri DataUri = null;
                if (mData != null && mData.getData() != null) {
                    DataUri = mData.getData();
                }
                if (DataUri == null) {
                    Log.d(mGCMD.TAG,"DATA");
                    if (photoUri != null) {
                        DataUri = photoUri;
                    }
                }
                ContentResolver Imgcr = this.getContentResolver();
                String[] mMediaDATA = { MediaStore.Images.Media.DATA };
                Cursor mCursor = managedQuery(DataUri,mMediaDATA,null,null,null);
                int mImageIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                mCursor.moveToFirst();
                mPath = mCursor.getString(mImageIndex);
                Log.d(mGCMD.TAG,mPath);
                try     //scale pixel
                {
                    //read imageBitmap
                    BitmapFactory.Options mOptions = new BitmapFactory.Options();
                    mOptions.inSampleSize = 1;
                    mOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(Imgcr.openInputStream(DataUri), null, mOptions);
                    Log.d(mGCMD.TAG, "Source_Width" +mOptions.outWidth);
                    Log.d(mGCMD.TAG,"Source_Height"+mOptions.outHeight);

                    //insamplesize value = size scale 2=source 1/2     4=source 1/4

                    if(mOptions.outWidth>3000||mOptions.outHeight>3000) {
                        mOptions.inSampleSize = 4;
                        Log.d(mGCMD.TAG, "1");
                    }else if (mOptions.outWidth>1500||mOptions.outHeight>1500)  {
                        mOptions.inSampleSize = 2;
                        Log.d(mGCMD.TAG,"2");
                    } else {
                        mOptions.inSampleSize = 1;
                        Log.d(mGCMD.TAG,"3");
                    }
                    mOptions.inJustDecodeBounds = false;
                    mImgbitmap= BitmapFactory.decodeStream(Imgcr.openInputStream(DataUri),null,mOptions);

                    int digree = 0;
                    digree=mGCMD.DigreeImg(mPath);

                /*
                float newscaleWidth;
                float newscaleHeight;
                Log.d(mGCMD.TAG,"mImgbitmap.getWidth()"+mImgbitmap.getWidth());
                Log.d(mGCMD.TAG,"mImgbitmap.height()"+mImgbitmap.getHeight());

                if(mImgbitmap.getWidth()>=mImgbitmap.getHeight()){
                    newscaleWidth = ((float) mPicture_GCMD.scale_width) / mImgbitmap.getWidth();
                    newscaleHeight = ((float) mPicture_GCMD.scale_height) / mImgbitmap.getHeight();
                    Log.d(mGCMD.TAG,"scale");
                } else {
                    newscaleWidth = ((float) mPicture_GCMD.digree_scale_width) / mImgbitmap.getWidth();
                    newscaleHeight = ((float) mPicture_GCMD.digree_scale_height) / mImgbitmap.getHeight();
                    Log.d(mGCMD.TAG,"digree_scale");
                }

                Matrix matrix = new Matrix();
                matrix.postScale(newscaleWidth, newscaleHeight);
                mImgbitmap = Bitmap.createBitmap(mImgbitmap, 0, 0, mImgbitmap.getWidth(), mImgbitmap.getHeight(), matrix,true);
                Log.d(mGCMD.TAG, String.valueOf(mImgbitmap.getHeight()) + ",camera imageHeight");
                Log.d(mGCMD.TAG, String.valueOf(mImgbitmap.getWidth()) + ",camera imageWidth");*/

                    if(digree!=0) {
                        Matrix m = new Matrix();
                        m.postRotate(digree);
                        mImgbitmap = Bitmap.createBitmap(mImgbitmap, 0, 0, mImgbitmap.getWidth(),
                                mImgbitmap.getHeight(), m, true);
                    }

                    if(mChange_Option_Dialoge == mGCMD.Dialog_Bank){
                        mGCMD.Upload_Picture_Menu_tmp_Bitmap=mImgbitmap;
                        Dialogtmp.setImageBitmap(mImgbitmap);
                        if (mImgbitmap.getHeight()>mImgbitmap.getWidth()){
                            Dialogtmp.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        } else {
                            Dialogtmp.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                        bank_boolean = true;
                        Long tsLong = System.currentTimeMillis()/1000;
                        String ts = tsLong.toString();
                        mGCMD.Upload_Picture_Menu_Path_array_tmp=mGCMD.Upload_menu_Path_file+login_id+ts+".png";
                        String path=mGCMD.Upload_menu_Path+mGCMD.Upload_Picture_Menu_Path_array_tmp;
                        Log.d(mGCMD.TAG,path);
                        mGCMD.Upload_Picture_Menu_Path_array=path;
                        mGCMD.Write_File_Path_Bitmap(path, mGCMD.Upload_Picture_Menu_tmp_Bitmap);
                    } else {
                        mGCMD.Upload_Picture_Menu_Bir_tmp_Bitmap=mImgbitmap;
                        Dialogtmp.setImageBitmap(mImgbitmap);
                        if (mImgbitmap.getHeight()>mImgbitmap.getWidth()){
                            Dialogtmp.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        } else {
                            Dialogtmp.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                        bir_boolean = true;
                        Long tsLong = System.currentTimeMillis()/1000;
                        String ts = tsLong.toString();
                        mGCMD.Upload_Picture_Menu_Bir_Path_array_tmp=mGCMD.Upload_menu_Bir_Path_file+login_id+ts+".png";
                        String path=mGCMD.Upload_menu_Bir_Path+mGCMD.Upload_Picture_Menu_Bir_Path_array_tmp;
                        Log.d(mGCMD.TAG,path);
                        mGCMD.Upload_Picture_Menu_Bir_Path_array=path;
                        mGCMD.Write_File_Path_Bitmap(path, mGCMD.Upload_Picture_Menu_Bir_tmp_Bitmap);
                    }

                }  catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(mGCMD.TAG, "sadsadsaddsa");
            }
            super.onActivityResult(request, result, mData);
        }
        //--------------------------------------------------------------------
        public void AlertDialog_Upload_Camera(Activity tmp,Bitmap Localtmp, final ImageView DialogImg) {

            final Dialog dialog;
            DisplayMetrics metrics = new DisplayMetrics();
            tmp.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            final int width=metrics.widthPixels;
            final int height=metrics.heightPixels;
            Dialogtmp=DialogImg;

            dialog = new Dialog(tmp,R.style.MyDialog);//指定自定義樣式
            dialog.setContentView(R.layout.pleasure_img_example_load);
            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
            dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ImageView ImgLoad = (ImageView) dialog.findViewById(R.id.Pleasure_Img_Example_Main);

            ImgLoad.setImageBitmap(Localtmp);
            lp.width=width;
            lp.height=height;
            lp.y=0;
            dialogWindow.setAttributes(lp);
            WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            dialogWindow.setAttributes(p);
            dialog.show();

            Button ImgLoad_Check = (Button) dialog.findViewById(R.id.Pleasure_Img_CHeck_Example);

            ImgLoad_Check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(DialogImg == bank_img){
                        StackShiftInit(0);
                    } else {
                        StackShiftInit(1);
                    }
                    dialog.cancel();
                    Log.d(mGCMD.TAG, "pleasure_img_upload_option");
                }
            });
            ImageView ImgLoad_Return = (ImageView) dialog.findViewById(R.id.Pleasure_Img_Return_Example);
            ImgLoad_Return.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    Log.d(mGCMD.TAG, "pleasure_img_camera_option2");
                }
            });
        }
        //--------------------------------------------------------------------
        private void StackShiftInit(int Source_tmp ) {
            final int source_bank = 0 , source_birthday=1;
            Log.d(mGCMD.TAG, "StackShiftInit");


            switch (Source_tmp) {
                case source_bank:
                    File tmp = new File(mGCMD.Upload_Picture_Menu_Path_array);
                    tmp.delete();
                    bank_boolean =false;
                    mGCMD.Upload_Picture_Menu_tmp_Bitmap = null;
                    mGCMD.Upload_Picture_Menu_Path_array = null;
                    mGCMD.Upload_Picture_Menu_Path_array_tmp = null;
                    bank_img.setImageResource(R.drawable.add_create);
                    Log.d(mGCMD.TAG,"source_bank");
                    break;
                case source_birthday:
                    File bir = new File(mGCMD.Upload_Picture_Menu_Bir_Path_array);
                    bir.delete();
                    bir_boolean = false;
                    mGCMD.Upload_Picture_Menu_Bir_tmp_Bitmap = null;
                    mGCMD.Upload_Picture_Menu_Bir_Path_array = null;
                    mGCMD.Upload_Picture_Menu_Bir_Path_array_tmp = null;
                    birthday_img.setImageResource(R.drawable.add_create);
                    Log.d(mGCMD.TAG,"source_birthday");
                    break;
            }

        }
        private void  InitImgOnDialogOption(){
            if(mGCMD.Upload_Picture_Menu_Path_array !=null) {
                Log.d(mGCMD.TAG,"Upload_Picture_Menu_Path_array");
                File tmp = new File(mGCMD.Upload_Picture_Menu_Path_array);
                if (tmp.exists()) {
                    tmp.delete();
                }
                bank_boolean = false;
                mGCMD.Upload_Picture_Menu_tmp_Bitmap = null;
                mGCMD.Upload_Picture_Menu_Path_array = null;
                mGCMD.Upload_Picture_Menu_Path_array_tmp = null;
                bank_img.setImageResource(R.drawable.add_create);
            }
            if(mGCMD.Upload_Picture_Menu_Bir_Path_array != null) {
                Log.d(mGCMD.TAG,"Upload_Picture_Menu_Bir_Path_array");
                File bir = new File(mGCMD.Upload_Picture_Menu_Bir_Path_array);
                if (bir.exists()) {
                    bir.delete();
                }
                bir_boolean = false;
                mGCMD.Upload_Picture_Menu_Bir_tmp_Bitmap = null;
                mGCMD.Upload_Picture_Menu_Bir_Path_array = null;
                mGCMD.Upload_Picture_Menu_Bir_Path_array_tmp = null;
                birthday_img.setImageResource(R.drawable.add_create);
            }
        }
}
