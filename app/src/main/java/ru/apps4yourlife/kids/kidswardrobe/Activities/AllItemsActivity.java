package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;

public class AllItemsActivity extends AppCompatActivity {

    private GridView mGridItems;
    private Context mContext;
    //private WardrobeDBDataManager mDataManager;
    private Cursor mCategoriesCursor;
    private WardrobeDBDataManager mDataManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        mDataManager = new WardrobeDBDataManager(this);
        mCategoriesCursor = mDataManager.GetAllClothesCategories(false);

        setContentView(R.layout.activity_all_items);

        mGridItems = findViewById(R.id.gridItems);
        mGridItems.setAdapter(new GridAdapter(this));
        mGridItems.setOnItemClickListener(new AllItemsClickListener());

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // actionBar.setHomeAsUpIndicator(R.drawable.);

    }

    private class GridAdapter extends BaseAdapter {

        private Context mContext;

        public GridAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return mCategoriesCursor.getCount();
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

            mCategoriesCursor.moveToPosition(i);
            long catId = mCategoriesCursor.getLong(mCategoriesCursor.getColumnIndex(WardrobeContract.ClothesCategory._ID));

            TextView itemName = (TextView) itemView.findViewById(R.id.categoryName);
            itemName.setText(
                    mCategoriesCursor.getString(mCategoriesCursor.getColumnIndex(WardrobeContract.ClothesCategory.COLUMN_CAT_NAME))
            );

            TextView count = (TextView) itemView.findViewById(R.id.countItemsInCategory);
            count.setText(String.valueOf(mDataManager.GetCountItemsInCategory(catId)));
            itemView.setTag(catId);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.categoryImage);
            imageView.setImageBitmap(mDataManager.GetCategoryImage(catId));

            return itemView;
        }
    }

    private class AllItemsClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mCategoriesCursor.moveToPosition(i);
            long id = mCategoriesCursor.getLong(mCategoriesCursor.getColumnIndex(WardrobeContract.ClothesCategory._ID));
            //Toast.makeText(mContext,"Category ID clicked: " + String.valueOf(id) , Toast.LENGTH_SHORT ).show();
            Intent intent = new Intent(adapterView.getContext(), CategoryItemsActivity.class);
            intent.putExtra("ID", String.valueOf(id));
            startActivityForResult(intent, 399);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 399 && resultCode > 0) {
            mCategoriesCursor = mDataManager.GetAllClothesCategories(false);
            GridAdapter adapter =  (GridAdapter) mGridItems.getAdapter();
            adapter.notifyDataSetChanged();
            mGridItems.invalidateViews();
        }
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


}
