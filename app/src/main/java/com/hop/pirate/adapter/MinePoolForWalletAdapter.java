package com.hop.pirate.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hop.pirate.R;
import com.hop.pirate.activity.RechargePacketsActivity;
import com.hop.pirate.callback.ResultCallBack;
import com.hop.pirate.model.TabHomeModel;
import com.hop.pirate.model.bean.OwnPool;
import com.hop.pirate.model.bean.UserAccountData;
import com.hop.pirate.model.impl.TabHomeModelImpl;
import com.hop.pirate.service.WalletWrapper;
import com.hop.pirate.util.TimeUtil;
import com.hop.pirate.util.Utils;

import java.util.List;

public class MinePoolForWalletAdapter extends RecyclerView.Adapter<MinePoolForWalletAdapter.ViewHolder> {
    private Context mContext;
    private TabHomeModel mTabHomeModel;
    private int[] topColors = {R.color.color_6d97ce, R.color.color_ce6d8e};
    private int[] bottomColors = {R.color.color_5a84c2, R.color.color_c25a7b};

    public MinePoolForWalletAdapter(Context context) {
        mContext = context;
    }

    private List<OwnPool> mMinePoolBeans;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mine_pool_for_wallet, viewGroup, false);
        mTabHomeModel = new TabHomeModelImpl();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final OwnPool ownPool = mMinePoolBeans.get(i);

        viewHolder.minePoolNameTv.setText(ownPool.getName());
        viewHolder.rechargeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, RechargePacketsActivity.class);
                i.putExtra(RechargePacketsActivity.PoolKey, ownPool.getAddress());
                mContext.startActivity(i);
            }
        });
        mTabHomeModel.getUserDataOfPool(WalletWrapper.MainAddress, ownPool.getAddress(), new ResultCallBack<UserAccountData>() {
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onSuccess(UserAccountData userAccountData) {
                String packets = Utils.ConvertBandWidth(userAccountData.getPackets());

                viewHolder.packetsTv.setText(formatText(packets, "\nPackets"));

                double token = userAccountData.getToken();
                viewHolder.tokenTv.setText(formatText(Utils.ConvertCoin(token), " HOP\nToken"));
                String c = Utils.ConvertBandWidth(userAccountData.getCredit());
                viewHolder.creditTv.setText(formatText(c, "\nCredit"));

                viewHolder.refundTimeTv.setText("Reund Time " + TimeUtil.forMinePoolTime(userAccountData.getExpire()));
            }

            @Override
            public void onComplete() {

            }
        });


        GradientDrawable topDrawable = (GradientDrawable) viewHolder.topbgIv.getBackground();
        GradientDrawable bottomDrawable = (GradientDrawable) viewHolder.bottomBgIv.getBackground();
        int topColor = i % topColors.length;
        int bottomColor = i % bottomColors.length;
        topDrawable.setColor(mContext.getResources().getColor(topColors[topColor]));
        topDrawable.setCornerRadii(new float[]{13, 13, 13, 13, 0, 0, 0, 0});
        bottomDrawable.setColor(mContext.getResources().getColor(bottomColors[bottomColor]));
        bottomDrawable.setCornerRadii(new float[]{0, 0, 0, 0, 13, 13, 13, 13});

        viewHolder.rechargeTv.setTextColor(mContext.getResources().getColor(topColors[topColor]));
    }

    @Override
    public int getItemCount() {
        return mMinePoolBeans == null ? 0 : mMinePoolBeans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView minePoolNameTv;
        private TextView rechargeTv;
        private TextView creditTv;
        private TextView tokenTv;
        private TextView packetsTv;
        private ImageView topbgIv;
        private ImageView bottomBgIv;
        private TextView refundTimeTv;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            minePoolNameTv = itemView.findViewById(R.id.minePoolNameTv);
            rechargeTv = itemView.findViewById(R.id.rechargeTv);
            creditTv = itemView.findViewById(R.id.creditTv);
            tokenTv = itemView.findViewById(R.id.tokenTv);
            packetsTv = itemView.findViewById(R.id.packetsTv);
            topbgIv = itemView.findViewById(R.id.topbgIv);
            bottomBgIv = itemView.findViewById(R.id.bottomBgIv);
            refundTimeTv = itemView.findViewById(R.id.refundTimeTv);
        }
    }


    public void setMinePoolBeans(List<OwnPool> minePoolBeans) {
        mMinePoolBeans = minePoolBeans;
        notifyDataSetChanged();
    }

    private SpannableString formatText(String start, String end) {
        SpannableString spannableString = new SpannableString(start + end);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#aaffffff"));
        RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(0.8f);
        spannableString.setSpan(colorSpan, start.length() - 1, (start + end).length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(relativeSizeSpan, start.length() - 1, (start + end).length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
