package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.google.android.gms.ads.AdView;

import java.util.List;

import ru.apps4yourlife.kids.kidswardrobe.Adapters.PagerAdapter;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.BillingHelper;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.GeneralHelper;

public class StartActivity extends AppCompatActivity implements PurchasesUpdatedListener, BillingHelper.LastPurchaseListener {

    //This is our tablayout
    private TabLayout mTabLayout;
    //This is our viewPager
    private ViewPager mViewPager;

    // pager Adapter
    private PagerAdapter mpagerAdapter;
    private BillingHelper mBillingHelper;
    private String mLastGoodAsked;
    private int mNoAdsStatus; // 0 - can be taken, 1 - already taken

    private AdView mAdView;

    public void setLastPurchase(String code) {
        mLastGoodAsked = code;
    }


    @Override
    protected void onResume() {
        ImageView randomImage = (ImageView) findViewById(R.id.start_randomImage);
        int imageId = GeneralHelper.GetRandomImageId();
        //Toast.makeText(this,, Toast.LENGTH_SHORT).show();
        //Log.e("IMAGE", "NUMBER = " + imageId);
        randomImage.setImageResource(imageId);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // for case, insert default status for purchase
        WardrobeDBDataManager dbDataManager = new WardrobeDBDataManager(this);
        dbDataManager.InsertOrUpdatePurchase(BillingHelper.SKUCodes.noAdsCode,-1);
        mNoAdsStatus = dbDataManager.getPurchaseStatus(BillingHelper.SKUCodes.noAdsCode);

        mLastGoodAsked = "";
        setContentView(R.layout.activity_start);
        if (mNoAdsStatus > 0) {
            // уже все куплено
            updateUI();
        } else {
            MobileAds.initialize(this, this.getString(R.string.app_id));
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
        mTabLayout = (TabLayout) findViewById(R.id.main_tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mpagerAdapter = new PagerAdapter(getSupportFragmentManager(),2);
        mViewPager.setAdapter(mpagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        ImageView randomImage = (ImageView) findViewById(R.id.start_randomImage);
        randomImage.setImageResource(GeneralHelper.GetRandomImageId());
    }

    public void updateUI() {
        AdView adView = (AdView) findViewById(R.id.adView);
        adView.setVisibility(View.GONE);
        invalidateOptionsMenu();
    }

    public void donateClick() {
        Intent intent = new Intent(this, DonationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchaseList) {
        if (responseCode == BillingClient.BillingResponse.OK && purchaseList != null){
            for (Purchase purchase : purchaseList) {
                switch (purchase.getSku()) {
                    case BillingHelper.SKUCodes.noAdsCode:
                        Toast.makeText(this,"Спасибо за покупку! Реклама вот-вот исчезнет.", Toast.LENGTH_LONG).show();
                        WardrobeDBDataManager dbDataManager = new WardrobeDBDataManager(this);
                        dbDataManager.InsertOrUpdatePurchase(BillingHelper.SKUCodes.noAdsCode,1);
                        mNoAdsStatus = dbDataManager.getPurchaseStatus(BillingHelper.SKUCodes.noAdsCode);
                        updateUI();
                    break;
                }
            }
        }
        if (responseCode == BillingClient.BillingResponse.ITEM_ALREADY_OWNED) {
            switch (mLastGoodAsked) {
                case BillingHelper.SKUCodes.noAdsCode:
                    Toast.makeText(this,"Похоже Вы совершили покупку ранее. Реклама вот-вот исчезнет.", Toast.LENGTH_LONG).show();
                    WardrobeDBDataManager dbDataManager = new WardrobeDBDataManager(this);
                    dbDataManager.InsertOrUpdatePurchase(BillingHelper.SKUCodes.noAdsCode,1);
                    mNoAdsStatus = dbDataManager.getPurchaseStatus(BillingHelper.SKUCodes.noAdsCode);
                    updateUI();
                break;
            }
        }
    }

    public void btnAddNewClothes_Click(View v) {
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

    public void btnShare_Click(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_VIEW);
        Uri tmpUri = Uri.parse("http://www.apps4yourlife.ru/");
        sendIntent.setData(tmpUri);
        //sendIntent.setType("text/html");
        this.startActivity(sendIntent);
    }

    public void submitnoAdsPurchase() {
        int noAds149Status = 0; // 0 - не куплено, 1 - куплено, -1 0 ошибка
        // Use the Builder class for convenient dialog construction
        // Проверка, что покупки еще нет
        // проверка, что можно купить
        mBillingHelper = new BillingHelper(this, this, this);
        mBillingHelper.StartOperationInStore(BillingHelper.SKUCodes.noAdsCode, 1); // покупаем noAds149
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_noAds_buy :
                submitnoAdsPurchase();
                return true;
            case R.id.action_help_buy :
                donateClick();
                return true;
            case R.id.action_settings :
                settingsClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void settingsClick() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        int menu_id;
        if (mNoAdsStatus > 0) {
            menu_id = R.menu.menu_main_noads;
        } else  {
            menu_id = R.menu.menu_main;
        }
        inflater.inflate(menu_id, menu);
        return true;
    }


}

// **************  RELEASE 2.5. **************** //
// DONE: 1) Upgrade to 28.xx
// DONE: 2) Return ADS
// DONE: 3) Add Comment2


// BUGS

// DONE: CLICK TO ITEM IN REPORT
// DONE: GOOGLE DRIVE REWORK!!

// todo:test test test


// **************  RELEASE 2.5. **************** //
