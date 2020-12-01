package com.hop.pirate.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.hop.pirate.R;
import com.hop.pirate.model.bean.ExtendToken;
import com.hop.pirate.model.bean.FlowBean;
import com.hop.pirate.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FlowSelectAdapter extends RecyclerView.Adapter<FlowSelectAdapter.ViewHolder> {

    private List<FlowBean> flows = new ArrayList<>();
    private Context mContext;
    private RechargeFlowState mRechargeFlowState;
    private double mBytesPerToken;

    public interface RechargeFlowState {
        void recharge(double bytesInm);
    }

    public FlowSelectAdapter(Context context, double mBytesPerToken, RechargeFlowState rechargeFlowState) {

        mContext = context;
        this.mBytesPerToken = mBytesPerToken;
        flows.add(new FlowBean(500, 500 / mBytesPerToken, 0));
        flows.add(new FlowBean(1000, 1000 / mBytesPerToken, 0));
        flows.add(new FlowBean(2000, 2000 / mBytesPerToken, 0));
        flows.add(new FlowBean(5000, 5000 / mBytesPerToken, 0));
        flows.add(new FlowBean(8000, 8000 / mBytesPerToken, 0));
        flows.add(new FlowBean(0, 0, 1));
        this.mRechargeFlowState = rechargeFlowState;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_flow_select, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        final FlowBean flowBean = flows.get(position);
        if (flowBean.getType() == 1) {
            viewHolder.customTv.setVisibility(View.VISIBLE);
            viewHolder.flowTv.setVisibility(View.GONE);
            viewHolder.hopTv.setVisibility(View.GONE);
        } else {
            viewHolder.customTv.setVisibility(View.GONE);
            viewHolder.flowTv.setVisibility(View.VISIBLE);
            viewHolder.hopTv.setVisibility(View.VISIBLE);
            if (flowBean.getFlow() >= 1000) {
                viewHolder.flowTv.setText(flowBean.getFlow() / 1000 + "G");
            } else {
                viewHolder.flowTv.setText(flowBean.getFlow() + "M");
            }

            viewHolder.hopTv.setText(String.format(Locale.CHINA, "%.4f ", flowBean.getHop()) + ExtendToken.CurSymbol);
        }
        if (flowBean.isSelected()) {
            viewHolder.flowTv.setTextColor(mContext.getResources().getColor(R.color.color_ffffff));
            viewHolder.hopTv.setTextColor(mContext.getResources().getColor(R.color.color_ffffff));
            viewHolder.constraintlayout.setBackgroundResource(R.drawable.bg_item_recharge_flow_selected);
        } else {
            viewHolder.flowTv.setTextColor(mContext.getResources().getColor(R.color.color_6791c8));
            viewHolder.hopTv.setTextColor(mContext.getResources().getColor(R.color.color_6791c8));
            viewHolder.constraintlayout.setBackgroundResource(R.drawable.bg_item_recharge_flow_normal);
        }
        viewHolder.constraintlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < flows.size(); i++) {
                    flows.get(i).setSelected(i == position);
                }
                notifyDataSetChanged();
                if (flowBean.getType() == 1) {
                    showCustomerBuyFlowDialog();
                } else {
                    showPasswordDialog(flowBean.getHop());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return flows == null ? 0 : flows.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout constraintlayout;
        private TextView flowTv;
        private TextView hopTv;
        private TextView customTv;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintlayout = itemView.findViewById(R.id.constraintlayout);
            flowTv = itemView.findViewById(R.id.flowTv);
            hopTv = itemView.findViewById(R.id.hopTv);
            customTv = itemView.findViewById(R.id.customTv);
        }
    }

    private void showCustomerBuyFlowDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.BottomSheetStyle);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.layout_customer_buy_flow, null);
        TextView backTv = (TextView) dialogView.findViewById(R.id.backTv);
        final EditText hopNumberEt = (EditText) dialogView.findViewById(R.id.hopNumberEt);
        final TextView flowNumberEt = (TextView) dialogView.findViewById(R.id.flowNumberTt);
        TextView submitOrderTv = (TextView) dialogView.findViewById(R.id.submitOrderTv);
        TextView exchangePriceTv = (TextView) dialogView.findViewById(R.id.exchangeTv);
        String price = String.format(Locale.CHINA, "1HOP=%.2fM", mBytesPerToken);
        exchangePriceTv.setText(price);
        flowNumberEt.setKeyListener(null);
        hopNumberEt.setHint(Utils.TokenNameFormat(mContext, R.string.recharge_hop_money));
        hopNumberEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = hopNumberEt.getText().toString();
                if (TextUtils.isEmpty(text)|| text.equals(".") ) {
                    return;
                }
                String flow = String.valueOf(Double.parseDouble(hopNumberEt.getText().toString()) * mBytesPerToken) + "M";
                flowNumberEt.setText(flow);
            }
        });

        submitOrderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (TextUtils.isEmpty(hopNumberEt.getText().toString())) {
                    return;
                }
                showPasswordDialog(Double.parseDouble(hopNumberEt.getText().toString()));
            }
        });

        backTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(dialogView);
        try {
            // hack bg color of the BottomSheetDialog
            ViewGroup parent = (ViewGroup) dialogView.getParent();
            parent.setBackgroundResource(android.R.color.transparent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();
    }

    private void showPasswordDialog(final double tokenNo) {
        if (mRechargeFlowState != null) {
            mRechargeFlowState.recharge(tokenNo);
        }

    }
}
