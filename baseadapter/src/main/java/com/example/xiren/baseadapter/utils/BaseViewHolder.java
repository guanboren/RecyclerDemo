package com.example.xiren.baseadapter.utils;

import android.util.SparseArray;
import android.view.View;

public class BaseViewHolder {
    /**
     * 用法简单介绍 if (convertView == null) {<br>
     * convertView = LayoutInflater.from(context).inflate(R.layout.banana_phone,
     * parent, false);<br>
     * }<br>
     * ImageView bananaView = ViewHolder.get(convertView, R.id.banana);<br>
     * TextView phoneView = ViewHolder.get(convertView, R.id.phone);<br>
     *
     * @param view
     * @param id
     * @return
     */
    public static <T extends View> T get(View view, int id)
    {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }

}
