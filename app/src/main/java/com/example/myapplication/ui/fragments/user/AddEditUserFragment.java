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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class AddEditUserFragment extends BaseFragment implements View.OnClickListener {
    String TAG= AddEditUserFragment.class.getSimpleName();
    AddEditUserFragBinding binding;
    UsersItem selectedUserItem;
    String mAction;
    String[] roles;
    int activeRoleSpinnerPosition=-1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding=AddEditUserFragBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.saveBtn.setOnClickListener(this);
        Log.v(TAG,"action "+getArguments().get(Constants.FRAGMENT_ACTION_KEY));
        mAction=getArguments().getString(Constants.FRAGMENT_ACTION_KEY);
        roles=new String[]{"Admin","Support","Client"};
        ArrayAdapter ad
                = new ArrayAdapter(
                getBaseActivity(),
                android.R.layout.simple_spinner_item, Arrays.asList(roles));

        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        binding.userRoleSpinner.setAdapter(ad);
        if(mAction.equals(Constants.ACTION_ADD)){
            setTitle(getString(R.string.add_user));
            binding.userIdEt.setVisibility(View.GONE);
            binding.userRoleSpinner.setSelection(getRolePositionByRoleValue(Integer.parseInt(UserRole.USER)));
            //do something

        }else if(mAction.equals(Constants.ACTION_EDIT)){
            selectedUserItem= (UsersItem) getArguments().getSerializable(Constants.SELECTED_ITEM_KEY);
            setTitle(getString(R.string.menu_edit_user)+" "+selectedUserItem.getName());
            binding.userIdEt.setText("#"+ selectedUserItem.getId());
            binding.userEmailEt.setText(selectedUserItem.getEmail());
            binding.userNameEt.setText(selectedUserItem.getName());
            binding.userPhoneEt.setText(selectedUserItem.getPhone_number());
            binding.userPasswordEt.setText(selectedUserItem.getPassword());
            int p=getRolePositionByRoleValue(Integer.parseInt(selectedUserItem.getRole_as()));
            binding.userRoleSpinner.setSelection(p);
        }

    }

    @Override
    public void onClick(View view) {
        //so save btn is clicked
        getBaseActivity().log("mAction="+mAction+" ticket_add="+Constants.ACTION_ADD);
        getBaseActivity().showLoadingProgressDialog(true);
        if(mAction.equals(Constants.ACTION_ADD)){
            Ion.with(this).load("POST",
                    Constants.API_SIGN_UP)
                    .addQuery("email",binding.userEmailEt.getText().toString())
                    .addQuery("username",binding.userNameEt.getText().toString())
                    .addQuery("password",binding.userPasswordEt.getText().toString())
                    .addQuery("role_as",""+getRoleValueFromSpinnerPosition(binding.userRoleSpinner.getSelectedItemPosition()))
                    .addQuery("phone_number",binding.userPhoneEt.getText().toString())
                    .asString()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<String>>() {

                        @Override
                        public void onCompleted(Exception e, Response<String> result) {
                            if(e!=null)
                            {
                                getBaseActivity().log("error:"+e.getCause()+" !");
                                e.printStackTrace();
                                return;
                            }
                            if (result.getHeaders().code() == 200) {
                                try {
                                    JSONObject res = new JSONObject(result.getResult());
                                    if (res.getString("message").equals("User Created")) {
                                        //success
                                        getBaseActivity().log("user created !");
                                        ((MainActivity)getBaseActivity()).onBackPressed();
                                    } else {
                                        getBaseActivity().showSnackBar(res.getString("message"));
                                        getBaseActivity().log("error:" + res.getString("message"));
                                    }
                                } catch (JSONException jsonException) {
                                    jsonException.printStackTrace();
                                    getBaseActivity().showSnackBar("error:" + jsonException.toString());
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                    getBaseActivity().showSnackBar("error:" + ex.getMessage());
                                }
                            }
                            getBaseActivity().showLoadingProgressDialog(false);

                        }
                    });

        }else if(mAction.equals(Constants.ACTION_EDIT)){
            UsersItem user=new UsersItem();
            user=new Gson().fromJson(new Gson().toJson(selectedUserItem),UsersItem.class);
            user.setName(binding.userNameEt.getText().toString());
            user.setEmail(binding.userEmailEt.getText().toString());
            user.setPhone_number(binding.userPhoneEt.getText().toString());
            user.setPassword(binding.userPasswordEt.getText().toString());
            user.setRole_as(String.valueOf(getRoleValueFromSpinnerPosition(binding.userRoleSpinner.getSelectedItemPosition())));

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
    }
    public static int getRolePositionByRoleValue(int roleInt){
        return roleInt-1;
    }
    public static int getRoleValueFromSpinnerPosition(int position){
        return position+1;
    }
}
