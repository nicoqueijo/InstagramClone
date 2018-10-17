package com.nicoqueijo.android.instagramclone.utils;

import android.os.Environment;

public class FilePaths {

    // "storage/emulated/0"
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES = ROOT_DIR + "/DCIM/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/camera";
}
