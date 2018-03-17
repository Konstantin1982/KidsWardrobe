package ru.apps4yourlife.kids.kidswardrobe.Utilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import ru.apps4yourlife.kids.kidswardrobe.R;

/**
 * Created by ksharafutdinov on 15-Mar-18.
 */

public  class ChoosePhotoApplicationDialogFragment extends DialogFragment {
    public interface ChoosePhotoApplicationDialogListener {
        public void onTakeNewPhotoClick();
        public void onChooseFromGalleryClick();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // setTitle(R.string.dialog_choose_application_title)
        builder
                .setItems(R.array.dialog_choose_application_items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // CREATE A NEW PHOTO
                                mListener.onTakeNewPhotoClick();
                                break;
                        }
                    }
                });
        return builder.create();
    }

    ChoosePhotoApplicationDialogListener mListener;

    public void setmListener(Context context) {
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ChoosePhotoApplicationDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ChoosePhotoApplicationDialogListener");
        }
    }
}
