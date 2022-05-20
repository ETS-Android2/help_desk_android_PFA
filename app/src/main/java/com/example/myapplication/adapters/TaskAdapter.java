package com.example.myapplication.adapters;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.TaskListItemBinding;
import com.example.myapplication.models.TaskItem;
import com.example.myapplication.ui.MainActivity;
import com.example.myapplication.utils.Constants;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONObject;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<TaskItem> items;
    private MainActivity activity;
    private final String TAG= TaskAdapter.class.getSimpleName();
    public TaskAdapter(MainActivity activity, List<TaskItem> items){
        this.items =items;
        this.activity=activity;
    }
    public void setItems(List<TaskItem> items){
        this.items =items;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TaskListItemBinding binding=TaskListItemBinding.inflate(LayoutInflater.from(parent.getContext()));
        ViewHolder viewHolder=new ViewHolder(binding);
        viewHolder.binding=binding;
        viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskItem item=items.get(position);
        holder.binding.taskIdTv.setText("#"+ item.getId());
        holder.binding.taskNameTv.setText( item.getName_task());
        holder.binding.taskDescriptionTv.setText( item.getDescription_task());

        holder.binding.statusTv.setText(Boolean.parseBoolean(item.getStatus())?R.string.done:R.string.not_done);
        holder.binding.deleteBtn.setTag(new Integer(position));
        holder.binding.deleteBtn.setOnClickListener((v)->{
            activity.showLoadingProgressDialog(true);
            Log.d(TAG, "delete:"+v.getTag());
            Ion.with(activity).load("DELETE",
                    Constants.API_URL_BASE+"taskDelete/"+item.getId())
                    .as(new TypeToken<JSONObject>(){}).withResponse().setCallback(new FutureCallback<Response<JSONObject>>() {
                @Override
                public void onCompleted(Exception e, Response<JSONObject> result) {
                    if(e!=null)
                    {
                        e.printStackTrace();
                        activity.showSnackBar("Error: "+e.getClass().getSimpleName());
                        activity.showLoadingProgressDialog(false);
                        return;
                    }
                    if (result.getHeaders().code() == 200) {
                        items.remove(item);
                        notifyItemRemoved((Integer) v.getTag());
                        activity.showSnackBar("task deleted successfully!");
                    }
                    activity.showLoadingProgressDialog(false);
                }
            });

        });
        holder.binding.editBtn.setTag(new Integer(position));
        holder.binding.editBtn.setOnClickListener((v)->{
            Log.d(TAG, "edit:"+v.getTag());
            Bundle params=new Bundle();
            params.putString(Constants.FRAGMENT_ACTION_KEY,Constants.ACTION_EDIT);
            params.putSerializable(Constants.SELECTED_ITEM_KEY, items.get((Integer) v.getTag()));
            activity.getNavController().navigate(R.id.action_edit_task,params);
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TaskListItemBinding binding;
        public ViewHolder(TaskListItemBinding binding){
            super(binding.getRoot());

        }
    }
}
