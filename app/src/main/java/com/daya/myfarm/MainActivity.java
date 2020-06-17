package com.daya.myfarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.daya.myfarm.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.createFarm.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MapsActivity.class)));
        binding.viewFarm.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ViewFarmActivity.class)));
    }
}
