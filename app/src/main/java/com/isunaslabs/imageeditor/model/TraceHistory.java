package com.isunaslabs.imageeditor.model;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.isunaslabs.imageeditor.Utils;

import java.util.List;

public class TraceHistory {
    private List<PointF> points;
    private Paint tracePaint;
    private Path tracePath;
    private PointF estimatedCenter;
    private int pollutionCode;
    private Paint labelTextPaint;
    private PointF labelPosition;

    public TraceHistory(List<PointF> points, Paint tracePaint, int pollutionCode) {
        this.points = points;
        this.pollutionCode = pollutionCode;
        generateTracePaint(tracePaint);
        generateTracePath();
        generateLabelTextPaint();
    }

    private void generateTracePaint(Paint tracePaint) {
        this.tracePaint = new Paint();
        this.tracePaint.setAntiAlias(true);
        this.tracePaint.setStrokeJoin(tracePaint.getStrokeJoin());
        this.tracePaint.setStrokeCap(tracePaint.getStrokeCap());
        this.tracePaint.setColor(tracePaint.getColor());
        this.tracePaint.setStyle(tracePaint.getStyle());
        this.tracePaint.setStrokeWidth(tracePaint.getStrokeWidth());
    }

    private void generateLabelTextPaint() {
        labelTextPaint = new Paint();
        labelTextPaint.setAntiAlias(true);
        labelTextPaint.setStrokeWidth(8);
        labelTextPaint.setStyle(Paint.Style.FILL);
        labelTextPaint.setColor(Color.WHITE);
        labelTextPaint.setTextSize(Utils.getLabelTextSize());

        Paint.FontMetrics metrics = new Paint.FontMetrics();
        labelTextPaint.getFontMetrics(metrics);

    }

    private void generateTracePath() {
        tracePath = new Path();
        boolean isFirstTime = true;
        for (PointF singlePoint : points) {
            if (isFirstTime) {
                tracePath.moveTo(singlePoint.x, singlePoint.y);
                tracePath.lineTo(singlePoint.x, singlePoint.y);
                isFirstTime = false;
            } else {
                tracePath.lineTo(singlePoint.x, singlePoint.y);
            }
        }
        calculatePathCenter();
    }

    private void calculatePathCenter() {
        RectF bounds = new RectF();
        tracePath.computeBounds(bounds,false);
        estimatedCenter = new PointF((bounds.left + bounds.right)/2,
                (bounds.top + bounds.bottom)/2);
        labelPosition = new PointF();

        //evaluate where to direction of the label
        /*Utils.imageWidth is equal to the layout width*/
        if(bounds.right > Utils.getImageWidth() / 2f) {
            labelPosition.x = bounds.left - 20;
        }else {
            labelPosition.x = bounds.right + 20;
        }

        labelPosition.y = bounds.bottom;
    }

    public PointF getEstimatedCenter() {
        return estimatedCenter;
    }

    public Paint getTracePaint() {
        return tracePaint;
    }

    public Paint getLabelTextPaint() {
        return labelTextPaint;
    }

    public Path getTracePath() {
        return tracePath;
    }

    public PointF getLabelPosition() {
        return labelPosition;
    }

    public int getPollutionCode() {
        return pollutionCode;
    }

}
