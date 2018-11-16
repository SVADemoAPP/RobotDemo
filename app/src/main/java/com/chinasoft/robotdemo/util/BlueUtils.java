package com.chinasoft.robotdemo.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.chinasoft.robotdemo.model.BleAdvertisedData;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlueUtils {
    //蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    //搜索状态的标示
    private boolean mScanning = true;
    //蓝牙适配器List
    private List<BluetoothDevice> mBlueList;


    private FindBlue findBlue;
    //上下文
    private Context context;
    //单例模式
    private static BlueUtils blueUtils;
    public interface FindBlue{
       void getBlues(BluetoothDevice bluetoothDevice);
    }

    //蓝牙的回调地址
    private BluetoothAdapter.LeScanCallback mLesanCall;
    //扫描执行回调
   // private BlueUddtils.Callback callback;

    //单例模式
    public static BlueUtils getBlueUtils(){
        if(blueUtils == null){
            blueUtils = new BlueUtils();
        }
        return blueUtils;
    }

    /***
     * 初始化蓝牙的一些信息
     */
    public void getInitialization(Context context){
        this.context = context;
        //初始化蓝牙适配器
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        //初始化蓝牙
        mBluetoothAdapter = bluetoothManager.getAdapter();

        mBluetoothAdapter.enable();
        //初始化List
        mBlueList = new ArrayList<>();
        //实例化蓝牙回调
        mLesanCall = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
                Log.e("bb","地方" + bluetoothDevice.getName());
                //返回三个对象 分类别是 蓝牙对象 蓝牙信号强度 以及蓝牙的广播包
                if(!mBlueList.contains(bluetoothDevice)){
                    String deviceName = bluetoothDevice.getName();
                    if(TextUtils.isEmpty(deviceName)){
                        return;
                    }

                    final BleAdvertisedData badata = parseAdertisedData(bytes);

                    if( deviceName == null ){
                        deviceName = badata.getName();
                    }
                    Log.e("aa","地方" +deviceName);
                    mBlueList.add(bluetoothDevice);
                    if(bluetoothDevice.getName().contains("SlamWare")){
                        findBlue.getBlues(bluetoothDevice);
                    }

                }
            }
        };
    }

    public void setFindBlue(FindBlue findBlue) {
        this.findBlue = findBlue;
    }

    /**
     * 开启蓝牙
     */
    public void startBlue(){
        if(mScanning){
            mScanning = false;
            //开始扫描并设置回调

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                //mBluetoothAdapter.startLeScan(new UUID[]{UUID.fromString("00001848-0000-1000-8000-00805f9b34fb")},mLesanCall);
                mBluetoothAdapter.startLeScan(mLesanCall);
            } else {
                //mLEScanner.stopScan(mLesanCall);

            }
        }
    }

    /**
     * 停止蓝牙扫描
     */
    public void stopBlue(){
        if(!mScanning){
            //结束蓝牙扫描
            mBluetoothAdapter.stopLeScan(mLesanCall);
        }
    }

    /**
     * 接口回调
     */
    public interface Callbacks{
        void CallbackList(List<BluetoothDevice> mBlueLis);
    }

    /**
     * 设置接口回调
     * @param callback 自身
     */
//    public void setCallback(Callbacks callback) {
//        this.callback = callback;
//    }

    /**
     * 判断是否支持蓝牙
     * @return
     */
    public boolean isSupportBlue(){
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 返回蓝牙对象
     * @return
     */
    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public static BleAdvertisedData parseAdertisedData(byte[] advertisedData) {
        List<UUID> uuids = new ArrayList<UUID>();
        String name = null;
        if( advertisedData == null ){
            return new BleAdvertisedData(uuids, name);
        }

        ByteBuffer buffer = ByteBuffer.wrap(advertisedData).order(ByteOrder.LITTLE_ENDIAN);
        while (buffer.remaining() > 2) {
            byte length = buffer.get();
            if (length == 0) break;

            byte type = buffer.get();
            switch (type) {
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                    while (length >= 2) {
                        uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
                        length -= 2;
                    }
                    break;
                case 0x06: // Partial list of 128-bit UUIDs
                case 0x07: // Complete list of 128-bit UUIDs
                    while (length >= 16) {
                        long lsb = buffer.getLong();
                        long msb = buffer.getLong();
                        uuids.add(new UUID(msb, lsb));
                        length -= 16;
                    }
                    break;
                case 0x09:
                    byte[] nameBytes = new byte[length-1];
                    buffer.get(nameBytes);
                    try {
                        name = new String(nameBytes, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    buffer.position(buffer.position() + length - 1);
                    break;
            }
        }
        return new BleAdvertisedData(uuids, name);
    }
}
