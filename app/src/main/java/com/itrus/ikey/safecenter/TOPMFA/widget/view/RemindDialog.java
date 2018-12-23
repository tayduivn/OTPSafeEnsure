package com.itrus.ikey.safecenter.TOPMFA.widget.view;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itrus.ikey.safecenter.TOPMFA.R;


public class RemindDialog extends Dialog implements View.OnClickListener {

	private Context context;
	private TextView tvTitle;
	private View topLine;
	private LinearLayout layoutContainer;
	private TextView tvContainer;

	public TextView getTvTitle() {
		return tvTitle;
	}

	public TextView getTvContainer() {
		return tvContainer;
	}

	private Button btnSingle;
	private LinearLayout layoutBtnlayout;
	private Button btnCancel;
	private Button btnOk;
	private CheckBox cbShow;
	private boolean isCbShow = false;

	public boolean isCbShow() {
		return isCbShow;
	}

	public void setCbShow(boolean isCbShow) {
		this.isCbShow = isCbShow;
		if (cbShow != null) {
			cbShow.setVisibility(isCbShow() ? View.VISIBLE : View.GONE);
		}
	}

	public RemindDialog(Context context) {
		super(context, R.style.CustomDialog);
		this.context = context;
		setContentView(R.layout.remin_dialog);
		initView();
		initData();
	}

	private void initView() {
		tvTitle = (TextView) findViewById(R.id.tv_dialog_title);
		topLine = findViewById(R.id.line_top);
		layoutContainer = (LinearLayout) findViewById(R.id.layout_dialog_container);
		cbShow = (CheckBox) layoutContainer.findViewById(R.id.cb_show_again);
		cbShow.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (onCheckedListener != null) {
					onCheckedListener.OnChecked(isChecked);
				}
			}
		});

		tvContainer = (TextView) findViewById(R.id.tv_dialog_container);
		tvContainer.setMovementMethod(ScrollingMovementMethod.getInstance());
		btnSingle = (Button) findViewById(R.id.btn_dialog_single);
		btnSingle.setOnClickListener(this);
		layoutBtnlayout = (LinearLayout) findViewById(R.id.layout_dialog_btn_layout);
		btnCancel = (Button) findViewById(R.id.btn_dialog_cancel);
		btnCancel.setOnClickListener(this);
		btnOk = (Button) findViewById(R.id.btn_dialog_ok);
		btnOk.setOnClickListener(this);

		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);

		lp.x = 0;
		lp.y = 0;
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialogWindow.setAttributes(lp);
	}

	private boolean btnLock = false;
	private boolean containerLock = false;

	private void initData() {

	}

	public void setOkBtnColor(int color) {
		this.btnOk.setTextColor(color);
	}

	public void setTitle(CharSequence title) {
		if (title == null)
			return;
		tvTitle.setText(title);
	}

	public void setText(CharSequence text) {
		if (text == null || containerLock)
			return;
		tvContainer.setText(text);
		containerLock = true;
	}

	public void setView(View view) {
		if (view == null || containerLock)
			return;
		tvContainer.setVisibility(View.GONE);
		layoutContainer.addView(view);
		containerLock = true;
	}

	private View.OnClickListener cancelListener;
	private View.OnClickListener okListener;

	public void setNotitle(boolean noTitle) {
		tvTitle.setVisibility(noTitle ? View.GONE : View.VISIBLE);
		topLine.setVisibility(noTitle ? View.GONE : View.VISIBLE);
	}

	public void setSelect(CharSequence cancel, View.OnClickListener cancelListener, CharSequence ok, View.OnClickListener okListener) {
		if (btnLock)
			return;
		btnSingle.setVisibility(View.GONE);
		layoutBtnlayout.setVisibility(View.VISIBLE);
		if (cancel == null || cancelListener == null)
			return;
		btnCancel.setText(cancel);
		this.cancelListener = cancelListener;
		if (ok == null || okListener == null)
			return;
		btnOk.setText(ok);
		this.okListener = okListener;
		btnLock = true;
	}

	private View.OnClickListener listener;

	public void setButton(CharSequence text, View.OnClickListener listener) {
		if (btnLock)
			return;
		btnSingle.setVisibility(View.VISIBLE);
		layoutBtnlayout.setVisibility(View.GONE);
		if (text == null || listener == null)
			return;
		btnSingle.setText(text);
		this.listener = listener;
		btnLock = true;
	}

	private boolean okDismiss = true;
	private boolean singleDismiss = true;
	private boolean cancelDismiss = true;

	public void setSingleButtonTextColor(int color) {
		btnSingle.setTextColor(context.getResources().getColor(color));
	}

	public boolean isSingleDismiss() {
		return singleDismiss;
	}

	public void setSingleDismiss(boolean singleDismiss) {
		this.singleDismiss = singleDismiss;
	}

	public boolean isOkDismiss() {
		return okDismiss;
	}

	public void setOkDismiss(boolean okDismiss) {
		this.okDismiss = okDismiss;
	}

	public boolean isCancelDismiss() {
		return cancelDismiss;
	}

	public void setCancelDismiss(boolean cancelDismiss) {
		this.cancelDismiss = cancelDismiss;
	}

	private OnCheckedListener onCheckedListener = null;

	public void setOnCheckListener(OnCheckedListener listener) {
		onCheckedListener = listener;
	}

	public interface OnCheckedListener {
		public void OnChecked(boolean isChecked);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_dialog_cancel:
			if (this.cancelListener == null)
				return;
			if (cancelDismiss) {
				this.dismiss();
			}
			this.cancelListener.onClick(v);
			break;
		case R.id.btn_dialog_ok:
			if (this.okListener == null)
				return;
			if (okDismiss) {
				this.dismiss();
			}
			this.okListener.onClick(v);
			break;
		case R.id.btn_dialog_single:
			if (this.listener == null)
				return;
			if (singleDismiss) {
				this.dismiss();
			}
			this.listener.onClick(v);
			break;
		}
	}

}
