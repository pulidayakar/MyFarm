package com.daya.myfarm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.daya.myfarm.callBacks.ListClickCallBack;
import com.daya.myfarm.databinding.ActivityViewFarmBinding;
import com.daya.myfarm.roomDatabase.LocationTask;
import com.daya.myfarm.viewModels.LocationViewModel;

public class ViewFarmActivity extends AppCompatActivity implements ListClickCallBack {
    private MyFarmAdapter adapter;
    private ActivityViewFarmBinding binding;
    private Context context;
    private LocationViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_farm);
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(LocationViewModel.class);
        adapter = new MyFarmAdapter(context, this);
        binding.rvList.setLayoutManager(new LinearLayoutManager(context));
        binding.rvList.setAdapter(adapter);
        viewModel.getAllLocationData().observe(this, list -> adapter.setList(list));
    }

    @Override
    public void onLongClick(LocationTask task) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Are you sure want to delete?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", (dialog, which) -> viewModel.deleteSingleItem(task));
        alertDialog.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
    }

    @Override
    public void onClick(LocationTask locationTask) {
        startActivity(new Intent(context, ViewFormMapsActivity.class).putExtra(ViewFormMapsActivity.LOCATION_DATA, locationTask));
    }

   /* private void getData() {
        class getData extends AsyncTask<Void, Void, List<LocationTask>>{
            @Override
            protected List<LocationTask> doInBackground(Void... voids) {
                return DatabaseClient.getInstance(context)
                        .getAppDatabase()
                        .locationDao()
                        .getAllData();
            }

            @Override
            protected void onPostExecute(List<LocationTask> locationTasks) {
                super.onPostExecute(locationTasks);
                adapter = new MyFarmAdapter(locationTasks, context);
                binding.rvList.setLayoutManager(new LinearLayoutManager(context));
                binding.rvList.setAdapter(adapter);
            }
        }
        new getData().execute();
    }*/
}
