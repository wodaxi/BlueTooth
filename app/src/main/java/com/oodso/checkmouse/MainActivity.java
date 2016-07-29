package com.oodso.checkmouse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.oodso.checkmouse.adapter.LeDeviceListAdapter;
import com.oodso.checkmouse.dao.UserDataManager;
import com.oodso.checkmouse.ui.DataActivity;
import com.oodso.checkmouse.utils.SPUtils;
import com.oodso.checkmouse.utils.ShowToast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener, LeDeviceListAdapter.ClickIterface {

    private ListView mDevice;
    private List<BluetoothDevice> devices;
    private BluetoothAdapter blAdapter;
    private ProgressDialog dialog;
    private LeDeviceListAdapter leDeviceListAdapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:


                    leDeviceListAdapter.notifyDataSetChanged();


                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int count = leDeviceListAdapter.getCount();
                            if (count != 0) {

                                for (int i = 0; i < count; i++) {
                                    BluetoothDevice device = leDeviceListAdapter.getDevice(i);
                                    String deviceNameByAdress = spUtils.getDeviceNameByAdress(device.getAddress());
                                    if (!TextUtils.isEmpty(deviceNameByAdress)) {
                                        leDeviceListAdapter.updateView(mDevice, deviceNameByAdress, i);
                                        leDeviceListAdapter.notifyDataSetChanged();
                                    } else {

                                    }
                                }
                            } else {
//                                showToast.show("暂无设备", 0);
                                    Toast.makeText(MainActivity.this,"未发现设备",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, 1000);
                    break;
                case 2:
                    showProgressDialog(MainActivity.this);
                    break;
//                case 3:
//                    showToast.show("连接成功",0);
//                    BluetoothDevice device = (BluetoothDevice) msg.obj;
//
//
//                    break;
//                case 4:
//                    showToast.show("连接失败",0);
//                    break;

            }
        }
    };
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            // TODO Auto-generated method stub
            /*
             * device : 识别的远程设备
			 * rssi : RSSI的值作为对远程蓝牙设备的报告; 0代表没有蓝牙设备;
			 * scanRecode: 远程设备提供的配对号(公告)
			 */
            //判断是否是检测鼠设备
            if (toHexString1(scanRecord).contains("18f0")) {}
//                leDeviceListAdapter.addDevice(device);
            if (!devices.contains(device)){

                devices.add(device);
            }
                Message message = handler.obtainMessage();
                message.what = 1;
                handler.sendMessage(message);


//            System.out.println("搜索到的设备 -- " + device);


        }
    };
    private Intent intent;
    private UserDataManager mUserDataManager;
    private SPUtils spUtils;
    private ShowToast showToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (mUserDataManager == null) {
//            mUserDataManager = new UserDataManager(this);
//            mUserDataManager.openDataBase();
//        }

        spUtils = new SPUtils(MainActivity.this);
        showToast = new ShowToast(MainActivity.this);
        devices = new ArrayList<>();

        initView();
    }

    private void initView() {

        findViewById(R.id.bt_search).setOnClickListener(this);
        findViewById(R.id.bt_local).setOnClickListener(this);
        mDevice = (ListView) findViewById(R.id.ll_device_list);
        leDeviceListAdapter = new LeDeviceListAdapter(MainActivity.this,devices);
        leDeviceListAdapter.SetClickIterface(this);


        mDevice.setAdapter(leDeviceListAdapter);
        mDevice.setOnItemClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_search:
                //跳转页面
//                intent = new Intent(MainActivity.this, LocalDeviceActiviry.class);
//                startActivity(intent);
                break;
            case R.id.bt_local:
                //检测蓝牙是否开启
                blAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!blAdapter.isEnabled()) {
                    this.intent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(this.intent, 1);
                } else {

                    turnToSearch();
                }

                break;
        }
    }

    private void turnToSearch() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                leDeviceListAdapter.clear();
                leDeviceListAdapter.notifyDataSetChanged();
                boolean b = blAdapter.startLeScan(mLeScanCallback);


                Message message = handler.obtainMessage();
                message.what = 2;
                handler.sendMessage(message);

            }
        }, 0);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                blAdapter.stopLeScan(mLeScanCallback);
            }
        }, 5000);
    }


    /**
     * 数组转成十六进制字符串
     *
     * @param b
     * @return HexString
     */
    public static String toHexString1(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        for (byte aB : b) {
            buffer.append(toHexString1(aB));
        }
        return buffer.toString();
    }

    public static String toHexString1(byte b) {
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1) {
            return "0" + s;
        } else {
            return s;
        }
    }

    @Override
    protected void onPause() {
        if (mUserDataManager != null) {
            mUserDataManager.closeDataBase();
            mUserDataManager = null;
        }

        showToast.cancel();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        showToast.cancel();
        super.onDestroy();
    }

    public void showProgressDialog(Context context) {
        dialog = ProgressDialog.show(context, "蓝牙扫描", "全力搜索中...",
                false, true);
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    dialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = leDeviceListAdapter.getDevice(position);
        if (device == null)
            return;
//        turnShowDialog(device, position);
    }

//    private  BluetoothGattCallback btgCallback = new BluetoothGattCallback() {
//        @Override
//        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//            super.onConnectionStateChange(gatt, status, newState);
//
//            if (newState == BluetoothProfile.STATE_CONNECTED) {
//                gatt.discoverServices(); //执行到这里其实蓝牙已经连接成功了
//                Message message = handler.obtainMessage();
//                message.what = 3;
//                message.obj = mDevice;
//                handler.sendMessage(message);
//
//            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                Message message = handler.obtainMessage();
//                message.what = 4;
//
//                handler.sendMessage(message);
//            }
//        }
//
//        @Override
//        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//            super.onServicesDiscovered(gatt, status);
//
//
//        }
//
//        @Override
//        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            super.onCharacteristicRead(gatt, characteristic, status);
//        }
//
//        @Override
//        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            super.onCharacteristicWrite(gatt, characteristic, status);
//        }
//    };

    private void turnShowDialog(final BluetoothDevice device, final int index) {
        final EditText et = new EditText(MainActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设备重命名");
        builder.setView(et);
//        builder.setItems(new String[] { "连接", "命名", "查看电量", "甲醛浓度", "历史数据" }, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case 0:
//                        device.connectGatt(MainActivity.this,false,btgCallback);
//                        break;
//                    case 1:
//                        break;
//
//                    case 2:
//
//                        break;
//                    case 3:
//
//                        break;
//                    case 4:
//                        break;
//
//                }
//
//            }
//        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String device_name = et.getText().toString();

                String address = device.getAddress();

                if (!TextUtils.isEmpty(device_name)) {
                    leDeviceListAdapter.updateView(mDevice, device_name, index);
//                    UserData userData = new UserData(device_name, address);
//                    mUserDataManager = new UserDataManager(MainActivity.this);
//                    mUserDataManager.openDataBase();
//                    mUserDataManager.insertUserData(userData);


                    spUtils.saveAddress(address, device_name);
                }
            }


        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            turnToSearch();
        }

    }


    @Override
    public void changeName(BluetoothDevice device, int index) {
        turnShowDialog(device, index);
    }

    @Override
    public void gotoData(BluetoothDevice device, int index) {
        Intent intent = new Intent(MainActivity.this, DataActivity.class);
        intent.putExtra("device", device);
        intent.putExtra("index", index);
        startActivity(intent);
    }
}
