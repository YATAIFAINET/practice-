package com.example.ytt.crudtophp;

import android.app.Activity;
import android.icu.text.BreakIterator;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ytt.crudtophp.Adaptors.BriefNameAdaptor;
import com.example.ytt.crudtophp.DataAccessor.MyMember;
import com.example.ytt.crudtophp.DataAccessor.NetAccessor;

import org.json.JSONObject;

public class GetMemberData extends Activity implements AdapterView.OnItemSelectedListener{
    Handler hnd=null;
    Spinner mSpin=null;
    BriefNameAdaptor bna=null;
    TextView userName,userEmail,userStarsign,userBloodType,userBMI,userTel,userAddr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_handler();
        setContentView(R.layout.activity_get_member_data);
        init_views();
        acquireData();

    }

    private void acquireData(){
        Thread th1=new Thread(new NetAccessor(hnd,5,null));
        th1.start();
    }

    private void init_handler(){
        final Activity ee=this;
        final AdapterView.OnItemSelectedListener osl=this;
        hnd=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String src = (String) msg.obj;
                switch(msg.arg1){
                    case NetAccessor.PAIR_DATA:
                        String[] data = src.split(",");
                        Log.i("atvt:GetMember:string",src);
                        int count=Integer.parseInt(data[data.length-1]);
                        MyMember[] members = new MyMember[count];
                        for (int i = 0; i < count; i++) {
                            Log.d("GMD : int-string",data[i]);
                            Log.d("GMD : length",Integer.toString(data[i].length()));

                            members[i] = new MyMember(data[i * 3], data[i * 3 + 1],data[i * 3 + 2]);
                        }
                        bna=new BriefNameAdaptor(ee,members);
                        mSpin.setAdapter(bna);
                        mSpin.setOnItemSelectedListener(osl);
                        break;
                    case NetAccessor.FULL_DATA:
                        try{
                        JSONObject member=new JSONObject(src);
                            userName.setText(member.getString("name"));
                            userEmail.setText(member.getString("email"));
                            userAddr.setText(member.getString("address"));
                            userStarsign.setText(member.getString("starsign"));
                            userBMI.setText(Integer.toString(member.getInt("BMI")));
                            userTel.setText(member.getString("phone"));
                            userBloodType.setText(member.getString("bloodtype"));

                        }catch (Exception e){
                            Log.e("JSON problem",e.getMessage());
                        }

                        break;

                }

            }
        };
    }

    private void init_views(){
        mSpin=(Spinner)findViewById(R.id.memberSpin);
        userName=(TextView)findViewById(R.id.show_fullData_name);
        userEmail=(TextView)findViewById(R.id.show_fullData_email);
        userAddr=(TextView)findViewById(R.id.show_fullData_addr);
        userStarsign=(TextView)findViewById(R.id.show_fullData_starSign);
        userTel=(TextView)findViewById(R.id.show_fullData_tel);
        userBMI=(TextView)findViewById(R.id.show_fullData_bmi);
        userBloodType=(TextView)findViewById(R.id.show_fullData_btype);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            BriefNameAdaptor bfn=(BriefNameAdaptor) parent.getAdapter();
            String r_userId=Integer.toString(bfn.getArray()[position].getId());
            Thread th2=new Thread(new NetAccessor(hnd,NetAccessor.FULL_DATA,r_userId));
            th2.start();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
