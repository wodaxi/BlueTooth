/*
 * Copyright (c) 2011, QUALCOMM Incorporated.
 * All Rights Reserved.
 * QUALCOMM Proprietary and Confidential.
 * Developed by QRD Engineering team.
 */
package com.oodso.checkmouse.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class UserDataManager {

    private static final String TAG = "UserDataManager";
    private static final String DB_NAME = "user_data";
    private static final String TABLE_NAME = "users";
    public static final String ID = "_id";

    public static final String USER_NAME = "user_name";
    public static final String USER_PWD = "user_pwd";
    public static final String SILENT = "silent";
    public static final String VIBRATE = "vibrate";

    private static final int DB_VERSION = 2;
    private Context mContext = null;

    private static final String DB_CREATE = "CREATE TABLE " + TABLE_NAME + " ("
            + ID + " integer primary key," + USER_NAME + " varchar,"
            + USER_PWD + " varchar" + ");";

    private SQLiteDatabase mSQLiteDatabase = null;
    private DataBaseManagementHelper mDatabaseHelper = null;

    private static class DataBaseManagementHelper extends SQLiteOpenHelper {

        DataBaseManagementHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "db.getVersion()=" + db.getVersion());
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
            db.execSQL(DB_CREATE);
            Log.i(TAG, "db.execSQL(DB_CREATE)");
            Log.e(TAG, DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "DataBaseManagementHelper onUpgrade");
            onCreate(db);
        }
    }

    public UserDataManager(Context context) {
        mContext = context;
        Log.i(TAG, "UserDataManager construction!");
    }

    public void openDataBase() throws SQLException {

        mDatabaseHelper = new DataBaseManagementHelper(mContext);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
    }

    public void openDBRead() throws SQLException {
        mDatabaseHelper = new DataBaseManagementHelper(mContext);
        mSQLiteDatabase = mDatabaseHelper.getReadableDatabase();
    }

    public void closeDataBase() throws SQLException {

        mDatabaseHelper.close();
    }

    public long insertUserData(UserData userData) {

        String userName = userData.getUserName();
        String userPwd = userData.getUserPwd();

        ContentValues values = new ContentValues();
        values.put(USER_NAME, userName);
        values.put(USER_PWD, userPwd);
        return mSQLiteDatabase.insert(TABLE_NAME, ID, values);
    }

    public boolean updateUserData(UserData userData) {

        int id = userData.getUserId();
        String userName = userData.getUserName();
        String userPwd = userData.getUserPwd();

        ContentValues values = new ContentValues();
        values.put(USER_NAME, userName);
        values.put(USER_PWD, userPwd);
        return mSQLiteDatabase.update(TABLE_NAME, values, ID + "=" + id, null) > 0;
    }

    public Cursor fetchUserData(int id) throws SQLException {

        Cursor mCursor = mSQLiteDatabase.query(false, TABLE_NAME, null, ID
                + "=" + id, null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchAllUserDatas() {

        return mSQLiteDatabase.query(TABLE_NAME, null, null, null, null, null,
                null);
    }

    public ArrayList<UserData> findUserData() throws SQLException {
        ArrayList<UserData> userDatas = new ArrayList<>();
        Cursor cursor = mSQLiteDatabase.query(TABLE_NAME, null, null, null, null, null,
                null);
        int columnCount = cursor.getColumnCount();
        while (cursor.moveToNext()) {
            HashMap<String, String> maps = new HashMap<>();
            for (int i = 0; i < columnCount; i++) {
                String columnName = cursor.getColumnName(i);
                maps.put(columnName, cursor.getString(cursor.getColumnIndex(columnName)));
            }

            UserData userData = new UserData();
            userData.setUserName(maps.get("user_name"));
            userData.setUserPwd(maps.get("user_pwd"));
            userDatas.add(userData);
        }
        cursor.close();
        mSQLiteDatabase.close();
        return userDatas;
    }

    public ArrayList<String> findDeviceAddress() {
        ArrayList<String> addrssList = new ArrayList<>();

        Cursor cursor = mSQLiteDatabase.query(TABLE_NAME, null, null, null, null, null,
                null);
        int columnCount = cursor.getColumnCount();
        while (cursor.moveToNext()) {
            HashMap<String, String> maps = new HashMap<>();
            for (int i = 0; i < columnCount; i++) {
                String columnName = cursor.getColumnName(i);
                maps.put(columnName, cursor.getString(cursor.getColumnIndex(columnName)));

                addrssList.add(cursor.getString(cursor.getColumnIndex(columnName)));

            }
        }
        cursor.close();
        mSQLiteDatabase.close();
        return addrssList;
    }

    public boolean deleteUserData(int id) {

        return mSQLiteDatabase.delete(TABLE_NAME, ID + "=" + id, null) > 0;
    }

    public boolean deleteAllUserDatas() {

        return mSQLiteDatabase.delete(TABLE_NAME, null, null) > 0;
    }


    public String getStringByColumnName(String columnName, int id) {
        Cursor mCursor = fetchUserData(id);
        int columnIndex = mCursor.getColumnIndex(columnName);
        String columnValue = mCursor.getString(columnIndex);
        mCursor.close();
        return columnValue;
    }


    public int findUserNameByUserPwd(String userpwd) throws SQLException {

        int result = 1;
        Cursor cursor = mSQLiteDatabase.query(TABLE_NAME, null, USER_PWD + "=" + "'" + userpwd + "'", null, null, null, null);
        if (cursor != null) {
            result = cursor.getCount();
            cursor.close();
        }
        mSQLiteDatabase.close();
        System.out.println("userNameByUserPwd" + result);
        return result;
    }

    public boolean updateUserDataByPwd(String username, int id) {
        ContentValues values = new ContentValues();
        values.put(USER_NAME, username);
        return mSQLiteDatabase.update(TABLE_NAME, values, ID + "=" + id, null) > 0;
    }


}
