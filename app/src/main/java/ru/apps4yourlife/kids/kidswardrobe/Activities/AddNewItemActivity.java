package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import ru.apps4yourlife.kids.kidswardrobe.R;

public class AddNewItemActivity extends AppCompatActivity {
    private static final String TOAST_TEXT = "Test ads are being shown. ";

    private int mDetailShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        AdView adView = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        // Toasts the test ad message on the screen. Remove this after defining your own ad unit ID.
        Toast.makeText(this, TOAST_TEXT, Toast.LENGTH_LONG).show();
        mDetailShown = 0;
    }

    public void btnShowDetail_clicked(View v) {


        AutoCompleteTextView editView;
        mDetailShown = 1 - mDetailShown;
        int visibility;
        if (mDetailShown == 1) visibility = View.VISIBLE;
        else  visibility = View.GONE;


        editView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView4);
        editView.setVisibility(visibility);
        editView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView5);
        editView.setVisibility(visibility);
        editView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView6);
        editView.setVisibility(visibility);
        editView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView7);
        editView.setVisibility(visibility);
        editView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView8);
        editView.setVisibility(visibility);

        Button mButton = (Button) findViewById(R.id.buttonShowMoreDetails);
        if (mDetailShown == 0)
            mButton.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_expand_more_black_24dp, 0);
        else
            mButton.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_expand_less_black_24dp, 0);
    }
}
