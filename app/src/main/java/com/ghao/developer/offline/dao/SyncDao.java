package com.ghao.developer.offline.dao;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

public class SyncDao {
    private DBHelper mDBHelper;
    private Context context;
    public SyncDao(Context context){
        this.context = context;
        mDBHelper = DBHelper.getInstance(context,Const.DB_NAME,Const.VERSION,new ArrayList<String>());
    }
    public Cursor getInData(){
        return mDBHelper.query("select * from rkd order by rksj desc");
    }
    public Cursor getOutData(){
        return mDBHelper.query("select * from ckd order by rksj desc");
    }
    public Cursor getDataByStatus(String status){
        return mDBHelper.query("select * from rkd where status='"+status+"' order by rksj desc");
    }
    public Cursor getDetailByRkdbh(String kdbh){
        return mDBHelper.query("select * from rkd where rkdbh='"+kdbh+"'");
    }
    public Cursor getDetailByCkdbh(String kdbh){
        return mDBHelper.query("select * from ckd where ckdbh='"+kdbh+"'");
    }
}
