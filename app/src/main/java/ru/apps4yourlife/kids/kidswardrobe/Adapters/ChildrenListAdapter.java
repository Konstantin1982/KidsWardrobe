package ru.apps4yourlife.kids.kidswardrobe.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.apps4yourlife.kids.kidswardrobe.R;

/**
 * Created by ksharafutdinov on 29-Mar-18.
 */

public class ChildrenListAdapter extends RecyclerView.Adapter <ChildrenListAdapter.ChildrenListAdapterViewHolder> {
    private Context mContext;

    public ChildrenListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ChildrenListAdapter.ChildrenListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.child_list_item, parent, false);

        return new ChildrenListAdapter.ChildrenListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChildrenListAdapter.ChildrenListAdapterViewHolder holder, int position) {
        holder.nameTextView.setText("Вася");
        holder.ageTextView.setText("01 январь 2000 года");

        return;
    }

    @Override
    public int getItemCount() {
        return 8;
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
