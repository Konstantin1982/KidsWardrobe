package ru.apps4yourlife.kids.kidswardrobe.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;

/**
 * Created by ksharafutdinov on 18-May-18.
 */

public class ChoosePlaceDialogFragment extends DialogFragment {

    private ArrayList<Integer> mSelectedItems;
    private Context mContext;
    private WardrobeDBDataManager mDataManager;

    public interface ChoosePlaceDialogFragmentListener {
        void OnClickPlaces(ArrayList<Integer> selectedItems);
    }

    private ChoosePlaceDialogFragmentListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = this.getContext();
        mSelectedItems = new ArrayList();  // Where we track the selected items
        mDataManager = new WardrobeDBDataManager(mContext);
        Cursor mItems = mDataManager.GetAllCommentsWithChecked();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_places)
                .setMultiChoiceItems(
                        mItems,
                        "CHECKED",
                        WardrobeContract.ClothesItem.COLUMN_COMMENT,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(mContext, mSelectedItems.toString(), Toast.LENGTH_SHORT).show();
                        mListener.OnClickPlaces(mSelectedItems);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }



    public void setmListener(Context context) {
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ChoosePlaceDialogFragmentListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ChoosePlaceDialogFragmentListener");
        }
    }

}
