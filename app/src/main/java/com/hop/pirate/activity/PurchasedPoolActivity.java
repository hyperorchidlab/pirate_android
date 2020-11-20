package com.hop.pirate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hop.pirate.Constants;
import com.hop.pirate.R;
import com.hop.pirate.adapter.MyPoolAdapter;
import com.hop.pirate.base.BaseActivity;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.PurchasedPoolModel;
import com.hop.pirate.model.bean.OwnPool;
import com.hop.pirate.model.impl.PurchasedPoolModelImpl;
import com.hop.pirate.service.WalletWrapper;
import com.hop.pirate.util.Utils;
import com.hop.pirate.util.WrapContentLinearLayoutManager;

import java.util.List;

/**
 * @description:
 * @author: Mr.x
 * @date :   2020/10/19 3:12 PM
 */
public class PurchasedPoolActivity extends BaseActivity {
    private PurchasedPoolModel mMyPoolModel;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageView mEmptyIv;
    private TextView mEmptyTv;
    private MyPoolAdapter mMyPoolAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pool);
    }

    @Override
    public void initViews() {
        TextView titleTv = findViewById(R.id.titleTv);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        RecyclerView myPoolRecyclerView = findViewById(R.id.mMyPoolRecyclerView);
        mEmptyIv = findViewById(R.id.emptyIv);
        mEmptyTv = findViewById(R.id.emptyTv);

        titleTv.setText(R.string.my_pool);
        myPoolRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) );
        mMyPoolAdapter = new MyPoolAdapter(this);
        myPoolRecyclerView.setAdapter(mMyPoolAdapter);
        mMyPoolModel = new PurchasedPoolModelImpl();
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMyPool();
            }
        });

    }

    @Override
    public void initData() {
        getMyPool();
    }

    private void getMyPool() {
        if (TextUtils.isEmpty(WalletWrapper.MainAddress)) {
            Utils.toastTips(getString(R.string.wallet_read_failed));
            return;
        }
        mMyPoolModel.getPoolDataOfUser(this, WalletWrapper.MainAddress, new ResultCallBack<List<OwnPool>>() {
            @Override
            public void onError(Throwable e) {
                mSwipeRefreshLayout.setRefreshing(false);
                Utils.toastException(PurchasedPoolActivity.this, e, Constants.REQUEST_OWN_MINE_POOL_ERROR);
            }

            @Override
            public void onSuccess(List<OwnPool> ownPools) {
                getMyPoolSuccess(ownPools);
            }

            @Override
            public void onComplete() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getMyPoolSuccess(List<OwnPool> ownPools) {
        if (ownPools == null || ownPools.size() == 0) {
            mEmptyIv.setVisibility(View.VISIBLE);
            mEmptyTv.setVisibility(View.VISIBLE);
        } else {
            mEmptyIv.setVisibility(View.GONE);
            mEmptyTv.setVisibility(View.GONE);
        }
        mMyPoolAdapter.setMinePoolBeans(ownPools);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.REQUEST_BUY_PACKET && resultCode == RESULT_OK){
            getMyPool();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMyPoolModel != null) {
            mMyPoolModel.removeAllSubscribe();
        }
    }


}
