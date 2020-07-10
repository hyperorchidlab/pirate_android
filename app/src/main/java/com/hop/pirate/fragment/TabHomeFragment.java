package com.hop.pirate.fragment;

import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hop.pirate.Constants;
import com.hop.pirate.R;
import com.hop.pirate.activity.MineMachineListActivity;
import com.hop.pirate.activity.MinePoolListActivity;
import com.hop.pirate.callback.AlertDialogOkCallBack;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.event.EventLoadWalletSuccess;
import com.hop.pirate.event.EventRechargeSuccess;
import com.hop.pirate.event.EventReloadPoolsMarket;
import com.hop.pirate.event.EventVPNClosed;
import com.hop.pirate.event.EventVPNOpen;
import com.hop.pirate.model.TabHomeModel;
import com.hop.pirate.model.bean.ExtendToken;
import com.hop.pirate.model.impl.TabHomeModelImpl;
import com.hop.pirate.service.HopService;
import com.hop.pirate.service.SysConf;
import com.hop.pirate.model.bean.UserAccountData;
import com.hop.pirate.service.WalletWrapper;
import com.hop.pirate.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidLib.AndroidLib;
import pub.devrel.easypermissions.AppSettingsDialog;

import static android.app.Activity.RESULT_OK;

public class TabHomeFragment extends BaseFragement implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, Handler.Callback {
    private static final int RC_VPN_RIGHT = 126;
    public static final String _KEY_CACHED_POOL_IN_USE_ = "_KEY_CACHED_POOL_IN_USE_%s";
    public static final String _KEY_CACHED_POOL_NAME_IN_USE_ = "_KEY_CACHED_POOL_NAME_IN_USE_%s";
    public static final String _KEY_CACHED_MINER_ID_IN_USE_ = "_KEY_CACHED_MINER_ID_IN_USE_OF_%s";
    private TabHomeModel mTabHomeModel;
    private TextView mMiningPool, mMiningMachine;
    private TextView mPackets, mCredit;
    private TextView mServiceTV;

