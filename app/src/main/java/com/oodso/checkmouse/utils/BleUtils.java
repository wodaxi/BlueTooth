package com.oodso.checkmouse.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

/**
 * Created by xulei on 2016/6/30.
 */
public class BleUtils  {
    private Activity mAty;

    // 用于记录每个特征值
    private BleCallback bleCallback;
    public BluetoothAdapter mBluetoothAdapter;


    public BleUtils(Activity mActivity, BleCallback bleCallback) {

        this.mAty = mActivity;
        this.bleCallback = bleCallback;

        initBle(mActivity);
    }
    public void initBle(Activity activity) {
        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(activity, "不支持蓝牙BLE", Toast.LENGTH_SHORT).show();
            System.exit(0);
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) activity
                .getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (null == mBluetoothAdapter) {
            Toast.makeText(activity, "蓝牙初始化失败......", Toast.LENGTH_SHORT).show();
            System.exit(0);
        }
    }
}
