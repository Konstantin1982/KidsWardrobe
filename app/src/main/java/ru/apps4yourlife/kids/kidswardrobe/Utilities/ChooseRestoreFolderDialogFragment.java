package ru.apps4yourlife.kids.kidswardrobe.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;

import java.util.ArrayList;
import java.util.List;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;

/**
 * Created by ksharafutdinov on 29-Oct-18.
 */

public class ChooseRestoreFolderDialogFragment extends android.support.v4.app.DialogFragment {

    private int mSelectedPosition;
    private Context mContext;
    private MetadataBuffer metadataBuffer;

    public interface ChooseRestoreFolderDialogFragmentListener {
        void OnClickRestoreName(int position);
        MetadataBuffer SetParameters();
    }

    private ChooseRestoreFolderDialogFragment.ChooseRestoreFolderDialogFragmentListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        mContext = this.getContext();
        mSelectedPosition = 0;
        /*
        mDataManager = new WardrobeDBDataManager(mContext);
        Cursor mItems = mDataManager.GetAllCommentsWithChecked();
        */

       metadataBuffer =  mListener.SetParameters();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String items[] = new String[5];
        //ArrayList<String> items = new ArrayList<>();
        for (int i = 0; i < metadataBuffer.getCount(); i++) {
            if (i > 4) break;
            Metadata data = metadataBuffer.get(i);

            String title = data.getTitle();
            String newTitle = "Создан " + title.substring(0,4) + "-" + title.substring(4,6) + "-" + title.substring(6,8) + " в " + title.substring(9,11) + ":" + title.substring(11,13);
            items[i] = newTitle;
        }
        builder.setTitle("Выберите копию для восстановления")
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedPosition = i;
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(mContext, mSelectedItems.toString(), Toast.LENGTH_SHORT).show();
                        mListener.OnClickRestoreName(mSelectedPosition);
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
            mListener = (ChooseRestoreFolderDialogFragment.ChooseRestoreFolderDialogFragmentListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ChoosePlaceDialogFragmentListener");
        }
    }


}
