package com.singun.openvpn.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.singun.openvpn.util.LogUtil;

/**
 * Created by Administrator on 2016/10/13.
 */
public class PackageNameDB extends SQLiteOpenHelper {

    private static int version = 1;

    public PackageNameDB(Context context) {
        super(context, "packageName.db", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE package_name(_id INTEGER PRIMARY KEY AUTOINCREMENT, package VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
