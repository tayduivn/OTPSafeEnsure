package com.itrus.ikey.safecenter.TOPMFA.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.itrus.ikey.safecenter.TOPMFA.base.MyApplication;
import com.itrus.ikey.safecenter.TOPMFA.utils.LocalBroadcastUtil;


/**
 * Created by STAR on 2016/8/3.
 */
public abstract class BaseFragment extends Fragment {
    private MyApplication mApp;
    protected Activity ctx;
    private boolean isCache = false;
    protected View rootView;
    protected Context mContext;
    private boolean isDestroyed = false;
    private IntentFilter filter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        filter = new IntentFilter();
        wrapLocalBroadcastFilter(filter);
        registLocalBroadCast();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);//启用onCreateOptionMenu
        isCache = true;
        return super.onCreateView(inflater, container, savedInstanceState);

    }


    //	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
//		LogMe.d("fragment", this.getClass().getName() + "===========onCreateView");
//		injected = true;
//		setHasOptionsMenu(true);//启用onCreateOptionMenu
//		View v =  x.view().inject(this, inflater, container);
//		//return super.onCreateView(inflater,container,savedInstanceState);
//		return v;
//	}
//@Override
//public void onViewCreated(View view, Bundle savedInstanceState) {
//	super.onViewCreated(view, savedInstanceState);
//	LogMe.d("fragment", this.getClass().getName() + "===========onViewCreated");
//	if (!injected) {
//		x.view().inject(this, this.getView());
//	}
//}
//
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx = getActivity();
        mApp = (MyApplication) ctx.getApplication();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

//   @Override
//   public void setUserVisibleHint(boolean isVisibleToUser) {
//       super.setUserVisibleHint(isVisibleToUser);
//       if (isVisibleToUser) {
////   	 setUIStatus();
//       } else {
//           //相当于Fragment的onPause
//       }
//   }


    public boolean isCache() {
        return isCache;
    }

    public MyApplication getApp() {
        return mApp;
    }


    protected void showMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    protected void wrapLocalBroadcastFilter(IntentFilter filter) {
    }


    protected void dealLocalBroadcast(Context context, Intent intent) {
    }


    protected void registLocalBroadCast() {
        if (null != m_receiver && null != filter) {
            LocalBroadcastUtil.registerLocalBroadCast(m_receiver, filter);
        }
    }

    protected void unregisterLocalBroadCast() {
        if (null != m_receiver) {
            LocalBroadcastUtil.unregisterLocalBroadCast(m_receiver);
        }
    }

    private final BroadcastReceiver m_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isDestroy()) {
                return;
            }
            dealLocalBroadcast(context, intent);
        }
    };

    public boolean isDestroy() {
        return isDestroyed;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
        unregisterLocalBroadCast();
    }
}
