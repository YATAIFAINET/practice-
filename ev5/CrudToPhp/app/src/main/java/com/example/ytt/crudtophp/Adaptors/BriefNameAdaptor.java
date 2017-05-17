package com.example.ytt.crudtophp.Adaptors;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ytt.crudtophp.DataAccessor.MyMember;
import com.example.ytt.crudtophp.DataAccessor.NetAccessor;
import com.example.ytt.crudtophp.R;

import org.json.JSONObject;

/**
 * Created by ytt on 2017/5/16.
 */

public class BriefNameAdaptor  extends BaseAdapter {
    MyMember[] mems;
    LayoutInflater li;

    public BriefNameAdaptor(Activity home, MyMember[] members){
        mems=members;
        li=home.getLayoutInflater();
    }


    @Override
    public int getCount() {
        return mems.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        if(v==null){
            v=li.inflate(R.layout.layout_brief_info,null);
        }
        TextView userEmail=(TextView)v.findViewById(R.id.show_briefData_email);
        TextView userName=(TextView)v.findViewById(R.id.show_briefData_name);
        userName.setBackgroundColor(Color.GRAY);
        userName.setText(mems[position].getUserName());
        userEmail.setText(mems[position].getEmail());
        return v;
    }

    public MyMember[] getArray(){
        return mems;
    }



}
