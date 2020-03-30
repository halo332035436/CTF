package com.bullb.ctf.WebView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.Response.CampaignResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.Model.Campaign;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.Model.UserNotification;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.Error;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Utils.WidgetUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WebViewActivity extends AppCompatActivity {
    private WebView mWebView;
    private AVLoadingIndicatorView progressBar;

    private ApiService apiService;
    private KeyTools keyTools;
    private Call<BaseResponse> getNotificationTask;
    private Call<BaseResponse> getSingleNotificationTask;
    private Call<BaseResponse> getCampaignTask;
    private Call<BaseResponse> getUserTask;

    private String data;
    private String id;
    private String type;
    private String url;


    private Gson gson;


    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private final static int FILE_REQUEST = 100;
    private final static int VIDEO_REQUEST = 120;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        keyTools = KeyTools.getInstance(this);
        apiService = ServiceGenerator.createService(ApiService.class, this);

        data = getIntent().getStringExtra("data");
        id = getIntent().getStringExtra("n_id");
        type = getIntent().getStringExtra("type");
        url = getIntent().getStringExtra("url");

        gson = new Gson();

        initUi();

//        getData();
    }



    private void initUi(){
        mWebView = (WebView) findViewById(R.id.webview);
        progressBar = (AVLoadingIndicatorView)findViewById(R.id.progress_bar);

        setToolbar();

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new MyWebClient(null, mWebView));
        mWebView.setWebChromeClient(new MyChromeWebClient());
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setBackgroundColor(0);
        mWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if(url.length()<3 || !url.substring(url.length()-3).equals("pdf")){
                    return;
                }
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }


        });

        if(getIntent().getStringExtra("notification")!=null){
            getNotification();
        }
        if (data!= null)
            loadData(data);
        else if (type != null){
            if (id == null || id.isEmpty()){
                SharedUtils.definedError(this, Error.NO_NOTIFICATION_ID_ERROR);
            }
            if (type == null || type.isEmpty()){
                SharedUtils.definedError(this, Error.NO_NOTIFICATION_TYPE_ERROR);
            }

            if (type.equals("notification")){
                getNotificationList();
            }
            else if (type.equals("campaign")){
                getCampaign();
            }
            else if (type.equals("banner")){
                loadUrl(url);
            }
            else{
                SharedUtils.definedError(this, Error.UNDEFINED_NOTIFICATION_TYPE_ERROR);
            }
        }
//        mWebView.loadUrl("https://www.w3schools.com/tags/tryit.asp?filename=tryhtml5_input_type_file");
        mWebView.loadUrl(getIntent().getStringExtra("data"));
//        mWebView.getSettings().setSupportMultipleWindows(true);
//        mWebView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg)
//            {
//                WebView.HitTestResult result = view.getHitTestResult();
//                String data = result.getExtra();
//                Context context;
//                context = view.getContext();
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
//                context.startActivity(browserIntent);
//                return false;
//            }
//        });

    }


    private void getNotificationList() {
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getNotificationList();
            }
        };

        if (getNotificationTask != null) getNotificationTask.cancel();

        progressBar.setVisibility(View.VISIBLE);

        final Call<BaseResponse> myNotificationListTask = apiService.getNotificationListTask("Bearer " + SharedPreference.getToken(this));
        getNotificationTask = myNotificationListTask;
        myNotificationListTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!myNotificationListTask.isCanceled()) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        ArrayList<UserNotification> temp = new Gson().fromJson(data, new TypeToken<ArrayList<UserNotification>>(){}.getType());

                        setNotificationData(temp, id);
                        //clear notification count
                        SharedPreference.setNotificationUnread(WebViewActivity.this, false);
                        SharedPreference.setNotificationUnreadCount(WebViewActivity.this, 0);

                    } else {
                        SharedUtils.handleServerError(WebViewActivity.this, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!myNotificationListTask.isCanceled()){
                    progressBar.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetryUncancellable(WebViewActivity.this, retry);
                }
            }
        });
    }

    private void setNotificationData(ArrayList<UserNotification> temp, String id) {
        for (UserNotification notification: temp){
            if (notification.id.equals(id)){
//                loadData(notification.data.details);
                loadData(notification.message);
                return;
            }
        }
        SharedUtils.definedError(this, Error.ID_NOT_IN_USER);
    }

    private void getCampaign(){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getCampaign();
            }
        };

        getCampaignTask  = apiService.getSpecificCampaignTask("Bearer " + SharedPreference.getToken(WebViewActivity.this), id);

        progressBar.setVisibility(View.VISIBLE);
        getCampaignTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!getCampaignTask.isCanceled()){
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        Campaign campaign = new Gson().fromJson(data, Campaign.class);
                        loadData(campaign.details);
                    }
                } else {
                    SharedUtils.handleServerError(WebViewActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!getCampaignTask.isCanceled()) {
                    progressBar.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetryUncancellable(WebViewActivity.this, retry);
                }
            }
        });
    }

    private void setCampaignData(CampaignResponse temp, String id) {
        for (Campaign campaign: temp.campaigns){
            if (campaign.id.equals(id)){
                loadData(campaign.details);
                return;
            }
        }
        SharedUtils.definedError(this, Error.ID_NOT_IN_USER);
    }

    private void loadUrl(String url){

        mWebView.loadUrl(url);
    }



    private void loadData(String data){
        mWebView.loadDataWithBaseURL("", "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "</head>\n" +
                "<body>\n" +
                data+
                "</body>\n" +
                "</html>", "text/html", "UTF-8", "");
    }


    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);

        toolbarTitle.setText(getIntent().getStringExtra("title"));
        toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.toolbar_bg));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notificationBtn.setOnClickListener(new WidgetUtils.NotificationButtonListener(this));
        notificationBtn.setVisibility(View.GONE);
        backBtn.setOnClickListener(new WidgetUtils.BackButtonListener(this));
        backBtn.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        if (getNotificationTask != null){
            getNotificationTask.cancel();
        }
        if (getCampaignTask != null){
            getCampaignTask.cancel();
        }
        super.onDestroy();
    }

    private void getNotification(){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getNotification();
            }
        };

        getSingleNotificationTask  = apiService.getNotificationTask("Bearer " + SharedPreference.getToken(WebViewActivity.this), getIntent().getStringExtra("notification"));

        progressBar.setVisibility(View.VISIBLE);
        getSingleNotificationTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!getSingleNotificationTask.isCanceled()){
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        String data = keyTools.decryptData(response.body().iv, response.body().data);
                        UserNotification notification = new Gson().fromJson(data, UserNotification.class);
                        loadData(notification.message);
                        updateUnread();
                    }
                } else {
                    SharedUtils.handleServerError(WebViewActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (!getSingleNotificationTask.isCanceled()) {
                    progressBar.setVisibility(View.GONE);
                    SharedUtils.networkErrorDialogWithRetryUncancellable(WebViewActivity.this, retry);
                }
            }
        });
    }

    private void updateUnread(){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateUnread();
            }
        };

        getUserTask = apiService.getCurrentUserTask("Bearer " + SharedPreference.getToken(this));
        getUserTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful()){
                    String data = keyTools.decryptData(response.body().iv,response.body().data);
                    User user = gson.fromJson(data, User.class);

                    //set Unread Notification
                    if (user.unread_notifications_count > 0){
                        SharedPreference.setNotificationUnread(WebViewActivity.this, true);
                    }else{
                        SharedPreference.setNotificationUnread(WebViewActivity.this, false);
                    }
                    SharedPreference.setNotificationUnreadCount(WebViewActivity.this, user.unread_notifications_count);


                } else{
//                    SharedUtils.networkErrorDialogWithRetryUncancellable(WebViewActivity.this, retry);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
//                SharedUtils.networkErrorDialogWithRetryUncancellable(WebViewActivity.this, retry);
            }
        });
    }


    //    private void getData() {
