package ru.apps4yourlife.kids.kidswardrobe.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import ru.apps4yourlife.kids.kidswardrobe.R;

public class ItemSetsActivity extends AppCompatActivity  {

    private ArrayList<ImageView> allImagesInSet;
    private myDragEventListener dragEventListener;
    private ArrayList<SortKeys> currentSortOrderArray;
    private int mCountOfItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_sets);

        currentSortOrderArray = new ArrayList<SortKeys>();

        dragEventListener = new myDragEventListener(this);

        mCountOfItems = 3;
        allImagesInSet = new ArrayList<ImageView>();

        for (int i =0; i < mCountOfItems; i++) {
            SortKeys itemSort = new SortKeys(i,i);
            currentSortOrderArray.add(itemSort);
            ImageView itemImageView = findViewById(getResourceByNumber(i));
            itemImageView.setTag(i);
            itemImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return onLongClickHandler(view);
                }
            });
            itemImageView.setOnDragListener(dragEventListener);
            allImagesInSet.add(itemImageView);
        }
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

    public void ImageReplacer(int sourceId, int targetId) {
        if (sourceId == targetId) {
            return;
        }
        ArrayList<Drawable> sources = new ArrayList<Drawable>();
        ArrayList<Integer> sortes = new ArrayList<Integer>();
        if (sourceId < targetId) {
            // 0 2
            ImageView itemImage;
            SortKeys itemSort;
            // IMAGES GOES LEFT
            for (int i = sourceId + 1; i <= targetId; i++) {
                itemImage = allImagesInSet.get(i);
                sources.add(itemImage.getDrawable());
            }
            itemImage = allImagesInSet.get(sourceId);
            sources.add(itemImage.getDrawable());

            // SORTING GOES RIGHT
            itemSort = currentSortOrderArray.get(sourceId);
            int oldPositionSource = itemSort.currentSortOrder;
            itemSort = currentSortOrderArray.get(targetId);
            sortes.add(itemSort.currentSortOrder);
            sortes.add(oldPositionSource);
            for (int i = sourceId + 1 ; i < targetId; i++) {
                itemSort = currentSortOrderArray.get(i);
                sortes.add(itemSort.currentSortOrder);
            }

            for (int i = sourceId; i <= targetId; i++) {
                itemImage = allImagesInSet.get(i);
                itemImage.setImageDrawable(sources.get(i-sourceId));
                itemSort = currentSortOrderArray.get(i);
                itemSort.ChangeSortOrder(sortes.get(i-sourceId));
                currentSortOrderArray.set(i, itemSort);
            }
            Collections.sort(currentSortOrderArray);
        }

        if (sourceId > targetId) {
            // 2 0
            ImageView itemImage;
            SortKeys itemSort;
            // IMAGES GOES RIGHT
            itemImage = allImagesInSet.get(sourceId);
            sources.add(itemImage.getDrawable());
            itemImage = allImagesInSet.get(targetId);
            sources.add(itemImage .getDrawable());
            for (int i = targetId + 1; i < sourceId ; i++) {
                itemImage = allImagesInSet.get(i);
                sources.add(itemImage.getDrawable());
            }

            // SORTING GOES LEFT
            for (int i = targetId + 1 ; i <= sourceId; i++) {
                itemSort = currentSortOrderArray.get(i);
                sortes.add(itemSort.currentSortOrder);
            }
            itemSort = currentSortOrderArray.get(targetId);
            sortes.add(itemSort.currentSortOrder);

            for (int i = targetId; i <= sourceId; i++) {
                itemImage = allImagesInSet.get(i);
                itemImage.setImageDrawable(sources.get(i-targetId));
                itemSort = currentSortOrderArray.get(i);
                itemSort.ChangeSortOrder(sortes.get(i-targetId));
                currentSortOrderArray.set(i, itemSort);
            }
            Collections.sort(currentSortOrderArray);
        }

        String message = "";
        for (SortKeys item : currentSortOrderArray) {
            message += "ID: " + item.itemId + "; SORT = " + item.currentSortOrder +"; ";
        }
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
        Log.e("REPLACER", message);

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
                    ImageReplacer(sourceId, targetId);
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
        }

        return result;
    }

    protected class SortKeys implements Comparable {
        public int itemId;
        public int currentSortOrder;

        public SortKeys (int id, int sort) {
            itemId = id;
            currentSortOrder = sort;
        }

        public void ChangeSortOrder(int newSort) {
            currentSortOrder = newSort;
        }

        @Override
        public int compareTo(Object o) {
            SortKeys tmp = (SortKeys) o;
            return this.currentSortOrder - tmp.currentSortOrder;
        }
    }
}
