package com.example.nick.joeyi_android2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by Nick on 2015/11/19.
 */
public class shopping_main extends FragmentActivity {

    private Button btn_into;
    ViewPager pager = null;
    PagerTabStrip tabStrip = null;
    ArrayList<View> viewContainter = new ArrayList<View>();
    ArrayList<String> titleContainer = new ArrayList<String>();
    public String TAG = "tag";

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.use_page_layout);

        pager = (ViewPager) this.findViewById(R.id.viewPager);
        tabStrip = (PagerTabStrip) this.findViewById(R.id.tabstrip);
        //取消tab下面的???
        tabStrip.setDrawFullUnderline(false);
        //?置tab的背景色
        tabStrip.setBackgroundColor(0xFFFFF8DC);
        //?置?前tab??的下划??色
        tabStrip.setTabIndicatorColor(0xFFff3366);
        tabStrip.setTextSpacing(200);

        View view1 = LayoutInflater.from(this).inflate(R.layout.use1, null);
        View view2 = LayoutInflater.from(this).inflate(R.layout.use2, null);
        final View view3 = LayoutInflater.from(this).inflate(R.layout.use3, null);

        //viewpager?始添加view
        viewContainter.add(view1);
        viewContainter.add(view2);
        viewContainter.add(view3);
        //???
        titleContainer.add("這排可以拿掉");
        titleContainer.add("這排可以拿掉");
        titleContainer.add("這排可以拿掉");


        pager.setAdapter(new PagerAdapter() {

            //viewpager中的?件?量
            @Override
            public int getCount() {
                return viewContainter.size();
            }
            //滑?切?的?候???前的?件
            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                ((ViewPager) container).removeView(viewContainter.get(position));
            }
            //每次滑?的?候生成的?件
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ((ViewPager) container).addView(viewContainter.get(position));
                return viewContainter.get(position);
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titleContainer.get(position);
            }
        });

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
                Log.d(TAG, "--------changed:" + arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                Log.d(TAG, "-------scrolled arg0:" + arg0);
                Log.d(TAG, "-------scrolled arg1:" + arg1);
                Log.d(TAG, "-------scrolled arg2:" + arg2);
            }

            @Override
            public void onPageSelected(int arg0) {
                Log.d(TAG, "------selected:" + arg0);
                switch (arg0){
                    case 2:
                        btn_into=(Button)view3.findViewById(R.id.btn_into);
                        btn_into.setOnClickListener(new Button.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                               /* Intent Intentintent = new Intent();
                                Intentintent.setClass(getApplicationContext(),login. class );
                                startActivity(Intentintent);
                                finish();*/
                            }


                        });
                        break;

                }
            }
        });

    }
}