//        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                getData();
//            }
//        };
//
//        if (getNotificationTask != null) getNotificationTask.cancel();
//
//        progressBar.setVisibility(View.VISIBLE);
//
//        final Call<BaseResponse> myNotificationTask = apiService.getNotificationTask("Bearer " + SharedPreference.getToken(this), getIntent().getStringExtra("notification_id"));
//        getNotificationTask = myNotificationTask;
//        myNotificationTask.enqueue(new Callback<BaseResponse>() {
//            @Override
//            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
//                if (!myNotificationTask.isCanceled()) {
//                    progressBar.setVisibility(View.GONE);
//                    if (response.isSuccessful()) {
//                        String data = keyTools.decryptData(response.body().iv, response.body().data, WebViewActivity.this);
//                    } else {
//                        SharedUtils.handleServerError(WebViewActivity.this, response);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BaseResponse> call, Throwable t) {
//                if (!myNotificationTask.isCanceled()){
//                    progressBar.setVisibility(View.GONE);
//                    SharedUtils.networkErrorDialogWithRetryUncancellable(WebViewActivity.this, retry);
//                }
//            }
//        });
//    }


    private File fileUri = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + SystemClock.currentThreadTimeMillis() + ".jpg");
    private Uri imageUri;

    //自定义 WebChromeClient 辅助WebView处理图片上传操作【<input type=file> 文件上传标签】
    public class MyChromeWebClient extends WebChromeClient {

//        // For Android 3.0-
//        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
//            mUploadMessage = uploadMsg;
//            if (videoFlag) {
//                recordVideo();
//            } else {
//                uploadFile();
//            }
//
//        }
//
//        // For Android 3.0+
//        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
//            mUploadMessage = uploadMsg;
//            if (videoFlag) {
//                recordVideo();
//            } else {
//                uploadFile();
//            }
//        }
//
//        //For Android 4.1
//        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
//            mUploadMessage = uploadMsg;
//            if (videoFlag) {
//                recordVideo();
//            } else {
//                uploadFile();
//            }
//        }

        // For Android 5.0+
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            mUploadCallbackAboveL = filePathCallback;
            uploadFile();
            return true;
        }
    }

    /**
     * 拍照
     */
    private void uploadFile() {
        imageUri = Uri.fromFile(fileUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this, getPackageName()+".provider", fileUri);//通过FileProvider创建一个content类型的Uri
        }

//        Intent intentCamera = new Intent();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
//        }
//        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//        //将拍照结果保存至photo_file的Uri中，不保留在相册中
//        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);


        Intent pickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickerIntent.setType("*/*");


        Intent chooserIntent = Intent.createChooser(pickerIntent, "");
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{intentCamera});

        startActivityForResult(chooserIntent, FILE_REQUEST);

//        PhotoUtils.takePicture(this, imageUri, FILE_REQUEST);
//        PhotoUtils.openPic(this, FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) {
                return;
            }
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILE_REQUEST || mUploadCallbackAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                results = new Uri[]{imageUri};
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }

                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }
        mUploadCallbackAboveL.onReceiveValue(results);
        mUploadCallbackAboveL = null;
    }

}
