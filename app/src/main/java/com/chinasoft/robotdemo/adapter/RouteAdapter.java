package com.chinasoft.robotdemo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinasoft.robotdemo.R;

import java.util.ArrayList;
import java.util.List;

public class RouteAdapter extends BaseAdapter {
    private ViewHolder holder;
    private Context mContext;
    private List<String> routeList = new ArrayList();
private  OnRouteListener onRouteListener;

    public void setOnRouteListener(OnRouteListener onRouteListener) {
        this.onRouteListener = onRouteListener;
    }

    public RouteAdapter(Context mContext, List<String> routeList) {
        this.mContext = mContext;
        this.routeList = routeList;
    }


    public int getCount() {
            return routeList.size();
    }


    public void setRouteList(List<String> routeList) {
        this.routeList = routeList;
    }

    public Object getItem(int position) {
        return routeList.get(position);
    }

    public long getItemId(int arg0) {
        return (long) arg0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

                holder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.item_route_show, null);
                holder.tv_route=convertView.findViewById(R.id.tv_route);
                holder.rl_delete=convertView.findViewById(R.id.rl_delete);
                convertView.setTag(holder);
        } else {
                holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_route.setText(routeList.get(position));
holder.rl_delete.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        onRouteListener.delete(position);
    }
});
        return convertView;
    }

    private class ViewHolder{
        public TextView tv_route;
        public RelativeLayout rl_delete;

    }

    public  interface  OnRouteListener{
        void delete(int position);
    }

}
