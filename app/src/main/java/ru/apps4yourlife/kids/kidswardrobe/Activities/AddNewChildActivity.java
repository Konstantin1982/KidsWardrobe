package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.support.v4.app.DialogFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChoosePhotoApplication;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChoosePhotoApplicationDialogFragment;

public class AddNewChildActivity extends AppCompatActivity implements ChoosePhotoApplicationDialogFragment.ChoosePhotoApplicationDialogListener  {
    static final int REQUEST_IMAGE_CAPTURE = 0;

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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    public void onTakeNewPhotoClick() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 0);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            // Get the dimensions of the View
            int targetW = mAddChildButton.getWidth();
            int targetH = mAddChildButton.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            mAddChildButton.setImageBitmap(bitmap);
        }
    }


    @Override
    public void onChooseFromGalleryClick() {

    }
}
