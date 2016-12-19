package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.MainActivity;

/**
 * Created by krrish on 17/12/2016.
 */

public class stockWidgetIntentService extends IntentService {

        private static final String[] QUOTE_COLUMNS = {
                Contract.Quote._ID,
                Contract.Quote.COLUMN_SYMBOL,
                Contract.Quote.COLUMN_PRICE
        };
        // these indices must match the projection
        private static final int INDEX_STOCK_ID = 0;
        private static final int INDEX_STOCK_SYMBOL = 1;
        private static final int INDEX_STOCK_PRICE = 2;

        public stockWidgetIntentService() {
            super("stockWidgetIntentService");
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            // Retrieve all of the stock widget ids: these are the widgets we need to update
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                    StockWidgetProvider.class));

            // GET STOCKS
            Cursor data = getContentResolver().query(Contract.Quote.URI, QUOTE_COLUMNS, null,
                    null, Contract.Quote.COLUMN_SYMBOL);
            if (data == null) {
                return;
            }
            if (!data.moveToFirst()) {
                data.close();
                return;
            }
            StringBuilder stockdata=new StringBuilder();
            // Extract the weather data from the Cursor
            do{
                int stockID = data.getInt(INDEX_STOCK_ID);
                String Symbol = data.getString(INDEX_STOCK_SYMBOL);
                int stockPrice = (int) data.getDouble(INDEX_STOCK_PRICE);
                stockdata.append(Symbol +" "+"$"+stockPrice +"\n");
                Log.d("stockdata", stockdata.toString());
            }while(data.moveToNext());
            data.close();

            // Perform this loop procedure for each Today widget
            for (int appWidgetId : appWidgetIds) {
                int layoutId = R.layout.widget_stock_small;
                RemoteViews views = new RemoteViews(getPackageName(), layoutId);
                String description="Stocks";
                // Content Descriptions for RemoteViews were only added in ICS MR1
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, description);
                }
                views.setImageViewResource(R.id.widget_icon, R.drawable.widget_icon);
                views.setTextViewText(R.id.widget_symbol_stock, stockdata);

                // Create an Intent to launch MainActivity
                Intent launchIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
                views.setOnClickPendingIntent(R.id.widget, pendingIntent);

                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
        private void setRemoteContentDescription(RemoteViews views, String description) {
            views.setContentDescription(R.id.widget_icon, description);
        }
    }

