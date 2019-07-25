package ru.apps4yourlife.kids.kidswardrobe.Activities;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChoosePhotoApplicationDialogFragment;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChooseSizeTypesDialogFragment;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper;

public class AddNewItemActivity extends AppCompatActivity
        implements
            ChoosePhotoApplicationDialogFragment.ChoosePhotoApplicationDialogListener,
            ChooseSizeTypesDialogFragment.ChooseSizeTypesDialogListener,
            AdapterView.OnItemSelectedListener
{
    private static final String TOAST_TEXT = "Test ads are being shown. ";
    private static final int TYPE_KIND_CLOTHES = 0;
    private static final int SIZES_VALUES = 1;
    private static final int TYPE_COMMENTS = 2;

    private WardrobeDBDataManager mDataManager;

    private Uri mCurrentPhotoUri;
    private Bitmap mPhotoPreview;
    private ImageButton maddNewItemImageButton;
    private String mPositionFromList;

    private AutoCompleteTextView mTypeClothesTextView;
    private Cursor mClothesCategoriesCursor;
    // predefined Type
    private String mPreType = "";
    private long mPreTypeID = 0;
    private boolean mNeedSaveType;
    // predefined sizes types
    private int mPreTypeSize1;
    private int mPreTypeSize2;
    private int mPreTypeSize1_old;
    private int mPreTypeSize2_old;


    private int mItemID = 0;
    // sex and season
    private int mSeason = 0;
    private int mSex = 0;
    private Context mContext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreTypeSize1_old = -1;
        mPreTypeSize2_old = -1;

        setContentView(R.layout.activity_add_new_item);
        mContext = this;
        mDataManager = new WardrobeDBDataManager(this);

        maddNewItemImageButton = (ImageButton) findViewById(R.id.addNewItemImageButton);

        // Fill TYPES VALUE
        mTypeClothesTextView = (AutoCompleteTextView) findViewById(R.id.typeClothesTextView);
        ArrayAdapter<String> mAutoCompleteTextViewAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                getList(TYPE_KIND_CLOTHES, 0)
        );
        mTypeClothesTextView.setAdapter(mAutoCompleteTextViewAdapter);
        mTypeClothesTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (isFocused) {
                    mTypeClothesTextView.showDropDown();
                } else {
                    checkIsItNewValue(mTypeClothesTextView.getText().toString());

                }
            }
        });
        mTypeClothesTextView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Object item = adapterView.getItemAtPosition(position);
                if (item!= null) {
                    checkIsItNewValue(item.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mTypeClothesTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Object item = adapterView.getItemAtPosition(position);
                if (item!= null) {
                    checkIsItNewValue(item.toString());
                }

            }
        });

        final AutoCompleteTextView commentTextView = (AutoCompleteTextView) findViewById(R.id.commentEditText);
        mAutoCompleteTextViewAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                getList(TYPE_COMMENTS, 0)
        );
        commentTextView.setAdapter(mAutoCompleteTextViewAdapter);
        commentTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (isFocused) {
                    commentTextView.showDropDown();
                }
            }
        });


        String sentId = getIntent().getStringExtra("ID");
        if (sentId != null &&  !sentId.isEmpty()) {
            mItemID = Integer.valueOf(sentId);
        }

        mPositionFromList = getIntent().getStringExtra("POSITION");
        Spinner itemSexSpinner = (Spinner) findViewById(R.id.spinner_sex_item);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.child_sex_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        itemSexSpinner.setAdapter(spinnerAdapter);
        itemSexSpinner.setOnItemSelectedListener(this);

        Spinner itemSeasonSpinner = (Spinner) findViewById(R.id.spinner_season);
        ArrayAdapter<CharSequence> spinnerAdapter2 = ArrayAdapter.createFromResource(this, R.array.season_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        itemSeasonSpinner.setAdapter(spinnerAdapter2);
        itemSeasonSpinner.setOnItemSelectedListener(this);


        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);

        if (mItemID == 0) {
            mCurrentPhotoUri = null;
            mPhotoPreview = null;
            String catIdString = getIntent().getStringExtra("CATEGORY_ID");
            if (catIdString != null &&  !catIdString.isEmpty()) {
                // predefined Category
                long cat_id = Long.valueOf(catIdString);
                WardrobeDBDataManager dataManager = new WardrobeDBDataManager(this);
                if (cat_id != dataManager.GetDefaultCategoryId()) { //
                    Cursor currentCat = mDataManager.GetCategoryById(cat_id);
                    mPreType = currentCat.getString(currentCat.getColumnIndex(WardrobeContract.ClothesCategory.COLUMN_CAT_NAME));
                    mPreTypeID = cat_id;
                    mTypeClothesTextView.setText(currentCat.getString(currentCat.getColumnIndex(WardrobeContract.ClothesCategory.COLUMN_CAT_NAME)));
                    updateAdaptersForSizes(
                            currentCat.getInt(currentCat.getColumnIndex(WardrobeContract.ClothesCategory.COLUMN_SIZE_TYPE)),
                            currentCat.getInt(currentCat.getColumnIndex(WardrobeContract.ClothesCategory.COLUMN_SIZE_TYPE_ADDITIONAL))
                    );
                }
            }
        } else {
            Cursor currentItemCursor = mDataManager.GetItemById(mItemID);
            // фотография + путь к ней
            mCurrentPhotoUri = Uri.parse(currentItemCursor.getString(currentItemCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_LINK_TO_PHOTO)));

            byte[] previewInBytes = currentItemCursor.getBlob(currentItemCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_PHOTO_PREVIEW));
            mPhotoPreview = GeneralHelper.getBitmapFromBytes(previewInBytes,GeneralHelper.GENERAL_HELPER_CHILD_TYPE);
            maddNewItemImageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
            maddNewItemImageButton.setImageBitmap(mPhotoPreview);
            maddNewItemImageButton.setBackground(null);


            // категория
            long cat_id = currentItemCursor.getLong(currentItemCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_CAT_ID));
            Cursor currentCat = mDataManager.GetCategoryById(cat_id);
            mPreType = currentCat.getString(currentCat.getColumnIndex(WardrobeContract.ClothesCategory.COLUMN_CAT_NAME));
            mPreTypeID = cat_id;
            mTypeClothesTextView.setText(currentCat.getString(currentCat.getColumnIndex(WardrobeContract.ClothesCategory.COLUMN_CAT_NAME)));
            // сезон (СПИННЕР)
            mSeason = currentItemCursor.getInt(currentItemCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SEASON));
            itemSeasonSpinner.setSelection(mSeason);
            // для кого (СПИННЕР)
            mSex = currentItemCursor.getInt(currentItemCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SEX));
            itemSexSpinner.setSelection(mSex);
            // размер 1
            // размер 2
            mPreTypeSize1 = currentCat.getInt(currentCat.getColumnIndex(WardrobeContract.ClothesCategory.COLUMN_SIZE_TYPE));
            mPreTypeSize2 =    currentCat.getInt(currentCat.getColumnIndex(WardrobeContract.ClothesCategory.COLUMN_SIZE_TYPE_ADDITIONAL));
            updateAdaptersForSizes(mPreTypeSize1, mPreTypeSize2);

            Cursor sizeCursor = mDataManager.GetSizesValuesById(currentItemCursor.getLong(currentItemCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SIZE_MAIN)));
            Cursor sizeCursor2 = mDataManager.GetSizesValuesById(currentItemCursor.getLong(currentItemCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_SIZE_ADDITIONAL)));

            if (sizeCursor.getCount() > 0) {
                AutoCompleteTextView sizeClothesTextView = (AutoCompleteTextView) findViewById(R.id.sizeClothesTextView);
                sizeClothesTextView.setText(sizeCursor.getString(sizeCursor.getColumnIndex(WardrobeContract.Sizes.COLUMN_VALUE)));
            }

            if (sizeCursor2.getCount() > 0) {
                AutoCompleteTextView sizeClothesTextView2 = (AutoCompleteTextView) findViewById(R.id.sizeTypeAdditionalTextView);
                sizeClothesTextView2.setText(sizeCursor2.getString(sizeCursor2.getColumnIndex(WardrobeContract.Sizes.COLUMN_VALUE)));
            }

            // комментарий
            commentTextView.setText(currentItemCursor.getString(currentItemCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_COMMENT)));
            //currentItemCursor.close();
            //currentCat.close();
            //sizeCursor.close();
            //sizeCursor2.close();
        }

    }

    private List<String> getList (int typeOfList, int typeOfValues) {
        List<String> mList = new ArrayList<>();
        switch (typeOfList) {
            case TYPE_KIND_CLOTHES:
                mClothesCategoriesCursor = mDataManager.GetAllClothesCategories(true);
                if (mClothesCategoriesCursor.getCount() > 0) {
                    for (int i = 0; i < mClothesCategoriesCursor.getCount(); i++) {
                        mClothesCategoriesCursor.moveToPosition(i);
                        mList.add(
                                mClothesCategoriesCursor.getString(
                                        mClothesCategoriesCursor.getColumnIndex(
                                                WardrobeContract.ClothesCategory.COLUMN_CAT_NAME
                                        )
                                )
                        );
                    }
                }
                break;
            case SIZES_VALUES:
                Cursor mSizesValuesCursor = mDataManager.GetSizesValuesByType(typeOfValues);
                if (mSizesValuesCursor.getCount() > 0) {
                    for (int i = 0; i < mSizesValuesCursor.getCount(); i++) {
                        mSizesValuesCursor.moveToPosition(i);
                        mList.add(
                                mSizesValuesCursor.getString(
                                        mSizesValuesCursor.getColumnIndex(
                                                WardrobeContract.Sizes.COLUMN_VALUE
                                        )
                                )
                        );
                    }
                }
                //mSizesValuesCursor.close();
                break;
            case TYPE_COMMENTS:
                Cursor commentsCursor = mDataManager.GetAllComments();
                if (commentsCursor.getCount() > 0) {
                    for (int i = 0; i < commentsCursor.getCount(); i++) {
                        commentsCursor.moveToPosition(i);
                        String comment = commentsCursor.getString(commentsCursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_COMMENT));
                        if (!comment.isEmpty()) {
                            mList.add(comment);
                        }
                    }
                }
                //commentsCursor.close();
                break;

        }
        return mList;
    }

    private void updateAdaptersForSizes(int newSizeTypeMain, int newSizeTypeAdditional) {

        final  AutoCompleteTextView sizeClothesTextView;
        // MAIN
        sizeClothesTextView = (AutoCompleteTextView) findViewById(R.id.sizeClothesTextView);
        ArrayAdapter<String> autoCompleteTextViewAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                getList(SIZES_VALUES, newSizeTypeMain)
        );
        sizeClothesTextView.setAdapter(autoCompleteTextViewAdapter);
        sizeClothesTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (isFocused) {
                    sizeClothesTextView.showDropDown();
                }
            }
        });
        TextView sizeLabel = (TextView) findViewById(R.id.sizeMainLabel);
        sizeLabel.setText(mDataManager.GetSizeTypeName(newSizeTypeMain));


        // ADDITIONAL sizeTypeAdditionalTextView
        final  AutoCompleteTextView sizeClothesTextView2;
        sizeClothesTextView2 = (AutoCompleteTextView) findViewById(R.id.sizeTypeAdditionalTextView);
        ArrayAdapter<String> autoCompleteTextViewAdapter2 = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                getList(SIZES_VALUES, newSizeTypeAdditional)
        );
        sizeClothesTextView2.setAdapter(autoCompleteTextViewAdapter2);
        sizeClothesTextView2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (isFocused) {
                    sizeClothesTextView2.showDropDown();
                }
            }
        });
        sizeLabel = (TextView) findViewById(R.id.sizeTypeAdditionalLabel);
        sizeLabel.setText(mDataManager.GetSizeTypeName(newSizeTypeAdditional));


        if (mPreTypeSize1_old != newSizeTypeMain) {
            sizeClothesTextView.setText("");
        }
        if (mPreTypeSize2_old != newSizeTypeAdditional) {
            sizeClothesTextView2.setText("");
        }

        mPreTypeSize1_old = mPreTypeSize1;
        mPreTypeSize2_old = mPreTypeSize2;

        mPreTypeSize1 = newSizeTypeMain;
        mPreTypeSize2 = newSizeTypeAdditional;


    }


    private void checkIsItNewValue(String chosenType) {
        mNeedSaveType = false;
        if (!chosenType.equalsIgnoreCase("") && !chosenType.equalsIgnoreCase(mPreType)) {
            boolean isFind = false;
            mPreType = chosenType;
            for (int i = 0; i < mClothesCategoriesCursor.getCount(); i++) {
                mClothesCategoriesCursor.moveToPosition(i);
                String savedType = mClothesCategoriesCursor.getString(mClothesCategoriesCursor.getColumnIndex(WardrobeContract.ClothesCategory.COLUMN_CAT_NAME));
                if (savedType.toLowerCase().equals(chosenType.toLowerCase())) {
                    // it is not new.
                    updateAdaptersForSizes(
                            mClothesCategoriesCursor.getInt(
                                    mClothesCategoriesCursor.getColumnIndex(WardrobeContract.ClothesCategory.COLUMN_SIZE_TYPE)),
                            mClothesCategoriesCursor.getInt(
                                    mClothesCategoriesCursor.getColumnIndex(WardrobeContract.ClothesCategory.COLUMN_SIZE_TYPE_ADDITIONAL))
                    );
                    mPreTypeID = mClothesCategoriesCursor.getLong(mClothesCategoriesCursor.getColumnIndex(WardrobeContract.ClothesCategory._ID));
                    isFind = true;
                    break;
                }
            }
            mNeedSaveType = !isFind;
            if (!isFind) {
                // TODO show button to link category and size!!!
                FloatingActionButton warningButton = (FloatingActionButton) findViewById(R.id.warningSizeButton);
                warningButton.setVisibility(View.VISIBLE);

            }
        }

    }
    public void btnAddNewItemPhoto_click(View view) {
        ChoosePhotoApplicationDialogFragment mApplicationDialogFragment = new ChoosePhotoApplicationDialogFragment();
        mApplicationDialogFragment.setmListener(this);
        mApplicationDialogFragment.show(getSupportFragmentManager(),"ChoosePhotoApplicationDialogFragment");
    }

    @Override
    public void onTakeNewPhotoClick() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
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
                mCurrentPhotoUri = data.getData();
            }
            if (mCurrentPhotoUri != null) {
                mPhotoPreview = GeneralHelper.resizeBitmapFile(this, maddNewItemImageButton.getWidth() - 10, maddNewItemImageButton.getHeight() - 10, mCurrentPhotoUri);
                maddNewItemImageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
                maddNewItemImageButton.setImageBitmap(mPhotoPreview);
                maddNewItemImageButton.setBackground(null);
            } else {
                if (requestCode == 0) {
                    GetPhotoURITask2 getPhotoTask = new GetPhotoURITask2();
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

    public void btnShowSizeTypeDialog_click(View view) {
        LooseFocus();
        ChooseSizeTypesDialogFragment applicationDialogFragment = new ChooseSizeTypesDialogFragment();
        applicationDialogFragment.setmListener(this);
        applicationDialogFragment.show(getSupportFragmentManager(),"ChooseSizeTypesDialogFragment");
    }

    public void onNewSizesTypesDefined(int type1, int type2) {
        updateAdaptersForSizes(type1, type2);
        mPreTypeSize1 = type1;
        mPreTypeSize2 = type2;
        //Toast.makeText(this,"TYpes: " + type1 + type2, Toast.LENGTH_SHORT).show();
        FloatingActionButton warningButton = (FloatingActionButton) findViewById(R.id.warningSizeButton);
        warningButton.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        int menuId;
        if (mItemID > 0) {
            menuId = R.menu.menu_save_edit_item;
        } else {
            menuId = R.menu.menu_save_child;
        }
        inflater.inflate(menuId, menu);
        return true;
    }

    public void LooseFocus() {
        findViewById(R.id.main_layout_add_new_item).requestFocus();
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
                btnSaveNewItem_click();
                return true;
            case R.id.menu_item_delete :
                LooseFocus();
                btnDeleteItem_click();
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

    public void btnDeleteItem_click() {
        if (mItemID > 0) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.are_you_sure_to_delete)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ApproveDeleteRecord();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            builder.create().show();
        }
    }

    public void ApproveDeleteRecord() {
        if (mItemID > 0) {
            WardrobeDBDataManager dataManager = new WardrobeDBDataManager(this);
            int result = dataManager.DeleteItemById(mItemID);
            setResult(1);
            finish();
        }
    }

    public void btnSaveNewItem_click() {
        boolean formIsOK = true;
        FloatingActionButton warningButton = (FloatingActionButton) findViewById(R.id.warningSizeButton);
        if (warningButton.getVisibility() == View.VISIBLE || mPhotoPreview == null) {
            formIsOK = false;
            // TODO: animation of warning button
            if (mPhotoPreview == null) {
                Toast.makeText(this,"Фотография - обязательна.", Toast.LENGTH_SHORT).show();
            }
        }

        if (formIsOK) {
            WardrobeDBDataManager dataManager = new WardrobeDBDataManager(this);
            // Insert Type with size types if needed
            if (mNeedSaveType) {
                mPreTypeID = dataManager.InsertOrUpdateClothesCategory(0,mPreType,mPreTypeSize1,mPreTypeSize2);
            }
            if (mPreTypeID == 0) {
                mPreTypeID = dataManager.GetDefaultCategoryId();
            }

            // insert size values
            AutoCompleteTextView sizeClothesTextView = (AutoCompleteTextView) findViewById(R.id.sizeClothesTextView);
            long sizeValue1 =     dataManager.FindOrInsertNewSizeValue(mPreTypeSize1, sizeClothesTextView.getText().toString());

            AutoCompleteTextView sizeClothesTextView2 = (AutoCompleteTextView) findViewById(R.id.sizeTypeAdditionalTextView);
            long sizeValue2 =     dataManager.FindOrInsertNewSizeValue(mPreTypeSize2, sizeClothesTextView2.getText().toString());

            EditText commentEdit  = (EditText ) findViewById(R.id.commentEditText);
            long new_id = dataManager.InsertOrUpdateItem(mItemID, mPreTypeID, mCurrentPhotoUri, mPhotoPreview, mSeason, mSex, sizeValue1, sizeValue2, commentEdit.getText().toString());
            //Toast.makeText(this, "New item has been inserted: " + new_id , Toast.LENGTH_SHORT).show();
            Intent data = new Intent();
            data.putExtra("POSITION", mPositionFromList);
            setResult(1, data);
            finish();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        Spinner spinner = (Spinner) adapterView;
        if (spinner.getId() == R.id.spinner_season) {
            mSeason = position;
        }
        if (spinner.getId() == R.id.spinner_sex_item) {
            mSex = position;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onDestroy() {
        //if (mClothesCategoriesCursor != null) mClothesCategoriesCursor.close();
        super.onDestroy();
    }
    public class GetPhotoURITask2 extends AsyncTask<Void,Void,Void> {

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
                mPhotoPreview = GeneralHelper.resizeBitmapFile(mContext, maddNewItemImageButton.getWidth() - 10, maddNewItemImageButton.getHeight() - 10, mCurrentPhotoUri);
                maddNewItemImageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
                maddNewItemImageButton.setImageBitmap(mPhotoPreview);
                maddNewItemImageButton.setBackground(null);
            }
        }
    }

}
