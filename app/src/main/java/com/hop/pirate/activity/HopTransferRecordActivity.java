package com.hop.pirate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hop.pirate.R;
import com.hop.pirate.adapter.TransferRecordAdapter;
import com.hop.pirate.base.BaseActivity;
import com.hop.pirate.model.bean.TransferRecordBean;
import com.hop.pirate.util.WrapContentLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class HopTransferRecordActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView mTransferRecordRecyclerView;
    private TransferRecordAdapter mTransferRecordAdapter;
    private List<TransferRecordBean> transferRecordBeans;
    private TextView mTurnOutTv;
    private TextView mTransferTv;
    private ImageView mBackIv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hop_transfer_record);
        initViews();
        initData();
    }


    @Override
    public void initViews() {
        mTransferRecordRecyclerView = findViewById(R.id.transferRecordRecyclerView);
        mTurnOutTv = findViewById(R.id.turnOutTv);
        mTransferTv = findViewById(R.id.transferTv);
        mBackIv = findViewById(R.id.backIv);
        ((TextView) findViewById(R.id.titleTv)).setText(getResources().getString(R.string.wallet_flow_unit_hop));

        mTurnOutTv.setOnClickListener(this);
        mTransferTv.setOnClickListener(this);
        mBackIv.setOnClickListener(this);

        mTransferRecordRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mTransferRecordAdapter = new TransferRecordAdapter(this);
        mTransferRecordRecyclerView.setAdapter(mTransferRecordAdapter);

    }

    @Override
    public void initData() {
        transferRecordBeans = new ArrayList<>();
        transferRecordBeans.add(new TransferRecordBean(783.0451, "李先生", System.currentTimeMillis()));
        transferRecordBeans.add(new TransferRecordBean(621.231, "赵先生", System.currentTimeMillis() - 4000000));
        transferRecordBeans.add(new TransferRecordBean(744.1341, "王先生", System.currentTimeMillis() - 6000000));
        mTransferRecordAdapter.setTransferRecordBeans(transferRecordBeans);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.turnOutTv:
                startActivity(new Intent(this, TransferOutActivity.class));
                break;
            case R.id.transferTv:
                startActivity(new Intent(this, TransferInActivity.class));

                break;
            case R.id.backIv:
                finish();
                break;
            default:
                break;
        }
    }
}
