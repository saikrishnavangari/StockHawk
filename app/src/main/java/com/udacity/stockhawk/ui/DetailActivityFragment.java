package com.udacity.stockhawk.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.udacity.stockhawk.MyXAxisValueFormatter;
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

public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public final static String Symbol = "SYMBOL";
    private static String msymbol;
    private static int LOADER_ID = 1;
    private static Uri mUri;
    @BindView(R.id.chart)
    LineChart chart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            msymbol = arguments.getString(Symbol);
        }
        Log.d("symbol", msymbol);
        mUri = Quote.makeUriForStock(msymbol);
        View view = inflater.inflate(R.layout.detailview_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
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
            ArrayList<String> historyDate = new ArrayList<>(); // Array data of historical dates
            ArrayList<Float> historyPrice = new ArrayList<>(); // Array data of historical dates in a chronological order
            if (null != history) {
                String[] str = history.split("\\r?\\n|,");
                String pattern = "dd/mm/yyyy";
                for (int i = 0; i < str.length - 1; i++) {
                    if (i % 2 == 0) {
                        long dateInMilliseconds = Long.parseLong(str[i]);
                        historyDate.add((Utilities.getDate(dateInMilliseconds, pattern)));

                    } else {
                        historyPrice.add(Float.valueOf(str[i]));


                    }
                }
            }
            // Plot the graph
            plotGraph(historyDate, historyPrice);
        }
        cursor.close();
    }


    void plotGraph(ArrayList<String> dates, ArrayList<Float> prices) {
        for (Float i : prices)
            Log.d("symbol values", i.toString());
        List<Entry> entries = new ArrayList<Entry>();
        String[] datesArray = new String[dates.size()];
        for (int i = 0; i < prices.size(); i++) {
            // turn your data into Entry objects
            entries.add(new Entry(i, prices.get(i)));
        }
        Collections.sort(entries, new EntryXComparator());
        LineDataSet stockPrices = new LineDataSet(entries, msymbol); // add entries to dataset
        stockPrices.setColor(R.color.white);
        XAxis xAxis = chart.getXAxis();
        MyXAxisValueFormatter formatter = new MyXAxisValueFormatter(dates.toArray(datesArray));
        xAxis.setValueFormatter(formatter);
        stockPrices.setValueTextColor(R.color.colorPrimary);
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(stockPrices);
        LineData data = new LineData(dataSets);
        chart.setData(data);
        chartSettings();

    }

    void chartSettings() {
        chart.setBackgroundColor(Color.WHITE);
        chart.setBorderColor(Color.BLACK);
        chart.invalidate();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
