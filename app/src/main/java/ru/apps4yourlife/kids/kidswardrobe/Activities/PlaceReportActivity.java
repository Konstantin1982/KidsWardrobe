package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.icu.text.UnicodeSetSpanner;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChooseChildDialogFragment;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChoosePhotoApplicationDialogFragment;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChoosePlaceDialogFragment;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChooseSeasonDialogFragment;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChooseTypeDialogFragment;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper;

public class PlaceReportActivity extends AppCompatActivity
        implements
        ChoosePlaceDialogFragment.ChoosePlaceDialogFragmentListener,
        ChooseTypeDialogFragment.ChooseTypeDialogFragmentListener,
        ChooseSeasonDialogFragment.ChooseSeasonDialogFragmentListener,
        ChooseChildDialogFragment.ChooseChildDialogFragmentListener{

    private ArrayList<Integer> mSelectedPlaces;
    private String mChosenPlacesAsString;

    private ArrayList<Integer> mSelectedTypes;
    private String mChosenTypesAsString;

    private ArrayList<Integer> mSelectedSeasons;
    private String mChosenSeasonsAsString;

    private ArrayList<Integer> mSelectedChildren;
    private String mChosenChildrenAsString;




    private WardrobeDBDataManager mDataManager;
    private Cursor mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_report);
        mDataManager = new WardrobeDBDataManager(this);
        mSelectedPlaces = new ArrayList<Integer>();
        mSelectedTypes = new ArrayList<Integer>();
        mSelectedSeasons = new ArrayList<Integer>();
        mSelectedChildren = new ArrayList<Integer>();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void placeTextView_click(View view) {
        mItems = mDataManager.GetAllCommentsWithChecked();
        if (mItems.getCount() > 0) {
            ChoosePlaceDialogFragment mApplicationDialogFragment = new ChoosePlaceDialogFragment();
            mApplicationDialogFragment.setmListener(this);
            mApplicationDialogFragment.show(getSupportFragmentManager(), "ChoosePlaceDialogFragment");
        } else {
            Toast.makeText(this, "Нет мест для выбора", Toast.LENGTH_SHORT).show();
        }
    }
    public void typeTextView_click(View view) {
        mItems = mDataManager.GetAllClothesCategoriesWithChecked();
        if (mItems.getCount() > 0) {
            ChooseTypeDialogFragment mApplicationDialogFragment = new ChooseTypeDialogFragment();
            mApplicationDialogFragment.setmListener(this);
            mApplicationDialogFragment.show(getSupportFragmentManager(), "ChoosePlaceDialogFragment");
        } else {
            Toast.makeText(this, "Нет типов для выбора", Toast.LENGTH_SHORT).show();
        }
    }

    public void childTextView_click(View view) {
        mItems = mDataManager.GetAllChildrenWithChecked();
        if (mItems.getCount() > 0) {
            ChooseChildDialogFragment mApplicationDialogFragment = new ChooseChildDialogFragment();
            mApplicationDialogFragment.setmListener(this);
            mApplicationDialogFragment.show(getSupportFragmentManager(), "ChooseChildDialogFragment");
        } else {
            Toast.makeText(this, "Дети в приложение не добавлены", Toast.LENGTH_SHORT).show();
        }
    }

    public void seazonTextView_click(View view) {
        ChooseSeasonDialogFragment mApplicationDialogFragment = new ChooseSeasonDialogFragment();
        mApplicationDialogFragment.setmListener(this);
        mApplicationDialogFragment.show(getSupportFragmentManager(), "ChoosePlaceDialogFragment");
    }



    @Override
    public void OnClickPlaces(ArrayList<Integer> selectedItems) {
        mChosenPlacesAsString = "";
        mSelectedPlaces = selectedItems;
        if (!mSelectedPlaces.isEmpty()) {
            for (Integer cursorIndex : mSelectedPlaces) {
                if (!mChosenPlacesAsString.isEmpty()) mChosenPlacesAsString = mChosenPlacesAsString.concat(",");
                mItems.moveToPosition(cursorIndex);
                mChosenPlacesAsString = mChosenPlacesAsString.concat(mItems.getString(mItems.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_COMMENT)));
            }
        }
        TextView places = (TextView) findViewById(R.id.placeTextView_PlaceReport);
        places.setText(mChosenPlacesAsString);
    }

    @Override
    public void OnClickTypes(ArrayList<Integer> selectedItems) {
        mChosenTypesAsString = "";
        mSelectedTypes = selectedItems;
        if (!mSelectedTypes.isEmpty()) {
            for (Integer cursorIndex : mSelectedTypes) {
                if (!mChosenTypesAsString.isEmpty()) mChosenTypesAsString = mChosenTypesAsString.concat(",");
                mItems.moveToPosition(cursorIndex);
                mChosenTypesAsString = mChosenTypesAsString.concat(mItems.getString(mItems.getColumnIndex(WardrobeContract.ClothesCategory.COLUMN_CAT_NAME)));
            }
        }
        TextView places = (TextView) findViewById(R.id.typeClothesTextView_PlaceReport);
        places.setText(mChosenTypesAsString);

    }

    @Override
    public void OnClickSeason(boolean[] selectedItems) {
        mChosenSeasonsAsString = "";
        mSelectedSeasons = new ArrayList<Integer>();
        if (selectedItems.length > 0) {
            String[] seasons = getResources().getStringArray(R.array.season_array);
            int i = 0;
            for (String season: seasons) {
                if (selectedItems[i]){
                    if (!mChosenSeasonsAsString.isEmpty()) mChosenSeasonsAsString = mChosenSeasonsAsString.concat(",");
                    mSelectedSeasons.add(i);
                    mChosenSeasonsAsString = mChosenSeasonsAsString.concat(season);
                }
                i++;
            }
        }
        TextView places = (TextView) findViewById(R.id.seazonTextView_PlaceReport);
        places.setText(mChosenSeasonsAsString);

    }

    @Override
    public void OnClickChild(ArrayList<Integer> selectedItems) {
        mChosenChildrenAsString = "";
        mSelectedChildren = selectedItems;
        CheckBox goodSizeCheckBox = (CheckBox) findViewById(R.id.onlyGoodSize_checkBox);
        boolean isChosen = !selectedItems.isEmpty();
        if (isChosen) {
            String[] seasons = getResources().getStringArray(R.array.season_array);
            int i = 0;
            for (int child: selectedItems) {
                if (!mChosenChildrenAsString.isEmpty()) mChosenChildrenAsString = mChosenChildrenAsString.concat(",");
                mItems.moveToPosition(child);
                mChosenChildrenAsString = mChosenChildrenAsString.concat(mItems.getString(mItems.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_NAME)));
            }
        }
        goodSizeCheckBox.setChecked(isChosen);
        goodSizeCheckBox.setEnabled(!isChosen);

        TextView places = (TextView) findViewById(R.id.childTextView_PlaceReport);
        places.setText(mChosenChildrenAsString);

    }

    public void runReport_Places(View view) {
        String SQL = "select * from item join categories join sizes join sizes_names";

        String filterSQL = " 1 = 1 ";
        if (!mSelectedPlaces.isEmpty()) {
            mItems = mDataManager.GetAllCommentsWithChecked();
            filterSQL = filterSQL.concat(" AND ( ");
            String filterPlaces = "";
            for (Integer cursorIndex : mSelectedPlaces) {
                if (!filterPlaces.isEmpty()) {
                    filterPlaces =  filterPlaces.concat(" OR ");
                }
                mItems.moveToPosition(cursorIndex);
                filterPlaces =  filterPlaces.concat(" item.comment LIKE '" + mItems.getString(mItems.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_COMMENT)) + "'");
            }
            filterSQL = filterSQL.concat(filterPlaces +  " ) ");
        }
        //Toast.makeText(this,"After Places: FILTER = " + filterSQL, Toast.LENGTH_LONG).show();

        if (!mSelectedTypes.isEmpty()) {
            String filterTypes = "";
            filterSQL = filterSQL.concat(" AND item.cat_id IN (");
            mItems = mDataManager.GetAllClothesCategoriesWithChecked();
            for (Integer cursorIndex : mSelectedTypes) {
                if (!filterTypes.isEmpty()) {
                    filterTypes =  filterTypes.concat(",");
                }
                mItems.moveToPosition(cursorIndex);
                filterTypes =  filterTypes.concat(mItems.getString(mItems.getColumnIndex(WardrobeContract.ClothesCategory._ID)));
            }
            filterSQL = filterSQL.concat(filterTypes + " ) ");
        }
        TextView places = (TextView) findViewById(R.id.typeClothesTextView_PlaceReport);

        //Toast.makeText(this,"After Types: FILTER = " + filterSQL, Toast.LENGTH_LONG).show();


        if (!mSelectedSeasons.isEmpty()) {
            String filterSeasons = "";
            filterSQL = filterSQL.concat(" AND item.season IN (");
            for (Integer cursorIndex : mSelectedSeasons) {
                if (!filterSeasons.isEmpty()) {
                    filterSeasons =  filterSeasons.concat(",");
                }
                filterSeasons =  filterSeasons.concat(String.valueOf(cursorIndex));
            }
            filterSQL = filterSQL.concat(filterSeasons + " ) ");
        }
        //Toast.makeText(this,"After Seasons: FILTER = " + filterSQL, Toast.LENGTH_LONG).show();

        CheckBox goodSizeCheckBox = (CheckBox) findViewById(R.id.onlyGoodSize_checkBox);
        String filterSizes = GeneralHelper.GetFilterForSizes(this, mSelectedChildren, goodSizeCheckBox.isChecked(),0);
        if (!filterSizes.isEmpty()) {
            filterSQL = filterSQL.concat("AND (item.size in " + filterSizes + "OR item.size2 in " + filterSizes + " ) ");
        }
       // Toast.makeText(this,"After Children1: FILTER = " + filterSQL, Toast.LENGTH_SHORT).show();


        if (!mSelectedChildren.isEmpty()) {
            String filterSex = "";
            //filterSQL = filterSQL.concat(" AND items.sex IN (0,");
            mItems = mDataManager.GetAllChildrenWithChecked();
            for (Integer cursorIndex : mSelectedChildren) {
                mItems.moveToPosition(cursorIndex);
                int sexValue = mItems.getInt(mItems.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_SEX));
                if (sexValue > 0) {
                    filterSex = "(0," + sexValue + ")";
                }
            }
            if (!filterSex.isEmpty()) {
                filterSQL = filterSQL.concat(" AND item.sex IN " + filterSex + " ");
            }
        }
        //Toast.makeText(this,"After Children2: FILTER = " + filterSQL, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, ReportResultListActivity.class);
        intent.putExtra("FILTER",filterSQL);
        intent.putExtra("SORT","comment");
        startActivityForResult(intent,499);
    }

    public void btnUpdateSizes_click(View view) {
        Intent intent = new Intent(this,ChildrenListActivity.class);
        startActivityForResult(intent,999);
    }
}
