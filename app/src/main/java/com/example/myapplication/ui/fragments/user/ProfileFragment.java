package com.example.myapplication.ui.fragments.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.adapters.UsersAdapter;
import com.example.myapplication.databinding.AddEditUserFragBinding;
import com.example.myapplication.databinding.UserProfileFragBinding;
import com.example.myapplication.models.UsersItem;
import com.example.myapplication.ui.MainActivity;
import com.example.myapplication.ui.fragments.BaseFragment;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.UserRole;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends BaseFragment implements View.OnClickListener {
    String TAG= ProfileFragment.class.getSimpleName();
    UserProfileFragBinding binding;
    UsersItem selectedUserItem;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding=UserProfileFragBinding.inflate(inflater);
        return binding.getRoot();
        //application programming interface
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.saveBtn.setOnClickListener(this);
        getBaseActivity().showLoadingProgressDialog(true);
        loadUserData();

    }

    @Override
    public void onClick(View view) {
        UsersItem user=new UsersItem();
        user=new Gson().fromJson(new Gson().toJson(selectedUserItem),UsersItem.class);
        user.setName(binding.userNameEt.getText().toString());
        user.setEmail(binding.userEmailEt.getText().toString());
        user.setPhone_number(binding.userPhoneEt.getText().toString());
        user.setPassword(binding.userPasswordEt.getText().toString());


        //update btn is clicked
        getBaseActivity().showLoadingProgressDialog(true);
        Ion.with(getActivity()).load("PATCH",
                Constants.API_URL_BASE+"userUpdate/"+selectedUserItem.getId())
                .setJsonPojoBody(user)
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e!=null)
                {
                    e.printStackTrace();
                    getBaseActivity().showSnackBar("Error: "+e.getClass().getSimpleName());
                    getBaseActivity().showLoadingProgressDialog(false);
                    return;
                }
                if (result.getHeaders().code() == 200) {
                    //so changes are saved
                    getBaseActivity().showSnackBar("user changes saved!");
                }else {
                    try {
                        JSONObject obj=new JSONObject(result.getResult());
                        if(obj.has("detail")){
                            String msg=obj.getJSONArray("detail").getJSONObject(0).getString("msg");
                            getBaseActivity().showSnackBar(msg);
                        }
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                        getBaseActivity().showSnackBar(jsonException.getMessage());
                    }
                }
                getBaseActivity().showLoadingProgressDialog(false);

            }
        });
        }
    private void loadUserData(){
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
                            boolean found=false;
                            for(UsersItem user: result.getResult()){
                                    if(user.getId().equals(getBaseActivity().getLoggedInUserId())){
                                        selectedUserItem=user;
                                        found=true;
                                        insertUserInUi(user);
                                    }
                            }
                            if(!found){
                                //user is not found
                                getBaseActivity().showSnackBar("user not found");
                                getBaseActivity().onBackPressed();
                            }else{
                                //user is found

                            }
                        };
                        getBaseActivity().showLoadingProgressDialog(false);

                    }

                });
    }

    private void insertUserInUi(UsersItem u){
        binding.userEmailEt.setText(u.getEmail());
        binding.userNameEt.setText(u.getName());
        binding.userPasswordEt.setText(u.getPassword());
        binding.userPhoneEt.setText(u.getPhone_number());
    }
}

