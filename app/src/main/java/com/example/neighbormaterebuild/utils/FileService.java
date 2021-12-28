package com.example.neighbormaterebuild.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

//import com.example.neighbormaterebuild.BuildConfig;

import com.example.neighbormaterebuild.BuildConfig;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileService {

    private static final String LOG_TAG = "ExternalStorageDemo";

    private final String fileName = "user.txt";
    String filePath = "app";
    File myExternalFile;
    Context context;

    public FileService() {
    }

    public FileService(Context context) {
        this.context = context;
        File sdCardDir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            sdCardDir = this.context.getFilesDir();
        }
        else
        {
            sdCardDir = Environment.getExternalStorageDirectory();
        }

        Log.e("sdCardDir location", sdCardDir.getPath());
        File dir = new File (sdCardDir.getAbsolutePath() +
                (BuildConfig.FLAVOR.equals("stg") ? "/com.example.neighbormaterebuild.stg/app" : "/com.example.neighbormaterebuild.dev/app"));

        Log.e("dir location", dir.getPath());
        dir.mkdirs();
        Log.e("dir location", dir.getPath());
        myExternalFile = new File(dir, fileName);
    }

    public void writeFile(String deviceID, String usercode, String password) {
        try {
            FileOutputStream fos = new FileOutputStream(myExternalFile);
            String line = deviceID + "\n" + usercode + "\n" + password;
            fos.write(line.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFile() {
        try {
            FileInputStream fis = new FileInputStream(myExternalFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(in));
            String strLine;
            String myData = "";

            while ((strLine = br.readLine()) != null) {
                myData = myData + strLine + "\n";
            }
            in.close();
            Log.e("myData location", myData);
            return myData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
