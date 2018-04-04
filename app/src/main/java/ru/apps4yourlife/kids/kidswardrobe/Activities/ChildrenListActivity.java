package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ru.apps4yourlife.kids.kidswardrobe.Adapters.CategoryListAdapter;
import ru.apps4yourlife.kids.kidswardrobe.Adapters.ChildrenListAdapter;
import ru.apps4yourlife.kids.kidswardrobe.R;

public class ChildrenListActivity extends AppCompatActivity implements ChildrenListAdapter.ChildrenListAdapterClickHandler {

    private RecyclerView mListChildren;
    private ChildrenListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_list);

        mListChildren = (RecyclerView) findViewById(R.id.childrenList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setMeasurementCacheEnabled(false);
        mListChildren.setLayoutManager(layoutManager);

        mAdapter = new ChildrenListAdapter(this, this);
        mListChildren.setAdapter(mAdapter);

    }

    public void onChildClick(String ID) {
        Intent intent = new Intent(this, AddNewChildActivity.class);
        intent.putExtra("ID",ID);
        startActivityForResult(intent,199);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 199 && resultCode > 0) {
            mAdapter.updateListValues();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
