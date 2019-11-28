package com.example.android.mygarden;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.utils.PlantUtils;

public class PlantWateringService extends IntentService {

    public static final String ACTION_WATER_PLANTS = "com.example.android.mygarden.action.water_plants";
    public static final String ACTION_UPDATE_PLANTS = "com.example.android.mygarden.action.update_plants";

    public PlantWateringService() {
        super("PlantWateringService");
    }
    public static void startUpdateWidget(Context mcontext){
        Intent i = new Intent(mcontext,PlantWateringService.class);
        i.setAction(ACTION_UPDATE_PLANTS);
        mcontext.startService(i);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) return;
        if (intent.getAction().equals(ACTION_WATER_PLANTS)){
            handleActionWaterPlants();
        }
        else if (intent.getAction().equals(ACTION_UPDATE_PLANTS)){
            handleActionUpdatePlants();
        }
    }

    private void handleActionUpdatePlants() {
        Uri PLANTS_URI = PlantContract.BASE_CONTENT_URI.buildUpon().appendPath(PlantContract.PATH_PLANTS).build();
        Cursor cursor = getContentResolver().query(PLANTS_URI,null,null,null,PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);

        int imagRes = R.drawable.grass;
        if (cursor != null && cursor.moveToNext()){
            long timeNow = System.currentTimeMillis();
            long waterAt = cursor.getLong(cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME));
            long createdAt = cursor.getLong(cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME));
            int plantType = cursor.getInt(cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE));
            imagRes = PlantUtils.getPlantImageRes(this,timeNow - createdAt,timeNow - waterAt,plantType);
        }
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        int[] ids = manager.getAppWidgetIds(new ComponentName(this,PlantWidgetProvider.class));
        PlantWidgetProvider.updateWidgets(this,manager,imagRes,ids);
    }

    private void handleActionWaterPlants() {
        Uri PLANTS_URI = PlantContract.BASE_CONTENT_URI.buildUpon().appendPath(PlantContract.PATH_PLANTS).build();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME,System.currentTimeMillis());
        long time = System.currentTimeMillis() - PlantUtils.MAX_AGE_WITHOUT_WATER;
        getContentResolver().update(PLANTS_URI,contentValues,PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME + " > ?", new String[]{String.valueOf(time)});
    }
}
