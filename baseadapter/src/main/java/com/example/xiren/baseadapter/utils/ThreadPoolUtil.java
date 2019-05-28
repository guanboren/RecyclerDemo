package com.example.xiren.baseadapter.utils;

import android.os.Handler;
import android.os.Looper;

public class ThreadPoolUtil {
    /**
     * 检测并在主线程中执行 runnable
     *
     * @param runnable
     */
    public static void runOnMainThread(Runnable runnable)
    {
        if (ThreadPoolUtil.isMainThread()) {
            runnable.run();
        } else {
            getMainHandle().post(runnable);
        }
    }

    /**
     * 获取当前app的主handler
     *
     * @return
     */
    public static Handler getMainHandle()
    {
        return new Handler(Looper.getMainLooper());
    }

    /**
     * 检测当前是否在主线程
     *
     * @return
     */
    public static boolean isMainThread()
    {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
