package ru.apps4yourlife.kids.kidswardrobe.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;

/**
 * Created by ksharafutdinov on 18-May-18.
 */

public class ChooseTypeDialogFragment extends DialogFragment {

    private ArrayList<Integer> mSelectedItems;
    private Context mContext;
    private WardrobeDBDataManager mDataManager;

    public interface ChooseTypeDialogFragmentListener {
        void OnClickTypes(ArrayList<Integer> selectedItems);
    }

    private ChooseTypeDialogFragmentListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = this.getContext();
        mSelectedItems = new ArrayList();  // Where we track the selected items
        mDataManager = new WardrobeDBDataManager(mContext);
        Cursor mItems = mDataManager.GetAllClothesCategoriesWithChecked();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_types)
                .setMultiChoiceItems(
                        mItems,
                        "CHECKED",
                        WardrobeContract.ClothesCategory.COLUMN_CAT_NAME,
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
                      //  Toast.makeText(mContext, mSelectedItems.toString(), Toast.LENGTH_SHORT).show();
                        mListener.OnClickTypes(mSelectedItems);
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
            mListener = (ChooseTypeDialogFragmentListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ChoosePlaceDialogFragmentListener");
        }
    }

}
