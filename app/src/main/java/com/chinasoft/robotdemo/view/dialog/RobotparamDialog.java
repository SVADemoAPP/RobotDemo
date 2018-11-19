package com.chinasoft.robotdemo.view.dialog;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinasoft.robotdemo.R;
import com.chinasoft.robotdemo.framwork.utils.CommonTools;

public class RobotparamDialog extends Dialog implements OnClickListener {
    ObjectAnimator animReturn1;
    ObjectAnimator animReturn2;
    ObjectAnimator animStart1;
    ObjectAnimator animStart2;
    private EditText[] ets;
    private int[] idsRl = new int[]{R.id.rl1, R.id.rl2,R.id.rl3};
    private RelativeLayout[] ivs;
    private Context mContext;
    private int nowEdit = 0;
    private OnRobotparamListener onRobotparamListener;
    private RelativeLayout[] rls;
    private TextView tv_confirm,tv_cancel;

    public interface OnRobotparamListener {
        void paramsComplete(String ip, int port,String userId);
    }

    public void setData(String ip, int port,String userId) {
        ets[0].setText(ip);
        ets[1].setText(port + "");
        ets[2].setText(userId);
        ets[0].setSelection(ets[0].getText().length());
        ets[1].setSelection(ets[1].getText().length());
        ets[2].setSelection(ets[2].getText().length());
    }

    public void setOnRobotparamListener(OnRobotparamListener onRobotparamListener) {
        this.onRobotparamListener = onRobotparamListener;
    }

    public RobotparamDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_robotparam);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_confirm.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        rls = new RelativeLayout[idsRl.length];
        ets = new EditText[rls.length];
        ivs = new RelativeLayout[rls.length];
        for (int i = 0; i < rls.length; i++) {
            rls[i] = (RelativeLayout) findViewById(idsRl[i]);
            ets[i] = (EditText) rls[i].getChildAt(2);
            ivs[i] = (RelativeLayout) rls[i].getChildAt(3);
            final int finalI = i;
            ivs[i].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ets[finalI].setText("");
                }
            });
            ets[i].setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        startEdit(finalI);
                        ets[finalI].setSelection(ets[finalI].getText().length());
                        return;
                    }
                    exitEdit(finalI);
                }
            });
            ets[i].addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (TextUtils.isEmpty(s) || nowEdit != finalI) {
                        ivs[finalI].setVisibility(View.INVISIBLE);
                    } else {
                        ivs[finalI].setVisibility(View.VISIBLE);
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void afterTextChanged(Editable s) {
                }
            });
            rls[i].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ets[finalI].requestFocus();
                }
            });
        }
        initAnimation();
        ets[0].requestFocus();
        nowEdit = 0;
    }

    private void initAnimation() {
        animStart1 = ObjectAnimator.ofFloat(null, "translationY", new float[]{0.0f, (float) (-CommonTools.dp2px(mContext, 20.0f))});
        animStart1.setDuration(500);
        animStart2 = ObjectAnimator.ofFloat(null, "translationX", new float[]{0.0f, (float) (-CommonTools.dp2px(mContext, 108.33f))});
        animStart2.setDuration(500);
        animReturn1 = ObjectAnimator.ofFloat(null, "translationY", new float[]{(float) (-CommonTools.dp2px(mContext, 20.0f)), 0.0f});
        animReturn1.setDuration(500);
        animReturn2 = ObjectAnimator.ofFloat(null, "translationX", new float[]{(float) (-CommonTools.dp2px(mContext, 108.33f)), 0.0f});
        animReturn2.setDuration(500);
    }

    private void startEdit(int index) {
        if (!TextUtils.isEmpty(ets[index].getText())) {
            ivs[index].setVisibility(View.VISIBLE);
        }
        nowEdit = index;
        rls[index].getChildAt(0).setBackgroundResource(R.mipmap.feature_blue_stroken);
        animStart1.setTarget(rls[index].getChildAt(1));
        animStart1.start();
        ((TextView) rls[index].getChildAt(1)).setTextColor(mContext.getResources().getColor(R.color.blue));
        animStart2.setTarget(ets[index]);
        animStart2.start();
    }

    private void exitEdit(int index) {
        ivs[index].setVisibility(View.GONE);
        rls[index].getChildAt(0).setBackgroundResource(R.mipmap.feature_gray_stroken);
        animReturn1.setTarget(rls[index].getChildAt(1));
        animReturn1.start();
        ((TextView) rls[index].getChildAt(1)).setTextColor(mContext.getResources().getColor(R.color.black_text));
        animReturn2.setTarget(ets[index]);
        animReturn2.start();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                for (EditText text : ets) {
                    if (TextUtils.isEmpty(text.getText().toString())) {
                        Toast.makeText(mContext, R.string.nullWarm, Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                onRobotparamListener.paramsComplete(ets[0].getText().toString(), Integer.parseInt(ets[1].getText().toString()),ets[2].getText().toString());
                dismiss();
                return;
            case R.id.tv_cancel:
                dismiss();
                return;
            default:
                return;
        }
    }
}
