package com.ghao.developer.offline.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {
    private OnSqliteUpdateListener onSqliteUpdateListener;
    private static HashMap<String,DBHelper> sDBHelperHashMap = new HashMap<String,DBHelper>();
    private List<String> createTableSqls;
    private String currentDbName;

    private String  NAME = "";
    public DBHelper(Context context, String dbName,int dbVersion,List<String> tableSqls) {
        super(context,dbName,null,dbVersion);
        createTableSqls = new ArrayList<String>();
        createTableSqls.addAll(tableSqls);
        currentDbName = dbName;
    }

    public static DBHelper getInstance(Context context,String dbName,int dbVersion,List<String> tableSqls){
        DBHelper dbHelper = sDBHelperHashMap.get(dbName);
        if(dbHelper==null){
            dbHelper = new DBHelper(context,dbName,dbVersion,tableSqls);
        }
        sDBHelperHashMap.put(dbName,dbHelper);
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for(String createTable : createTableSqls){
            sqLiteDatabase.execSQL(createTable);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(this.onSqliteUpdateListener!=null){
            onSqliteUpdateListener.onSqliteUpdateListener(sqLiteDatabase,i,i1);
        }
    }

    public void execSQL(String sql,Object[] bindArgs){
        DBHelper dbHelper = sDBHelperHashMap.get(currentDbName);
        synchronized (dbHelper){
            SQLiteDatabase mSQLiteDatabase = dbHelper.getWritableDatabase();
            mSQLiteDatabase.execSQL(sql,bindArgs);
        }
    }

    public long insert(String table,ContentValues contentValues){
        DBHelper dbHelper = sDBHelperHashMap.get(currentDbName);
        synchronized (dbHelper){
            SQLiteDatabase mSQLiteDatabase = dbHelper.getWritableDatabase();
            long resultNumber = mSQLiteDatabase.insert(table,null,contentValues);
            return resultNumber;
        }
    }

    public int update(String table,ContentValues contentValues,String whereClause , String[] whereArgs){
        DBHelper dbHelper = sDBHelperHashMap.get(currentDbName);
        synchronized (dbHelper){
            SQLiteDatabase mSQLiteDatabase = dbHelper.getWritableDatabase();
            int resultNumber = mSQLiteDatabase.update(table,contentValues,whereClause,whereArgs);
            return resultNumber;
        }
    }

    public int delete(String table,String whereClause,String[] whereArgs){
        DBHelper dbHelper = sDBHelperHashMap.get(currentDbName);
        synchronized (dbHelper){
            SQLiteDatabase mSQLiteDatabase = dbHelper.getWritableDatabase();
            int resultNumber = mSQLiteDatabase.delete(table,whereClause,whereArgs);
            return resultNumber;
        }
    }

    public Cursor rawQuery(String sql,String[] bindArgs){
        DBHelper dbHelper = sDBHelperHashMap.get(currentDbName);
        synchronized (dbHelper){
            SQLiteDatabase mSQLiteDatabase = dbHelper.getReadableDatabase();
            Cursor cursor = mSQLiteDatabase.rawQuery(sql,bindArgs);
            return cursor;
        }
    }

    public Cursor query(String tableName,String sql){
        DBHelper dbHelper = sDBHelperHashMap.get(currentDbName);
        synchronized (dbHelper){
            SQLiteDatabase mSQLiteDatabase = dbHelper.getReadableDatabase();
            Cursor cursor= mSQLiteDatabase.rawQuery(sql,null);
            return cursor;
        }
    }

    public void setOnSqliteUpdateListener(OnSqliteUpdateListener onSqliteUpdateListener){
        this.onSqliteUpdateListener = onSqliteUpdateListener;
    }

}
