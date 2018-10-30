package ru.apps4yourlife.kids.kidswardrobe.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
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
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.query.SortOrder;
import com.google.android.gms.drive.query.SortableField;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.CharBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import ru.apps4yourlife.kids.kidswardrobe.Data.WardrobeDBHelper;
import ru.apps4yourlife.kids.kidswardrobe.R;
import ru.apps4yourlife.kids.kidswardrobe.Utilities.ChooseRestoreFolderDialogFragment;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, ChooseRestoreFolderDialogFragment.ChooseRestoreFolderDialogFragmentListener {


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
    private ArrayList<String> mFilesToCopy;
    private int mCount;
    private DriveContents mContents;
    private int mCheckedRestore;

    protected static final int PARAM_ROOT = 0;
    protected static final int PARAM_KIDS = 1;
    protected static final int PARAM_DATE = 2;

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

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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

    public void updateUI(GoogleSignInAccount account) {
        if (account != null) {
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
        if (mSignInAccount != null && mSignInAccount.getGrantedScopes().containsAll(mRequiredScopes)) {
            if (doNextOperation == 1) {
                doNextOperation = 0;
                CreateBackup_btn(findViewById(android.R.id.content));
                return;
            }
            if (doNextOperation == 2) {
                doNextOperation = 0;
                CreateRestore_btn(findViewById(android.R.id.content));
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
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    public void CreateBackup_btn(View view) {
        if (mSignInAccount == null || !mSignInAccount.getGrantedScopes().containsAll(mRequiredScopes)) {
            doNextOperation = 1;
            signIn();
            Toast.makeText(this, "Необходимо залогиниться в Google", Toast.LENGTH_LONG).show();
            return;
        }
        mBackupFolderName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        runTask(0);
    }

    public void CreateRestore_btn(View view) {
        if (mSignInAccount == null || !mSignInAccount.getGrantedScopes().containsAll(mRequiredScopes)) {
            doNextOperation = 2;
            signIn();
            Toast.makeText(this, "Необходимо залогиниться в Google", Toast.LENGTH_LONG).show();
            return;
        }
        runTask(1);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            mSignInAccount = completedTask.getResult(ApiException.class);
            updateUI(mSignInAccount);
            if (doNextOperation == 1) {
                CreateBackup_btn(findViewById(android.R.id.content));
            }
        } catch (ApiException e) {
            Toast.makeText(this,"Произошла ошибка при попытке логина в Google.", Toast.LENGTH_LONG).show();
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

    public void runTask(int direction) {
        CheckBox wifiCheckBox = (CheckBox) findViewById(R.id.wifiCheckBox);
        boolean isOK = true;
        if (wifiCheckBox.isChecked()) {
            // WIFI only
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (!mWifi.isConnected()) {
                isOK = false;
                Toast.makeText(this, "Wifi не подключен. Подключите Wi-Fi или разрешите приложению работать без Wi-Fi.", Toast.LENGTH_LONG).show();
            }
        }
        if (isOK) {
            if (direction == 0) RunBackupOperationStep(1);
            if (direction == 1) RunRestoreOperationStep(1);
        }
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
                            updateUI(mSignInAccount = null);
                        }
                    });
        }
    }


    protected DriveResourceClient getDriveResourceClient() {
        return mDriveResourceClient;
    }

    public String getFolderName(int parameter) {
        if (parameter == PARAM_ROOT) return "Apps";
        if (parameter == PARAM_KIDS) return "KidsWardrobe";
        if (parameter == PARAM_DATE) return mBackupFolderName;
        return "";
    }

    public void RunBackupOperationStep(int stepNumber) {
        if (mDriveClient == null || mDriveResourceClient == null ) {
            initializeDriveClient(mSignInAccount);
        }
        findViewById(R.id.layout_progress_parent).setVisibility(View.VISIBLE);
        TextView progressHeader = (TextView) findViewById(R.id.header_progress);
        progressHeader.setText("Создаем резервную копию...");
        switch (stepNumber) {
            case 1:
                UpdateBackupProgress(0);
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
                break;
            case 4:
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
                break;
            case 5:
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
                break;
            case 6:
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
                break;
            case 7:
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
                mFilesToCopy = new ArrayList<>();
                mFilesToCopy.add(this.getDatabasePath(WardrobeDBHelper.DATABASE_NAME).getPath());
                File directory = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File[] files = directory.listFiles();
                if (files.length > 0) {
                    for (int i = 0; i < files.length; i++) {
                        mFilesToCopy.add(files[i].getAbsolutePath());
                    }
                }
                mCount = 0;
                RunBackupOperationStep(9);
                break;
            case 9:
                UpdateBackupProgress(1);
                getDriveResourceClient().createContents()
                    .addOnSuccessListener(this, new OnSuccessListener<DriveContents>() {
                        @Override
                        public void onSuccess(DriveContents driveContents) {
                            mContents = driveContents;
                            RunBackupOperationStep(10);
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ShowErrorMessage(9);
                        }
                    });
                break;
            case 10:
                OutputStream outputStream = mContents.getOutputStream();
                final File sourceFile = new File(mFilesToCopy.get(mCount));
                try {
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(sourceFile.getName())
                            .setMimeType("text/plain")
                            .setStarred(false)
                            .build();
                    getDriveResourceClient().createFile(mDriveFolder, changeSet, mContents)
                            .addOnSuccessListener(this, new OnSuccessListener<DriveFile>() {
                                @Override
                                public void onSuccess(DriveFile driveFile) {
                                    if (mCount < mFilesToCopy.size() - 1) {
                                        mCount++;
                                        RunBackupOperationStep(9);
                                    } else {
                                        ShowErrorMessage(200);
                                    }
                                }
                            })
                            .addOnFailureListener(this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    ShowErrorMessage(10);
                                }
                            });

                } catch (IOException e1) {

                }
                break;
        }
    }


    public void RunRestoreOperationStep(int stepNumber) {
        if (mDriveClient == null || mDriveResourceClient == null ) {
            initializeDriveClient(mSignInAccount);
        }
        switch (stepNumber) {
            case 1:
                mDriveResourceClient.getRootFolder()
                        .addOnSuccessListener(this, new OnSuccessListener<DriveFolder>() {
                            @Override
                            public void onSuccess(DriveFolder driveFolder) {
                                mDriveFolder = driveFolder;
                                mDriveId = mDriveFolder.getDriveId();
                                RunRestoreOperationStep(2);
                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ShowErrorMessage(1001);
                            }
                        });
                break;
            case 2:
                Query query = new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE))
                        .addFilter(Filters.eq(SearchableField.TITLE, getFolderName(PARAM_ROOT)))
                        .build();
                getDriveResourceClient().queryChildren(mDriveFolder, query)
                        .addOnSuccessListener(this, new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadata) {
                                mMetadataBuffer = metadata;
                                RunRestoreOperationStep(3);
                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ShowErrorMessage(1002);
                            }
                        });
                break;
            case 3:
                boolean isFound = false;
                if (mMetadataBuffer != null) {
                    if (mMetadataBuffer.getCount() > 0) {
                        Metadata data = mMetadataBuffer.get(0);
                        mDriveFolder = data.getDriveId().asDriveFolder();
                        mDriveId = data.getDriveId();
                        isFound = true;
                        RunRestoreOperationStep(4);
                    }
                }
                if (isFound == false) {
                    ShowErrorMessage(1003);
                }
                break;
            case 4:
                query = new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE))
                        .addFilter(Filters.eq(SearchableField.TITLE, getFolderName(PARAM_KIDS)))
                        .build();
                getDriveResourceClient().queryChildren(mDriveFolder, query)
                        .addOnSuccessListener(this, new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadata) {
                                mMetadataBuffer = metadata;
                                RunRestoreOperationStep(5);
                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ShowErrorMessage(1004);
                            }
                        });
                break;
            case 5:
                isFound = false;
                if (mMetadataBuffer != null) {
                    if (mMetadataBuffer.getCount() > 0) {
                        Metadata data = mMetadataBuffer.get(0);
                        mDriveFolder = data.getDriveId().asDriveFolder();
                        mDriveId = data.getDriveId();
                        isFound = true;
                        RunRestoreOperationStep(6);
                    }
                }
                if (isFound == false) {
                    ShowErrorMessage(1005);
                }
                break;
            case 6:
                query = new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE))
                        .setSortOrder(new SortOrder.Builder().addSortDescending(SortableField.CREATED_DATE).build())
                        .build();
                getDriveResourceClient().queryChildren(mDriveFolder, query)
                        .addOnSuccessListener(this, new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadata) {
                                mMetadataBuffer = metadata;
                                RunRestoreOperationStep(7);
                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ShowErrorMessage(1006);
                            }
                        });
                break;
            case 7:
                isFound = false;
                if (mMetadataBuffer != null) {
                    if (mMetadataBuffer.getCount() > 0) {
                            ChooseRestoreFolderDialogFragment dialog = new ChooseRestoreFolderDialogFragment();
                            dialog.setmListener(this);
                            dialog.show(getSupportFragmentManager(), "ChooseRestoreFolderDialogFragment");
                        isFound = true;
                    }
                }
                if (isFound == false) {
                    ShowErrorMessage(1007);
                }
                break;
            case 8:
                findViewById(R.id.layout_progress_parent).setVisibility(View.VISIBLE);
                TextView progressHeader = (TextView) findViewById(R.id.header_progress);
                progressHeader.setText("Восстанавливаем из резервной копии...");
                Metadata data = mMetadataBuffer.get(mCheckedRestore);
                mDriveFolder = data.getDriveId().asDriveFolder();
                mDriveId = data.getDriveId();
                query = new Query.Builder().build();
                getDriveResourceClient().queryChildren(mDriveFolder, query)
                        .addOnSuccessListener(this, new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadata) {
                                mMetadataBuffer = metadata;
                                mCount = 0;
                                RunRestoreOperationStep(9);
                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ShowErrorMessage(1008);
                            }
                        });
                break;
            case 9:
                UpdateBackupProgress(2);
                if (mMetadataBuffer.getCount() > 0 ) {
                    Metadata fileData = mMetadataBuffer.get(mCount);
                    DriveId id = fileData.getDriveId();
                    getDriveResourceClient().openFile(id.asDriveFile(),DriveFile.MODE_READ_ONLY)
                            .addOnFailureListener(this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    ShowErrorMessage(1009);
                                }
                            })
                            .addOnSuccessListener(this, new OnSuccessListener<DriveContents>() {
                                @Override
                                public void onSuccess(DriveContents driveContents) {
                                    mContents = driveContents;
                                    RunRestoreOperationStep(10);
                                }
                            });
                } else {
                    ShowErrorMessage(1009);
                }
                break;
            case 10:
                try {
                    Metadata fileData2 = mMetadataBuffer.get(mCount);
                    String targetFilePath = "";
                    if (fileData2.getFileExtension().equalsIgnoreCase("db")) {
                        targetFilePath = this.getDatabasePath(WardrobeDBHelper.DATABASE_NAME).getPath();
                    } else {
                        targetFilePath = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + "/" + fileData2.getTitle();
                    }
                    try {
                        File targetFile = new File(targetFilePath);
                        FileOutputStream fileOutputStream = new FileOutputStream(targetFile, false);
                        InputStream sourceFile = mContents.getInputStream();
                        byte[] buffer = new byte[1024];
                        int bytesRead = 0;
                        int offset = 0;
                        boolean isRead = true;
                        while (isRead) {
                            int currentBytesRead = sourceFile.read(buffer, offset, 1024);
                            if (currentBytesRead > 0) {
                                bytesRead += currentBytesRead;
                                fileOutputStream.write(buffer);
                            } else {
                                isRead = false;
                                break;
                            }
                        }
                        fileOutputStream.close();
                        getDriveResourceClient().discardContents(mContents)
                                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mCount++;
                                        if (mCount > mMetadataBuffer.getCount() - 1) {
                                            // FINISH
                                            ShowErrorMessage(1200);
                                        } else {
                                            RunRestoreOperationStep(9);
                                        }
                                    }
                                })
                                .addOnFailureListener(this, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        ShowErrorMessage(1010);
                                    }
                                });
                    } catch (Exception ex) {
                        //
                        ex.printStackTrace();
                    }
                } catch (NullPointerException ex) {
                    ShowErrorMessage(1011);
                }
                break;
        }
    }

    public void ShowErrorMessage(int step) {
        findViewById(R.id.layout_progress_parent).setVisibility(View.GONE);
        mDriveClient = null;
        if (step < 1000) {
            if (step == 200) {
                Toast.makeText(this, "Резервное копирование проведено успешно.", Toast.LENGTH_LONG).show();
            } else {
                boolean isFound = false;
                if (step == 1 || step == 2 || step == 4 || step == 6){
                    Toast.makeText(this, "Ошибка на этапе поиска нужной папки в Google Drive.", Toast.LENGTH_LONG).show();
                    isFound = true;
                }
                if (step == 3 || step == 5 || step == 7){
                    Toast.makeText(this, "Не получилось создать папку для бекапа в Google Drive.", Toast.LENGTH_LONG).show();
                    isFound = true;
                }
                if (step == 9 || step == 10) {
                    Toast.makeText(this, "Не получилось скопировать один из файлов в Google Drive.", Toast.LENGTH_LONG).show();
                    isFound = true;
                }
                if (isFound == false) {
                    Toast.makeText(this, "Неизвестная ошибка при операции Backup.", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            if (step == 1200) {
                Toast.makeText(this, "Данные восстановлены из резервной копии с Google Drive.", Toast.LENGTH_LONG).show();
            } else {
                boolean isFound = false;
                if (step < 1008){
                    Toast.makeText(this, "Не смог найти сохраненные копии на Google Drive.", Toast.LENGTH_LONG).show();
                    isFound = true;
                }
                if (step >= 1008){
                    Toast.makeText(this, "Не получилось скопировать папку из Google Drive", Toast.LENGTH_LONG).show();
                    isFound = true;
                }
                if (isFound == false) {
                    Toast.makeText(this, "Неизвестная ошибка при операции Restore.", Toast.LENGTH_LONG).show();
                }
            }
        }
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
            progressFooter.setText("Копирую файл " + (mCount+1) + " из " + mFilesToCopy.size() + ".");
        }
        if (stage == 2) {
            TextView progressFooter = (TextView) findViewById(R.id.footer_progress);
            progressFooter.setText("Копирую файл " + (mCount+1) + " из " + mMetadataBuffer.getCount() + ".");
        }
    }

    @Override
    public void OnClickRestoreName(int position) {
        mCheckedRestore = position;
        RunRestoreOperationStep(8);
    }

    @Override
    public MetadataBuffer SetParameters() {
        return mMetadataBuffer;
    }
}


