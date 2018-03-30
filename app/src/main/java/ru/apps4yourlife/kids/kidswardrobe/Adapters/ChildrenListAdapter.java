package ru.apps4yourlife.kids.kidswardrobe.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeContract;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;

/**
 * Created by ksharafutdinov on 29-Mar-18.
 */

public class ChildrenListAdapter extends RecyclerView.Adapter <ChildrenListAdapter.ChildrenListAdapterViewHolder> {
    private Context mContext;
    private Cursor mListChildrenCursor;

    public ChildrenListAdapter(Context context) {
        mContext = context;
        WardrobeDBDataManager mDataManager = new WardrobeDBDataManager(mContext);
        mListChildrenCursor = mDataManager.GetChildrenListFromDb("");
    }

    @Override
    public ChildrenListAdapter.ChildrenListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.child_list_item, parent, false);

        return new ChildrenListAdapter.ChildrenListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChildrenListAdapter.ChildrenListAdapterViewHolder holder, int position) {

        mListChildrenCursor.moveToPosition(position);
        holder.nameTextView.setTag(mListChildrenCursor.getString(mListChildrenCursor.getColumnIndex(WardrobeContract.ChildEntry._ID)));
        holder.nameTextView.setText(mListChildrenCursor.getString(mListChildrenCursor.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_NAME)));
        String dateTextFromDB = mListChildrenCursor.getString(mListChildrenCursor.getColumnIndex(WardrobeContract.ChildEntry.COLUMN_BIRTHDATE));
        Calendar calendar = new GregorianCalendar(1970,01,01);
        if (calendar.equals(dateTextFromDB)) {
            dateTextFromDB = mContext.getResources().getString(R.string.birthdate_undefinded);
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            try {
                dateTextFromDB = dateFormat.parse(dateTextFromDB).toString();
            } catch (ParseException ex) {
                dateTextFromDB = mContext.getResources().getString(R.string.birthdate_undefinded);
            }
        }
        Toast.makeText(mContext, dateTextFromDB, Toast.LENGTH_SHORT).show();
        holder.ageTextView.setText(dateTextFromDB);
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

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }
    }

}
