package com.example.NewsLooker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.example.NewsLooker.adapter.NewsAdapter;
import com.example.NewsLooker.bean.NewsInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchNewsActivity extends Activity {

    private EditText searchtext;
    private NewsAdapter newsAdapter;
    private List<NewsInfo> newsInfos = null;
    private ProgressDialog proDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchpage);

        searchtext = (EditText) findViewById(R.id.searchtext);
        Button searchbtn = (Button) findViewById(R.id.searchbtn);
        ListView lv = (ListView) findViewById(R.id.searchcontent);
        newsInfos = new ArrayList<NewsInfo>();
        newsAdapter = new NewsAdapter(SearchNewsActivity.this, newsInfos);
        lv.setAdapter(newsAdapter);

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String keyword = searchtext.getText().toString();
                if (keyword.trim().equals("") || keyword == null) {
                    Toast.makeText(SearchNewsActivity.this, "请输入关键词", Toast.LENGTH_LONG).show();
                    return;
                }

                proDialog = ProgressDialog.show(SearchNewsActivity.this, "正在搜索..",
                        "连接中..请稍后....", true, true);
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
                        (SearchNewsActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                try {
                    String str = URLEncoder.encode(searchtext.getText().toString(), "utf-8");
                    asyncHttpClient.get("http://news.baidu.com/ns?word=title" + str +
                            "&tn=newsfcu&from=news&cl=2&rn=30&ct=0", new AsyncHttpResponseHandler() {

                        Document doc = null;

//                        String picurl=null;
//                        boolean flag=false;
                        @Override
                        public void onSuccess(int code, Header[] headers, byte[] bytes) {
                            newsInfos.clear();
                            try {
                                doc = Jsoup.parse(new String(bytes, "gb2312"));

                                char c = 160;

                                if (doc != null) {
                                    Elements ela = doc.select("div.baidu a");
                                    Elements elspan = doc.select("div.baidu span");

                                    for (int i = 0; i < ela.size() - 1; i++) {
                                        NewsInfo newsInfo = new NewsInfo();
                                        newsInfo.setTitle(ela.get(i).text().replace("原标题:", ""));//1
                                        newsInfo.setContent_url(ela.get(i).attr("href"));//2
                                        String span = elspan.get(i).text();
                                        int index = span.indexOf(c);
                                        newsInfo.setFrom(span.substring(0, index));//3
                                        newsInfo.setTime(span.substring(index + 1, span.length() - 1));//4
                                        newsInfo.setHitsnum(0);//6
                                        newsInfo.setReadstatus(false);//7
                                        newsInfos.add(newsInfo);
                                    }
                                }
                                proDialog.dismiss();
                                newsAdapter.notifyDataSetChanged();
                                if (newsInfos.size() == 0) {
                                    Toast.makeText(SearchNewsActivity.this, "未搜索到数据...", Toast.LENGTH_LONG).show();
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Toast.makeText(SearchNewsActivity.this, "网路获取出现问题...", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsInfo newsInfo = newsInfos.get(position);
                Intent intent = new Intent(SearchNewsActivity.this, DetailsActivity.class);
                intent.putExtra("news", newsInfo);
                intent.putExtra("tag","search");
                SearchNewsActivity.this.startActivity(intent);
                SearchNewsActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
}
