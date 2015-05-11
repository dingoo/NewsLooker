package com.example.NewsLooker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.NewsLooker.util.CommonTools;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class AboutActivity extends Activity{

    private static final int UPDATA_CLIENT = 1;
    private static final int GET_UNDATAINFO_ERROR = 2;
    private static final int DOWN_ERROR = 3;
    private static final int NO_NEED_DOWN = 4;
    private static final int SET_MAX = 5;
    private static final int SET_PROGRESS = 6;
    private static final int SET_WAIT = 7;
    private static final int SET_STOP = 8;
    int versionCode=-1;
    String versionName="";

    private ProgressDialog pd=null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);


        try {
                PackageManager manager = getPackageManager();
                PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
                versionCode= info.versionCode;
                versionName=info.versionName;
            }catch (Exception e) {
                e.printStackTrace();
        }

        pd=new  ProgressDialog(this);
        Button update= (Button) findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new CheckVersionTask()).start();
            }
        });

        TextView tv=(TextView)findViewById(R.id.appdescribe);
        tv.setText(String.format(getString(R.string.aboutapp), versionName, versionCode));

    }

    //从服务器下载apk:
    public  File getFileFromServer(String urlpath){
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的

        FileOutputStream fos=null;
        BufferedInputStream bis=null;
        InputStream is=null;

        try {
            URL url = new URL(urlpath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            if (conn.getResponseCode()==200) {
                int filelength=conn.getContentLength();
                handler.obtainMessage(SET_MAX,filelength,0).sendToTarget();
                is = conn.getInputStream();
                File file=new File(Environment.getExternalStorageDirectory()+"/NewaLooker","NewsLooker.apk");
                fos = new FileOutputStream(file);
                bis = new BufferedInputStream(is);
                byte[] buffer = new byte[1024];
                int len;
                int total = 0;
                while ((len = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    total += len;
                    //获取当前下载量
                    handler.obtainMessage(SET_PROGRESS,total,0).sendToTarget();
                }
                return file;
            }
        } catch (Exception e) {
            handler.obtainMessage(DOWN_ERROR,"下载文件时出错").sendToTarget();
            if(pd.isShowing())pd.dismiss();
            e.printStackTrace();
        }finally {
            try {
                if (fos!=null)fos.close();
                if(bis!=null)bis.close();
                if(is!=null)is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public class CheckVersionTask implements Runnable{

        @Override
        public void run() {
            handler.obtainMessage(SET_WAIT).sendToTarget();
            String path=CommonTools.SERVERURL+"/update.php?version=" + versionCode;
            try {
                Document doc= Jsoup.connect(path).get();
                String backinfo=doc.body().text().toString();

                if(backinfo.equals("noneed")){
                    handler.obtainMessage(SET_STOP).sendToTarget();
                    handler.obtainMessage(NO_NEED_DOWN).sendToTarget();
                }else if(backinfo.equals("candown")){
                    handler.obtainMessage(SET_STOP).sendToTarget();
                    handler.obtainMessage(UPDATA_CLIENT).sendToTarget();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * 弹出对话框通知用户更新程序
     *
     * 弹出对话框的步骤：
     *  1.创建alertDialog的builder.
     *  2.要给builder设置属性, 对话框的内容,样式,按钮
     *  3.通过builder 创建一个对话框
     *  4.对话框show()出来
     */
    protected void showUpdataDialog() {
        AlertDialog.Builder builer = new AlertDialog.Builder(this) ;
        builer.setTitle("版本升级");
        builer.setMessage("发现新版本，是否更新？");
        //当点确定按钮时从服务器上下载 新的apk 然后安装
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                downLoadApk();
            }
        });
        //当点取消按钮时进行登录
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builer.create();
        dialog.show();
    }

    /*
 * 从服务器中下载APK
 */
    protected void downLoadApk() {
        pd=new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.setProgress(0);
        new Thread(){
            @Override
            public void run() {
                try {
                    File file = getFileFromServer(CommonTools.SERVERURL + "/update.php?operator=down");
                    sleep(3000);
                    installApk(file);
                    pd.dismiss();
                } catch (Exception e) {
                    handler.obtainMessage(DOWN_ERROR,"下载新版本失败").sendToTarget();
                    e.printStackTrace();
                }
            }}.start();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case UPDATA_CLIENT:
                    //对话框通知用户升级程序
                    showUpdataDialog();
                    break;
                case GET_UNDATAINFO_ERROR:
                    //服务器超时
                    Toast.makeText(getApplicationContext(), "获取服务器更新信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case DOWN_ERROR:
                    //下载apk失败
                    Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case NO_NEED_DOWN:
                    Toast.makeText(getApplicationContext(), "不需要更新", Toast.LENGTH_SHORT).show();
                    break;
                case SET_MAX:
                    pd.setMax(msg.arg1);
                    pd.show();
                    break;
                case SET_PROGRESS:
//                    Log.i("MAX",pd.getMax()+"");
                    if(msg.arg1<pd.getMax()) {
                        pd.setProgress(msg.arg1);
                    }else {
                        pd.dismiss();
                    }
                    break;
                case SET_WAIT:
                    pd.setTitle("正在检查更新……");
                    pd.show();
                    break;
                case SET_STOP:
                    pd.dismiss();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
