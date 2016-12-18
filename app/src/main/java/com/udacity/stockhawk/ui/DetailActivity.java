package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.udacity.stockhawk.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by krrish on 17/12/2016.
 */

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.chart)
    LineChart chart;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailview_chart);
        ButterKnife.bind(this);
        Intent intent=getIntent();
        String stockSymbol=intent.getStringExtra(MainActivity.EXTRA_SYMBOL);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
