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
    private String mCurrentPhotoPath;
    private ImageButton maddNewItemImageButton;

    private AutoCompleteTextView mTypeClothesTextView;
    private String oldType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        AdView adView = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        // Toasts the test ad message on the screen. Remove this after defining your own ad unit ID.
        Toast.makeText(this, TOAST_TEXT, Toast.LENGTH_LONG).show();
        mDetailShown = 0;


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

    public void btnShowDetail_clicked(View v) {


        AutoCompleteTextView editView;
        mDetailShown = 1 - mDetailShown;
        int visibility;
        if (mDetailShown == 1) visibility = View.VISIBLE;
        else  visibility = View.GONE;


        editView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView4);
        editView.setVisibility(visibility);
        editView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView5);
        editView.setVisibility(visibility);
        editView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView6);
        editView.setVisibility(visibility);
        editView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView7);
        editView.setVisibility(visibility);
        editView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView8);
        editView.setVisibility(visibility);

        // TODO: Make copmatibility with preLollipop
        /*
        Button mButton = (Button) findViewById(R.id.buttonShowMoreDetails);
        if (mDetailShown == 0)
            mButton.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_expand_more_black_24dp, 0);
        else
            mButton.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_expand_less_black_24dp, 0);
        */
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
            maddNewItemImageButton = (ImageButton) findViewById(R.id.addNewItemImageButton);
            Bitmap bitmap = GeneralHelper.resizeBitmapFile(maddNewItemImageButton.getWidth(), maddNewItemImageButton.getHeight(), mCurrentPhotoPath);
            maddNewItemImageButton.setImageBitmap(bitmap);
        }
    }


    @Override
    public void onChooseFromGalleryClick() {

    }
}
