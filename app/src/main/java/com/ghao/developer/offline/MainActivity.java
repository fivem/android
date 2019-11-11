package com.ghao.developer.offline;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import com.ghao.developer.offline.dao.InDao;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, InFragment.OnFragmentInteractionListener,OutFragment.OnFragmentInteractionListener,SyncFragment.OnFragmentInteractionListener {

    private Context context = this;
    ViewPager viewPager;
    BottomNavigationView navigation;
    private String operator = "in";
    List<Fragment> fragments = new ArrayList<>();
    private final Logger LOG = LoggerFactory.getLogger(MainActivity.class);

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_in:
                   viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_out:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_sync:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Integer perm = context.checkCallingOrSelfPermission( "android.permission.WRITE_EXTERNAL_STORAGE");
        if(perm==PackageManager.PERMISSION_GRANTED){
            LOG.debug("mainActivity is created");
            String path = "/sdcard/zebra-log";
            File downloadFile = new File(path);
            if(downloadFile.exists()){
                Log.e("file","file exists");
                File[] files = downloadFile.listFiles();
                for(File file : files){
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        InputStreamReader isr=new InputStreamReader(fis,"utf8");
                        BufferedReader br=new BufferedReader(isr);
                        String line ;
                        while((line=br.readLine()) != null){
                            Log.e("readForLogBack",line);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else{
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        viewPager = findViewById(R.id.view_pager);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragments.add(new InFragment());
        fragments.add(new OutFragment());
        fragments.add(new SyncFragment());

        FragmentAdapter adapter = new FragmentAdapter(fragments,getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                navigation.getMenu().getItem(i).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Integer perm = context.checkCallingOrSelfPermission( "android.permission.WRITE_EXTERNAL_STORAGE");
        if(perm==PackageManager.PERMISSION_GRANTED) {
            float rand = (float) (Math.random() * 10f);
            Log.e("random:", rand + "");
            if (rand > 55.0f) {
                LOG.info("执行版本更新");
                DownloadTask downloadTask = new DownloadTask(this);
                //String url = "http://94.191.126.165:88/zebra.png";
                String url = "http://94.191.126.165:88/zebra-release.apk";
                downloadTask.execute(url);
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(MainActivity.this,"this is："+uri,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(resultCode, data);
            final String qrContent = scanResult.getContents();
            //Toast.makeText(context, "扫描结果:" + qrContent, Toast.LENGTH_SHORT).show();
            if(qrContent!=null){
                if("in".equals(this.operator)){
                    //System.out.println("执行入库操作:"+qrContent);
                    LOG.info("入库扫描结果:"+qrContent);
                    InFragment fragment = (InFragment) fragments.get(0);
                    fragment.setEditTextValue(qrContent);
                }else if("out".equals(this.operator)){
                    LOG.info("出库扫描结果:"+qrContent);
                    OutFragment fragment = (OutFragment) fragments.get(1);
                    fragment.setEditTextValue(qrContent);
                    //System.out.println("执行出库操作:"+qrContent);
                }
            }
        }
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public void Scanner(String operator){
        if(!isNetworkConnected(context)){
            System.out.println("网络未连接");
        }else{
            System.out.println("网络已连接");
        }
        this.operator = operator;
        //打开扫描界面扫描条形码或二维码
        Intent intent = new Intent(context, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
