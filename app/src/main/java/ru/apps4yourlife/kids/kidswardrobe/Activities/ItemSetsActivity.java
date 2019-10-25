package ru.apps4yourlife.kids.kidswardrobe.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.PixelCopy;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.BillingHelper;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper;

public class ItemSetsActivity extends AppCompatActivity  {

    private ArrayList<ImageView> allImagesInSet;
    private myDragEventListener dragEventListener;
    private ArrayList<Integer> currentSortOrderArrayInt;
    private int mCountOfItems;
    private Bitmap mBitmap;

    private int mNoAdsStatus; // 0 - can be taken, 1 - already taken

    private AdView mAdView;
    private String mLastGoodAsked;
    private int mSetId;

    private WardrobeDBDataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_sets);

        mSetId = Integer.valueOf(getIntent().getStringExtra("ID"));
        mDataManager = new WardrobeDBDataManager(this);

        Cursor setsItemsCursor = mDataManager.getAllItemsFromSet(mSetId);


        currentSortOrderArrayInt = new ArrayList<Integer>();

        dragEventListener = new myDragEventListener(this);

        mCountOfItems = setsItemsCursor.getCount();
        if (mCountOfItems > 9) mCountOfItems = 9;
        allImagesInSet = new ArrayList<ImageView>();

        for (int i = 0; i < 9; i ++) {
            currentSortOrderArrayInt.add(-1);
        }

        for (int i =0; i < mCountOfItems; i++) {
            setsItemsCursor.moveToPosition(i);
            int itemId = setsItemsCursor.getInt(0);
            int sortOrder = setsItemsCursor.getInt(2);
            currentSortOrderArrayInt.set(sortOrder, itemId);
            ImageView itemImageView = findViewById(getResourceByNumber(sortOrder));
            byte[] previewInBytes = setsItemsCursor.getBlob(1);
            Bitmap mPhotoPreview = GeneralHelper.getBitmapFromBytes(previewInBytes,GeneralHelper.GENERAL_HELPER_CHILD_TYPE);
            itemImageView.setImageBitmap(mPhotoPreview);
            itemImageView.setTag(itemId);
        }

        for (int i =0; i < 9; i++) {
            ImageView itemImageView = findViewById(getResourceByNumber(i));
            itemImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return onLongClickHandler(view);
                }
            });
            itemImageView.setOnDragListener(dragEventListener);
        }

        WardrobeDBDataManager dbDataManager = new WardrobeDBDataManager(this);
        dbDataManager.InsertOrUpdatePurchase(BillingHelper.SKUCodes.noAdsCode,-1);
        mNoAdsStatus = dbDataManager.getPurchaseStatus(BillingHelper.SKUCodes.noAdsCode);

        mLastGoodAsked = "";
        if (mNoAdsStatus > 0) {
            // уже все куплено
            updateUI();
        } else {
            MobileAds.initialize(this, this.getString(R.string.app_id));
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("1FF81EEFAF751AD2DF1BCD1F8546349B").build();
            mAdView.loadAd(adRequest);
        }


        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);

    }



    public void updateUI() {
        AdView adView = (AdView) findViewById(R.id.adView);
        adView.setVisibility(View.GONE);
        invalidateOptionsMenu();
    }

    protected boolean onLongClickHandler(View view) {
        ClipData.Item item = new ClipData.Item(view.getTag().toString());
        ClipData dragData = new ClipData(
                view.getTag().toString(),
                new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
                item);
        view.startDragAndDrop(dragData, new View.DragShadowBuilder(view),null,0);
        return false;
    }

    public void ImageReplacer(int sourceId, int targetId, int imageNumber) {
        // ID of Items
        if (sourceId == targetId) {
            return;
        }
        int oldSourceSortOrder = -1, oldTargetSourceOrder = -1;
        for (int i = 0; i < 9; i++) {
            if (currentSortOrderArrayInt.get(i) == sourceId) oldSourceSortOrder = i;
            if (currentSortOrderArrayInt.get(i) == targetId) oldTargetSourceOrder = i;
        }
        if (targetId == -1) oldTargetSourceOrder = getSortOrderById(imageNumber);
        ImageView sourceImage = findViewById(getResourceByNumber(oldSourceSortOrder));
        ImageView targetImage = findViewById(getResourceByNumber(oldTargetSourceOrder));
        Drawable oldtargetDrawable = targetImage.getDrawable();
        targetImage.setImageDrawable(sourceImage.getDrawable());
        sourceImage.setImageDrawable(oldtargetDrawable);
        sourceImage.setTag(targetId);
        targetImage.setTag(sourceId);
        currentSortOrderArrayInt.set(oldSourceSortOrder,targetId);
        currentSortOrderArrayInt.set(oldTargetSourceOrder,sourceId);

        return;
    }

    protected  class myDragEventListener implements View.OnDragListener {

        private Context mContext;

        public myDragEventListener(Context context) {
            mContext = context;
        }


        // This is the method that the system calls when it dispatches a drag event to the
        // listener.
        public boolean onDrag(View v, DragEvent event) {

            // Defines a variable to store the action type for the incoming event
            final int action = event.getAction();
            ImageView v1 = (ImageView) v;

            // Handles each of the expected events
            switch(action) {

                case DragEvent.ACTION_DRAG_STARTED:
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        return true;
                    }
                    return false;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v1.setColorFilter(0x5500ff00);
                    v1.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v1.clearColorFilter();
                    v1.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    int sourceId = Integer.valueOf(item.getText().toString());
                    int targetId = Integer.valueOf(v1.getTag().toString());
                    int imageNumber = 0;
                    if (targetId == -1) {
                        imageNumber  = v1.getId();
                    }
                    ImageReplacer(sourceId, targetId, imageNumber);
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v1.clearColorFilter();
                    v1.invalidate();
                    return true;
                default:
                    break;
            }
            return false;
        }
    };

    public int getResourceByNumber(int number) {
        int result = R.id.dragabbleImageView1;
        switch (number) {
            case 0:
                result = R.id.dragabbleImageView1;
                break;
            case 1:
                result = R.id.dragabbleImageView2;
                break;
            case 2:
                result = R.id.dragabbleImageView3;
                break;
            case 3:
                result = R.id.dragabbleImageView4;
                break;
            case 4:
                result = R.id.dragabbleImageView5;
                break;
            case 5:
                result = R.id.dragabbleImageView6;
                break;
            case 6:
                result = R.id.dragabbleImageView7;
                break;
            case 7:
                result = R.id.dragabbleImageView8;
                break;
            case 8:
                result = R.id.dragabbleImageView9;
                break;
        }

        return result;
    }
    public int getSortOrderById(int number) {
        int result = 0;
        switch (number) {
            case R.id.dragabbleImageView1:
                result = 0;
                break;
            case R.id.dragabbleImageView2:
                result = 1;
                break;
            case R.id.dragabbleImageView3:
                result = 2;
                break;
            case R.id.dragabbleImageView4:
                result = 3;
                break;
            case R.id.dragabbleImageView5:
                result = 4;
                break;
            case R.id.dragabbleImageView6:
                result = 5;
                break;
            case R.id.dragabbleImageView7:
                result = 6;
                break;
            case R.id.dragabbleImageView8:
                result = 7;
                break;
            case R.id.dragabbleImageView9:
                result = 8;
                break;
        }

        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save_edit_item, menu);
        return true;
    }

    public void LooseFocus() {
        findViewById(R.id.main_layout_item_sets).requestFocus();
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void btnSaveNewItem_click() {
        boolean formIsOK = true;
        EditText setNameEditField = findViewById(R.id.nameOfSet);
        String setName = setNameEditField.getText().toString();
        if (setName.isEmpty()) {
            formIsOK = false;
            Toast.makeText(this,"Назовите комплект.", Toast.LENGTH_SHORT).show();
        }

        if (formIsOK) {
            WardrobeDBDataManager dataManager = new WardrobeDBDataManager(this);
            // Insert Type with size types if needed
            long new_id = dataManager.UpdateItemsInSet(currentSortOrderArrayInt, setName, mSetId) ;
            //Toast.makeText(this, "New item has been inserted: " + new_id , Toast.LENGTH_SHORT).show();
            Intent data = new Intent();
            setResult(1, data);
            finish();
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
        if (mSetId > 0) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.are_you_sure_to_delete2)
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
        if (mSetId > 0) {
            WardrobeDBDataManager dataManager = new WardrobeDBDataManager(this);
            int result = dataManager.DeleteSet(mSetId);
            setResult(1);
            finish();
        }
    }

    public void onScreenCaptureClick(View view) {
        getBitmapFromView(view, this);
    }

    private void saveBitmap() {
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            } else {
                String tmpUri = MediaStore.Images.Media.insertImage(this.getContentResolver(), mBitmap, "tmp.jpg", "комплект");
                if (tmpUri != null) {
                    openScreenshot(Uri.parse(tmpUri));
                }
                Log.e("MEDIA", tmpUri);
            }
            /*
            // create bitmap screen capture
            File imageFile = null;
            try {
                imageFile = GeneralHelper.createImageFile(this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (imageFile != null) {
                int quality = 100;
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                outputStream.close();
                openScreenshot(imageFile);
            }
            */

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
                saveBitmap();
            }
        }
    }

    private void openScreenshot(Uri imageFileUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageFileUri);
        startActivity(intent );
        /*
        //String path = imageFile.getAbsolutePath();
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(imageFile.getAbsolutePath()));
        MediaScannerConnection.scanFile(this,
                new String[] { imageFile.getAbsolutePath() }, new String[] {type},
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("ExternalStorage", "Scanned " + path + ":");
                        Log.e("ExternalStorage", "-> uri=" + uri);
                    }
                });
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        MediaScannerConnection.scanFile(
                getApplicationContext(),
                new String[]{imageFile.getAbsolutePath()},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.v("grokkingandroid",
                                "file " + path + " was scanned seccessfully: " + uri);
                    }
                });
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = FileProvider.getUriForFile(this,
            "ru.apps4yourlife.kids.fileprovider",
            imageFile);
        mediaScanIntent.setData(uri);
        this.sendBroadcast(mediaScanIntent);
        */
    }


    public void getBitmapFromView(View buttonview, Activity activity) {
        View view = buttonview.getRootView();
        if (Build.VERSION.SDK_INT >= 26) {
            Window window = activity.getWindow();
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            int[] locationOfViewInWindow = new int[2];
            view.getLocationInWindow(locationOfViewInWindow);
            Handler handler = new Handler();
            try {
                PixelCopy.request(window, new Rect(locationOfViewInWindow[0],
                                locationOfViewInWindow[1],
                                locationOfViewInWindow[0] + view.getWidth(),
                                locationOfViewInWindow[1] + view.getHeight()), bitmap,
                        new PixelCopy.OnPixelCopyFinishedListener() {
                            @Override
                            public void onPixelCopyFinished(int i) {
                                if (i == PixelCopy.SUCCESS) {
                                    Log.i("PIXEL", "FINISHED");
                                    mBitmap = bitmap;
                                    saveBitmap();
                                }
                            }
                        },
                        handler
                );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(this,"Кнопка работает только в Android 8 и выше. Сделайте скриншот вручную",Toast.LENGTH_LONG).show();
        }
    }
}
