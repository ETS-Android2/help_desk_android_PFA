package com.example.myapplication.ui.fragments.stats;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.databinding.FragmentStatsBinding;
import com.example.myapplication.models.TaskItem;
import com.example.myapplication.models.TicketItem;
import com.example.myapplication.ui.fragments.BaseFragment;
import com.example.myapplication.utils.Constants;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsFragment extends BaseFragment {
    FragmentStatsBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= FragmentStatsBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        getBaseActivity().showLoadingProgressDialog(true);
        loadTicketsStats();
        loadTaskssStats();
    }

    private void loadTicketsStats(){
        String url=Constants.API_URL_BASE+"ticketsList/"+getBaseActivity().getLoggedInUserId();
        Ion.with(getActivity()).load("GET",url
        )
                .as(new TypeToken<List<TicketItem>>(){}).withResponse().setCallback(new FutureCallback<Response<List<TicketItem>>>() {
            @Override
            public void onCompleted(Exception e, Response<List<TicketItem>> result) {
                if(e!=null)
                {
                    e.printStackTrace();
                    getBaseActivity().showSnackBar("Error: "+e.getClass().getSimpleName());
                    getBaseActivity().showLoadingProgressDialog(false);
                    return;
                }
                if (result.getHeaders().code() == 200) {
                    int resolved=0;
                    int notResolved=0;
                    for(TicketItem d:result.getResult())
                        if(Boolean.parseBoolean(d.getStatus()))
                            resolved++;
                        else
                            notResolved++;
                    updatePieChart(resolved,notResolved,binding.ticketsPieChartView);
                }
            }
        });
    }
    private void loadTaskssStats(){
            String url=Constants.API_URL_BASE+"tasksList";
            Ion.with(getActivity()).load("GET",url
            )
                    .as(new TypeToken<List<TaskItem>>(){}).withResponse().setCallback(new FutureCallback<Response<List<TaskItem>>>() {
                @Override
                public void onCompleted(Exception e, Response<List<TaskItem>> result) {
                    if(e!=null)
                    {
                        e.printStackTrace();
                        getBaseActivity().showSnackBar("Error: "+e.getClass().getSimpleName());
                        getBaseActivity().showLoadingProgressDialog(false);
                        return;
                    }
                    if (result.getHeaders().code() == 200) {
                        int resolved=0;
                        int notResolved=0;
                        for(TaskItem d:result.getResult())
                            if(Boolean.parseBoolean(d.getStatus()))
                                resolved++;
                            else
                                notResolved++;
                        updatePieChart(resolved,notResolved,binding.tasksPieChartView);
                    }
                    getBaseActivity().showLoadingProgressDialog(false);
                }
            });
        }

    private void updatePieChart(int resolved, int notResolved, PieChart pieChart){

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "";

        //initializing data
        Map<String, Integer> typeAmountMap = new HashMap<>();
        typeAmountMap.put("Resolved",resolved);
        typeAmountMap.put("In Progress",notResolved);


        //initializing colors for the entries
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#304567"));
        colors.add(Color.parseColor("#309967"));

        //input data and fit data into pie chart entry
        for(String type: typeAmountMap.keySet()){
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        //setting text size of the value
        pieDataSet.setValueTextSize(12f);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

}
