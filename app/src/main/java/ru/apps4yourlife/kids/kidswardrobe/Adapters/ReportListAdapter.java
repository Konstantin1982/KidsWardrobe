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

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper;

/**
 * Created by ksharafutdinov on 29-Mar-18.
 */

public class ReportListAdapter extends RecyclerView.Adapter <ReportListAdapter.ItemListAdapterViewHolder> {
    private String mCurrentHeader;
    private String mFilter;
    private String mType;
    private String mQuery;
    private  String mShownHeader;
    private  WardrobeDBDataManager mDataManager;

    private Context mContext;
    private Cursor mListItemsCursor;
    private Cursor mSizesWithNames;
    private Map<Long, ArrayList<Integer>> mSuitableSizesByChildren;

    public interface ItemListAdapterClickHandler {
        void onItemClick(String itemId, String itemPositionInList);
    }

    private final ItemListAdapterClickHandler mItemListAdapterClickHandler;

    public ReportListAdapter(Context context, ItemListAdapterClickHandler clickHandler) {
        mItemListAdapterClickHandler = clickHandler;
        mContext = context;
        mFilter = "";
        mQuery = "";
        mCurrentHeader = "пустое место";
        mShownHeader = "";
        mDataManager = new WardrobeDBDataManager(mContext);
        mSuitableSizesByChildren = GeneralHelper.GetSuitSizesByChild(mContext);
        mSizesWithNames = mDataManager.GetAllSizesWithNamesForCategory(-1);

    }

    public void SetFilterAndTypeAndQuery(String filter, String type, String query) {
        mFilter = filter;
        mType = type;
        mQuery = query;
        if (mQuery.isEmpty()) {
            mListItemsCursor = mDataManager.GetItemsForReport(mFilter, mType);
        } else {
            mListItemsCursor = mDataManager.GetAnyQuery(mQuery);
        }
    }


