package ru.apps4yourlife.kids.kidswardrobe.Activities;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChoosePhotoApplicationDialogFragment;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;


public class AddNewChildActivity extends AppCompatActivity implements ChoosePhotoApplicationDialogFragment.ChoosePhotoApplicationDialogListener  {

    private ImageButton mAddChildButton;
    private String mCurrentPhotoPath;
    private Bitmap mPhotoPreview;
    private Button mBirthDateButton;
    private Date mChosenDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_child);
        mChosenDate = new GregorianCalendar(1970,01,01).getTime();
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
                mCurrentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ru.apps4yourlife.kids.fileprovider",
                        photoFile);
                takePictureIntent = GeneralHelper.prepareTakePhotoIntent(takePictureIntent, this, photoURI);
                startActivityForResult(takePictureIntent, 0);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            mAddChildButton = (ImageButton) findViewById(R.id.addNewChildImageButton);
            mPhotoPreview = GeneralHelper.resizeBitmapFile(mAddChildButton.getWidth(), mAddChildButton.getHeight(), mCurrentPhotoPath);
            mAddChildButton.setImageBitmap(mPhotoPreview);
        }
    }


    @Override
    public void onChooseFromGalleryClick() {

    }

    public void btnSaveNewChild_click(View view) {
        // TODO: validate form
        // TODO: get sex
        WardrobeDBDataManager dataManager = new WardrobeDBDataManager(this);
        TextView mName = (TextView) findViewById(R.id.nameChild);
        long res = dataManager.InsertNewChild(
                    mName.getText().toString(),
                    0,
                    mChosenDate.getTime(),
                    mCurrentPhotoPath,
                    mPhotoPreview);
        Toast.makeText(this,"New chils has been inserted: " + res,Toast.LENGTH_SHORT).show();

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
