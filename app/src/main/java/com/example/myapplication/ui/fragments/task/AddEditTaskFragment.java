package com.example.myapplication.ui.fragments.task;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.databinding.AddEditTaskFragBinding;
import com.example.myapplication.models.ProjectItem;
import com.example.myapplication.models.TaskItem;
import com.example.myapplication.models.TicketItem;
import com.example.myapplication.ui.MainActivity;
import com.example.myapplication.ui.fragments.BaseFragment;
import com.example.myapplication.utils.Constants;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class AddEditTaskFragment extends BaseFragment implements View.OnClickListener {
    String TAG= AddEditTaskFragment.class.getSimpleName();
    AddEditTaskFragBinding binding;
    TaskItem selectedTaskItem;
    ProjectItem selectedProjectItem;
    String mAction;
    String[] taskStatues;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding=AddEditTaskFragBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            binding.saveBtn.setOnClickListener(this);
            Log.v(TAG,"action "+getArguments().get(Constants.FRAGMENT_ACTION_KEY));
            mAction=getArguments().getString(Constants.FRAGMENT_ACTION_KEY);

        if(mAction.equals(Constants.ACTION_ADD)){
            selectedProjectItem= (ProjectItem) getArguments().getSerializable(Constants.SELECTED_ITEM_KEY);
            setTitle(getString(R.string.add_ticket)+" for project:"+selectedProjectItem.getName_project());
            binding.taskIdEt.setVisibility(View.GONE);
            binding.taskStatusCheckBox.setEnabled(false);
            binding.statusParent.setVisibility(View.GONE);
            //do something
        }else if(mAction.equals(Constants.ACTION_EDIT)){
            //do something else
            selectedTaskItem= (TaskItem) getArguments().getSerializable(Constants.SELECTED_ITEM_KEY);
            binding.taskIdEt.setText("#"+selectedTaskItem.getId());
            binding.taskDescriptionEt.setText(selectedTaskItem.getDescription_task());
            binding.taskNameEt.setText(selectedTaskItem.getName_task());
            binding.taskStatusCheckBox.setChecked(Boolean.parseBoolean(selectedTaskItem.getStatus()));

        }

    }

    @Override
    public void onClick(View view) {
        if(mAction.equals(Constants.ACTION_ADD)){
            getBaseActivity().showLoadingProgressDialog(true);
            String url=Constants.API_URL_BASE+"taskAdd";
            Ion.with(this).load("POST",url
            )
                    .addQuery("name_task",binding.taskNameEt.getText().toString())
                    .addQuery("description_task",binding.taskDescriptionEt.getText().toString())
                    .addQuery("id_project",selectedProjectItem.getId())
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
                                getBaseActivity().showSnackBar("task added !");
                                getBaseActivity().onBackPressed();
                            }else{
                                getBaseActivity().showSnackBar("failed to add add task !");
                            }

                            getBaseActivity().showLoadingProgressDialog(false);

                        }
                    });

        }else if(mAction.equals(Constants.ACTION_EDIT)){
            getBaseActivity().showLoadingProgressDialog(true);
            TaskItem taskToSend=new Gson().fromJson(new Gson().toJson(selectedTaskItem),TaskItem.class);
            taskToSend.setStatus(String.valueOf(binding.taskStatusCheckBox.isChecked()));
            taskToSend.setDescription_task(binding.taskDescriptionEt.getText().toString());
            taskToSend.setName_task(binding.taskNameEt.getText().toString());
            String url=Constants.API_URL_BASE+"taskUpdate/"+selectedTaskItem.getId();
            Ion.with(this).load("PATCH",url
            )
                    .setJsonPojoBody(taskToSend)
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
                                getBaseActivity().showSnackBar("task changes saved!");
                                getBaseActivity().onBackPressed();
                            }else{
                                getBaseActivity().showSnackBar("failed to save task changes!");
                            }

                            getBaseActivity().showLoadingProgressDialog(false);

                        }
                    });

        }
    }
}
