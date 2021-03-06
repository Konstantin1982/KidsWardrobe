package ru.apps4yourlife.kids.kidswardrobe.Utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.InputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;
import com.squareup.picasso.Transformation;

/**
 * Created by 123 on 27.03.2018.
 */

public class GeneralHelper implements Transformation{

    public static final int GENERAL_HELPER_CHILD_TYPE = 106;
    public static final int GENERAL_HELPER_CLOTHES_TYPE = 107;

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static int GetRandomImageId() {
        Random r = new Random();
        int imageNumber = r.nextInt(15);
        int result;
        switch (imageNumber) {
            case 0:
                result = R.drawable.background_easter_640;
                break;
            case 1:
                result = R.drawable.background_flower_640;
                break;
            case 2:
                result = R.drawable.background_girl_640;
                break;
            case 3:
                result = R.drawable.background_mushrooms_640;
                break;
            case 4:
                result = R.drawable.background_puffin_640;
                break;
            case 5:
                result = R.drawable.background_reindeer_640;
                break;
            case 6:
                result = R.drawable.background_snowman_640;
                break;
            case 7:
                result = R.drawable.background_stones_640;
                break;
            case 8:
                result = R.drawable.background_unicorn_640;
                break;
            case 9:
                result = R.drawable.background_ant_640;
                break;
            case 10:
                result = R.drawable.background_cat_640;
                break;
            case 11:
                result = R.drawable.background_giraffe_640;
                break;
            case 12:
                result = R.drawable.background_gnomes_640;
                break;
            case 13:
                result = R.drawable.background_rabbit2_640;
                break;
            case 14:
                result = R.drawable.background_winter_640;
                break;
            default:
                result = R.drawable.background_elephant_640;
        }
        return result;
    }

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

    public static File createFolderForArchive(Context context) throws IOException {
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
                case GENERAL_HELPER_CHILD_TYPE:
                case GENERAL_HELPER_CLOTHES_TYPE:
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



    @Override
    public Bitmap transform(final Bitmap source) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        final Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        float radius = Math.min(source.getWidth() / 2, source.getHeight() / 2);
        canvas.drawCircle(source.getWidth() / 2, source.getHeight() / 2, radius, paint);
        if (source != output)
            source.recycle();

        return output;
    }


    @Override
    public String key() {
        return "circle";
    }

    public static Date GetCurrentDate() {
        GregorianCalendar tmpDate = new GregorianCalendar();
        GregorianCalendar normalDate = new GregorianCalendar(
                tmpDate.get(Calendar.YEAR),
                tmpDate.get(Calendar.MONTH),
                tmpDate.get(Calendar.DAY_OF_MONTH),
                0,
                0,
                0);
        return normalDate.getTime();
    }

    public static double GetDoubleValueFromEditText(EditText field) {
        String valueAsString = field.getText().toString();
        double value = 0;
        if (!valueAsString.isEmpty()) {
            value = Double.valueOf(valueAsString);
        }
        return value;
    }

    public static String GetStringValueFromEditText(EditText field) {
        return field.getText().toString();
    }



    public static ArrayList<Integer> AddSizesForChild(Context context, long childId, ArrayList<Integer> currentList) {

        ArrayList<Integer> newList = currentList;
        if (newList == null) newList = new ArrayList<Integer>();

        WardrobeDBDataManager mDataManager = new WardrobeDBDataManager(context);
        Cursor childSizeCursor = mDataManager.GetLatestChildSize(String.valueOf(childId));
        Cursor childCursor = mDataManager.GetChildByIdFromDb(String.valueOf(childId));
        //1 - Рост
        //2 - Возраст в годах
        //3 - Размер ноги.
        //4 - Обувь
        double size1 = 0, size2 = 0, size3 = 0, size4 = 0;
        if (childSizeCursor.getCount() > 0) {
            childSizeCursor.moveToPosition(0);
            size1 = childSizeCursor.getDouble(childSizeCursor.getColumnIndex(WardrobeContract.ChildSizeEntry.COLUMN_HEIGHT));
            size3 = childSizeCursor.getDouble(childSizeCursor.getColumnIndex(WardrobeContract.ChildSizeEntry.COLUMN_FOOT_SIZE));
            size4 = childSizeCursor.getDouble(childSizeCursor.getColumnIndex(WardrobeContract.ChildSizeEntry.COLUMN_SHOES_SIZE));
        }

        Calendar currentCalendar = new GregorianCalendar();
        Calendar birthdayCalendar = new GregorianCalendar();
        birthdayCalendar.setTimeInMillis(childCursor.getLong(childCursor.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_BIRTHDATE)));
        double tmp = (60 * 60 * 24);
        double tmp1 =(currentCalendar.getTimeInMillis() - birthdayCalendar.getTimeInMillis()) / 365000;
        size2 =  tmp1 / tmp;
        int nextSize = 0;
        if (size1 > 0) {
            nextSize = mDataManager.GetSizeIdByFilter(1, size1, 0);
            if (nextSize > 0) newList.add(nextSize);
            nextSize = mDataManager.GetSizeIdByFilter(1, size1, 1);
            if (nextSize > 0) newList.add(nextSize);
            nextSize = mDataManager.GetSizeIdByFilter(1, size1, 2);
            if (nextSize > 0) newList.add(nextSize);
        }
        if (size2 > 0) {
            nextSize = mDataManager.GetSizeIdByFilter(2, size2, 0);
            if (nextSize > 0) newList.add(nextSize);
            nextSize = mDataManager.GetSizeIdByFilter(2, size2, 1);
            if (nextSize > 0) newList.add(nextSize);
            nextSize = mDataManager.GetSizeIdByFilter(2, size2, 2);
            if (nextSize > 0) newList.add(nextSize);
        }
        if (size3 > 0) {
            // 1 - search all clothes with sizes values
            nextSize = mDataManager.GetSizeIdByFilter(4, size3, 0);
            if (nextSize > 0) newList.add(nextSize);
            nextSize = mDataManager.GetSizeIdByFilter(4, size3, 1);
            if (nextSize > 0) newList.add(nextSize);
            nextSize = mDataManager.GetSizeIdByFilter(4, size3, 2);
            if (nextSize > 0) newList.add(nextSize);
            // 2 - search all clothes with type 6 values
            nextSize = mDataManager.GetSizeIdByFilter(6, size3, 0);
            if (nextSize > 0) newList.add(nextSize);
            nextSize = mDataManager.GetSizeIdByFilter(6, size3, 1);
            if (nextSize > 0) newList.add(nextSize);
            nextSize = mDataManager.GetSizeIdByFilter(6, size3, 2);
            if (nextSize > 0) newList.add(nextSize);

        }
        if (size4 > 0) {
            // size4 - ID of value.
            // 1 - search all clothes with sizes ids
            nextSize = mDataManager.GetSizeIdByFilter(4, size4, 3);
            if (nextSize > 0) newList.add(nextSize);
            nextSize = mDataManager.GetSizeIdByFilter(4, size4, 4);
            if (nextSize > 0) newList.add(nextSize);
            nextSize = mDataManager.GetSizeIdByFilter(4, size4, 5);
            if (nextSize > 0) newList.add(nextSize);

            // 2 - search all clothes with type 6 values
            nextSize = mDataManager.GetSizeIdByFilter(6, size4, 6);
            if (nextSize > 0) newList.add(nextSize);
            nextSize = mDataManager.GetSizeIdByFilter(6, size4, 7);
            if (nextSize > 0) newList.add(nextSize);
            nextSize = mDataManager.GetSizeIdByFilter(6, size4, 8);
            if (nextSize > 0) newList.add(nextSize);

        }

