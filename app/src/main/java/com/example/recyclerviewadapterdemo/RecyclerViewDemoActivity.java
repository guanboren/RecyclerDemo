package com.example.recyclerviewadapterdemo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xiren.baseadapter.BaseRecyclerFrameAdapter;
import com.example.xiren.baseadapter.BaseRecyclerPagingAdapter;
import com.example.xiren.baseadapter.BaseRecyclerViewHolder;
import com.example.xiren.baseadapter.delegate.AdapterPagingDelegate;
import com.example.xiren.baseadapter.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_demo);
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView_main);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        TestAdapter mAdapter = new TestAdapter(this);

        // GridLayoutManager 勿设置
//        mAdapter.setDividerColor(R.color.colorPrimary);

        mAdapter.setHorizontalSpacing(ViewUtil.dip2px(this, 10));

        // GridLayoutManager 设置无效
        mAdapter.setVerticalSpacing(ViewUtil.dip2px(this, 10));

        View view = LayoutInflater.from(this).inflate(R.layout.view_recyclerview_empty, mRecyclerView, false);
        mAdapter.setEmptyView(view);

        setRecyclerHeader(mAdapter);

        setRecyclerFooter(mAdapter);

        mRecyclerView.setAdapter(mAdapter);


        mAdapter.getPagingDelegate().setStartRequestPage(1);
        mAdapter.setPagingListener(new AdapterPagingDelegate.PagingListener<String>() {
            @Override
            public void onNextPageRequest(BaseRecyclerPagingAdapter<String> adapter, int page)
            {
                requestData(page, adapter);
            }
        });
    }


    private ViewGroup.LayoutParams getLayoutParams()
    {
        return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
    }

    private void setRecyclerHeader(BaseRecyclerFrameAdapter mAdapter)
    {
        TextView mHeader = new TextView(this);
        mHeader.setLayoutParams(getLayoutParams());
        mHeader.setGravity(Gravity.CENTER);
        mHeader.setText("我是头部");
        mHeader.setBackgroundColor(Color.parseColor("#f3f5f7"));
        mAdapter.addHeaderView(mHeader);
    }


    private void setRecyclerFooter(BaseRecyclerFrameAdapter mAdapter)
    {
        TextView mFooter = new TextView(this);
        mFooter.setLayoutParams(getLayoutParams());
        mFooter.setBackgroundColor(Color.parseColor("#f3f5f7"));
        mFooter.setGravity(Gravity.CENTER);
        mFooter.setText("我是底部");
        mAdapter.addFootView(mFooter);
    }


    // 模拟请求服务器
    private void requestData(final int page, final BaseRecyclerPagingAdapter<String> adapter)
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                if (page == 1) {
                    adapter.getPagingDelegate().mayHaveNextPage();
                    adapter.resetData(getData());
                } else if (page == 5) {
                    adapter.getPagingDelegate().noMorePage();
                } else {
                    adapter.getPagingDelegate().mayHaveNextPage();
                    adapter.addData(getData());
                }
            }
        }, 2000);
    }

    private List<String> getData()
    {
        List<String> strs = new ArrayList<String>();
        strs.add("1");
        strs.add("2");
        strs.add("3");
        strs.add("4");
        strs.add("5");
        strs.add("6");
        strs.add("7");
        strs.add("8");
        strs.add("9");
        strs.add("10");
        return strs;
    }


    public class TestAdapter extends BaseRecyclerPagingAdapter<String> {

        TestAdapter(Context context)
        {
            super(context);
        }

        @Override
        public View onViewCreate(int viewType, @NonNull LayoutInflater inflater, ViewGroup parent)
        {
            TextView mTextView = new TextView(getContext());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewUtil.dip2px(getContext(), 200f));
            mTextView.setLayoutParams(layoutParams);
            mTextView.setGravity(Gravity.CENTER);
            return mTextView;
        }

        @Override
        public void onViewAttach(int position, @NonNull String item, @NonNull BaseRecyclerViewHolder viewHolder)
        {
            if (viewHolder.itemView instanceof TextView) {
                TextView itemView = (TextView) viewHolder.itemView;
                viewHolder.itemView.setBackgroundColor(Color.parseColor("#ff5000"));
                itemView.setText(item);
            }
        }
    }
}
