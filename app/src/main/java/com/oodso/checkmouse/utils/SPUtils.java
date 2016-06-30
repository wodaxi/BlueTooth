package com.oodso.checkmouse.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xulei on 2016/6/27.
 */
public class SPUtils {
    private static final String SP_NAME = "devices";
    private final SharedPreferences mSP;

    public SPUtils(Context context) {
        mSP = context.getSharedPreferences(SP_NAME, 0);
    }
    public void saveAddress(String address,String name){
        mSP.edit().putString(address,name).commit();
    }

    public Map<String ,String> getDevices(String address){
        Map<String, String> maps = new HashMap<String, String>();
        maps.put(address,mSP.getString(address,""));
        return maps;

    }
    public String getDeviceNameByAdress(String address){
        return  mSP.getString(address, "");
    }

    public void clearSP(){
        mSP.edit().clear().commit();
    }

}
