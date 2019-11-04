package com.ghao.developer.offline;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.ghao.developer.offline.dao.SyncDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        String rkdbh = intent.getStringExtra("title");
        SyncDao syncDao = new SyncDao(this);
        Cursor cursor = syncDao.getDetailByRkdbh(rkdbh);
        while(cursor.moveToNext()){
            String htbh = cursor.getString(2);
            String pcbh = cursor.getString(3);
            long czsj = cursor.getLong(5);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            // time为转换格式后的字符串
            String time = dateFormat.format(new Date(czsj)) ;

            EditText rkdbhText = findViewById(R.id.rkdbhText);
            rkdbhText.setText(rkdbh);

            EditText htbhText = findViewById(R.id.htbhText);
            htbhText.setText(htbh);

            EditText pcbhText = findViewById(R.id.pcbhText);
            pcbhText.setText(pcbh);

            EditText czsjText = findViewById(R.id.czsjText);
            czsjText.setText(time);
        }
    }


}
