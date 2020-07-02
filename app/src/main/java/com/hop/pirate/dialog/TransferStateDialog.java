package com.hop.pirate.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hop.pirate.R;

public class TransferStateDialog extends Dialog {

    Context mContext;
    private ImageView mTransferStateIv;
    private TextView mTransferStateTitleTv;
    private TextView mTransferStateDesTv;
    private TextView mSureTv;


    private boolean transferSuccess;

    public TransferStateDialog(Context context, boolean transferSuccess) {
        super(context, R.style.payPasswordDialog);
        this.mContext = context;
        this.transferSuccess = transferSuccess;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_transfer_state);
        mTransferStateIv = findViewById(R.id.transferStateIv);
        mTransferStateTitleTv = findViewById(R.id.transferStateTitleTv);
        mTransferStateDesTv = findViewById(R.id.transferStateDesTv);
        mSureTv = findViewById(R.id.sureTv);

        if (transferSuccess) {
            mTransferStateIv.setBackgroundResource(R.drawable.transfer_success);
            mTransferStateTitleTv.setText(mContext.getString(R.string.transfer_success));
        } else {
            mTransferStateIv.setBackgroundResource(R.drawable.unbind);
            mTransferStateTitleTv.setText(mContext.getString(R.string.transfer_fail));
            mTransferStateDesTv.setText(mContext.getString(R.string.transfer_check_address));
        }
        mSureTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }


}
