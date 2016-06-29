package com.oodso.checkmouse.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.oodso.checkmouse.R;

import java.util.ArrayList;

public class SearchDeviceListAdapter extends BaseAdapter {
    private ArrayList<BluetoothDevice> mLeDevices;
    public ArrayList<String> mAddress;
    public ArrayList<String> mDeviceNames;
    private LayoutInflater mInflator;
    private ViewHolder viewHolder;

    public SearchDeviceListAdapter(Activity mActivity) {
        super();
        mLeDevices = new ArrayList<BluetoothDevice>();
        mAddress = new ArrayList<String>();
        mDeviceNames = new ArrayList<String>();
        mInflator = mActivity.getLayoutInflater();
    }

    public void addDevice(BluetoothDevice device) {
        if (!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }

    public String getAddress(int position){
       return mAddress.get(position);    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mDeviceNames.size();
    }

    @Override
    public Object getItem(int i) {
        return mDeviceNames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        viewHolder = new ViewHolder();
        if (view == null) {
            view = mInflator.inflate(R.layout.device_item, null);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
            viewHolder.deviceNumber = (TextView) view.findViewById(R.id.device_number);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

            viewHolder.deviceName.setText(mDeviceNames.get(i));
            viewHolder.deviceAddress.setText(mAddress.get(i));
            viewHolder.deviceNumber.setText((i+1) + "");

        return view;
    }

    static class ViewHolder {
      public  TextView deviceName, deviceAddress, deviceNumber;

    }

    public void updateView(ListView mDevice, String string, int x) {
        int firstVisiblePosition = mDevice.getFirstVisiblePosition();
        if(x - firstVisiblePosition >=0){
            View view = mDevice.getChildAt(x - firstVisiblePosition);
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.deviceName = (TextView) view.findViewById(R.id.device_name);
            holder.deviceName.setText(string);


        }
    }
}
