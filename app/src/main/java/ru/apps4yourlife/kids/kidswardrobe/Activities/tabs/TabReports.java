package ru.apps4yourlife.kids.kidswardrobe.Activities.tabs;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ru.apps4yourlife.kids.kidswardrobe.R;

/**
 * Created by ksharafutdinov on 05-Feb-18.
 */

public class TabReports extends Fragment {
    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        View view =inflater.inflate(R.layout.tab_reports_layout, container, false);
                AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable icon =  getResources().getDrawable( R.drawable.ic_baby_boy );
            Button b = (Button) view.findViewById(R.id.button_clothes_for_child);
            b.setCompoundDrawables( icon, null, null, null );
        } else {
            Drawable icon = AppCompatResources.getDrawable(container.getContext(), R.drawable.ic_baby_boy );
            icon.setBounds( 0, 0, 60, 60 );
            Button b = (Button) view.findViewById(R.id.button_clothes_for_child);
            b.setCompoundDrawables( icon, null, null, null );
        }


        return view;

    }



}
