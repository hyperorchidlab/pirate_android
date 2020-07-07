package com.hop.pirate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hop.pirate.Constants;
import com.hop.pirate.R;
import com.hop.pirate.base.BaseActivity;
import com.hop.pirate.callback.AlertDialogOkCallBack;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.event.EventClearAllRequest;
import com.hop.pirate.event.EventInitLibSuccess;
import com.hop.pirate.event.EventLoadWalletSuccess;
import com.hop.pirate.event.EventRechargeSuccess;
import com.hop.pirate.event.EventReloadPoolsMarket;
import com.hop.pirate.event.EventShowTabHome;
import com.hop.pirate.event.EventSkipTabPacketsMarket;
import com.hop.pirate.fragment.TabHomeFragment;
import com.hop.pirate.fragment.TabPacketsMarketFragment;
import com.hop.pirate.fragment.TabSettingFragment;
import com.hop.pirate.fragment.TabWalletFragment;
import com.hop.pirate.model.MainModel;
import com.hop.pirate.model.bean.WalletBean;
import com.hop.pirate.model.impl.MainModelImpl;
import com.hop.pirate.service.HopService;
import com.hop.pirate.service.SysConf;
import com.hop.pirate.service.WalletWrapper;
import com.hop.pirate.util.BottomNavigatorAdapter;
import com.hop.pirate.util.FragmentNavigator;
import com.hop.pirate.util.Utils;
import com.hop.pirate.view.BottomNavigatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidLib.AndroidLib;

public class MainActivity extends BaseActivity implements androidLib.HopDelegate, Handler.Callback {
    public static WalletBean sWalletBean;
    private MainModel mMainModel;
    public static final String TAG = "HopProtocol";
    public static final int ATSysSettingChanged = 1;
    public static final int ATPoolsInMarketChanged = 2;
    public static final int ATCounterDataRead = 3;
    public static final int ATNeedToRecharge = 4;
    public static final int ATRechargeSuccess = 5;
    private Handler mHandler;
    private String oldCreditStr = "";
    private int currentPositon;
    private Class[] mFragmentArray = {TabHomeFragment.class, TabPacketsMarketFragment.class, TabWalletFragment.class,
            TabSettingFragment.class};
    private BottomNavigatorView bottomNavigatorView;
    private FragmentNavigator mNavigator;
    private TextView mGetFreeCoinTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler(this);
        initNavigator(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        mMainModel = new MainModelImpl();
        initService();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initService();


    }

    void initService() {
        if (HopService.IsRunning) {
            return;
        }
        showDialogFragment(R.string.synchronization_block, false);
        mMainModel.initService(this, this, new ResultCallBack<String>() {
            @Override
            public void onError(Throwable e) {
                dismissDialogFragment();
                Utils.showOkAlert(MainActivity.this, R.string.tips, R.string.blockchain_sync_error, new AlertDialogOkCallBack() {

                    @Override
                    public void onClickOkButton(String parameter) {
                        finish();
                    }
                });

            }

            @Override
            public void onSuccess(String str) {

            }

            @Override
            public void onComplete() {
                loadWallet();
                mMainModel.syncAllPoolsData();
                mMainModel.initSysSeting();
                EventBus.getDefault().post(new EventInitLibSuccess());
            }
        });

    }


    public void loadWallet() {
        mMainModel.getWalletInfo(this, new ResultCallBack<WalletBean>() {
            @Override
            public void onError(Throwable e) {
                Utils.toastException(MainActivity.this, e, Constants.REQUEST_WALLET_INFO_ERROR);
                dismissDialogFragment();
            }

            @Override
            public void onSuccess(WalletBean walletBean) {
                sWalletBean = walletBean;
                String tokenName = Utils.getString(Constants.CUR_SYMBOL, "");
                if (sWalletBean.getHop() != 0 && tokenName.startsWith("HOP")) {
                    gone(mGetFreeCoinTv);
                }

            }

            @Override
            public void onComplete() {
                dismissDialogFragment();
                EventBus.getDefault().postSticky(new EventLoadWalletSuccess());
            }
        });
    }

