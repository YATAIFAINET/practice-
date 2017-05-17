package com.example.nick.joeyi_android2;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Activity_Google_map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayAdapter<String> listAdapter_sex;
    private Button save_btn, btn_contact_old, often_qa;
    private ImageView img_goto;
    private String ip, folder;
    private Handler mUI_Handler = new Handler();
    private HandlerThread mThread;
    private Handler mThreadHandler;
    String config = "", login_id;
    public static String DOMAIN = "http://maps.googleapis.com/maps/api/geocode/json";
    public String addressstr;
    private String[] contact_spinner;
    private Spinner spinner;
    private ArrayAdapter qaList;

    private ArrayList<Marker> array_marker = new ArrayList<Marker>();
    private ArrayList<String> array_address = new ArrayList<String>();
    private ArrayList<String> array_title = new ArrayList<String>();
    ArrayList<View> viewList = new ArrayList<View>();
    ViewPager mViewPager;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隱藏logo
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.home_2);
        setTitle("Map");

        array_address.add("116台北市文山區羅斯福路六段218號");
        array_address.add("116台北市文山區羅斯福路六段292號");
        array_address.add("116台北市文山區羅斯福路六段186號");
        array_title.add("景美捷運站");
        array_title.add("景美醫院");
        array_title.add("不知名");


        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        final LayoutInflater mInflater = getLayoutInflater().from(this);

        for (int i = 0; i < 3; i++) {
            View v1 = mInflater.inflate(R.layout.pager_layout, null);
            viewList.add(v1);
            TextView text_title = (TextView) v1.findViewById(R.id.textView129);
            TextView text_address = (TextView) v1.findViewById(R.id.textView130);
            text_title.setText(array_title.get(i));
            text_address.setText(array_address.get(i));
        }

        mViewPager.setAdapter(new MyViewPagerAdapter(viewList));
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.e("select", "" + position);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(getLatLngByAddress((String) array_address.get(position)))      // Sets the center of the map to Mountain View
                        .zoom(15)                   // Sets the zoom
                        .bearing(0)                // Sets the orientation of the camera to east
                        .tilt(0)                  // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 500, null);
                array_marker.get(position).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                for (int i = 0; i < array_marker.size(); i++) {
                    if (i != position) {
                        array_marker.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }
                }

            }

            boolean isScrolled = false;

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case 1:// 手势滑动
                        isScrolled = false;
                        break;
                    case 2:// 界面切换
                        isScrolled = true;
                        break;
                    case 0:// 滑动结束

                        if (mViewPager.getCurrentItem() == mViewPager.getAdapter()
                                .getCount() - 1 && !isScrolled) {
                            mViewPager.setCurrentItem(0);
                        } else if (mViewPager.getCurrentItem() == 0 && !isScrolled) {
                            mViewPager.setCurrentItem(mViewPager.getAdapter()
                                    .getCount() - 1);
                        }
                        break;
                }
            }
        });

        for (int k = 0; k < viewList.size(); k++) {
            final int finalK = k;
            viewList.get(k).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Activity_Google_map.this, array_title.get(finalK), Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;

        public MyViewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mListViews.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for (int i = 0; i < 3; i++) {
            LatLng sydney = getLatLngByAddress((String) array_address.get(i));
            Marker marker = mMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            array_marker.add(marker);
        }

        array_marker.get(0).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        // Add a marker in Sydney and move the camera
        LatLng sydney1 = getLatLngByAddress("116台北市文山區羅斯福路六段218號");
//        mMap.addMarker(new MarkerOptions().position(sydney1).title("景美捷運站"));
//        LatLng sydney2 = getLatLngByAddress("116台北市文山區羅斯福路六段218號");
//        mMap.addMarker(new MarkerOptions().position(sydney2).title("景美捷運站"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(getLatLngByAddress((String) array_address.get(0)))      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (int i = 0; i < array_marker.size(); i++) {
                    if (marker.equals(array_marker.get(i))) {
                        mViewPager.setCurrentItem(i);
                        array_marker.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    } else {
                        array_marker.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_normal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;

        }
        //Toast.makeText(context, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
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

    // 同步取得 LatLng
    public static LatLng getLatLngByAddress(String address) {
        LatLng latLng = null;
        AddressToLatLng ga = new AddressToLatLng(address);
        FutureTask<LatLng> future = new FutureTask<LatLng>(ga);
        new Thread(future).start();
        try {
            latLng = future.get();
        } catch (Exception e) {

        }
        return latLng;
    }

    // Address 轉 LatLng
    private static class AddressToLatLng implements Callable<LatLng> {
        private String queryURLString = DOMAIN
                + "?address=%s&sensor=false&language=zh_tw";
        private String address;

        AddressToLatLng(String address) {
            this.address = address;
        }

        @Override
        public LatLng call() {
            LatLng latLng = null;
            try {
                // 輸入地址得到緯經度(中文地址需透過URLEncoder編碼)
                latLng = getLocationByAddress(URLEncoder.encode(address,
                        "UTF-8"));
            } catch (UnsupportedEncodingException e) {
            }
            return latLng;
        }

        private LatLng getLocationByAddress(String address) {
            String urlString = String.format(queryURLString, address);
            LatLng latLng = null;
            try {
                // 取得 json string
                String jsonStr = HttpUtil.get(urlString);
                // 取得 json 根陣列節點 results
                JSONArray results = new JSONObject(jsonStr)
                        .getJSONArray("results");
                System.out.println("results.length() : " + results.length());
                if (results.length() >= 1) {
                    // 取得 results[0]
                    JSONObject jsonObject = results.getJSONObject(0);
                    // 取得 geometry --> location 物件
                    JSONObject laln = jsonObject.getJSONObject("geometry")
                            .getJSONObject("location");

                    latLng = new LatLng(Double.parseDouble(laln
                            .getString("lat")), Double.parseDouble(laln
                            .getString("lng")));
                }

            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
            return latLng;
        }
    }

    private static class HttpUtil {
        public static String get(String urlString) throws Exception {
            InputStream is = null;
            Reader reader = null;
            StringBuilder str = new StringBuilder();
            URL url = new URL(urlString);
            URLConnection URLConn = url.openConnection();
            URLConn.setRequestProperty("User-agent", "IE/6.0");
            is = URLConn.getInputStream();
            reader = new InputStreamReader(is, "UTF-8");
            char[] buffer = new char[1];
            while (reader.read(buffer) != -1) {
                str.append(new String(buffer));
            }
            return str.toString();
        }
    }
}
