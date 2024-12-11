package com.example.cq_mobile.HelperManagers.mapFolder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

import com.example.cq_mobile.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class MarkerManager {

    private static final int DIAMETER = 200;
    private static final int PADDING = 25;

    public BitmapDescriptor getCustomCircleMarkerIcon(Context context) {
        Bitmap bitmap = Bitmap.createBitmap(DIAMETER, DIAMETER, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        float radius = DIAMETER / 2f;
        // Draw the filled circle
        canvas.drawCircle(radius, radius, radius, paint);

        // Load the drawable (replace with your image resource)
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.map_marker_todo);

        if (drawable != null) {
            // Convert the drawable to a bitmap
            Bitmap drawableBitmap = drawableToBitmap(drawable);

            // Scale the drawable bitmap to fit the circle with padding
            int scaledWidth = DIAMETER - 2 * PADDING;
            int scaledHeight = DIAMETER - 2 * PADDING;
            Bitmap scaledImage = Bitmap.createScaledBitmap(drawableBitmap, scaledWidth, scaledHeight, false);

            // Calculate the starting position to center the image within the circle
            float left = PADDING;
            float top = PADDING;

            // Draw the image onto the canvas with padding
            canvas.drawBitmap(scaledImage, left, top, null);

            // Draw the border around the circle
            Paint borderPaint = new Paint();
            borderPaint.setColor(Color.WHITE); // Border color
            borderPaint.setStrokeWidth(5); // Border thickness
            borderPaint.setStyle(Paint.Style.STROKE); // Only draw the border
            canvas.drawCircle(radius, radius, radius - 5, borderPaint);
        }

        // Create the BitmapDescriptor from the bitmap
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return bitmap;
    }
}
