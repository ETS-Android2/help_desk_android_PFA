package com.example.myapplication.ui.fragments.project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.databinding.AddEditProjectFragBinding;
import com.example.myapplication.models.ProjectItem;
import com.example.myapplication.models.TicketItem;
import com.example.myapplication.ui.MainActivity;
import com.example.myapplication.ui.fragments.BaseFragment;
import com.example.myapplication.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.Calendar;

public class AddEditProjectFragment extends BaseFragment implements View.OnClickListener {
    String TAG= AddEditProjectFragment.class.getSimpleName();
    AddEditProjectFragBinding binding;
    ProjectItem selectedProjectItem;
    TicketItem selectedTicketItem;
    String mAction;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding=AddEditProjectFragBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.saveBtn.setOnClickListener(this);
        Log.v(TAG,"action "+getArguments().get(Constants.FRAGMENT_ACTION_KEY));
        mAction=getArguments().getString(Constants.FRAGMENT_ACTION_KEY);
        if(mAction.equals(Constants.ACTION_ADD)){
            binding.projectIdEt.setVisibility(View.GONE);
            binding.manageTasks.setVisibility(View.GONE);
            binding.statusParent.setVisibility(View.GONE);
            selectedTicketItem= (TicketItem) getArguments().getSerializable(Constants.SELECTED_ITEM_KEY);
            if(selectedTicketItem!=null){
                setTitle(getString(R.string.add_project)+" for #"+selectedTicketItem.getId());
                getBaseActivity().log("adding project for ticket "+selectedTicketItem.getId());
            }else{
                binding.saveBtn.setVisibility(View.GONE);
                getBaseActivity().showSnackBar("You cannot add a project to null ticket ");
                ((MainActivity)getBaseActivity()).getNavController().navigate(R.id.nav_tickets_list);
            }
        }else if(mAction.equals(Constants.ACTION_EDIT)){
            binding.manageTasks.setOnClickListener(this);
            Object o=getArguments().containsKey(Constants.SELECTED_ITEM_KEY);
            selectedProjectItem= (ProjectItem) getArguments().getSerializable(Constants.SELECTED_ITEM_KEY);

            if(selectedProjectItem!=null) {
                    binding.projectIdEt.setText("#" + selectedProjectItem.getId());
                    binding.projectDescriptionEt.setText(selectedProjectItem.getDescription_project());
                    binding.projectNameEt.setText(selectedProjectItem.getName_project());
                    binding.projectStatusCheckBox.setChecked(Boolean.parseBoolean(selectedProjectItem.getStatus()));
                }else if(selectedTicketItem !=null){
                    //do nothing


                }else{
                    getBaseActivity().showSnackBar("Error: no project or ticket specified!");
                }
            }

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==binding.saveBtn.getId()){
            getBaseActivity().log("mAction="+mAction+" ticket_add="+Constants.ACTION_ADD);
            if(mAction.equals(Constants.ACTION_ADD)){
                //getBaseActivity().showSnackBar("project added!");
                String url=Constants.API_URL_BASE+"projectAdd";
                Ion.with(this).load("POST",url
                        )
                        .addQuery("name_project",binding.projectNameEt.getText().toString())
                        .addQuery("description_project",binding.projectDescriptionEt.getText().toString())
                        .addQuery("id_ticket",selectedTicketItem.getId())
                        .asString()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<String>>() {

                            @Override
                            public void onCompleted(Exception e, Response<String> result) {
                                if(e!=null)
                                {
                                    getBaseActivity().log("error:"+e.getCause()+" !");
                                    getBaseActivity().showLoadingProgressDialog(false);
                                    e.printStackTrace();
                                    return;
                                }
                                if (result.getHeaders().code() == 200) {
                                    getBaseActivity().showSnackBar("project added !");
                                        getBaseActivity().onBackPressed();
                                    getBaseActivity().onBackPressed();
                                }else{
                                    getBaseActivity().showSnackBar("failed to add add project !");
                                }

                                getBaseActivity().showLoadingProgressDialog(false);

                            }
                        });

            }else
                if(mAction.equals(Constants.ACTION_EDIT)){
                ProjectItem projectToSend= new ProjectItem();
                projectToSend=new Gson().fromJson(new Gson().toJson(selectedProjectItem),ProjectItem.class);
                projectToSend.setName_project(binding.projectNameEt.getText().toString());
                projectToSend.setDescription_project(binding.projectDescriptionEt.getText().toString());
                projectToSend.setStatus(String.valueOf(binding.projectStatusCheckBox.isChecked()));
                Ion.with(getActivity()).load("PATCH",
                        Constants.API_URL_BASE+"projectUpdate/"+projectToSend.getId())
                        .setJsonPojoBody(projectToSend)
                        .asJsonObject().withResponse().setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if(e!=null)
                        {
                            e.printStackTrace();
                            getBaseActivity().showSnackBar("Error: "+e.getClass().getSimpleName());
                            getBaseActivity().showLoadingProgressDialog(false);
                            return;
                        }
                        if (result.getHeaders().code() == 200) {
                            //so changes are saved
                            getBaseActivity().showSnackBar("project changes saved!");

                        }else {
                            getBaseActivity().showSnackBar("failed to save changes");
                        }
                        getBaseActivity().showLoadingProgressDialog(false);
                    }
                });

            }
        }else if(view.getId()==binding.manageTasks.getId()){
            Bundle b=new Bundle();
            b.putSerializable(Constants.SELECTED_ITEM_KEY,selectedProjectItem);
            ((MainActivity)getBaseActivity()).getNavController().navigate(R.id.action_view_project_tasks,b);

        }
    }
}
