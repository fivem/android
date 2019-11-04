package com.ghao.developer.offline.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class InDao {
    private DBHelper dbHelper;
    private Context context;
    public InDao(Context context) {
        this.context = context;
        List<String> createTableSqls = new ArrayList<String>();
        dbHelper = DBHelper.getInstance(context,"gh.store",3,createTableSqls);
    }

    /**
     * 根据入款单编号执行入库操作
     * @param contentValues
     */
    public long actionIn(ContentValues contentValues){
        long resultNumber = 0;
        //查询货品是否已经执行过入库(在sqlite数据库中是否已经存在.防止多次扫码入库)
        String rkdbh = contentValues.getAsString("rkdbh");
        Cursor cursor = dbHelper.query("rkd","select * from rkd where rkdbh='"+rkdbh+"' or rkdbh is null");
        //TODO
        if(cursor!=null && cursor.getCount()!=0){
            Toast.makeText(context, "编号["+rkdbh+"]已经入库.", Toast.LENGTH_SHORT).show();
        }else{
            resultNumber = dbHelper.insert("rkd",contentValues);
        }
        return resultNumber;
    }
}
