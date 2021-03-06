package com.hop.pirate.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hop.pirate.Constants;
import com.hop.pirate.R;
import com.hop.pirate.activity.MyPoolActivity;
import com.hop.pirate.adapter.RechargeAdapter;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.TabPacketsMarketModel;
import com.hop.pirate.model.bean.MinePoolBean;
import com.hop.pirate.model.impl.TabPacketsMarketModelImpl;
import com.hop.pirate.util.Utils;
import com.hop.pirate.util.WrapContentLinearLayoutManager;

import java.util.List;

public class TabPacketsMarketFragment extends BaseFragement implements View.OnClickListener {
    private TabPacketsMarketModel mTabPacketsMarketModel;
    private RechargeAdapter mRechargeAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageView mEmptyIv;
    private TextView mEmptyTv;
    private TextView myPoolTv;
    private List<MinePoolBean> mMinePoolBeans;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_packets_market, null);
        initViews(view);
        mTabPacketsMarketModel = new TabPacketsMarketModelImpl();
        mSwipeRefreshLayout.setRefreshing(true);
        getPoolInfo();
        return view;
    }

    @Override
    public void initViews(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        RecyclerView rechargeRecycleView = view.findViewById(R.id.rechargeRecycleView);
        mEmptyIv = view.findViewById(R.id.emptyIv);
        mEmptyTv = view.findViewById(R.id.emptyTv);
        myPoolTv = view.findViewById(R.id.myPoolTv);
        mRechargeAdapter = new RechargeAdapter(mActivity);
        rechargeRecycleView.setLayoutManager(new WrapContentLinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        rechargeRecycleView.setAdapter(mRechargeAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPoolInfo();
            }
        });
        myPoolTv.setOnClickListener(this);
    }


    public void getPoolInfo() {
        mTabPacketsMarketModel.getPoolInfo(new ResultCallBack<List<MinePoolBean>>() {
            @Override
            public void onError(Throwable e) {
                mSwipeRefreshLayout.setRefreshing(false);
                Utils.toastException(mActivity, e, Constants.REQUEST_PACKETS_MARKET_ERROR);

            }

            @Override
            public void onSuccess(List<MinePoolBean> minePoolBeans) {
                mMinePoolBeans = minePoolBeans;
            }

            @Override
            public void onComplete() {
                setData();
            }
        });

    }


    private void setData() {
        if (mMinePoolBeans == null || mMinePoolBeans.size() == 0) {
            mEmptyIv.setVisibility(View.VISIBLE);
            mEmptyTv.setVisibility(View.VISIBLE);
        } else {
            mEmptyIv.setVisibility(View.GONE);
            mEmptyTv.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout.setRefreshing(false);
        mRechargeAdapter.setMinePoolBeans(mMinePoolBeans);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mTabPacketsMarketModel.removeAllSubscribe();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mActivity, MyPoolActivity.class);
        startActivity(intent);
    }
}
