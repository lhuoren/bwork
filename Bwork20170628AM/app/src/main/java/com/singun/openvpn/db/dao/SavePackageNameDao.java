package com.singun.openvpn.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.singun.openvpn.db.PackageNameDB;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/13.
 */
public class SavePackageNameDao {

    public final SQLiteDatabase mDb;

    public SavePackageNameDao(Context context){
        mDb = new PackageNameDB(context).getWritableDatabase();
    }

    public void add(String cityName){
        if(query().contains(cityName)){
            //如果存在什么都不存
        }else {
            //如果不存在
            mDb.execSQL("INSERT INTO package_name VALUES (NULL, ?)", new Object[]{cityName});
        }
    }

    public List<String> query(){
        List<String> cityList = new ArrayList<>();
        Cursor cursor = mDb.rawQuery("select package from package_name", null);

        while (cursor.moveToNext()){
            cityList.add(cursor.getString(cursor.getColumnIndex("package")));
        }
//        cursor.close();
        if (cursor != null){cursor.moveToFirst();}
        return cityList;
    }
//    mDb.close();

    // 删除package_name表的记录
    public void delect() {
        mDb.delete("package_name",null,null);
    }
}
