package com.example.nick.joeyi_android2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class test extends Activity {

    private Button btn_shopping,btn_moneypv;
    private Context context;
    String ip="",folder="";


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_page);
        context=this;
        Toast.makeText(getApplicationContext(), "123456", Toast.LENGTH_SHORT).show();
    }
}
