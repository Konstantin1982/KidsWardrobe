package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import ru.apps4yourlife.kids.kidswardrobe.Adapters.CategoryListAdapter;
import ru.apps4yourlife.kids.kidswardrobe.R;

public class CategoryItemsActivity extends AppCompatActivity {

    private RecyclerView mListItems;
    private long mCategoryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_items);

        mListItems = (RecyclerView) findViewById(R.id.categoryListItems);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setMeasurementCacheEnabled(false);
        mListItems.setLayoutManager(layoutManager);
        String extraData = getIntent().getStringExtra("ID");
        mCategoryID = Long.valueOf(extraData);

        RecyclerView.Adapter mAdapter = new CategoryListAdapter(this, mCategoryID);
        mListItems.setAdapter(mAdapter);
    }
}
