package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ru.apps4yourlife.kids.kidswardrobe.Adapters.ReportListAdapter;
import ru.apps4yourlife.kids.kidswardrobe.R;

public class ReportResultListActivity extends AppCompatActivity implements ReportListAdapter.ItemListAdapterClickHandler {

    private RecyclerView mListReport;
    private ReportListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_result_list);

        mListReport = (RecyclerView) findViewById(R.id.listReportRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setMeasurementCacheEnabled(false);


        mListReport.setLayoutManager(layoutManager);
        mListReport.setHasFixedSize(true);
        mAdapter = new ReportListAdapter(this, this);
        String sentFilter = getIntent().getStringExtra("FILTER");
        String sentType = getIntent().getStringExtra("SORT");
        if (sentFilter != null &&  !sentFilter.isEmpty()) {
            mAdapter.SetFilterAndType(sentFilter, sentType);
        }
        mListReport.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(String itemId, String itemPositionInList) {

    }
}
