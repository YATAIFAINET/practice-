package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.magiclen.magicqrcode.QRCodeEncoder;


/**
 * Created by YTT1 on 2016/7/19.
 */
public class QrCode_Option {
    private String TAG ="Fainet";
    //--------------------------------------------------------------------------------
    public  String Tmp_GetRid(Activity Act_tmp){
        String tmp="";

        MagicLenGCM magicLenGCM = new MagicLenGCM(Act_tmp);
        magicLenGCM.openGCM();

        while(tmp.equals("")){
            tmp=magicLenGCM.getRegistrationId();
        }

        return tmp;
    }

    //--------------------------------------------------------------------------------
    public void QrCheckInit_GetRid(ImageView tmp,Activity Act_tmp,String tmp_login_fac_no,String path) {

        Canvas canvas;
        Bitmap baseBitmap;
        String TEXT="";

        baseBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(baseBitmap);

        Log.d(TAG,"wadsasd"+tmp_login_fac_no);

        if(!(tmp_login_fac_no ==null)){
        //TEXT="qr_code_mb@@"+tmp_login_fac_no;
        TEXT =path+tmp_login_fac_no;
            Log.d(TAG,"TEXT"+TEXT);
        QRCodeEncoder qr = new QRCodeEncoder(TEXT);
        Log.d(TAG,TEXT);
        boolean[][] qrData = qr.encode();
        drawQRCode(canvas, qrData, tmp, baseBitmap);
        } else {
            Toast.makeText(Act_tmp,"Rid Error",Toast.LENGTH_SHORT).show();
        }
    }
    //--------------------------------------------------------------------------------
    public static void drawQRCode(final Canvas canvas, final boolean[][] qrData,final ImageView img, final Bitmap baseBitmap) {
        final Paint paint = new Paint();
        final int width = canvas.getWidth();
        final int height = canvas.getHeight();

        //畫背景(全白)
        paint.setColor(Color.WHITE);
        canvas.drawRect(new Rect(0, 0, width, height), paint);

        final int imageSize = Math.min(width, height);
        final int length = qrData.length;
        final int size = imageSize / length;
        final int actualImageSize = size * length;
        final int offsetImageX = (width - actualImageSize) / 2;
        final int offsetImageY = (height - actualImageSize) / 2;

        //畫資料(true為黑色)
        paint.setColor(Color.BLACK);
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (qrData[i][j]) {
                    final int x = i * size + offsetImageX;
                    final int y = j * size + offsetImageY;
                    canvas.drawRect(new Rect(x, y, x + size, y + size), paint);
                }
            }
        }
        img.setImageBitmap(baseBitmap);
    }
}
