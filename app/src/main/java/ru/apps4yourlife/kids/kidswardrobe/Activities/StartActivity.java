package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import ru.apps4yourlife.kids.kidswardrobe.Adapters.PagerAdapter;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper;

public class StartActivity extends AppCompatActivity {

    //This is our tablayout
    private TabLayout mTabLayout;
    //This is our viewPager
    private ViewPager mViewPager;

    // pager Adapter
    private PagerAdapter mpagerAdapter;


    @Override
    protected void onResume() {
        ImageView randomImage = (ImageView) findViewById(R.id.start_randomImage);
        int imageId = GeneralHelper.GetRandomImageId();
        //Toast.makeText(this,, Toast.LENGTH_SHORT).show();
        Log.e("IMAGE", "NUMBER = " + imageId);
        randomImage.setImageResource(imageId);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);
        MobileAds.initialize(this, getString(R.string.app_id));

        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        // Toasts the test ad message on the screen. Remove this after defining your own ad unit ID.
        //Toast.makeText(this, TOAST_TEXT, Toast.LENGTH_LONG).show();


        mTabLayout = (TabLayout) findViewById(R.id.main_tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mpagerAdapter = new PagerAdapter(getSupportFragmentManager(),2);
        mViewPager.setAdapter(mpagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        ImageView randomImage = (ImageView) findViewById(R.id.start_randomImage);
        randomImage.setImageResource(GeneralHelper.GetRandomImageId());
    }

    public void btnAddNewClothes_Click(View v) {
        // Code here executes on main thread after user presses button
        //Toast.makeText(v.getContext(), "Button Clicked", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, AddNewItemActivity.class);
        startActivity(intent);
    }
    public void btnAddNewChild_Click (View v) {
        Intent intent = new Intent(this, AddNewChildActivity.class);
        startActivity(intent);
    }
    public void btnShowAllClothes_Click(View v) {
        // Code here executes on main thread after user presses button
        //Toast.makeText(v.getContext(), "Button Clicked", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, AllItemsActivity.class);
        startActivity(intent);
    }

    public void btnDropDB_Click (View view) {

        WardrobeDBDataManager dataManager = new WardrobeDBDataManager(this);
        if (dataManager.DeleteDatabase(this)) {
            //Toast.makeText(view.getContext(), "Database deleted", Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(view.getContext(), "Database ERROR", Toast.LENGTH_LONG).show();
        }

    }

    public void btnShowListChildren_Click(View view) {
        Intent intent = new Intent(this,ChildrenListActivity.class);
        startActivity(intent);
    }
    public void btnShowReportAllClothes_Click(View view) {
        Intent intent = new Intent(this,PlaceReportActivity.class);
        startActivity(intent);
    }

    public void btnShowReportChildClothes_Click(View view) {
        Intent intent = new Intent(this,ChildReportActivity.class);
        startActivity(intent);
    }


    private boolean isActivityStarted(Intent aIntent) {
        try {
            startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }
    public void btnRateThisApp_click(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=ru.apps4yourlife.kids.kidswardrobe"));
        if (!isActivityStarted(intent)) {
            intent.setData(Uri
                    .parse("https://play.google.com/store/apps/details?id=ru.apps4yourlife.kids.kidswardrobe"));
            if (!isActivityStarted(intent)) {
                Toast.makeText(
                        this,
                        "Не удалось открыть Google Play.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
