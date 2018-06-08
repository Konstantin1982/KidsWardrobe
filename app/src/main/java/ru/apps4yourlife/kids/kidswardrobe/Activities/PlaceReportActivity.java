package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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
        mSelectedChildren = new ArrayList<Integer>();
        if (!selectedItems.isEmpty()) {
            String[] seasons = getResources().getStringArray(R.array.season_array);
            int i = 0;
            for (int child: selectedItems) {
                if (!mChosenChildrenAsString.isEmpty()) mChosenChildrenAsString = mChosenChildrenAsString.concat(",");
                mItems.moveToPosition(child);
                mChosenChildrenAsString = mChosenChildrenAsString.concat(mItems.getString(mItems.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_NAME)));
            }
        }
        TextView places = (TextView) findViewById(R.id.childTextView_PlaceReport);
        places.setText(mChosenChildrenAsString);

    }
}
