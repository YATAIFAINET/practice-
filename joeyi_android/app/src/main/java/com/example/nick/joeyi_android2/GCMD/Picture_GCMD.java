package com.example.nick.joeyi_android2.GCMD;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

import com.example.nick.joeyi_android2.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by YTT1 on 2017/3/24.
 */
public class Picture_GCMD {
    private ProgressDialog dialog = null;
    private  String Upresult="";

    private android.os.Handler mUI_Handler = new android.os.Handler();
    private HandlerThread mThread;
    private android.os.Handler mThreadHandler;

    private int prodialogcount=0; ; //目前跑到哪
    private int persioncount =0;  //加入百分比

    //defin digreescale
    public final static int firstdigree=0;
    public final static int seconddigree=90;
    public final static int thriddigree=180;
    public final static int fourdigree=270;
    //debug log
    public final static String TAG = "Fainet_JoeYi";
    //change camera
    public final static String[] CameraList = { "相機", "選取照片" };
    public final static int CAMERA = 66 ;
    public final static int PHOTO = 99 ;
    public static boolean Dialog_Bank = false ;
    public static boolean Dialog_Birthday = true ;
    //----------
    //path file
    public static String Upload_Path= Environment.getExternalStorageDirectory().toString()+"/joeyi_tmp/";
    public static String Upload_menu_Path= Environment.getExternalStorageDirectory().toString()+"/joeyi_tmp/menu_Item/";
    public static String Upload_menu_Bir_Path= Environment.getExternalStorageDirectory().toString()+"/joeyi_tmp/bir_Item/";

    public static String Upload_menu_Path_file="Menu";
    public static String Upload_menu_Bir_Path_file="Bir";
    //bitmap define param
    public static String Upload_Picture_Menu_Path_array;
    public static String Upload_Picture_Menu_Path_array_tmp;
    public static Bitmap Upload_Picture_Menu_tmp_Bitmap;

    public static String Upload_Picture_Menu_Bir_Path_array;
    public static String Upload_Picture_Menu_Bir_Path_array_tmp;
    public static Bitmap Upload_Picture_Menu_Bir_tmp_Bitmap;
    public static String Cus_Picture_Upload ;

    public static String ip;
    public static String folder;

    //===func lib
    //upload file
    //------------------------------------------------------------
    public Picture_GCMD () {
        ip = "220.135.2.83/joeyi";
        //ip = " 203.66.168.190";
        folder = "ct/sh";
        Cus_Picture_Upload="http://"+ip+"/"+folder+"/"+"joeyi_Upload.php";
    }

