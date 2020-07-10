package com.hop.pirate.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hop.pirate.Constants;
import com.hop.pirate.R;
import com.hop.pirate.activity.MainActivity;
import com.hop.pirate.adapter.MinePoolForWalletAdapter;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.event.EventClearAllRequest;
import com.hop.pirate.event.EventLoadWalletSuccess;
import com.hop.pirate.event.EventRechargeSuccess;
import com.hop.pirate.event.EventSkipTabPacketsMarket;
import com.hop.pirate.model.TabWalletModel;
import com.hop.pirate.model.bean.ExtendToken;
import com.hop.pirate.model.bean.MinePoolBean;
import com.hop.pirate.model.bean.OwnPool;
import com.hop.pirate.model.bean.WalletBean;
import com.hop.pirate.model.impl.TabWalletModelImpl;
import com.hop.pirate.service.HopService;
import com.hop.pirate.util.Utils;
import com.hop.pirate.util.WrapContentLinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class TabWalletFragment extends BaseFragement implements View.OnClickListener {
    public static boolean initsyncPoolsAndUserData;
    private WalletBean mWalletBean;
    private TabWalletModel mTabWalletModel;
    private TextView mHopNumberTv;
    private TextView mEthNumberTv;
    private ImageView mAddMinePoolBgIv;
    private TextView mAddMinePoolTv;
    private RecyclerView mMinePoolRecyclerView;


    private TextView mEthUintTv;
    private TextView mHopUintTv;
    private TextView mRefreshMinPoolTv;
    private TextView mHopTv;
    private ImageView mEmptyIv;
    private TextView mEmptyTv;
    private MinePoolForWalletAdapter mMinePoolForWalletAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, null);
        EventBus.getDefault().register(this);
        mTabWalletModel = new TabWalletModelImpl();
        initViews(view);
        initData();
        getPoolDataOfUser(false);
        return view;
    }

    @Override
    public void initViews(View view) {
        mHopNumberTv = view.findViewById(R.id.hopNumberTv);
        mEthNumberTv = view.findViewById(R.id.ethNumberTv);
        mEthUintTv = view.findViewById(R.id.ethUintTv);
        mHopUintTv = view.findViewById(R.id.hopUintTv);
        mHopTv = view.findViewById(R.id.hopTv);
        mEmptyIv = view.findViewById(R.id.emptyIv);
        mEmptyTv = view.findViewById(R.id.emptyTv);

        mAddMinePoolBgIv = view.findViewById(R.id.addMinePoolBgIv);
        mAddMinePoolTv = view.findViewById(R.id.addMinePoolTv);
        mMinePoolRecyclerView = view.findViewById(R.id.minePoolRecyclerView);
        mRefreshMinPoolTv = view.findViewById(R.id.refreshMinPoolTv);

        mAddMinePoolBgIv.setOnClickListener(this);
        mAddMinePoolTv.setOnClickListener(this);
        mEthUintTv.setOnClickListener(this);
        mHopUintTv.setOnClickListener(this);
        mHopNumberTv.setOnClickListener(this);
        mEthNumberTv.setOnClickListener(this);
        mRefreshMinPoolTv.setOnClickListener(this);
        mMinePoolRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mMinePoolForWalletAdapter = new MinePoolForWalletAdapter(mActivity);
        mMinePoolRecyclerView.setAdapter(mMinePoolForWalletAdapter);
    }

    @Override
    public void onShow() {
        super.onShow();
        initData();
    }

    private void initData() {
        if (MainActivity.sWalletBean == null) {
            Utils.toastTips(getString(R.string.wallet_read_failed));
            return;
        }
        mHopNumberTv.setText(Utils.ConvertCoin(MainActivity.sWalletBean.getHop()));
        mEthNumberTv.setText(Utils.ConvertCoin(MainActivity.sWalletBean.getEth()));
        mHopTv.setText(ExtendToken.CurSymbol);
        mHopUintTv.setText(ExtendToken.CurSymbol);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refreshMinPoolTv:
                mActivity.showDialogFragment();
                ((MainActivity) mActivity).loadWallet();
                break;
            case R.id.addMinePoolBgIv:
            case R.id.addMinePoolTv:
                EventBus.getDefault().post(new EventSkipTabPacketsMarket());
                break;
        }
    }


    private void getPoolDataOfUser(boolean isShowDialog) {
        if (MainActivity.sWalletBean == null) {
            Utils.toastTips(getString(R.string.wallet_read_failed));
            return;
        }
        mTabWalletModel.getPoolDataOfUser(mActivity,MainActivity.sWalletBean.getMain(), new ResultCallBack<List<OwnPool>>() {
            @Override
            public void onError(Throwable e) {
                mActivity.dismissDialogFragment();
                Utils.toastException(mActivity, e, Constants.REQUEST_OWNE_MINE_POOL_ERROR);
            }

            @Override
            public void onSuccess(List<OwnPool> ownPools) {
                if (ownPools == null || ownPools.size() == 0) {
                    mEmptyIv.setVisibility(View.VISIBLE);
                    mEmptyTv.setVisibility(View.VISIBLE);
                } else {
                    mEmptyIv.setVisibility(View.GONE);
                    mEmptyTv.setVisibility(View.GONE);
                }
                mMinePoolForWalletAdapter.setMinePoolBeans(ownPools);
            }

            @Override
            public void onComplete() {
                mActivity.dismissDialogFragment();
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadWalletSuccess(EventLoadWalletSuccess eventLoadWalletSuccess) {
        initData();
        getPoolDataOfUser(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clearAllRequest(EventClearAllRequest eventClearAllRequest) {
        mTabWalletModel.removeAllSubscribe();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void rechargeSuccess(EventRechargeSuccess eventRechargeSuccess) {
        getPoolDataOfUser(false);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
