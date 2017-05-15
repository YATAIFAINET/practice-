package com.example.ytt.ev4_camera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraDevice;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.ytt.ev4_camera.Prefs.Ev4Prefs;

import java.io.File;
import java.net.URI;

public class MainActivity extends Activity {
    private File dir=null;
    private static int REQ_TAKE_PICTURE=30;
    File ext_pic=null;
    ImageView show_on_main=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup_storage();
    }





    //---------------------- setting up

    private void setup_storage(){
        //experiments


        Log.i("public pic directory", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath());
        File e=Environment.getExternalStoragePublicDirectory(MediaStore.Images.Media.DATA);

        MediaStore.Images.Media.getContentUri(MediaStore.EXTRA_MEDIA_ALBUM);
        Log.i("Album?   ", MediaStore.Images.Media.getContentUri(MediaStore.Images.Media.DATA).getPath());



        /*
        String[] s=e.list();
        for(int i=0;i<s.length;i++){
            Log.d("files in dir",s[i]);

        }
    */


        dir=new File(Ev4Prefs.IMG_DIR);
        if(!dir.exists()){
            Log.d("Folder Creation",Boolean.toString(dir.mkdir()));
        }
        else{Log.d("Folder"," Folder Exists");}

    }



    //---------------------- GUI referencing methods

    public void take_picture(View v){
            Intent takePicture=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            ext_pic=new File(dir,"ev6.png");
         MediaStore.Images.Media.getContentUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
       /* try{
            if(!ext_pic.exists()){ext_pic.createNewFile();}
        }catch(Exception e){
            Log.e("File creation ",e.getMessage());
        }*/
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(dir));
            startActivityForResult(takePicture,REQ_TAKE_PICTURE);
        Log.d("fainet", Ev4Prefs.IMG_DIR);
    }


    public void view_gallery(View v){

    }


    //----------------------- Handling Result


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ_TAKE_PICTURE && resultCode==RESULT_OK){
            show_on_main=(ImageView)findViewById(R.id.main_show_image);
            Log.e("Success","taking");

            data.getData();
            Bitmap ctrd= BitmapFactory.decodeFile(ext_pic.getPath());

            Log.e("Image null", Boolean.toString(ctrd==null));



        }
    }
}
