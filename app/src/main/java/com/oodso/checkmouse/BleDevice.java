package com.oodso.checkmouse;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.oodso.checkmouse.utils.HexUtils;

/**
 * Created by xulei on 2016/7/29.
 */
public class BleDevice {

    public BluetoothDevice device = null;
    protected Context context = null;
    Intent serviceIntent;
    protected BleService bleService = null;
    public RFStarBLEBroadcastReceiver delegate = null;

    public BleDevice(Context context, BluetoothDevice device) {
        this.device = device;
        this.context = context;
        this.registerReceiver();

        if (serviceIntent == null) {
            serviceIntent = new Intent(this.context, BleService.class);
            context.bindService(serviceIntent, serviceConnection, Service.BIND_AUTO_CREATE);
        }

    }

    /**
     * 设置连接，绑定服务
     */
    public void setBLEBroadcastDelegate(RFStarBLEBroadcastReceiver delegate) {
        this.delegate = delegate;
    }

    /**
     * 连接服务
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {


        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            bleService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            // Log.d(BLEApp.KTag, "55 serviceConnected :   服务启动 ");
            bleService = ((BleService.LocalBinder) service).getService();
            bleService.initBluetoothDevice(device);
        }
    };

    /**
     * 注册监视蓝牙设备（返回数据的）广播
     */
    public void registerReceiver() {
        context.registerReceiver(gattUpdateRecevice,
                this.bleIntentFilter());
    }

    /**
     * 注销监视蓝牙返回的广播
     */
    public void ungisterReceiver() {
        this.context.unregisterReceiver(gattUpdateRecevice);
    }

    /**
     * 监视广播的属性
     *
     * @return
     */
    protected IntentFilter bleIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BleService.ACTION_GAT_RSSI);
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTING);
        return intentFilter;
    }

    /**
     * 接收蓝牙广播
     */
    private BroadcastReceiver gattUpdateRecevice = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            // TODO Auto-generated method stub
            String characteristicUUID = intent
                    .getStringExtra(BleService.RFSTAR_CHARACTERISTIC_ID);
            if (BleService.ACTION_GATT_CONNECTED.equals(intent
                    .getAction())) {
                Toast.makeText(context, "蓝牙连接成功", Toast.LENGTH_SHORT).show();
            } else if (BleService.ACTION_GATT_DISCONNECTED.equals(intent
                    .getAction())) { // 断开
                Toast.makeText(context, "蓝牙连接断开", Toast.LENGTH_SHORT).show();
                // }
            } else if (BleService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(intent.getAction())) {
                Log.e("11", "BleDevice 发现服务");
                bleService.WriteData(0);
//                discoverCharacteristicsFromService();
            } else if (BleService.ACTION_DATA_AVAILABLE.equals(intent
                    .getAction())) {

                byte[] data = intent
                        .getByteArrayExtra(BleService.EXTRA_DATA);
                liveDateToDouble(data[8],data[9]);

                Log.e("121",intent
                        .getByteArrayExtra(BleService.EXTRA_DATA).toString());
                if (intent.getByteArrayExtra(BleService.EXTRA_DATA) == null) {
                    Toast.makeText(context, "ble设备无数据返回", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
            delegate.onReceive(context, intent, device.getAddress(),
                    characteristicUUID);
        }
    };

    public interface RFStarBLEBroadcastReceiver {
        /**
         * 监视蓝牙状态的广播 macData蓝牙地址的唯一识别码
         */
        public void onReceive(Context context, Intent intent, String macData,
                              String uuid);
    }

    // 判断是连接还是断开
    public boolean connectedOrDis(String action) {
        if (BleService.ACTION_GATT_CONNECTED.equals(action)) {
            System.out.println("BleDevice连接成功");
            bleService.WriteData(0);
            return true;

        } else if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
            System.out.println("BleDevice连接断开");
            return false;
        }
        return false;
    }

    /**
     * 获取特征值
     *
     * @param characteristic
     */
    public void readValue(BluetoothGattCharacteristic characteristic) {
        if (characteristic == null) {
            Log.w("11", "55555555555 readValue characteristic is null");
        } else {

            bleService.readValue(this.device, characteristic);

        }
    }

    /**
     * 根据特征值写入数据
     *
     * @param characteristic
     */
    public void writeValue(BluetoothGattCharacteristic characteristic) {
        if (characteristic == null) {
            Log.w("11", "55555555555 writeValue characteristic is null");
        } else {
            Log.d("11", "charaterUUID write is success  : "
                    + characteristic.getUuid().toString());
            bleService.writeValue(this.device, characteristic);
        }
    }

    public boolean DisConne() {
        bleService.Disconnect();
        return true;
    }


    public double liveDateToDouble(byte h, byte l) {
        double hl = 0;
        double tempL = 0;
        String strL = toHexString1(l);
        if (strL.equalsIgnoreCase("ff")) {
            strL = "0";
        }
        String strH = toHexString1(h);
        if (strH.equalsIgnoreCase("ff")) {
            strH = "0";
        }
        if (l != 0) {
            tempL = (double) HexUtils.HexToInt(strL) / 100;
        }
        if (h != 0) {       //高位等于0 不理会
            double tempH = (double) HexUtils.HexToInt(strH) / 100;
            hl = tempH + tempL;
        } else {
            hl = tempL;
        }
        return hl;
    }
    //打印的数字
    public static String toHexString1(byte b) {
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1) {
            return "0" + s;
        } else {
            return s;
        }
    }

}
