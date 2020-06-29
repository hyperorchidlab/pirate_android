package com.hop.pirate.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.hop.pirate.R;

public class PayPasswordDialog extends Dialog {

	Context mContext;
	private EditText mPasswordEt;
	private PasswordCallBack mRechargeFlowCallBack;
	public interface PasswordCallBack{
		void callBack(String password);
	}

	public PayPasswordDialog(Context context,PasswordCallBack rechargeFlowCallBack) {
		super(context, R.style.payPasswordDialog);
		this.mContext=context;
		this.mRechargeFlowCallBack=rechargeFlowCallBack;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_pay_password);
		mPasswordEt = findViewById(R.id.keyWordView);
		findViewById(R.id.sureTv).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mRechargeFlowCallBack.callBack(mPasswordEt.getText().toString().trim());
				dismiss();
			}
		});

	}

	@Override
	public void show() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		super.show();
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.gravity= Gravity.BOTTOM;
		layoutParams.width= WindowManager.LayoutParams.MATCH_PARENT;
		layoutParams.height= WindowManager.LayoutParams.WRAP_CONTENT;

		getWindow().getDecorView().setPadding(0, 0, 0, 0);

		getWindow().setAttributes(layoutParams);
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run() {
				mPasswordEt.setFocusable(true);
				mPasswordEt.setFocusableInTouchMode(true);
				mPasswordEt.requestFocus();
				InputMethodManager inputManager = (InputMethodManager)     mPasswordEt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(mPasswordEt, 0);
			}
		}, 200);

	}

}
