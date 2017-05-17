package com.example.nick.joeyi_android2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class AsyncImageFileLoader {
    private static String TAG = "AsyncImageFileLoader";
    private HashMap<String, SoftReference<Bitmap>> imageCache;
    private HandlerThread mThread;
    private Handler mThreadHandler;

    public AsyncImageFileLoader() {
        imageCache = new HashMap<String, SoftReference<Bitmap>>();
    }

    public Bitmap loadBitmap(final String imageFile, final int show_width, final int show_height
            , final ImageCallback imageCallback) {
        //如果此圖片已讀取過的話，將會暫存在cache中，所以可以直接從cache中讀取
        if (imageCache.containsKey(imageFile)) {
            SoftReference<Bitmap> softReference = imageCache.get(imageFile);
            Bitmap bmp = softReference.get();
            if (bmp != null) {
                return bmp;
            }
        }
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.imageCallback((Bitmap) message.obj, imageFile);
            }
        };

        new Thread() {
            @Override
            public void run() {
                //sleep 500ms 讓GridView可以先顯示所有item，否則會因為開始讀取圖片而使系統忙碌
                //造成圖片讀取完畢後才顯示所有item
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Bitmap bmp = loadImageFromFile(imageFile, show_width, show_height);
                imageCache.put(imageFile, new SoftReference<Bitmap>(bmp));
                Message message = handler.obtainMessage(0, bmp);
                handler.sendMessage(message);
            }
        }.start();
        return null;
    }
    public Bitmap loadImageFromFile(String file, int display_width, int display_height) {

        Bitmap bmp = readBitmap(file, display_width, display_height);
        return bmp;
    }

    public interface ImageCallback {
        public void imageCallback(Bitmap imageBitmap, String imageFile);
    }

    private static int computeSampleSize(BitmapFactory.Options opts, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(opts, minSideLength, maxNumOfPixels);

        int roundedSize;

        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        Log.d(TAG, "roundedSize = " + roundedSize);

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options opts, int minSideLength, int maxNumOfPixels) {
        double w = opts.outWidth;
        double h = opts.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (maxNumOfPixels == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    private Bitmap readBitmap(String img_file, int show_width, int show_height) {
        Log.d(TAG, "readBitmap");

       /* BitmapFactory.Options opt = new BitmapFactory.Options();

        opt.inJustDecodeBounds = true; //設定BitmapFactory.decodeStream不decode，只抓取原始圖片的長度和寬度
        //BitmapFactory.decodeFile(img_file, opt);
        try ( InputStream is = new URL(img_file).openStream() ) {
            Bitmap bitmap = BitmapFactory.decodeStream( is );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inPurgeable  = true;
        opt.inInputShareable = true;
        //計算適合的縮放大小，避免OutOfMenery
        opt.inSampleSize = computeSampleSize(opt, -1, show_width*show_height);
        opt.inJustDecodeBounds = false;//設定BitmapFactory.decodeStream需decodeFile

        Bitmap bmp = BitmapFactory.decodeFile(img_file, opt);

        System.gc(); // system garbage recycle

        if(bmp != null)
        {
            Log.d(TAG, "decodeFile success");

            return bmp;
        }
        else
        {
            Log.d(TAG, "bmp == null");
            return null;
        }
    }*/
        URL imgUrl;
        //Bitmap defaultImg = BitmapFactory.decodeResource(con.getResources(),wu.listview.webimg.R.drawable.icon);
        Bitmap webImg = null;
        try {
            imgUrl = new URL(img_file);
        } catch (MalformedURLException e) {
            Log.d("MalformedURLException", e.toString());
            return null;//抓不到網路圖時, 讀預設圖片
        }
        try {
            HttpURLConnection httpURLConnection =
                    (HttpURLConnection) imgUrl.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(false);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            int length = (int) httpURLConnection.getContentLength();
            int tmpLength = 512;
            int readLen = 0, desPos = 0;
            byte[] img = new byte[length];
            byte[] tmp = new byte[tmpLength];
            if (length != -1) {
                while ((readLen = inputStream.read(tmp)) > 0) {
                    System.arraycopy(tmp, 0, img, desPos, readLen);
                    desPos += readLen;
                }
                webImg = BitmapFactory.decodeByteArray(img, 0, img.length);
            }
            httpURLConnection.disconnect();
        } catch (IOException e) {
            Log.d("IOException", e.toString());
            return null; //抓不到網路圖時, 讀預設圖片
        }
        return webImg;
    }
}