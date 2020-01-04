package com.titanarchers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;       //see app build.gradle

import java.util.ArrayList;
import java.util.List;

public class TargetView extends View {

    public static int BRUSH_SIZE = 6;                 //strokeWidth
    public static final int DEFAULT_COLOR = Color.GREEN;
    private static final float TOUCH_TOLERANCE = 4;
    private static final int TOUCH_OFFSET = 100;
    private static final float ARROW_POINT_RADIUS = 10;
    private static final float TARGET_BITMAP_SCALE_FACTOR = 2.625f;
    private Bitmap mBitmap;
    private int currentColor = DEFAULT_COLOR;
    private int mStrokeWidth;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private float mScaleFactor;
    private float mArrowScaledRadius;

    private float centerX;
    private float centerY;
    private float mX, mY;
    private float[] mRadii = new float[11];

    private ArrowPointViewModel mModel;
    private List<ArrowPoint> mArrowPointList = new ArrayList<>();

    private  OnCoordinateUpdate mCoordinatesListener;

    public void setCoordinatesListener(OnCoordinateUpdate listener) {
        mCoordinatesListener = listener;
    }

    public interface OnCoordinateUpdate {
        void onUpdate(int score, int color, float x, float y);
    }

    public TargetView(Context context) {
        this(context, null);
    }

    public TargetView(Context context, AttributeSet attrs) {
        super(context, attrs);

        FragmentActivity mActivity = (FragmentActivity) context;
        mModel = ViewModelProviders.of(mActivity).get(ArrowPointViewModel.class);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setColor(DEFAULT_COLOR);
        mBitmapPaint.setStrokeWidth(BRUSH_SIZE);

        mBitmapPaint.setStyle(Paint.Style.STROKE);
        mBitmapPaint.setStrokeJoin(Paint.Join.ROUND);
        mBitmapPaint.setStrokeCap(Paint.Cap.ROUND);
        mBitmapPaint.setXfermode(null);
        mBitmapPaint.setAlpha(0xff);
        mBitmapPaint.setTextSize(60);
    }

    public void init(DisplayMetrics metrics) {
        mScaleFactor = metrics.density / TARGET_BITMAP_SCALE_FACTOR;
        mArrowScaledRadius = ARROW_POINT_RADIUS * (mScaleFactor);
        mStrokeWidth = (int) (BRUSH_SIZE * mScaleFactor);

        Resources res = getResources();
        mBitmap = BitmapFactory.decodeResource(res, R.drawable.archery_target);
        Canvas mCanvas = new Canvas(mBitmap.copy(Bitmap.Config.ARGB_8888, true));

        centerX = mCanvas.getWidth() / 2;
        centerY = mCanvas.getHeight() / 2;

        currentColor = DEFAULT_COLOR;

        fillRadii();
    }

    public void clear() {
        mArrowPointList = mModel.getArrowPoints().getValue();
        if (mArrowPointList != null) {
            mArrowPointList.clear();
        }
        mX = 0;
        mY = 0;
        mStrokeWidth = (int) (BRUSH_SIZE * mScaleFactor);
        currentColor = DEFAULT_COLOR;

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        mArrowPointList = mModel.getArrowPoints().getValue();
        if (mArrowPointList != null) {
            for (ArrowPoint pts : mArrowPointList) {
                mBitmapPaint.setColor(pts.color);
                mBitmapPaint.setMaskFilter(null);

                canvas.drawCircle(pts.x, pts.y, mArrowScaledRadius, mBitmapPaint);
            }
        }

        mBitmapPaint.setColor(currentColor);
        canvas.drawCircle(mX , mY - TOUCH_OFFSET, mArrowScaledRadius, mBitmapPaint);

        if (mX != 0 && mY != 0) {
            mBitmapPaint.setStrokeWidth(2);
            canvas.drawLine(centerX, centerY, mX, mY - TOUCH_OFFSET, mBitmapPaint);


            int score;
            score = getScore(mX, mY - TOUCH_OFFSET);

            //Show score on canvas as it changes
            String scoreStr;
            if (score == 0) scoreStr = "M";
            else
            if (score == 11) scoreStr = "X";
            else scoreStr = String.valueOf(score);

            canvas.drawText(scoreStr,20, getHeight() - 50,mBitmapPaint);

            mBitmapPaint.setStrokeWidth(mStrokeWidth);
        }

        //get ArrowPoint Group color - sets currentColor in groups of three
        //do after drawCircle - last of group gets next color temporarily
        getArrowGroupColor();

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();

        //Only 30 arrows per round
        if (mModel.getListSize() >= 30) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);

