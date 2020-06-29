package com.hop.pirate.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.hop.pirate.Constants;
import com.hop.pirate.IntentKey;
import com.hop.pirate.R;
import com.hop.pirate.adapter.MiningPoolAdapter;
import com.hop.pirate.base.BaseActivity;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.MinePoolListModel;
import com.hop.pirate.model.bean.ExtendToken;
import com.hop.pirate.model.bean.MinePoolBean;
import com.hop.pirate.model.impl.MinePoolListModelImpl;
import com.hop.pirate.util.Utils;
import com.hop.pirate.util.WrapContentLinearLayoutManager;

import java.util.List;

public class MinePoolListActivity extends BaseActivity {
    private MinePoolListModel mMinePoolListModel;
    private RecyclerView mMiningPoolRecyclerVeiw;
    private MinePoolBean mCurrentMinePoolBean;
    private MiningPoolAdapter mMiningPoolAdapter;

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
        mMiningPoolAdapter = new MiningPoolAdapter(this, mCurrentMinePoolBean);
        mMiningPoolRecyclerVeiw.setAdapter(mMiningPoolAdapter);
    }

    @Override
    public void initData() {
        if (MainActivity.sWalletBean == null) {
            Utils.toastTips(getString(R.string.wallet_read_failed));
            return;
        }
        mCurrentMinePoolBean = (MinePoolBean) getIntent().getSerializableExtra(IntentKey.CURRENT_MINE_POOL);
        mMiningPoolRecyclerVeiw.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        showDialogFragment();
        mMinePoolListModel.getPoolDataOfUser(MainActivity.sWalletBean.getMain(), new ResultCallBack<List<MinePoolBean>>() {
            @Override
            public void onError(Throwable e) {
                dismissDialogFragment();
                Utils.toastException(MinePoolListActivity.this, e, Constants.REQUEST_OWNE_MINE_POOL_ERROR);
            }

            @Override
            public void onSuccess(List<MinePoolBean> minePoolBeans) {
                mMiningPoolAdapter.setMinePoolBeans(minePoolBeans);
            }

            @Override
            public void onComplete() {
                dismissDialogFragment();
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
}
