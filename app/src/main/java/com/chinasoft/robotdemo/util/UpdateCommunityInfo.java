package com.chinasoft.robotdemo.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

import com.chinasoft.robotdemo.framwork.sharef.SharedPrefHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class UpdateCommunityInfo {
    private static final String LTE_SIGNAL_STRENGTH = "getLteSignalStrength";
    private static final String LTE_TAG = "LTE_Tag";
    public String CI = "-";
    public String CellID = "-";
    public String MCC = "-";
    public String MNC = "-";
    public String PCI = "-";
    public String RSRP = "-";
    public String RSRQ = "-";
    public String SINR = "-";
    public String TAC = "-";
    private boolean bMatch;
    private boolean bRun;
    private List<CellInfo> cellInfoList = new ArrayList();
    private Context context;
    private long differentTime;
    public String eNodeBId = "-";
    public Handler handler;
    private CellLocation location;
    PhoneStateListener mListener = new PhoneStateListener() {
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            try {
                UpdateCommunityInfo.this.SINR = String.valueOf((Integer) signalStrength.getClass().getMethod("getLteRssnr", new Class[0]).invoke(signalStrength, new Object[0]));
                UpdateCommunityInfo.this.RSRP = String.valueOf((Integer) signalStrength.getClass().getMethod("getLteRsrp", new Class[0]).invoke(signalStrength, new Object[0]));
                UpdateCommunityInfo.this.RSRQ = String.valueOf((Integer) signalStrength.getClass().getMethod("getLteRsrq", new Class[0]).invoke(signalStrength, new Object[0]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    Runnable runnable = new Runnable() {
        public void run() {
            if (UpdateCommunityInfo.this.bRun) {
                UpdateCommunityInfo.this.getCommunityInfo();
                UpdateCommunityInfo.this.handler.sendEmptyMessage(0);
                UpdateCommunityInfo.this.handler.postDelayed(UpdateCommunityInfo.this.runnable, 500);
            }
        }
    };
    private String saveCommunityTag;
    private TelephonyManager telephonymanager;

    public UpdateCommunityInfo(Context context, TelephonyManager telephonymanager, Handler handler) {
        this.context = context;
        this.handler = handler;
        this.telephonymanager = telephonymanager;
        this.saveCommunityTag = SharedPrefHelper.getString(context, "communityTag", "");
        try {
            telephonymanager.listen(this.mListener, 256);
        } catch (Exception e) {
            LLog.getLog().e("小区信息错误：", e.toString());
        }
    }

    @SuppressLint("MissingPermission")
    public void getCommunityInfo() {
        String mccMnc;
        GsmCellLocation l1;
        try {
            this.cellInfoList.clear();
            this.cellInfoList = this.telephonymanager.getAllCellInfo();
            if (this.cellInfoList != null) {
                LLog.getLog().e("所有信号信息", this.cellInfoList.toString());
                if (this.cellInfoList.size() == 0) {
                    List<NeighboringCellInfo> cellInfoList1 = this.telephonymanager.getNeighboringCellInfo();
                    if (cellInfoList1 != null) {
                        LLog.getLog().e("弃用方法获取到的信号长度", new StringBuilder(String.valueOf(cellInfoList1.size())).toString());
                    }
                }
                CellInfo cellInfo = (CellInfo) this.cellInfoList.get(0);
                if (cellInfo == null || !(cellInfo instanceof CellInfoLte)) {
                    LLog.getLog().e("小区信息获取失败", "没有LTE信号");
                    try {
                        mccMnc = this.telephonymanager.getNetworkOperator();
                        if (mccMnc != null && mccMnc.length() >= 5) {
                            this.MCC = mccMnc.substring(0, 3);
                            this.MNC = mccMnc.substring(3, 5);
                        }
                        this.location = this.telephonymanager.getCellLocation();
                        if (this.location != null && (this.location instanceof GsmCellLocation)) {
                            l1 = (GsmCellLocation) this.location;
                            this.TAC = String.valueOf(l1.getLac());
                            this.CI = String.valueOf(l1.getCid());
                            this.eNodeBId = String.valueOf(Integer.valueOf(this.CI).intValue() / 256);
                            this.CellID = String.valueOf(Integer.valueOf(this.CI).intValue() - (Integer.valueOf(this.eNodeBId).intValue() * 256));
                            LLog.getLog().e("小区信息更新", "TAC:" + this.TAC + ", PCI:" + this.PCI + ", CI:" + this.CI + ", eNodeBId:" + this.eNodeBId + ", CellID:" + this.CellID + ", RSRP:" + this.RSRP + ", RSRQ:" + this.RSRQ + ", SINR:" + this.SINR + ", MNC:" + this.MNC + ", MCC:" + this.MCC);
                            compaireAndJudge();
                            return;
                        }
                        return;
                    } catch (Exception e1) {
                        LLog.getLog().e("小区信息获取失败2", e1.toString());
                        return;
                    }
                }
                CellIdentityLte cellIdentity = ((CellInfoLte) cellInfo).getCellIdentity();
                this.CI = String.valueOf(cellIdentity.getCi());
                this.PCI = String.valueOf(cellIdentity.getPci());
                this.TAC = String.valueOf(cellIdentity.getTac());
                this.eNodeBId = String.valueOf(Integer.valueOf(this.CI).intValue() / 256);
                this.CellID = String.valueOf(Integer.valueOf(this.CI).intValue() - (Integer.valueOf(this.eNodeBId).intValue() * 256));
                this.MCC = String.valueOf(cellIdentity.getMcc());
                this.MNC = String.valueOf(cellIdentity.getMnc());
                String[] split = ((CellInfoLte) cellInfo).getCellSignalStrength().toString().split(" ");
                this.RSRP = split[2].split("=")[1];
                this.RSRQ = split[3].split("=")[1];
                LLog.getLog().e("小区信息更新", "TAC:" + this.TAC + ", PCI:" + this.PCI + ", CI:" + this.CI + ", eNodeBId:" + this.eNodeBId + ", CellID:" + this.CellID + ", RSRP:" + this.RSRP + ", RSRQ:" + this.RSRQ + ", SINR:" + this.SINR + ", MNC:" + this.MNC + ", MCC:" + this.MCC);
                compaireAndJudge();
                return;
            }
            LLog.getLog().e("小区信息获取失败", "没有网络信号");
        } catch (Exception e) {
            LLog.getLog().e("获取小区信息失败", e.toString());
            try {
                mccMnc = this.telephonymanager.getNetworkOperator();
                if (mccMnc != null && mccMnc.length() >= 5) {
                    this.MCC = mccMnc.substring(0, 3);
                    this.MNC = mccMnc.substring(3, 5);
                }
                this.location = this.telephonymanager.getCellLocation();
                if (this.location != null && (this.location instanceof GsmCellLocation)) {
                    l1 = (GsmCellLocation) this.location;
                    this.TAC = String.valueOf(l1.getLac());
                    this.CI = String.valueOf(l1.getCid());
                    this.eNodeBId = String.valueOf(Integer.valueOf(this.CI).intValue() / 256);
                    this.CellID = String.valueOf(Integer.valueOf(this.CI).intValue() - (Integer.valueOf(this.eNodeBId).intValue() * 256));
                    LLog.getLog().e("小区信息更新", "TAC:" + this.TAC + ", PCI:" + this.PCI + ", CI:" + this.CI + ", eNodeBId:" + this.eNodeBId + ", CellID:" + this.CellID + ", RSRP:" + this.RSRP + ", RSRQ:" + this.RSRQ + ", SINR:" + this.SINR + ", MNC:" + this.MNC + ", MCC:" + this.MCC);
                    compaireAndJudge();
                }
            } catch (Exception e12) {
                LLog.getLog().e("小区信息获取失败2", e12.toString());
            }
        }
    }

    public void compaireAndJudge() {
        if (!this.saveCommunityTag.equals("")) {
            String tag = this.eNodeBId + "_" + this.CellID;
            String[] eachCom = this.saveCommunityTag.split(";");
            this.bMatch = false;
            for (String equals : eachCom) {
                if (equals.equals(tag)) {
                    this.bMatch = true;
                    break;
                }
            }
            if (!this.bMatch && System.currentTimeMillis() - this.differentTime > 20000) {
                this.differentTime = System.currentTimeMillis();
            }
        }
    }

    public void startUpdateData() {
        this.bRun = true;
        this.handler.postDelayed(this.runnable, 1000);
    }

    public void endUpdateData() {
        this.bRun = false;
        this.handler.removeCallbacks(this.runnable);
    }

    private void getLTEsignalStrength(SignalStrength sStrength) {
        try {
            for (Method mthd : SignalStrength.class.getMethods()) {
                if (mthd.getName().equals(LTE_SIGNAL_STRENGTH)) {
                    this.SINR = String.valueOf((Integer) mthd.invoke(sStrength, new Object[0]));
                    Log.i(LTE_TAG, "signalStrength = " + this.SINR);
                    return;
                }
            }
        } catch (Exception e) {
            LLog.getLog().e(LTE_TAG, "signalException: " + e.toString());
        }
    }
}
