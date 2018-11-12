package com.chinasoft.robotdemo.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.chinasoft.robotdemo.R;
import com.chinasoft.robotdemo.framwork.activity.BaseActivity;
import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;
import com.chinasoft.robotdemo.util.Constant;

/**
 * Created by chinasoft_gyr on 2018/11/12.
 */

public class SettingActivity extends BaseActivity {

    private ImageView iv_exit;
    private Button btn_save;
    private EditText et_robotIp,et_robotPort,et_sdPath;

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_setting);
    }

    @Override
    public void dealLogicBeforeInitView() {

    }

    @Override
    public void initView() {
        et_robotIp=findViewById(R.id.et_robotIp);
        et_robotPort=findViewById(R.id.et_robotPort);
        et_sdPath=findViewById(R.id.et_sdPath);
        iv_exit = findViewById(R.id.iv_exit);
        btn_save = findViewById(R.id.btn_save);
        iv_exit.setOnClickListener(this);
        btn_save.setOnClickListener(this);
    }

    @Override
    public void dealLogicAfterInitView() {
        et_robotIp.setText(Constant.robotIp);
        et_robotPort.setText(Constant.robotPort+"");
        et_sdPath.setText(Constant.sdPath);
    }

    @Override
    public void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.iv_exit:
                finish();
                break;
            case R.id.btn_save:
                Constant.robotIp=et_robotIp.getText().toString();
                Constant.robotPort=Integer.parseInt(et_robotPort.getText().toString());
                Constant.sdPath=et_sdPath.getText().toString();
                SharedPrefHelper.putString(this,"robotIp",Constant.robotIp);
                SharedPrefHelper.putInt(this,"robotPort",Constant.robotPort);
                SharedPrefHelper.putString(this,"sdPath",Constant.sdPath);
                setResult(-1);
                finish();
                break;
            default:
                break;
        }
    }
}
