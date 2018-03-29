package ru.apps4yourlife.kids.kidswardrobe.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * Created by ksharafutdinov on 27-Mar-18.
 */

public class WardrobeDBDataManager {
    private WardrobeDBHelper mDBHelper;


    public WardrobeDBDataManager(Context context) {
        mDBHelper = new WardrobeDBHelper(context);
    }

    public  long InsertNewChild(String childName, int childSex, Date childBirthdate, String linkToPhoto, Bitmap smallPhoto) {
        long result = 0;
        ContentValues newChildValues = new ContentValues();
        newChildValues.put(WardrobeContract.ChildEntry.COLUMN_NAME, childName);
        newChildValues.put(WardrobeContract.ChildEntry.COLUMN_SEX, childSex);
        newChildValues.put(WardrobeContract.ChildEntry.COLUMN_BIRTHDATE, childBirthdate.toString());
        newChildValues.put(WardrobeContract.ChildEntry.COLUMN_LINK_TO_PHOTO, linkToPhoto);

        byte[] smallPhotoBytes = getBytes(smallPhoto);
        newChildValues.put(WardrobeContract.ChildEntry.COLUMN_PHOTO_PREVIEW, smallPhotoBytes);

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        result = db.insert(WardrobeContract.ChildEntry.TABLE_NAME,null,newChildValues);
        db.close();
        return result;
    }

    private static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public boolean DeleteDatabase(Context context) {
        return context.deleteDatabase(mDBHelper.getDatabaseName());
    }

    public int getChildrenCount() {
        int result = 0;
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        return result;
    }

}