    private RadioButton mGlobalModel;
    private RadioButton mIntelligentModel;
    private TextView mPirateNetworkStatus;
    private Intent mHopIntent;
    private Handler mHandler;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        EventBus.getDefault().register(this);
        mTabHomeModel = new TabHomeModelImpl();
        mHandler = new Handler(this);
        initViews(view);
        updateHomeData();
        loadConf();
        return view;
    }


    private void updateHomeData() {

        if (SysConf.CurPoolAddress.equals("")) {
            mMiningPool.setText(getResources().getString(R.string.select_subscribed_mining_pool));
        } else {
            mMiningPool.setText(SysConf.CurPoolName);
        }
        if (SysConf.CurMinerID.equals("")) {
            mMiningMachine.setText(getResources().getString(R.string.select_miner));
        } else {
            mMiningMachine.setText(SysConf.CurMinerID);
        }
        mPackets.setText(Utils.ConvertBandWidth(SysConf.PacketsBalance));
        mCredit.setText(Utils.ConvertBandWidth(SysConf.PacketsCredit));
    }


    private void loadConf() {
        String poolAddress = String.format(_KEY_CACHED_POOL_IN_USE_, ExtendToken.CurSymbol);
        SysConf.CurPoolAddress = Utils.getString(poolAddress, "");
        String poolName = String.format(_KEY_CACHED_POOL_NAME_IN_USE_, ExtendToken.CurSymbol);
        SysConf.CurPoolName = Utils.getString(poolName, "");

        String mKey = String.format(_KEY_CACHED_MINER_ID_IN_USE_, SysConf.CurPoolAddress);
        SysConf.CurMinerID = Utils.getString(mKey, "");

        if (SysConf.CurPoolAddress.equals("") || WalletWrapper.MainAddress.equals("")) {
            SysConf.PacketsBalance = 0;
            SysConf.PacketsCredit = 0;
            updateHomeData();
            return;
        }

        mTabHomeModel.getUserDataOfPool(WalletWrapper.MainAddress, SysConf.CurPoolAddress, new ResultCallBack<UserAccountData>() {
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onSuccess(UserAccountData userAccountData) {
                SysConf.PacketsBalance = userAccountData.getPackets();
                SysConf.PacketsCredit = userAccountData.getCredit();
            }

            @Override
            public void onComplete() {
                mActivity.dismissDialogFragment();
                updateHomeData();
            }
        });

    }


    @Override
    public void initViews(View view) {

        RadioGroup modelGrp = view.findViewById(R.id.networkModelRg);
        modelGrp.setOnCheckedChangeListener(this);
        mIntelligentModel = view.findViewById(R.id.intelligentModel);
        mGlobalModel = view.findViewById(R.id.globalModel);
        mMiningPool = view.findViewById(R.id.miningPool);
        mMiningMachine = view.findViewById(R.id.miningMachin);
        mPirateNetworkStatus = view.findViewById(R.id.pirateNetworkStatus);

        mPackets = view.findViewById(R.id.useFlowTv);
        mCredit = view.findViewById(R.id.unclearedTv);

        ImageView selectMiningIv = view.findViewById(R.id.homeMiningPoolIv);
        ImageView homeMiningIv = view.findViewById(R.id.selectMiningPoolIv);

        ImageView homeMiningMachineIv = view.findViewById(R.id.homeMiningMachinIv);
        ImageView selectMiningMachineIv = view.findViewById(R.id.selectMiningMathinIv);

        mServiceTV = view.findViewById(R.id.serviceSwitchTv);
        mServiceTV.setOnClickListener(this);

        selectMiningIv.setOnClickListener(this);
        homeMiningIv.setOnClickListener(this);
        homeMiningMachineIv.setOnClickListener(this);
        selectMiningMachineIv.setOnClickListener(this);

        if (AndroidLib.isGlobalMode()) {
            mGlobalModel.setChecked(true);
        } else {
            mIntelligentModel.setChecked(true);
        }

        if (HopService.IsRunning) {
            mPirateNetworkStatus.setText(getString(R.string.use));
        } else {
            mPirateNetworkStatus.setText(getString(R.string.unaccessed));
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.homeMiningPoolIv:
            case R.id.selectMiningPoolIv:
                intent = new Intent(mActivity, MinePoolListActivity.class);
                startActivityForResult(intent, Constants.REQUEST_MINE_POOL_CODE);
                break;
            case R.id.homeMiningMachinIv:
            case R.id.selectMiningMathinIv:
                if (SysConf.CurPoolAddress.equals("")) {
                    Utils.toastTips(getString(R.string.select_subscribed_mining_pool));
                    return;
                }
                intent = new Intent(mActivity, MineMachineListActivity.class);
                startActivityForResult(intent, Constants.REQUEST_MINE_MACHINE_CODE);
                break;
            case R.id.serviceSwitchTv:

                if (!Utils.isFastClick()) {
                    return;
                }

                if (!checkMsgforStartVpnService()) {
                    return;
                }
                if (HopService.IsRunning) {
                    HopService.Stop();
                } else {

                    Intent ii = VpnService.prepare(mActivity);
                    if (ii != null) {
                        startActivityForResult(ii, RC_VPN_RIGHT);
                    } else {
                        onActivityResult(RC_VPN_RIGHT, RESULT_OK, null);
                    }
                }
                break;
            default:
                break;
        }
    }

    private boolean checkMsgforStartVpnService() {
        if (SysConf.CurPoolAddress.equals("")) {
            Utils.toastTips(getString(R.string.select_subscribed_mining_pool));
            return false;
        }
        if (SysConf.CurMinerID.equals("")) {
            Utils.toastTips(getString(R.string.select_miner));
            return false;
        }
        return true;
    }

    void startVpnService() {

        if (!WalletWrapper.IsOpen()) {

            AlertDialogOkCallBack callBack = new AlertDialogOkCallBack() {
                @Override
                public void onClickOkButton(final String password) {
                    mActivity.showDialogFragment(R.string.open_walet);
                    startHopService(password);
                }


            };
            Utils.showPassWord(mActivity, callBack);
            return;
        }
        mActivity.showDialogFragment(R.string.connect);
        mHopIntent = new Intent(mActivity, HopService.class);
        mActivity.startService(mHopIntent);
    }

    private void startHopService(final String password) {
        mTabHomeModel.openWallet(mActivity, password, new ResultCallBack<Boolean>() {
            @Override
            public void onError(Throwable e) {
                Utils.toastException(mActivity, e, Constants.REQUEST_OPEN_WALLET_ERROR);
                mActivity.dismissDialogFragment();

            }

            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onComplete() {
                mActivity.showDialogFragment(R.string.connect);
                mHopIntent = new Intent(mActivity, HopService.class);
                mActivity.startService(mHopIntent);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == Constants.REQUEST_MINE_POOL_CODE || requestCode == Constants.REQUEST_MINE_MACHINE_CODE) {
            if (HopService.IsRunning) {
                HopService.Stop();
                mActivity.stopService(new Intent(mActivity, HopService.class));
            }
            updateHomeData();
        } else if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            Utils.toastTips("returned from setting!");
        } else if (RC_VPN_RIGHT == requestCode) {
            startVpnService();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        boolean isGlobal = checkedId == R.id.globalModel;
        AndroidLib.setGlobalModel(isGlobal);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void VPNOpen(EventVPNOpen eventVPNOpen) {
        mPirateNetworkStatus.setText(getString(R.string.use));
        mActivity.dismissDialogFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void VPNClose(EventVPNClosed eventVPNClosed) {
        mPirateNetworkStatus.setText(getString(R.string.unaccessed));
        mActivity.dismissDialogFragment();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void rechargeSuccess(EventRechargeSuccess eventRechargeSuccess) {
        loadConf();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventReloadPoolsMarket(EventReloadPoolsMarket eventReloadPoolsMarket) {
        loadConf();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadWalletSuccess(EventLoadWalletSuccess eventLoadWalletSuccess) {
        loadConf();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);

    }


    @Override
    public boolean handleMessage(Message msg) {
        mActivity.dismissDialogFragment();
        updateHomeData();
        return false;
    }

}
