package com.smallgroup.drawapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DrawingView extends View {

    private ArrayList<AdvancedPath> paths;
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = 0xFF660000;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;

    private GestureDetector gestureDetector;

    private boolean erase=false;

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paths = new ArrayList<>();
        setupDrawing();
        setupGestureDetector();
    }

    private void setupGestureDetector() {
        GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                //TODO("отмена действия")
                removeLastPath();
                removeLastPath();
                Log.d("GESTURE", "Double tap");
                return super.onDoubleTap(e);
            }
        };
        gestureDetector = new GestureDetector(getContext(), listener);
    }

    private void setupDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        paths.forEach(path -> drawCanvas.drawPath(path.getPath(), path.getPaint()));
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        if(gestureDetector.onTouchEvent(event)) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                paths.add(new AdvancedPath(
                        new Path(drawPath), new Paint(drawPaint))
                );
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void setColor(String color){
        invalidate();
        paintColor = Color.parseColor(color);
        drawPaint.setColor(paintColor);
    }

    public void setStrokeWidth(float size){
        invalidate();
        drawPaint.setStrokeWidth(size);
    }

    public void setErase(boolean isErase){
        erase=isErase;
        if(erase)
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else
            drawPaint.setXfermode(null);
    }

    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void removeLastPath() {
        if (paths.size() > 0) {
            paths.remove(paths.size() - 1);
            drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            Log.d("REMOVE", "remove last path"+ paths.size());
            invalidate();
        }
    }
}
