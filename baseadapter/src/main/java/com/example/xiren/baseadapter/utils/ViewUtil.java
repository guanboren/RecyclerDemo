package com.example.xiren.baseadapter.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

public class ViewUtil {
    /**
     * 批量设置View的显示和隐藏
     *
     * @param visibility Modifier： Modified Date： Modify：
     */
    public static void setViewVisibility(int visibility, View... views)
    {
        setViewVisibility(visibility, null, views);
    }

    /**
     * 批量设置View的显示和隐藏
     *
     * @param visibility
     * @param animation  使用动画
     * @param views
     */
    public static void setViewVisibility(final int visibility, final Animation animation, View... views)
    {
        if (views == null || views.length == 0)
            return;
        for (final View v : views) {
            if (null == v || v.getVisibility() == visibility) {
                continue;
            }

            Runnable runnable = new Runnable() {
                @Override
                public void run()
                {
                    if (animation == null) {
                        v.setVisibility(visibility);
                    } else {
                        v.setVisibility(visibility);
                        v.startAnimation(animation);

                    }
                }
            };

            ThreadPoolUtil.runOnMainThread(runnable);
        }
    }

    public static int dip2px(Context context, float dipValue)
    {
        if (context == null)
            return 0;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
