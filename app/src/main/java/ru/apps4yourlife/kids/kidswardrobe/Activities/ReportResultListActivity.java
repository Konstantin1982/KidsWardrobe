package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import ru.apps4yourlife.kids.kidswardrobe.Adapters.ReportListAdapter;
import ru.apps4yourlife.kids.kidswardrobe.Adapters.ReportListAdapterNew;
import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBDataManager;
import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.BillingHelper;

public class ReportResultListActivity extends AppCompatActivity implements
        ReportListAdapterNew.ItemListAdapterClickHandler,
        ReportListAdapterNew.ImageListAdapterClickHandler,
        ReportListAdapterNew.SetButtonClickHandler {

    private RecyclerView mListReport;
    private ReportListAdapterNew mAdapter;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private String mLastGoodAsked;
    private int mNoAdsStatus; // 0 - can be taken, 1 - already taken
    private AdView mAdView;
    private WardrobeDBDataManager mDbManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_result_list);

        mShortAnimationDuration = getResources().getInteger(R.integer.short_animation_duration);
        mListReport = (RecyclerView) findViewById(R.id.listReportRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        layoutManager.setMeasurementCacheEnabled(false);

        WardrobeDBDataManager dbDataManager = new WardrobeDBDataManager(this);
        mNoAdsStatus = dbDataManager.getPurchaseStatus(BillingHelper.SKUCodes.noAdsCode);

        mLastGoodAsked = "";
        if (mNoAdsStatus > 0) {
            // уже все куплено
            updateUI();
        } else {
            MobileAds.initialize(this, this.getString(R.string.app_id));
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("1FF81EEFAF751AD2DF1BCD1F8546349B").build();
            mAdView.loadAd(adRequest);
        }

        mDbManager = new WardrobeDBDataManager(this);
        mListReport.setLayoutManager(layoutManager);
        mListReport.setHasFixedSize(true);
        mAdapter = new ReportListAdapterNew(this, this, this, this);
        String sentFilter = getIntent().getStringExtra("FILTER");
        String sentType = getIntent().getStringExtra("SORT");
        String sentQuery = getIntent().getStringExtra("QUERY");

        if (sentType.equalsIgnoreCase("comment")) {
            setTitle(R.string.title_activity_place_report);
        }
        if (sentType.equalsIgnoreCase("child")) {
            setTitle(R.string.title_activity_children_report);
        }



        if (sentQuery == null || sentQuery.isEmpty()) {
            if (sentFilter != null &&  !sentFilter.isEmpty()) {
                mAdapter.SetFilterAndTypeAndQuery(sentFilter, sentType, "");
            }
        } else {
            if (sentQuery != null &&  !sentQuery.isEmpty()) {
                mAdapter.SetFilterAndTypeAndQuery("", "child",sentQuery);
            }
        }
        mListReport.setAdapter(mAdapter);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)  actionBar.setDisplayHomeAsUpEnabled(true);

        final ImageView zoomImage = findViewById(R.id.zoomImage);
        if (zoomImage != null) {
            zoomImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    zoomImage.setVisibility(View.GONE);
                }
            });
        }
    }

    public void updateUI() {
        AdView adView = (AdView) findViewById(R.id.adView);
        adView.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(String itemId, String itemPositionInList) {
        //Toast.makeText(this,"ITEM CLICKED",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onImageClick(Bitmap image, View view) {
        zoomImageFromThumb(view,image);
        //Toast.makeText(this,"IMAGE! CLICKED",Toast.LENGTH_SHORT).show();
    }

    private void zoomImageFromThumb(final View thumbView, Bitmap image) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        final ImageView expandedImageView = findViewById(R.id.zoomImage);
        expandedImageView.setImageBitmap(image);

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();


        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.layout_report)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        expandedImageView.setVisibility(View.VISIBLE);
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;
    }

    @Override
    public void onSetButtonClick(Button sender, int setNumber, int itemId) {
        // switch background
        int background = (int) sender.getTag(sender.getId());

        int backgroundRes;
        if (background == 1) {
            backgroundRes = R.drawable.oval_white;
        } else {
            backgroundRes = R.drawable.oval_light;
        }
        sender.setTag(sender.getId(),1 - background);
        sender.setBackground(getDrawable(backgroundRes));
        // update DB
        mDbManager.InsertOrDeleteItemToSet(itemId, setNumber);

    }

}
