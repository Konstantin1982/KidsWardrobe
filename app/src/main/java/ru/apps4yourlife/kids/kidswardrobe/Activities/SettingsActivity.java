package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBHelper;
import ru.apps4yourlife.kids.kidswardrobe.R;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int RC_SIGN_IN = 999;
    protected static final int REQUEST_CODE_SIGN_IN = 0;
    protected static final int REQUEST_CODE_OPEN_ITEM = 1;
    private int doNextOperation;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private Set<Scope> mRequiredScopes;
    private GoogleSignInAccount mSignInAccount;
    private DriveFolder mDriveFolder;
    private DriveId mDriveId;
    private boolean mResultOfStep;
    private MetadataBuffer mMetadataBuffer;
    private String mBackupFolderName;
    private  String mResourceId;
    private ArrayList<String> mFilesToCopy;
    protected static final int PARAM_ROOT = 0;
    protected static final int PARAM_KIDS = 1;
    protected static final int PARAM_DATE = 2;
    protected static final int PARAM_ROOT_ROOT = 3;

    protected static final int OPERATION_CHECK_FOLDER = 0;
    protected static final int OPERATION_CREATE_FOLDER = 1;
    protected static final int OPERATION_GETROOT_FOLDER = 2;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        mRequiredScopes = new HashSet<>(2);
        mRequiredScopes.add(Drive.SCOPE_FILE);
        mRequiredScopes.add(Drive.SCOPE_APPFOLDER);
        doNextOperation = 0;
        mSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(mSignInAccount);
    }


    public void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            Toast.makeText(this,"You're signed in.", Toast.LENGTH_SHORT).show();
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
            TextView emailTextView = (TextView) findViewById(R.id.email_text_view);
            emailTextView.setText(account.getEmail());

            ImageView profilePhotoImageView = (ImageView) findViewById(R.id.profile_photo);
            Picasso.get().load(account.getPhotoUrl()).into(profilePhotoImageView);
        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);

            TextView emailTextView = (TextView) findViewById(R.id.email_text_view);
            emailTextView.setText("E-mail address");

            ImageView profilePhotoImageView = (ImageView) findViewById(R.id.profile_photo);
            profilePhotoImageView.setImageBitmap(null);
        }
    }

    private void signIn() {
        //Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //startActivityForResult(signInIntent, RC_SIGN_IN);
        if (mSignInAccount != null && mSignInAccount.getGrantedScopes().containsAll(mRequiredScopes)) {
            if (doNextOperation == 1) {
                doNextOperation = 0;
                CreateBackup_btn(findViewById(android.R.id.content));
                return;
            }
        } else {
            GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(Drive.SCOPE_FILE)
                            .requestScopes(Drive.SCOPE_APPFOLDER)
                            .requestEmail()
                            .requestProfile()
                            .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, signInOptions);
            startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        }
    }


    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
        //onDriveClientReady();
        //createFile();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    public void CreateBackup_btn(View view) {
        //Chieck SignIN
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount == null || !signInAccount.getGrantedScopes().containsAll(mRequiredScopes)) {
            // no login or no enough access
            doNextOperation = 1;
            signIn();
            Toast.makeText(this, "Необходимо залогиниться в Google", Toast.LENGTH_LONG).show();
            return;
        }
        // Start Task
        mBackupFolderName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        runTask();
    }



    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
            if (doNextOperation == 1) {
                CreateBackup_btn(findViewById(android.R.id.content));
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("SIGN-IN", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this,"Произошла ошибка.", Toast.LENGTH_LONG).show();
            updateUI(null);
        }
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    public void runTask() {
        RunBackupOperationStep(1);
    }

    public void signOut(View v) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this,gso);
        if (googleSignInClient != null) {
            googleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updateUI(null);
                        }
                    });
        }
    }

    private void createFile() {

    }
    protected void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    protected DriveClient getDriveClient() {
        return mDriveClient;
    }

    protected DriveResourceClient getDriveResourceClient() {
        return mDriveResourceClient;
    }

    public String getFolderName(int parameter) {
        if (parameter == PARAM_ROOT) return "Apps3";
        if (parameter == PARAM_KIDS) return "KidsWardrobe3";
        if (parameter == PARAM_DATE) return mBackupFolderName;
        return "";
    }

    public void RunBackupOperationStep(int stepNumber) {
        if (mDriveClient == null || mDriveResourceClient == null ) {
            initializeDriveClient(mSignInAccount);
        }
        switch (stepNumber) {
            case 1:
                // Get Root Folder
                mDriveResourceClient.getRootFolder()
                        .addOnSuccessListener(this, new OnSuccessListener<DriveFolder>() {
                            @Override
                            public void onSuccess(DriveFolder driveFolder) {
                                mDriveFolder = driveFolder;
                                mDriveId = mDriveFolder.getDriveId();
                                RunBackupOperationStep(2);
                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ShowErrorMessage(1);
                            }
                        });
                // result - driveid for root folder
                break;
            case 2:
                // Get Children folders
                Query query = new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE))
                        .addFilter(Filters.eq(SearchableField.TITLE, getFolderName(PARAM_ROOT)))
                        .build();
                getDriveResourceClient().queryChildren(mDriveFolder, query)
                        .addOnSuccessListener(this, new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadata) {
                                mMetadataBuffer = metadata;
                                RunBackupOperationStep(3);

                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ShowErrorMessage(2);
                            }
                        });
                // result - metaarray with children
                break;
            case 3:
                // find apps in children set / create Apps
                boolean isFound = false;
                if (mMetadataBuffer != null) {
                    if (mMetadataBuffer.getCount() > 0) {
                        Metadata data = mMetadataBuffer.get(0);
                        mDriveFolder = data.getDriveId().asDriveFolder();
                        mDriveId = data.getDriveId();
                        isFound = true;
                        RunBackupOperationStep(4);
                    }
                }
                if (isFound == false) {
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(getFolderName(PARAM_ROOT))
                            .setMimeType(DriveFolder.MIME_TYPE)
                            .setStarred(false)
                            .build();
                    mDriveResourceClient.createFolder(mDriveFolder, changeSet)
                            .addOnSuccessListener(this, new OnSuccessListener<DriveFolder>() {
                                @Override
                                public void onSuccess(DriveFolder driveFolder) {
                                    mDriveFolder = driveFolder.getDriveId().asDriveFolder();
                                    mDriveId = driveFolder.getDriveId();
                                    RunBackupOperationStep(4);
                                }
                            })
                            .addOnFailureListener(this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    ShowErrorMessage(3);
                                    return;
                                }
                            })
                    ;

                }
                // result  - Apps driveid
                break;
            case 4:
                // Get Children folders
                query = new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE))
                        .addFilter(Filters.eq(SearchableField.TITLE, getFolderName(PARAM_KIDS)))
                        .build();
                getDriveResourceClient().queryChildren(mDriveFolder, query)
                        .addOnSuccessListener(this, new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadata) {
                                mMetadataBuffer = metadata;
                                RunBackupOperationStep(5);

                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ShowErrorMessage(4);
                            }
                        });
                // result - metaarray with children
                break;
            case 5:
                // find apps in children set / create KidsWardrobe
                isFound = false;
                if (mMetadataBuffer != null) {
                    if (mMetadataBuffer.getCount() > 0) {
                        Metadata data = mMetadataBuffer.get(0);
                        mDriveFolder = data.getDriveId().asDriveFolder();
                        mDriveId = data.getDriveId();
                        isFound = true;
                        RunBackupOperationStep(6);
                    }
                }
                if (isFound == false) {
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(getFolderName(PARAM_KIDS))
                            .setMimeType(DriveFolder.MIME_TYPE)
                            .setStarred(false)
                            .build();
                    mDriveResourceClient.createFolder(mDriveFolder, changeSet)
                            .addOnSuccessListener(this, new OnSuccessListener<DriveFolder>() {
                                @Override
                                public void onSuccess(DriveFolder driveFolder) {
                                    mDriveFolder = driveFolder.getDriveId().asDriveFolder();
                                    mDriveId = driveFolder.getDriveId();
                                    RunBackupOperationStep(6);
                                }
                            })
                            .addOnFailureListener(this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    ShowErrorMessage(5);
                                    return;
                                }
                            })
                    ;

                }
                // result  - KidsWardrobe driveid
                break;
            case 6:
                // Get Children folders
                query = new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE))
                       .addFilter(Filters.eq(SearchableField.TITLE, getFolderName(PARAM_DATE)))
                        .build();
                getDriveResourceClient().queryChildren(mDriveFolder, query)
                        .addOnSuccessListener(this, new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadata) {
                                mMetadataBuffer = metadata;
                                RunBackupOperationStep(7);

                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ShowErrorMessage(6);
                            }
                        });
                // result - metaarray with children
                break;
            case 7:
                // find apps in children set / create mBackupFolderName
                isFound = false;
                if (mMetadataBuffer != null) {
                    if (mMetadataBuffer.getCount() > 0) {
                        Metadata data = mMetadataBuffer.get(0);
                        mDriveFolder = data.getDriveId().asDriveFolder();
                        mDriveId = data.getDriveId();
                        isFound = true;
                        RunBackupOperationStep(8);
                    }
                }
                if (isFound == false) {
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(getFolderName(PARAM_DATE))
                            .setMimeType(DriveFolder.MIME_TYPE)
                            .setStarred(false)
                            .build();
                    mDriveResourceClient.createFolder(mDriveFolder, changeSet)
                            .addOnSuccessListener(this, new OnSuccessListener<DriveFolder>() {
                                @Override
                                public void onSuccess(DriveFolder driveFolder) {
                                    mDriveFolder = driveFolder.getDriveId().asDriveFolder();
                                    mDriveId = driveFolder.getDriveId();
                                    RunBackupOperationStep(8);
                                }
                            })
                            .addOnFailureListener(this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    ShowErrorMessage(7);
                                    return;
                                }
                            })
                    ;
                }
                break;
            case 8:
                // create list of files to beckup
                mFilesToCopy = new ArrayList<>();
                mFilesToCopy.add(this.getDatabasePath(WardrobeDBHelper.DATABASE_NAME).getPath());

                File directory = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File[] files = directory.listFiles();
                if (files.length > 0) {
                    for (int i = 0; i < files.length; i++) {
                        mFilesToCopy.add(files[i].getAbsolutePath());
                    }
                }
                RunBackupOperationStep(9);
                break;
            case 9:

                break;
        }
    }

    public void ShowErrorMessage(int step) {
        Toast.makeText(this,"Error on step " + step, Toast.LENGTH_LONG).show();
        return;
    }


}