        //childCursor.close();
        //childSizeCursor.close();
        return newList;
    }



    public static String GetFilterForSizes(Context context, ArrayList<Integer> children, boolean isSizeChecked, int typeList) {
        String filter = "";
        ArrayList<Integer> sizesIds = new ArrayList<Integer>();
        if (typeList == 0) {
            if (isSizeChecked || !children.isEmpty()) {
                if (children.isEmpty()) {
                    WardrobeDBDataManager mDataManager = new WardrobeDBDataManager(context);
                    Cursor childrenCursor = mDataManager.GetChildrenListFromDb("");
                    for (int i = 0; i < childrenCursor.getCount(); i++) {
                        childrenCursor.moveToPosition(i);
                        long childId = childrenCursor.getLong(childrenCursor.getColumnIndex("_id"));
                        sizesIds = AddSizesForChild(context, childId, sizesIds);
                    }
                    //childrenCursor.close();
                } else {
                    WardrobeDBDataManager mDataManager = new WardrobeDBDataManager(context);
                    Cursor childrenCursor = mDataManager.GetAllChildrenWithChecked();
                    childrenCursor.moveToPosition(children.get(0));
                    long childId = childrenCursor.getLong(childrenCursor.getColumnIndex("_id"));
                    sizesIds = AddSizesForChild(context, childId, sizesIds);
                    //childrenCursor.close();
                }
            }
        }
        if (typeList == 1) {
            if (isSizeChecked || !children.isEmpty()) {
                    WardrobeDBDataManager mDataManager = new WardrobeDBDataManager(context);
                    Cursor childrenCursor = mDataManager.GetChildByIdFromDb(String.valueOf(children.get(0)));
                    childrenCursor.moveToPosition(0);
                    long childId = childrenCursor.getLong(childrenCursor.getColumnIndex("_id"));
                    sizesIds = AddSizesForChild(context, childId, sizesIds);
                    //childrenCursor.close();
            }
        }
        if (!sizesIds.isEmpty()) {
            filter = "(" ;
            for (Integer id : sizesIds) {
                filter = filter.concat(String.valueOf(id) + ", ");
            }
            filter = filter.concat("-1)");
        }
        return filter;
    }

    public static Map<Long, ArrayList<Integer>> GetSuitSizesByChild(Context context) {
        Map<Long, ArrayList<Integer>> suitSizes = new HashMap<Long, ArrayList<Integer>>();
        WardrobeDBDataManager mDataManager = new WardrobeDBDataManager(context);
        Cursor childrenCursor = mDataManager.GetChildrenListFromDb("");
        for (int i = 0; i < childrenCursor.getCount(); i++) {
            ArrayList<Integer> sizesIds = new ArrayList<Integer>();
            childrenCursor.moveToPosition(i);
            long childId = childrenCursor.getLong(childrenCursor.getColumnIndex("_id"));
            sizesIds = AddSizesForChild(context,childId,sizesIds);
            suitSizes.put(childId,sizesIds);
        }
        //childrenCursor.close();
        return suitSizes;
    }

    public static String GetNewHeaderForReport(Cursor cursor, String reportType) {
        String newHeader = "";
        if (reportType == "comment") {
            newHeader = cursor.getString(cursor.getColumnIndex(WardrobeContract.ClothesItem.COLUMN_COMMENT));
        }
        return newHeader;
    }

    public static void ShowHelp(final Context context) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = inflater.inflate(R.layout.helpview, null);
            WebView messageWebView = (WebView) dialogView.findViewById(R.id.helpShowWebView);
            String fileName = "file:///android_asset/setshelp.html";
            messageWebView.loadUrl(fileName);
            builder.setView(dialogView);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return;

    }
}
