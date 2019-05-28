package com.example.xiren.baseadapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xiren.baseadapter.interfaces.AdapterDataDelegate;
import com.example.xiren.baseadapter.interfaces.RecyclerViewDrawer;
import com.example.xiren.baseadapter.utils.GridSpaceItemDecoration;
import com.example.xiren.baseadapter.utils.WrapperUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 带头部，尾部，空布局的RecyclerAdapter
 *
 * @author Guanboren
 * @date 2019.05.28
 */
public abstract class BaseRecyclerFrameAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder> implements RecyclerViewDrawer<T>, AdapterDataDelegate<T> {
    /**
     * 空布局
     */
    private View emptyView;

    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFootViews = new SparseArrayCompat<>();

    /**
     * 标识是否给adapter设置过数据
     */
    private boolean afterSetData = false;
    private float verticalSpacing = 0;
    private float horizontalSpacing = 0;
    private int dividerColor = android.R.color.transparent;

    public interface OnRecyclerItemClickListener<T> {
        /**
         * recyclerAdapter的item点击事件
         *
         * @param parent   父view
         * @param data     加载的数据
         * @param position 位置
         */
        void onItemClick(View parent, List<T> data, int position);
    }

    private Context context;
    protected LayoutInflater inflater;
    public List<T> list;
    private BaseRecyclerFrameAdapter.OnRecyclerItemClickListener<T> itemClickListener;

    public BaseRecyclerFrameAdapter(Context context)
    {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    protected boolean isEmpty()
    {
        return emptyView != null && getData().size() == 0 && afterSetData;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        if (isEmpty()) {
            if (null == emptyView) {
                emptyView = new TextView(getContext());
                ((TextView) emptyView).setGravity(Gravity.CENTER);
                ((TextView) emptyView).setText("数据为空");
            }
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            emptyView.setLayoutParams(lp);
            return new BaseRecyclerViewHolder(emptyView);
        } else if (mHeaderViews.get(viewType) != null) {
            return new BaseRecyclerViewHolder(mHeaderViews.get(viewType));
        } else if (mFootViews.get(viewType) != null) {
            return new BaseRecyclerViewHolder(mFootViews.get(viewType));
        } else {
            View view = onViewCreate(viewType, inflater, viewGroup);
            return new BaseRecyclerViewHolder(view);
        }

    }


    @Override
    public int getItemViewType(int position)
    {
        if (isEmpty()) {
            return BaseAdapterViewType.VIEW_TYPE_CONTENT_EMPTY;
        } else if (isHeaderViewPos(position)) {
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position)) {
            return mFootViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }
        return super.getItemViewType(position - getHeadersCount());
    }

    @Override
    public void onBindViewHolder(final BaseRecyclerViewHolder viewHolder, final int position)
    {
        if (isEmpty()) {
            return;
        }
        if (isHeaderViewPos(position)) {
            return;
        }
        if (isFooterViewPos(position)) {
            return;
        }

        if (getData().size() != 0) {
            onViewAttach(position - getHeadersCount(), list.get(position - getHeadersCount()), viewHolder);
        }

        if (itemClickListener != null) {
            viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    itemClickListener.onItemClick(viewHolder.getConvertView(), list, viewHolder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        if (isEmpty()) {
            return 1;
        } else {
            return getHeadersCount() + getFootersCount() + getRealItemCount();
        }
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);

        GridSpaceItemDecoration divider = new GridSpaceItemDecoration.Builder(recyclerView.getContext())
                .setHorizontalSpan(horizontalSpacing)
                .setVerticalSpan(verticalSpacing)
                .setColor(Color.parseColor("#f3f5f7"))
                .setShowLastLine(false)
                .setColor(context.getResources().getColor(dividerColor))
                .build();
        recyclerView.addItemDecoration(divider);

        WrapperUtils.onAttachedToRecyclerView(recyclerView, new WrapperUtils.SpanSizeCallback() {
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position)
            {
                int viewType = getItemViewType(position);
                if (isEmpty()) {
                    return layoutManager.getSpanCount();
                } else if (mHeaderViews.get(viewType) != null) {
                    return layoutManager.getSpanCount();
                } else if (mFootViews.get(viewType) != null) {
                    return layoutManager.getSpanCount();
                }
                if (oldLookup != null)
                    return oldLookup.getSpanSize(position);
                return 1;
            }
        });
    }


    public void setVerticalSpacing(float verticalSpacing)
    {
        this.verticalSpacing = verticalSpacing;
    }

    public void setHorizontalSpacing(float horizontalSpacing)
    {
        this.horizontalSpacing = horizontalSpacing;
    }

    public void setDividerColor(@ColorRes int dividerColor)
    {
        this.dividerColor = dividerColor;
    }


    @Override
    public void onViewAttachedToWindow(BaseRecyclerViewHolder holder)
    {
        super.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (isEmpty() || isHeaderViewPos(position) || isFooterViewPos(position)) {
            WrapperUtils.setFullSpan(holder);
        }
    }

    @Override
    public void resetData(List<T> data)
    {
        afterSetData = true;
        if (null == data) {
            return;
        }
        if (null == this.list) {
            this.list = new ArrayList<>();
        } else {
            this.list.clear();
        }
        addData(data);
    }

    @Override
    public void addData(List<T> data)
    {
        afterSetData = true;
        if (null == data) {
            return;
        }

        if (null == this.list) {
            this.list = new ArrayList<>();
        }
        this.list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public List<T> getData()
    {
        if (null == list) {
            list = new ArrayList<>();
        }
        return this.list;
    }

    public void removeData(int position)
    {
        list.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void clearData()
    {
        if (list != null) {
            list.clear();
        }
        notifyDataSetChanged();

        afterSetData = false;
    }

    public Context getContext()
    {
        return context;
    }

    /**
     * 为RecyclerView设置一个空内容布局，如果没有调用过addData(data)或者resetData(data)，那么这个方法将不会生效（无法显示出emptyView）
     *
     * @param view 空布局view
     */
    public void setEmptyView(View view)
    {
        this.emptyView = view;
    }

    public void setOnRecyclerItemClickListener(BaseRecyclerFrameAdapter.OnRecyclerItemClickListener<T> itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    /**
     * 判断是否是头部view
     *
     * @param position view的位置
     * @return 是否是头部view
     */
    private boolean isHeaderViewPos(int position)
    {
        return position < getHeadersCount();
    }


    /**
     * 获取真实的Item数量
     *
     * @return 真实的Item数量
     */
    private int getRealItemCount()
    {
        return getData().size();
    }


    /**
     * 判断是否是底部view
     *
     * @param position view的位置
     * @return 是否是底部view
     */
    private boolean isFooterViewPos(int position)
    {
        return position >= getHeadersCount() + getRealItemCount();
    }


    /**
     * 增加头部View
     *
     * @param view 头部view
     */
    public void addHeaderView(View view)
    {
        mHeaderViews.put(mHeaderViews.size() + BaseAdapterViewType.VIEW_TYPE_HEADER, view);
    }

    /**
     * 增加底部View
     *
     * @param view 底部view
     */
    public void addFootView(View view)
    {
        mFootViews.put(mFootViews.size() + BaseAdapterViewType.VIEW_TYPE_FOOTER, view);
    }

    /**
     * 获取头部View的数量
     *
     * @return 头部view的数量
     */
    public int getHeadersCount()
    {
        return mHeaderViews.size();
    }


    /**
     * 获取底部View的数量
     *
     * @return 底部view的数量
     */
    public int getFootersCount()
    {
        return mFootViews.size();
    }

}
