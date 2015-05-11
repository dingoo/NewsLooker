package com.example.NewsLooker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;

public class RegisterActivity extends Activity{

    private EditText et_un;
    private EditText et_pw;
    private EditText et_cpw;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        et_un= (EditText) findViewById(R.id.et_un);
        et_pw= (EditText) findViewById(R.id.et_pw);
        et_cpw= (EditText) findViewById(R.id.et_cpw);
    }

    @Override
    public void finish() {
        super.finish();
        onDestroy();
    }

    public void onRegClick(View view){
        switch (view.getId()){
            case R.id.btn_reg:
                String un=et_un.getText().toString();
                String pw=et_pw.getText().toString();
                String cpw=et_cpw.getText().toString();
                if(un.trim().equals("")||un==null){
                    Toast.makeText(this,"用户名未填写",Toast.LENGTH_LONG).show();
                    return;
                }
                if(pw.trim().equals("")||pw==null){
                    Toast.makeText(this,"密码未填写",Toast.LENGTH_LONG).show();
                    return;
                }
                if(cpw.trim().equals("")||cpw==null){
                    Toast.makeText(this,"确认密码未填写",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!pw.equals(cpw)){
                    Toast.makeText(this,"两次密码不一致，请重新填写",Toast.LENGTH_LONG).show();
                    et_pw.setText("");
                    et_cpw.setText("");
                    return;
                }else {
                    pd=ProgressDialog.show(this, "", "正在注册", true, true);
                    String url="http://192.168.1.102/NewsServer/datadeal/Register.php?username="+un+"&password="+pw;
                    AsyncHttpClient client=new AsyncHttpClient();
                    client.get(url, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            String result=new String(bytes);
                            if (result.equals("success")) {
                                Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_LONG).show();
                                try{
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                RegisterActivity.this.startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                RegisterActivity.this.finish();
                                RegisterActivity.this.onDestroy();
                            } else if(result.equals("failure")){
                                Toast.makeText(RegisterActivity.this,"注册失败，请重新尝试",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Toast.makeText(RegisterActivity.this,"注册失败，请检查网络或重新尝试",Toast.LENGTH_LONG).show();
                        }
                    });
                    pd.dismiss();
                }
                break;
            case R.id.btn_reset:
                et_un.setText("");
                et_pw.setText("");
                et_cpw.setText("");
                break;
            default:
                break;
        }
    }
}
