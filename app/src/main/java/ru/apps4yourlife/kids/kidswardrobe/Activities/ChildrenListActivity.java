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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_list);

        mListChildren = (RecyclerView) findViewById(R.id.childrenList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setMeasurementCacheEnabled(false);
        mListChildren.setLayoutManager(layoutManager);

        RecyclerView.Adapter mAdapter = new ChildrenListAdapter(this, this);
        mListChildren.setAdapter(mAdapter);

    }

    public void onChildClick(int ID) {
        Intent intent = new Intent(this, AddNewChildActivity.class);
        intent.putExtra("ID",ID);
        startActivity(intent);
    }
}
