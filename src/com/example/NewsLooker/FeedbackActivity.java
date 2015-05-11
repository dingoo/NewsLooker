package com.example.NewsLooker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.NewsLooker.util.CommonTools;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;

import java.net.URLEncoder;
import java.util.List;

public class FeedbackActivity extends Activity{

    private List userinfo=null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);

        userinfo=CommonTools.getCacheUserinfo(this);
        Button feedback=(Button)findViewById(R.id.feedback_submit);
        final EditText fbtext=(EditText)findViewById(R.id.feedback_content);
        final Spinner spinner= (Spinner) findViewById(R.id.feedback_type_spinner);

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content= fbtext.getText().toString().trim();

                if(content.equals(null)||content.equals(" ")||content.equals("")){
                    Toast.makeText(FeedbackActivity.this, "请输入反馈内容！", Toast.LENGTH_LONG).show();
                }else {
                    String type=spinner.getSelectedItem().toString();
                    String userid="-1";
                    if(userinfo.get(0).toString()=="true"){
                        userid=userinfo.get(6).toString();
                    }
                    String path= CommonTools.SERVERURL+"feedbackdeal.php?userid="+userid+
                            "&type="+URLEncoder.encode(type)+"&content="+URLEncoder.encode(content);
                    Log.d("FEEDBACK",path);
                    AsyncHttpClient client=new AsyncHttpClient();
                    client.get(path, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            String result=new String(bytes);
                            Log.d("FEEDBACKRES",result);
                            if(result.equals("success")){
                                Toast.makeText(FeedbackActivity.this,"反馈成功，谢谢您的帮助！",Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(FeedbackActivity.this,"服务器异常",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Toast.makeText(FeedbackActivity.this, "反馈失败！", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}
