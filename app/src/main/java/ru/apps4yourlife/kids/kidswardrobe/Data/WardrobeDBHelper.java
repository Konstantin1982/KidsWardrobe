package ru.apps4yourlife.kids.kidswardrobe.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by ksharafutdinov on 27-Mar-18.
 */

public class WardrobeDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "wardrobe.db";
    public static final int DATABASE_VERSION = 1;

    private Context mContext;

    public WardrobeDBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Toast.makeText(mContext,"Oncreate DB is called",Toast.LENGTH_LONG).show();
        final String SQL_CREATE_CHILDREN_TABLE =
                "CREATE TABLE " +
                        WardrobeContract.ChildEntry.TABLE_NAME + "(" +
                        WardrobeContract.ChildEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WardrobeContract.ChildEntry.COLUMN_NAME + " VARCHAR(255), " +
                        WardrobeContract.ChildEntry.COLUMN_SEX + " INTEGER, " +
                        WardrobeContract.ChildEntry.COLUMN_BIRTHDATE + " DATE DEFAULT NULL, " +
                        WardrobeContract.ChildEntry.COLUMN_LINK_TO_PHOTO + " VARCHAR(255), " +
                        WardrobeContract.ChildEntry.COLUMN_PHOTO_PREVIEW + " BLOB" +
                        ")";

        final String SQL_CREATE_SETTINGS_TABLE =
                "CREATE TABLE " +
                        WardrobeContract.SettingsEntry.TABLE_NAME + "(" +
                        WardrobeContract.SettingsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WardrobeContract.SettingsEntry.COLUMN_KEY + " VARCHAR(255), " +
                        WardrobeContract.SettingsEntry.COLUMN_VALUE  + " VARCHAR(255) " +
                        ")";
        sqLiteDatabase.execSQL(SQL_CREATE_CHILDREN_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SETTINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
