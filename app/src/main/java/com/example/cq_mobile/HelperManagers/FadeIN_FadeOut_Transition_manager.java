package com.example.cq_mobile.HelperManagers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class FadeIN_FadeOut_Transition_manager {

    /**
     * Apply fade-in animation to a view
     * @param view The view to apply fade-in to
     * @param duration Duration of the fade-in animation in milliseconds
     */
    public static void fadeIn(final View view, long duration) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0f); // Start with transparent view
        view.animate()
                .alpha(1f) // Fade in to fully opaque
                .setDuration(duration)
                .setListener(null);
    }

    /**
     * Apply fade-out animation to a view
     * @param view The view to apply fade-out to
     * @param duration Duration of the fade-out animation in milliseconds
     */
    public static void fadeOut(final View view, long duration) {
        view.animate()
                .alpha(0f) // Fade out to transparent
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.INVISIBLE); // Hide the view after fading out
                    }
                });
    }
}



/*
// Fade-in example
FadeIN_FadeOut_Transition_manager.fadeIn(myView, 1000);  // 1000 ms = 1 second

// Fade-out example
FadeIN_FadeOut_Transition_manager.fadeOut(myView, 1000);  // 1000 ms = 1 second
 */