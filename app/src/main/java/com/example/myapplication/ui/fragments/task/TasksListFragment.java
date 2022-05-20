package com.example.myapplication.ui.fragments.task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.ProjectsAdapter;
import com.example.myapplication.adapters.TaskAdapter;
import com.example.myapplication.databinding.FragmentTasksListBinding;
import com.example.myapplication.models.ProjectItem;
import com.example.myapplication.models.TaskItem;
import com.example.myapplication.ui.MainActivity;
import com.example.myapplication.ui.fragments.BaseFragment;
import com.example.myapplication.utils.Constants;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.ArrayList;
import java.util.List;

public class TasksListFragment extends BaseFragment {
    ProjectItem selectedProject;
    FragmentTasksListBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= FragmentTasksListBinding.inflate(inflater);
        selectedProject = (ProjectItem) getArguments().getSerializable(Constants.SELECTED_ITEM_KEY);
        getBaseActivity().setTitle(getString(R.string.menu_manage_tasks)+" for project "+selectedProject.getName_project());

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.tasks_list_menu,menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasksForThisProject();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add_task:
                Bundle data=new Bundle();
                data.putString(Constants.FRAGMENT_ACTION_KEY,Constants.ACTION_ADD);
                data.putSerializable(Constants.SELECTED_ITEM_KEY,selectedProject);
                ((MainActivity)getActivity()).getNavController().navigate(R.id.action_add_task_to_project,data);
                break;
        }
        return true;
    }
    private void loadTasksForThisProject(){
        getBaseActivity().showLoadingProgressDialog(true);
        Ion.with(getActivity()).load("GET",
                Constants.API_URL_BASE+"projectTasks/"+selectedProject.getId())
                .as(new TypeToken<ArrayList<TaskItem>>(){}).withResponse().setCallback(new FutureCallback<Response<ArrayList<TaskItem>>>() {
            @Override
            public void onCompleted(Exception e, Response<ArrayList<TaskItem>> result) {
                if(e!=null)
                {
                    e.printStackTrace();
                    getBaseActivity().showSnackBar("Error: "+e.getClass().getSimpleName());
                    getBaseActivity().showLoadingProgressDialog(false);
                    return;
                }
                if (result.getHeaders().code() == 200) {
                    binding.recyclerView.setAdapter(new TaskAdapter((MainActivity) getActivity(),result.getResult()));
                }
                getBaseActivity().showLoadingProgressDialog(false);
            }
        });

    }
}
