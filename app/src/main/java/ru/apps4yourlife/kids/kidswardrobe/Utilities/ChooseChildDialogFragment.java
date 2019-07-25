package ru.apps4yourlife.kids.kidswardrobe.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;

/**
 * Created by ksharafutdinov on 18-May-18.
 */

public class ChooseChildDialogFragment extends DialogFragment {

    private ArrayList<Integer> mSelectedItems;
    private Context mContext;
    private WardrobeDBDataManager mDataManager;

    public interface ChooseChildDialogFragmentListener {
        void OnClickChild(ArrayList<Integer> selectedItems);
    }

    private ChooseChildDialogFragmentListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = this.getContext();
        mSelectedItems = new ArrayList();  // Where we track the selected items
        mDataManager = new WardrobeDBDataManager(mContext);
        Cursor mItems = mDataManager.GetAllChildrenWithChecked();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_places)
                .setSingleChoiceItems(
                        mItems,
                        -1,
                        WardrobeContract.ChildEntry.COLUMN_NAME,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSelectedItems = new ArrayList();
                                mSelectedItems.add(which);
                            }
                        })
                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(mContext, mSelectedItems.toString(), Toast.LENGTH_SHORT).show();
                        mListener.OnClickChild(mSelectedItems);
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
            mListener = (ChooseChildDialogFragmentListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ChoosePlaceDialogFragmentListener");
        }
    }

}
