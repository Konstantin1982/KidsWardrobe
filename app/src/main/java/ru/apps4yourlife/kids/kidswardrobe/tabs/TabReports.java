package ru.apps4yourlife.kids.kidswardrobe.tabs;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.apps4yourlife.kids.kidswardrobe.R;

/**
 * Created by ksharafutdinov on 05-Feb-18.
 */

public class TabReports extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        return inflater.inflate(R.layout.tab_reports_layout, container, false);
    }
}
