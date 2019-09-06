package ru.apps4yourlife.kids.kidswardrobe.Utilities;

import android.app.Dialog;
import android.content.Context;
import androidx.fragment.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;


import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ru.apps4yourlife.kids.kidswardrobe.R;

/**
 * Created by ksharafutdinov on 29-Oct-18.
 */

public class ChooseRestoreFolderDialogFragment extends DialogFragment {

    private int mSelectedPosition;
    private Context mContext;
    private FileList filesList;

    public interface ChooseRestoreFolderDialogFragmentListener {
        void OnClickRestoreName(int position);
        FileList SetParameters();
    }

    private ChooseRestoreFolderDialogFragment.ChooseRestoreFolderDialogFragmentListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        mContext = this.getContext();
        mSelectedPosition = 0;

       filesList =  mListener.SetParameters();
       final int count = filesList.getFiles().size() > 10 ? 10 : filesList.getFiles().size();


        String items[] = new String[count];
        int i = 0;
        //ArrayList<String> items = new ArrayList<>();
        for (File file : filesList.getFiles())  {
            String createdTmString = "";
            DateTime createdTm = file.getCreatedTime();
            try {
                SimpleDateFormat currentFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Date date = currentFormat.parse(createdTm.toStringRfc3339());
                SimpleDateFormat goodFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                createdTmString = goodFormat.format(date);
            } catch (Exception e) {
                createdTmString = createdTm.toString();
                e.printStackTrace();
            }
            String newTitle = "Создан " + createdTmString;
            items[i] = newTitle;
            i++;
            if (i >= count) break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
