package ru.apps4yourlife.kids.kidswardrobe.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import ru.apps4yourlife.kids.kidswardrobe.Adapters.ChildrenListAdapter;
import ru.apps4yourlife.kids.kidswardrobe.Adapters.SetsListAdapter;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;

public class SetsListActivity extends AppCompatActivity implements SetsListAdapter.SetsListAdapterClickHandler {

    private RecyclerView mListSetsRecyclerView;
    private SetsListAdapter mSetsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets_list);
        mListSetsRecyclerView = findViewById(R.id.item_sets_recyclerview);

        mSetsListAdapter = new SetsListAdapter(this, this);
        mListSetsRecyclerView.setAdapter(mSetsListAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        layoutManager.setMeasurementCacheEnabled(false);
        mListSetsRecyclerView.setLayoutManager(layoutManager);
        mListSetsRecyclerView.setHasFixedSize(true);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onSetClick(String setId, String itemPositionInList) {
        // Open set activity
    }
}
