package com.daya.myfarm;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.daya.myfarm.callBacks.ListClickCallBack;
import com.daya.myfarm.databinding.MyfarmLayoutBinding;
import com.daya.myfarm.roomDatabase.LocationTask;

import java.util.List;

public class MyFarmAdapter extends RecyclerView.Adapter<MyFarmAdapter.ViewHolder> {
    private List<LocationTask> list;
    private Context context;
    private ListClickCallBack callBack;
    public MyFarmAdapter(Context context, ListClickCallBack callBack) {
        this.context = context;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public MyFarmAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyfarmLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.myfarm_layout, parent, false);
        return new ViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull MyFarmAdapter.ViewHolder holder, int position) {
        holder.onBind(list.get(position));
    }
    public void setList(List<LocationTask> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        MyfarmLayoutBinding binding;
        public ViewHolder(@NonNull MyfarmLayoutBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
        public void onBind(LocationTask locationTask) {
            binding.tvName.setText(locationTask.getName());
            itemView.setOnClickListener(v -> callBack.onClick(locationTask));
            itemView.setOnLongClickListener(v -> {
                callBack.onLongClick(locationTask);
                return true;
            });
        }
    }
}
