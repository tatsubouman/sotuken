package com.sample.rotationsampleactivity;

import java.io.*;
import java.text.NumberFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.Window;


import android.view.View;
import android.widget.*;

import java.io.FileOutputStream;
import java.util.Calendar;



public class RotationSampleActivity extends AppCompatActivity
        implements SensorEventListener{

    //照度の数値を表示するためのTextview
    private TextView textViewLight;

    // センサーマネージャー
    private SensorManager sensorManager;

    // 加速度センサーにリスナーとして自身を登録済みか否か
    private boolean registerAccelerometer;

    //ジャイロセンサーにリスナーとして自身を登録済みか否か
    private boolean registerGyroscope;
    // 地磁気センサーにリスナーとして自身を登録済みか否か
    private boolean registerMagneticFieldSensor;
    //重力加速度なしの加速度センサーとして自身を登録済みか否か
    private boolean registerLinearAccelerometer;


    // 地磁気センサーが返す値
    private float[] magneticFields = new float[3];

    // 画面の向きを表す定数値
    private int rotation;

    protected volatile boolean stopRun = false;


    private ShowInfoOfSensor SIoS;
    protected TextViewOfSensor textviewofsensor;

    protected float light;
    private TextView brightness;
    protected TextView textViewX, textViewY, textViewZ, textViewAccel, stoptimertask;
    protected TextView textViewlaccelX, textViewlaccelY, textViewlaccelZ;
    protected TextView xCoordinate,yCoordinate;
    protected String fileNameAccelesX, fileNameAccelesY, fileNameAccelesZ, fileNameDebug, fileNameAcceles;
    protected String fileNamelAccelesX, fileNamelAccelesY, fileNamelAccelesZ;
    //重力加速度あり
    protected float[] accels = new float[3];
    //重力加速度なし
    protected float[] laccels = new float[3];
    protected float[] gyro = new float[3];
    protected TextView textViewXg, textViewYg, textViewZg;
    protected Runnable runnable;
    final Handler handler = new Handler();


    private float buf_x[] = new float[3];
    private float buf_y[] = new float[3];
    private float buf_z[] = new float[3];
    private float buf_gyro_x[] = new float[3];
    private float buf_gyro_y[] = new float[3];
    private float buf_gyro_z[] = new float[3];
    private float buf_speed_x[] = new float[3];
    private float buf_speed_y[] = new float[3];
    private float buf_speed_z[] = new float[3];
    private float accelerate0[] = new float[3];
    private float speed0[] = new float[3];
    private float Comp_disp;
    protected float disp0[] = new float[3];
    private float true_disp_x;
    private float true_disp_y;
    private float a[] = new float[3];
    private float b[] = new float[3];
    private float c[] = new float[3];
    private float Gyro_a[] = new float[3];
    private float Gyro_b[] = new float[3];
    private float Gyro_c[] = new float[3];
    private float Angle[] = new float[3];
    private float Current_Angle[] = new float[3];
    private float accelerate, speed, disp;
    private int cnt_a = 0;
    private int cnt_s = 0;
    private long EndTime;
    private long StartTime;
    private float setTimeSpan;
    private float accelTimeSpan[] = new float[3];
    private float speedTimeSpan[] = new float[5];
    TextView textViewDisp;
    private String speed_string, disp_string;

    protected TextView textViewAzimuth;
    protected float[] orientations = new float[3];

    private String br = System.getProperty("line.separator");

    private CanvasAct canvasAct;






    /**
     * アクティビティ作成時に呼び出されるメソッド
     */
    @SuppressLint("CutPasteId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // スーパークラスのonCreateを呼び出す
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        SIoS = new ShowInfoOfSensor();
        textviewofsensor = new TextViewOfSensor();



        // センサーマネージャーを取得する
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // メンバ変数を初期化する
        registerAccelerometer = false;
        registerMagneticFieldSensor = false;
        registerLinearAccelerometer = false;

        for (int i = 0; i < 3; i++) {
            accels[i] = 0.0f;
            laccels[i] = 0.0f;
            gyro[i] = 0.0f;
            //textviewofsensor.gyro[i] = 0.0f;
            magneticFields[i] = 0.0f;
            //textviewofsensor.orientations[i] = 0.0f;
        }
        light = 0.0f;

        // TextViewを取得する
        textViewX = findViewById(R.id.textViewX);
        textViewY = findViewById(R.id.textViewY);
        textViewZ = findViewById(R.id.textViewZ);
        textViewlaccelX = findViewById(R.id.textViewlaccelX);
        textViewlaccelY = findViewById(R.id.textViewlaccelY);
        textViewlaccelZ = findViewById(R.id.textViewlaccelZ);
        textViewXg = findViewById(R.id.textViewgyroX);
        textViewYg = findViewById(R.id.textViewgyroY);
        textViewZg = findViewById(R.id.textViewgyroZ);
        stoptimertask = findViewById(R.id.stoptimertask);
        textViewLight = findViewById(R.id.textViewLight);
        textViewDisp = findViewById(R.id.disp);
        xCoordinate = findViewById(R.id.x_coordinate);
        yCoordinate = findViewById(R.id.y_coordinate);
        this.canvasAct = findViewById(R.id.canvas_act);
        findViewById(R.id.delete).setOnClickListener(deleteDrawing);
        textViewAzimuth = findViewById(R.id.azimuth);



    }
    View.OnClickListener deleteDrawing = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            canvasAct.buttonFlag = true;
            for(int i = 0; i < 3; i++) {
                disp0[i] = 0;
            }
        }
    };
    //ファイルを保存する
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void saveFile(String file, String flo) {
        // try-with-resources
        try (FileOutputStream fileOutputstream = openFileOutput(file,
                Context.MODE_APPEND);) {

            fileOutputstream.write(flo.getBytes());

        } catch (IOException e) {
            //e.printStackTrace();
        }
    }


    /**
     * アクティビティがポーズ(一時停止)状態になる時に呼び出されるメソッド
     */
    @Override
    protected void onPause() {
        // 加速度センサーや地磁気センサーに対してリスナーが登録済みであれば、
        if (registerAccelerometer || registerMagneticFieldSensor || registerGyroscope || registerLinearAccelerometer) {
            // センサー群に対するリスナーの登録を解除する
            sensorManager.unregisterListener(this);

            // リスナーを登録していない状態であることを示すためにfalseをセットする
            registerAccelerometer = false;
            registerMagneticFieldSensor = false;
            registerGyroscope = false;
            registerLinearAccelerometer = false;
        }

        // スーパークラスのonPauseを呼び出す
        super.onPause();
    }

    /**
     * アクティビティがポーズ(一時停止)状態から復帰する時に呼び出されるメソッド
     */
    @Override
    protected void onResume() {
        // 加速度センサーにリスナーを登録していなければ、
        if (!registerAccelerometer) {
            // 加速度センサーを取得する
            List<Sensor> sensors =
                    sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

            // 加速度センサーの取得に成功したら、
            if (sensors.size() > 0) {
                // 加速度センサーのリスナーとして自身を登録する
                registerAccelerometer = sensorManager.registerListener(
                        this, sensors.get(0), SensorManager.SENSOR_DELAY_FASTEST);
            }
        }

        if (!registerLinearAccelerometer) {
            // 加速度センサーを取得する
            List<Sensor> sensors =
                    sensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);

            // 加速度センサーの取得に成功したら、
            if (sensors.size() > 0) {
                // 加速度センサーのリスナーとして自身を登録する
                registerLinearAccelerometer = sensorManager.registerListener(
                        this, sensors.get(0), SensorManager.SENSOR_DELAY_FASTEST);
            }
        }

        if (!registerGyroscope) {
            // ジャイロセンサーを取得する
            List<Sensor> sensors =
                    sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);

            // ジャイロセンサーの取得に成功したら、
            if (sensors.size() > 0) {
                // ジャイロセンサーのリスナーとして自身を登録する
                registerGyroscope = sensorManager.registerListener(
                        this, sensors.get(0), SensorManager.SENSOR_DELAY_FASTEST);
            }

        }

        // 地磁気センサーにリスナーを登録していなければ、
        if (!registerMagneticFieldSensor) {
            // 地磁気センサーを取得する
            List<Sensor> sensors =
                    sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);

            // 地磁気センサーの取得に成功したら、
            if (sensors.size() > 0) {
                // 地磁気センサーのリスナーとして自身を登録する
                registerMagneticFieldSensor = sensorManager.registerListener(
                        this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
            }
        }

        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_LIGHT);
        if(sensors.size() > 0) {
            Sensor s = sensors.get(0);
            sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_FASTEST);
        }
        // スーパークラスのonResumeを呼び出す
        super.onResume();
    }

    /**
     * センサーの精度が変更された時に呼び出されるメソッド
     *
     * @param sensor   センサー
     * @param accuracy センサーの精度を表す定数値
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // センサーの種類が、加速度センサーであれば
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // 加速度センサーの精度が変更された時に行いたい処理をここに記述する
        }
        //センサーの種類が、加速度センサーであれば
        else if(sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){

        }
        // センサーの種類が、地磁気センサーであれば
        else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            // 地磁気センサーの精度が変更された時に行いたい処理をここに記述する
        }
        //センサーの種類が、ジャイロセンサーであれば
        else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {

        }
    }


    /**
     * センサーの値が変更された時に呼び出されるメソッド
     *
     * @param event センサーから通知された値などを保持するオブジェクト
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        // 数値を小数点以下2桁までの文字列に変換するためのもの
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);

        // 加速度センサーであれば、accelsに新しい値をセットする
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            for (int i = 0; i < 3; i++) {
                accels[i] = event.values[i];
            }
        }
        else if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            for(int i = 0; i < 3; i++){
                laccels[i] = event.values[i];
            }
        }
        // 地磁気センサーであれば、magneticFieldsに新しい値をセットする
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            for (int i = 0; i < 3; i++)
                magneticFields[i] = event.values[i];
        }
        //ジャイロセンサーであれば、gyroに新しい値をセットする
        else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            for (int i = 0; i < 3; i++)
                gyro[i] = event.values[i];
            //SIoS.showInfo(event, 2);
        }
        else if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            light = event.values[0];
            if(light < 100 && !stopRun){
                speed0[0] = 0;
                speed0[1] = 0;
                speed0[2] = 0;
                disp0[0] = 0;
                disp0[1] = 0;
                disp0[2] = 0;
                Current_Angle[0] = 0;
                Current_Angle[1] = 0;
                Current_Angle[2] = 0;
                ToRecord(1);
                stopRun = true;
                //プログラムからストップボタンを押す
                //stopButton.performClick();
            }else if(light >= 100 && stopRun){
                Calendar calendar = Calendar.getInstance();
                //String text = editText.getText().toString();
                fileNameAccelesX = "AccelsX" + calendar.getTime() + ".csv";
                fileNameAccelesY = "AccelsY" + calendar.getTime() + ".csv";
                fileNameAccelesZ = "AccelsZ" + calendar.getTime() + ".csv";
                fileNamelAccelesX = "LinearAccelsX" + calendar.getTime() + "Linear.csv";
                fileNamelAccelesY = "LinearAccelsY" + calendar.getTime() + "Linear.csv";
                fileNamelAccelesZ = "LinearAccelsZ" + calendar.getTime() + "Linear.csv";
                fileNameDebug = "Debug" + calendar.getTime() + ".csv";
                fileNameAcceles = "Accels" + calendar.getTime() + ".csv";

                StartTime = EndTime = 1;
                cnt_a = cnt_s = 1;
                accelTimeSpan[0] = speedTimeSpan[0] = 0f;
                for (int i = 0; i < 3; ++i) {
                    buf_x[i] = 0f;
                    buf_y[i] = 0f;
                    buf_z[i] = 0f;
                    buf_gyro_x[i] = 0f;
                    buf_gyro_y[i] = 0f;
                    buf_gyro_z[i] = 0f;
                    buf_speed_x[i] = 0f;
                    buf_speed_y[i] = 0f;
                    buf_speed_z[i] = 0f;
                    speed0[i] = 0f;
                    disp0[i] = 0f;
                }

                ToRecord(2);
                stopRun = false;
                //プログラムからスターとボタンを押す
                //startButton.performClick();
            }
        }

        textViewLight.setText("照度: "+numberFormat.format(light)+"lux");
        xCoordinate.setText(numberFormat.format(canvasAct.x));
        yCoordinate.setText(numberFormat.format(canvasAct.y));

        //textviewofsensor.TextViewGyro();


        // 傾きの値を算出する際に利用する行列を生成する
        float[] R = new float[9];
        float[] I = new float[9];

        // 加速度センサーと地磁気センサーから取得した値を元に計算を行い、傾きの値を算出する
        SensorManager.getRotationMatrix(R, I, accels, magneticFields);

        // 画面の向きに対応する座標系への変換処理を行う
        switch (rotation) {
            // 標準の縦向きの状態。座標系の変換処理は行わず、傾きの値を取得する
            case Surface.ROTATION_0: {
                SensorManager.getOrientation(R, orientations);
                break;
            }

            // 標準の縦向きの状態から、左に90度回転させた座標系に変換する
            case Surface.ROTATION_90: {
                float[] outR = new float[9];
                SensorManager.remapCoordinateSystem(
                        R, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outR);
                SensorManager.getOrientation(outR, orientations);
                break;
            }

            // 標準の縦向きの状態から、180度回転させた座標系に変換する
            case Surface.ROTATION_180: {
                // 左に90度回転する処理を2回行い、180度回転した座標系へと変換する
                float[] outR = new float[9];
                float[] outR2 = new float[9];
                SensorManager.remapCoordinateSystem(
                        R, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outR);
                SensorManager.remapCoordinateSystem(
                        outR, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outR2);
                SensorManager.getOrientation(outR2, orientations);
                break;
            }

            // 標準の縦向きの状態から、右に90度回転させた横向きの状態
            case Surface.ROTATION_270: {
                // 右に90度回転した座標系へと変換する
                float[] outR = new float[9];
                SensorManager.remapCoordinateSystem(
                        R, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_MINUS_X, outR);
                SensorManager.getOrientation(outR, orientations);
                break;
            }
        }
        textViewAzimuth.setText("方位角 : " + (int) Math.toDegrees(orientations[0]));
    }
    //センサからの加速度の値を20msごとに取得し、CSVファイルとして保存する
    public void ToRecord (int v) {
        if(v == 2) {
            runnable = new Runnable() {
                public void run() {

                    EndTime = System.currentTimeMillis();
                    setTimeSpan = (EndTime - StartTime) / 1000f;
                    StartTime = System.currentTimeMillis();
                    if(setTimeSpan > 1) {
                        setTimeSpan = 0.02f;
                    }

                    accelTimeSpan[cnt_a] = accelTimeSpan[cnt_a - 1] + setTimeSpan;
                    speedTimeSpan[cnt_s] = speedTimeSpan[cnt_s - 1] + setTimeSpan;

                    // 数値を小数点以下2桁までの文字列に変換するためのもの
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    numberFormat.setMaximumFractionDigits(2);
                    // 加速度センサー、ジャイロセンサーから取得した値をTextViewにセットする
                    textViewX.setText("Xaccels : " + numberFormat.format(accels[0]));
                    textViewY.setText("Yaccels : " + numberFormat.format(accels[1]));
                    textViewZ.setText("Zaccels : " + numberFormat.format(accels[2]));
                    textViewXg.setText("Xgyro : " + numberFormat.format(gyro[0]));
                    textViewYg.setText("Ygyro : " + numberFormat.format(gyro[1]));
                    textViewZg.setText("Zgyro : " + numberFormat.format(gyro[2]));
                    textViewlaccelX.setText("Xlaccels : " + numberFormat.format(laccels[0]));
                    textViewlaccelY.setText("Ylaccels : " + numberFormat.format(laccels[1]));
                    textViewlaccelZ.setText("Zlaccels : " + numberFormat.format(laccels[2]));
                    stoptimertask.setText("計測中");
                    canvasAct.invalidate();


                    saveFile(fileNameAccelesX , numberFormat.format(accels[0]));
                    saveFile(fileNameAccelesX, ",");
                    saveFile(fileNameAccelesY, numberFormat.format(accels[1]));
                    saveFile(fileNameAccelesY, ",");
                    saveFile(fileNameAccelesZ, numberFormat.format(accels[2]));
                    saveFile(fileNameAccelesZ, ",");

                    saveFile(fileNamelAccelesX , numberFormat.format(laccels[0]));
                    saveFile(fileNamelAccelesX, ",");
                    saveFile(fileNamelAccelesY, numberFormat.format(laccels[1]));
                    saveFile(fileNamelAccelesY, ",");
                    saveFile(fileNamelAccelesZ, numberFormat.format(laccels[2]));
                    saveFile(fileNamelAccelesZ, ",");

                    saveFile(fileNameAcceles , numberFormat.format(accels[0]));
                    saveFile(fileNameAcceles, ",");
                    saveFile(fileNameAcceles , numberFormat.format(accels[1]));
                    saveFile(fileNameAcceles, ",");
                    saveFile(fileNameAcceles , numberFormat.format(accels[2]));
                    saveFile(fileNameAcceles, br);

                    saveFile(fileNameDebug , numberFormat.format(laccels[0]));
                    saveFile(fileNameDebug, ",");
                    saveFile(fileNameDebug , numberFormat.format(laccels[1]));
                    saveFile(fileNameDebug, ",");
                    saveFile(fileNameDebug , numberFormat.format(laccels[2]));

                    //XYZそれぞれの加速度センサーとジャイロセンサーの値を配列に格納
                    buf_x[cnt_a] = laccels[0];
                    buf_y[cnt_a] = laccels[1];
                    buf_z[cnt_a] = laccels[2];
                    buf_gyro_x[cnt_a] = gyro[0];
                    buf_gyro_y[cnt_a] = gyro[1];
                    buf_gyro_z[cnt_a] = gyro[2];
                    ++cnt_a;
                    ++cnt_s;

                    //3つの加速度を計算に使用するためcnt_aが3の時に計算が開始される
                    if (cnt_a == 3){
                        //ax^2+bx+cの二次関数のa,b,cを求める
                        a[0] = (((buf_x[0] - buf_x[1]) * (accelTimeSpan[0] - (accelTimeSpan[2]))) - ((buf_x[0] - buf_x[2]) * (accelTimeSpan[0] - (accelTimeSpan[1])))) / ((accelTimeSpan[0] - (accelTimeSpan[1])) * (accelTimeSpan[0] - (accelTimeSpan[2])) * ((accelTimeSpan[1]) - (accelTimeSpan[2])));
                        b[0] = (buf_x[0] - buf_x[1]) / (accelTimeSpan[0] - (accelTimeSpan[1])) - a[0] * (accelTimeSpan[0] + (accelTimeSpan[1]));
                        c[0] = buf_x[0] - a[0] * accelTimeSpan[0] * accelTimeSpan[0] - b[0] * accelTimeSpan[0];
                        a[1] = (((buf_y[0] - buf_y[1]) * (accelTimeSpan[0] - (accelTimeSpan[2]))) - ((buf_y[0] - buf_y[2]) * (accelTimeSpan[0] - (accelTimeSpan[1])))) / ((accelTimeSpan[0] - (accelTimeSpan[1])) * (accelTimeSpan[0] - (accelTimeSpan[2])) * ((accelTimeSpan[1]) - (accelTimeSpan[2])));
                        b[1] = (buf_y[0] - buf_y[1]) / (accelTimeSpan[0] - (accelTimeSpan[1])) - a[1] * (accelTimeSpan[0] + (accelTimeSpan[1]));
                        c[1] = buf_y[0] - a[1] * accelTimeSpan[0] * accelTimeSpan[0] - b[1] * accelTimeSpan[0];
                        a[2] = (((buf_z[0] - buf_z[1]) * (accelTimeSpan[0] - (accelTimeSpan[2]))) - ((buf_z[0] - buf_z[2]) * (accelTimeSpan[0] - (accelTimeSpan[1])))) / ((accelTimeSpan[0] - (accelTimeSpan[1])) * (accelTimeSpan[0] - (accelTimeSpan[2])) * ((accelTimeSpan[1]) - (accelTimeSpan[2])));
                        b[2] = (buf_z[0] - buf_z[1]) / (accelTimeSpan[0] - (accelTimeSpan[1])) - a[2] * (accelTimeSpan[0] + (accelTimeSpan[1]));
                        c[2] = buf_z[0] - a[2] * accelTimeSpan[0] * accelTimeSpan[0] - b[2] * accelTimeSpan[0];

                        Gyro_a[0] = (((buf_gyro_x[0] - buf_gyro_x[1]) * (accelTimeSpan[0] - (accelTimeSpan[2]))) - ((buf_gyro_x[0] - buf_gyro_x[2]) * (accelTimeSpan[0] - (accelTimeSpan[1])))) / ((accelTimeSpan[0] - (accelTimeSpan[1])) * (accelTimeSpan[0] - (accelTimeSpan[2])) * ((accelTimeSpan[1]) - (accelTimeSpan[2])));
                        Gyro_b[0] = (buf_gyro_x[0] - buf_gyro_x[1]) / (accelTimeSpan[0] - (accelTimeSpan[1])) - Gyro_a[0] * (accelTimeSpan[0] + (accelTimeSpan[1]));
                        Gyro_c[0] = buf_gyro_x[0] - Gyro_a[0] * accelTimeSpan[0] * accelTimeSpan[0] - Gyro_b[0] * accelTimeSpan[0];
                        Gyro_a[1] = (((buf_gyro_y[0] - buf_gyro_y[1]) * (accelTimeSpan[0] - (accelTimeSpan[2]))) - ((buf_gyro_y[0] - buf_gyro_y[2]) * (accelTimeSpan[0] - (accelTimeSpan[1])))) / ((accelTimeSpan[0] - (accelTimeSpan[1])) * (accelTimeSpan[0] - (accelTimeSpan[2])) * ((accelTimeSpan[1]) - (accelTimeSpan[2])));
                        Gyro_b[1] = (buf_gyro_y[0] - buf_gyro_y[1]) / (accelTimeSpan[0] - (accelTimeSpan[1])) - Gyro_a[1] * (accelTimeSpan[0] + (accelTimeSpan[1]));
                        Gyro_c[1] = buf_gyro_y[0] - Gyro_a[1] * accelTimeSpan[0] * accelTimeSpan[0] - Gyro_b[1] * accelTimeSpan[0];
                        Gyro_a[2] = (((buf_gyro_z[0] - buf_gyro_z[1]) * (accelTimeSpan[0] - (accelTimeSpan[2]))) - ((buf_gyro_z[0] - buf_gyro_z[2]) * (accelTimeSpan[0] - (accelTimeSpan[1])))) / ((accelTimeSpan[0] - (accelTimeSpan[1])) * (accelTimeSpan[0] - (accelTimeSpan[2])) * ((accelTimeSpan[1]) - (accelTimeSpan[2])));
                        Gyro_b[2] = (buf_gyro_z[0] - buf_gyro_z[1]) / (accelTimeSpan[0] - (accelTimeSpan[1])) - Gyro_a[2] * (accelTimeSpan[0] + (accelTimeSpan[1]));
                        Gyro_c[2] = buf_gyro_z[0] - Gyro_a[2] * accelTimeSpan[0] * accelTimeSpan[0] - Gyro_b[2] * accelTimeSpan[0];

                        for (int i = 0; i < 3; ++i){

                            //XYZそれぞれの速度を順に算出する
                            saveFile(fileNameDebug, ",");
                            speed = ((accelTimeSpan[1]) * (func_2d(a[i], b[i], c[i], accelTimeSpan[0]) + (4 * func_2d(a[i], b[i], c[i], accelTimeSpan[1])) + func_2d(a[i], b[i], c[i], accelTimeSpan[2]))) * 1/6 + speed0[i];
                            //saveFile(fileNameDebug , numberFormat.format(speed));
                            speed_string = String.valueOf(speed);
                            saveFile(fileNameDebug , String.format(speed_string));
                            saveFile(fileNameDebug, ",");

                            Angle[i] = ((accelTimeSpan[1]) * (func_2d(Gyro_a[i], Gyro_b[i], Gyro_c[i], accelTimeSpan[0]) + (4 * func_2d(Gyro_a[i], Gyro_b[i], Gyro_c[i], accelTimeSpan[1])) + func_2d(Gyro_a[i], Gyro_b[i], Gyro_c[i], accelTimeSpan[2]))) * 1/6;
                            Current_Angle[i] += Angle[i];
                            saveFile(fileNameDebug , numberFormat.format(Current_Angle[i]));
                            //XYZそれぞれの速度を配列に格納する
                            switch (i)
                            {
                                case 0:
                                    buf_speed_x[i] = speed; //x軸の速度
                                    break;
                                case 1:
                                    buf_speed_y[i] = speed; //y軸の速度
                                    break;
                                case 2:
                                    buf_speed_z[i] = speed; //z軸の速度
                                    break;
                            }
                            //初速度の更新
                            speed0[i] = speed;
                        }

                        //cnt_a及び１つ目の加速度の値を初期化
                        cnt_a = 1;
                        accelTimeSpan[0] = setTimeSpan;
                        buf_x[0] = buf_x[2];
                        buf_y[0] = buf_y[2];
                        buf_z[0] = buf_z[2];
                        buf_gyro_x[0] = buf_gyro_x[2];
                        buf_gyro_y[0] = buf_gyro_y[2];
                        buf_gyro_z[0] = buf_gyro_z[2];

                        //3つの速度を使用するためcnt_sが3の時に計算が開始される
                        if (cnt_s == 5){

                            //ax^2+bx+cの二次関数のa,b,cを求める
                            a[0] = (((buf_speed_x[0] - buf_speed_x[1]) * (speedTimeSpan[0] - (speedTimeSpan[4]))) - ((buf_speed_x[0] - buf_speed_x[2]) * (speedTimeSpan[0] - (speedTimeSpan[2])))) / ((speedTimeSpan[0] - (speedTimeSpan[2])) * (speedTimeSpan[0] - (speedTimeSpan[4])) * ((speedTimeSpan[2]) - (speedTimeSpan[4])));
                            b[0] = (buf_speed_x[0] - buf_speed_x[1]) / (speedTimeSpan[0] - (speedTimeSpan[2])) - a[0] * (speedTimeSpan[0] + (speedTimeSpan[2]));
                            c[0] = buf_speed_x[0] - a[0] * speedTimeSpan[0] * speedTimeSpan[0] - b[0] * speedTimeSpan[0];
                            a[1] = (((buf_speed_y[0] - buf_speed_y[1]) * (speedTimeSpan[0] - (speedTimeSpan[4]))) - ((buf_speed_y[0] - buf_speed_y[2]) * (speedTimeSpan[0] - (speedTimeSpan[2])))) / ((speedTimeSpan[0] - (speedTimeSpan[2])) * (speedTimeSpan[0] - (speedTimeSpan[4])) * ((speedTimeSpan[2]) - (speedTimeSpan[4])));
                            b[1] = (buf_speed_y[0] - buf_speed_y[1]) / (speedTimeSpan[0] - (speedTimeSpan[2])) - a[1] * (speedTimeSpan[0] + (speedTimeSpan[2]));
                            c[1] = buf_speed_y[0] - a[1] * speedTimeSpan[0] * speedTimeSpan[0] - b[1] * speedTimeSpan[0];
                            a[2] = (((buf_speed_z[0] - buf_speed_z[1]) * (speedTimeSpan[0] - (speedTimeSpan[4]))) - ((buf_speed_z[0] - buf_speed_z[2]) * (speedTimeSpan[0] - (speedTimeSpan[2])))) / ((speedTimeSpan[0] - (speedTimeSpan[2])) * (speedTimeSpan[0] - (speedTimeSpan[4])) * ((speedTimeSpan[2]) - (speedTimeSpan[4])));
                            b[2] = (buf_speed_z[0] - buf_speed_z[1]) / (speedTimeSpan[0] - (speedTimeSpan[2])) - a[2] * (speedTimeSpan[0] + (speedTimeSpan[2]));
                            c[2] = buf_speed_z[0] - a[2] * speedTimeSpan[0] * speedTimeSpan[0] - b[2] * speedTimeSpan[0];

                            for (int i = 0; i < 3; ++i){
                                //XYZそれぞれの変位を順に算出する
                                saveFile(fileNameDebug, ",");
                                disp = ((speedTimeSpan[3]) * (func_2d(a[i], b[i], c[i], speedTimeSpan[0]) + (4 * func_2d(a[i], b[i], c[i], speedTimeSpan[2])) + func_2d(a[i], b[i], c[i], speedTimeSpan[4]))) * 1/6 + disp0[i];

                                disp_string = String.valueOf(disp);
                                saveFile(fileNameDebug , String.format(disp_string));


                                switch (i)
                                {
                                    case 0:
                                        //x軸の変位が算出された時
                                        break;
                                    case 1:
                                        //y軸の変位が算出された時
                                        textViewDisp.setText("Disp : " + numberFormat.format(disp));
                                        break;
                                    case 2:
                                        //z軸の変位が算出された時
                                        break;
                                }

                                //変位の更新
                                disp0[i] = disp;

                            }
                            Comp_disp = (disp0[0] * disp0[0]) + (disp0[1] * disp0[1]);
                            Comp_disp = (float) Math.sqrt(Comp_disp);

                            true_disp_x = (float) Math.cos(Current_Angle[2]) * Comp_disp;
                            true_disp_y = (float) Math.sin(Current_Angle[2]) * Comp_disp;

                            //画面上に現在地を表示
                            canvasAct.drawDispX = true_disp_y;
                            canvasAct.drawDispY = -1 * true_disp_x;


                            saveFile(fileNameDebug, ",");
                            disp_string = String.valueOf(true_disp_x);
                            saveFile(fileNameDebug , String.format(disp_string));
                            saveFile(fileNameDebug, ",");
                            disp_string = String.valueOf(true_disp_y);
                            saveFile(fileNameDebug , String.format(disp_string));

                            cnt_s = 1;
                            speedTimeSpan[0] = setTimeSpan;
                            buf_speed_x[0] = buf_speed_x[2];
                            buf_speed_y[0] = buf_speed_y[2];
                            buf_speed_z[0] = buf_speed_z[2];
                            saveFile(fileNameDebug, br);
                        }
                        else{
                            saveFile(fileNameDebug, br);
                        }

                    }
                    else{
                        saveFile(fileNameDebug, br);
                    }

                    handler.postDelayed(this, 10);

                }
            };
            handler.post(runnable);
        }
        if(v == 1) {
            handler.removeCallbacks(runnable);
            stoptimertask.setText("停止中");
        }
    }
    public static float func_2d(float a, float b, float c, float x) {
        float y;
        y = (a * (x * x)) + (b * x) + c;
        return y;
    }
}

