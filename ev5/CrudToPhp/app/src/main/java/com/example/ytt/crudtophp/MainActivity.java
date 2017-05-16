package com.example.ytt.crudtophp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void check_userExist(View v){
        Intent checkExist =new Intent(this,MemberExist.class);
        startActivity(checkExist);
    }

    public void add_member(View v){
        startActivity(new Intent(this,AddMember.class));
    }

    public void edit_memberData(View v){
        startActivity(new Intent(this,ChangeData.class));
    }

    public void delete_user(View v){
        startActivity(new Intent(this,DeleteUser.class));

    }

    public void general_connect(View v){
        startActivity(new Intent(this,ConnectionTest.class));
    }
}
