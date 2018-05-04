package ru.apps4yourlife.kids.kidswardrobe.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper;

/**
 * Created by ksharafutdinov on 27-Feb-18.
 */

public class CategoryListAdapter extends RecyclerView.Adapter <CategoryListAdapter.CategoryListAdapterViewHolder> {

    private Context mContext;
    private long mCategoryId;
    private Cursor mItemsInCategoryCursor;
    private final CategoryListAdapterClickHandler mCategoryListAdapterClickHandler;

    public CategoryListAdapter(Context context, long categoryId, CategoryListAdapterClickHandler clickHandler) {
        mContext = context;
        mCategoryId = categoryId;
        mCategoryListAdapterClickHandler = clickHandler;
        WardrobeDBDataManager mDataManager = new WardrobeDBDataManager(mContext);
        mItemsInCategoryCursor = mDataManager.GetAllItemsInCategory (mCategoryId);
    }

    public interface CategoryListAdapterClickHandler {
        void onItemClick(String childId, String itemPositionInList);
    }

    public void updateListValues(Cursor newItemList, int position) {
        mItemsInCategoryCursor = newItemList;
        if (position >= 0) {
            notifyItemChanged(position);
        } else {
            notifyDataSetChanged();
        }
    }

    @Override
    public CategoryListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        return new CategoryListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryListAdapterViewHolder holder, int position) {
        mItemsInCategoryCursor.moveToPosition(position);
        holder.firstTextView.setText(mItemsInCategoryCursor.getString(mItemsInCategoryCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SEX)));
        holder.secondTextView.setText(mItemsInCategoryCursor.getString(mItemsInCategoryCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SEASON)));
        byte[] previewInBytes = mItemsInCategoryCursor.getBlob(mItemsInCategoryCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_PHOTO_PREVIEW));
        Bitmap smallPhoto = GeneralHelper.getBitmapFromBytes(previewInBytes,GeneralHelper.GENERAL_HELPER_CLOTHES_TYPE);
        holder.mImagePreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.mImagePreview.setImageBitmap(smallPhoto);
        return;
    }

    @Override
    public int getItemCount() {
        return mItemsInCategoryCursor.getCount();
    }

    class CategoryListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /*
        final ImageView iconView;
        final TextView dateView;
        final TextView descriptionView;
        final TextView highTempView;
        final TextView lowTempView;
        */

        private ImageView mImagePreview;
        private TextView firstTextView;
        private TextView secondTextView;

        CategoryListAdapterViewHolder(View view) {
            super(view);
            /*
            iconView = (ImageView) view.findViewById(R.id.weather_icon);
            dateView = (TextView) view.findViewById(R.id.date);
            descriptionView = (TextView) view.findViewById(R.id.weather_description);
            highTempView = (TextView) view.findViewById(R.id.high_temperature);
            lowTempView = (TextView) view.findViewById(R.id.low_temperature);
            */

            firstTextView = (TextView) view.findViewById(R.id.textView);
            secondTextView = (TextView) view.findViewById(R.id.textView2);
            mImagePreview = (ImageView) view.findViewById(R.id.previewItemImageInList);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click. We fetch the date that has been
         * selected, and then call the onClick handler registered with this adapter, passing that
         * date.
         *
         * @param v the View that was clicked
         */
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mItemsInCategoryCursor.moveToPosition(position);
            Log.e("ADAPTER","CALL ACTIVITY with position = " + position);
            mCategoryListAdapterClickHandler.onItemClick (
                    mItemsInCategoryCursor.getString(mItemsInCategoryCursor.getColumnIndex(WardrobeContract.ClothesItem._ID)),
                    String.valueOf(position)
            );
        }
    }
}
