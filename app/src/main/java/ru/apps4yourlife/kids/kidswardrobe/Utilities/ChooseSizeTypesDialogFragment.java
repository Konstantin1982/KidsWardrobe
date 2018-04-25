package ru.apps4yourlife.kids.kidswardrobe.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;

/**
 * Created by ksharafutdinov on 25-Apr-18.
 */

public class ChooseSizeTypesDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener  {

    private int mType1;
    private int mType2;

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mSizeTypesCursor.moveToPosition(i);
        int chosenType = mSizeTypesCursor.getInt(mSizeTypesCursor.getColumnIndex(WardrobeContract.SizesTypes.COLUMN_ID));
        Spinner spinner = (Spinner) adapterView;
        if (spinner.getId() == R.id.spinner_type_size_dialog) {
            mType1 = chosenType;
        }
        if (spinner.getId() == R.id.spinner_type_size_2_dialog) {
            mType2 = chosenType;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public interface ChooseSizeTypesDialogListener {
        public void onNewSizesTypesDefined(int type1, int type2);
    }

    ChooseSizeTypesDialogListener mListener;
    Cursor mSizeTypesCursor;

    public void setmListener(Context context) {
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ChooseSizeTypesDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ChoosePhotoApplicationDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mType2 = 0;
        mType1 = 0;

        mSizeTypesCursor = new WardrobeDBDataManager(this.getContext()).GetAllSizesTypes();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //LayoutInflater inflater = this.getLayoutInflater();
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.size_types_list, null);

        Spinner mainSizeTypeSpinner = (Spinner) v.findViewById(R.id.spinner_type_size_dialog);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_spinner_item,getSizeTypesName(mSizeTypesCursor));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mainSizeTypeSpinner.setAdapter(spinnerAdapter);
        mainSizeTypeSpinner.setOnItemSelectedListener(this);


        Spinner additionalSizeTypeSpinner = (Spinner) v.findViewById(R.id.spinner_type_size_2_dialog);
        ArrayAdapter<String> spinnerAdapter2 = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_spinner_item,getSizeTypesName(mSizeTypesCursor));
        spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        additionalSizeTypeSpinner.setAdapter(spinnerAdapter2);
        additionalSizeTypeSpinner.setOnItemSelectedListener(this);

        builder.setView(v);
        builder.setTitle(R.string.dialog_choose_size_types);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked OK Button
                mListener.onNewSizesTypesDefined(mType1,mType2);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked cancel button
            }
        });
        return builder.create();
    }

    public List<String> getSizeTypesName(Cursor sizeTypes) {
        List<String> resList = new ArrayList<String>();
        for (int i = 0; i < sizeTypes.getCount(); i++) {
            sizeTypes.moveToPosition(i);
            resList.add(sizeTypes.getString(sizeTypes.getColumnIndex(WardrobeContract.SizesTypes.COLUMN_SIZE_TYPE_NAME)));
        }

        return resList;
    }
}
