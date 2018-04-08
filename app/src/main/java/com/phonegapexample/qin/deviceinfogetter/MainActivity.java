package com.phonegapexample.qin.deviceinfogetter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private static String TAG = "test_tag";

    public static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 33;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);
//                    return true;
//                case R.id.navigation_dashboard:
//                    mTextMessage.setText(R.string.title_dashboard);
//                    return true;
//                case R.id.navigation_notifications:
//                    mTextMessage.setText(R.string.title_notifications);
//                    return true;
//            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //分辨率----
        //cpu设置-----
        //内存设置-----
        //手机厂商-----
        //手机型号-----
        //手机号码-----
        //imei------
        //imsi-----
        //simserial
        //android Id------
        //mac-----



        Log.v(TAG, "running send");

        JSONObject infoObj = getInfoJson();
        Log.v(TAG, "infoObj:" + infoObj.toString());


        String url = null;
        try {
            url = "http://agent1.pconline.com.cn:8060/counter/adAnalyse/fflloogg.jsp?m=add&f=emulator_param&c="
                    + URLEncoder.encode(infoObj.toString(), "gbk");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        sendGetMessage(url);


        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("分辨率: w:").append(infoObj.optString("widthPixels")).append(", h:").append(infoObj.optString("heightPixels")).append("\n");


        stringBuilder.append("手机型号: ").append(infoObj.optString("model")).append("\n")
                .append("手机厂商: ").append(infoObj.optString("manufacturer")).append("\n")
                .append("hardware: ").append(infoObj.optString("hardware")).append("\n")
                .append("mac: ").append(infoObj.optString("mac")).append("\n");

        stringBuilder.append("imei: ").append(infoObj.optString("imei")).append("\n")
                .append("imsi: ").append(infoObj.optString("imsi")).append("\n")
                .append("line1Number: ").append(infoObj.optString("line1Number")).append("\n")
                .append("simSerialNumber: ").append(infoObj.optString("simSerialNumber")).append("\n");
        stringBuilder.append("androidId: ").append(infoObj.optString("androidId")).append("\n");




        mTextMessage = (TextView) findViewById(R.id.message);
        mTextMessage.setText(stringBuilder.toString());

    }


    public void clickSend(View view) throws UnsupportedEncodingException {

        Log.v(TAG, "running send");

        JSONObject infoObj = getInfoJson();
        Log.v(TAG, "infoObj:" + infoObj.toString());


        String url = "http://agent1.pconline.com.cn:8060/counter/adAnalyse/fflloogg.jsp?m=add&f=emulator_param&c="
                + URLEncoder.encode(infoObj.toString(), "gbk");

        sendGetMessage(url);

    }


    public JSONObject getInfoJson() {

        JSONObject returnObj = new JSONObject();

        try {

            //from screen
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(dm);

            int densityDpi = dm.densityDpi;
            int widthPixels = dm.widthPixels;
            int heightPixels = dm.heightPixels;


            returnObj.put("densityDpi", densityDpi);
            returnObj.put("widthPixels", widthPixels);
            returnObj.put("heightPixels", heightPixels);

            Log.v(TAG, "in dm2: widthPixels=" + widthPixels + ", heightPixels=" + heightPixels + ", densityDpi=" + densityDpi);


            //from build
            String model = Build.MODEL;
            String manufacturer = Build.MANUFACTURER;
            String hardware = Build.HARDWARE;
            String ver = Build.VERSION.RELEASE;
            int sdkInt = Build.VERSION.SDK_INT;
            String serial = Build.SERIAL;
//            String cpuAbi = Build.CPU_ABI;
//            String cpuAbi2 = Build.CPU_ABI2;


            returnObj.put("model", model);
            returnObj.put("manufacturer", manufacturer);
            returnObj.put("hardware", hardware);
            returnObj.put("model", model);
            returnObj.put("ver", ver);
            returnObj.put("sdk_int", sdkInt);
            returnObj.put("serial", serial);
//            returnObj.put("cpuAbi", cpuAbi);
//            returnObj.put("cpuAbi2", cpuAbi2);


            //from linux
            JSONObject cpuInfoJson = getCpuInfoJson();
            JSONObject memInfoJson = getMemInfoJson();
            String mac = getMac();


            returnObj.put("cpu", cpuInfoJson);
            returnObj.put("mem", memInfoJson);
            returnObj.put("mac", mac);

            Log.v(TAG, "in dm2: model=" + model + ", manufacturer=" + manufacturer);
            Log.v(TAG, "in dm2: cpuAbi=, cpuInfo=" + cpuInfoJson.toString());
            Log.v(TAG, "in dm2: memInfo=" + memInfoJson.toString());
            Log.v(TAG, "in dm2: ver=" + ver + ", sdk_int=" + sdkInt + ", serial=" + serial);
            Log.v(TAG, "in dm2: mac=" + mac);


            //from TelephonyManager
            TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);



            String line1Number = "--";
            String imsi = "--";
            String simSerialNumber = "--";


            int readsmsCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_SMS);

            int readPhoneStatusCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE);



            Log.v(TAG, "read sms check:" + readsmsCheck + ", " + (PackageManager.PERMISSION_GRANTED == readsmsCheck));
            if(PackageManager.PERMISSION_GRANTED == readPhoneStatusCheck) {

            } else {
//                ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE},
//                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            }

            Log.v(TAG, "requestPermissions:" + MY_PERMISSIONS_REQUEST_READ_CONTACTS);


            //check again
            readsmsCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_SMS);

            readPhoneStatusCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE);

            if(PackageManager.PERMISSION_GRANTED == readPhoneStatusCheck) {
                line1Number = telephonyManager.getLine1Number();
                imsi = telephonyManager.getSubscriberId();
                simSerialNumber = telephonyManager.getSimSerialNumber();
            }

            if(PackageManager.PERMISSION_GRANTED == readsmsCheck) {
                line1Number = telephonyManager.getLine1Number();
                imsi = telephonyManager.getSubscriberId();
            }


            //PackageManager.PERMISSION_GRANTED

            int phoneStatusCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE);

            Log.v(TAG, "read phoneStatus check 111:" + phoneStatusCheck);


            String imei = telephonyManager.getDeviceId();

            returnObj.put("imei", imei);
            returnObj.put("line1Number", line1Number);
            returnObj.put("imsi", imsi);
            returnObj.put("simSerialNumber", simSerialNumber);


            Log.v(TAG, "in dm2: imei=" + imei + ", line1Number=" + line1Number + ", imsi=" + imsi + ", simSerialNumber=" + simSerialNumber);


            //from Secure
            String androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

            returnObj.put("androidId", androidId);

            Log.v(TAG, "in dm2: androidId=" + androidId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return returnObj;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 33: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    /**
     * 请求一次url
     */
    public void sendGetMessage(final String sendUrl) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //发送计数器请求
                HttpURLConnection connection = null;
                try {

                    URL url = new URL(sendUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(10000);
                    connection.setRequestMethod("GET");
                    connection.setUseCaches(false);
                    connection.setRequestProperty("User-Agent", "test ua");
                    connection.setRequestProperty("Referer", "test referer");

                    connection.connect();
                    int resCode = connection.getResponseCode();
                    Log.v(TAG, "in GET code:" + resCode);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    public static String getMac() {
        String macSerial = null;
        String str = "";

        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
    }


    public static JSONObject getCpuInfoJson() throws JSONException {

        JSONObject cpuObj = new JSONObject();
        try {
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());

            BufferedReader bufferedReader = new BufferedReader(ir);

            String line;
            String[] infoArr;
            while((line = bufferedReader.readLine()) != null) {

                infoArr = line.split(":");
                if(infoArr.length != 2) {
                    continue;
                }
                cpuObj.put(infoArr[0].trim(), infoArr[1].trim());

            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return cpuObj;
    }



    public static String getCpuInfo() {
        String str = "";

        try {
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());

            BufferedReader bufferedReader = new BufferedReader(ir);

            String line;
            while((line = bufferedReader.readLine()) != null) {
                str += line + "\n";

            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return str;
    }



    public static JSONObject getMemInfoJson() throws JSONException {

        JSONObject infoObj = new JSONObject();

        try {
            Process pp = Runtime.getRuntime().exec("cat /proc/meminfo ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());

            BufferedReader bufferedReader = new BufferedReader(ir);

            String line;
            String[] infoArr;
            while((line = bufferedReader.readLine()) != null) {
                infoArr = line.split(":");
                if(infoArr.length != 2) {
                    continue;
                }
                infoObj.put(infoArr[0].trim(), infoArr[1].trim());

            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return infoObj;
    }


    public static String getMemInfo() {
        String str = "";

        try {
            Process pp = Runtime.getRuntime().exec("cat /proc/meminfo ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());

            BufferedReader bufferedReader = new BufferedReader(ir);

            String line;
            while((line = bufferedReader.readLine()) != null) {
                str += line + "\n";

            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return str;
    }

}
