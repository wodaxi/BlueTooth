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

import com.oodso.checkmouse.adapter.LeDeviceListAdapter;
import com.oodso.checkmouse.dao.UserData;
import com.oodso.checkmouse.dao.UserDataManager;
import com.oodso.checkmouse.ui.LocalDeviceActiviry;

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ListView mDevice;
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
                    break;
                case 2:
                    break;

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
            if (toHexString1(scanRecord).contains("18f0")) {
                
            }
            System.out.println("检测鼠设备-- " + device.getAddress());



            leDeviceListAdapter.addDevice(device);

            Message message = handler.obtainMessage();
            message.what = 1;
            handler.sendMessage(message);
        }
    };
    private Intent intent;
    private UserDataManager mUserDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (mUserDataManager == null) {
            mUserDataManager = new UserDataManager(this);
            mUserDataManager.openDataBase();
        }

        initView();
    }

    private void initView() {

        findViewById(R.id.bt_search).setOnClickListener(this);
        findViewById(R.id.bt_local).setOnClickListener(this);
        mDevice = (ListView) findViewById(R.id.ll_device_list);
        leDeviceListAdapter = new LeDeviceListAdapter(MainActivity.this);



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
            case R.id.bt_local:
                //跳转页面
                intent = new Intent(MainActivity.this, LocalDeviceActiviry.class);
                startActivity(intent);
                break;
            case R.id.bt_search:
                //检测蓝牙是否开启
                blAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!blAdapter.isEnabled()) {
                    this.intent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(this.intent);
                } else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            leDeviceListAdapter.clear();
                            boolean b = blAdapter.startLeScan(mLeScanCallback);
                            showProgressDialog(MainActivity.this);
                        }
                    }, 0);

                }
                break;
        }


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
        super.onPause();
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
        if(device == null)
            return;
        turnShowDialog(device,position);
    }

    private void turnShowDialog(final BluetoothDevice device, final int index) {
        final EditText et = new EditText(MainActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设备重命名");
        builder.setView(et);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String device_name = et.getText().toString();

                String address = device.getAddress();

                if (!TextUtils.isEmpty(device_name)){
                    leDeviceListAdapter.updateView(mDevice,device_name,index);
                    UserData userData = new UserData(device_name,address);
                    mUserDataManager = new UserDataManager(MainActivity.this);
                    mUserDataManager.openDataBase();
                    mUserDataManager.insertUserData(userData);
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


}
