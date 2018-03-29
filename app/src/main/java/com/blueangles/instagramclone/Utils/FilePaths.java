package com.blueangles.instagramclone.Utils;

import android.os.Environment;

/**
 * Created by Ashith VL on 10/19/2017.
 */

public class FilePaths {
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES_DOWNLOAD = ROOT_DIR + "/Download";
    public String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/camera";

    public String FIREBASE_IMAGE_STORAGE = "photos/users/";
}
