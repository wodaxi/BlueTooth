package com.oodso.checkmouse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;

import java.util.HashMap;

public class MainActivity extends Activity implements View.OnClickListener{

    private ListView mDevice;
    private BluetoothAdapter blAdapter;
    private Handler handler = new Handler();
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            // TODO Auto-generated method stub
			/*
			 * device : 识别的远程设备
			 * rssi : RSSI的值作为对远程蓝牙设备的报告; 0代表没有蓝牙设备;
			 * scanRecode: 远程设备提供的配对号(公告)
			 */


			if(toHexString1(scanRecord).contains("18f0")){
				System.out.println("检测鼠设备-- " + device.getAddress());
                if(!maps.containsKey(device.getAddress())){
                    maps.put(device.getAddress(),device.getName());
                }
                /*
                 * 　Map map = new HashMap();
                 　　Iterator iter = map.entrySet().iterator();
                 　　while (iter.hasNext()) {
                 　　Map.Entry entry = (Map.Entry) iter.next();
                 　　Object key = entry.getKey();
                 　　Object val = entry.getValue();
                 　　}
                 */
//                Iterator<Map.Entry<String, String>> iterator = maps.entrySet().iterator();
//                Map.Entry<String, String> next = iterator.next();
//                if(device.getAddress() != next.getKey()){
//
//                    maps.put(device.getAddress(),device.getName());
//
//                    System.out.println("maps -- "+maps.toString());
//                }

			}
        }
    };
    private HashMap<String,String> maps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        maps = new HashMap<String,String>();
        initView();
    }

    private void initView() {
        findViewById(R.id.bt_search).setOnClickListener(this);
        mDevice = (ListView) findViewById(R.id.ll_device_list);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.bt_search){
            //检测蓝牙是否开启
            blAdapter = BluetoothAdapter.getDefaultAdapter();
            if(!blAdapter.isEnabled()){
                Intent intent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(intent);
            }else{
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        boolean b = blAdapter.startLeScan(mLeScanCallback);
                        showProgressDialog(MainActivity.this);
                    }
                },10);
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
        blAdapter.stopLeScan(mLeScanCallback);
    }
    public void showProgressDialog(Context context){
//        ProgressDialog progressDialog = new ProgressDialog(context);


        ProgressDialog dialog = ProgressDialog.show(context, "提示", "全力搜索中...",
                false, true);
        dialog.show();

    }
}
