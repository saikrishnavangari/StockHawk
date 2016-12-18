package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.Contract.Quote;
import com.udacity.stockhawk.ui.DetailActivityFragment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by krrish on 17/12/2016.
 */
public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    private static final String[] QUOTE_COLUMNS = {
            Contract.Quote._ID,
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Quote.COLUMN_ABSOLUTE_CHANGE
    };
    // these indices must match the projection
    private static final int INDEX_STOCK_ID = 0;
    private static final int INDEX_STOCK_SYMBOL = 1;
    private static final int INDEX_STOCK_PRICE = 2;
    private static final int INDEX_STOCK_ABSOLUTE_CHANGE = 3;
    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();
    private DecimalFormat dollarFormatWithPlus;
    private DecimalFormat dollarFormat;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(Quote.URI, QUOTE_COLUMNS, null, null, Contract.Quote.COLUMN_SYMBOL);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);


                String symbol = data.getString(INDEX_STOCK_SYMBOL);
                float price = data.getFloat(INDEX_STOCK_PRICE);
                Log.d(LOG_TAG, symbol);
                float rawAbsoluteChange = data.getFloat(INDEX_STOCK_ABSOLUTE_CHANGE);
                if (rawAbsoluteChange > 0) {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                    //
                } else {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, getString(R.string.a11y_widget));
                }

                dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus.setPositivePrefix("+$");
                String change = dollarFormatWithPlus.format(rawAbsoluteChange);
                views.setTextViewText(R.id.symbol, symbol);
                views.setTextViewText(R.id.price, dollarFormat.format(price));
                views.setTextViewText(R.id.change, String.valueOf(change));
                final Intent fillInIntent = new Intent();

                fillInIntent.putExtra(DetailActivityFragment.Symbol, symbol);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_icon, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(Quote.POSITION_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
