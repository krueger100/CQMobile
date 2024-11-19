package com.example.cq_mobile.ui.home.ClockFolder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

/*
public class ClockView extends View {

    private Paint paint;
    private int centerX, centerY;
    private int radius;

    private int hour, minute, second;

    private Handler handler;
    private Runnable runnable;

    public ClockView(Context context) {
        super(context);
        init();
    }

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // Update the current time
                Calendar calendar = Calendar.getInstance();
                hour = calendar.get(Calendar.HOUR);
                minute = calendar.get(Calendar.MINUTE);
                second = calendar.get(Calendar.SECOND);

                // Redraw the view to update the seconds hand
                invalidate();

                // Post a new runnable every second (1000ms)
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable); // Start the runnable
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        radius = Math.min(w, h) / 2 - 20; // 20px margin
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw clock circle
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        canvas.drawCircle(centerX, centerY, radius, paint);

        // Draw clock ticks (hour marks)
        paint.setStrokeWidth(4);
        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(i * 30); // 30 degrees for each hour
            int startX = (int) (centerX + Math.cos(angle) * (radius - 20));
            int startY = (int) (centerY + Math.sin(angle) * (radius - 20));
            int endX = (int) (centerX + Math.cos(angle) * radius);
            int endY = (int) (centerY + Math.sin(angle) * radius);
            canvas.drawLine(startX, startY, endX, endY, paint);
        }

        // Draw the clock hands
        drawHand(canvas, (hour % 12) * 30 + (minute / 2), radius - 100, 10);  // Hour hand
        drawHand(canvas, minute * 6, radius - 40, 6);  // Minute hand
        drawHand(canvas, second * 6, radius - 20, 4);  // Second hand
    }

    private void drawHand(Canvas canvas, float angle, int length, int strokeWidth) {
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.BLACK);
        canvas.save();
        canvas.rotate(angle, centerX, centerY);
        canvas.drawLine(centerX, centerY, centerX, centerY - length, paint);
        canvas.restore();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(runnable); // Stop the handler when the view is detached
    }
}


 */
public class ClockView extends View {

    private Paint paint;
    private int centerX, centerY;
    private int radius;

    private int hour, minute, second;

    private Handler handler;
    private Runnable runnable;

    public ClockView(Context context) {
        super(context);
        init();
    }

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);  // Default color for hands and ticks

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // Update the current time
                Calendar calendar = Calendar.getInstance();
                hour = calendar.get(Calendar.HOUR);
                minute = calendar.get(Calendar.MINUTE);
                second = calendar.get(Calendar.SECOND);

                // Redraw the view to update the seconds hand
                invalidate();

                // Post a new runnable every second (1000ms)
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable); // Start the runnable
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        radius = Math.min(w, h) / 2 - 20; // 20px margin
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Set the background color to white
        canvas.drawColor(Color.WHITE);

        // Draw clock circle
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        canvas.drawCircle(centerX, centerY, radius, paint);

        // Draw clock ticks (hour marks)
        paint.setStrokeWidth(4);
        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(i * 30); // 30 degrees for each hour
            int startX = (int) (centerX + Math.cos(angle) * (radius - 20));
            int startY = (int) (centerY + Math.sin(angle) * (radius - 20));
            int endX = (int) (centerX + Math.cos(angle) * radius);
            int endY = (int) (centerY + Math.sin(angle) * radius);
            canvas.drawLine(startX, startY, endX, endY, paint);
        }

        // Draw the clock hands
        drawHand(canvas, (hour % 12) * 30 + (minute / 2), radius - 100, 10);  // Hour hand
        drawHand(canvas, minute * 6, radius - 40, 6);  // Minute hand
        drawHand(canvas, second * 6, radius - 20, 4);  // Second hand
    }

    private void drawHand(Canvas canvas, float angle, int length, int strokeWidth) {
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.BLACK);
        canvas.save();
        canvas.rotate(angle, centerX, centerY);
        canvas.drawLine(centerX, centerY, centerX, centerY - length, paint);
        canvas.restore();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(runnable); // Stop the handler when the view is detached
    }
}
