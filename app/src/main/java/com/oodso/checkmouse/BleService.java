package com.oodso.checkmouse;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by xulei on 2016/7/29.
 */
public class BleService extends Service {
    private final IBinder kBinder = new LocalBinder();
    private static ArrayList<BluetoothGatt> arrayGatts = new ArrayList<BluetoothGatt>(); // 存放BluetoothGatt的集合
    public final static String ACTION_GATT_CONNECTED = "com.oodso.checkmouse.BleService.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_CONNECTING = "com.oodso.checkmouse.BleService.ACTION_GATT_CONNECTING";
    public final static String ACTION_GATT_DISCONNECTED = "com.oodso.checkmouse.BleService.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.oodso.checkmouse.BleService.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.oodso.checkmouse.BleService.ACTION_DATA_AVAILABLE"; //数据可用的 有效的
    public final static String EXTRA_DATA = "com.oodso.checkmouse.BleService.EXTRA_DATA"; //额外的 数据
    public final static String ACTION_GAT_RSSI = "com.oodso.checkmouse.BleService.RSSI";
    public final static String RFSTAR_CHARACTERISTIC_ID = "com.oodso.checkmouse.BleService.characteristic";

    public static final UUID SERVIE_UUID = UUID
            .fromString("000018f0-0000-1000-8000-00805f9b34fb");//服务 UUID
    private static final UUID UUID_WRITE = UUID
            .fromString("00002af1-0000-1000-8000-00805f9b34fb");// 写的UUID
    private static final UUID UUID_READ = UUID
            .fromString("00002af0-0000-1000-8000-00805f9b34fb");//读取的UUID

    // 实时模式命令  读取当前 浓度值  返回 一条
    public static final byte[] CURRENT_DATA = {0x66, 0x77, 0x00, 0x01, 0x01,
            0x00, 0x00, 0x00, 0x00, 0x00};
    // 读取电池电量命令  返回命令只用解析最后一个字节即可
    public static final byte[] ELECTRIC_DATA = {0x66, 0x77, 0x00, 0x01, 0x04,
            0x00, 0x00, 0x00, 0x00, 0x00};

    private BluetoothGattCharacteristic writeCharacteristic1;
    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGatt gatt;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return kBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    /**
     * 初始化BLE 如果已经连接就不用再次连接
     *
     * @param device
     * @return
     */
    public boolean initBluetoothDevice(BluetoothDevice device) {

        gatt = this.getBluetoothGatt(device);
        if (gatt != null) {
            if (gatt.connect()) {
                // 已经连接上
                Log.d("11", "当前连接的设备 : "+gatt.getDevice().getName()
                        + gatt.getDevice().getAddress() + "  连接上  数量: "
                        + arrayGatts.size());
            } else {
                return false;
            }
            return true;
        }
        Log.d("11", "5555" + device.getName() + ": 蓝牙设备正准备连接");
        gatt = device.connectGatt(this, false, bleGattCallback);
        arrayGatts.add(gatt);
        return true;
    }

    // 从arrayGatts匹配出与device中address想同的BluetoothGatt
    private BluetoothGatt getBluetoothGatt(BluetoothDevice device) {
        BluetoothGatt gatt = null;
        for (BluetoothGatt tmpGatt : arrayGatts) {
            if (tmpGatt.getDevice().getAddress().equals(device.getAddress())) {
                gatt = tmpGatt;
            }
        }
        return gatt;
    }

    /**
     * 发送数据到广播
     *
     * @param action
     */
    private void broadcastUpdate(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**
     * 发送带蓝牙信息的到广播
     *
     * @param action
     * @param characteristic
     */
    private void broadcastUpdate(String action,
                                 BluetoothGattCharacteristic characteristic) {
        Intent intent = new Intent(action);

        // For all other profiles, writes the data formatted in HEX.
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            intent.putExtra(EXTRA_DATA, characteristic.getValue());
            intent.putExtra(RFSTAR_CHARACTERISTIC_ID, characteristic.getUuid()
                    .toString());
        }
        sendBroadcast(intent);
    }

    private final BluetoothGattCallback bleGattCallback = new BluetoothGattCallback() {


        /*
        连接的状发生变化 (non-Javadoc)
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            String action = null;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                action = ACTION_GATT_CONNECTED;
                gatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                action = ACTION_GATT_DISCONNECTED;
            }
            if (action != null && !action.equals("")) {
                broadcastUpdate(action);
            }
        }

        /*
         * 搜索device中的services (non-Javadoc)
         *
         * @see
         * android.bluetooth.BluetoothGattCallback#onServicesDiscovered(android
         * .bluetooth.BluetoothGatt, int)
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(SERVIE_UUID);
                if (service != null) {
                    readCharacteristic = service.getCharacteristic(UUID_READ);
                    writeCharacteristic1 = service.getCharacteristic(UUID_WRITE);
                }

                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
            }
        }

        /*
         * 读取特征值 (non-Javadoc)
         *
         * @see
         * android.bluetooth.BluetoothGattCallback#onCharacteristicRead(android
         * .bluetooth.BluetoothGatt,
         * android.bluetooth.BluetoothGattCharacteristic, int)
         */
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         android.bluetooth.BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            } else {
            }
        }

        /*
         * 特征值的变化 (non-Javadoc)
         *
         * @see
         * android.bluetooth.BluetoothGattCallback#onCharacteristicChanged(android
         * .bluetooth.BluetoothGatt,
         * android.bluetooth.BluetoothGattCharacteristic)
         */
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            android.bluetooth.BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        /*
         * 读取信号 (non-Javadoc)
         *
         * @see
         * android.bluetooth.BluetoothGattCallback#onReadRemoteRssi(android.
         * bluetooth.BluetoothGatt, int, int)
         */
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (gatt.connect()) {
                broadcastUpdate(ACTION_GAT_RSSI);
            }
        }
    };

    public void WriteData(int type) {
        switch (type) {
            case 0:
                //实时模式
                if (gatt != null && writeCharacteristic1 != null) {
                    writeCharacteristic1.setValue(CURRENT_DATA);
                    gatt.writeCharacteristic(writeCharacteristic1);
                    Log.e("命令：--", CURRENT_DATA.toString());
                }
                break;
            case 1:
                if (gatt != null && writeCharacteristic1 != null) {
                    writeCharacteristic1.setValue(ELECTRIC_DATA);
                    gatt.writeCharacteristic(writeCharacteristic1);
                }
                break;
        }

    }

    public void writeValue(BluetoothDevice device,
                           BluetoothGattCharacteristic characteristic) {
        // TODO Auto-generated method stub
        BluetoothGatt gatt = this.getBluetoothGatt(device);
        if (gatt == null) {
            Log.w("11", "kBluetoothGatt 为没有初始化，所以不能写入数据");
            return;
        }
        gatt.writeCharacteristic(characteristic);
        Log.d("11", "55 connect :  连接上  数量： " + arrayGatts.size());
    }

    public void readValue(BluetoothDevice device,
                          BluetoothGattCharacteristic characteristic) {
        // TODO Auto-generated method stub
        BluetoothGatt gatt = this.getBluetoothGatt(device);
        if (gatt == null) {
            Log.w("", "kBluetoothGatt 为没有初始化，所以不能读取数据");
            return;
        }
        gatt.readCharacteristic(characteristic);
    }

    public void Disconnect() {
        if (gatt != null) {
            gatt.disconnect();
        }
    }
}
