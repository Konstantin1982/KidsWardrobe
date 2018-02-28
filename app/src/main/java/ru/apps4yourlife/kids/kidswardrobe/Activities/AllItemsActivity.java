package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import ru.apps4yourlife.kids.kidswardrobe.R;

public class AllItemsActivity extends AppCompatActivity {

    private GridView mGridItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_items);

        mGridItems = findViewById(R.id.gridItems);
        mGridItems.setAdapter(new GridAdapter(this));
        mGridItems.setOnItemClickListener(new AllItemsClickListener());
    }

    private class GridAdapter extends BaseAdapter {

        private Context mContext;

        public GridAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return 8;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View itemView;
            itemView = getLayoutInflater().inflate(R.layout.grid_item, null);
            return itemView;
        }
    }

    private class AllItemsClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(adapterView.getContext(), CategoryItemsActivity.class);
            startActivity(intent);
        }
    }

}
