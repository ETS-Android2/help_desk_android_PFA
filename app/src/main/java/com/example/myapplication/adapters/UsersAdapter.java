package com.example.myapplication.adapters;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.UserListItemBinding;
import com.example.myapplication.models.UsersItem;
import com.example.myapplication.ui.MainActivity;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.UserRole;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private List<UsersItem> items;
    private MainActivity activity;
    private final String TAG= UsersAdapter.class.getSimpleName();
    public UsersAdapter(MainActivity activity, List<UsersItem> items){
        this.items =items;
        this.activity=activity;
    }
    public void setItems(List<UsersItem> items){
        this.items =items;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserListItemBinding binding=UserListItemBinding.inflate(LayoutInflater.from(parent.getContext()));
        ViewHolder viewHolder=new ViewHolder(binding);
        viewHolder.binding=binding;
        viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UsersItem item=items.get(position);
        holder.binding.userIdTv.setText("#"+ item.getId());
        holder.binding.userEmailTv.setText( item.getEmail());
        holder.binding.userPhoneTv.setText( item.getPhone_number());
        holder.binding.userNameTv.setText( item.getName());
        holder.binding.userRoleTv.setText( UserRole.asString(item.getRole_as()));
        holder.binding.deleteBtn.setTag(new Integer(position));
        holder.binding.deleteBtn.setOnClickListener((v)->{
            Log.d(TAG, "delete:"+v.getTag());
            activity.showLoadingProgressDialog(true);
            Ion.with(activity).load("DELETE",
                    Constants.API_URL_BASE+"userDelete/"+item.getId())
                    .asString()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<String>>() {

                        @Override
                        public void onCompleted(Exception e, Response<String> result) {
                            if(e!=null)
                            {
                               activity.log("error:"+e.getCause()+" !");
                                e.printStackTrace();
                                return;
                            }
                            if (result.getHeaders().code() == 200) {

                                activity.log("user deleted !");
                                items.remove(((Integer)v.getTag()).intValue());
                                notifyDataSetChanged();
                            }else{
                                activity.showSnackBar("failed to delete user");   
                            }
                           activity.showLoadingProgressDialog(false);

                        }
                    });;
            notifyDataSetChanged();
            activity.showSnackBar("user deleted");
        });
        holder.binding.editBtn.setTag(new Integer(position));
        holder.binding.editBtn.setOnClickListener((v)->{
            Log.d(TAG, "edit:"+v.getTag());
            Bundle params=new Bundle();
            params.putString(Constants.FRAGMENT_ACTION_KEY,Constants.ACTION_EDIT);
            params.putSerializable(Constants.SELECTED_ITEM_KEY, items.get((Integer) v.getTag()));
            activity.getNavController().navigate(R.id.action_edit_user,params);
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public UserListItemBinding binding;
        public ViewHolder(UserListItemBinding binding){
            super(binding.getRoot());

        }
    }
}
