package ru.apps4yourlife.kids.kidswardrobe.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ru.apps4yourlife.kids.kidswardrobe.Activities.tabs.TabReports;
import ru.apps4yourlife.kids.kidswardrobe.Activities.tabs.TabManager;

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
                TabManager tabManager = new TabManager();
                return tabManager;
            case 1:
                TabReports tabChildren = new TabReports();
                return tabChildren;
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
                return "Управление";
            case 1:
                return "Просмотр";
            default:
            return "undefined " + (position + 1);
        }
    }
}
