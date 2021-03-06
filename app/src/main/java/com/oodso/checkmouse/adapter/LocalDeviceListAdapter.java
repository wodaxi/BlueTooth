package com.oodso.checkmouse.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.oodso.checkmouse.R;
import com.oodso.checkmouse.dao.UserData;

import java.util.ArrayList;

public class LocalDeviceListAdapter extends BaseAdapter {
    public ArrayList<UserData> mUserData;
    private LayoutInflater mInflator;
    private ViewHolder viewHolder;

    public LocalDeviceListAdapter(Activity mActivity) {
        super();
        mUserData = new ArrayList<UserData>();
        mInflator = mActivity.getLayoutInflater();
    }
    public UserData getUserData(int position) {
        return mUserData.get(position);
    }

    public void addUserData(UserData device) {
        if (!mUserData.contains(device)) {
            mUserData.add(device);
        }
    }
    @Override
    public int getCount() {
        return mUserData.size();
    }

    @Override
    public Object getItem(int i) {
        return mUserData.get(i);
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
        UserData userData = mUserData.get(i);


        viewHolder.deviceAddress.setText(userData.getUserPwd());
        viewHolder.deviceName.setText(userData.getUserName());
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
