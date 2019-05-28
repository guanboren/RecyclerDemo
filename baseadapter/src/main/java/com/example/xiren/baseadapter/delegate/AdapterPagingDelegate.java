package com.example.xiren.baseadapter.delegate;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xiren.baseadapter.BaseAdapterViewType;
import com.example.xiren.baseadapter.BaseRecyclerPagingAdapter;
import com.example.xiren.baseadapter.BaseRecyclerViewHolder;
import com.example.xiren.baseadapter.R;
import com.example.xiren.baseadapter.interfaces.AdapterPageChangedListener;
import com.example.xiren.baseadapter.utils.ViewUtil;

/**
 * 可以控制adapter分页加载的代理
 *
 * @author Guanboren
 * @date 2019.05.27
 */

public class AdapterPagingDelegate<T> implements AdapterPageChangedListener {
    /**
     * 每次获取数据，加一个delay延迟再返回给调用者(用于优化用户体验)
     */
    private final int LOADING_DELAY_TIME = 0;

    /**
     * 正在加载
     */
    private final int STATUS_LOADING = 1;
    /**
     * 点击加载下一页
     */
    private final int STATUS_LOADING_NEED_TAP = STATUS_LOADING + 1;
    /**
     * 加载完毕
     */
    private final int STATUS_LOADING_END = STATUS_LOADING_NEED_TAP + 1;

    private int mLoadingStatus = STATUS_LOADING;

    private BaseRecyclerPagingAdapter<T> adapter;
    private PagingListener<T> pagingListener;

    /**
     * 起始请求的页码
     */
    private int startRequestPage = 0;
    /**
     * 当前加载的页码
     */
    private int page = 0;
    /**
     * 当前是否在做下一次请求
     */
    private boolean isLoadingNextPage = false;

    private LayoutInflater mInflater;

    public interface PagingListener<T> {
        /**
         * 分页的监听，返回当前adapter和当前请求的页数(页数从0开始)
         *
         * @param adapter
         * @param page    下一页，page会自动计数
         */
        void onNextPageRequest(BaseRecyclerPagingAdapter<T> adapter, int page);
    }


    public AdapterPagingDelegate(BaseRecyclerPagingAdapter<T> adapter)
    {
        this.mInflater = LayoutInflater.from(adapter.getContext());
        this.adapter = adapter;
    }

    public void setPagingListener(PagingListener<T> pagingListener)
    {
        this.pagingListener = pagingListener;

        if (adapter.getData().size() == 0) {
            adapterLoadNextPage(page);
        }
    }

    public int getItemCount()
    {
        if (pagingListener == null) {
            return adapter.getData().size();
        } else {
            return adapter.getData().size() + 1;
        }
    }

    /**
     * 根据position获取item当前展示的类型
     *
     * @param position
     */
    public int getItemViewType(int position)
    {
        if (position < adapter.getData().size() || pagingListener == null) {
            return BaseAdapterViewType.VIEW_TYPE_CONTENT;
        } else {
            switch (mLoadingStatus) {
                case STATUS_LOADING:
                    return BaseAdapterViewType.VIEW_TYPE_LOADING;
                case STATUS_LOADING_NEED_TAP:
                    return BaseAdapterViewType.VIEW_TYPE_LOADING_TAP_NEXT;
                case STATUS_LOADING_END:
                    return BaseAdapterViewType.VIEW_TYPE_LOADING_COMPLETE;
                default:
                    return BaseAdapterViewType.VIEW_TYPE_UNKNOWN;
            }
        }
    }

