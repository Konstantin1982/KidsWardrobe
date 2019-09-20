package ru.apps4yourlife.kids.kidswardrobe.Data;
import android.provider.BaseColumns;

/**
 * Created by ksharafutdinov on 27-Mar-18.
 */

public class WardrobeContract {

    // Children
    public static final class ChildEntry implements BaseColumns {
        public static final String TABLE_NAME = "children";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SEX = "sex";
        public static final String COLUMN_BIRTHDATE = "birthdate";
        public static final String COLUMN_PHOTO_PREVIEW = "photo_preview";
        public static final String COLUMN_LINK_TO_PHOTO = "photo";
    }


    // Settings ??
    public static final class SettingsEntry implements BaseColumns {
        public static final String TABLE_NAME = "settings";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_VALUE = "value";
    }


    // Sizes of Children
    public static final class ChildSizeEntry implements BaseColumns {
        public static final String TABLE_NAME = "child_size";
        public static final String COLUMN_CHILD_ID = "child_id";
        public static final String COLUMN_DATE_ENTERED = "date_entered";
        public static final String COLUMN_HEIGHT = "height";
        public static final String COLUMN_FOOT_SIZE = "foot_size";
        public static final String COLUMN_SHOES_SIZE = "shoes_size";
    }



    // CLOTHES

    // Clothes_Item
    public static final class ClothesItem implements BaseColumns {
        public static final String TABLE_NAME = "item";
        public static final String COLUMN_CAT_ID = "cat_id";
        public static final String COLUMN_PHOTO_PREVIEW = "photo_preview";
        public static final String COLUMN_LINK_TO_PHOTO = "photo";
        public static final String COLUMN_SEASON = "season";
        public static final String COLUMN_SEX = "sex";
        //public static final String COLUMN_SHOES_SIZE = "shoes_size";
        public static final String COLUMN_SIZE_MAIN = "size";
        public static final String COLUMN_SIZE_ADDITIONAL = "size2";
        public static final String COLUMN_COMMENT = "comment";
        public static final String COLUMN_COMMENT2 = "comment2";

    }
    // categories
    public static final class ClothesCategory implements BaseColumns {
        public static final String TABLE_NAME = "category";
        public static final String COLUMN_CAT_NAME = "name";
        public static final String COLUMN_SIZE_TYPE = "size_type";
        public static final String COLUMN_SIZE_TYPE_ADDITIONAL = "size_type2";
    }
    // size
    public static final class Sizes implements BaseColumns {
        public static final String TABLE_NAME = "sizes";
        public static final String COLUMN_SIZE_TYPE = "size_type";
        public static final String COLUMN_VALUE = "shown_value";
        public static final String COLUMN_REAL_VALUE = "real_value";
    }

    // size_type
    public static final class SizesTypes implements BaseColumns {
        public static final String TABLE_NAME = "sizes_types";
        public static final String COLUMN_SIZE_TYPE_NAME = "name";
        public static final String COLUMN_ID = "type_id";
    }

    // set_items
    public static final class ItemsSets implements BaseColumns {
        public static final String TABLE_NAME = "items_sets";
        public static final String COLUMN_ITEM_ID = "item_id";
        public static final String COLUMN_SET_ID = "set_id"; // 1, 2, 3
        public static final String COLUMN_SORT_ORDER = "sort_order";
    }


}
