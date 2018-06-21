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
        View view =inflater.inflate(R.layout.tab_reports_layout, container, false);

        Drawable placesIcon, childrenIcon;
        Button placesButton, childrenButton;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            placesIcon =  getResources().getDrawable( R.drawable.ic_search );
            childrenIcon =  getResources().getDrawable( R.drawable.ic_baby_clothes );

        } else {
            Drawable icon = AppCompatResources.getDrawable(container.getContext(), R.drawable.ic_baby_boy );
            placesIcon =  AppCompatResources.getDrawable(container.getContext(), R.drawable.ic_search );
            childrenIcon =  AppCompatResources.getDrawable(container.getContext(), R.drawable.ic_baby_clothes );
        }
        placesIcon.setBounds( 0, 0, 100, 100 );
        childrenIcon.setBounds( 0, 0, 100, 100 );
        placesButton = (Button) view.findViewById(R.id.button_clothes_for_review);
        childrenButton = (Button) view.findViewById(R.id.button_clothes_for_child);

        placesButton.setCompoundDrawables( placesIcon,  null, null, null );
        childrenButton.setCompoundDrawables( childrenIcon,  null, null, null );

        return view;
    }



}
