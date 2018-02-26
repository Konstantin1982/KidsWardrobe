package ru.apps4yourlife.kids.kidswardrobe.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.apps4yourlife.kids.kidswardrobe.Activities.tabs.TabChildren;
import ru.apps4yourlife.kids.kidswardrobe.Activities.tabs.TabClothes;
import ru.apps4yourlife.kids.kidswardrobe.Activities.tabs.TabReports;

/**
 * Created by ksharafutdinov on 05-Feb-18.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private int mTabCount;

    public PagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        mTabCount = tabCount;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                TabClothes tabClothes = new TabClothes();
                return tabClothes;
            case 1:
                TabChildren tabChildren = new TabChildren();
                return tabChildren;
            case 2:
                TabReports tabReports = new TabReports();
                return tabReports;

        }
        return null;
    }

    @Override
    public int getCount() {
        return mTabCount;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        // TODO: real titles
        switch (position) {
            case 0:
                return "Одежда";
            case 1:
                return "Дети";
            case 2:
                return "Что надеть";
            default:
            return "undefined " + (position + 1);
        }
    }
}
