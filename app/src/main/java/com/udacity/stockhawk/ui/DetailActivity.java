package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.udacity.stockhawk.R;

/**
 * Created by krrish on 17/12/2016.
 */

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailview_chart);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            String stockSymbol = intent.getStringExtra(DetailActivityFragment.Symbol);
            Bundle arguments = new Bundle();
            arguments.putString(DetailActivityFragment.Symbol, stockSymbol);
            Log.d("symbol", stockSymbol);
            DetailActivityFragment df = new DetailActivityFragment();
            df.setArguments(arguments);

            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, df)
                    .commit();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
