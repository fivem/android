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
        createTableSqls.add("create table if not exists rkd(id INTEGER primary key AUTOINCREMENT,rkdbh text,htbh text,pcbh,text,status INTEGER,rksj text,czr text)");
        dbHelper = DBHelper.getInstance(context,"gh.store",2,createTableSqls);
    }

    /**
     * 根据入款单编号执行入库操作
     * @param contentValues
     */
    public void actionIn(ContentValues contentValues){
        //查询货品是否已经执行过入库(在sqlite数据库中是否已经存在.防止多次扫码入库)
        String rkdbh = contentValues.getAsString("rkdbh");
        Cursor cursor = dbHelper.query("rkd","select * from rkd where rkdbh='"+rkdbh+"' or rkdbh is null");
        //TODO
        if(cursor!=null && cursor.getCount()!=0 && false){
            Toast.makeText(context, "编号["+rkdbh+"]已经入库.", Toast.LENGTH_SHORT).show();
        }else{
            dbHelper.insert("rkd",contentValues);
        }
    }
}
