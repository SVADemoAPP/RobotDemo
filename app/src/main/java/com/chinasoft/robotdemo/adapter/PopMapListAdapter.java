package com.chinasoft.robotdemo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chinasoft.robotdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XHF on 2018/12/3.
 */

public class PopMapListAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mapList = new ArrayList();
    private PopMapListAdapter.OnMaplistClickListener onMaplistClickListener;
    private int num = -1;
    private class ViewHolder {
        public TextView tv_name;

    }

    public PopMapListAdapter(Context mContext, List<String> mapList) {
        this.mContext = mContext;
        this.mapList = mapList;
    }

    public void setOnMaplistClickListener(PopMapListAdapter.OnMaplistClickListener onMaplistClickListener) {
        this.onMaplistClickListener = onMaplistClickListener;
    }

    public void setMapList(List<String> mapList) {
        this.mapList = mapList;
    }

    public int getCount() {
        return this.mapList.size();
    }

    public Object getItem(int arg0) {
        return this.mapList.get(arg0);
    }

    public long getItemId(int arg0) {
        return (long) arg0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        PopMapListAdapter.ViewHolder holder;
        if (convertView == null) {
            holder = new PopMapListAdapter.ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pop_maplist_layout, parent, false);
            holder.tv_name = (TextView) convertView.findViewById(R.id.maplist_item);
            convertView.setTag(holder);
        } else {
            holder = (PopMapListAdapter.ViewHolder) convertView.getTag();
        }
        final TextView textView = holder.tv_name;
        String name = mapList.get(position);
        if (position == num) {
            textView.setTextColor(mContext.getResources().getColor(R.color.blue_pop));
        } else {
            textView.setTextColor(Color.WHITE);
        }
        textView.setText(name);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMaplistClickListener.click(mapList.get(position));
                num = position;
                textView.setTextColor(mContext.getResources().getColor(R.color.blue_pop));
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    public interface OnMaplistClickListener {
        void click(String map);
    }

    private void setDefault() {

    }
}
