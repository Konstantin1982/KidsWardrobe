package ru.apps4yourlife.kids.kidswardrobe.Activities;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChoosePhotoApplicationDialogFragment;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;


public class AddNewChildActivity extends AppCompatActivity implements ChoosePhotoApplicationDialogFragment.ChoosePhotoApplicationDialogListener  {

    private ImageButton mAddChildButton;
    private Uri mCurrentPhotoUri;
    private Bitmap mPhotoPreview;
    private Button mBirthDateButton;
    private Date mChosenDate;
    private String mCurrentChildID;
    private String mPositionFromList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_child);
        mCurrentChildID = getIntent().getStringExtra("ID");
        mPositionFromList = getIntent().getStringExtra("POSITION");
        Log.e("ACTIVITY ADD","Received POSITION = " + mPositionFromList);
        if (mCurrentChildID  == null) {
            mChosenDate = new GregorianCalendar(1970, 01, 01).getTime();
        } else {
            // TODO: fill parameters for child
            Toast.makeText(this,"ID = " + mCurrentChildID, Toast.LENGTH_SHORT).show();
            Cursor currentChildCursor = new WardrobeDBDataManager(this).GetChildByIdFromDb(mCurrentChildID);
            // Name
            TextView mName = (TextView) findViewById(R.id.nameChild);
            mName.setText(currentChildCursor.getString(currentChildCursor.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_NAME)));
            //Age
            long birthDateAsLong = currentChildCursor.getLong(currentChildCursor.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_BIRTHDATE));
            String birthDateAsString = GeneralHelper.getStringFromBirthDate(birthDateAsLong,this.getResources().getString(R.string.birthdate_undefinded));
            mBirthDateButton = (Button) findViewById(R.id.birthdate_button);
            mBirthDateButton.setText(birthDateAsString);
            mChosenDate = new Date(birthDateAsLong);
            //Photo
            byte[] previewInBytes = currentChildCursor.getBlob(currentChildCursor.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_PHOTO_PREVIEW));
            Bitmap smallPhoto = GeneralHelper.getBitmapFromBytes(previewInBytes,GeneralHelper.GENERAL_HELPER_CHILD_TYPE);
            mAddChildButton = (ImageButton) findViewById(R.id.addNewChildImageButton);
            mPhotoPreview = smallPhoto;
            mAddChildButton.setImageBitmap(mPhotoPreview);
            //Path to photo??
        }
    }

    public void btnAddNewChildPhoto_click(View v) {
        ChoosePhotoApplicationDialogFragment mApplicationDialogFragment = new ChoosePhotoApplicationDialogFragment();
        mApplicationDialogFragment.setmListener(this);
        mApplicationDialogFragment.show(getSupportFragmentManager(),"ChoosePhotoApplicationDialogFragment");
    }


    @Override
    public void onTakeNewPhotoClick() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = GeneralHelper.createImageFile(this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                mCurrentPhotoUri = FileProvider.getUriForFile(this,
                        "ru.apps4yourlife.kids.fileprovider",
                        photoFile);
                takePictureIntent = GeneralHelper.prepareTakePhotoIntent(takePictureIntent, this, mCurrentPhotoUri);
                startActivityForResult(takePictureIntent, 0);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((requestCode == 1 || requestCode == 0) && resultCode == RESULT_OK) {
            if (requestCode == 1) {
                mCurrentPhotoUri = data.getData();
            }
            mAddChildButton = (ImageButton) findViewById(R.id.addNewChildImageButton);
            mPhotoPreview = GeneralHelper.resizeBitmapFile(this, mAddChildButton.getWidth(), mAddChildButton.getHeight(), mCurrentPhotoUri);
            mAddChildButton.setImageBitmap(mPhotoPreview);
        }
    }


    @Override
    public void onChooseFromGalleryClick() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 1);
    }

    public void btnSaveNewChild_click(View view) {
        // TODO: validate form
        // TODO: get sex
        boolean formIsOK = true;
        WardrobeDBDataManager dataManager = new WardrobeDBDataManager(this);
        TextView mName = (TextView) findViewById(R.id.nameChild);
        String mNameValue = mName.getText().toString();
        if (mNameValue.isEmpty()) {
            formIsOK = false;
            mName.setError(getResources().getString(R.string.error_name_undefinded));
        }

        if (mPhotoPreview == null) {
            mPhotoPreview  = BitmapFactory.decodeResource(getResources(), R.drawable.default_photo);
        }

        if (formIsOK) {
            long res = dataManager.InsertOrUpdateChild(
                    mNameValue,
                    0,
                    mChosenDate.getTime(),
                    mCurrentPhotoUri.toString(),
                    mPhotoPreview,
                    mCurrentChildID);
            //TODO: after insert action
            Toast.makeText(this, "New chils has been inserted: " + res, Toast.LENGTH_SHORT).show();
            Intent data = new Intent();
            data.putExtra("POSITION", mPositionFromList);
            setResult(1, data);
            finish();
        }
    }
    public void btnSetBirthDate_click (View view) {
        Calendar currentCalendar = new GregorianCalendar();
        new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                        mBirthDateButton = (Button) findViewById(R.id.birthdate_button);
                        Calendar calendar = new GregorianCalendar(year,monthOfYear,dayOfMonth);
                        mChosenDate = calendar.getTime();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd");
                        mBirthDateButton.setText(dateFormat.format(calendar.getTime()));
                    }
                },
                currentCalendar.get(currentCalendar.YEAR),
                currentCalendar.get(currentCalendar.MONTH),
                currentCalendar.get(currentCalendar.DAY_OF_MONTH)).show();
    }
}
