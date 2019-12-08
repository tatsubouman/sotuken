package com.sample.rotationsampleactivity;

import android.hardware.SensorEvent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.TextView;

public class ShowInfoOfSensor {

    public TextView textInfo, textInfo2;

    public ShowInfoOfSensor(){

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showInfo(SensorEvent event, int pattern) {
        // センサー名
        StringBuffer info = new StringBuffer("Name: ");
        info.append(event.sensor.getName());
        info.append("\n");

        // ベンダー名
        info.append("Vendor: ");
        info.append(event.sensor.getVendor());
        info.append("\n");

        // 型番
        info.append("Type: ");
        info.append(event.sensor.getType());
        info.append("\n");

        // 最小遅れ
        int data = event.sensor.getMinDelay();
        info.append("Mindelay: ");
        info.append(String.valueOf(data));
        info.append(" usec\n");

        // 最大遅れ
        data = event.sensor.getMaxDelay();
        info.append("Maxdelay: ");
        info.append(String.valueOf(data));
        info.append(" usec\n");

        // レポートモード
        data = event.sensor.getReportingMode();
        String stinfo = "unknown";
        if (data == 0) {
            stinfo = "REPORTING_MODE_CONTINUOUS";
        } else if (data == 1) {
            stinfo = "REPORTING_MODE_ON_CHANGE";
        } else if (data == 2) {
            stinfo = "REPORTING_MODE_ONE_SHOT";
        }
        info.append("ReportingMode: ");
        info.append(stinfo);
        info.append("\n");

        // 最大レンジ
        info.append("MaxRange: ");
        float fData = event.sensor.getMaximumRange();
        info.append(String.valueOf(fData));
        info.append("\n");

        // 分解能
        info.append("Resolution: ");
        fData = event.sensor.getResolution();
        info.append(String.valueOf(fData));
        info.append(" m/s^2\n");

        // 消費電流
        info.append("Power: ");
        fData = event.sensor.getPower();
        info.append(String.valueOf(fData));
        info.append(" mA\n");

        if (pattern == 1) {
            textInfo.setText(info);
        } else if (pattern == 2) {
            textInfo2.setText(info);
        }
    }
}
