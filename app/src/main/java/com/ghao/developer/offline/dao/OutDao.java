package com.ghao.developer.offline.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;

public class OutDao {
    private DBHelper mDBHelper;
    private Context context;
    public OutDao(Context context){
        this.context = context;
        mDBHelper = DBHelper.getInstance(context,Const.DB_NAME,Const.VERSION,new ArrayList<String>());
    }
    public long actionOut(ContentValues contentValues){
        String ckdbh = contentValues.getAsString("ckdbh");
        Cursor cursor = mDBHelper.query("select * from ckd where ckdbh='"+ckdbh+"'");
        if(cursor.getCount()>0){
            Toast.makeText(context,"编号["+ckdbh+"]已经出库",Toast.LENGTH_SHORT).show();
            return 0;
        }else{
            return mDBHelper.insert("ckd",contentValues);
        }
    }
}
