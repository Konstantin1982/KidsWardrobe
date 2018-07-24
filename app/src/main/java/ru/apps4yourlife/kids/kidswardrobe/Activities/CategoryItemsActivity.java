package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import ru.apps4yourlife.kids.kidswardrobe.Adapters.CategoryListAdapter;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;

public class CategoryItemsActivity extends AppCompatActivity implements CategoryListAdapter.CategoryListAdapterClickHandler {

    private RecyclerView mListItems;
    private long mCategoryID;
    private CategoryListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_items);

        AdView adView = (AdView) findViewById(R.id.adView_items);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        mListItems = (RecyclerView) findViewById(R.id.categoryListItems);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setMeasurementCacheEnabled(false);
        mListItems.setLayoutManager(layoutManager);
        String extraData = getIntent().getStringExtra("ID");
        mCategoryID = Long.valueOf(extraData);

        mAdapter = new CategoryListAdapter(this, mCategoryID, this);
        mListItems.setAdapter(mAdapter);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        WardrobeDBDataManager dataManager = new WardrobeDBDataManager(this);
        String catName = dataManager.GetCategoryNameById(mCategoryID);
        if (catName.isEmpty()) {
            catName = this.getString(R.string.title_activity_category_default);
        }
        actionBar.setTitle(catName);
    }

    @Override
    public void onItemClick(String ID, String POSITION) {
        Intent intent = new Intent(this, AddNewItemActivity.class);
        intent.putExtra("ID",ID);
        intent.putExtra("POSITION",POSITION);
        Log.e("ACTIVITY","List received position = " + POSITION);
        startActivityForResult(intent,299);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 299 && resultCode > 0) {
            WardrobeDBDataManager mDataManager = new WardrobeDBDataManager(this);
            Cursor newItemsCursor = mDataManager.GetAllItemsInCategory(mCategoryID);
            String position = null;
            if (data != null && data.hasExtra("POSITION")) {
                position = data.getStringExtra("POSITION");
            }
            if (position == null) {
                position = "-1";
            }
            mAdapter.updateListValues (newItemsCursor, Integer.parseInt(position));
            //newItemsCursor.close();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_to_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(399);
                finish();
                return true;
            case R.id.menu_item_home:
                Intent intent = new Intent(this, StartActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void btnAddNewItemFromList_Click(View v) {
        onItemClick("0", "-1");
    }


}
