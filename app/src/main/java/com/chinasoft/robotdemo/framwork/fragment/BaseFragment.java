package com.chinasoft.robotdemo.framwork.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class BaseFragment extends Fragment implements OnClickListener{

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dealLogicBeforeInitView();
        initView();
        dealLogicAfterInitView();
    }

    /**
     * 初始化VIEW，在onCreate()生命周期中回调
     */
    public abstract void initView();

    /**
     * 在实例化布局之前处理的逻辑
     */
    public abstract void dealLogicBeforeInitView();

    /**
     * 在实例化布局之后处理的逻辑
     */
    public abstract void dealLogicAfterInitView();

    /**
     * onClick方法的封装，在此方法中处理点击事件
     *
     * @param view
     */
    abstract public void onClickEvent(View view);

    @Override
    public void onClick(View v) {
        onClickEvent(v);
    }

    /**
     * 通过类名启动Activity
     *
     * @param pClass
     */
    protected void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

    /**
     * 通过类名启动Activity，并且含有Bundle数据
     *
     * @param pClass
     * @param pBundle
     */
    protected void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this.getActivity(), pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        this.getActivity().startActivity(intent);
    }
}
