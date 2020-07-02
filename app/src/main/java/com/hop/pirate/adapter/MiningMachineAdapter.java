package com.hop.pirate.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hop.pirate.R;
import com.hop.pirate.model.bean.MinerBean;
import com.hop.pirate.service.SysConf;

import java.text.DecimalFormat;
import java.util.List;

public class MiningMachineAdapter extends RecyclerView.Adapter<MiningMachineAdapter.ViewHolder> {


    private List<MinerBean> mMineMachineBeans;
    private Context mContext;
    private int selectedIndex;
    private boolean showTime;
    private DecimalFormat mDecimalFormat = new DecimalFormat("0.00");

    public boolean isShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
        notifyDataSetChanged();
    }

    public MiningMachineAdapter(Context context) {
        mContext = context;
    }


    public void setMineMachineBeans(List<MinerBean> mineMachineBeans) {
        mMineMachineBeans = mineMachineBeans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mine_machine, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        final MinerBean miningMachineBean = mMineMachineBeans.get(position);
        viewHolder.miningMachineTv.setText(miningMachineBean.getMID());
        viewHolder.miningZoneTv.setText(miningMachineBean.getZone());
        if (miningMachineBean.getTime() == 0) {
            viewHolder.miningMachineTimesTv.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.miningMachineTimesTv.setVisibility(View.VISIBLE);
        }
        viewHolder.miningMachineTimesTv.setText(mDecimalFormat.format(miningMachineBean.getTime()) + ".ms");

        if (miningMachineBean.getMID().equals(SysConf.CurMinerID)) {
            viewHolder.selectedIv.setVisibility(View.VISIBLE);
            viewHolder.constraintlayout.setBackgroundResource(R.color.color_dcdde0);
            miningMachineBean.setSelected(true);
        } else {
            viewHolder.selectedIv.setVisibility(View.GONE);
            viewHolder.constraintlayout.setBackgroundResource(R.color.color_f6f6f7);
        }
        viewHolder.constraintlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (miningMachineBean.getMID().equals(SysConf.CurMinerID)) {
                    ((Activity) mContext).finish();
                    return;
                }
                SysConf.SetCurMiner(miningMachineBean.getMID());

                ((Activity) mContext).setResult(Activity.RESULT_OK, null);
                ((Activity) mContext).finish();
            }
        });

        viewHolder.measurementNetworkSpeedTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miningMachineBean.TestPing();
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mMineMachineBeans == null ? 0 : mMineMachineBeans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout constraintlayout;
        private TextView miningMachineTv;
        private TextView miningMachineTimesTv;
        private TextView measurementNetworkSpeedTv;
        private TextView miningZoneTv;
        private ImageView selectedIv;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintlayout = itemView.findViewById(R.id.constraintlayout);
            miningMachineTv = itemView.findViewById(R.id.miningMachineTv);
            measurementNetworkSpeedTv = itemView.findViewById(R.id.measurementNetworkSpeedTv);
            miningMachineTimesTv = itemView.findViewById(R.id.miningMachineTimesTv);
            selectedIv = itemView.findViewById(R.id.selectedIv);
            miningZoneTv = itemView.findViewById(R.id.miningZoneTv);
        }
    }
}
