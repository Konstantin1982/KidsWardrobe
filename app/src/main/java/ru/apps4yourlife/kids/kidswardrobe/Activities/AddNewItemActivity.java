package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChoosePhotoApplicationDialogFragment;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper;

public class AddNewItemActivity extends AppCompatActivity implements ChoosePhotoApplicationDialogFragment.ChoosePhotoApplicationDialogListener {
    private static final String TOAST_TEXT = "Test ads are being shown. ";
    private static final int TYPE_KIND_CLOTHES = 0;


    private int mDetailShown;
    private Uri mCurrentPhotoUri;
    private Bitmap mPhotoPreview;
    private ImageButton maddNewItemImageButton;


    private AutoCompleteTextView mTypeClothesTextView;
    private String oldType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);
        /*
        AdView adView = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        // Toasts the test ad message on the screen. Remove this after defining your own ad unit ID.
        Toast.makeText(this, TOAST_TEXT, Toast.LENGTH_LONG).show();
        mDetailShown = 0;
*/

        mTypeClothesTextView = (AutoCompleteTextView) findViewById(R.id.typeClothesTextView);
        ArrayAdapter<String> mAutoCompleteTextViewAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,getList(TYPE_KIND_CLOTHES));
        mTypeClothesTextView.setAdapter(mAutoCompleteTextViewAdapter);
        mTypeClothesTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    oldType = mTypeClothesTextView.getText().toString();
                    mTypeClothesTextView.showDropDown();
                } else {
                    checkIsItNewValue(mTypeClothesTextView.getText().toString());
                    updateAdapterForSizes(mTypeClothesTextView.getText().toString());

                }
            }
        });
    }

    private List<String> getList (int typeOfList) {
        List<String> mList = new ArrayList<>();
        switch (typeOfList) {
            case TYPE_KIND_CLOTHES:
                String[] mTypes = {"Куртки", "Штаны", "Обувь", "Трусы", "Шапки", "Платья", "Комбинезоны", "Носки", "Варежки-Перчатки", "Рубашки"};
                for (String type : mTypes) {
                    mList.add(type);
                }
                break;
        }
        return mList;
    }

    private void updateAdapterForSizes(String chosenType) {
        if (chosenType!= "") {

        }
    }
    private void checkIsItNewValue(String chosenType) {

    }
    public void btnAddNewItemPhoto_click(View view) {
        ChoosePhotoApplicationDialogFragment mApplicationDialogFragment = new ChoosePhotoApplicationDialogFragment();
        mApplicationDialogFragment.setmListener(this);
        mApplicationDialogFragment.show(getSupportFragmentManager(),"ChoosePhotoApplicationDialogFragment");
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
}
