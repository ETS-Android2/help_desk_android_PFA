package com.example.myapplication.ui.fragments.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.UsersAdapter;
import com.example.myapplication.databinding.FragmentUsersListBinding;
import com.example.myapplication.models.UsersItem;
import com.example.myapplication.ui.MainActivity;
import com.example.myapplication.ui.fragments.BaseFragment;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.UserRole;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UsersListFragment extends BaseFragment {
    FragmentUsersListBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= FragmentUsersListBinding.inflate(inflater);
        getBaseActivity().showLoadingProgressDialog(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUsersList();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.users_list_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add_user:
                Bundle data=new Bundle();
                data.putString(Constants.FRAGMENT_ACTION_KEY,Constants.ACTION_ADD);
                ((MainActivity)getActivity()).getNavController().navigate(R.id.action_add_user,data);
                break;
        }
        return true;
    }
    private void loadUsersList(){

        Ion.with(getActivity()).load("GET",
                Constants.API_URL_BASE+"usersList")
                .as(new TypeToken<List<UsersItem>>(){}).withResponse().setCallback(new FutureCallback<Response<List<UsersItem>>>() {
            @Override
            public void onCompleted(Exception e, Response<List<UsersItem>> result) {
                if(e!=null)
                {
                    e.printStackTrace();
                    getBaseActivity().showSnackBar("Error: "+e.getClass().getSimpleName());
                    getBaseActivity().showLoadingProgressDialog(false);
                    return;
                }
                if (result.getHeaders().code() == 200) {
                    binding.recyclerView.setAdapter(new UsersAdapter((MainActivity) getActivity(),result.getResult()));

                }
                getBaseActivity().showLoadingProgressDialog(false);
            }
        });



    }
}
