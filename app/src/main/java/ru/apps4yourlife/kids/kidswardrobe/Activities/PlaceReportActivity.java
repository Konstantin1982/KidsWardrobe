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
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChoosePhotoApplicationDialogFragment;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChoosePlaceDialogFragment;

public class PlaceReportActivity extends AppCompatActivity  implements ChoosePlaceDialogFragment.ChoosePlaceDialogFragmentListener {

    private ArrayList<Integer> mSelectedPlaces;
    private String mChosenPlacesAsString;
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
}
