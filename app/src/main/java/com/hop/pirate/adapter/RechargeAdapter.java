package com.hop.pirate.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hop.pirate.R;
import com.hop.pirate.activity.RechargePacketsActivity;
import com.hop.pirate.model.bean.MinePoolBean;
import com.hop.pirate.util.Utils;

import java.util.List;

public class RechargeAdapter extends RecyclerView.Adapter<RechargeAdapter.ViewHolder> {
    private Context mContext;
    private List<MinePoolBean> mMinePoolBeans;
    private int[] colorIds = {R.color.color_6d97ce, R.color.color_f7aa6e, R.color.color_4cc2d0};

    public RechargeAdapter(Context context) {
        mContext = context;
    }

    public void setMinePoolBeans(List<MinePoolBean> minePoolBeans) {
        mMinePoolBeans = minePoolBeans;
        notifyDataSetChanged();
    }

    public List<MinePoolBean> getMinePoolBeans() {
        return mMinePoolBeans;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recharge, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final MinePoolBean minePoolBean = mMinePoolBeans.get(i);
        GradientDrawable mDrawable = (GradientDrawable) viewHolder.mConstraintLayout.getBackground();
        mDrawable.setColor(ContextCompat.getColor(mContext, colorIds[i % 3]));
        mDrawable.setCornerRadius(16);
        viewHolder.mConstraintLayout.setBackground(mDrawable);
        viewHolder.minePoolNameTv.setText(minePoolBean.getName());
        viewHolder.websiteAddressTv.setText(mContext.getString(R.string.websitAddress) + minePoolBean.getWebsiteAddress());
        viewHolder.emailTv.setText(mContext.getString(R.string.email) + minePoolBean.getEmail());
        viewHolder.minePoolAddressTv.setText(minePoolBean.getAddress());
        viewHolder.mortgageNumberTv.setText(mContext.getString(R.string.gtn) + Utils.ConvertCoin(minePoolBean.getMortgageNumber()));
        viewHolder.rechargeTv.setTextColor(ContextCompat.getColor(mContext, colorIds[i % 3]));
        viewHolder.rechargeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, RechargePacketsActivity.class);
                i.putExtra(RechargePacketsActivity.PoolKey, minePoolBean.getAddress());
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMinePoolBeans == null ? 0 : mMinePoolBeans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout mConstraintLayout;
        private TextView minePoolNameTv;
        private TextView websiteAddressTv;
        private TextView emailTv;
        private TextView rechargeTv;
        private TextView minePoolAddressTv;
        private TextView mortgageNumberTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mConstraintLayout = itemView.findViewById(R.id.constraintLayout);
            minePoolNameTv = itemView.findViewById(R.id.minePoolNameTv);
            websiteAddressTv = itemView.findViewById(R.id.websiteAddressTv);
            emailTv = itemView.findViewById(R.id.emailTv);
            rechargeTv = itemView.findViewById(R.id.rechargeTv);
            minePoolAddressTv = itemView.findViewById(R.id.minePoolAddressTv);
            mortgageNumberTv = itemView.findViewById(R.id.mortgageNumberTv);
        }
    }
}
