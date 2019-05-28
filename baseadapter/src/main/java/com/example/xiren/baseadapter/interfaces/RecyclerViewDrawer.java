package com.example.xiren.baseadapter.interfaces;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xiren.baseadapter.BaseRecyclerViewHolder;

/**
 * Recycler绘制者
 *
 * @author Guanboren
 * @date 2019.05.27
 */

public interface RecyclerViewDrawer<T> {
    /**
     * 在adapter的getView中，首次创建view，初始化并返回创建的布局
     *
     * @param viewType
     * @param inflater
     * @param parent
     * @return
     */
    View onViewCreate(int viewType, @NonNull LayoutInflater inflater, ViewGroup parent);

    /**
     * 在adapter的getView中，每次滑动listView会循环执行本方法
     *
     * @param position
     * @param item
     * @param viewHolder
     */
    void onViewAttach(int position, @NonNull T item, @NonNull BaseRecyclerViewHolder viewHolder);
}
