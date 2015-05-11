package com.example.NewsLooker.bean;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.example.NewsLooker.DetailsActivity;
import com.example.NewsLooker.R;
import com.example.NewsLooker.adapter.NewsAdapter;
import com.example.NewsLooker.util.CommonTools;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {
	private final static String TAG = "NewsFragment";
	Activity activity;
	List<NewsInfo> newsList = new ArrayList<NewsInfo>();
	NewsListView mListView;
	NewsAdapter mAdapter;
	String text;
	int newsnum;
	ImageView detail_loading;
	public final static int SET_NEWSLIST = 0;
	//Toast提示框
	private RelativeLayout notify_view;
	private TextView notify_view_text;

	private final static String url= CommonTools.SERVERURL+"getNewsinfo.php";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Bundle args = getArguments();
		text = args != null ? args.getString("text") : "";
		initData();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
	}
	/** 此方法意思为fragment是否可见 ,可见时候加载数据 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			//fragment可见时加载数据
			if(newsList !=null && newsList.size() !=0){
				handler.obtainMessage(SET_NEWSLIST).sendToTarget();
			}else{
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(2);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						handler.obtainMessage(SET_NEWSLIST).sendToTarget();
					}
				}).start();
			}
		}else{
			//fragment不可见时不执行操作
		}
		super.setUserVisibleHint(isVisibleToUser);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.news_fragment, null);
		mListView = (NewsListView) view.findViewById(R.id.mListView);
		TextView item_textview = (TextView)view.findViewById(R.id.item_textview);
		detail_loading = (ImageView)view.findViewById(R.id.detail_loading);
		//Toast提示框
		notify_view = (RelativeLayout)view.findViewById(R.id.notify_view);
		notify_view_text = (TextView)view.findViewById(R.id.notify_view_text);
		item_textview.setText(text);
		return view;
	}

	private void initData() {
		if (isOpenNetwork()) {
			AsyncHttpClient client=new AsyncHttpClient();
			client.get(url+"?count=0&dir=up", new JsonHttpResponseHandler() {

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
                            newsList.add(t);
							object=null;
                            t=null;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(activity.getApplicationContext(),"数据获取失败...",Toast.LENGTH_LONG).show();
                }
            });
		} else {
			Toast.makeText(activity.getApplicationContext(),"未打开网络连接...",Toast.LENGTH_LONG).show();
		}
		newsnum=newsList.size();
	}

	private boolean isOpenNetwork() {
		ConnectivityManager connectivityManager= (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivityManager.getActiveNetworkInfo()!=null){
			return connectivityManager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SET_NEWSLIST:
				detail_loading.setVisibility(View.GONE);
				if(mAdapter == null){
					mAdapter = new NewsAdapter(activity, newsList);
				}
				mListView.setAdapter(mAdapter);
				mListView.setOnRefreshListener(new NewsListView.OnRefreshListener() {
					@Override
					public void onRefresh() {
						// Do work to refresh the list here.
						new GetDataTask().execute(newsnum);
					}
				});
				mListView.setOnLoadMoreListener(new NewsListView.OnLoadMoreListener() {
					@Override
					public void onLoadMore() {
//						new LoadDataTask().execute(newsnum-newsList.size());
					}
				});
				mListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = new Intent(activity, DetailsActivity.class);
                        intent.putExtra("news",newsList.get(position-1));
						intent.putExtra("tag","main");
                        startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

					}
				});
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private class GetDataTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected String doInBackground(Integer... params) {
			Document doc=null;
			try {
				doc= Jsoup.connect(url+"?count="+params[0]+"&dir=up").get();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return doc.body().text().toString();
		}

		@Override
		protected void onPostExecute(String data) {
			// Call onRefreshComplete when the list has been refreshed.
			int updatenum=0;
			try {
				JSONArray jsonArray=new JSONArray(data);
				NewsInfo t = null;
				JSONObject object = null;
				updatenum=jsonArray.length();
				for (int i = 0; i < updatenum; i++) {
					t = new NewsInfo();
					object = jsonArray.getJSONObject(i);
					t.setTitle(object.getString("title"));
					t.setType(object.getString("type"));
					t.setTime(object.getString("time"));
					t.setHitsnum(object.getInt("hitsnum"));
					t.setContent_url(object.getString("content_url"));
					t.setPic_url(object.getString("pic_url"));
					t.setFrom(object.getString("from"));
					t.setReadstatus(false);
					newsList.add(t);
					object = null;
					t = null;
				}

				newsnum+=updatenum;
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if(newsList.size()>30){
				int num=newsList.size()-30;
				for (int i=0;i<num;i++){
					newsList.remove(i);
				}
			}
			mAdapter.notifyDataSetChanged();
			mListView.onRefreshComplete();
			initNotify(updatenum);
			super.onPostExecute(data);
		}
	}

	private class LoadDataTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected String doInBackground(Integer... params) {
			Document doc=null;
			try {
				doc= Jsoup.connect(url+"?count="+params[0]+"&dir=down").get();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return doc.body().text().toString();
		}

		@Override
		protected void onPostExecute(String data) {
			// Call onRefreshComplete when the list has been refreshed.
			try {
				JSONArray jsonArray=new JSONArray(data);
				NewsInfo t = null;
				JSONObject object = null;

				for (int i = 0; i < jsonArray.length(); i++) {
					t = new NewsInfo();
					object = jsonArray.getJSONObject(i);
					t.setTitle(object.getString("title"));
					t.setType(object.getString("type"));
					t.setTime(object.getString("time"));
					t.setHitsnum(object.getInt("hitsnum"));
					t.setContent_url(object.getString("content_url"));
					t.setPic_url(object.getString("pic_url"));
					t.setFrom(object.getString("from"));
					t.setReadstatus(false);
					newsList.add(t);
					object = null;
					t = null;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(newsList.size()>30){
				int num=newsList.size()-30;
				for (int i=0;i<num;i++){
					newsList.remove(i);
				}
			}
			mAdapter.notifyDataSetChanged();
			mListView.onLoadMoreComplete();

			super.onPostExecute(data);
		}
	}

	/* 初始化通知栏目*/
	private void initNotify(final int count) {
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(count==0){
					notify_view_text.setText("当前没有更新了");
				}else {
					notify_view_text.setText(String.format("发现%1$d条更新", count));
				}
				notify_view.setVisibility(View.VISIBLE);
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						notify_view.setVisibility(View.GONE);
					}
				}, 2000);
			}
		}, 1000);
	}
	/* 摧毁视图 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mAdapter = null;
	}
}
