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
import android.widget.ImageButton;
import java.io.File;
import java.io.IOException;

import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChoosePhotoApplicationDialogFragment;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper;


public class AddNewChildActivity extends AppCompatActivity implements ChoosePhotoApplicationDialogFragment.ChoosePhotoApplicationDialogListener  {

    private ImageButton mAddChildButton;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_child);
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
            Bitmap bitmap = GeneralHelper.resizeBitmapFile(mAddChildButton.getWidth(), mAddChildButton.getHeight(), mCurrentPhotoPath);
            mAddChildButton.setImageBitmap(bitmap);
        }
    }


    @Override
    public void onChooseFromGalleryClick() {

    }
}
