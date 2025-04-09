package com.example.cq_mobile.LoginFolder;

import android.os.Handler;
import android.os.Looper;

public class MainThreadManager {

    private static Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    // Post a Runnable to run on the main thread
    public static void runOnMainThread(Runnable runnable) {
        if (isMainThread()) {
            // If already on the main thread, execute directly
            runnable.run();
        } else {
            // Otherwise, post to the main thread handler
            mainThreadHandler.post(runnable);
        }
    }

    // Check if the current thread is the main thread
    private static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}

/*
// Example of using MainThreadManager in an AsyncTask or background thread
new Thread(new Runnable() {
    @Override
    public void run() {
        // Simulate some background work, e.g., network request
        try {
            Thread.sleep(2000); // Simulate a delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Post the result back to the main thread to update UI
        MainThreadManager.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                // Update UI components here
                Toast.makeText(getContext(), "Background task completed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}).start();

 */