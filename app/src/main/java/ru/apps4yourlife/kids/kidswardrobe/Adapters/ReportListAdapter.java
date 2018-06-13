package ru.apps4yourlife.kids.kidswardrobe.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper;

/**
 * Created by ksharafutdinov on 29-Mar-18.
 */

public class ReportListAdapter extends RecyclerView.Adapter <ReportListAdapter.ItemListAdapterViewHolder> {
    private String mFilter;
    private Context mContext;
    private Cursor mListItemsCursor;

    public interface ItemListAdapterClickHandler {
        void onItemClick(String itemId, String itemPositionInList);
    }

    private final ItemListAdapterClickHandler mItemListAdapterClickHandler;

    public ReportListAdapter(Context context, ItemListAdapterClickHandler clickHandler) {
        mItemListAdapterClickHandler = clickHandler;
        mContext = context;
        mFilter = ""; // TODO
        WardrobeDBDataManager mDataManager = new WardrobeDBDataManager(mContext);
        mListItemsCursor = mDataManager.GetItemsForReport(mFilter);
    }

    public void SetFilter(String filter) {
        mFilter = filter;
    }


    @Override
    public ReportListAdapter.ItemListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("RECYCLER: ", "onCreateViewHolder is called");
        View view = LayoutInflater.from(mContext).inflate(R.layout.report_list_item, parent, false);
        return new ReportListAdapter.ItemListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReportListAdapter.ItemListAdapterViewHolder holder, int position) {
        Log.e("RECYCLER: ", "onBindViewHolder is called with position: " + position);

        mListItemsCursor.moveToPosition(position);
        // ID and Name
        //holder.nameTextView.setTag(mListChildrenCursor.getString(mListChildrenCursor.getColumnIndex(WardrobeContract.ChildEntry._ID)));
        //holder.nameTextView.setText(mListChildrenCursor.getString(mListChildrenCursor.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_NAME)));
        // BirthDate
        //long birthDateAsLong = mListChildrenCursor.getLong(mListChildrenCursor.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_BIRTHDATE));
        //holder.ageTextView.setText(                GeneralHelper.getStringFromBirthDate(                        birthDateAsLong,                        mContext.getResources().getString(R.string.birthdate_undefinded)
        //small photo
        //byte[] previewInBytes = mListChildrenCursor.getBlob(mListChildrenCursor.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_PHOTO_PREVIEW));
        //Bitmap smallPhoto = GeneralHelper.getBitmapFromBytes(previewInBytes, GeneralHelper.GENERAL_HELPER_CHILD_TYPE);
        //holder.smallPhotoImageView.setImageBitmap(smallPhoto);
        Log.e("RECYCLER: ", "height of view is : " + holder.itemView.getHeight());
        return;
    }

    @Override
    public int getItemCount() {
        if (mListItemsCursor != null) {
            return mListItemsCursor.getCount();
        } else{
            return 0;
        }
    }

    class ItemListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView size1TextView;
        private TextView size2TextView;
        private TextView typeTextView;
        private TextView suiteChildren;
        private ImageView ItemPhoto;

        ItemListAdapterViewHolder(View view) {
            super(view);
            //nameTextView = (TextView) view.findViewById(R.id.childNameInList);
            //ageTextView = (TextView) view.findViewById(R.id.childAgeInList);
            //smallPhotoImageView = (ImageView) view.findViewById(R.id.previewPhotoChildInList);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mListItemsCursor.moveToPosition(position);
            Log.e("ADAPTER","CALL ACTIVITY with position = " + position);
            mItemListAdapterClickHandler.onItemClick(
                    mListItemsCursor.getString(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem._ID)),
                    String.valueOf(position)
            );
        }
    }

}
