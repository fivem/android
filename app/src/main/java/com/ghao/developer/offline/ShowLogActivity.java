package com.ghao.developer.offline;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShowLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_log);

        String path = "/sdcard/zebra-log";
        File downloadFile = new File(path);
        if(downloadFile.exists()){
            String content = "" ;
            File[] files = downloadFile.listFiles();
            int i=0;
            for(File file : files){
                if(i>=2){
                    break;
                }
                i++;
                try {
                    FileInputStream fis = new FileInputStream(file);
                    InputStreamReader isr=new InputStreamReader(fis,"utf8");
                    BufferedReader br=new BufferedReader(isr);
                    String line ;
                    if(!"".equals(content)){
                        content += "---------------------------------\n";
                    }
                    content += file.getName() + "\n";
                    while((line=br.readLine()) != null){
                        Log.e("readForLogBack",line);
                        line +="\n";
                        content += line;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            TextView textView = findViewById(R.id.log);
            textView.setText(content);
        }
    }
}
