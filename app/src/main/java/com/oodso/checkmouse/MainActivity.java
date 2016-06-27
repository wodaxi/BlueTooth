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
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.oodso.checkmouse.adapter.LeDeviceListAdapter;
import com.oodso.checkmouse.utils.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ListView mDevice;
    private BluetoothAdapter blAdapter;
    private HashMap<String, String> maps;
    private ProgressDialog dialog;
    private ArrayList<BluetoothDevice> mLeDevices;
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
//            if (toHexString1(scanRecord).contains("18f0")) {
            System.out.println("检测鼠设备-- " + device.getAddress());

            //先去sp中寻找  看看是否有mac地址  如果没有就添加 如果有 就过滤
            spUtils.saveAddress(device.getAddress(),"");

            leDeviceListAdapter.addDevice(device);
            Map<String, String> devices = spUtils.getDevices(device.getAddress());



            Message message = handler.obtainMessage();
            message.what = 1;
            handler.sendMessage(message);

//            }
        }
    };
    private SPUtils spUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        maps = new HashMap<String, String>();
        mLeDevices = new ArrayList<BluetoothDevice>();
//        spUtils = new SPUtils(MainActivity.this);
        initView();
    }

    private void initView() {
        findViewById(R.id.bt_search).setOnClickListener(this);
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
        if (v.getId() == R.id.bt_search) {
            //检测蓝牙是否开启
            blAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!blAdapter.isEnabled()) {
                Intent intent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(intent);
            } else {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        leDeviceListAdapter.clear();
                        boolean b = blAdapter.startLeScan(mLeScanCallback);
                        showProgressDialog(MainActivity.this);
                    }
                }, 0);

            }
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
        final BluetoothDevice device = leDeviceListAdapter.getDevice(position);
        if (device == null)
            return;
        showDialog(device, position);
    }

    private void showDialog(final BluetoothDevice device, final int x) {
        final EditText et = new EditText(MainActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设备重命名");
        builder.setView(et);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String device_name = et.getText().toString();
                spUtils.saveAddress(leDeviceListAdapter.getDevice(x).getAddress(),device_name);

                leDeviceListAdapter.updateView(mDevice,device_name,x);

//                leDeviceListAdapter.notifyDataSetChanged();

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
