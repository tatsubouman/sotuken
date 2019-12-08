package com.sample.rotationsampleactivity;

import java.util.List;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Path;
import android.graphics.PointF;



public class CanvasAct extends View {
    private Paint paint;
    private Path path;
    protected float drawDispX, drawDispY;
    protected float x,y;
    private float meterPerCoordinate = 93.3673f;
    protected volatile boolean buttonFlag = false;
    private List<PointF>  pointList;

    /*public void initialize(){
        pointList = new ArrayList<PointF>();
    }*/




    public CanvasAct(Context context, AttributeSet attrs) {
        super(context, attrs);

        //this.path = new Path();

        //initialize();

        this.paint = new Paint();
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setAntiAlias(true);
        this.paint.setStrokeWidth(10);
        pointList = new ArrayList();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawPath(path, paint);
        paint.setStrokeWidth(10);
        paint.setColor(Color.argb(255, 0, 255, 0));
        // (x1,y1,x2,y2,paint) 始点の座標(x1,y1), 終点の座標(x2,y2)
        for (PointF point : pointList){
            canvas.drawPoint(point.x, point.y, paint);
        }
        canvas.drawPoint(x + drawDispX * meterPerCoordinate, y + drawDispY * meterPerCoordinate, paint);
        pointList.add(new PointF(x + drawDispX * meterPerCoordinate, y + drawDispY * meterPerCoordinate));
        //path.moveTo(x, y);
        //path.lineTo(x + drawDispX * meterPerCoordinate, y + drawDispY * meterPerCoordinate);
        //canvas.drawPath(path, paint);
        paint.setStrokeWidth(10);
        paint.setColor(Color.argb(255, 255, 0, 0));
        canvas.drawPoint(x, y, paint);
        if(buttonFlag) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            drawDispX = 0;
            drawDispY = 0;
            this.paint.reset();
            pointList = new ArrayList();
            invalidate();
            buttonFlag = false;
        }
    }


    public boolean onTouchEvent(MotionEvent event) {

        //画面（View）が押されたのかを判定
        if(event.getAction() == MotionEvent.ACTION_DOWN) {

            x = event.getX();    //X座標を変数にセット
            y = event.getY();    //Y座標を変数にセット
            buttonFlag = true;

        }



        //画面の更新（onDrawの呼び出し）
        invalidate();

        return true;
    }

    public void delete() {
        this.paint.reset();
        pointList = new ArrayList();
        invalidate();

    }
}