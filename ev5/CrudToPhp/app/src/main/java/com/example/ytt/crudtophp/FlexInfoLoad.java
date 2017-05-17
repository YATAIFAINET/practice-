package com.example.ytt.crudtophp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ytt.crudtophp.Adaptors.BriefNameAdaptor;
import com.example.ytt.crudtophp.Adaptors.BriefNameFlexAdaptor;
import com.example.ytt.crudtophp.DataAccessor.MyMember;
import com.example.ytt.crudtophp.DataAccessor.NetAccessor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by ytt on 2017/5/17.
 */

public class FlexInfoLoad extends Activity implements AdapterView.OnItemSelectedListener {

    Handler hnd=null;
    Spinner mSpin=null;
    BriefNameFlexAdaptor bna=null;
    TextView userName,userEmail,userStarsign,userBloodType,userBMI,userTel,userAddr;
    MyMember[] members;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_handler();
        setContentView(R.layout.activity_get_member_data);
        init_views();
        acquireData();

    }

    private void acquireData(){
        Thread th1=new Thread(new NetAccessor(hnd,NetAccessor.FLEX_JSON,null));
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
                    case NetAccessor.FLEX_JSON:
                        try{
                            JSONArray s=new JSONArray(src);
                            int count=s.length();
                            Log.d("a",src);
                            members=new MyMember[count];
                            for(int i=0;i<count;i++){
                                members[i]=new MyMember(s.getJSONArray(i));
                            }

                        }catch(Exception e){
                            Log.d("FLEX_INFO",e.getMessage());
                        }

                        break;

                    case NetAccessor.FLEX_DATA:
                        String[] data = src.split(",");
                        //trim the string infos

                        int count=Integer.parseInt(data[data.length-1]);
                        int patternLength=(data.length-1)/count;
                        members = new MyMember[count];
                        for (int i = 0; i < count; i++) {
                            String[]unitData=new String[patternLength];
                            for(int x=0;x<patternLength;x++){
                                unitData[x]=data[i*patternLength+x];
                                Log.d("SS"+Integer.toString(x),unitData[x]);
                            }

                            members[i]=new MyMember(unitData);
                        }
                       // bna=new BriefNameFlexAdaptor(ee,members);
                       // mSpin.setAdapter(bna);
                       // mSpin.setOnItemSelectedListener(osl);
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

                bna=new BriefNameFlexAdaptor(ee,members);
                mSpin.setAdapter(bna);
                mSpin.setOnItemSelectedListener(osl);



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
        /*BriefNameAdaptor bfn=(BriefNameAdaptor) parent.getAdapter();
        String r_userId=Integer.toString(bfn.getArray()[position].getId());
        Thread th2=new Thread(new NetAccessor(hnd,NetAccessor.FULL_DATA,r_userId));
        th2.start();*/
        bna.getArray()[position].showAllData();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
