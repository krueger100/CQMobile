package com.example.cq_mobile.HelperManagers.Animation;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class TransitionAnimationManager {

    // Fade In Animation
    public static void fadeIn(View view, int duration) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeIn.setDuration(duration);
        fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeIn.start();
    }

    // Fade Out Animation
    public static void fadeOut(View view, int duration) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        fadeOut.setDuration(duration);
        fadeOut.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeOut.start();
        fadeOut.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
    }

    // Slide In Animation (from the right)
    public static void slideInFromRight(View view, int duration) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator slideIn = ObjectAnimator.ofFloat(view, "translationX", view.getWidth(), 0);
        slideIn.setDuration(duration);
        slideIn.setInterpolator(new AccelerateDecelerateInterpolator());
        slideIn.start();
    }

    // Slide Out Animation (to the right)
    public static void slideOutToRight(View view, int duration) {
        ObjectAnimator slideOut = ObjectAnimator.ofFloat(view, "translationX", 0, view.getWidth());
        slideOut.setDuration(duration);
        slideOut.setInterpolator(new AccelerateDecelerateInterpolator());
        slideOut.start();
        slideOut.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
    }

    // Scale Animation (Zoom In & Out)
    public static void zoomIn(View view, int duration) {
        ObjectAnimator scaleUp = ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat("scaleX", 0.7f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 0.7f, 1f)
        );
        scaleUp.setDuration(duration);
        scaleUp.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleUp.start();
    }

    public static void zoomOut(View view, int duration) {
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat("scaleX", 1f, 0.7f),
                PropertyValuesHolder.ofFloat("scaleY", 1f, 0.7f)
        );
        scaleDown.setDuration(duration);
        scaleDown.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleDown.start();
    }


}
/*

TransitionAnimationManager.fadeIn(yourView, 300);
TransitionAnimationManager.fadeOut(yourView, 300);

TransitionAnimationManager.slideInFromRight(yourView, 300);
TransitionAnimationManager.slideOutToRight(yourView, 300);

TransitionAnimationManager.zoomIn(yourView, 300);
TransitionAnimationManager.zoomOut(yourView, 300);



 */