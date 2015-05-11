package com.example.NewsLooker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.example.NewsLooker.util.CommonTools;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;

import java.util.List;

public class LoginActivity extends Activity {

    private EditText userName, password;
    private CheckBox rem_pw, auto_login;
    private Button btn_login;
    private String userNameValue,passwordValue;
    private TextView register;
    private List<Object> list;
    private ProgressDialog pd;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去除标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.applogin);

        //获得实例对象
        userName = (EditText) findViewById(R.id.et_zh);
        password = (EditText) findViewById(R.id.et_mima);
        rem_pw = (CheckBox) findViewById(R.id.cb_mima);
        auto_login = (CheckBox) findViewById(R.id.cb_auto);
        btn_login = (Button) findViewById(R.id.btn_login);

        list= CommonTools.getCacheUserinfo(this);

        //判断记住密码多选框的状态
        if(Boolean.parseBoolean(list.get(4).toString()))
        {
            //设置默认是记录密码状态
            rem_pw.setChecked(true);
            userName.setText(list.get(1).toString());
            password.setText(list.get(2).toString());
            //判断自动登陆多选框状态
            if(Boolean.parseBoolean(list.get(5).toString()))
            {
                //设置默认是自动登录状态
                auto_login.setChecked(true);
                //跳转界面
                list.set(0,true);
                setResult(3);//3:成功  4：失败  5：放弃
                finish();
            }
        }

        //监听记住密码多选框按钮事件
        rem_pw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (rem_pw.isChecked()) {
                    list.set(4,true);
                }else {
                    list.set(4, false);
                }
            }
        });

        //监听自动登录多选框事件
        auto_login.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (auto_login.isChecked()) {
                    list.set(5, true);
                } else {
                    list.set(5, false);
                }
            }
        });

        register= (TextView) findViewById(R.id.register);

    }

    @Override
    public void finish() {
        CommonTools.putCacheUserinfo(this,list);
        super.finish();
        super.onDestroy();
    }

    public void onLoginClick(View v){
        switch (v.getId()){
            case R.id.btn_login:
                userNameValue = userName.getText().toString();
                passwordValue = password.getText().toString();

                if(userNameValue.trim().equals("")||userNameValue==null){
                    Toast.makeText(this,"用户名未填写",Toast.LENGTH_LONG).show();
                    return;
                }
                if(passwordValue.trim().equals("")||passwordValue==null){
                    Toast.makeText(this,"密码未填写",Toast.LENGTH_LONG).show();
                    return;
                }

                pd=ProgressDialog.show(this, "", "正在登陆", true, true);
                String url=CommonTools.SERVERURL+"LoginVaild.php?username="+userNameValue+"&password="+passwordValue;
                AsyncHttpClient client=new AsyncHttpClient();
                client.get(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        String result=new String(bytes);
                        if (result.startsWith("success")) {
                            Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_LONG).show();

                            if(rem_pw.isChecked())
                            {
                                //记住用户名、密码、
                                list.set(1,userNameValue);
                                list.set(2,passwordValue);
                            }
                            list.set(0,true);
                            list.set(6,result.split("\\|")[1]);
                            LoginActivity.this.setResult(3);
                            LoginActivity.this.finish();

                        } else if(result.equals("failure")){
                            Toast.makeText(LoginActivity.this,"登陆失败，请重新尝试",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(LoginActivity.this,"登陆失败，请检查网络或重新尝试",Toast.LENGTH_LONG).show();
                    }
                });
                pd.dismiss();
                break;
            case R.id.register:
                Intent intent=new Intent(this,RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
