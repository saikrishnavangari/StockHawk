package com.udacity.stockhawk.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.Utilities;
import com.udacity.stockhawk.data.Contract.Quote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by krrish on 18/12/2016.
 */

public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static int LOADER_ID=1;
    private static Uri mUri;
    String msymbol="AAPL";
    @BindView(R.id.chart)
    LineChart chart;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         mUri= Quote.makeUriForStock(msymbol);
        View view=inflater.inflate(R.layout.detailview_fragment,container,false);
        ButterKnife.bind(this,view);
     return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (null != mUri) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    Quote.QUOTE_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {

            String history = cursor.getString(Quote.POSITION_HISTORY);
            ArrayList<Float> historyDate = new ArrayList<>(); // Array data of historical dates
            ArrayList<Float> historyPrice = new ArrayList<>(); // Array data of historical dates in a chronological order
            ArrayList<Float> foramttedHIstoryPrice = new ArrayList<>(); // Array data of historical prices
            ArrayList<Float> formattedHistoryPrice = new ArrayList<>(); // Array data of historical prices in a chronological order
            if (null != history) {
                String[] str = history.split("\\r?\\n|,");
                String pattern = "dd/mm/yyyy";
                for (int i = 0; i < str.length - 1; i++) {
                    if (i % 2 == 0) {
                        long dateInMilliseconds = Long.parseLong(str[i]);
                        historyDate.add(Float.valueOf((Utilities.getDate(dateInMilliseconds, pattern))));
                        Collections.sort(historyDate);
                    } else {
                        historyPrice.add(Float.valueOf(str[i]));
                        Collections.sort(historyPrice);

                    }
                }
            }
            // Plot the graph
            plotGraph(historyDate, historyPrice);
        }
    }



    void plotGraph(ArrayList<Float> dates, ArrayList<Float> prices){
        List<Entry> entries = new ArrayList<Entry>();
        for(int i=0;i<dates.size();i++) {
            // turn your data into Entry objects
            entries.add(new Entry(dates.get(i),prices.get(i)));
        }
        LineDataSet dataSet = new LineDataSet(entries, "msymbol"); // add entries to dataset
        dataSet.setColor(R.color.white);
        dataSet.setValueTextColor(R.color.colorPrimary);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();

    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
