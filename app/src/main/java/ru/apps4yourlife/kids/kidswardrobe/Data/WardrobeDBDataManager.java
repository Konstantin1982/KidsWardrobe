package ru.apps4yourlife.kids.kidswardrobe.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.ads.doubleclick.CustomRenderedAd;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper;

/**
 * Created by ksharafutdinov on 27-Mar-18.
 */

public class WardrobeDBDataManager {
    private WardrobeDBHelper mDBHelper;
    private Context mContext;


    public WardrobeDBDataManager(Context context) {
        mDBHelper = new WardrobeDBHelper(context);
        mContext = context;
        //mDBHelper.getWritableDatabase(); // just to fix crash
    }

    public  long InsertOrUpdateChild(String childName, int childSex, long childBirthdate, Uri linkToPhoto, Bitmap smallPhoto, String idEntry) {
        long result = 0;

        ContentValues newChildValues = new ContentValues();
        newChildValues.put(WardrobeContract.ChildEntry.COLUMN_NAME, childName);
        newChildValues.put(WardrobeContract.ChildEntry.COLUMN_SEX, childSex);
        newChildValues.put(WardrobeContract.ChildEntry.COLUMN_BIRTHDATE, childBirthdate);//TODO: int to string
        String stringLinktoPhoto = "";
        if (linkToPhoto != null) {
            stringLinktoPhoto = linkToPhoto.toString();
        }

        newChildValues.put(WardrobeContract.ChildEntry.COLUMN_LINK_TO_PHOTO, stringLinktoPhoto);
        byte[] smallPhotoBytes = getBytes(smallPhoto);
        newChildValues.put(WardrobeContract.ChildEntry.COLUMN_PHOTO_PREVIEW, smallPhotoBytes);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        if (idEntry == null) {
            Toast.makeText(mContext, "To Insert: = " + childBirthdate, Toast.LENGTH_SHORT).show();
            result = db.insert(WardrobeContract.ChildEntry.TABLE_NAME, null, newChildValues);
        } else {
            result = db.update(WardrobeContract.ChildEntry.TABLE_NAME, newChildValues, WardrobeContract.ChildEntry._ID + " = ? ", new String[]{idEntry});
        }
        db.close();
        return result;
    }

    public long InsertOrUpdateChildSize(long idChild, double height, double footSize, double shoesSize) {
        long result = 0;

        long todayDateAsLong = GeneralHelper.GetCurrentDate().getTime();
        ContentValues newValues = new ContentValues();
        newValues.put(WardrobeContract.ChildSizeEntry.COLUMN_CHILD_ID, idChild);
        newValues.put(WardrobeContract.ChildSizeEntry.COLUMN_DATE_ENTERED, todayDateAsLong);
        newValues.put(WardrobeContract.ChildSizeEntry.COLUMN_HEIGHT, height);
        newValues.put(WardrobeContract.ChildSizeEntry.COLUMN_FOOT_SIZE, footSize);
        newValues.put(WardrobeContract.ChildSizeEntry.COLUMN_SHOES_SIZE, shoesSize);

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        // check this date select 1 from size where date = current date
        Cursor tmpCursor = db.query(
                WardrobeContract.ChildSizeEntry.TABLE_NAME,
                null,
                WardrobeContract.ChildSizeEntry.COLUMN_DATE_ENTERED + " = ?" ,
                new String[] { String.valueOf(todayDateAsLong)},
                null,
                null,
                WardrobeContract.ChildSizeEntry._ID);
        if (tmpCursor.getCount() > 0) {
            result = db.update(
                        WardrobeContract.ChildSizeEntry.TABLE_NAME,
                        newValues,
                        WardrobeContract.ChildSizeEntry.COLUMN_DATE_ENTERED + " = ?" ,
                        new String[] { String.valueOf(todayDateAsLong)}
                    );
        } else  {
            result = db.insert(WardrobeContract.ChildSizeEntry.TABLE_NAME, null, newValues);
        }
        tmpCursor.close();
        return result;
    }

    public Cursor GetLatestChildSize(String childId) {
        Cursor result = mDBHelper.getReadableDatabase().query(
                WardrobeContract.ChildSizeEntry.TABLE_NAME,
                null,
                WardrobeContract.ChildSizeEntry.COLUMN_CHILD_ID + " = ?",
                new String[] {String.valueOf(childId)},
                null,
                null,
                WardrobeContract.ChildSizeEntry.COLUMN_DATE_ENTERED + " DESC",
                "1");
        result.moveToFirst();
        return result;
    }



    // TODO: all size from child
    // TODO: latest size from child

    private static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public boolean DeleteDatabase(Context context) {
        return context.deleteDatabase(mDBHelper.getDatabaseName());
    }

    public Cursor GetChildrenListFromDb(String filtration) {
        Cursor childrenList = mDBHelper.getReadableDatabase().query(
                                    WardrobeContract.ChildEntry.TABLE_NAME,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    WardrobeContract.ChildEntry._ID);
        return childrenList;
    }
    public Cursor GetChildByIdFromDb(String ID) {
        Cursor childrenList = mDBHelper.getReadableDatabase().query(
                                    WardrobeContract.ChildEntry.TABLE_NAME,
                                    null,
                            WardrobeContract.ChildEntry._ID + " = ?",
                                     new String[] {String.valueOf(ID)},
                                    null,
                                    null,
                                    WardrobeContract.ChildEntry._ID);
        childrenList.moveToFirst();
        return childrenList;
    }



    public Cursor GetAllClothesCategories() {
        Cursor categoryList = mDBHelper.getReadableDatabase().query(
                WardrobeContract.ClothesCategory.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WardrobeContract.ClothesCategory.COLUMN_CAT_NAME + ", " + WardrobeContract.ClothesCategory._ID);
        return categoryList;

    }


    public Cursor GetAllSizesTypes() {
        Cursor sizeTypes = mDBHelper.getReadableDatabase().query(
                WardrobeContract.SizesTypes.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WardrobeContract.SizesTypes.COLUMN_ID);
        return sizeTypes;
    }

    public String GetSizeTypeName(int ID) {
        String sizeTypeName = "Размер";
        Cursor sizeType = mDBHelper.getReadableDatabase().query(
                WardrobeContract.SizesTypes.TABLE_NAME,
                null,
                WardrobeContract.SizesTypes.COLUMN_ID + " = ? ",
                new String[] {String.valueOf(ID)},
                null,
                null,
                WardrobeContract.SizesTypes.COLUMN_ID);
        if (sizeType.getCount() > 0) {
            sizeType.moveToFirst();
            sizeTypeName = sizeType.getString(sizeType.getColumnIndex(WardrobeContract.SizesTypes.COLUMN_SIZE_TYPE_NAME));
        }
        return sizeTypeName;
    }

    public Cursor GetSizesValuesByType(int type) {
        Cursor sizeValues = mDBHelper.getReadableDatabase().query(
                WardrobeContract.Sizes.TABLE_NAME,
                null,
                WardrobeContract.Sizes.COLUMN_SIZE_TYPE + " = ? ",
                new String[] {String.valueOf(type)},
                null,
                null,
                WardrobeContract.Sizes._ID);
        if (sizeValues.getCount() > 0) {
            sizeValues.moveToFirst();
        }
        return sizeValues;

    }

}
