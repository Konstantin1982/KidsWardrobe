package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import ru.apps4yourlife.kids.kidswardrobe.PagerAdapter;
import ru.apps4yourlife.kids.kidswardrobe.R;

public class StartActivity extends AppCompatActivity {

    //This is our tablayout
    private TabLayout mTabLayout;
    //This is our viewPager
    private ViewPager mViewPager;

    // pager Adapter
    private PagerAdapter mpagerAdapter;

    // BUttons
    private  Button mAddNewClothesButton;
    private static final String TOAST_TEXT = "Test ads are being shown. ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        // Load an ad into the AdMob banner view.

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        // Toasts the test ad message on the screen. Remove this after defining your own ad unit ID.
        Toast.makeText(this, TOAST_TEXT, Toast.LENGTH_LONG).show();


        mTabLayout = (TabLayout) findViewById(R.id.main_tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mpagerAdapter = new PagerAdapter(getSupportFragmentManager(),3);
        mViewPager.setAdapter(mpagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        /*
        mAddNewClothesButton = (Button) findViewById(R.id.button2);
        mAddNewClothesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Toast.makeText(v.getContext(), TOAST_TEXT, Toast.LENGTH_LONG).show();
            }
        });
        */
    }
    public void btnAddNewClothes_Click(View v) {
        // Code here executes on main thread after user presses button
        Toast.makeText(v.getContext(), "Button Clicked", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, AddNewItemActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
