package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChoosePhotoApplicationDialogFragment;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChooseSizeTypesDialogFragment;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper;

public class AddNewItemActivity extends AppCompatActivity implements ChoosePhotoApplicationDialogFragment.ChoosePhotoApplicationDialogListener, ChooseSizeTypesDialogFragment.ChooseSizeTypesDialogListener {
    private static final String TOAST_TEXT = "Test ads are being shown. ";
    private static final int TYPE_KIND_CLOTHES = 0;
    private static final int SIZES_VALUES = 1;

    private WardrobeDBDataManager mDataManager;


    private Uri mCurrentPhotoUri;
    private Bitmap mPhotoPreview;
    private ImageButton maddNewItemImageButton;


    private AutoCompleteTextView mTypeClothesTextView;
    private Cursor mClothesCategoriesCursor;
    private Cursor mSizesValuesCursor;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);
        mDataManager = new WardrobeDBDataManager(this);

        /*
        AdView adView = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        // Toasts the test ad message on the screen. Remove this after defining your own ad unit ID.
        Toast.makeText(this, TOAST_TEXT, Toast.LENGTH_LONG).show();
        mDetailShown = 0;
*/
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
        // mTypeClothesTextView.dropDown
        //


    }

    private List<String> getList (int typeOfList, int typeOfValues) {
        List<String> mList = new ArrayList<>();
        switch (typeOfList) {
            case TYPE_KIND_CLOTHES:
                mClothesCategoriesCursor = mDataManager.GetAllClothesCategories();
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
                mSizesValuesCursor = mDataManager.GetSizesValuesByType(typeOfValues);
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

    }


    private void checkIsItNewValue(String chosenType) {
        if (chosenType != "") {
            boolean isFind = false;
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
                    isFind = true;
                    break;
                }
            }
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
            maddNewItemImageButton = (ImageButton) findViewById(R.id.addNewItemImageButton);
            mPhotoPreview = GeneralHelper.resizeBitmapFile(this, maddNewItemImageButton.getWidth()-10, maddNewItemImageButton.getHeight()-10, mCurrentPhotoUri);
            maddNewItemImageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
            maddNewItemImageButton.setImageBitmap(mPhotoPreview);
            maddNewItemImageButton.setBackground(null);
        }
    }


    @Override
    public void onChooseFromGalleryClick() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 1);
    }

    public void btnShowSizeTypeDialog_click(View view) {
        ChooseSizeTypesDialogFragment applicationDialogFragment = new ChooseSizeTypesDialogFragment();
        applicationDialogFragment.setmListener(this);
        applicationDialogFragment.show(getSupportFragmentManager(),"ChooseSizeTypesDialogFragment");
    }

    public void onNewSizesTypesDefined(int type1, int type2) {
        Toast.makeText(this,"TYpes: " + type1 + type2, Toast.LENGTH_SHORT).show();
    }
}
