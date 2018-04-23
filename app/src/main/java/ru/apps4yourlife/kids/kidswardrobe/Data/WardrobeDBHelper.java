package ru.apps4yourlife.kids.kidswardrobe.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.apps4yourlife.kids.kidswardrobe.Activities.AddNewItemActivity;
import ru.apps4yourlife.kids.kidswardrobe.R;

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

    public void CreateTables(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_CHILDREN_TABLE =
                "CREATE TABLE " +
                        WardrobeContract.ChildEntry.TABLE_NAME + "(" +
                        WardrobeContract.ChildEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WardrobeContract.ChildEntry.COLUMN_NAME + " VARCHAR(255), " +
                        WardrobeContract.ChildEntry.COLUMN_SEX + " INTEGER, " +
                        WardrobeContract.ChildEntry.COLUMN_BIRTHDATE + " INTEGER, " +
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

        final String SQL_CREATE_CHILD_SIZE_TABLE =
                "CREATE TABLE " +
                        WardrobeContract.ChildSizeEntry.TABLE_NAME + "(" +
                        WardrobeContract.ChildSizeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WardrobeContract.ChildSizeEntry.COLUMN_CHILD_ID + " INTEGER, " +
                        WardrobeContract.ChildSizeEntry.COLUMN_DATE_ENTERED + " INTEGER, " +
                        WardrobeContract.ChildSizeEntry.COLUMN_HEIGHT + " DOUBLE, " +
                        WardrobeContract.ChildSizeEntry.COLUMN_FOOT_SIZE + " DOUBLE, " +
                        WardrobeContract.ChildSizeEntry.COLUMN_SHOES_SIZE + " DOUBLE" +
                        ")";

        final String SQL_CREATE_CLOTHES_ITEM_TABLE =
                "CREATE TABLE " +
                        WardrobeContract.ClothesItem.TABLE_NAME + "(" +
                        WardrobeContract.ClothesItem._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WardrobeContract.ClothesItem.COLUMN_CAT_ID + " INTEGER, " +
                        WardrobeContract.ClothesItem.COLUMN_SEX + " INTEGER, " +
                        WardrobeContract.ClothesItem.COLUMN_SEASON + " INTEGER, " +
                        //WardrobeContract.ClothesItem.COLUMN_SHOES_SIZE + " VARCHAR(255), " +
                        WardrobeContract.ClothesItem.COLUMN_SIZE_MAIN + " INTEGER, " +
                        WardrobeContract.ClothesItem.COLUMN_SIZE_ADDITIONAL + " INTEGER, " +
                        WardrobeContract.ClothesItem.COLUMN_COMMENT + " VARCHAR(255), " +
                        WardrobeContract.ClothesItem.COLUMN_LINK_TO_PHOTO + " VARCHAR(255), " +
                        WardrobeContract.ClothesItem.COLUMN_PHOTO_PREVIEW + " BLOB" +
                        ")";

        final String SQL_CREATE_CLOTHES_CATEGORY_TABLE =
                "CREATE TABLE " +
                        WardrobeContract.ClothesCategory.TABLE_NAME + "(" +
                        WardrobeContract.ClothesCategory._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WardrobeContract.ClothesCategory.COLUMN_CAT_NAME + " VARCHAR(255), " +
                        WardrobeContract.ClothesCategory.COLUMN_SIZE_TYPE + " INTEGER, " +
                        WardrobeContract.ClothesCategory.COLUMN_SIZE_TYPE_ADDITIONAL + " INTEGER" +
                        ")";

        final String SQL_CREATE_CLOTHES_SIZES_TABLE =
                "CREATE TABLE " +
                        WardrobeContract.Sizes.TABLE_NAME + "(" +
                        WardrobeContract.Sizes._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WardrobeContract.Sizes.COLUMN_SIZE_TYPE + " INTEGER, " +
                        WardrobeContract.Sizes.COLUMN_VALUE + " VARCHAR(255)" +
                        ")";

        final String SQL_CREATE_CLOTHES_SIZES_TYPES_TABLE =
                "CREATE TABLE " +
                        WardrobeContract.SizesTypes.TABLE_NAME + "(" +
                        WardrobeContract.SizesTypes._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WardrobeContract.SizesTypes.COLUMN_ID + " INTEGER, " +
                        WardrobeContract.SizesTypes.COLUMN_SIZE_TYPE_NAME + " VARCHAR(255)" +
                        ")";



        sqLiteDatabase.execSQL(SQL_CREATE_CHILDREN_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SETTINGS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CHILD_SIZE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CLOTHES_ITEM_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CLOTHES_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CLOTHES_SIZES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CLOTHES_SIZES_TYPES_TABLE);
        Log.e("DB", "TABLES WERE CREATED");
    }

    public void InsertInitialValuesToClothesCategories(SQLiteDatabase db) {
        String[] typesInXML = mContext.getResources().getStringArray(R.array.clothes_categories_values);
        //db.beginTransaction();
        try {
            for (String type : typesInXML) {
                ContentValues values = new ContentValues();
                String[] parcedType = type.split("\\|");
                values.put(WardrobeContract.ClothesCategory.COLUMN_SIZE_TYPE, Integer.valueOf(parcedType[0]));
                values.put(WardrobeContract.ClothesCategory.COLUMN_SIZE_TYPE_ADDITIONAL, Integer.valueOf(parcedType[1]));
                values.put(WardrobeContract.ClothesCategory.COLUMN_CAT_NAME, parcedType[2]);
                db.insert(WardrobeContract.ClothesCategory.TABLE_NAME, null, values);
            }
        } finally {
          //  db.endTransaction();
        }
    }

    public void InsertInitialValuesToSizes(SQLiteDatabase db) {
        String[] typesInXML = mContext.getResources().getStringArray(R.array.sizes);
        //db.beginTransaction();
        try {
            for (String type : typesInXML) {
                ContentValues values = new ContentValues();
                String[] parcedSizes = type.split("\\|");
                values.put(WardrobeContract.Sizes.COLUMN_SIZE_TYPE, Integer.valueOf(parcedSizes[0]));
                values.put(WardrobeContract.Sizes.COLUMN_VALUE, parcedSizes[1]);
                db.insert(WardrobeContract.Sizes.TABLE_NAME, null, values);
            }
        } finally {
            //db.endTransaction();
        }
    }

    public void InsertInitialValuesToSizesTypes(SQLiteDatabase db) {
        String[] typesInXML = mContext.getResources().getStringArray(R.array.sizes_types);
        // db.beginTransaction();
        try {
            for (String type : typesInXML) {
                ContentValues values = new ContentValues();
                String[] parcedSizesTypes = type.split("\\|");
                values.put(WardrobeContract.SizesTypes.COLUMN_ID, Integer.valueOf(parcedSizesTypes[0]));
                values.put(WardrobeContract.SizesTypes.COLUMN_SIZE_TYPE_NAME, parcedSizesTypes[1]);
                db.insert(WardrobeContract.SizesTypes.TABLE_NAME, null, values);
            }
        } finally {
            // db.endTransaction();
        }
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Toast.makeText(mContext,"Oncreate DB is called",Toast.LENGTH_LONG).show();
        Log.e("DB","DB IS CREATED!!");

        CreateTables(sqLiteDatabase);

        InsertInitialValuesToClothesCategories(sqLiteDatabase);
        InsertInitialValuesToSizes(sqLiteDatabase);
        InsertInitialValuesToSizesTypes(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
