package com.hop.pirate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.hop.pirate.Constants;
import com.hop.pirate.R;
import com.hop.pirate.base.BaseActivity;
import com.hop.pirate.callback.AlertDialogOkCallBack;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.event.EventClearAllRequest;
import com.hop.pirate.event.EventLoadWalletSuccess;
import com.hop.pirate.event.EventRechargeSuccess;
import com.hop.pirate.event.EventSkipTabPacketsMarket;
import com.hop.pirate.event.EventSyncVersion;
import com.hop.pirate.fragment.TabHomeFragment;
import com.hop.pirate.fragment.TabPacketsMarketFragment;
import com.hop.pirate.fragment.TabWalletFragment;
import com.hop.pirate.model.MainModel;
import com.hop.pirate.model.bean.WalletBean;
import com.hop.pirate.model.impl.MainModelImpl;
import com.hop.pirate.service.HopService;
import com.hop.pirate.service.WalletWrapper;
import com.hop.pirate.util.BottomNavigatorAdapter;
import com.hop.pirate.util.FragmentNavigator;
import com.hop.pirate.util.Utils;
import com.hop.pirate.view.BottomNavigatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidLib.AndroidLib;

public class MainActivity extends BaseActivity implements androidLib.HopDelegate {
    public static WalletBean sWalletBean;
    public static boolean isSyncVersion = false;
    private MainModel mMainModel;
    public static final String TAG = "HopProtocol";
    public static final int ATSysSettingChanged = 1;
    public static final int ATPoolsInMarketChanged = 2;
    public static final int ATCounterDataRead = 3;
    public static final int ATNeedToRecharge = 4;
    public static final int ATRechargeSuccess = 5;
    private Class[] mFragmentArray = {TabHomeFragment.class, TabPacketsMarketFragment.class,
            TabWalletFragment.class};
    private BottomNavigatorView bottomNavigatorView;
    private FragmentNavigator mNavigator;
    private TextView mGetFreeCoinTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initNavigator(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        mMainModel = new MainModelImpl();
        if (Utils.getApplication(this).isRunning()) {
            return;
        }
        initService();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Utils.getApplication(this).isRunning()) {
            return;
        } else {
            AndroidLib.stopProtocol();
        }
        initService();


    }

    void initService() {
        mMainModel.initService(this, this, new ResultCallBack<String>() {
            @Override
            public void onError(Throwable e) {
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
                loadWallet(false);
                mMainModel.syncVersion();
            }
        });

    }


    public void loadWallet(final boolean isShowLoading) {
        mMainModel.getWalletInfo(this, new ResultCallBack<WalletBean>() {
            @Override
            public void onError(Throwable e) {
                if (isShowLoading) {
                    dismissDialogFragment();
                }
                Utils.toastException(MainActivity.this, e, Constants.REQUEST_WALLET_INFO_ERROR);
            }

            @Override
            public void onSuccess(WalletBean walletBean) {
                sWalletBean = walletBean;
                WalletWrapper.MainAddress = walletBean.getMain();
                WalletWrapper.SubAddress = walletBean.getSub();
                WalletWrapper.EthBalance = walletBean.getEth();
                WalletWrapper.HopBalance = walletBean.getHop();
                WalletWrapper.Approved = walletBean.getApproved();
                String tokenName = Utils.getString(Constants.CUR_SYMBOL, "HOP");
                if (sWalletBean.getHop() != 0 && tokenName.startsWith("HOP")) {
                    gone(mGetFreeCoinTv);
                }

            }

            @Override
            public void onComplete() {
                if (isShowLoading) {
                    dismissDialogFragment();
                }
                EventBus.getDefault().postSticky(new EventLoadWalletSuccess());
            }
        });
    }

    private void initNavigator(Bundle savedInstanceState) {
        BottomNavigatorAdapter bottomNavigatorAdapter = new BottomNavigatorAdapter(this);
        for (Class fragment : mFragmentArray) {
            bottomNavigatorAdapter
                    .addTab(new BottomNavigatorAdapter.TabInfo(fragment.getSimpleName(), fragment, null));
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
                setCurrentTab(Constants.TAB_WALLET);
                gone(mGetFreeCoinTv);
            }
        });
    }

    @Override
    public void initData() {
    }

    private void setCurrentTab(int position) {
        if (null != mNavigator) {
            mNavigator.showFragment(position);
        }
        if (null != bottomNavigatorView) {
            bottomNavigatorView.select(position);
        }
        setFreeCoinStatus(position);
    }

    private void setFreeCoinStatus(int position) {
        if (position != Constants.TAB_WALLET && WalletWrapper.EthBalance == 0) {
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
    public void eventSyncVersion(EventSyncVersion eventSyncVersion) {
        mMainModel.syncVersion();
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
                break;
            case ATNeedToRecharge:
                Utils.toastTips(getResources().getString(R.string.packets_insufficient_need_recharge));
                break;
            case ATRechargeSuccess:
                EventBus.getDefault().post(new EventRechargeSuccess());
                break;
            default:
                break;

        }
    }


    @Override
    public void serviceExit(Exception err) {
        //Broad cast to restart service
        Utils.getApplication(this).setRunning(false);
        HopService.stop();

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void rechargeSuccess(EventRechargeSuccess eventRechargeSuccess) {
        loadWallet(false);
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
        if (!Utils.getApplication(this).isRunning()) {
            AndroidLib.stopProtocol();
        }
        if (mMainModel != null) {
            mMainModel.removeAllSubscribe();
        }

        EventBus.getDefault().unregister(this);
    }


}
