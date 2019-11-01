package com.ghao.developer.offline.dao;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

public class SyncDao {
    private DBHelper mDBHelper;
    private Context context;
    public SyncDao(Context context){
        this.context = context;
        mDBHelper = DBHelper.getInstance(context,"gh.store",2,new ArrayList<String>());
    }
    public Cursor getAllData(){
        return mDBHelper.query("rkd","select * from rkd order by rksj desc");
    }
    public Cursor getDataByStatus(String status){
        return mDBHelper.query("rkd","select * from rkd where status='"+status+"' order by rksj desc");
    }
}
