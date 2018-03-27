package ru.apps4yourlife.kids.kidswardrobe.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
        return result;
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

}
