package com.example.nick.joeyi_android2;

import android.app.ListActivity;

/**
 * Created by User on 2016/5/11.
 */
public class qrcode_main extends ListActivity {
/*
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String,String>> list;
    private HashMap<String,String> item;
    String config="",login_id;
    Boolean loadding_flag=false;
    String ip="",folder="";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        load_config();
        config  = readFromFile("client_config");
        Button e_cash_co=(Button)findViewById(R.id.e_cash_co);
        Button e_pv_co=(Button)findViewById(R.id.e_pv_co);
        Button sw_qr=(Button)findViewById(R.id.sw_qr);

        e_cash_co.setOnClickListener(btn_e_cash_co_listener);
        e_pv_co.setOnClickListener(btn_e_pv_co_listener);
        sw_qr.setOnClickListener(btn_sw_qr_listener);
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
    private Button.OnClickListener btn_e_cash_co_listener = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), qrcode.class);
            Bundle bundle2 = new Bundle();
            intent.putExtras(bundle2);


            startActivity(intent);
        }
    };
    private Button.OnClickListener btn_e_pv_co_listener = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), qrcode.class);
            Bundle bundle2 = new Bundle();
            intent.putExtras(bundle2);


            startActivity(intent);
        }
    };
    private Button.OnClickListener btn_sw_qr_listener = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), qrcode.class);
            Bundle bundle2 = new Bundle();
            intent.putExtras(bundle2);


            startActivity(intent);
        }
    };
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        Log.d("whereeeeeee", String.valueOf(position));
        if(position==0){
            Intent intent = new Intent();
            intent.setClass(getActivity(), my_account_order.class);
            startActivity(intent);

        }else if(position==1){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("from", "e_cash");
            intent.putExtras(bundle);
            intent.setClass(getActivity(), my_account_e_cash.class);
            startActivity(intent);

        }else if(position==2){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("from", "bonus");
            intent.putExtras(bundle);
            intent.setClass(getActivity(), my_account_e_cash.class);
            startActivity(intent);
        }else if(position==3){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("from", "bonus2");
            intent.putExtras(bundle);
            intent.setClass(getActivity(), my_account_e_cash.class);
            startActivity(intent);
        }else if(position==4){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("from", "e_cash2");
            intent.putExtras(bundle);
            intent.setClass(getActivity(), my_account_e_cash.class);
            startActivity(intent);
        }else if(position==5){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("from", "e_gold");
            intent.putExtras(bundle);
            intent.setClass(getActivity(), my_account_e_cash.class);
            startActivity(intent);
        }else if(position==6){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("from", "e_pv");
            intent.putExtras(bundle);
            intent.setClass(getActivity(), my_account_e_cash.class);
            startActivity(intent);
        }
        super.onListItemClick(l, v, position, id);

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
            TextView can_tv=(TextView)v.findViewById(R.id.can_tv);
            TextView cant_tv=(TextView)v.findViewById(R.id.cant_tv);

            if(position==0){
                can_tv.setVisibility(View.GONE);
                cant_tv.setVisibility(View.GONE);

            }
            return v;
        }
    }
    private String readFromFile(String FILENAME) {
        try{

            FileInputStream fin = getActivity().openFileInput(FILENAME);
            byte[] buff = new byte[fin.available()];
            fin.read(buff);
            String str = new String(buff);



            return str;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    private void chk_loadding() {
        FrameLayout Fralayout = (FrameLayout)getView().findViewById(R.id.acount_loadding);
        // LinearLayout LinearLayout = (android.widget.LinearLayout)getView().findViewById(R.id.Top_bar);
        // FrameLayout loadding=(FrameLayout)getView().findViewById(R.id.FF1) ;
        if (loadding_flag == true){//讀好了
            //  LinearLayout.setVisibility(View.VISIBLE);
            Fralayout.setVisibility(View.GONE);
            //   loadding.setVisibility(View.GONE);
        }else{
            Fralayout.setVisibility(View.VISIBLE);
            //  loadding.setVisibility(View.VISIBLE);
            // LinearLayout.setVisibility(View.GONE);
        }
    }
*/
}
