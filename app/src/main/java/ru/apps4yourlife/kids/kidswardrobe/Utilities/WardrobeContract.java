package ru.apps4yourlife.kids.kidswardrobe.Utilities;
import android.provider.BaseColumns;

/**
 * Created by ksharafutdinov on 27-Mar-18.
 */

public class WardrobeContract {

    public static final class ChildEntry implements BaseColumns {
        public static final String TABLE_NAME = "children";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SEX = "sex";
        public static final String COLUMN_BIRTHDATE = "birthdate";
        public static final String COLUMN_PHOTO_PREVIEW = "photo_preview";
        public static final String COLUMN_LINK_TO_PHOTO = "photo";
    }


}
