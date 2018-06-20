package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChooseChildDialogFragment;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChoosePlaceDialogFragment;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChooseSeasonDialogFragment;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChooseTypeDialogFragment;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper;

public class ChildReportActivity extends AppCompatActivity
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
            setContentView(R.layout.activity_child_report);
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
        TextView places = (TextView) findViewById(R.id.placeTextView_ChildReport);
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
        TextView places = (TextView) findViewById(R.id.typeClothesTextView_ChildReport);
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
        TextView places = (TextView) findViewById(R.id.seazonTextView_ChildReport);
        places.setText(mChosenSeasonsAsString);

    }

    @Override
    public void OnClickChild(ArrayList<Integer> selectedItems) {
        mChosenChildrenAsString = "";
        mSelectedChildren = selectedItems;
        boolean isChosen = !selectedItems.isEmpty();
        if (isChosen) {
            int i = 0;
            for (int child: selectedItems) {
                if (!mChosenChildrenAsString.isEmpty()) mChosenChildrenAsString = mChosenChildrenAsString.concat(",");
                mItems.moveToPosition(child);
                mChosenChildrenAsString = mChosenChildrenAsString.concat(mItems.getString(mItems.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_NAME)));
            }
        }
        TextView places = (TextView) findViewById(R.id.childTextView_ChildReport);
        places.setText(mChosenChildrenAsString);

    }

    public void runReport_Children(View view) {
        ArrayList<Integer> tmpChildrenSelection = new ArrayList<Integer>();
        if (mSelectedChildren.isEmpty()) {
            Cursor childIDs = mDataManager.GetChildrenIDsFromDb();
            if (childIDs.getCount() > 0) {
                for (int i = 0; i < childIDs.getCount(); i++) {
                    childIDs.moveToPosition(i);
                    mSelectedChildren.add(childIDs.getInt(childIDs.getColumnIndex(WardrobeContract.ChildEntry._ID)));
                }
            }
        } else {
            mItems = mDataManager.GetAllChildrenWithChecked();
            tmpChildrenSelection = mSelectedChildren;
            mSelectedChildren = new ArrayList<Integer>();
            int i = 0;
            for (int child: tmpChildrenSelection) {
                mItems.moveToPosition(child);
                mSelectedChildren.add(mItems.getInt(mItems.getColumnIndex(WardrobeContract.ChildEntry._ID)));
            }
        }
        if (mSelectedChildren.isEmpty()) {
            Toast.makeText(this,"В приложении нет ни одного ребенка", Toast.LENGTH_LONG);
        } else {
            String SQL = "";
            String filterSQL = "WHERE  1 = 1 ";
            for (Integer currentChildId : mSelectedChildren) {
                Cursor childCursor = mDataManager.GetChildByIdFromDb(String.valueOf(currentChildId));

                if (!SQL.isEmpty()) {
                    SQL = SQL.concat(" UNION ALL ");
                }
                SQL = SQL +  "select " + currentChildId + " as child, * from item ";
                // join category ON item.cat_id = category._id join sizes on (item.size = sizes._id OR item.size2 = sizes._id)" +                         " join sizes_types ON sizes.size_type = sizes_types.type_id ";

                filterSQL = " WHERE  1 = 1 ";
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
                TextView places = (TextView) findViewById(R.id.typeClothesTextView_ChildReport);

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
                ArrayList<Integer> tmp = new ArrayList<Integer>();
                tmp.add(currentChildId);
                String filterSizes = GeneralHelper.GetFilterForSizes(this, tmp , true,1);
                if (!filterSizes.isEmpty()) {
                    filterSQL = filterSQL.concat("AND ((item.size in " + filterSizes + " OR item.size2 in " + filterSizes + " ) OR (item.size = 0 AND item.size2 = 0)) ");
                }


                String filterSex = "";
                //filterSQL = filterSQL.concat(" AND items.sex IN (0,");
                int sexValue = childCursor.getInt(childCursor.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_SEX));
                if (sexValue > 0) {
                    filterSex = "(0," + sexValue + ")";
                }
                if (!filterSex.isEmpty()) {
                    filterSQL = filterSQL.concat(" AND item.sex IN " + filterSex + " ");
                }

                SQL = SQL + filterSQL;
            }
            Toast.makeText(this,"Final SQL = " + SQL, Toast.LENGTH_LONG).show();
            Log.e("SQL",SQL);
            // TODO: rework stupid feature;
            mSelectedChildren = tmpChildrenSelection;
            Intent intent = new Intent(this, ReportResultListActivity.class);
            intent.putExtra("FILTER",filterSQL);
            intent.putExtra("SORT","child");
            intent.putExtra("QUERY", SQL);
            startActivityForResult(intent,499);
        }
    }

    public void btnUpdateSizes_click(View view) {
        Intent intent = new Intent(this,ChildrenListActivity.class);
        startActivityForResult(intent,999);
    }}
