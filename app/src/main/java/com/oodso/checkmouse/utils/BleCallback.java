package com.oodso.checkmouse.utils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public interface BleCallback {
	public void writeData(BluetoothGattCharacteristic characteristic, int status) throws BleErrorEx;

	public void readData(BluetoothGattCharacteristic characteristic, int status) throws BleErrorEx;

	public void BleStateChange(BluetoothGatt gatt, int status, int newState);

	public void ServicesDiscovered(BluetoothGatt gatt, int status);

	public void mLeScanCallback(BluetoothDevice device);

	public void scanLeDevice(boolean enable);
	

}
