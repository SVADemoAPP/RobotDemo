package com.chinasoft.robotdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinasoft.robotdemo.R;
import com.chinasoft.robotdemo.bean.PrruModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XHF on 2018/11/19.
 */

public class PrruModelListAdapter extends BaseAdapter{
    private Context mContext;
    private List<PrruModel> mPrruModelList = new ArrayList();
    private  OnPrruModelListClickListener onPrruModelListClickListener;
    private ViewHolder holder;

    public PrruModelListAdapter(Context mContext, List<PrruModel> mPrruModelList) {
        this.mContext = mContext;
        this.mPrruModelList = mPrruModelList;
    }

    public void setPrruModelListClickListener(OnPrruModelListClickListener onPrruModelListClickListener) {
        this.onPrruModelListClickListener = onPrruModelListClickListener;
    }
    @Override
    public int getCount() {
        return mPrruModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPrruModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new PrruModelListAdapter.ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate( R.layout.layout_popup_list_choose_item, parent,false);
            holder.tv_id = convertView.findViewById(R.id.tv_id);
            holder.ll_pop_item = convertView.findViewById(R.id.ll_pop_item);
            convertView.setTag(holder);
        } else {
            holder = (PrruModelListAdapter.ViewHolder) convertView.getTag();
        }
        TextView textView = holder.tv_id;
        LinearLayout ll_item= holder.ll_pop_item;
        textView.setText(mPrruModelList.get(position).neId);
        ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPrruModelListClickListener.onClick(mPrruModelList.get(position));
            }
        });

        return convertView;
    }


    public interface OnPrruModelListClickListener{
        void onClick(PrruModel prruModel);
    }

    private class ViewHolder {
        public TextView tv_id;
        public LinearLayout ll_pop_item;

    }
}
