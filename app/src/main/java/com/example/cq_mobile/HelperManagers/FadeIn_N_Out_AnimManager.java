package com.example.cq_mobile.HelperManagers;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class FadeIn_N_Out_AnimManager {

    /**
     * Applies a looped fade-out on one ImageView and fade-in on another ImageView.
     *
     * @param imageView1      The first ImageView to fade out.
     * @param imageView2      The second ImageView to fade in.
     * @param fadeDuration    Duration for both fade-in and fade-out animations in milliseconds.
     * @param delayBetween    Delay between the fade-out and fade-in in milliseconds.
     */
    public static void applyLoopingFadeInOutAnimation(
            final View imageView1, final View imageView2,
            int fadeDuration, int delayBetween) {

        if (imageView1 == null || imageView2 == null) return;

        // Create fade-out animation for the first ImageView
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(fadeDuration);
        fadeOut.setFillAfter(true);

        // Create fade-in animation for the second ImageView
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(fadeDuration);
        fadeIn.setFillAfter(true);

        // Set an animation listener to control the transition between the images
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Hide the first ImageView at the start of fade-out
                imageView1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // No action needed
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Hide the first ImageView after fade-out completes
                imageView1.setVisibility(View.INVISIBLE);

                // Make the second ImageView visible and start fade-in
                imageView2.setVisibility(View.VISIBLE);
                imageView2.startAnimation(fadeIn);
            }
        });

        // Start the fade-out animation on the first ImageView
        imageView1.startAnimation(fadeOut);

        // Start the loop
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Once fade-in is complete, restart fade-out for the next loop
            fadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // No action needed
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // No action needed
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Once fade-in completes, fade out the second ImageView
                    imageView2.startAnimation(fadeOut);
                }
            });

        }, fadeDuration + delayBetween);  // Delay before fading out the second ImageView
    }
}