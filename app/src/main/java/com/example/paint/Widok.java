package com.example.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.util.DisplayMetrics;


import java.util.ArrayList;

public class Widok extends View {

    public static int WIELKOSC_PEDZLA = 15;
    public static final int DOMYSLNY_KOLOR = Color.BLUE;
    public static final int DOMYSLNY_KOLOR_TLA = Color.WHITE;
    private static final float TOLERANCJA_DOTYKU = 9;
    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<DotykPalca> paths = new ArrayList<>();
    private int obecnyKolor;
    private int OBECNY_KOLOR_TLA = DOMYSLNY_KOLOR_TLA;
    private int szerokosc;
    private boolean emboss;
    private boolean rozmycie;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    public Widok(Context context) {
        this(context, null);
    }

    public Widok(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DOMYSLNY_KOLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

        mEmboss = new EmbossMaskFilter(new float[] {1, 1, 1}, 0.4f, 6, 3.5f);
        mBlur = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);
    }

    public void init(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        obecnyKolor = DOMYSLNY_KOLOR;
        szerokosc = WIELKOSC_PEDZLA;
    }

    public void normal() {
        emboss = false;
        rozmycie = false;
    }

    public void emboss() {
        emboss = true;
        rozmycie = false;
    }

    public void rozmycie() {
        emboss = false;
        rozmycie = true;
    }

    public void clear() {
        OBECNY_KOLOR_TLA = DOMYSLNY_KOLOR_TLA;
        paths.clear();
        normal();
        invalidate();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(OBECNY_KOLOR_TLA);

        for (DotykPalca fp : paths) {
            mPaint.setColor(fp.kolor);
            mPaint.setStrokeWidth(fp.szerokosc);
            mPaint.setMaskFilter(null);

            if (fp.emboss)
                mPaint.setMaskFilter(mEmboss);
            else if (fp.rozmycie)
                mPaint.setMaskFilter(mBlur);

            mCanvas.drawPath(fp.path, mPaint);

        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    private void touchStart(float x, float y) {
        mPath = new Path();
        DotykPalca fp = new DotykPalca(obecnyKolor, emboss, rozmycie, szerokosc, mPath);
        paths.add(fp);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOLERANCJA_DOTYKU || dy >= TOLERANCJA_DOTYKU) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE :
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP :
                touchUp();
                invalidate();
                break;
        }

        return true;
    }
}
