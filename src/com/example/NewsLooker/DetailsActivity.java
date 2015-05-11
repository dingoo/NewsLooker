package com.example.NewsLooker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.example.NewsLooker.bean.NewsInfo;
import com.example.NewsLooker.util.CommonTools;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;

import java.net.URLEncoder;
import java.util.List;

@SuppressLint("JavascriptInterface")
public class DetailsActivity extends BaseActivity {
	private TextView title;
	private ProgressBar progressBar;
	private String news_url;
	private String news_title;
	private String news_source;
	private String news_date;
	private NewsInfo news;
	private TextView action_comment_count;
	WebView webView;
	private String tag;
	private boolean iscollection=false;

	private TextView back;
	private ImageView favor,wcomment,vcomment,share,report;
	private List userinfolist=null;
	private AsyncHttpClient client=new AsyncHttpClient();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);
		setNeedBackGesture(true);//设置需要手势监听
		getData();
		initView();
		initWebView();
	}
	/* 获取传递过来的数据 */
	private void getData() {
		news = (NewsInfo) getIntent().getSerializableExtra("news");
		tag=getIntent().getStringExtra("tag");

		news_url = news.getContent_url();
		news_title = news.getTitle();
		news_source = news.getFrom();
		news_date = news.getTime();

		userinfolist=CommonTools.getCacheUserinfo(this);
	}

	private void initWebView() {
		webView = (WebView)findViewById(R.id.wb_details);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		if (!TextUtils.isEmpty(news_url)) {
			WebSettings settings = webView.getSettings();
			settings.setJavaScriptEnabled(true);//设置可以运行JS脚本
//			settings.setTextZoom(120);//Sets the text zoom of the page in percent. The default is 100.
			settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
			settings.setUseWideViewPort(true); //打开页面时， 自适应屏幕
//			settings.setLoadWithOverviewMode(true);//打开页面时， 自适应屏幕 
			settings.setSupportZoom(true);// 用于设置webview放大
			settings.setBuiltInZoomControls(false);
			webView.setBackgroundResource(R.color.transparent);
			webView.setWebChromeClient(new MyWebChromeClient());
			webView.setWebViewClient(new MyWebViewClient());
			new MyAsnycTask().execute(news_url, news_title, news_source + " " +news_date);
		}
	}

	private void initView() {

		progressBar = (ProgressBar) findViewById(R.id.ss_htmlprogessbar);
//		customview_layout = (FrameLayout) findViewById(R.id.customview_layout);
		//底部栏目
		title = (TextView)findViewById(R.id.title_bar).findViewById(R.id.title);
		back= (TextView) findViewById(R.id.title_bar).findViewById(R.id.back);

		MyOnClickListener myOnClickListener=new MyOnClickListener(this);
		back.setOnClickListener(myOnClickListener);

		if (tag.equals("main")) {
			action_comment_count = (TextView) findViewById(R.id.tool_bar).findViewById(R.id.action_comment_count);
			favor= (ImageView) findViewById(R.id.tool_bar).findViewById(R.id.action_favor);

			String path=CommonTools.SERVERURL + "getCollection.php?newsid=" + news.getNewid()
					+ "&usersid=" + userinfolist.get(6)+"&operator=query";
			Log.d("PATH",path);
			client.get(path, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int i, Header[] headers, byte[] bytes) {
					String result=new String(bytes);
					Log.d("RESULT",result);
					if(Boolean.parseBoolean(result)){
						iscollection=true;
						favor.setImageResource(R.drawable.ic_action_favor_on_pressed);
					}
				}

				@Override
				public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
					Log.d("RESULTFAILURE","获取收藏状态失败");
				}
			});

			wcomment= (ImageView) findViewById(R.id.tool_bar).findViewById(R.id.action_write_comment);
			vcomment= (ImageView) findViewById(R.id.tool_bar).findViewById(R.id.action_view_comment);
			share= (ImageView) findViewById(R.id.tool_bar).findViewById(R.id.action_share);
			report= (ImageView) findViewById(R.id.tool_bar).findViewById(R.id.action_report);

			//设置点击监听
			favor.setOnClickListener(myOnClickListener);
			wcomment.setOnClickListener(myOnClickListener);
			vcomment.setOnClickListener(myOnClickListener);
			share.setOnClickListener(myOnClickListener);
			report.setOnClickListener(myOnClickListener);

			path=CommonTools.SERVERURL + "getComments.php?newsid=" + news.getNewid() +"&operator=querynum";
			Log.d("PATH", path);
			client.get(path, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int i, Header[] headers, byte[] bytes) {
					String result=new String(bytes);
					Log.d("COMMENTSNUM",result);
					action_comment_count.setText(result);
				}

				@Override
				public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

				}
			});
		} else{
			findViewById(R.id.tool_bar).setVisibility(View.GONE);
		}

		progressBar.setVisibility(View.VISIBLE);
		title.setTextSize(13);
		title.setVisibility(View.VISIBLE);
		title.setText(news_url);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		finish();
		onDestroy();
	}

	class MyOnClickListener implements View.OnClickListener {

		Context context;

		public MyOnClickListener(Context c){
			this.context=c;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.back:
					onBackPressed();
					break;
				case R.id.action_favor:
					if(!judgeLoginState()){
						Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
						return;
					}

					String path=CommonTools.SERVERURL + "getCollection.php?newsid=" + news.getNewid()
							+ "&usersid=" + userinfolist.get(6)+"&operator=collect";
					Log.d("PATH",path);
					client.get(path, new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int i, Header[] headers, byte[] bytes) {
							String result=new String(bytes);
							if(result.equals("success")){
								iscollection=!iscollection;
								if(iscollection) {
									Toast.makeText(context, "已收藏", Toast.LENGTH_SHORT).show();
									favor.setImageResource(R.drawable.ic_action_favor_on_pressed);
								}else {
									Toast.makeText(context, "已取消收藏", Toast.LENGTH_SHORT).show();
									favor.setImageResource(R.drawable.ic_action_favor_normal);
								}
							}else{
								Toast.makeText(context, "设置失败", Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
							Toast.makeText(context, "收藏失败", Toast.LENGTH_SHORT).show();
						}
					});
					break;
				case R.id.action_write_comment:
					if(!judgeLoginState()){
						Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
						return;
					}
					final View view=((Activity)context).getLayoutInflater().inflate(R.layout.wirtecomment,null);
					final EditText comText= (EditText) view.findViewById(R.id.et_comment);
					AlertDialog.Builder builder=new AlertDialog.Builder(context);
					builder.setTitle("对新闻“" + news.title + "”的评论：")
							.setView(view)
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {

								}
							})
							.setPositiveButton("发表", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									String content= URLEncoder.encode(comText.getText().toString());
									String path=CommonTools.SERVERURL + "getComments.php?newsid=" + news.getNewid()
											+ "&usersid=" + userinfolist.get(6)+"&content="+ content
											+"&operator=insert";
									Log.d("PATH",path);
									client.get(path,new AsyncHttpResponseHandler() {
												@Override
												public void onSuccess(int i, Header[] headers, byte[] bytes) {
													String result=new String(bytes);
													Log.d("COMMENTSRESULT",result);
													if(result.equals("success")){
														Toast.makeText(context, "发表成功", Toast.LENGTH_SHORT).show();
													}else {
														Toast.makeText(context, "服务器错误，发表失败", Toast.LENGTH_SHORT).show();
													}
												}

												@Override
												public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
													Toast.makeText(context, "发表失败", Toast.LENGTH_SHORT).show();
												}
											});

								}
							}).create().show();
					break;
				case R.id.action_view_comment:
