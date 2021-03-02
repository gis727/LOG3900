package com.example.client_leger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.client_leger.LiveGame.CanvasPathsHandler;
import com.example.client_leger.LiveGame.DrawingUpdate;
import com.example.client_leger.LiveGame.GameFragment;

public class CanvasView extends View {
    private Path drawPath, circlePath, mPath;
    public Paint drawPaint, canvasPaint, circlePaint, mBitmapPaint;
    public Canvas drawCanvas;
    public Bitmap canvasBitmap;
    private float brushSize;
    private boolean erase = false;
    public int currentWidth = 15;
    public int currentColor = Color.BLACK;
    private final CanvasPathsHandler pathsHandler = new CanvasPathsHandler();
    private boolean isTouchable = true;
    public float eraserRadius = 20;
    public boolean strokeEraserSelected = false;

    public CanvasView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing(){
        brushSize = 15;
        drawPath = new Path();
        mPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(currentColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.parseColor("#f4802b"));
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(1f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (canvasBitmap != null) canvasBitmap = Bitmap.createBitmap(canvasBitmap);
        else {
            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            canvasBitmap.eraseColor(Color.TRANSPARENT);
        }
        drawCanvas = new Canvas(canvasBitmap);
        DrawingUpdate.canvasWidth = drawCanvas.getWidth();
        DrawingUpdate.canvasHeight = drawCanvas.getHeight();
        Log.d("width", "onSizeChanged: " + String.valueOf(drawCanvas.getWidth()));
        Log.d("height", "onSizeChanged: " + String.valueOf(drawCanvas.getHeight()));
        if (GameFragment.GAME != null) {
            GameService.drawGame(this, GameFragment.GAME);
            GameFragment.GAME = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(erase) {
            canvas.drawBitmap(canvasBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(circlePath, circlePaint);
        } else {
            canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
            canvas.drawPath(drawPath, drawPaint);
            }
        }
    float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    public void setTouchable(boolean touchable)
    {
        isTouchable = touchable;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (!isTouchable) return false;

        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(erase) {
                    mPath.reset();
                    mPath.moveTo(touchX, touchY);
                    mX = touchX;
                    mY = touchY;
                } else {
                    drawPath.moveTo(touchX, touchY);
                }
                pathsHandler.OnFingerTouch(erase, new PointF(touchX, touchY), this, drawPaint, getContext(), currentColor);
                break;
            case MotionEvent.ACTION_MOVE:
                if(erase) {
                    float dx = Math.abs(touchX - mX);
                    float dy = Math.abs(touchY - mY);
                    if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                        mPath.quadTo(mX, mY, (touchX + mX)/2, (touchY + mY)/2);
                        mX = touchX;
                        mY = touchY;
                        circlePath.reset();
                        circlePath.addCircle(mX, mY, eraserRadius, Path.Direction.CW);
                    }
                } else {
                    drawPath.lineTo(touchX, touchY);
                }
                pathsHandler.OnFingerMove(erase, new PointF(touchX, touchY), this, drawPaint, getContext(), currentColor);
                break;
            case MotionEvent.ACTION_UP:
                if (!erase) {
                    drawPath.lineTo(touchX, touchY);
                    drawCanvas.drawPath(drawPath, drawPaint);
                }
                circlePath.reset();
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void setBrushSize(float newSize){
        brushSize=newSize;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize){
    }

    public void setErase(boolean isErase, final boolean strokeEraser){
        erase=isErase;
        eraserRadius = 20;
        strokeEraserSelected = erase && strokeEraser;
        if(erase) {
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setColor(currentColor);
            drawPaint.setXfermode(null);
        }
    }

    public void increaseBrushWidth() {
        if (currentWidth <= 68)
            currentWidth = (currentWidth + 1) * 2;
        setBrushSize(currentWidth);

    }

    public void decreaseBrushWidth() {
        if (currentWidth >= 15)
            currentWidth = (currentWidth / 2) - 1;
        setBrushSize(currentWidth);
    }

    public void changeColor(int color) {
        currentColor = color;
        drawPaint.setColor(currentColor);
    }

    public void clear() {
        canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        canvasBitmap.eraseColor(Color.TRANSPARENT);
        drawCanvas = new Canvas(canvasBitmap);
        drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
        circlePath.reset();
        drawPath.reset();
        invalidate();
    }
    public void resetGatherer() {
        pathsHandler.reset();
    }
}
