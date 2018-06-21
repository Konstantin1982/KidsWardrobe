package ru.apps4yourlife.kids.kidswardrobe.Activities.tabs;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import ru.apps4yourlife.kids.kidswardrobe.R;

/**
 * Created by ksharafutdinov on 05-Feb-18.
 */

public class TabManager extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_manage_info, container, false);

        Drawable addItemIcon, orgItemIcon, orgChildrenIcon;
        Button addItemButton, orgItemButton, orgChildrenButton;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addItemIcon =  getResources().getDrawable( R.drawable.ic_plus );
            orgItemIcon =  getResources().getDrawable( R.drawable.ic_list );
            orgChildrenIcon =  getResources().getDrawable( R.drawable.ic_children);

        } else {
            addItemIcon =  AppCompatResources.getDrawable(container.getContext(), R.drawable.ic_plus );
            orgItemIcon =  AppCompatResources.getDrawable(container.getContext(), R.drawable.ic_list );
            orgChildrenIcon =  AppCompatResources.getDrawable(container.getContext(), R.drawable.ic_children);
        }
        addItemIcon.setBounds( 0, 0, 100, 100 );
        orgItemIcon.setBounds( 0, 0, 100, 100 );
        orgChildrenIcon.setBounds( 0, 0, 100, 100 );

        addItemButton = (Button) view.findViewById(R.id.add_new_item_button);
        orgItemButton = (Button) view.findViewById(R.id.organize_items_button);
        orgChildrenButton = (Button) view.findViewById(R.id.button_edit_children);

        addItemButton.setCompoundDrawables( addItemIcon,  null, null, null );
        orgItemButton.setCompoundDrawables( orgItemIcon,  null, null, null );
        orgChildrenButton.setCompoundDrawables( orgChildrenIcon,  null, null, null );

        return view;
    }

}
