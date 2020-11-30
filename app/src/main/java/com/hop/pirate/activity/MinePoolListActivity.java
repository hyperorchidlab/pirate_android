package com.hop.pirate.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import com.hop.pirate.R;
import com.hop.pirate.adapter.MiningPoolAdapter;
import com.hop.pirate.base.BaseActivity;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.MinePoolListModel;
import com.hop.pirate.model.bean.ExtendToken;
import com.hop.pirate.model.bean.MinePoolBean;
import com.hop.pirate.model.impl.MinePoolListModelImpl;
import com.hop.pirate.service.WalletWrapper;
import com.hop.pirate.util.Utils;
import com.hop.pirate.util.WrapContentLinearLayoutManager;

import java.util.List;

public class MinePoolListActivity extends BaseActivity implements Handler.Callback {
    private MinePoolListModel mMinePoolListModel;
    private RecyclerView mMiningPoolRecyclerVeiw;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static MinePoolBean mCurrentMinePoolBean;
    private MiningPoolAdapter mMiningPoolAdapter;
    public static List<MinePoolBean> sMinePoolBeans;
    private int RELOAD_TIMES = 3;
    private int currentReloadTimes = 0;
    private Handler handler = new Handler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMinePoolListModel = new MinePoolListModelImpl();
        setContentView(R.layout.activity_mine_pool);
    }


    @Override
    public void initViews() {
        ((TextView) findViewById(R.id.titleTv)).setText(ExtendToken.CurSymbol);
        mMiningPoolRecyclerVeiw = findViewById(R.id.miningPoolRecyclerVeiw);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mMiningPoolAdapter = new MiningPoolAdapter(this, mCurrentMinePoolBean);
        mMiningPoolRecyclerVeiw.setAdapter(mMiningPoolAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPoolData(true);
            }
        });
    }

    @Override
    public void initData() {
        if (TextUtils.isEmpty(WalletWrapper.MainAddress)) {
            Utils.toastTips(getString(R.string.wallet_read_failed));
            return;
        }

        mMiningPoolRecyclerVeiw.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if (sMinePoolBeans == null || sMinePoolBeans.size() == 0) {
            swipeRefreshLayout.setRefreshing(true);
        } else {
            mMiningPoolAdapter.setMinePoolBeans(sMinePoolBeans);
        }
        getPoolData(false);

    }

    private void getPoolData(boolean isSync) {
        mMinePoolListModel.getPoolDataOfUser(this, WalletWrapper.MainAddress, isSync, new ResultCallBack<List<MinePoolBean>>() {
            @Override
            public void onError(Throwable e) {
                swipeRefreshLayout.setRefreshing(false);
                Utils.toastTips(getString(R.string.get_data_failed));
            }

            @Override
            public void onSuccess(List<MinePoolBean> minePoolBeans) {
                if ((minePoolBeans == null || minePoolBeans.size() == 0) && currentReloadTimes < RELOAD_TIMES) {
                    currentReloadTimes++;
                    handler.sendEmptyMessageDelayed(0, 1000);
                } else {
                    sMinePoolBeans = minePoolBeans;
                    swipeRefreshLayout.setRefreshing(false);
                    mMiningPoolAdapter.setMinePoolBeans(minePoolBeans);
                    Utils.toastTips(getString(R.string.loading_success));
                }

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void cancelWaitDialog() {
        super.cancelWaitDialog();
        mMinePoolListModel.removeAllSubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMinePoolListModel != null) {
            mMinePoolListModel.removeAllSubscribe();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        getPoolData(false);
        return false;
    }
}
