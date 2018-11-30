package com.chinasoft.robotdemo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chinasoft.robotdemo.R;

import java.util.ArrayList;
import java.util.List;

public class UserIdAdapter extends BaseAdapter {
    private ViewHolder1 holder1;
    private ViewHolder2 holder2;
    private Context mContext;
    private List<String> userIdList = new ArrayList();




    public UserIdAdapter(Context mContext, List<String> userIdList) {
        this.mContext = mContext;
        this.userIdList = userIdList;
    }


    public int getCount() {
        if(userIdList.size()<10) {
            return userIdList.size()+1;
        }else{
            return userIdList.size();
        }
    }

    public Object getItem(int position) {
        return userIdList.get(position);
    }

    public long getItemId(int arg0) {
        return (long) arg0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            if(position<userIdList.size()) {
                holder1 = new ViewHolder1();
                convertView = View.inflate(this.mContext, R.layout.item_userid_show, null);
                convertView.setTag(holder1);
            }else{
                holder2 = new ViewHolder2();
                convertView = View.inflate(this.mContext, R.layout.item_userid_add, null);
                convertView.setTag(holder2);
            }

        } else {
            if(convertView.getTag() instanceof ViewHolder1){
                holder1 = (ViewHolder1) convertView.getTag();
            }else{
                holder2 = (ViewHolder2) convertView.getTag();
            }

        }

        return convertView;
    }

    private class ViewHolder1 {
        public TextView tv_name;

    }

    private class ViewHolder2 {
        public TextView tv_name;

    }

}