//					Toast.makeText(context,"查看评论",Toast.LENGTH_SHORT).show();
					break;
				case R.id.action_share:
					if(!judgeLoginState()){
						Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
						return;
					}
					Toast.makeText(context,"已分享",Toast.LENGTH_SHORT).show();
					break;
				case R.id.action_report:
					if(!judgeLoginState()){
						Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
						return;
					}
					Toast.makeText(context,"已发送到后台，感谢您的举报",Toast.LENGTH_SHORT).show();
					break;
				default:
					Toast.makeText(context,"点的啥玩意",Toast.LENGTH_SHORT).show();
					break;
			}
		}
	}

	private boolean judgeLoginState(){
		if(userinfolist!=null){
			return Boolean.parseBoolean(userinfolist.get(0).toString());
		}
		return false;
	}

	private class MyAsnycTask extends AsyncTask<String, String,String>{


		@Override
		protected String doInBackground(String... urls) {
			//String data= DataTool.getNewsDetails(urls[0], urls[1], urls[2]);
			return urls[0];
		}

		@Override
		protected void onPostExecute(String data) {
			//webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
			webView.loadUrl(data);
		}
	}

	// 注入js函数监听
	private void addImageClickListner() {
		// 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，在还是执行的时候调用本地接口传递url过去
		webView.loadUrl("javascript:(function(){"
				+ "var objs = document.getElementsByTagName(\"img\");"
				+ "var imgurl=''; " + "for(var i=0;i<objs.length;i++)  " + "{"
				+ "imgurl+=objs[i].src+',';"
				+ "    objs[i].onclick=function()  " + "    {  "
				+ "        window.imagelistner.openImage(imgurl);  "
				+ "    }  " + "}" + "})()");
	}

	// 监听
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			view.getSettings().setJavaScriptEnabled(true);
			super.onPageFinished(view, url);
			// html加载完成之后，添加监听图片的点击js函数
			addImageClickListner();
			progressBar.setVisibility(View.GONE);
			webView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			view.getSettings().setJavaScriptEnabled(true);
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			progressBar.setVisibility(View.GONE);
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}
	
	private class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if(newProgress != 100){
				progressBar.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}
	}
}
