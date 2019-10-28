package ru.apps4yourlife.kids.kidswardrobe.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import ru.apps4yourlife.kids.kidswardrobe.Adapters.ChildrenListAdapter;
import ru.apps4yourlife.kids.kidswardrobe.Adapters.SetsListAdapter;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper;

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
        Intent intent = new Intent(this, ItemSetsActivity.class);
        intent.putExtra("ID",setId);
        intent.putExtra("POSITION",itemPositionInList);
        //Log.e("ACTIVITY","List received position = " + POSITION);
        startActivityForResult(intent,199);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        WardrobeDBDataManager dbDataManager= new WardrobeDBDataManager(this);
        Cursor mSetsCursor = WardrobeDBDataManager.getAllSetsForList(dbDataManager.mDBHelper.getReadableDatabase());

        mSetsListAdapter.updateListValues(mSetsCursor, -1);
        mSetsListAdapter.notifyDataSetChanged();
        mListSetsRecyclerView.invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(0);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onHelpShowClick(View view) {
        GeneralHelper.ShowHelp(this);
    }

}
