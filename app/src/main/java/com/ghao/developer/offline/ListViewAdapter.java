package com.ghao.developer.offline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class ListViewAdapter extends BaseAdapter {
    private List<Map<String,Object>> data;
    private LayoutInflater mLayoutInflater;
    private Context context;
    public ListViewAdapter(Context context,List<Map<String,Object>> data){
        this.context = context;
        this.data = data;
        this.mLayoutInflater = LayoutInflater.from(context);
    }
    public final class Assemble{
        public ImageView mImageView;
        public TextView mTextView;
        public Button mButton;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Assemble assemble;
        if(view == null){
            assemble = new Assemble();
            view = mLayoutInflater.inflate(R.layout.sync_listview,null);
            assemble.mImageView =(ImageView) view.findViewById(R.id.listview_image);
            assemble.mTextView = (TextView) view.findViewById(R.id.listview_textview);
            assemble.mButton = (Button) view.findViewById(R.id.listview_detail);
            view.setTag(assemble);
        }else{
            assemble = (Assemble) view.getTag();
        }
        assemble.mTextView.setText((String)data.get(i).get("title"));
        assemble.mImageView.setBackgroundResource((Integer) data.get(i).get("image"));
        return view;
    }
}
