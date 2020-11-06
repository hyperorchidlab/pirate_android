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

import com.hop.pirate.R;
import com.hop.pirate.adapter.MiningMachineAdapter;
import com.hop.pirate.base.BaseActivity;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.MineMachineListModel;
import com.hop.pirate.model.bean.MinerBean;
import com.hop.pirate.model.impl.MineMachineListModelImpl;
import com.hop.pirate.service.SysConf;
import com.hop.pirate.util.CustomClickListener;
import com.hop.pirate.util.WrapContentLinearLayoutManager;

import java.util.List;

public class MineMachineListActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {
    private MineMachineListModel mMineMachineListModel;
    private RecyclerView mMiningMachineRecyclerView;
    private MiningMachineAdapter miningMachineAdapter;
    public static List<MinerBean> sMinerBeans;
    Handler mHandler = new Handler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_machine);


    }

    @Override
    public void initViews() {
        TextView titleTv = findViewById(R.id.titleTv);
        mMiningMachineRecyclerView = findViewById(R.id.miningMachineRecyclerView);

        TextView pinAllMinersTv = findViewById(R.id.pinAllMinersTv);
        TextView refreshMineTv = findViewById(R.id.refreshMinetv);

        pinAllMinersTv.setOnClickListener(new CustomClickListener() {
            @Override
            protected void onSingleClick() {
                if (sMinerBeans == null || sMinerBeans.size() == 0) {
                    return;
                }

                showDialogFragment(R.string.testing_speed);
                overallSpeed();

            }

            @Override
            protected void onFastClick() {
            }
        });
        refreshMineTv.setOnClickListener(this);
        titleTv.setText(getResources().getString(R.string.mine_machine));
    }

    private void overallSpeed() {

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
        mMineMachineListModel = new MineMachineListModelImpl();
        mMiningMachineRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        miningMachineAdapter = new MiningMachineAdapter(this);
        mMiningMachineRecyclerView.setAdapter(miningMachineAdapter);

        if (sMinerBeans == null || sMinerBeans.size() == 0) {
            showDialogFragment();
        } else {
            miningMachineAdapter.setMineMachineBeans(sMinerBeans);
            return;
        }
        loadMinerUnderPool(sMinerBeans == null || sMinerBeans.size() == 0);
    }

    @Override
    public void onClick(View v) {
        showDialogFragment();
        loadMinerUnderPool(true);
    }

    @Override
    public void cancelWaitDialog() {
        super.cancelWaitDialog();
        mMineMachineListModel.removeAllSubscribe();
    }

    private void loadMinerUnderPool(final boolean hasLoading) {
        mMineMachineListModel.getMineMachine(this, SysConf.CurPoolAddress, 16, new ResultCallBack<List<MinerBean>>() {
            @Override
            public void onError(Throwable e) {
                if (hasLoading) {
                    showErrorDialog(R.string.get_data_failed);
                }
            }

            @Override
            public void onSuccess(List<MinerBean> minerBeans) {
                sMinerBeans = minerBeans;
                miningMachineAdapter.setMineMachineBeans(sMinerBeans);
            }

            @Override
            public void onComplete() {
                if (hasLoading) {
                    showSuccessDialog(R.string.loading_success);
                }
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