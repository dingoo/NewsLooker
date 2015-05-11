package com.example.NewsLooker;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.TabHost;

public class MainActivity extends TabActivity {

    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        tabHost=this.getTabHost();
        TabHost.TabSpec spec;

        spec=tabHost.newTabSpec("新闻主页").setIndicator("新闻主页").setContent(new Intent().setClass(this,MainNewsActivity.class));
        tabHost.addTab(spec);

        spec=tabHost.newTabSpec("搜索").setIndicator("搜索").setContent(new Intent().setClass(this,SearchNewsActivity.class));
        tabHost.addTab(spec);

        spec=tabHost.newTabSpec("我的").setIndicator("我的").setContent(new Intent().setClass(this,SettingActivity.class));
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);

        RadioGroup radioGroup=(RadioGroup) this.findViewById(R.id.main_tab_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.main_tab_home:
                        tabHost.setCurrentTabByTag("新闻主页");
                        break;
                    case R.id.main_tab_search:
                        tabHost.setCurrentTabByTag("搜索");
                        break;
                    case R.id.main_tab_setting:
                        tabHost.setCurrentTabByTag("我的");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private long mTime; //用来判断是否满足按两下返回键推出应用的时间间隔

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
//        if(keyCode==KeyEvent.KEYCODE_BACK){
//            if ((System.currentTimeMillis() - mTime) > 2000) {
//                Toast.makeText(this, "在按一次退出", Toast.LENGTH_SHORT).show();
//                mTime = System.currentTimeMillis();
//            } else {
//                finish();
//            }
//        }

        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("你确定退出吗？")
                    .setCancelable(false)
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    finish();
                                    System.exit(0);
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }
        return super.dispatchKeyEvent(event);

    }
}