                invalidate(); //the prev view - make action look smooth
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }

        return true;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mX = x;
            mY = y;
        }

    }

    private void touchUp() {
        int score;
        score = getScore(mX, mY - TOUCH_OFFSET);

        if (mCoordinatesListener != null) {
            mCoordinatesListener.onUpdate(score, currentColor, mX, mY - TOUCH_OFFSET);
        }

    }

    private void touchStart(float x, float y) {
        //save current position
        mX = x;
        mY = y;
    }

    private void fillRadii() {
        mRadii[0] = 25 * mScaleFactor;
        mRadii[1] = 50 * mScaleFactor;
        mRadii[2] = 100 * mScaleFactor;

        int cntr = 3;
        for (int i = 0; i < 8; i++) {
            float pos = (150 + (i * 50)) * mScaleFactor;     // each radius is 50 greater than the last
            mRadii[cntr] = pos;
            cntr++;
        }
    }

    //Calculate using first point on circumference of arrow that crosses
    private int getScore(float posX, float posY) {
        int result = 0;
        boolean found = false;

        //determine point on arrow that would first cross target radius
        double angle = Math.atan2(posY - centerY, posX - centerX);

        angle = angle + Math.PI;
        double arrowR = mArrowScaledRadius + 8 * mScaleFactor;

        double pX = posX + (arrowR * Math.cos(angle));
        double pY = posY + (arrowR * Math.sin(angle));
        float dist = ((float) pX - centerX)*((float)pX - centerX) +
                ((float)pY - centerY) * ((float)pY - centerY);


        for (int i = 0; i < mRadii.length; i++) {
            float radius = mRadii[i] * mRadii[i];

            if (dist < radius) {
                found = true;
                result++;
            }
        }

        if (!found) result = 0;
        return result;
    }

    private void getArrowGroupColor() {
        mArrowPointList = mModel.getArrowPoints().getValue();

        int arrowCount = (mArrowPointList == null) ? 0 : mArrowPointList.size();
        int numGroups = 0;
        int rmdr = arrowCount % 3;

        if (arrowCount == 0) {
            currentColor = Color.parseColor("#32C12C");    //Green
            return;
        }

        if (rmdr == 0) {
            numGroups = arrowCount / 3;
        } else {
            arrowCount -= rmdr;
            numGroups = arrowCount / 3;
        }

        switch (numGroups) {
            case 0:
                currentColor = Color.parseColor("#32C12C");    //Green
                break;
            case 1:
                currentColor = Color.parseColor("#009888");    //Teal
                break;
            case 2:
                currentColor = Color.parseColor("#7F4FC9");    //Purple
                break;
            case 3:
                currentColor = Color.parseColor("#9E9E9E");    //Grey
                break;
            case 4:
                currentColor = Color.parseColor("#CDE000");    //Lime
                break;
            case 5:
                currentColor = Color.parseColor("#FF9A00");    //Orange
                break;
            case 6:
                currentColor = Color.parseColor("#FF5500");    //Deep Orange
                break;
            case 7:
                currentColor = Color.parseColor("#7C5547");    //Brown
                break;
            case 8:
                currentColor = Color.parseColor("#50342C");    //Deep Brown
                break;
            default:
                currentColor = Color.parseColor("#32C12C");    //Green
                break;
        }
    }

}
