package com.example.cq_mobile.HelperManagers.Animation;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class ClickAnimationManager {
    public static void applyClickAnimation(View view) {
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat("scaleX", 0.9f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 0.9f, 1f)
        );
        scaleDown.setDuration(150);
        scaleDown.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleDown.start();
    }
}


/*
        ClickAnimationManager.applyClickAnimation(v); // Apply animation

 */