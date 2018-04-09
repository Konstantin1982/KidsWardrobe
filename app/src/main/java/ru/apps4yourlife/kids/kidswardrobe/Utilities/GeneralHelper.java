package ru.apps4yourlife.kids.kidswardrobe.Utilities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.R;

/**
 * Created by 123 on 27.03.2018.
 */

public class GeneralHelper {

    public static final int GENERAL_HELPER_CHILD_TYPE = 106;
    public static final int GENERAL_HELPER_CLOTHES_TYPE = 107;

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            storageDir.mkdirs();
        } catch (Exception ex) {
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    public static Intent prepareTakePhotoIntent(Intent intent, Context context, Uri photoURI) {
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        return intent;
    }

    public static Bitmap resizeBitmapFile(Context context, int targetW, int targetH, Uri contentUri)  {
        Bitmap resultBitmap = null;
        try {
            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(contentUri),null,bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.max(photoW / targetW, photoH / targetH);
            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;
            resultBitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(contentUri),null,bmOptions);
        }catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return resultBitmap;
    }

    public static Bitmap getBitmapFromBytes(byte[] bytes, int type) {

        Bitmap resultBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        if (resultBitmap == null) {
            switch (type) {
                case GENERAL_HELPER_CHILD_TYPE: // TODO Default bitmap for children
                case GENERAL_HELPER_CLOTHES_TYPE: // TODO Default bitmap for clothes
            }
        }
        return resultBitmap;
    }

    public static String getStringFromBirthDate (long birthDateAsLong, String defaultString) {
        String result;

        long defaultDateAsLong =  new GregorianCalendar(1970,01,01).getTime().getTime();
        if (birthDateAsLong == defaultDateAsLong) {
            result = defaultString;
        } else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            result = dateFormat.format(birthDateAsLong);
        }
        return result;
    }
}
