package ru.apps4yourlife.kids.kidswardrobe.Utilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import ru.apps4yourlife.kids.kidswardrobe.Activities.AddNewChildActivity;
import ru.apps4yourlife.kids.kidswardrobe.Activities.AddNewItemActivity;
import ru.apps4yourlife.kids.kidswardrobe.R;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ksharafutdinov on 14-Mar-18.
 */
public class ChoosePhotoApplication extends AppCompatActivity implements ChoosePhotoApplicationDialogFragment.ChoosePhotoApplicationDialogListener {

    protected Bitmap mResultThumbnail;


    private FragmentManager fm;
    private PackageManager pm;

    public ChoosePhotoApplication(AddNewChildActivity parentChild) {
        fm = parentChild.getSupportFragmentManager();
        pm = parentChild.getPackageManager();
    }
    public ChoosePhotoApplication(AddNewItemActivity parentItem) {
        fm = parentItem.getSupportFragmentManager();
        pm = parentItem.getPackageManager();
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(pm) != null) {
            startActivityForResult(takePictureIntent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mResultThumbnail = imageBitmap;
        }
    }

    @Override
    public void onTakeNewPhotoClick() {
        dispatchTakePictureIntent();
    }

    @Override
    public void onChooseFromGalleryClick() {

    }



    public void showApplicationDialog() {
        if (fm != null) {
            ChoosePhotoApplicationDialogFragment mApplicationDialogFragment = new ChoosePhotoApplicationDialogFragment();
            mApplicationDialogFragment.setmListener(this);
            mApplicationDialogFragment.show(fm,"ChoosePhotoApplicationDialogFragment");
        }
    }

    public void startChoosingPhotoApplication() {
        showApplicationDialog();
    }

}

