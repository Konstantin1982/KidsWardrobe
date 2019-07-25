package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.util.List;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.BillingHelper;

public class DonationActivity extends AppCompatActivity implements PurchasesUpdatedListener, BillingHelper.LastPurchaseListener  {
    private BillingHelper mBillingHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ConstraintLayout layout;
        Resources resources = getResources();
        Drawable drawable;

        // mecenat
        layout = (ConstraintLayout) findViewById(R.id.mecenat);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPurchase(BillingHelper.SKUCodes.help2500Code);
            }
        });
        //drawable = VectorDrawableCompat.create(resources, R.drawable.ic_mecenat, this.getTheme() );
        //layout.setBackground(drawable);


        // sponsor
        layout = (ConstraintLayout) findViewById(R.id.sponsor);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPurchase(BillingHelper.SKUCodes.help500Code);
            }
        });
        //drawable = VectorDrawableCompat.create(resources, R.drawable.ic_sponsor, this.getTheme() );
        //layout.setBackground(drawable);

        // filantrop
        layout = (ConstraintLayout) findViewById(R.id.filantrop);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPurchase(BillingHelper.SKUCodes.help99Code);
            }
        });
        //drawable = VectorDrawableCompat.create(resources, R.drawable.ic_filantrop, this.getTheme() );
        //layout.setBackground(drawable);

        // motivator
        layout = (ConstraintLayout) findViewById(R.id.motivator);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRateThisApp_click();
            }
        });
        //drawable = VectorDrawableCompat.create(resources, R.drawable.ic_motivator, this.getTheme() );
        //layout.setBackground(drawable);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(0);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void startPurchase(String skuCode) {

        mBillingHelper = new BillingHelper(this, this, this);
        mBillingHelper.StartOperationInStore(skuCode, 1);

    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchaseList) {
        if (responseCode == BillingClient.BillingResponse.OK && purchaseList != null){
            // remove Ads by default
            WardrobeDBDataManager dbDataManager = new WardrobeDBDataManager(this);
            dbDataManager.InsertOrUpdatePurchase(BillingHelper.SKUCodes.noAdsCode,1);
            for (Purchase purchase : purchaseList) {
                switch (purchase.getSku()) {
                    case BillingHelper.SKUCodes.help2500Code:
                        Toast.makeText(this,"Спасибо за Вашу помощь! Приятно быть меценатом, правда?", Toast.LENGTH_LONG).show();
                        break;
                    case BillingHelper.SKUCodes.help500Code:
                        Toast.makeText(this,"Спасибо за Вашу помощь! Из Вас отличный спонсор!", Toast.LENGTH_LONG).show();
                        break;
                    case BillingHelper.SKUCodes.help99Code:
                        Toast.makeText(this,"Спасибо за Вашу помощь! Филатропы спасут мир!", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(this,"Спасибо за Вашу помощь! Не пойму, что Вы купили - но я приятно удивлен!", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
        if (responseCode == BillingClient.BillingResponse.ITEM_ALREADY_OWNED) {
            Toast.makeText(this,"А я и не ожидал, что Вы будете помогать несколько раз! Теперь я обязательно добавлю эту возможность! СПАСИБО!!!", Toast.LENGTH_LONG).show();
            WardrobeDBDataManager dbDataManager = new WardrobeDBDataManager(this);
            dbDataManager.InsertOrUpdatePurchase(BillingHelper.SKUCodes.noAdsCode,1);
        }

    }
    private boolean isActivityStarted(Intent aIntent) {
        try {
            startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    public void btnRateThisApp_click() {
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

    @Override
    public void setLastPurchase(String code) {
        return;
    }
}
