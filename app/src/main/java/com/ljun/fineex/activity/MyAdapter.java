package com.ljun.fineex.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.ljun.fineex.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * .
 * Created by Juny on 2017/7/14 0014.
 */

public class MyAdapter extends DelegateAdapter.Adapter<MyAdapter.MainViewHolder> {

    private ArrayList<HashMap<String, Object>> listItem;
    // 用于存放数据列表

    private Context context;
    private LayoutHelper layoutHelper;
    private RecyclerView.LayoutParams layoutParams;
    private int count = 0;

    private MyItemClickListener myItemClickListener;

    //构造函数(传入每个的数据列表 & 展示的Item数量)
    public MyAdapter(Context context, LayoutHelper layoutHelper, int count,
                     ArrayList<HashMap<String, Object>> listItem) {
        this(context, layoutHelper, count, new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300),
                listItem);
    }

    public MyAdapter(Context context, LayoutHelper layoutHelper, int count,
                     @NonNull RecyclerView.LayoutParams layoutParams, ArrayList<HashMap<String, Object>> listItem) {
        this.context = context;
        this.layoutHelper = layoutHelper;
        this.count = count;
        this.layoutParams = layoutParams;
        this.listItem = listItem;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return layoutHelper;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        holder.Text.setText((String) listItem.get(position).get("ItemTitle"));
        holder.image.setImageResource((Integer) listItem.get(position).get("ItemImage"));
    }

    @Override
    public int getItemCount() {
        return count;
    }

    // 设置Item的点击事件
    // 绑定MainActivity传进来的点击监听器
    public void setOnItemClickListener(MyItemClickListener listener) {
        myItemClickListener = listener;
    }


    //定义Viewholder
    class MainViewHolder extends RecyclerView.ViewHolder {
        TextView Text;
        ImageView image;

        MainViewHolder(View root) {
            super(root);

            // 绑定视图
            Text = (TextView) root.findViewById(R.id.Item);
            image = (ImageView) root.findViewById(R.id.Image);

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myItemClickListener != null)
                        myItemClickListener.onItemClick(v, getAdapterPosition());
                }
            });

        }

        public TextView getText() {
            return Text;
        }
    }
}
