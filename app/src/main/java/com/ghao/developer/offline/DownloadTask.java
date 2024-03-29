package com.ghao.developer.offline;

import androidx.core.app.NotificationCompat;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DownloadTask extends AsyncTask<String,Integer,Boolean> {
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private boolean mSuccess = false;
    private Context context;
    public DownloadTask(Context context){
        this.context = context;
    }
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Log.e( "onPostExecute: ", "doInBackground的结果:"+aBoolean);
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.e( "onProgressUpdate: ","调用onProgressUpdate方法:"+values[0] );
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            String url = strings[0];
            Request request = new Request.Builder().url(url).build();
            Response response = null;
            response = mOkHttpClient.newCall(request).execute();
            if(response!=null && response.isSuccessful()){
                mSuccess = true;
                dealWithResult(response,url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mSuccess;
    }

    private boolean dealWithResult(Response response,String url){
        int len = 0;
        byte[] bytes = new byte[1024];
        InputStream is = null;
        FileOutputStream fos = null;
        try {

            Intent intent = new Intent(this.context,this.getClass());
            PendingIntent pi = PendingIntent.getActivity(this.context,0,intent,0);
            NotificationManager manager = (NotificationManager) this.context.getSystemService(this.context.NOTIFICATION_SERVICE);

            Notification.Builder notification = new Notification
                    .Builder(this.context,"default")
                    .setContentTitle("zebra新版下载")
                   // .setContentText("zebra新版下载")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher_cus_round)
                    .setContentIntent(pi)
                    .setChannelId(this.context.getPackageName())
                    .setAutoCancel(true);

            NotificationChannel channel = new NotificationChannel(
                    this.context.getPackageName(),
                    "版本更新",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            manager.createNotificationChannel(channel);


            String savePath = createFolderAndPath();
            File file = new File(savePath,getNameFromUrl(url));
            fos = new FileOutputStream(file);
            is = response.body().byteStream();
            manager.notify(0,notification.build());
            long sum=0;
            long total = response.body().contentLength();
            while((len=is.read(bytes))!=-1){
                fos.write(bytes,0,len);
                sum+=len;
                int progress = (int) (sum*1.0f/total*100);
                publishProgress(progress);
                notification.setProgress((int)total,(int)sum,false);
                manager.notify(0,notification.build());
            }
            fos.flush();
            installApk(file);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            try {

                fos.close();
                is.close();
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void installApk(File file){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
       // intent.setDataAndType(getUriFromFile(file),"image/png");
        intent.setDataAndType(getUriFromFile(file),"application/vnd.android.package-archive");
         context.startActivity(intent);


    }
    private String createFolderAndPath() throws IOException{
        String fileUrl = Environment.getExternalStorageDirectory().getPath()+"/upgrade";
        File downloadFile = new File(fileUrl);
        if(!downloadFile.mkdirs()){
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    private String getNameFromUrl(String url){
        return url.substring(url.lastIndexOf("/")+1);
    }

    private Uri getUriFromFile(File file){
        Uri fileUri = null;
        if(Build.VERSION.SDK_INT >=24){
            fileUri = FileProvider.getUriForFile(this.context, "com.ghao.developer.offline.fileprovider", file);
        }else{
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }
}
