package com.itrus.ikey.safecenter.TOPMFA.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View.OnClickListener;


public interface IBase extends OnClickListener{
	
	/**
	 * 布局 id
	 * @return
	 */
	public int getLayoutId();
	
	/**
	 * 初始化所有的View
	 * @param inflater
	 * @return
	 */
	void initView();

	/**
	 * 初始化所有的数据 
	 * @param savedInstanceState
	 */
	public void initData(Bundle savedInstanceState);

	
	/**
	 * 要接收的action
	 * @return
	 */
	public String[] filterActions();

	/**
	 * 收到广播后
	 * @param context
	 * @param intent
	 */
	public void onReceive(Context context, Intent intent);

	/**
	 * 注册广播
	 */
	public void register();

	/**
	 * 取消广播
	 */
	public void unRegister();
	
	/**
	 * 是否登录
	 * @return
	 */
	public boolean isLogin();
	
	/**
	 * 设置登录状态
	 */
	public void setIsLogin(boolean isLogin);
	
	
	public void jump2Activity(Class<?> _class);
	
	public void jump2Activity(Class<?> _class, Bundle extra);

	public void jump2Activity(Class<?> _class, Bundle extra, int requestCode);
	
}
