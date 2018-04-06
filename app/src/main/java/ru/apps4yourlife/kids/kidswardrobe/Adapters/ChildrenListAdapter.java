package ru.apps4yourlife.kids.kidswardrobe.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper;

import static ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper.getBitmapFromBytes;

/**
 * Created by ksharafutdinov on 29-Mar-18.
 */

public class ChildrenListAdapter extends RecyclerView.Adapter <ChildrenListAdapter.ChildrenListAdapterViewHolder> {
    private Context mContext;
    private Cursor mListChildrenCursor;

    public interface ChildrenListAdapterClickHandler {
        void onChildClick(String childId, String itemPositionInList);
    }

    private final ChildrenListAdapterClickHandler mChildrenListAdapterClickHandler;

    public ChildrenListAdapter(Context context, ChildrenListAdapterClickHandler clickHandler) {
        mChildrenListAdapterClickHandler = clickHandler;
        mContext = context;
        WardrobeDBDataManager mDataManager = new WardrobeDBDataManager(mContext);
        mListChildrenCursor = mDataManager.GetChildrenListFromDb("");
    }
    public void updateListValues(Cursor newChildData, int position) {
        mListChildrenCursor = newChildData;
        Log.e("CURSOR","Count of new cursor = " + mListChildrenCursor.getCount() + "; Position = " + position);
        if (position >= 0) {
            notifyItemChanged(position);
        } else {
            notifyItemInserted(mListChildrenCursor.getCount());
        }
    }

    @Override
    public ChildrenListAdapter.ChildrenListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("RECYCLER: ", "onCreateViewHolder is called");
        View view = LayoutInflater.from(mContext).inflate(R.layout.child_list_item, parent, false);
        return new ChildrenListAdapter.ChildrenListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChildrenListAdapter.ChildrenListAdapterViewHolder holder, int position) {
        Log.e("RECYCLER: ", "onBindViewHolder is called with position: " + position);

        mListChildrenCursor.moveToPosition(position);
        // ID and Name
        holder.nameTextView.setTag(mListChildrenCursor.getString(mListChildrenCursor.getColumnIndex(WardrobeContract.ChildEntry._ID)));
        holder.nameTextView.setText(mListChildrenCursor.getString(mListChildrenCursor.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_NAME)));
        // BirthDate
        long birthDateAsLong = mListChildrenCursor.getLong(mListChildrenCursor.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_BIRTHDATE));
        holder.ageTextView.setText(
                GeneralHelper.getStringFromBirthDate(
                        birthDateAsLong,
                        mContext.getResources().getString(R.string.birthdate_undefinded)
                )
        );
        //small photo
        byte[] previewInBytes = mListChildrenCursor.getBlob(mListChildrenCursor.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_PHOTO_PREVIEW));
        Bitmap smallPhoto = GeneralHelper.getBitmapFromBytes(previewInBytes, GeneralHelper.GENERAL_HELPER_CHILD_TYPE);
        holder.smallPhotoImageView.setImageBitmap(smallPhoto);
        Log.e("RECYCLER: ", "height of view is : " + holder.itemView.getHeight());

        return;
    }

    @Override
    public int getItemCount() {
        if (mListChildrenCursor != null) {
            return mListChildrenCursor.getCount();
        } else{
            return 0;
        }
    }

    class ChildrenListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameTextView;
        private TextView ageTextView;
        private ImageView smallPhotoImageView;

        ChildrenListAdapterViewHolder(View view) {
            super(view);

            nameTextView = (TextView) view.findViewById(R.id.childNameInList);
            ageTextView = (TextView) view.findViewById(R.id.childAgeInList);
            smallPhotoImageView = (ImageView) view.findViewById(R.id.previewPhotoChildInList);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mListChildrenCursor.moveToPosition(position);
            Log.e("ADAPTER","CALL ACTIVITY with position = " + position);
            mChildrenListAdapterClickHandler.onChildClick(
                    mListChildrenCursor.getString(mListChildrenCursor.getColumnIndex(WardrobeContract.ChildEntry._ID)),
                    String.valueOf(position)
            );
        }
    }

}
