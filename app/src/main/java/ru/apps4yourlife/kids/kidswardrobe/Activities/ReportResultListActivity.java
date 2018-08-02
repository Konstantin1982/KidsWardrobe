package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import ru.apps4yourlife.kids.kidswardrobe.Adapters.ReportListAdapter;
import ru.apps4yourlife.kids.kidswardrobe.R;

public class ReportResultListActivity extends AppCompatActivity implements
        ReportListAdapter.ItemListAdapterClickHandler,
ReportListAdapter.ImageListAdapterClickHandler{

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
        mAdapter = new ReportListAdapter(this, this, this);
        String sentFilter = getIntent().getStringExtra("FILTER");
        String sentType = getIntent().getStringExtra("SORT");
        String sentQuery = getIntent().getStringExtra("QUERY");

        if (sentType.equalsIgnoreCase("comment")) {
            setTitle(R.string.title_activity_place_report);
        }
        if (sentType.equalsIgnoreCase("child")) {
            setTitle(R.string.title_activity_children_report);
        }



        if (sentQuery == null || sentQuery.isEmpty()) {
            if (sentFilter != null &&  !sentFilter.isEmpty()) {
                mAdapter.SetFilterAndTypeAndQuery(sentFilter, sentType, "");
            }
        } else {
            if (sentQuery != null &&  !sentQuery.isEmpty()) {
                mAdapter.SetFilterAndTypeAndQuery("", "child",sentQuery);
            }
        }
        mListReport.setAdapter(mAdapter);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)  actionBar.setDisplayHomeAsUpEnabled(true);

        final ImageView zoomImage = findViewById(R.id.zoomImage);
        zoomImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImage.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(String itemId, String itemPositionInList) {
        //Toast.makeText(this,"ITEM CLICKED",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onImageClick(Bitmap image) {
        ImageView mImage = findViewById(R.id.zoomImage);
        mImage.setImageBitmap(image);
        mImage.setVisibility(View.VISIBLE);
        //Toast.makeText(this,"IMAGE! CLICKED",Toast.LENGTH_SHORT).show();
    }
}