    //------------------------------------------------------------------
    public void IniTialDialog(Activity tmp, final String [] arr_tmp,final String Mes){
        prodialogcount=0;
        int pertmp=0;
        dialog = new ProgressDialog(tmp);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("圖片上傳");
        dialog.setCancelable(false);
        dialog.setProgress(prodialogcount);
        dialog.setMessage(Mes);
        dialog.setIcon(R.drawable.logo);
        dialog.setIndeterminate(false);
        for (int i = 0;i<arr_tmp.length;i++){
            if(arr_tmp[i]!=null){
                pertmp++;
            }
        }
        dialog.setMax(pertmp);
        dialog.show();
    }
    //------------------------------------------------------------------
    public  void upload (final Activity tmp, final String [] arr_tmp) {

        IniTialDialog(tmp,arr_tmp,"Uploading");
        mThread = new HandlerThread("Pleasure_Menu_Upload_Send");
        mThread.start();

        mThreadHandler = new android.os.Handler(mThread.getLooper());
        mThreadHandler.post(new Runnable() {
            public void run() {
                for(int i=0;i<arr_tmp.length;i++)
                {
                    Log.d(TAG, "upload result message123213:" + arr_tmp[i]);
                    if(arr_tmp[i]!=null){
                        Upresult = uploadFile(arr_tmp[i], Cus_Picture_Upload);
                        if(Upresult.equals("fail")){
                            break;
                        }
                        Log.d(TAG,"upload result message:"+Upresult);
                    }
                }
                mUI_Handler.post(new Runnable() {

                    public void run() {
                        if(Upresult.equals("fail")){
                            Toast.makeText(tmp, "上傳圖片失敗,請重新上傳", Toast.LENGTH_SHORT).show();
                        }
                        dialog.setProgress(100);
                        dialog.dismiss();
                    }
                });
            }
        });

    }
    //------------------------------------------------------------
    public String uploadFile(String fileName,String cus) {

        String mStarresult="";
        String End = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        HttpURLConnection mUploadConnection = null;
        DataOutputStream mUploadDataOutput = null;
        File mSourceFile = new File(fileName);

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        if (!mSourceFile.isFile()) {

            dialog.dismiss();

            return mStarresult;

        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(mSourceFile);
                URL url = new URL(cus);
                // Open a HTTP  connection to  the URL
                mUploadConnection = (HttpURLConnection) url.openConnection();
                mUploadConnection.setDoInput(true); // Allow Inputs
                mUploadConnection.setDoOutput(true); // Allow Outputs
                mUploadConnection.setUseCaches(false); // Don'no_frame use a Cached Copy
                mUploadConnection.setRequestMethod("POST");
                mUploadConnection.setRequestProperty("Connection", "Keep-Alive");
                mUploadConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
                mUploadConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                mUploadConnection.setRequestProperty("uploaded_file", fileName);

                mUploadDataOutput = new DataOutputStream(mUploadConnection.getOutputStream());

                mUploadDataOutput.writeBytes(twoHyphens + boundary + End);
                mUploadDataOutput.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + End);

                mUploadDataOutput.writeBytes(End);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    mUploadDataOutput.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                mUploadDataOutput.writeBytes(End);
                mUploadDataOutput.writeBytes(twoHyphens + boundary + twoHyphens + End);

                //---read php retrun echo message
                InputStream inputStream = new BufferedInputStream(mUploadConnection.getInputStream());
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = bufReader.readLine()) != null) {
                    builder.append(line + "\n");
                }
                //-------

                if(mUploadConnection.getResponseCode() == 200){
                    Log.d(TAG,"mUploadConnection status =200 connect");
                    // mStarresult = mUploadConnection.getResponseMessage();
                    mStarresult = builder.toString();
                    Log.d("uploadFile", "HTTP Response is : "
                            +   mStarresult );
                }
                //close the streams //
                fileInputStream.close();
                mUploadDataOutput.flush();
                mUploadDataOutput.close();
                inputStream.close();

            }
            catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG,"exception");
            }
            //prodialogcount= prodialogcount+persioncount;
            prodialogcount =prodialogcount+1;
            dialog.setProgress(prodialogcount);
            return mStarresult;
        }
    }
    //img digreesclae
    //----------------------------------------------------------------
    public int DigreeImg(String tmp){
        int mTmp=0;
        ExifInterface imgExif = null;
        try {
            imgExif = new ExifInterface(tmp);
        } catch (IOException e) {
            e.printStackTrace();
            imgExif = null;
        }
        if (imgExif != null) {
            int Rotation = imgExif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch (Rotation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    mTmp = seconddigree;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    mTmp = thriddigree;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    mTmp = fourdigree;
                    break;
                default:
                    mTmp = firstdigree;
                    break;
            }
        }
        return  mTmp;
    }

    //write file
    public void FileCreate(){
        File dir;
        dir = new File(Upload_Path);
        if (!dir.exists() ){
            Log.d(TAG, "tmp2");
            dir.mkdir();
        };

        dir= new File(Upload_menu_Path);
        if(!dir.exists()) {
            dir.mkdir();
            Log.d(TAG, "tmp3");
        } else {
            Log.d(TAG, "tmp2");
            if (dir.isDirectory())
            {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++)
                {
                    new File(dir, children[i]).delete();
                }
            }
        }
        dir= new File(Upload_menu_Bir_Path);
        if(!dir.exists()) {
            dir.mkdir();
            Log.d(TAG, "tmp3");
        } else {
            Log.d(TAG, "tmp2");
            if (dir.isDirectory())
            {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++)
                {
                    new File(dir, children[i]).delete();
                }
            }
        }
    }
    //---------------------------------------------------------------
    public void Write_File_Path_Bitmap(String tmp,Bitmap Bit_tmp){

        FileOutputStream out;

        try {
            out = new FileOutputStream(tmp);
            Bit_tmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //-------
    public Bitmap Bitmaprecyle(Bitmap tmp){
        if(tmp != null && !tmp.isRecycled()){
            // 回收并且置为null
            tmp .recycle();
            tmp = null;
            System.gc();
            Log.d(TAG, "Bitmaprecyle");
        }
        return tmp;
    }
}
