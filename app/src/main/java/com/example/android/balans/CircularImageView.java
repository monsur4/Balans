package com.example.android.balans;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.io.IOException;

/**
 * Created by OKUNIYI MONSURU on 1/24/2019.
 */

public class CircularImageView extends android.support.v7.widget.AppCompatImageView {
    float radius; // is set within onMeasure

    float mStrokeWidth = 2f;
    int strokeColor = Color.RED;
    private Paint mPaintStroke;
    private Rect mRect;
    private Paint mPaintImage;
    private Bitmap mBitmap;
    private Shader mShader;

    private Uri mImageUri = null;
    private int mViewWidth;
    private int mViewHeight;

    public CircularImageView(Context context) {
        super(context);
        init();
    }

    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        mPaintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setStrokeWidth(mStrokeWidth);
        mPaintStroke.setColor(strokeColor);

        //mRect = new Rect(0, 0, getWidth(), getHeight());
        mPaintImage = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int desiredWidth = 200;
        int desiredHeight = 200;

        mViewWidth = resolveSizeAndState(desiredWidth, widthMeasureSpec, 0);
        mViewHeight = resolveSizeAndState(desiredHeight, heightMeasureSpec, 0);

        radius = (mViewWidth /2f) - mStrokeWidth;
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        realignImage(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mShader != null){
            mPaintImage.setShader(mShader);
            canvas.drawCircle(mViewWidth/2f, mViewHeight/2f, radius, mPaintImage);
        }
        canvas.drawCircle(getWidth()/2f, getHeight()/2f, radius, mPaintStroke);
    }

    /*@Override
    public void setImageURI(@Nullable Uri uri) {
        mImageUri = uri;
        if (mImageUri != null){
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    @Override
    public void setImageBitmap(Bitmap bm) {
        mBitmap = bm;
        mShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        realignImage(mBitmap);
    }

    private void realignImage(Bitmap mBitmap) {
        float bitmapHeight = mBitmap.getHeight();
        float bitmapWidth = mBitmap.getWidth();
        float scale = 1;

        if (bitmapWidth < bitmapHeight){
            scale = mViewWidth/bitmapWidth;
        }
        else{
            scale = mViewHeight/bitmapHeight;
        }
        float newWidth = scale * bitmapWidth;
        float newHeight = scale * bitmapHeight;

        //calcute the centre point for the image
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate((mViewWidth - newWidth)/2, (mViewHeight - newHeight)/2f);

        mShader.setLocalMatrix(matrix);
    }
}
