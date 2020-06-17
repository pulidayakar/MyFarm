package com.daya.myfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.daya.myfarm.callBacks.ListClickCallBack;
import com.daya.myfarm.databinding.ActivityViewFarmBinding;
import com.daya.myfarm.roomDatabase.LocationTask;
import com.daya.myfarm.viewModels.LocationViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ViewFarmActivity extends AppCompatActivity implements ListClickCallBack {
    private MyFarmAdapter adapter;
    private ActivityViewFarmBinding binding;
    private Context context;
    private LocationViewModel viewModel;
    private ArrayList<LocationTask> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_farm);
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(LocationViewModel.class);
        setSupportActionBar(binding.toolbar);
        adapter = new MyFarmAdapter(context, this);
        binding.rvList.setLayoutManager(new LinearLayoutManager(context));
        binding.rvList.setAdapter(adapter);
        viewModel.getAllLocationData().observe(this, list -> {
            this.list = (ArrayList<LocationTask>) list;
            adapter.setList(list);
        });
       // binding.viewAll.setOnClickListener(v -> startActivity(new Intent(context, ViewFormMapsActivity.class).putExtra(ViewFormMapsActivity.LOCATION_LIST_DATA, "all")));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemView) {
            startActivity(new Intent(context, ViewFormMapsActivity.class).putExtra(ViewFormMapsActivity.LOCATION_LIST_DATA, list));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onLongClick(LocationTask task) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Are you sure want to delete?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", (dialog, which) -> viewModel.deleteSingleItem(task));
        alertDialog.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    @Override
    public void onClick(LocationTask locationTask) {
        startActivity(new Intent(context, ViewFormMapsActivity.class).putExtra(ViewFormMapsActivity.LOCATION_DATA, locationTask));
    }

}
