package com.example.NewsLooker.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.NewsLooker.R;
import com.example.NewsLooker.bean.NewsInfo;
import com.example.NewsLooker.util.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class NewsAdapter extends BaseAdapter{

	List<NewsInfo> newsList;
	Activity activity;
	LayoutInflater inflater = null;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	public NewsAdapter(Activity activity, List<NewsInfo> newsList) {
		this.activity = activity;
		this.newsList = newsList;
		inflater = LayoutInflater.from(activity);
		options = Options.getListOptions();
	}
	
	@Override
	public int getCount() {
		return newsList == null ? 0 : newsList.size();
	}

	@Override
	public NewsInfo getItem(int position) {
		if (newsList != null && newsList.size() != 0) {
			return newsList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.newsitem, null);
			mHolder = new ViewHolder();
			mHolder.item_layout = (LinearLayout)view.findViewById(R.id.item_layout);
			mHolder.item_title = (TextView)view.findViewById(R.id.item_title);
			mHolder.item_source = (TextView)view.findViewById(R.id.item_source);
			mHolder.comment_count = (TextView)view.findViewById(R.id.comment_count);
			mHolder.publish_time = (TextView)view.findViewById(R.id.publish_time);
			mHolder.item_abstract = (TextView)view.findViewById(R.id.item_abstract);
			mHolder.right_image = (ImageView)view.findViewById(R.id.right_image);
			mHolder.right_padding_view = (View)view.findViewById(R.id.right_padding_view);
			//头部的日期部分
			mHolder.layout_list_section = (LinearLayout)view.findViewById(R.id.layout_list_section);
			mHolder.section_text = (TextView)view.findViewById(R.id.section_text);
			mHolder.section_day = (TextView)view.findViewById(R.id.section_day);
			
			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		//获取position对应的数据
		NewsInfo news = getItem(position);
		mHolder.item_title.setText(news.getTitle());
		if(news.getFrom()==null||news.getFrom().trim()==""){
			mHolder.item_source.setText("未知来源");
		}else {
			mHolder.item_source.setText(news.getFrom());
		}
//		mHolder.comment_count.setText("评论:" + DataTool.getDataNum("comments",news.getNewid()));
		mHolder.comment_count.setText("评论:0");
		mHolder.publish_time.setText(news.getTime());
		String imgUrlList = news.getPic_url();
		mHolder.comment_count.setVisibility(View.VISIBLE);
		mHolder.right_padding_view.setVisibility(View.VISIBLE);
		if(imgUrlList !=null){
            imageLoader.displayImage(imgUrlList, mHolder.right_image, options);
		}else{
			mHolder.right_image.setVisibility(View.GONE);
		}

		//判断该新闻概述是否为空
		if (!TextUtils.isEmpty(news.getDesc())) {
			mHolder.item_abstract.setVisibility(View.VISIBLE);
			mHolder.item_abstract.setText(news.getDesc());
		} else {
			mHolder.item_abstract.setVisibility(View.GONE);
		}

		//判断该新闻是否已读
		mHolder.item_layout.setSelected(news.getReadstatus());
		return view;
	}

	static class ViewHolder {
		LinearLayout item_layout;
		//title
		TextView item_title;
		//图片源
		TextView item_source;
		//评论数量
		TextView comment_count;
		//发布时间
		TextView publish_time;
		//新闻摘要
		TextView item_abstract;
		//右边图片
		ImageView right_image;
		//评论布局
		RelativeLayout comment_layout;
		TextView comment_content;
		//paddingview
		View right_padding_view;
		
		//头部的日期部分
		LinearLayout layout_list_section;
		TextView section_text;
		TextView section_day;
	}

}
