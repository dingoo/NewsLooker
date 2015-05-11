package com.example.NewsLooker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.renren.Renren;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import com.example.NewsLooker.util.CommonTools;

import java.util.HashMap;
import java.util.List;

public class SettingActivity extends Activity implements
        PlatformActionListener,Handler.Callback{

    private static final int MSG_USERID_FOUND = 1;
    private static final int MSG_LOGIN = 2;
    private static final int LOGIN_SEC = 3;
    private static final int LOGIN_FAIL= 4;
    private static final int LOGIN_QUIT = 5;

    private Platform platform;
    private RelativeLayout suclogin,loginapp;
    private List<Object> list;
    private boolean islogin=false;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ShareSDK.initSDK(this);
        list= CommonTools.getCacheUserinfo(this);
        suclogin= (RelativeLayout)findViewById(R.id.succeedlogin);
        loginapp= (RelativeLayout)findViewById(R.id.loginapp);
        tv=(TextView)findViewById(R.id.nichen);

        boolean auto_login=Boolean.parseBoolean(list.get(5).toString());

        if(auto_login&&!list.get(1).equals(null)&&!list.get(2).equals(null)){
            islogin=true;
        }

        if(islogin){
            suclogin.setVisibility(View.VISIBLE);
            loginapp.setVisibility(View.GONE);

            tv.setText(list.get(1).toString());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case LOGIN_FAIL:
                Toast.makeText(this,"登录失败", Toast.LENGTH_LONG).show();
                break;
            case LOGIN_QUIT:
                Toast.makeText(this,"未登录", Toast.LENGTH_LONG).show();
                break;
            case LOGIN_SEC:
                list=CommonTools.getCacheUserinfo(this);
                suclogin.setVisibility(View.VISIBLE);
                loginapp.setVisibility(View.GONE);
                tv.setText(list.get(1).toString());
                islogin=true;
                break;
        }
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.applogin:
                startActivityForResult(new Intent(this, LoginActivity.class),0);
                break;
            case R.id.sinalogin:
                platform= ShareSDK.getPlatform(SinaWeibo.NAME);
                authorize(platform);
                break;
            case R.id.qqlogin:
                platform= ShareSDK.getPlatform(QQ.NAME);
                authorize(platform);
                break;
            case R.id.weixinlogin:
                platform= ShareSDK.getPlatform(Wechat.NAME);
                authorize(platform);
                break;
            case R.id.renrenlogin:
                platform= ShareSDK.getPlatform(Renren.NAME);
                authorize(platform);
                break;
            case R.id.logoutbtn:
                suclogin.setVisibility(View.GONE);
                loginapp.setVisibility(View.VISIBLE);
                list.set(0, false);
                list.set(5,false);
                CommonTools.putCacheUserinfo(this, list);
                Toast.makeText(this,"您已经退出！",Toast.LENGTH_LONG).show();
                islogin=false;
                break;
            case R.id.collection:
                if(islogin){
                    startActivity(new Intent(this, CollectionActivity.class));
                }else{
                    Toast.makeText(this,R.string.notloginmark,Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.remark:
                if(islogin){
                    startActivity(new Intent(this, RemarkActivity.class));
                }else{
                    Toast.makeText(this,R.string.notloginmark,Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.feedback:
                startActivity(new Intent(this, FeedbackActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            default:
                break;
        }
    }

    public void onComplete(Platform plat, int action,
                           HashMap<String, Object> res) {
        Message msg = new Message();
        msg.arg1 = 1;
        msg.arg2 = action;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);
    }

    public void onError(Platform plat, int action, Throwable t) {
        t.printStackTrace();

        Message msg = new Message();
        msg.arg1 = 2;
        msg.arg2 = action;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);
    }

    public void onCancel(Platform plat, int action) {
        Message msg = new Message();
        msg.arg1 = 3;
        msg.arg2 = action;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);
    }

    private void authorize(Platform plat) {
        if (plat == null) {
            return;
        }

        if(plat.isValid()) {
            String userId = plat.getDb().getUserId();
            if (!TextUtils.isEmpty(userId)) {
                UIHandler.sendEmptyMessage(MSG_USERID_FOUND, this);
                login(plat.getName(), userId, null);
                return;
            }
        }

        plat.setPlatformActionListener(this);
        // true不使用SSO授权，false使用SSO授权
        plat.SSOSetting(true);
        //显示用户资料，null表示显示自己的资料
        plat.showUser(null);
    }

    /**
     * 处理操作结果
     * 如果获取到用户的名称，则显示名称；否则如果已经授权，则显示
     *平台名称
     */
    public boolean handleMessage(Message msg) {
        Platform plat = (Platform) msg.obj;
        String text = actionToString(msg.arg2);
        switch (msg.arg1) {
            case 1: {
                // 成功
                text = plat.getName() + " completed at " + text;
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            }
            break;
            case 2: {
                // 失败
                text = plat.getName() + " caught error at " + text;
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                return false;
            }
            case 3: {
                // 取消
                text = plat.getName() + " canceled at " + text;
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return false;
    }

    /** 将action转换为String */
    public static String actionToString(int action) {
        switch (action) {
            case Platform.ACTION_AUTHORIZING: return "ACTION_AUTHORIZING";
            case Platform.ACTION_GETTING_FRIEND_LIST: return "ACTION_GETTING_FRIEND_LIST";
            case Platform.ACTION_FOLLOWING_USER: return "ACTION_FOLLOWING_USER";
            case Platform.ACTION_SENDING_DIRECT_MESSAGE: return "ACTION_SENDING_DIRECT_MESSAGE";
            case Platform.ACTION_TIMELINE: return "ACTION_TIMELINE";
            case Platform.ACTION_USER_INFOR: return "ACTION_USER_INFOR";
            case Platform.ACTION_SHARE: return "ACTION_SHARE";
            default: {
                return "UNKNOWN";
            }
        }
    }

    private void login(String plat, String userId, HashMap<String, Object> userInfo) {
        Message msg = new Message();
        msg.what = MSG_LOGIN;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);
    }
}
