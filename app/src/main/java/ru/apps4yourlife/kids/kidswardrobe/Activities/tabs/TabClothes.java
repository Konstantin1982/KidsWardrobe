package ru.apps4yourlife.kids.kidswardrobe.Activities.tabs;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ru.apps4yourlife.kids.kidswardrobe.R;

/**
 * Created by ksharafutdinov on 05-Feb-18.
 */

public class TabClothes extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        return inflater.inflate(R.layout.tab_clothes_layout, container, false);
    }
    public void btnAddNewClothes_Click(View v) {
        // Code here executes on main thread after user presses button
        Toast.makeText(v.getContext(), "Button Clicked", Toast.LENGTH_LONG).show();
    }

}
