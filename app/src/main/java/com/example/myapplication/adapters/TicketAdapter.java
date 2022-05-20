package com.example.myapplication.adapters;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.TicketListItemBinding;
import com.example.myapplication.models.TicketItem;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.ui.MainActivity;
import com.example.myapplication.utils.UserRole;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONObject;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {
    private boolean isNormalUser=false;
    private List<TicketItem> items;
    private MainActivity activity;
    private final String TAG=TicketAdapter.class.getSimpleName();
    public TicketAdapter(MainActivity activity,List<TicketItem> items){
        this.items =items;
        this.activity=activity;
        isNormalUser=activity.getLoggedInUserRole().equals(UserRole.USER);
    }
    public void setItems(List<TicketItem> items){
        this.items =items;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TicketListItemBinding binding=TicketListItemBinding.inflate(LayoutInflater.from(parent.getContext()));
        ViewHolder viewHolder=new ViewHolder(binding);
        viewHolder.binding=binding;
        viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TicketItem item=items.get(position);
        holder.binding.ticketIdTv.setText("#"+ item.getId());
        holder.binding.ticketNameTv.setText( item.getName_ticket());
        holder.binding.ticketDexcriptionTv.setText( item.getDescription_ticket());
        holder.binding.ticketDateTv.setText(item.getDate_ticket());
        boolean isDone=Boolean.parseBoolean(item.getStatus());
        holder.binding.statusTv.setText(isDone?R.string.done:R.string.not_done);
        holder.binding.deleteBtn.setTag(new Integer(position));
        holder.binding.deleteBtn.setOnClickListener((v)->{
            Log.d(TAG, "delete:"+v.getTag());
            activity.showLoadingProgressDialog(true);
            Ion.with(activity).load("DELETE",
                    Constants.API_URL_BASE+"deleteticket/"+item.getId())
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
                        activity.showSnackBar("ticket deleted successfully!");
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
            TicketItem t=items.get((Integer) v.getTag());
            params.putSerializable(Constants.SELECTED_ITEM_KEY, t);
            activity.getNavController().navigate(R.id.action_edit_ticket,params);
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TicketListItemBinding binding;
        public ViewHolder(TicketListItemBinding binding){
            super(binding.getRoot());
            binding.deleteBtn.setVisibility(isNormalUser? View.INVISIBLE:View.VISIBLE);
            binding.editBtn.setVisibility(isNormalUser? View.GONE:View.VISIBLE);

        }
    }
}
