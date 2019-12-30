package com.isunaslabs.imageeditor.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import com.isunaslabs.imageeditor.Utils;
import com.isunaslabs.imageeditor.model.ImageToTrace;
import com.isunaslabs.imageeditor.model.TraceHistory;

import java.util.ArrayList;
import java.util.List;

public class DrawingPad extends SurfaceView {

    private static final String TAG = "DrawingPad";
    private static int STROKE_WIDTH = 15;
    private static int STROKE_COLOR = Color.argb(255, 75, 200, 255);


    private ImageToTrace imageToTrace;
    private int layoutWidth = 0;
    private int layoutHeight = 0;
    private Path tracingPath;
    private Paint tracePaint;
    private Paint maskPaint;
    private int maskHeight;
    private int imageHeight;
    private List<PointF> recentDrawnPoints;
    private List<TraceHistory> traceHistoryList = new ArrayList<>();
    private int imageXPosition = 0;
    private int imageYPosition = 0;
    private int pollutionCode = 1;
    private String imageUrl = "";
    private boolean userIsDrawing = false;
    private Paint labelTextBackgroundPaint;


    public DrawingPad(Context context) {
        super(context);
        initView();
    }

    public DrawingPad(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DrawingPad(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setPollutionCode(int pollutionCode) {
        this.pollutionCode = pollutionCode;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DrawingPad(Context context, AttributeSet attrs,
                      int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        tracePaint = new Paint();
        tracePaint.setAntiAlias(true);
        tracePaint.setStrokeJoin(Paint.Join.ROUND);
        tracePaint.setStrokeCap(Paint.Cap.ROUND);
        tracePaint.setColor(STROKE_COLOR);
        tracePaint.setStyle(Paint.Style.STROKE);
        tracePaint.setStrokeWidth(STROKE_WIDTH);

        maskPaint = new Paint();
        maskPaint.setAntiAlias(true);
        maskPaint.setColor(Color.BLACK);

        labelTextBackgroundPaint = new Paint();
        labelTextBackgroundPaint.setAntiAlias(true);
        labelTextBackgroundPaint.setColor(Color.BLACK);
        labelTextBackgroundPaint.setStrokeJoin(Paint.Join.ROUND);
        labelTextBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        layoutWidth = getWidth();
        layoutHeight = getHeight();

        /*initialize the image loading processes in onLayout since we'll later need
        to get the layout height for vertically centering the image*/
        Utils.loadBitmapFromUrl(
                getContext().getApplicationContext(),
                this.imageUrl, layoutWidth, new Utils.BitmapLoadListener() {
                    @Override
                    public void onBitmapLoaded(ImageToTrace image) {
                        imageToTrace = image;
                        /*get a Y point that would center the image
                        vertically on the screen*/
                        if (imageToTrace.getImageBitmap().getHeight() < layoutHeight) {
                            imageHeight = imageToTrace.getImageBitmap().getHeight();
                            maskHeight = (layoutHeight - imageHeight) / 2;
                            imageYPosition = maskHeight;
                        }
                        postInvalidate();
                    }
                });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(255, 255, 255, 255);

        if (imageToTrace != null) {
            canvas.drawBitmap(imageToTrace.getImageBitmap(),
                    imageXPosition, imageYPosition, null);
        }

        //trace the old touch points that were recorded
        for (TraceHistory traceHistory : traceHistoryList) {
            canvas.drawPath(traceHistory.getTracePath(), traceHistory.getTracePaint());
            /*canvas.drawCircle(traceHistory.getEstimatedCenter().x,
                    traceHistory.getEstimatedCenter().y, 15,
                    traceHistory.getLabelTextPaint());*/
            /*canvas.drawLine(traceHistory.getEstimatedCenter().x,
                    traceHistory.getEstimatedCenter().y,
                    traceHistory.getLabelPosition().x,
                    traceHistory.getLabelPosition().y,
                    traceHistory.getLabelTextPaint());*/

            String text = "污染 " + traceHistory.getPollutionCode();
            int padding = 20;
            canvas.drawRect(traceHistory.getEstimatedCenter().x - padding,
                    traceHistory.getEstimatedCenter().y + traceHistory.getLabelTextPaint().getFontMetrics().top - padding,
                    traceHistory.getEstimatedCenter().x + traceHistory.getLabelTextPaint().measureText(text) + padding,
                    traceHistory.getEstimatedCenter().y + traceHistory.getLabelTextPaint().getFontMetrics().bottom + padding,
                    labelTextBackgroundPaint);

            canvas.drawText(text,
                    traceHistory.getEstimatedCenter().x,
                    traceHistory.getEstimatedCenter().y,
                    traceHistory.getLabelTextPaint());
        }

        //trace on the image in real time as the user moves his finger
        if (tracingPath != null && userIsDrawing) {
            canvas.drawPath(tracingPath, tracePaint);
        }

        /*draw top and bottom masks*/
        canvas.drawRect(0, 0, layoutWidth, maskHeight, maskPaint);
        canvas.drawRect(0, imageYPosition + imageHeight, layoutWidth,
                imageYPosition + imageHeight + maskHeight, maskPaint);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float xDown = event.getX();
                float yDown = event.getY();
                tracingPath = new Path();
                tracingPath.moveTo(xDown, yDown);
                tracingPath.lineTo(xDown, yDown);
                recentDrawnPoints = new ArrayList<>();
                recentDrawnPoints.add(new PointF(xDown, yDown));
                postInvalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                float xMove = event.getX();
                float yMove = event.getY();
                tracingPath.lineTo(xMove, yMove);
                recentDrawnPoints.add(new PointF(xMove, yMove));
                userIsDrawing = true;
                postInvalidate();
                return true;
            case MotionEvent.ACTION_UP:
                float xUp = event.getX();
                float yUp = event.getY();
                tracingPath.lineTo(xUp, yUp);
                recentDrawnPoints.add(new PointF(xUp, yUp));
                traceHistoryList.add(new TraceHistory(recentDrawnPoints,
                        tracePaint, pollutionCode));
                userIsDrawing = false;
                postInvalidate();
                performClick();
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