    private void initNavigator(Bundle savedInstanceState) {
        BottomNavigatorAdapter bottomNavigatorAdapter = new BottomNavigatorAdapter(this);
        for (Class aMFragmentArray : mFragmentArray) {
            bottomNavigatorAdapter
                    .addTab(new BottomNavigatorAdapter.TabInfo(aMFragmentArray.getSimpleName(), aMFragmentArray, null));
        }
        mNavigator = new FragmentNavigator(getSupportFragmentManager(), bottomNavigatorAdapter, R.id.contentFrame);
        mNavigator.setDefaultPosition(Constants.TAB_HOME);
        mNavigator.onCreate(savedInstanceState);
    }

    @Override
    public void initViews() {
        bottomNavigatorView = findViewById(R.id.main_bottom_navigator_view);
        mGetFreeCoinTv = findViewById(R.id.getFreeCoinTv);
        setCurrentTab(mNavigator.getCurrentPosition());

        bottomNavigatorView.setOnBottomNavigatorViewItemClickListener(new BottomNavigatorView.OnBottomNavigatorViewItemClickListener() {
            @Override
            public void onBottomNavigatorViewItemClick(int position, View view) {
                setCurrentTab(position);
            }
        });
        mGetFreeCoinTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentTab(3);
                gone(mGetFreeCoinTv);
            }
        });
    }

    @Override
    public void initData() {
    }

    private void setCurrentTab(int position) {
        currentPositon = position;
        if (null != mNavigator) {
            mNavigator.showFragment(position);
        }
        if (null != bottomNavigatorView) {
            bottomNavigatorView.select(position);
        }
        setFreeCoinStatus(position);
    }

    private void setFreeCoinStatus(int position) {
        if (position != 3 && WalletWrapper.EthBalance == 0) {
            visiable(mGetFreeCoinTv);
        } else {
            gone(mGetFreeCoinTv);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addMiningPool(EventSkipTabPacketsMarket eventSkipTabPacketsMarket) {
        setCurrentTab(Constants.TAB_RECHARGE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventShowTabHome(EventShowTabHome eventShowTabHome) {
        setCurrentTab(Constants.TAB_HOME);
    }


    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        mNavigator.onSaveInstanceState(outState);
    }


    @Override
    public void actionNotify(short type, String msg) {
        Log.w(TAG, "actionNotify: type:[" + type + "] msg:=>" + msg);
        switch ((int) type) {
            case ATSysSettingChanged:
                break;
            case ATPoolsInMarketChanged:
                EventBus.getDefault().post(new EventReloadPoolsMarket());
                break;
            case ATNeedToRecharge:
                Utils.toastTips(getResources().getString(R.string.packets_nsufficient_need_recharge));
                break;
            case ATRechargeSuccess:
                EventBus.getDefault().post(new EventRechargeSuccess());
                break;

        }
    }


    @Override
    public void serviceExit(Exception err) {
        //Broad cast to restart service
        HopService.Stop();
    }

    @Override
    public void log(String str) {
        Log.i(TAG, str);
    }

    @Override
    public void cancelWaitDialog() {
        super.cancelWaitDialog();
        mMainModel.removeAllSubscribe();
        EventBus.getDefault().post(new EventClearAllRequest());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result == null) {
            return;
        }
        if (result.getContents() == null) {
            Utils.toastTips(getString(R.string.invalid_scan_code));
            return;
        }
        try {
            final String walletStr = result.getContents();
            WalletWrapper.showImportWalletDialog(this, walletStr, mHandler);

        } catch (Exception ex) {
            Utils.toastTips(getString(R.string.import_account_failed) + ex.getLocalizedMessage());
        }
    }


    @Override
    public boolean handleMessage(Message msg) {
        dismissDialogFragment();
        Utils.toastTips(getResources().getString(R.string.import_success));
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void rechargeSuccess(EventRechargeSuccess eventRechargeSuccess) {
        loadWallet();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WalletWrapper.closeWallet();
        if (!HopService.IsRunning) {
            AndroidLib.stopProtocol();
        }
        if (mMainModel != null) {
            mMainModel.removeAllSubscribe();
        }

        EventBus.getDefault().unregister(this);
    }


}