    /**
     * 根据viewType获取创建loadingview的样式
     *
     * @param viewType VIEW_TYPE_LOADING, VIEW_TYPE_LOADING_COMPLETE,
     *                 VIEW_TYPE_LOADING_TAP_NEXT
     * @return
     */
    private View onLoadingViewCrate(LayoutInflater inflater, int viewType, ViewGroup parent)
    {
        switch (viewType) {
            case BaseAdapterViewType.VIEW_TYPE_LOADING:
                return inflater.inflate(R.layout.adapter_loading_layout, parent, false);
            case BaseAdapterViewType.VIEW_TYPE_LOADING_COMPLETE:
                return inflater.inflate(R.layout.adapter_loading_complate_layout, parent, false);
            case BaseAdapterViewType.VIEW_TYPE_LOADING_TAP_NEXT:
                return inflater.inflate(R.layout.adapter_loading_tap_next_layout, parent, false);
            default:
                return inflater.inflate(R.layout.adapter_loading_layout, parent, false);
        }
    }

    /**
     * 创建adapter
     *
     * @param viewType
     * @param parent
     * @return
     */
    public View createLoadingView(int viewType, ViewGroup parent)
    {
        View view;
        switch (viewType) {
            case BaseAdapterViewType.VIEW_TYPE_LOADING: {
                view = onLoadingViewCrate(mInflater, viewType, parent);
                break;
            }
            case BaseAdapterViewType.VIEW_TYPE_LOADING_COMPLETE: {
                view = onLoadingViewCrate(mInflater, viewType, parent);
                break;
            }
            case BaseAdapterViewType.VIEW_TYPE_LOADING_TAP_NEXT: {
                view = onLoadingViewCrate(mInflater, viewType, parent);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        mLoadingStatus = STATUS_LOADING;
                        adapter.notifyDataSetChanged();
                    }
                });
                break;
            }
            default: {
                view = null;
            }
        }
        return view;
    }

    public void onItemViewAttach(int position, @NonNull BaseRecyclerViewHolder viewHolder)
    {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case BaseAdapterViewType.VIEW_TYPE_CONTENT: {
                break;
            }
            case BaseAdapterViewType.VIEW_TYPE_LOADING: {
                adapterLoadNextPage(page);
                break;
            }
            case BaseAdapterViewType.VIEW_TYPE_LOADING_COMPLETE: {
                //如果商品的数量不足5件，不显示没有更多数据的view
                ViewUtil.setViewVisibility(adapter.getItemCount() > 5 ? View.VISIBLE : View.GONE, viewHolder.getConvertView());
                break;
            }
            default: {
                break;
            }
        }
    }

//    public void startPaging() {
//        adapterLoadNextPage(page);
//    }

    private synchronized void adapterLoadNextPage(final int page)
    {
        if (!isLoadingNextPage) {
            this.isLoadingNextPage = true;
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run()
                {
                    pagingListener.onNextPageRequest(adapter, page);
                }

            }, LOADING_DELAY_TIME);
        }

    }

    /**
     * 设置初始请求的页码
     *
     * @param startRequestPage 分页的起始页码
     */
    public void setStartRequestPage(int startRequestPage)
    {
        this.startRequestPage = startRequestPage;
        resetPage();
    }

    /**
     * 页码重新设置
     */
    public void resetPage()
    {
        page = startRequestPage;
        mLoadingStatus = STATUS_LOADING;
    }

    /**
     * 下面还有数据，可以继续翻页
     */
    @Override
    public void mayHaveNextPage()
    {
        page++;
        isLoadingNextPage = false;
        mLoadingStatus = STATUS_LOADING;
        adapter.notifyDataSetChanged();
    }

    /**
     * 点击按钮加载下一页代替之前滑动到底部就自动加载
     */
    @Override
    public void tapNextPage()
    {
        isLoadingNextPage = false;
        mLoadingStatus = STATUS_LOADING_NEED_TAP;
        adapter.notifyDataSetChanged();
    }

    /**
     * 已加载完全部数据，停止分页
     */
    @Override
    public void noMorePage()
    {
        isLoadingNextPage = false;
        mLoadingStatus = STATUS_LOADING_END;
        adapter.notifyDataSetChanged();
    }
}
