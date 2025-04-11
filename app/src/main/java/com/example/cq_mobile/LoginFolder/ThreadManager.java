package com.example.cq_mobile.LoginFolder;

import android.os.Handler;
import android.os.Looper;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {

    private static Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private static ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor(); // For background tasks
    private static ExecutorService customExecutor = Executors.newCachedThreadPool(); // For custom threads

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

    // Run a task on the background thread
    public static void runOnBackgroundThread(Runnable task) {
        backgroundExecutor.execute(task);
    }

    // Run a task on a custom thread (using a cached thread pool)
    public static void runOnCustomThread(Runnable task) {
        customExecutor.execute(task);
    }

    // Optional: Shutdown the executors if needed
    public static void shutdownExecutors() {
        backgroundExecutor.shutdown();
        customExecutor.shutdown();
    }
}


/*
/// Running on the main thread
ThreadManager.runOnMainThread(() -> {
    // Code to run on the main thread
    Log.d("MainThread", "This is running on the main thread.");
});

// Running on a background thread
ThreadManager.runOnBackgroundThread(() -> {
    // Code to run on a background thread (e.g., network request)
    Log.d("BackgroundThread", "This is running on a background thread.");
});

// Running on a custom thread
ThreadManager.runOnCustomThread(() -> {
    // Code to run on a custom thread
    Log.d("CustomThread", "This is running on a custom thread.");
});

 */