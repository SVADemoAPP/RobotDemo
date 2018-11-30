package com.chinasoft.robotdemo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinasoft.robotdemo.R;

import java.util.ArrayList;
import java.util.List;

public class UserIdAdapter extends BaseAdapter {
    private ViewHolder holder;
    private Context mContext;
    private List<String> userIdList = new ArrayList();
private  OnUserIdListener onUserIdListener;

    public void setOnUserIdListener(OnUserIdListener onUserIdListener) {
        this.onUserIdListener = onUserIdListener;
    }

    public UserIdAdapter(Context mContext, List<String> userIdList) {
        this.mContext = mContext;
        this.userIdList = userIdList;
    }


    public int getCount() {
            return userIdList.size();
    }


    public void setUserIdList(List<String> userIdList) {
        this.userIdList = userIdList;
    }

    public Object getItem(int position) {
        return userIdList.get(position);
    }

    public long getItemId(int arg0) {
        return (long) arg0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

                holder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.item_userid_show, null);
                holder.tv_userid=convertView.findViewById(R.id.tv_userid);
                holder.rl_delete=convertView.findViewById(R.id.rl_delete);
                convertView.setTag(holder);
        } else {
                holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_userid.setText(userIdList.get(position));
holder.rl_delete.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        onUserIdListener.delete(position);
    }
});
        return convertView;
    }

    private class ViewHolder{
        public TextView tv_userid;
        public RelativeLayout rl_delete;

    }

    public  interface  OnUserIdListener{
        void delete(int position);
    }

}
