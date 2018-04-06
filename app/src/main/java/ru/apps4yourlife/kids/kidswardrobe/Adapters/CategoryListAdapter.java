package ru.apps4yourlife.kids.kidswardrobe.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ru.apps4yourlife.kids.kidswardrobe.R;

/**
 * Created by ksharafutdinov on 27-Feb-18.
 */

public class CategoryListAdapter extends RecyclerView.Adapter <CategoryListAdapter.CategoryListAdapterViewHolder> {

    private Context mContext;

    public CategoryListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public CategoryListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        return new CategoryListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryListAdapterViewHolder holder, int position) {

        holder.firstTextView.setText("Hi there");
        holder.secondTextView.setText("Hello there");
        // Toast.makeText(mContext,"Hi there" + position, Toast.LENGTH_SHORT).show();
        return;
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    class CategoryListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /*
        final ImageView iconView;
        final TextView dateView;
        final TextView descriptionView;
        final TextView highTempView;
        final TextView lowTempView;
        */

        private TextView firstTextView;
        private TextView secondTextView;

        CategoryListAdapterViewHolder(View view) {
            super(view);
            /*
            iconView = (ImageView) view.findViewById(R.id.weather_icon);
            dateView = (TextView) view.findViewById(R.id.date);
            descriptionView = (TextView) view.findViewById(R.id.weather_description);
            highTempView = (TextView) view.findViewById(R.id.high_temperature);
            lowTempView = (TextView) view.findViewById(R.id.low_temperature);
            */

            firstTextView = (TextView) view.findViewById(R.id.textView);
            secondTextView = (TextView) view.findViewById(R.id.textView2);

            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click. We fetch the date that has been
         * selected, and then call the onClick handler registered with this adapter, passing that
         * date.
         *
         * @param v the View that was clicked
         */
        @Override
        public void onClick(View v) {
            /*
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(dateInMillis);
            */
        }
    }
}
