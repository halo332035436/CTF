package com.bullb.ctf.Login;

import android.Manifest;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.Response.VersionResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.BuildConfig;
import com.bullb.ctf.LandingPageActivity;
import com.bullb.ctf.MainApplication;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.Model.Version;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.LanguageUtils;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Widget.NotificationIndicator;
import com.crashlytics.android.Crashlytics;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private final int INSTALL_TASK = 10;
    private final int ALLOW_UNKNOWN_TASK = 11;

    private KeyTools keyTools;
    private MultiplePermissionsListener permissionsListener;
    private ApiService apiService;
    private Call<VersionResponse> getVersionTask;
    private ProgressDialog progressDialog;
    private Version version;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageUtils.changeLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (SharedPreference.getUser(this) != null) {
            Crashlytics.setUserIdentifier(SharedPreference.getUser(this).id);
        }
        Crashlytics.setString("manufacturer",Build.MANUFACTURER);

        apiService = ServiceGenerator.createService(ApiService.class, this);

        keyTools = KeyTools.getInstance(this);

        SharedPreference.setChannelId(this, JPushInterface.getRegistrationID(this));
        Log.d("jiguang", JPushInterface.getRegistrationID(this));

        if (Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        createPermissionListeners();
        Dexter.continuePendingRequestsIfPossible(permissionsListener);

        //Ask for permission before everything start (Permission for push notification)
        Dexter.checkPermissions(permissionsListener, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.BLUETOOTH_ADMIN);


    }

    //check user logged in or not
    private void pageDirection(){
        Log.d("splash", "page direct");
        Intent intent = getIntent();
        if (keyTools.getPublicKey() == null || keyTools.getServerPublicKey() == null || SharedPreference.getToken(this) == null || SharedPreference.getUUID(this) == null
                || SharedPreference.getVersion(this)<BuildConfig.VERSION_CODE){
            intent.setClass(this, com.bullb.ctf.Login.LoginActivity.class);
        }
        else{
            Log.d("splash", "landing");
            intent.setClass(this, LandingPageActivity.class);
            intent.putExtra("open_app", true);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        SharedPreference.setVersion(this,BuildConfig.VERSION_CODE);
        finish();
    }


    private void getVersion(){
        Log.i("splash", "getversion");
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getVersion();
            }
        };

        getVersionTask = apiService.getVersionTask();
        getVersionTask.enqueue(new Callback<VersionResponse>() {
            @Override
            public void onResponse(Call<VersionResponse> call, Response<VersionResponse> response) {
                if (response.isSuccessful()){
                    if (BuildConfig.VERSION_CODE < response.body().getVersion().getVersion()){
                        version = response.body().getVersion();
                        showUpdateDialog(version.getUrl(), version.getVersion());
                    }
                    else{
                        pageDirection();
                    }
                }
                else{
                    if (BuildConfig.FLAVOR_server_type.equals(ServerPreference.SERVER_VERSION_HK)) {
                        new android.support.v7.app.AlertDialog.Builder(SplashActivity.this)
                                .setTitle(R.string.error_server)
                                .setMessage(R.string.error_server)
                                .setPositiveButton(R.string.retry, retry)
                                .setNegativeButton(R.string.dialog_title_server_version, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        changeServerVersion();
                                    }
                                })
                                .show();
                    }
                    else{
                        SharedUtils.handleServerError(SplashActivity.this, response);

                    }
                }
            }

            @Override
            public void onFailure(Call<VersionResponse> call, Throwable t) {
                SharedUtils.networkErrorDialogWithRetryUncancellable(SplashActivity.this, retry);
            }
        });
    }

    private void changeServerVersion(){
        final int serverVersion;
        if (ServerPreference.getServerVersion(this).equals(ServerPreference.SERVER_VERSION_HK)){
            serverVersion = 0;
        }
        else{
            serverVersion = 1;
        }
        new MaterialDialog.Builder(SplashActivity.this)
                .title(R.string.dialog_title_server_version)
                .items(R.array.server_type_arr)
                .itemsCallbackSingleChoice(serverVersion, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which != serverVersion) {
                            switch (which) {
                                case 0:
                                    ServerPreference.setServerVersion(SplashActivity.this, ServerPreference.SERVER_VERSION_HK);
                                    break;
                                case 1:
                                    ServerPreference.setServerVersion(SplashActivity.this, ServerPreference.SERVER_VERSION_CN);
                                    break;
                                default:
                                    ServerPreference.setServerVersion(SplashActivity.this, ServerPreference.SERVER_VERSION_HK);

                            }
                            apiService = ServiceGenerator.createService(ApiService.class,SplashActivity.this);
                            getVersion();


                        }
                        return true;
                    }
                })
                .positiveColor(ContextCompat.getColor(this,R.color.colorPrimary))
                .negativeColor(ContextCompat.getColor(this,R.color.colorPrimary))
                .widgetColor(ContextCompat.getColor(this,R.color.colorPrimary))
                .positiveText(R.string.choose)
                .negativeText(R.string.cancel)
                .show();
    }


    private void showUpdateDialog(final String url, final int version){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_update)
                .setMessage(R.string.dialog_content_app_update)
                .setPositiveButton(R.string.app_update, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            Log.d("unknown source", String.valueOf(Settings.Secure.getInt(getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS)));
                            if (Settings.Secure.getInt(getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS) == 0) {
                                showAllowUnknownDialog();
                            }else{
                                String mf = android.os.Build.MANUFACTURER;

                                if (mf.toLowerCase().equals("oppo") || mf.toLowerCase().equals("vivo") || mf.toLowerCase().equals("nubia")) {
                                    showDeleteDialog();
                                }
                                else{
                                    downloadApk(url, version);
                                }
                            }
                        } catch (Settings.SettingNotFoundException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void showDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_update)
                .setMessage(R.string.uninstall_update)
                .setPositiveButton(R.string.app_update, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.DOWNLOAD_URL));
                        startActivity(myIntent);
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void showAllowUnknownDialog(){
        new android.app.AlertDialog.Builder(this).setTitle(R.string.alert_permission_setting)
                .setMessage(R.string.dialog_allow_unknown_sources)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        showUpdateDialog(version.getUrl(), version.getVersion());
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivityForResult(new Intent(Settings.ACTION_SECURITY_SETTINGS),ALLOW_UNKNOWN_TASK);
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void downloadApk(final String url, final int version) {
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = "ctf_smart_talent_v_" + String.valueOf(version) + ".apk";
        destination += fileName;

        //Delete update file if exists
        final File file = new File(destination);
        if (file.exists()) {
            startInstaller(file);
            return;
            //file.delete() - test this, I think sometimes it doesnt work
        }



        FileDownloader.getImpl().create(url)
                .setPath(file.getPath())
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d("version", "pending");
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        Log.d("version", "connected");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
                        Log.d("version", "progress");
                        progressDialog.setProgress((soFarBytes*100)/totalBytes);
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                        Log.d("version", "blockComplete");
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                        Log.d("version", "retry");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        Log.d("version", "completed");
                        progressDialog.dismiss();
                        startInstaller(file);
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d("version", "paused");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Log.d("version", "error");
                        e.printStackTrace();
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                        builder.setTitle(R.string.app_update)
                                .setMessage(R.string.update_fail)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        downloadApk(url, version);
                                    }
                                })
                                .setCancelable(false)
                                .create()
                                .show();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Log.d("version", "warn");
                    }
                }).start();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.downloading));
        progressDialog.setProgress(0);
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        progressDialog.show();

    }

    private void startInstaller(File file){
        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(SplashActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            grantUriPermission("com.google.android.packageinstaller", contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent,INSTALL_TASK);
    }


    private void createPermissionListeners() {
        MultiplePermissionsListener feedbackViewMultiplePermissionListener = new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()){
                    //init push notification service
                    initNotification();

                    LanguageUtils.getLanguage(SplashActivity.this);
                        SharedPreference.clearStoredData(SplashActivity.this);
                        getVersion();
                }
                else{
                    finish();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                showPermissionRationale(token);
            }
        };


        permissionsListener =
                new CompositeMultiplePermissionsListener(feedbackViewMultiplePermissionListener,
                        SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with((ViewGroup)findViewById(R.id.root),
                                R.string.alert_permission)
                                .withOpenSettingsButton(R.string.setting)
                                .build());
    }

    public void showPermissionRationale(final PermissionToken token) {
        new android.app.AlertDialog.Builder(this).setTitle(R.string.alert_permission_setting)
                .setMessage(R.string.alert_permission)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.cancelPermissionRequest();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                .setCancelable(false)
                .show();
    }


    private void initNotification(){
        //init Notification Service
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, BuildConfig.PUSH_NOTIFICATION_API_KEY);
        boolean isEnabled = PushManager.isPushEnabled(this);
        Log.i("is enabled:", String.valueOf(isEnabled));

        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
                getResources().getIdentifier("notification_custom_builder", "layout", getPackageName()),
                getResources().getIdentifier("notification_icon", "id", getPackageName()),
                getResources().getIdentifier("notification_title", "id", getPackageName()),
                getResources().getIdentifier("notification_text", "id", getPackageName()));
        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            cBuilder.setStatusbarIcon(R.mipmap.ic_launcher);
        }
        else{
            cBuilder.setStatusbarIcon(R.mipmap.noti_icon);
        }
        cBuilder.setLayoutDrawable(R.mipmap.ic_launcher);
        cBuilder.setNotificationSound(Settings.System.DEFAULT_NOTIFICATION_URI.toString());
        cBuilder.setNotificationVibrate(new long[] { 1000, 1000});

        // 推送高级设置，通知栏样式设置为下面的ID
        PushManager.setNotificationBuilder(SplashActivity.this, 1, cBuilder);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INSTALL_TASK || requestCode == ALLOW_UNKNOWN_TASK){
            showUpdateDialog(version.getUrl(), version.getVersion());
        }
    }


    @Override
    protected void onDestroy() {
        if (getVersionTask != null){
            getVersionTask.cancel();
        }
        super.onDestroy();
    }
}
