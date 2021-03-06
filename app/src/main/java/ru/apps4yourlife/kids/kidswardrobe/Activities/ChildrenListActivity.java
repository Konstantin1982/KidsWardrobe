package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.content.Intent;
import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import ru.apps4yourlife.kids.kidswardrobe.Adapters.ChildrenListAdapter;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;

public class ChildrenListActivity extends AppCompatActivity implements ChildrenListAdapter.ChildrenListAdapterClickHandler {

    private RecyclerView mListChildren;
    private ChildrenListAdapter mAdapter;
    private int mNoAdsStatus = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_list);

        WardrobeDBDataManager dbDataManager = new WardrobeDBDataManager(this);
        /*
        mNoAdsStatus = dbDataManager.getPurchaseStatus(BillingHelper.SKUCodes.noAdsCode);
        if (mNoAdsStatus > 0) {
            updateUI();
        } else {
            AdView adView = (AdView) findViewById(R.id.adView_children);
            AdRequest adRequest = new AdRequest.Builder()
                    .setRequestAgent("android_studio:ad_template").build();
            adView.loadAd(adRequest);
        }
        */

        mListChildren = (RecyclerView) findViewById(R.id.childrenList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        layoutManager.setMeasurementCacheEnabled(false);

        mListChildren.setLayoutManager(layoutManager);
        mListChildren.setHasFixedSize(true);
        mAdapter = new ChildrenListAdapter(this, this);
        mListChildren.setAdapter(mAdapter);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }
    public void updateUI () {
        //AdView adView = (AdView) findViewById(R.id.adView_children);
        //adView.setVisibility(View.GONE);
    }


    public void onChildClick(String ID, String POSITION) {
        Intent intent = new Intent(this, AddNewChildActivity.class);
        intent.putExtra("ID",ID);
        intent.putExtra("POSITION",POSITION);
        //Log.e("ACTIVITY","List received position = " + POSITION);
        startActivityForResult(intent,199);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 199 && resultCode > 0) {
            //Log.e("ACTIVITY: ","onActivityResult is called for List with POSITION = " + data.getStringExtra("POSITION"));
            WardrobeDBDataManager mDataManager = new WardrobeDBDataManager(this);
            Cursor newChildrenCursor = mDataManager.GetChildrenListFromDb("");
            String position = data.getStringExtra("POSITION");

            if (position == null) {
                position = "-1";
            }
            mAdapter.updateListValues(newChildrenCursor, Integer.parseInt(position));
            //newChildrenCursor.close();
        }
    }

    public void btnAddNewChildFromList_Click(View view) {
        Intent intent = new Intent(this, AddNewChildActivity.class);
        startActivityForResult(intent,199);
        return;
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

}
