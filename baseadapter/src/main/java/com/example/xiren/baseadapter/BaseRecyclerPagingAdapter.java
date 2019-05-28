package com.example.xiren.baseadapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.xiren.baseadapter.delegate.AdapterPagingDelegate;
import com.example.xiren.baseadapter.utils.WrapperUtils;

/**
 * 带分页的RecyclerAdapter
 */
public abstract class BaseRecyclerPagingAdapter<T> extends BaseRecyclerFrameAdapter<T> {

    private AdapterPagingDelegate<T> pagingDelegate;
    private AdapterPagingDelegate.PagingListener<T> pagingListener;

    public BaseRecyclerPagingAdapter(Context context)
    {
        super(context);

        pagingDelegate = new AdapterPagingDelegate<>(this);
    }

    public void setPagingListener(AdapterPagingDelegate.PagingListener<T> pagingListener)
    {
        this.pagingListener = pagingListener;
        pagingDelegate.setPagingListener(pagingListener);
    }

    /**
     * 清空数据，之后会重新执行分页方法 onNextPageRequest(adapter, page)
     */
    @Override
    public void clearData()
    {
        super.clearData();
        pagingDelegate.resetPage();
    }

    private boolean hasLoadMore()
    {
        return pagingListener != null && !isEmpty();
    }

    private boolean isShowLoadMore(int position)
    {
        return hasLoadMore() && position >= super.getItemCount();
    }

    @Override
    public int getItemCount()
    {
        return super.getItemCount() + (hasLoadMore() ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position)
    {
        if (isShowLoadMore(position)) {
            return pagingDelegate.getItemViewType(position);
        }
        return super.getItemViewType(position);
    }

    public AdapterPagingDelegate getPagingDelegate()
    {
        return pagingDelegate;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = pagingDelegate.createLoadingView(viewType, parent);

        if (itemView == null) {
            return super.onCreateViewHolder(parent, viewType);
        } else {
            return new BaseRecyclerViewHolder(itemView);
        }
//        }

    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder viewHolder, int position)
    {
        if (isShowLoadMore(position)) {
            pagingDelegate.onItemViewAttach(position, viewHolder);
            return;
        }
        super.onBindViewHolder(viewHolder, position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
        WrapperUtils.onAttachedToRecyclerView(recyclerView, new WrapperUtils.SpanSizeCallback() {
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position)
            {
                if (isShowLoadMore(position)) {
                    return layoutManager.getSpanCount();
                }
                if (oldLookup != null) {
                    return oldLookup.getSpanSize(position);
                }
                return 1;
            }
        });
    }


    @Override
    public void onViewAttachedToWindow(BaseRecyclerViewHolder holder)
    {
        super.onViewAttachedToWindow(holder);
        if (isShowLoadMore(holder.getLayoutPosition())) {
            WrapperUtils.setFullSpan(holder);
        }
    }


}
