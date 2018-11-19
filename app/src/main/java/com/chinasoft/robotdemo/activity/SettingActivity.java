package com.chinasoft.robotdemo.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinasoft.robotdemo.R;
import com.chinasoft.robotdemo.framwork.activity.BaseActivity;
import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;
import com.chinasoft.robotdemo.util.Constant;

/**
 * Created by chinasoft_gyr on 2018/11/12.
 */

public class SettingActivity extends BaseActivity {

private TextView tv_exit,tv_reset,tv_save;
    private TextView tv_http;
    private TextView tv_https;

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_setting);
    }

    @Override
    public void dealLogicBeforeInitView() {

    }

    @Override
    public void initView() {
        tv_exit=findViewById(R.id.tv_exit);
        tv_reset=findViewById(R.id.tv_reset);
        tv_save=findViewById(R.id.tv_save);
        tv_http = findViewById(R.id.tv_http);
        tv_https = findViewById(R.id.tv_https);
        tv_exit.setOnClickListener(this);
        tv_reset.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        tv_http.setOnClickListener(this);
        tv_https.setOnClickListener(this);
    }

    @Override
    public void dealLogicAfterInitView() {
    }

    @Override
    public void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.tv_exit:
                finish();
                break;
            case R.id.tv_reset:
                break;
            case R.id.tv_save:
                break;
            case R.id.tv_http:
                break;
            case R.id.tv_https:
                break;
            default:
                break;
        }
    }
}
