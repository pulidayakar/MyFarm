package com.daya.myfarm.callBacks;

import com.daya.myfarm.roomDatabase.LocationTask;

public interface ListClickCallBack {
    void onLongClick(LocationTask task);
    void onClick(LocationTask locationTask);
}
