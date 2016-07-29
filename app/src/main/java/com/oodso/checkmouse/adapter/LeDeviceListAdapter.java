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

import java.util.List;

public class LeDeviceListAdapter extends BaseAdapter {
    private List<BluetoothDevice> mLeDevices;
    private LayoutInflater mInflator;
    private ViewHolder viewHolder;
    public ClickIterface clickinterface;

    public LeDeviceListAdapter(Activity mActivity,List<BluetoothDevice> mLeDevices) {
        super();
       this.mLeDevices = mLeDevices;
        mInflator = mActivity.getLayoutInflater();
    }

    public void addDevice(BluetoothDevice device) {
        if (!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }
    public void SetClickIterface(ClickIterface c){
        this.clickinterface = c;
    }

    public interface ClickIterface {
        public void changeName( BluetoothDevice device,  int index);

        public void gotoData(BluetoothDevice device,  int index);

    }


    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        viewHolder = new ViewHolder();
        if (view == null) {
            view = mInflator.inflate(R.layout.device_item, null);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
            viewHolder.deviceNumber = (TextView) view.findViewById(R.id.device_number);
            viewHolder.tv_changename = (TextView) view.findViewById(R.id.tv_changename);
            viewHolder.tv_goto_data = (TextView) view.findViewById(R.id.tv_goto_data);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final BluetoothDevice device = mLeDevices.get(i);

        final String deviceName = device.getName();
//        if (deviceName != null && deviceName.length() > 0)
//            viewHolder.deviceName.setText(device.getName());
        viewHolder.deviceAddress.setText(device.getAddress().toString());
        viewHolder.deviceNumber.setText((i + 1) + "");

        viewHolder.tv_changename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickinterface.changeName(device,i);
            }
        });
        viewHolder.tv_goto_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickinterface.gotoData(device,i);
            }
        });

        return view;
    }

    static class ViewHolder {
        public TextView deviceName, deviceAddress, deviceNumber, tv_changename, tv_goto_data;

    }

    public void updateView(ListView mDevice, String string, int x) {
        int firstVisiblePosition = mDevice.getFirstVisiblePosition();
        if (x - firstVisiblePosition >= 0) {
            View view = mDevice.getChildAt(x - firstVisiblePosition);
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.deviceName = (TextView) view.findViewById(R.id.device_name);
            holder.deviceName.setText(string);

        }
    }
}
