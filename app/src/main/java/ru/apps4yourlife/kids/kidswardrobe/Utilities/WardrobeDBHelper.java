package ru.apps4yourlife.kids.kidswardrobe.Utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ksharafutdinov on 27-Mar-18.
 */

public class WardrobeDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "wardrobe.db";
    public static final int DATABASE_VERSION = 1;

    public WardrobeDBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_CHILDREN_TABLE =
                "CREATE TABLE " +
                        WardrobeContract.ChildEntry.TABLE_NAME + "(" +
                        WardrobeContract.ChildEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WardrobeContract.ChildEntry.COLUMN_NAME + " VARCHAR(255), " +
                        WardrobeContract.ChildEntry.COLUMN_SEX + " INTEGER, " +
                        WardrobeContract.ChildEntry.COLUMN_BIRTHDATE + " DATE, " +
                        WardrobeContract.ChildEntry.COLUMN_LINK_TO_PHOTO + " VARCHAR(255), " +
                        WardrobeContract.ChildEntry.COLUMN_PHOTO_PREVIEW + " BLOB" +
                        ")";
        sqLiteDatabase.execSQL(SQL_CREATE_CHILDREN_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
