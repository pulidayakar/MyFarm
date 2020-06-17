package com.daya.myfarm.viewModels;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.daya.myfarm.roomDatabase.LocationTask;
import com.daya.myfarm.roomDatabase.AppDataBase;
import com.daya.myfarm.roomDatabase.DatabaseClient;
import com.daya.myfarm.roomDatabase.LocationDao;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
       /* DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locationDao.deleteAll();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    LocationTask task = ds.getValue(LocationTask.class);
                    locationDao.insert(task);
                }
                getAllListData = locationDao.getAllData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Firebase Reference", "getUser:onCancelled", databaseError.toException());
                getAllListData = locationDao.getAllData();
            }
        });*/
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.orderByChild("name").equalTo(locationTask.getName());
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase database", "onCancelled", databaseError.toException());
            }
        });
    }

    public class AddLocation extends AsyncTask<LocationTask, Void, LocationTask>{
        @Override
        protected LocationTask doInBackground(LocationTask... locationTasks) {
            locationDao.insert(locationTasks[0]);
            return locationTasks[0];
        }

        @Override
        protected void onPostExecute(LocationTask locationTask) {
            super.onPostExecute(locationTask);
            message.postValue("Location Added successfully");
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            database.push().setValue(locationTask);
        }
    }
    public class AddLocationToLocalDb extends AsyncTask<LocationTask, Void, LocationTask>{
        @Override
        protected LocationTask doInBackground(LocationTask... locationTasks) {
            locationDao.insert(locationTasks[0]);
            return locationTasks[0];
        }

        @Override
        protected void onPostExecute(LocationTask locationTask) {
            super.onPostExecute(locationTask);
        }
    }
}
