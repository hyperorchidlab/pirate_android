package com.hop.pirate.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hop.pirate.R;
import com.hop.pirate.model.bean.TransferRecordBean;
import com.hop.pirate.util.TimeUtil;

import java.util.List;

public class TransferRecordAdapter extends RecyclerView.Adapter<TransferRecordAdapter.ViewHolder> {


    private List<TransferRecordBean> transferRecordBeans;
    private Context mContext;

    public TransferRecordAdapter(Context context) {
        mContext = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_transfer_record, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        TransferRecordBean transferRecordBean = transferRecordBeans.get(i);
        viewHolder.transferCountTv.setText(transferRecordBean.getHopCount()+" "+mContext.getString(R.string.wallet_flow_unit_hop));
        viewHolder.transferTime.setText(TimeUtil.forTime(transferRecordBean.getTime()));
        viewHolder.transferPerson.setText(transferRecordBean.getTransfrom()+mContext.getString(R.string.hop_transfer_to_me));
    }

    @Override
    public int getItemCount() {
        return transferRecordBeans == null ? 0 :transferRecordBeans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView transferCountTv;
        private TextView transferTime;
        private TextView transferPerson;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            transferCountTv=itemView.findViewById(R.id.transferCountTv);
            transferTime=itemView.findViewById(R.id.transferTime);
            transferPerson=itemView.findViewById(R.id.transferPerson);
        }
    }

    public void setTransferRecordBeans(List<TransferRecordBean> transferRecordBeans) {
        this.transferRecordBeans = transferRecordBeans;
        notifyDataSetChanged();
    }
}
