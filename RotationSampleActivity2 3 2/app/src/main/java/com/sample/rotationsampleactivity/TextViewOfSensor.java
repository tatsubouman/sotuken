package com.sample.rotationsampleactivity;

import java.text.NumberFormat;
import android.widget.TextView;

public class TextViewOfSensor {
    protected TextView textViewXg, textViewYg, textViewZg, textViewGyro;
    protected float[] gyro = new float[3];
    protected TextView textViewX, textViewY, textViewZ, textViewAccel;
    protected float[] accels = new float[3];
    //protected TextView textViewAzimuth, textViewPitch, textViewRoll;
    protected float[] orientations = new float[3];




    public TextViewOfSensor(){

    }


    public void TextViewGyro(){
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        textViewGyro.setText("端末に加えられた加速度");
        textViewXg.setText("X : " + numberFormat.format(gyro[0]));
        textViewYg.setText("Y : " + numberFormat.format(gyro[1]));
        textViewZg.setText("Z : " + numberFormat.format(gyro[2]));
    }

    public void TextViewAccel(){
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        textViewAccel.setText("端末にかかっている重力加速度を含む加速度");
        textViewX.setText("X : " + numberFormat.format(accels[0]));
        textViewY.setText("Y : " + numberFormat.format(accels[1]));
        textViewZ.setText("Z : " + numberFormat.format(accels[2]));
    }

   /* public void TextViewMagnetic(){
        textViewAzimuth.setText("方位角 : " + (int) Math.toDegrees(orientations[0]));
        textViewPitch.setText("傾斜 : " + (int) Math.toDegrees(orientations[1]));
        textViewRoll.setText("回転 : " + (int) Math.toDegrees(orientations[2]));
    }*/
}
