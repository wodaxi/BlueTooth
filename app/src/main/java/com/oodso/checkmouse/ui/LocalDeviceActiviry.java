package com.oodso.checkmouse.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.oodso.checkmouse.R;
import com.oodso.checkmouse.adapter.LocalDeviceListAdapter;
import com.oodso.checkmouse.dao.UserData;
import com.oodso.checkmouse.dao.UserDataManager;
import com.oodso.checkmouse.utils.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xulei on 2016/6/28.
 * <p/>
 * <p/>
 * 显示本地设备的页面
 */
public class LocalDeviceActiviry extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView mDeviceList;
    private UserDataManager mUserDataManager;
    private HashMap<String, String> maps;
    private LocalDeviceListAdapter localDeviceListAdapter;
    private Button bt_local, bt_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (mUserDataManager == null) {
            mUserDataManager = new UserDataManager(this);
            mUserDataManager.openDataBase();
        }
        initUI();
    }

    private void initUI() {
        bt_local = (Button) findViewById(R.id.bt_local);
        bt_local.setText("清空数据");
        bt_local.setOnClickListener(this);

        bt_search = (Button) findViewById(R.id.bt_search);
        bt_search.setText("返回");
        bt_search.setOnClickListener(this);

        mDeviceList = (ListView) findViewById(R.id.ll_device_list);

        //从数据库去查寻数据 填充到listviwe中
        mUserDataManager.openDataBase();
        ArrayList<UserData> userDatas = mUserDataManager.findUserData();

        localDeviceListAdapter = new LocalDeviceListAdapter(LocalDeviceActiviry.this);
        localDeviceListAdapter.mUserData = userDatas;

        mDeviceList.setAdapter(localDeviceListAdapter);
        mDeviceList.setOnItemClickListener(this);

    }

    @Override
    protected void onPause() {
        if (mUserDataManager != null) {
            mUserDataManager.closeDataBase();
            mUserDataManager = null;
        }
        super.onPause();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final UserData userdata = localDeviceListAdapter.getUserData(position);
        if (userdata == null)
            return;
        turnShowDialog(userdata, position);


    }

    private void turnShowDialog(final UserData userdata, final int index) {
        final EditText et = new EditText(LocalDeviceActiviry.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("要进行的操作");
//        builder.setView(et);
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String device_name = et.getText().toString();
//                localDeviceListAdapter.updateView(mDeviceList, device_name, index);
////                String userPwd = userdata.getUserPwd();
//
//                boolean b = mUserDataManager.updateUserDataByPwd(device_name, index + 1);
//                if (b) {
//
//                    updateAdapter();
//                } else {
//                    Toast.makeText(LocalDeviceActiviry.this, "数据库操作失败", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//
//
//        });
        builder.setItems(new String[] { "连接", "命名", "查看电量", "甲醛浓度", "历史数据" }, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        String address = userdata.getUserPwd();

                        break;
                    case 1:
                        break;

                    case 2:

                        break;
                    case 3:

                        break;
                    case 4:
                        break;

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

    private void updateAdapter() {
        ArrayList<UserData> userDatas = mUserDataManager.findUserData();
        localDeviceListAdapter.mUserData = userDatas;
        localDeviceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_local) {
            //弹框  是否清除本地数据

            turnShowDialog();
        }
        if (v.getId() == R.id.bt_search) {
            //弹框  是否清除本地数据

            LocalDeviceActiviry.this.finish();
        }


    }

    private void turnShowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("清空本地数据？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SPUtils spUtils = new SPUtils(LocalDeviceActiviry.this);
                spUtils.clearSP();


                if (mUserDataManager != null) {
                    mUserDataManager.openDBRead();


                    boolean b = mUserDataManager.deleteAllUserDatas();
                    if (b) {
                        updateAdapter();
                    } else {
                        Toast.makeText(LocalDeviceActiviry.this, "暂无数据", Toast.LENGTH_SHORT).show();
                    }
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
