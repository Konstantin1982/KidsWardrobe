package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import ru.apps4yourlife.kids.kidswardrobe.Adapters.CategoryListAdapter;
import ru.apps4yourlife.kids.kidswardrobe.Adapters.ChildrenListAdapter;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;

public class ChildrenListActivity extends AppCompatActivity implements ChildrenListAdapter.ChildrenListAdapterClickHandler {

    private RecyclerView mListChildren;
    private ChildrenListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("ACTIVITY: ","On Create is called for List");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_list);

        mListChildren = (RecyclerView) findViewById(R.id.childrenList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setMeasurementCacheEnabled(false);

        mListChildren.setLayoutManager(layoutManager);
        mListChildren.setHasFixedSize(true);
        mAdapter = new ChildrenListAdapter(this, this);
        mListChildren.setAdapter(mAdapter);

    }

    public void onChildClick(String ID, String POSITION) {
        Intent intent = new Intent(this, AddNewChildActivity.class);
        intent.putExtra("ID",ID);
        intent.putExtra("POSITION",POSITION);
        Log.e("ACTIVITY","List received position = " + POSITION);
        startActivityForResult(intent,199);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 199 && resultCode > 0) {
            Log.e("ACTIVITY: ","onActivityResult is called for List with POSITION = " + data.getStringExtra("POSITION"));
            WardrobeDBDataManager mDataManager = new WardrobeDBDataManager(this);
            Cursor newChildrenCursor = mDataManager.GetChildrenListFromDb("");
            String position = data.getStringExtra("POSITION");

            if (position == null) {
                position = "-1";
            }
            mAdapter.updateListValues(newChildrenCursor, Integer.parseInt(position));
        }
    }

    public void btnAddNewChildFromList_Click(View view) {
        Intent intent = new Intent(this, AddNewChildActivity.class);
        startActivityForResult(intent,199);
        return;
    }
}
