package com.daya.myfarm.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daya.myfarm.roomDatabase.LocationTask;
import com.daya.myfarm.viewModels.LocationRepository;

import java.util.List;

public class LocationViewModel extends AndroidViewModel {
    private LiveData<List<LocationTask>> getAllData;
    private MutableLiveData<String> message;
    private LocationRepository locationRepository;
    public LocationViewModel(@NonNull Application application) {
        super(application);
        locationRepository = new LocationRepository(application);
        getAllData = locationRepository.getGetAllData();
        message = locationRepository.getMessage();
    }
    public LiveData<List<LocationTask>> getAllLocationData(){
        return getAllData;
    }

    public void addLocation(LocationTask locationTask){
        locationRepository.addLocation(locationTask);
    }

    public void deleteSingleItem(LocationTask locationTask){
        locationRepository.deleteData(locationTask);
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }
}
