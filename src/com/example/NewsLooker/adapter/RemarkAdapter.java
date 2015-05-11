package com.example.NewsLooker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.NewsLooker.R;

import java.util.List;
import java.util.Map;

public class RemarkAdapter extends BaseAdapter{

    List<Map<String,String>> infolist;
    Context context;
    LayoutInflater inflater = null;
    String tag;

    public RemarkAdapter(Context context,List<Map<String,String>> list,String tag){
        this.infolist=list;
        this.context=context;
        this.tag=tag;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return infolist==null?0:infolist.size();
    }

    @Override
    public Map<String,String> getItem(int position) {
        if(infolist!=null&&infolist.size()!=0){
            return infolist.get(position);
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
            mHolder = new ViewHolder();
            if (tag.equals("my")) {
                view = inflater.inflate(R.layout.remarkitem, null);
                mHolder.newstitle= (TextView) view.findViewById(R.id.tv_newstitle);
                mHolder.userremark= (TextView) view.findViewById(R.id.tv_userremark);
                mHolder.remarktime= (TextView) view.findViewById(R.id.tv_remarktime);
            } else if(tag.equals("main")){
                view = inflater.inflate(R.layout.remarkviewitem, null);
                mHolder.newstitle= (TextView) view.findViewById(R.id.tv_user);
                mHolder.userremark= (TextView) view.findViewById(R.id.tv_ur);
                mHolder.remarktime= (TextView) view.findViewById(R.id.tv_utime);
            }
            view.setTag(mHolder);
        }else {
            mHolder = (ViewHolder) view.getTag();
        }

        Map<String,String> item=getItem(position);
        if (tag.equals("my")) {
            mHolder.newstitle.setText(item.get("title"));
        } else if(tag.equals("main")){
            mHolder.newstitle.setText(item.get("username"));
        }
        mHolder.remarktime.setText(item.get("time"));
        mHolder.userremark.setText(item.get("content"));

        return view;
    }

    static class ViewHolder{
        TextView newstitle;
        TextView userremark;
        TextView remarktime;
    }
}
