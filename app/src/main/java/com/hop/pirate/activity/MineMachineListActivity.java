package com.hop.pirate.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hop.pirate.Constants;
import com.hop.pirate.R;
import com.hop.pirate.adapter.MiningMachineAdapter;
import com.hop.pirate.base.BaseActivity;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.MineMachineListModel;
import com.hop.pirate.model.bean.MinerBean;
import com.hop.pirate.model.impl.MineMachineListModelImpl;
import com.hop.pirate.service.SysConf;
import com.hop.pirate.util.CustomClickListener;
import com.hop.pirate.util.Utils;
import com.hop.pirate.util.WrapContentLinearLayoutManager;

import java.util.List;

public class MineMachineListActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {
    private MineMachineListModel mMineMachineListModel;
    private RecyclerView mMiningMachineRecyclerView;
    MiningMachineAdapter miningMachineAdapter;
    public static List<MinerBean> sMinerBeans;
    Handler mHandler = new Handler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_machine);
        mMineMachineListModel = new MineMachineListModelImpl();
        loadMinerUnderPool();
    }

    @Override
    public void initViews() {
        TextView titleTv = findViewById(R.id.titleTv);
        ViewGroup title = findViewById(R.id.title);
        ImageView titleRightIv = findViewById(R.id.titleRight1Iv);
        mMiningMachineRecyclerView = findViewById(R.id.miningMachineRecyclerView);

        TextView pinAllMinersTv = findViewById(R.id.pinAllMinersTv);
        TextView refreshMinetv = findViewById(R.id.refreshMinetv);

        pinAllMinersTv.setOnClickListener(new CustomClickListener() {
            @Override
            protected void onSingleClick() {
                if (sMinerBeans == null || sMinerBeans.size() == 0) {
                    return;
                }

                showDialogFragment(R.string.testing_speed);
                OverallSpeed();

            }

            @Override
            protected void onFastClick() {
            }
        });
        refreshMinetv.setOnClickListener(this);
        titleTv.setText(getResources().getString(R.string.mine_machine));
    }

    private void OverallSpeed() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                int size = sMinerBeans.size();
                for (int i = 0; i < size; i++) {
                    final MinerBean bean = sMinerBeans.get(i);
                    bean.TestPing();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }


    @Override
    public void initData() {
        mMiningMachineRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        miningMachineAdapter = new MiningMachineAdapter(this);
        mMiningMachineRecyclerView.setAdapter(miningMachineAdapter);
    }

    @Override
    public void onClick(View v) {
        loadMinerUnderPool();
    }

    @Override
    public void cancelWaitDialog() {
        super.cancelWaitDialog();
        mMineMachineListModel.removeAllSubscribe();
    }

    private void loadMinerUnderPool() {
        showDialogFragment();
        mMineMachineListModel.getMinemachine(this, SysConf.CurPoolAddress, 16, new ResultCallBack<List<MinerBean>>() {
            @Override
            public void onError(Throwable e) {
                dismissDialogFragment();
                Utils.toastException(MineMachineListActivity.this, e, Constants.REQUEST_MINE_MACHINE_ERROR);
            }

            @Override
            public void onSuccess(List<MinerBean> minerBeans) {
                sMinerBeans = minerBeans;
                miningMachineAdapter.setMineMachineBeans(sMinerBeans);
                miningMachineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onComplete() {
                dismissDialogFragment();
            }
        });

    }

    @Override
    public boolean handleMessage(Message msg) {
        dismissDialogFragment();
        miningMachineAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        if (mMineMachineListModel != null) {
            mMineMachineListModel.removeAllSubscribe();
        }
    }
}