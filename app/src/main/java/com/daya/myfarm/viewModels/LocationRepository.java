package com.daya.myfarm.viewModels;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daya.myfarm.roomDatabase.LocationTask;
import com.daya.myfarm.roomDatabase.AppDataBase;
import com.daya.myfarm.roomDatabase.DatabaseClient;
import com.daya.myfarm.roomDatabase.LocationDao;

import java.util.List;

public class LocationRepository {
    private LiveData<List<LocationTask>> getAllListData;
    private LocationDao locationDao;
    private MutableLiveData<String> message = new MutableLiveData<>();

    public LocationRepository(@NonNull Application application){
        AppDataBase appDataBase = DatabaseClient.getInstance(application).getAppDatabase();
        locationDao = appDataBase.locationDao();
        getAllListData = locationDao.getAllData();
    }

    public LiveData<List<LocationTask>> getGetAllData(){
        return getAllListData;
    }
    public void addLocation(LocationTask locationTask){
        new AddLocation().execute(locationTask);
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public void deleteData(LocationTask locationTask){
        locationDao.deletebyName(locationTask.getName());
    }

    public class AddLocation extends AsyncTask<LocationTask, Void, Void>{

        @Override
        protected Void doInBackground(LocationTask... locationTasks) {
            locationDao.insert(locationTasks[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            message.postValue("Location Added successfully");
        }
    }
}
