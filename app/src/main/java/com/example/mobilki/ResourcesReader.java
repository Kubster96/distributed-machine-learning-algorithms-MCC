package com.example.mobilki;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.BATTERY_SERVICE;

public class ResourcesReader {

    private Context context;

    public ResourcesReader(Context context) {
        this.context = context;
    }

    private Resources readResources() {


        long memory = readTotalRam();
        Map<String, String> cpuInfo = getCPUInfo();
        int battery = getBatteryLevel();
        String network = getNetwork();
        boolean hasWifi = hasWifi();
        int downloadSpeed = getDownloadSpeed();
        int uploadSpeed = getUploadSpeed();

        Resources resources = new Resources(null, 0, 0, 0, 0, false, 0, 0, null);
        return resources;
    }

    private int getUploadSpeed() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return nc.getLinkUpstreamBandwidthKbps();
    }

    private int getDownloadSpeed() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return nc.getLinkDownstreamBandwidthKbps();
    }

    private boolean hasWifi() {
        ConnectivityManager connManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isAvailable();
    }

    private String getNetwork() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = telephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_1xRTT: return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_CDMA: return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EDGE: return "EDGE";
            case TelephonyManager.NETWORK_TYPE_EHRPD: return "eHRPD";
            case TelephonyManager.NETWORK_TYPE_EVDO_0: return "EVDO rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A: return "EVDO rev. A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B: return "EVDO rev. B";
            case TelephonyManager.NETWORK_TYPE_GPRS: return "GPRS";
            case TelephonyManager.NETWORK_TYPE_HSDPA: return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSPA: return "HSPA";
            case TelephonyManager.NETWORK_TYPE_HSPAP: return "HSPA+";
            case TelephonyManager.NETWORK_TYPE_HSUPA: return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_IDEN: return "iDen";
            case TelephonyManager.NETWORK_TYPE_LTE: return "LTE";
            case TelephonyManager.NETWORK_TYPE_UMTS: return "UMTS";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN: return "Unknown";
        }
        throw new RuntimeException("New type of network");
    }

    private int getBatteryLevel() {
        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        if (bm != null) {
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }
        return 0;
    }

    private static Map<String, String> getCPUInfo() {
        Map<String, String> output = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader (new FileReader("/proc/cpuinfo"));
            String str;

            while ((str = br.readLine ()) != null) {
                String[] data = str.split (":");
                if (data.length > 1) {
                    String key = data[0].trim ().replace (" ", "_");
                    if (key.equals ("model_name")) key = "cpu_model";
                    output.put (key, data[1].trim ());
                }
            }
            br.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        return output;
    }

    private long readTotalRam() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.totalMem;
    }
}
