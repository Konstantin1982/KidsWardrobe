package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ru.apps4yourlife.kids.kidswardrobe.Adapters.CategoryListAdapter;
import ru.apps4yourlife.kids.kidswardrobe.R;

public class CategoryItemsActivity extends AppCompatActivity {

    private RecyclerView mListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_items);

        mListItems = findViewById(R.id.categoryListItems);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        RecyclerView.Adapter mAdapter =new CategoryListAdapter(this);

        mListItems.setLayoutManager(layoutManager);
        mListItems.setAdapter(mAdapter);

    }
}
