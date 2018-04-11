package ru.apps4yourlife.kids.kidswardrobe.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * Created by ksharafutdinov on 27-Mar-18.
 */

public class WardrobeDBDataManager {
    private WardrobeDBHelper mDBHelper;
    private Context mContext;


    public WardrobeDBDataManager(Context context) {
        mDBHelper = new WardrobeDBHelper(context);
        mContext = context;
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

}
