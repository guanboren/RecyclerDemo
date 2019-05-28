package com.example.xiren.baseadapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.xiren.baseadapter.utils.BaseViewHolder;


public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {
    private View convertView;

    public BaseRecyclerViewHolder(View itemView)
    {
        super(itemView);
        this.convertView = itemView;
    }

    public View getConvertView()
    {
        return convertView;
    }

    public <T extends View> T findViewById(int id)
    {
        return BaseViewHolder.get(convertView, id);
    }
}
