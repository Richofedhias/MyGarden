package com.example.android.mygarden;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android.mygarden.ui.MainActivity;

public class PlantWidgetProvider extends AppWidgetProvider {

    static void updateWidget(Context context, AppWidgetManager manager,int imagRes, int id){
        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context,0,i,0);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.plant_widget);
        remoteViews.setOnClickPendingIntent(R.id.plant_image,pi);

        Intent waterIntent = new Intent(context,PlantWateringService.class);
        waterIntent.setAction(PlantWateringService.ACTION_WATER_PLANTS);
        PendingIntent pIntent = PendingIntent.getService(context,0,waterIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.plant_image,imagRes);
        remoteViews.setOnClickPendingIntent(R.id.water_image,pIntent);

        manager.updateAppWidget(id,remoteViews);
    }

    public static void updateWidgets(Context mcontext , AppWidgetManager manager, int imagRes, int[] ids) {

        for (int id :ids)
            updateWidget(mcontext,manager,imagRes,id);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        PlantWateringService.startUpdateWidget(context);
    }
}
