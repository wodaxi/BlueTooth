package com.oodso.checkmouse.ui;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.oodso.checkmouse.BleDevice;
import com.oodso.checkmouse.BleService;
import com.oodso.checkmouse.R;
import com.oodso.checkmouse.utils.SPUtils;

/**
 * Created by xulei on 2016/7/28.
 */
public class DataActivity extends Activity implements BleDevice.RFStarBLEBroadcastReceiver {

    private TextView tv_data, tv_device_name, tv_device_state;
    private SPUtils spUtils;
    private Button bt_dicon;
    private Button bt_connect;
    private BluetoothDevice device;
    private BleDevice bleDevice;

    private boolean isConnect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        spUtils = new SPUtils(DataActivity.this);
        initUI();
        initData();

    }


    private void initUI() {

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_data = (TextView) findViewById(R.id.tv_data);
        tv_device_name = (TextView) findViewById(R.id.tv_device_name);
        tv_device_state = (TextView) findViewById(R.id.tv_device_state);
        Button bt_local = (Button) findViewById(R.id.bt_local);
        bt_dicon = (Button) findViewById(R.id.bt_dicon);
        bt_connect = (Button) findViewById(R.id.bt_connect);
        LinearLayout ll_data = (LinearLayout) findViewById(R.id.ll_data);
        ListView mDevice = (ListView) findViewById(R.id.ll_device_list);

        tv_title.setText("实时数据");
        bt_local.setVisibility(View.GONE);
        mDevice.setVisibility(View.GONE);
        ll_data.setVisibility(View.VISIBLE);


    }

    private void initData() {
        Bundle extras = getIntent().getExtras();
        device = (BluetoothDevice) extras.get("device");

        int index = (int) extras.get("index");
        tv_device_name.setText("Device Address:" + device.getAddress());


        tv_data.setText(device.getAddress());

//        String deviceNameByAdress = spUtils.getDeviceNameByAdress(device.getAddress());


        bt_connect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                bleDevice = new BleDevice(DataActivity.this, device);
                bleDevice.setBLEBroadcastDelegate(DataActivity.this);

            }
        });
        bt_dicon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                bleDevice = new BleDevice(DataActivity.this, device);
                bleDevice.DisConne();
                finish();
            }
        });

    }


    @Override
    public void onReceive(Context context, Intent intent, String macData, String uuid) {
        String action = intent.getAction();
        isConnect = bleDevice.connectedOrDis(action);
        if (BleService.ACTION_GATT_CONNECTED.equals(action)) {
            System.out.println("DataActivity连接成功");
            tv_device_state.setText("Device State :" + "连接成功");
        } else if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
            System.out.println("DataActivity连接断开");
            tv_device_state.setText("Device State :" + "未连接");
        } else if (BleService.ACTION_GATT_SERVICES_DISCOVERED
                .equals(action)) {

            Log.e("11", "发现服务");

        } else if (BleService.ACTION_DATA_AVAILABLE
                .equals(action)) {
            Log.e("121", "有数据返回");

        }
    }
}
