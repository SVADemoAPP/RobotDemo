package com.chinasoft.robotdemo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chinasoft.robotdemo.R;

import java.util.ArrayList;
import java.util.List;

public class MaplistAdapter extends BaseAdapter {
    private String currentMap;
    private ViewHolder holder;
    private Context mContext;
    private List<String> mapList = new ArrayList();
    private OnMaplistClickListener onMaplistClickListener;

    private class ViewHolder {
        public TextView tv_name;

    }

    public void setCurrentMap(String currentMap) {
        this.currentMap = currentMap;
    }

    public MaplistAdapter(Context mContext, List<String> mapList) {
        this.mContext = mContext;
        this.mapList = mapList;
    }

    public void setOnMaplistClickListener(OnMaplistClickListener onMaplistClickListener) {
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(this.mContext, R.layout.item_maplist, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TextView textView = holder.tv_name;
        String name =  mapList.get(position);
        textView.setText(name);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMaplistClickListener.click(mapList.get(position));
            }
        });

        int i;
        if (currentMap.equals(name)) {
            i = R.mipmap.maplist_select;
        } else {
            i = R.mipmap.maplist_unselect;
        }
        textView.setBackgroundResource(i);
        return convertView;
    }

    public interface OnMaplistClickListener{
        void click(String map);
    }
}