    @Override
    public ReportListAdapter.ItemListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("RECYCLER: ", "onCreateViewHolder is called");
        int viewId = R.layout.report_list_item;
        if (viewType == 100)  viewId = R.layout.report_list_header;
        View view = LayoutInflater.from(mContext).inflate(viewId, parent, false);
        return new ReportListAdapter.ItemListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReportListAdapter.ItemListAdapterViewHolder holder, int position) {
        mListItemsCursor.moveToPosition(position);

        if (mType.equalsIgnoreCase("comment")) {
            byte[] previewInBytes = mListItemsCursor.getBlob(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_PHOTO_PREVIEW));
            Bitmap smallPhoto = GeneralHelper.getBitmapFromBytes(previewInBytes, GeneralHelper.GENERAL_HELPER_CLOTHES_TYPE);
            holder.itemPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.itemPhoto.setImageBitmap(smallPhoto);

            int size1 = mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SIZE_MAIN));
            int size2 = mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SIZE_ADDITIONAL));
            int found = 0;
            String sizeName1 = "", sizeName2 = "";
            String sizeValue1 = "", sizeValue2 = "";
            for (int i = 0; i < mSizesWithNames.getCount(); i++) {
                mSizesWithNames.moveToPosition(i);
                int rIndex = mSizesWithNames.getColumnIndex(WardrobeContract.Sizes._ID);
                int dbId = mSizesWithNames.getInt(rIndex);
                if (dbId == size1) {
                    sizeName1 = mSizesWithNames.getString(mSizesWithNames.getColumnIndex(WardrobeContract.SizesTypes.COLUMN_SIZE_TYPE_NAME)) + ": " +
                            mSizesWithNames.getString(mSizesWithNames.getColumnIndex(WardrobeContract.Sizes.COLUMN_VALUE));
                    sizeValue1 = mSizesWithNames.getString(mSizesWithNames.getColumnIndex(WardrobeContract.Sizes.COLUMN_VALUE));
                    found++;
                }
                if (dbId == size2) {
                    sizeName2 = mSizesWithNames.getString(mSizesWithNames.getColumnIndex(WardrobeContract.SizesTypes.COLUMN_SIZE_TYPE_NAME)) + ": " +
                            mSizesWithNames.getString(mSizesWithNames.getColumnIndex(WardrobeContract.Sizes.COLUMN_VALUE));
                    sizeValue2 = mSizesWithNames.getString(mSizesWithNames.getColumnIndex(WardrobeContract.Sizes.COLUMN_VALUE));
                    found++;
                }
                if (found == 2) break;
            }
            if (sizeValue1.isEmpty()) {
                sizeName1 = "";
            }
            holder.size1TextView.setText(sizeName1);
            if (sizeValue2.isEmpty()) {
                sizeName2 = "";
            }
            if (sizeName1.isEmpty() && sizeName2.isEmpty()) {
                sizeName2 = "Размеры не указаны";
            }
            holder.size2TextView.setText(sizeName2);
            // Top ROW
            String topRow = mDataManager.GetCategoryNameById(mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_CAT_ID)));
            int season = mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SEASON));
            String[] mSeason = mContext.getResources().getStringArray(R.array.season_array);
            topRow = topRow.concat("(" + mSeason[season]);


            int sex = mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SEX));
            if (sex > 0) {
                String[] mSex = mContext.getResources().getStringArray(R.array.child_sex_array);
                topRow = topRow.concat(", " + mSex[sex]);
            }
            topRow = topRow.concat(")");
            holder.typeTextView.setText(topRow);

            Set<Long> keys = mSuitableSizesByChildren.keySet();
            String chidrenNames = "";
            for (Long key : keys) {
                Cursor child = mDataManager.GetChildByIdFromDb(String.valueOf(key));
                int childSex = child.getInt(child.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_SEX));
                if (sex == childSex || sex == 0 || childSex == 0) {
                    ArrayList<Integer> suitSizes = mSuitableSizesByChildren.get(key);
                    if (suitSizes.contains(size1) || suitSizes.contains(size2)) {
                        if (!chidrenNames.isEmpty()) chidrenNames = chidrenNames.concat(", ");
                        chidrenNames = chidrenNames.concat(child.getString(child.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_NAME)));
                    }
                }
            }
            if (chidrenNames.isEmpty()) {
                //chidrenNames = "Непонятно кому подходит";
                holder.suiteChildren.setVisibility(View.GONE);
            } else {
                chidrenNames = "Подойдет: " + chidrenNames;
                holder.suiteChildren.setVisibility(View.VISIBLE);
                holder.suiteChildren.setText(chidrenNames);
            }

            //mCurrentHeader
            String itemLocation = mListItemsCursor.getString(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_COMMENT));
            if (!itemLocation.equalsIgnoreCase(mCurrentHeader)) {
                // time to change header
                mCurrentHeader = itemLocation;
                if (mCurrentHeader.isEmpty()) {
                    mCurrentHeader = "Место не определено";
                }
                if (!mShownHeader.equalsIgnoreCase(mCurrentHeader)) {
                    holder.headerTextView.setVisibility(View.VISIBLE);
                    holder.headerTextView.setText(mCurrentHeader);
                    mShownHeader = mCurrentHeader;
                } else {
                    holder.headerTextView.setVisibility(View.GONE);
                }
            } else {
                holder.headerTextView.setVisibility(View.GONE);
            }
            return;
        }

        if (mType.equalsIgnoreCase("child")) {
            byte[] previewInBytes = mListItemsCursor.getBlob(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_PHOTO_PREVIEW));
            Bitmap smallPhoto = GeneralHelper.getBitmapFromBytes(previewInBytes, GeneralHelper.GENERAL_HELPER_CLOTHES_TYPE);
            holder.itemPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.itemPhoto.setImageBitmap(smallPhoto);

            int size1 = mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SIZE_MAIN));
            int size2 = mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SIZE_ADDITIONAL));
            int found = 0;
            String sizeName1 = "", sizeName2 = "";
            String sizeValue1 = "", sizeValue2 = "";
            for (int i = 0; i < mSizesWithNames.getCount(); i++) {
                mSizesWithNames.moveToPosition(i);
                int rIndex = mSizesWithNames.getColumnIndex(WardrobeContract.Sizes._ID);
                int dbId = mSizesWithNames.getInt(rIndex);
                if (dbId == size1) {
                    sizeName1 = mSizesWithNames.getString(mSizesWithNames.getColumnIndex(WardrobeContract.SizesTypes.COLUMN_SIZE_TYPE_NAME)) + ": " +
                            mSizesWithNames.getString(mSizesWithNames.getColumnIndex(WardrobeContract.Sizes.COLUMN_VALUE));
                    sizeValue1 = mSizesWithNames.getString(mSizesWithNames.getColumnIndex(WardrobeContract.Sizes.COLUMN_VALUE));
                    found++;
                }
                if (dbId == size2) {
                    sizeName2 = mSizesWithNames.getString(mSizesWithNames.getColumnIndex(WardrobeContract.SizesTypes.COLUMN_SIZE_TYPE_NAME)) + ": " +
                            mSizesWithNames.getString(mSizesWithNames.getColumnIndex(WardrobeContract.Sizes.COLUMN_VALUE));
                    sizeValue2 = mSizesWithNames.getString(mSizesWithNames.getColumnIndex(WardrobeContract.Sizes.COLUMN_VALUE));
                    found++;
                }
                if (found == 2) break;
            }
            if (sizeValue1.isEmpty()) {
                sizeName1 = "";
            }
            holder.size1TextView.setText(sizeName1);
            if (sizeValue2.isEmpty()) {
                sizeName2 = "";
            }
            if (sizeName1.isEmpty() && sizeName2.isEmpty()) {
                sizeName2 = "Размеры не указаны";
            }
            holder.size2TextView.setText(sizeName2);
            // Top ROW
            String topRow = mDataManager.GetCategoryNameById(mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_CAT_ID)));
            int season = mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SEASON));
            String[] mSeason = mContext.getResources().getStringArray(R.array.season_array);
            topRow = topRow.concat("(" + mSeason[season]);


            int sex = mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SEX));
            if (sex > 0) {
                String[] mSex = mContext.getResources().getStringArray(R.array.child_sex_array);
                topRow = topRow.concat(", " + mSex[sex]);
            }
            topRow = topRow.concat(")");
            holder.typeTextView.setText(topRow);

            Set<Long> keys = mSuitableSizesByChildren.keySet();
            String chidrenNames = "";
            for (Long key : keys) {
                Cursor child = mDataManager.GetChildByIdFromDb(String.valueOf(key));
                int childSex = child.getInt(child.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_SEX));
                if (sex == childSex || sex == 0 || childSex == 0) {
                    ArrayList<Integer> suitSizes = mSuitableSizesByChildren.get(key);
                    if (suitSizes.contains(size1) || suitSizes.contains(size2)) {
                        if (!chidrenNames.isEmpty()) chidrenNames = chidrenNames.concat(", ");
                        chidrenNames = chidrenNames.concat(child.getString(child.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_NAME)));
                    }
                }
            }

            String itemLocation = mListItemsCursor.getString(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_COMMENT));
            if (!itemLocation.isEmpty()) {
                holder.suiteChildren.setVisibility(View.VISIBLE);
                holder.suiteChildren.setText("Где лежит: " + itemLocation);
            } else {
                holder.suiteChildren.setVisibility(View.VISIBLE);
                holder.suiteChildren.setText("Непонятно, где находится");
            }

            //mCurrentHeader
            String currentChild =   mListItemsCursor.getString(mListItemsCursor.getColumnIndex("child"));
            if (!mCurrentHeader.equalsIgnoreCase(currentChild)) {
                // time to change header
                mCurrentHeader = currentChild;
                Cursor childCursor = mDataManager.GetChildByIdFromDb(currentChild);
                mShownHeader = childCursor.getString(childCursor.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_NAME));
                holder.headerTextView.setVisibility(View.VISIBLE);
                holder.headerTextView.setText(mShownHeader);
            } else {
                holder.headerTextView.setVisibility(View.GONE);
            }
        }


        return;
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
        /*
        String oldHeader = GeneralHelper.GetNewHeaderForReport(mListItemsCursor, mType);
        mListItemsCursor.moveToPosition(position);
        String newHeader = GeneralHelper.GetNewHeaderForReport(mListItemsCursor, mType);
        if (oldHeader != newHeader) {
            mCurrentHeader = newHeader;
            return 100;
        } else {
            return 0;
        }
        */
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
        private TextView headerTextView;
        private TextView suiteChildren;
        private ImageView itemPhoto;

        ItemListAdapterViewHolder(View view) {
            super(view);
            size1TextView = (TextView) view.findViewById(R.id.report_size1TextView);
            size2TextView = (TextView) view.findViewById(R.id.report_size2TextView);
            typeTextView = (TextView) view.findViewById(R.id.report_typeTextView);
            suiteChildren = (TextView) view.findViewById(R.id.report_suiteChilren);
            headerTextView = (TextView) view.findViewById(R.id.report_headerSection);
            itemPhoto = (ImageView) view.findViewById(R.id.item_photo);
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
