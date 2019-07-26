package ru.apps4yourlife.kids.kidswardrobe.Activities;
import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChoosePhotoApplicationDialogFragment;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;


public class AddNewChildActivity extends AppCompatActivity implements
        ChoosePhotoApplicationDialogFragment.ChoosePhotoApplicationDialogListener,
        //LoaderManager.LoaderCallbacks<Uri>,
        AdapterView.OnItemSelectedListener {

    private ImageButton mAddChildButton;
    private Uri mCurrentPhotoUri;
    private Bitmap mPhotoPreview;
    private Button mBirthDateButton;
    private Date mChosenDate;
    private String mCurrentChildID;
    private String mPositionFromList;
    private int mChildSex;
    private Context mContext;
    private Intent mPhotoIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_add_new_child);
        mCurrentChildID = getIntent().getStringExtra("ID");
        mPositionFromList = getIntent().getStringExtra("POSITION");
        //Log.e("ACTIVITY ADD","Received POSITION = " + mPositionFromList);

        Spinner childSexSpinner = (Spinner) findViewById(R.id.child_sex_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.child_sex_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        childSexSpinner.setAdapter(spinnerAdapter);
        childSexSpinner.setOnItemSelectedListener(this);

        if (mCurrentChildID  == null) {
            mChosenDate = new GregorianCalendar(1970, 01, 01).getTime();
            mChildSex = 0;
            mPhotoPreview  = BitmapFactory.decodeResource(getResources(), R.drawable.default_photo);
            mCurrentPhotoUri = null;
        } else {
            //Toast.makeText(this,"ID = " + mCurrentChildID, Toast.LENGTH_SHORT).show();
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
            mAddChildButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mAddChildButton.setImageBitmap(mPhotoPreview);
            // Sex
            mChildSex = currentChildCursor.getInt(currentChildCursor.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_SEX));
            childSexSpinner.setSelection(mChildSex);
            //currentChildCursor.close();
            // SIZE
            Cursor currentChildSizesCursor = new WardrobeDBDataManager(this).GetLatestChildSize(mCurrentChildID);
            if (currentChildSizesCursor.getCount() > 0) {
                EditText childHeight = (EditText) findViewById(R.id.heightChildEditText);
                childHeight.setText(currentChildSizesCursor.getString(currentChildSizesCursor.getColumnIndex(WardrobeContract.ChildSizeEntry.COLUMN_HEIGHT)));

                EditText childFoot = (EditText) findViewById(R.id.footSizeEditText);
                childFoot.setText(currentChildSizesCursor.getString(currentChildSizesCursor.getColumnIndex(WardrobeContract.ChildSizeEntry.COLUMN_FOOT_SIZE)));

                EditText childShoes = (EditText) findViewById(R.id.shoesSizeEditText);
                childShoes.setText(currentChildSizesCursor.getString(currentChildSizesCursor.getColumnIndex(WardrobeContract.ChildSizeEntry.COLUMN_SHOES_SIZE)));
            }
            //currentChildSizesCursor.close();
        }
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
    }

    public void btnAddNewChildPhoto_click(View v) {
        ChoosePhotoApplicationDialogFragment mApplicationDialogFragment = new ChoosePhotoApplicationDialogFragment();
        mApplicationDialogFragment.setmListener(this);
        mApplicationDialogFragment.show(getSupportFragmentManager(),"ChoosePhotoApplicationDialogFragment");
    }


    @Override
    public void onTakeNewPhotoClick() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 100);
        } else {
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_DENIED) {
                onTakeNewPhotoClick();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((requestCode == 1 || requestCode == 0) && resultCode == RESULT_OK) {
            if (requestCode == 1)  {
                //Log.e("PHOTO","Uri is null, getting URI from gallery");
                mCurrentPhotoUri = data.getData();
            }
            if (mCurrentPhotoUri != null) {
                //Log.e("PHOTO","Uri is OK, getting photo from URI");
                // move it to LoaderComplete
                mAddChildButton = (ImageButton) findViewById(R.id.addNewChildImageButton);
                mPhotoPreview = new GeneralHelper().transform(GeneralHelper.resizeBitmapFile(this, mAddChildButton.getWidth()-10, mAddChildButton.getHeight()-10, mCurrentPhotoUri));
                mAddChildButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mAddChildButton.setImageBitmap(mPhotoPreview);
                mAddChildButton.setBackground(null);
            }
            else {
                if (requestCode == 0) {
                    //Log.e("PHOTO","Uri is NOT OK");
                    // GET Async TASK
                    GetPhotoURITask getPhotoTask = new GetPhotoURITask();
                    getPhotoTask.execute();

                } else {
                    Toast.makeText(this,"Ошибка при передаче фотографии. Попробуйте еще раз!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    public void onChooseFromGalleryClick() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 1);
    }

    public void btnSaveNewChild_click() {
        //TODO: it can be cicked if photo was not changed!
        // TODO: get sex
        boolean formIsOK = true;
        WardrobeDBDataManager dataManager = new WardrobeDBDataManager(this);
        TextView mName = (TextView) findViewById(R.id.nameChild);
        String mNameValue = mName.getText().toString();
        if (mNameValue.isEmpty()) {
            formIsOK = false;
            mName.setError(getResources().getString(R.string.error_name_undefinded));
            return;
        }

        if (formIsOK) {
            long res = dataManager.InsertOrUpdateChild(
                    mNameValue,
                    mChildSex,
                    mChosenDate.getTime(),
                    mCurrentPhotoUri,
                    mPhotoPreview,
                    mCurrentChildID);
            if (mCurrentChildID == null || mCurrentChildID.isEmpty()) mCurrentChildID = String.valueOf(res);
            //TODO: after insert action
            double childFoot = GeneralHelper.GetDoubleValueFromEditText((EditText) findViewById(R.id.footSizeEditText));
            double childHeight = GeneralHelper.GetDoubleValueFromEditText((EditText) findViewById(R.id.heightChildEditText));
            double childShoes = GeneralHelper.GetDoubleValueFromEditText((EditText) findViewById(R.id.shoesSizeEditText));
            if (childFoot + childHeight + childShoes > 0) {
                long resSize =
                        dataManager.InsertOrUpdateChildSize(
                            Long.valueOf(mCurrentChildID),
                            childHeight,
                            childFoot,
                            childShoes);

                //Toast.makeText(this, "Sizes for Child were updated. Child ID = " + mCurrentChildID, Toast.LENGTH_SHORT).show();

            }

            //Toast.makeText(this, "New chils has been inserted: " + res, Toast.LENGTH_SHORT).show();
            Intent data = new Intent();
            data.putExtra("POSITION", mPositionFromList);
            setResult(1, data);
            finish();
        }

        // size


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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        //Toast.makeText(this,"Variant clicked: " + position,Toast.LENGTH_SHORT).show();
        mChildSex = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //Toast.makeText(this,"Nothing clicked!!!",Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save_child, menu);
        return true;
    }

    public void LooseFocus() {
        findViewById(R.id.main_layout_add_new_child).requestFocus();
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_save :
                LooseFocus();
                btnSaveNewChild_click();
                return true;
            case android.R.id.home:
                LooseFocus();
                setResult(0);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class GetPhotoURITask extends AsyncTask<Void,Void,Void> {

        private int res = 0;

        @Override
        protected Void doInBackground(Void... voids) {
            //Log.e("PHOTO","ASYNC TASK IS STARTED!!!!!!");
            int counter = 0;
            while (mCurrentPhotoUri == null) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    counter++;
                    if (counter > 10) {
                        return null;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            res = 1;
            //Log.e("PHOTO","URI IS OK IN ASYNC TASK");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (res == 0) Toast.makeText(mContext,"При сохранении фотографии произошла ошибка. Попробуйте еще раз!", Toast.LENGTH_LONG).show();
            if (res == 1) {
                mAddChildButton = (ImageButton) findViewById(R.id.addNewChildImageButton);
                mPhotoPreview = new GeneralHelper().transform(GeneralHelper.resizeBitmapFile(mContext, mAddChildButton.getWidth()-10, mAddChildButton.getHeight()-10, mCurrentPhotoUri));
                mAddChildButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mAddChildButton.setImageBitmap(mPhotoPreview);
                mAddChildButton.setBackground(null);
            }
        }
    }

}
