package ru.apps4yourlife.kids.kidswardrobe.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.DriveServiceHelper;


public class SettingsActivity extends AppCompatActivity  {

    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 2;
    protected static final int PARAM_ROOT = 0;
    protected static final int PARAM_KIDS = 1;
    protected static final int PARAM_DATE = 2;
    private String mBackupFolderName;

    private DriveServiceHelper mDriveServiceHelper;
    private String mOpenFileId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (lastSignedInAccount != null) {
            updateUI (lastSignedInAccount);
        }
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void updateUI(GoogleSignInAccount account) {
        boolean isAllow = false;
        if (account != null) {
            TextView emailTextView = (TextView) findViewById(R.id.email_text_view);
            emailTextView.setText(account.getEmail());

            TextView userNameTextView  = (TextView) findViewById(R.id.userNameTextView);
            emailTextView.setText(account.getDisplayName());
            isAllow = true;
        } else {
            TextView emailTextView = (TextView) findViewById(R.id.email_text_view);
            emailTextView.setText("E-mail address");

            TextView userNameTextView  = (TextView) findViewById(R.id.userNameTextView);
            emailTextView.setText("Неизвестный пользователь");
        }
        Button backupButton  = findViewById(R.id.backupButton);
        Button restoreButton = findViewById(R.id.restoreButton);
        backupButton.setEnabled(isAllow);
        restoreButton.setEnabled(isAllow);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                }
                break;

            case REQUEST_CODE_OPEN_DOCUMENT:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    Uri uri = resultData.getData();
                    if (uri != null) {
                        //openFileFromFilePicker(uri);
                    }
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }

    public void signInOnClick (View view) {
        requestSignIn();
    }
    /**
     * Starts a sign-in activity using {@link #REQUEST_CODE_SIGN_IN}.
     */
    private void requestSignIn() {
        Log.e("GDRIVE", "Requesting sign-in");

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    /**
     * Handles the {@code result} of a completed sign-in activity initiated from {@link
     * #requestSignIn()}.
     */
    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)

                .addOnSuccessListener(googleAccount -> {
                    Log.e("GDRIVE", "Signed in as " + googleAccount.getEmail());

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    this, Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleAccount.getAccount());
                    Drive googleDriveService =
                            new Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    new GsonFactory(),
                                    credential)
                                    .setApplicationName("Детский гардероб.")
                                    .build();

                    // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                    // Its instantiation is required before handling any onClick actions.
                    mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
                    updateUI(googleAccount);
                })
                .addOnFailureListener(exception -> Log.e("GDRIVE", "Unable to sign in.", exception));
    }





    public void CreateBackup_btn(View view) {
        if (mDriveServiceHelper == null) {
            Toast.makeText(this,"Пожалуйста, залогиньтесь в Google Drive", Toast.LENGTH_LONG).show();
            requestSignIn();
            return;
        }
        mBackupFolderName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        runTask(0);
    }

    public void CreateRestore_btn(View view) {
        if (mDriveServiceHelper == null) {
            Toast.makeText(this,"Пожалуйста, залогиньтесь в Google Drive", Toast.LENGTH_LONG).show();
            requestSignIn();
            return;
        }
        runTask(1);
    }



    public String getFolderName(int parameter) {
        if (parameter == PARAM_ROOT) return "Apps";
        if (parameter == PARAM_KIDS) return "KidsWardrobe";
        if (parameter == PARAM_DATE) return mBackupFolderName;
        return "";
    }

    public void runTask(int direction) {
        if (mDriveServiceHelper == null) {
            Toast.makeText(this,"Пожалуйста, залогиньтесь в Google Drive", Toast.LENGTH_LONG).show();
            requestSignIn();
            return;
        }
        CheckBox wifiCheckBox = (CheckBox) findViewById(R.id.wifiCheckBox);
        boolean isOK = true;
        if (wifiCheckBox.isChecked()) {
            try {
                // WIFI only
                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(NetworkCapabilities.TRANSPORT_WIFI);
                if (!mWifi.isConnected()) {
                    isOK = false;
                    Toast.makeText(this, "Wifi не подключен. Подключите Wi-Fi или разрешите приложению работать без Wi-Fi.", Toast.LENGTH_LONG).show();
                }
            } catch (Exception ex) {
                isOK = false;
                Toast.makeText(this, "Wifi не подключен. Подключите Wi-Fi или разрешите приложению работать без Wi-Fi.", Toast.LENGTH_LONG).show();
            }
        }
        if (isOK) {
            if (direction == 0) RunBackupOperationStep(1);
            if (direction == 1) RunRestoreOperationStep(1);
        }
    }


    public void RunBackupOperationStep(int stepNumber) {
        findViewById(R.id.layout_progress_parent).setVisibility(View.VISIBLE);
        TextView progressHeader = (TextView) findViewById(R.id.header_progress);
        progressHeader.setText("Создаем резервную копию...");
        switch (stepNumber) {
            case 1 :
                UpdateBackupProgress(0);
                mDriveServiceHelper.g
                break;
        }
    }


    public void RunRestoreOperationStep(int stepNumber) {
    }

    public void ShowErrorMessage(int step) {
        return;
    }


    public void UpdateBackupProgress(int stage){
        // 0 - подготовка
        if (stage == 0) {
            TextView progressFooter = (TextView) findViewById(R.id.footer_progress);
            progressFooter.setText("Идет подготовка к копированию файлов");
        }
        // 1 - копирование файлов
        if (stage == 1) {
            TextView progressFooter = (TextView) findViewById(R.id.footer_progress);
            //progressFooter.setText("Копирую файл " + (mCount+1) + " из " + mFilesToCopy.size() + ".");
        }
        if (stage == 2) {
            TextView progressFooter = (TextView) findViewById(R.id.footer_progress);
            //progressFooter.setText("Копирую файл " + (mCount+1) + " из " + mMetadataBuffer.getCount() + ".");
        }
    }

}


