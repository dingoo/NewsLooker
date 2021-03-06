package com.example.NewsLooker;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.NewsLooker.bean.NewsInfo;
import com.example.NewsLooker.util.CommonTools;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RemarkActivity extends BaseActivity{

    private List userinfo=null;
    private List<NewsInfo> newsInfos=new ArrayList<NewsInfo>();
    private ListView collectionlv=null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myremark);

        findViewById(R.id.title_bar).findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView tv_title=(TextView)findViewById(R.id.title_bar).findViewById(R.id.title);
        tv_title.setText(R.string.myremark);
        tv_title.setGravity(Gravity.CENTER_HORIZONTAL);

        collectionlv= (ListView) findViewById(R.id.collectioncontent);

        userinfo= CommonTools.getCacheUserinfo(this);

        String path= CommonTools.SERVERURL+"getCollection.php?operator=getcolnews&userid="+userinfo.get(6);
        Log.d("Collection---path", path);
        AsyncHttpClient client=new AsyncHttpClient();
        client.get(path, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject object= null;
                    NewsInfo t=null;
                    try {
                        t=new NewsInfo();
                        object = response.getJSONObject(i);
                        t.setNewid(object.getInt("newid"));
                        t.setTitle(object.getString("title"));
                        t.setType(object.getString("type"));
                        t.setTime(object.getString("time"));
                        t.setHitsnum(object.getInt("hitsnum"));
                        t.setContent_url(object.getString("content_url"));
                        t.setPic_url(object.getString("pic_url"));
                        t.setFrom(object.getString("from"));
                        t.setReadstatus(false);
                        newsInfos.add(t);
                        object=null;
                        t=null;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(RemarkActivity.this, "���ݻ�ȡʧ��...", Toast.LENGTH_LONG).show();
            }
        });
    }
}
