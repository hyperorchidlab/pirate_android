package com.hop.pirate.adapter;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hop.pirate.Constants;
import com.hop.pirate.R;
import com.hop.pirate.base.BaseActivity;
import com.hop.pirate.model.bean.ExtendToken;
import com.hop.pirate.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class TokenAdapter extends RecyclerView.Adapter<TokenAdapter.ViewHolder> {

    private TokenAdapterItemClick mTokenAdapterItemClick;

    public void setTokenBeans(List<ExtendToken> tokenBeans) {
        mTokenBeans = tokenBeans;
        notifyDataSetChanged();
    }

    private List<ExtendToken> mTokenBeans;
    private BaseActivity mContext;

    public interface TokenAdapterItemClick {
        void itemClick(ExtendToken extendToken);
    }

    public TokenAdapter(BaseActivity context, TokenAdapterItemClick tokenAdapterItemClick) {
        mContext = context;
        mTokenAdapterItemClick = tokenAdapterItemClick;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        final int currentPosition = position;
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_token, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        final ExtendToken tokenBean = mTokenBeans.get(position);
        String curSymbol = Utils.getString(Constants.CUR_TOKENI, ExtendToken.CurTokenI);
        if (tokenBean.getTokenI().equals(curSymbol)) {
            tokenBean.setChecked(true);
        } else {
            tokenBean.setChecked(false);
        }
        viewHolder.tokenNameTv.setText(tokenBean.getSymbol());
        viewHolder.balanceTv.setText(Utils.ConvertCoin(tokenBean.getBalance()));
        viewHolder.statusIv.setVisibility(tokenBean.isChecked() ? View.VISIBLE : View.INVISIBLE);
        viewHolder.constraintlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tokenBean.isChecked()) {
                    return;
                }
                mTokenAdapterItemClick.itemClick(tokenBean);

            }
        });


    }

    @Override
    public int getItemCount() {
        return mTokenBeans == null ? 0 : mTokenBeans.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout constraintlayout;
        private TextView tokenNameTv;
        private TextView balanceTv;
        private ImageView statusIv;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintlayout = itemView.findViewById(R.id.constraintlayout);
            tokenNameTv = itemView.findViewById(R.id.tokenNameTv);
            balanceTv = itemView.findViewById(R.id.balanceTv);
            statusIv = itemView.findViewById(R.id.statusIv);

        }
    }
}
