package com.hop.pirate.adapter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hop.pirate.R;
import com.hop.pirate.activity.MineMachineListActivity;
import com.hop.pirate.activity.SupportedCurrenciesActivity;
import com.hop.pirate.base.BaseActivity;
import com.hop.pirate.event.EventReloadPoolsMarket;
import com.hop.pirate.model.bean.MinePoolBean;
import com.hop.pirate.service.HopService;
import com.hop.pirate.service.SysConf;
import com.hop.pirate.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MiningPoolAdapter extends RecyclerView.Adapter<MiningPoolAdapter.ViewHolder> implements Handler.Callback {


    private List<MinePoolBean> mMinePoolBeans = new ArrayList<>();
    private BaseActivity mContext;
    private MinePoolBean mCurrentMinePoolBean;
    private Handler mHandler;

    public MiningPoolAdapter(BaseActivity context, MinePoolBean minePoolBean) {
        this.mCurrentMinePoolBean = minePoolBean;
        mContext = context;
        mHandler = new Handler(this);
    }

    public void setMinePoolBeans(List<MinePoolBean> minePoolBeans) {
        mMinePoolBeans.clear();
        mMinePoolBeans.addAll(minePoolBeans);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mining_pool_for_mining, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        final MinePoolBean minePoolBean = mMinePoolBeans.get(position);
        viewHolder.poolNameTv.setText(minePoolBean.getName());
        if (mCurrentMinePoolBean != null && minePoolBean.getWebsiteAddress().equals(mCurrentMinePoolBean.getWebsiteAddress())) {
            viewHolder.constraintlayout.setBackgroundColor(mContext.getResources().getColor(R.color.color_f0be50));
        } else {
            viewHolder.constraintlayout.setBackgroundColor(mContext.getResources().getColor(R.color.color_ffffff));
        }
        viewHolder.constraintlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (minePoolBean.getAddress().equals(SysConf.CurPoolAddress)) {
                    ((Activity) mContext).finish();
                    return;
                }
                if (Utils.getApplication(mContext).isRunning()) {
                    Utils.getApplication(mContext).setRunning(false);
                    HopService.stop();
                }

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mContext.showDialogFragment(R.string.mining_pool_exchange_mine_pool, false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MineMachineListActivity.sMinerBeans = null;
                                SysConf.changeCurPool(minePoolBean.getAddress(), minePoolBean.getName());
                                EventBus.getDefault().post(new EventReloadPoolsMarket());
                                mContext.dismissDialogFragment();
                                ((Activity) mContext).setResult(Activity.RESULT_OK);
                                ((Activity) mContext).finish();
                            }
                        }).start();
                    }
                }, 200);


            }
        });


    }

    @Override
    public int getItemCount() {
        return mMinePoolBeans == null ? 0 : mMinePoolBeans.size();
    }

    @Override
    public boolean handleMessage(Message msg) {

        return false;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout constraintlayout;
        private TextView poolNameTv;
        private TextView poolStatusTv;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintlayout = itemView.findViewById(R.id.constraintlayout);
            poolNameTv = itemView.findViewById(R.id.poolNameTv);
            poolStatusTv = itemView.findViewById(R.id.poolStatusTv);

        }
    }
}
