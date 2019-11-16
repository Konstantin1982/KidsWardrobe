package ru.apps4yourlife.kids.kidswardrobe.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

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

public class ReportListAdapterNew extends RecyclerView.Adapter <ReportListAdapterNew.ItemListAdapterViewHolder> {
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

    public interface ImageListAdapterClickHandler {
        void onImageClick(Bitmap image, View view);
    }

    public interface SetButtonClickHandler {
        void onSetButtonClick(Button sender, int setNumber, int itemId);
    }

    private final ItemListAdapterClickHandler mItemListAdapterClickHandler;
    private final ImageListAdapterClickHandler mImageClickHandler;
    private final SetButtonClickHandler mSetButtonClickHandler;

    public ReportListAdapterNew(Context context, ItemListAdapterClickHandler clickHandler, ImageListAdapterClickHandler clickHandler2, SetButtonClickHandler setButtonClickHandler) {
        mItemListAdapterClickHandler = clickHandler;
        mImageClickHandler = clickHandler2;
        mSetButtonClickHandler = setButtonClickHandler;

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
    public ReportListAdapterNew.ItemListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Log.e("RECYCLER: ", "onCreateViewHolder is called");
        int viewId = R.layout.report_list_item_new;
        View view = LayoutInflater.from(mContext).inflate(viewId, parent, false);
        return new ReportListAdapterNew.ItemListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReportListAdapterNew.ItemListAdapterViewHolder holder, int position) {
        mListItemsCursor.moveToPosition(position);

        if (mType.equalsIgnoreCase("comment")) {
            // itemPhoto
            byte[] previewInBytes = mListItemsCursor.getBlob(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_PHOTO_PREVIEW));
            Bitmap smallPhoto = GeneralHelper.getBitmapFromBytes(previewInBytes, GeneralHelper.GENERAL_HELPER_CLOTHES_TYPE);
            holder.itemPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.itemPhoto.setImageBitmap(smallPhoto);

            // itemType
            String itemType = mDataManager.GetCategoryNameById(mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_CAT_ID)));
            holder.typeTextView.setText(itemType);

            // itemSeason
            int season = mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SEASON));
            String[] mSeason = mContext.getResources().getStringArray(R.array.season_array);
            holder.seasonTextView.setText(mSeason[season]);

            // itemSex
            int sex = mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SEX));
            String sexText = "";
            if (sex > 0) {
                String[] mSex = mContext.getResources().getStringArray(R.array.child_sex_array);
                sexText =  mSex[sex];
            }
            if (sexText.isEmpty()) {
                //holder.sexTextView.setHeight(0);
                //holder.sexTextView.setVisibility(View.INVISIBLE);
            } else {
                holder.sexTextView.setText(sexText);
                holder.sexTextView.setVisibility(View.VISIBLE);
            }

            // itemSize1 + itemSize2
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

            if (sizeName1.isEmpty()) {
                //holder.size1TextView.setHeight(0);
                //holder.size1TextView.setVisibility(View.INVISIBLE);
            }

            //itemComment
            String comment2 = mListItemsCursor.getString(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_COMMENT2));
            if (comment2.contentEquals("")) {
                //holder.commentTextView.setHeight(0);
                //holder.commentTextView.setVisibility(View.INVISIBLE);
            } else  {
                holder.commentTextView.setVisibility(View.VISIBLE);
                holder.commentTextView.setText( comment2 );
            }


            // itemSuite
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
                chidrenNames = "";
                holder.suiteChildren.setVisibility(View.VISIBLE);
                holder.suiteChildren.setText(chidrenNames);
            } else {
                chidrenNames = "Подойдет: " + chidrenNames;
                holder.suiteChildren.setVisibility(View.VISIBLE);
                holder.suiteChildren.setText(chidrenNames);
            }


            //            // Header = Location
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
        }

        if (mType.equalsIgnoreCase("child")) {
            byte[] previewInBytes = mListItemsCursor.getBlob(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_PHOTO_PREVIEW));
            Bitmap smallPhoto = GeneralHelper.getBitmapFromBytes(previewInBytes, GeneralHelper.GENERAL_HELPER_CLOTHES_TYPE);
            holder.itemPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.itemPhoto.setImageBitmap(smallPhoto);

            int size1 = mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SIZE_MAIN));
            int size2 = mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SIZE_ADDITIONAL));
            String comment2 = mListItemsCursor.getString(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_COMMENT2));

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
            holder.typeTextView.setText(topRow);


            int sex = mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SEX));
            String sexText = "";
            if (sex > 0) {
                String[] mSex = mContext.getResources().getStringArray(R.array.child_sex_array);
                sexText =  mSex[sex];
            }
            holder.sexTextView.setText(sexText);
            holder.sexTextView.setVisibility(View.VISIBLE);
            int season = mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SEASON));
            String[] mSeason = mContext.getResources().getStringArray(R.array.season_array);
            holder.seasonTextView.setText(mSeason[season]);


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
            if (comment2.contentEquals("")) {
                holder.commentTextView.setHeight(0);
                holder.commentTextView.setVisibility(View.INVISIBLE);
            } else  {
                holder.commentTextView.setVisibility(View.VISIBLE);
                holder.commentTextView.setText(comment2 );
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
        int itemId = mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem._ID));
        int backId, inSet;

        inSet = mDataManager.IsItemInSet(itemId, 1);
        backId = (inSet == 1) ? R.drawable.oval_light : R.drawable.oval_white;
        holder.set1Button.setTag(holder.set1Button.getId(), inSet);
        holder.set1Button.setBackground(mContext.getDrawable(backId));

        inSet = mDataManager.IsItemInSet(itemId, 2);
        backId = (inSet == 1) ? R.drawable.oval_light : R.drawable.oval_white;
        holder.set2Button.setTag(holder.set2Button.getId(), inSet);
        holder.set2Button.setBackground(mContext.getDrawable(backId));

        inSet = mDataManager.IsItemInSet(itemId, 3);
        backId = (inSet == 1) ? R.drawable.oval_light : R.drawable.oval_white;
        holder.set3Button.setTag(holder.set3Button.getId(), inSet);
        holder.set3Button.setBackground(mContext.getDrawable(backId));

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
        private TextView commentTextView;
        private TextView seasonTextView;
        private TextView sexTextView;
        private ImageView itemPhoto;
        private ConstraintLayout mLayout;
        // set's buttons
        private Button set1Button;
        private Button set2Button;
        private Button set3Button;

        ItemListAdapterViewHolder(View view) {
            super(view);
            size1TextView = (TextView) view.findViewById(R.id.report_size1TextView);
            size2TextView = (TextView) view.findViewById(R.id.report_size2TextView);
            typeTextView = (TextView) view.findViewById(R.id.report_typeTextView);
            suiteChildren = (TextView) view.findViewById(R.id.report_suiteChilren);
            commentTextView = (TextView) view.findViewById(R.id.report_comment);
            headerTextView = (TextView) view.findViewById(R.id.report_headerSection);
            itemPhoto = (ImageView) view.findViewById(R.id.item_photo);
            seasonTextView = view.findViewById(R.id.report_season);
            sexTextView = view.findViewById(R.id.report_sex);
            mLayout = (ConstraintLayout) view.findViewById(R.id.report_layout_item);

            set1Button = view.findViewById(R.id.btnsetNumber1);
            set1Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListItemsCursor.moveToPosition(getAdapterPosition());
                    int itemId = mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem._ID));
                    mSetButtonClickHandler.onSetButtonClick(set1Button, 1, itemId);
                }
            });
            set2Button = view.findViewById(R.id.btnsetNumber2);
            set2Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListItemsCursor.moveToPosition(getAdapterPosition());
                    int itemId = mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem._ID));
                    mSetButtonClickHandler.onSetButtonClick(set2Button, 2, itemId);
                }
            });

            set3Button = view.findViewById(R.id.btnsetNumber3);
            set3Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListItemsCursor.moveToPosition(getAdapterPosition());
                    int itemId = mListItemsCursor.getInt(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem._ID));
                    mSetButtonClickHandler.onSetButtonClick(set3Button, 3, itemId);
                }
            });

            view.setOnClickListener(this);
            itemPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bitmap bitmap = null;
                    Uri fullImageUri = Uri.parse(mListItemsCursor.getString(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_LINK_TO_PHOTO)));
                    if (fullImageUri != null) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), fullImageUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (bitmap == null) {
                        //Log.e("PHOTO", "BITMAP IS taken from PREVIEW!");
                        BitmapDrawable drawable = (BitmapDrawable) itemPhoto.getDrawable();
                        bitmap = drawable.getBitmap();
                    }
                    mImageClickHandler.onImageClick(bitmap,view);
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mListItemsCursor.moveToPosition(position);
            //Log.e("ADAPTER","CALL ACTIVITY with position = " + position);
            mItemListAdapterClickHandler.onItemClick(
                    mListItemsCursor.getString(mListItemsCursor.getColumnIndex(WardrobeContract.ClothesItem._ID)),
                    String.valueOf(position)
            );
        }
    }

}
