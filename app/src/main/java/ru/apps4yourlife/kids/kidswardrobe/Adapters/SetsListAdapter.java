package ru.apps4yourlife.kids.kidswardrobe.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper;


public class SetsListAdapter extends RecyclerView.Adapter <SetsListAdapter.SetsListAdapterViewHolder> {
    private Context mContext;
    private Cursor mSetsCursor;
    private WardrobeDBDataManager mDataManager;

    public interface SetsListAdapterClickHandler {
        void onSetClick(String setId, String itemPositionInList);
    }

    private final SetsListAdapterClickHandler setsListAdapterClickHandler;

    public SetsListAdapter(Context context, SetsListAdapterClickHandler clickHandler) {
        setsListAdapterClickHandler = clickHandler;
        mContext = context;
        mDataManager = new WardrobeDBDataManager(mContext);
        mSetsCursor = mDataManager.getAllSetsForList();
    }

    public void updateListValues(Cursor newSetsData, int position) {
        mSetsCursor = newSetsData;
        if (position >= 0) {
            notifyItemChanged(position);
        } else {
            notifyItemInserted(mSetsCursor.getCount());
        }
    }

    @Override
    public SetsListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.sets_list_item, parent, false);
        return new SetsListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SetsListAdapterViewHolder holder, int position) {
        //Log.e("RECYCLER: ", "onBindViewHolder is called with position: " + position);

        mSetsCursor.moveToPosition(position);
        Integer setId = mSetsCursor.getInt(0);
        if (setId > 3) {
            holder.setNameTextView.setText(mSetsCursor.getString(1));
        } else {
            holder.setNameTextView.setText("Временный комплект №" + (setId));
        }
        holder.setImageView1.setVisibility(View.INVISIBLE);
        holder.setImageView2.setVisibility(View.INVISIBLE);
        holder.setImageView3.setVisibility(View.INVISIBLE);
        Cursor setImages = mDataManager.getAllItemsFromSet(Integer.valueOf(setId));
        if (setImages.getCount() > 0) {
            for (int i = 0; i < setImages.getCount(); i++) {
                if (i > 2) break;
                setImages.moveToPosition(i);
                byte[] previewInBytes = setImages.getBlob(1);
                Bitmap smallPhoto = GeneralHelper.getBitmapFromBytes(previewInBytes,GeneralHelper.GENERAL_HELPER_CHILD_TYPE);
                if (i == 0) {
                    holder.setImageView1.setVisibility(View.VISIBLE);
                    holder.setImageView1.setImageBitmap(smallPhoto);
                }
                if (i == 1) {
                    holder.setImageView2.setVisibility(View.VISIBLE);
                    holder.setImageView2.setImageBitmap(smallPhoto);
                }
                if (i == 2) {
                    holder.setImageView3.setVisibility(View.VISIBLE);
                    holder.setImageView3.setImageBitmap(smallPhoto);
                }
            }
        }

        return;
    }

    @Override
    public int getItemCount() {
        if (mSetsCursor != null) {
            return mSetsCursor.getCount();
        } else{
            return 0;
        }
    }

    class SetsListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView setNameTextView;
        private ImageView setImageView1;
        private ImageView setImageView2;
        private ImageView setImageView3;

        SetsListAdapterViewHolder(View view) {
            super(view);
            setNameTextView = (TextView) view.findViewById(R.id.sets_name_in_list);
            setImageView1 = view.findViewById(R.id.setImage1_list);
            setImageView2 = view.findViewById(R.id.setImage2_list);
            setImageView3 = view.findViewById(R.id.setImage3_list);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mSetsCursor.moveToPosition(position);
            //Log.e("ADAPTER","CALL ACTIVITY with position = " + position);
            setsListAdapterClickHandler.onSetClick(
                    mSetsCursor.getString(mSetsCursor.getColumnIndex(WardrobeContract.ItemsSets.COLUMN_SET_ID)),
                    String.valueOf(position)
            );
        }
    }

}
