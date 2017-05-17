package com.example.nick.joeyi_android2;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;



/**
 * Created by Wilson on 2015/5/14.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver{

    @Override
    public void onReceive(final Context context, Intent intent2) {

        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bundle extras = intent2.getExtras();
        String title,message,recive,work_no;
        final String mb_no;
        recive=extras.getString("message");
        String [] temp,qr;
        int nid =(int)(Math.random()* 99 + 1);
        Intent intent= new Intent(context,contact_reply.class);
        if(recive!=null){
            qr = recive.split("###");
            if(qr[0].equals("qr")){
                temp = qr[1].split("%%%");
                Log.d("intent0506", String.valueOf(temp.length));
                title="廠商QRcode扣款通知";

                message=temp[0];
                mb_no=temp[1];
                Log.e("mb_no*****555555",mb_no);
                NotificationManager notificationManager =
                        (NotificationManager) context
                                .getSystemService(Context.NOTIFICATION_SERVICE);

                // Intent 的旗標常數參數設定
                intent= new Intent(context,qr_new.class);
                Bundle bundle = new Bundle();
                bundle.putString("mb_no",mb_no);
                bundle.putString("go_from","push");
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                int requestCode = 1;
                PendingIntent pendingIntent = PendingIntent.getActivity(context,
                        requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification.Builder builder = new Notification.Builder(context);


                builder.setSmallIcon(R.drawable.amalogo) // 通知服務 icon
                        .setContentTitle(title) // 標題
                        .setContentText(message) // 內文
                        .setContentIntent(pendingIntent) // 設定Intent服務
                        .setAutoCancel(true)
                        .setStyle(style(message))
                        .setSound(uri);

                Log.d("push", "android.resource://" + context.getPackageName() + "/" + R.raw.push_ring);
                builder.setPriority(Notification.PRIORITY_HIGH); // 亦可帶入Notification.PRIORITY_MAX參數
                Notification notification = builder.build();
                notificationManager.notify(nid, notification); // 發佈Notification
                // }
            }else{
                temp = recive.split("%%%");
                Log.d("abcabc", recive);
                title="已有新回覆，請點擊查看";
                if(temp.length==4){
                    if(temp[3].equals("down_push")){
                        title="下線購物通知";
                        //nid= 1;
                        intent= new Intent(context,news2.class);
                    }
                    if(temp[3].equals("join_push")){
                        title="新會員入會通知";
                        //nid = 2;
                        intent= new Intent(context,news2.class);
                    }
                    if(temp[3].equals("bonus_push")){
                        title="獎金發布通知";
                        //nid = 3;
                        intent= new Intent(context,news2.class);
                    }
                    if(temp[3].equals("form")){
                        title="公司發布訊息通知";
                        //nid = 3;
                        intent= new Intent(context,news2.class);
                    }
                }

                message=temp[0];
                work_no=temp[1];
                mb_no=temp[2];
                Log.e("mb_no*****",mb_no);
                NotificationManager notificationManager =
                        (NotificationManager) context
                                .getSystemService(Context.NOTIFICATION_SERVICE);



                // Intent 的旗標常數參數設定

                Bundle bundle = new Bundle();
                bundle.putString("go_to","detail");
                bundle.putString("go_from","online_notify");
                bundle.putString("work_no",work_no);
                bundle.putString("mb_no",mb_no);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                int requestCode = 1;
                PendingIntent pendingIntent = PendingIntent.getActivity(context,
                        requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Bitmap bmp1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.amalogo);
                    Notification.Builder builder = new Notification.Builder(context);
                    builder.setSmallIcon(R.drawable.logonoti)
                            .setLargeIcon(bmp1)
                            .setContentTitle(title) // 標題
                            .setContentText(message) // 內文
                            .setContentIntent(pendingIntent) // 設定Intent服務
                            .setAutoCancel(true)
                            .setStyle(style(message))
                            .setSound(uri);

                    builder.setPriority(Notification.PRIORITY_HIGH); // 亦可帶入Notification.PRIORITY_MAX參數
                    Notification notification = builder.build();
                    notificationManager.notify(nid, notification); // 發佈Notification
            }

        }

        setResultCode(Activity.RESULT_OK);
    }
    private Notification.Style style(String message){
        int a=0;
        double msg_len=message.length();
        double aaa=msg_len/20;
        int arr_num=(int)Math.ceil(aaa);

        String[] lines = new String[arr_num]; // InboxStyle要顯示的字串內容

        while(arr_num>0){
            if(((a+1)*20)>(int)msg_len){
                lines[a]=message.substring(a*20,(int)msg_len);
            }else{
                lines[a]=message.substring(a*20, (a+1)*20);
            }
            a++;
            arr_num--;
        }
        Notification.InboxStyle inboxStyle = new Notification.InboxStyle(); // 建立InboxStyle
        for (int i = 0; i < lines.length; i++) {
            inboxStyle.addLine(lines[i]); // 將字串加入InboxStyle
        }
        return inboxStyle;
    }
}