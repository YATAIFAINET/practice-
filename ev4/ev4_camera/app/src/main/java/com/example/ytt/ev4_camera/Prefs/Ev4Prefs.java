package com.example.ytt.ev4_camera.Prefs;

import android.os.Environment;
import android.provider.MediaStore;

/**
 * Created by ytt on 2017/5/12.
 */

public class Ev4Prefs {

    public static final String IMG_DIR= MediaStore.Images.Media.getContentUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath()).getPath();


}
