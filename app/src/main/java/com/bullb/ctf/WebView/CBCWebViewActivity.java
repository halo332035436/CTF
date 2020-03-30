package com.bullb.ctf.WebView;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
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
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bullb.ctf.BuildConfig;
import com.bullb.ctf.Login.SplashActivity;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.LanguageUtils;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.WidgetUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CBCWebViewActivity extends AppCompatActivity {
    private LinearLayout ll;
//    private BTWebView webview;
    private WebView webView;
    private KeyTools keyTools;

    public static final String SERVICE_EVALUTION = "SERVICE_EVALUTION";
    public static final String PERFORMANCE_ASSESS = "PERFORMANCE_ASSESS";
    public static final String SEVEN_FISH_QIYU = "SEVEN_FISH_QIYU";

    String key = "rnfEKcintpeyoyct";//16

    byte[] initVector;
    String ivString;

    private String url;
    private String title;

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private final static int PHOTO_REQUEST = 100;
    private final static int VIDEO_REQUEST = 120;
    private MultiplePermissionsListener permissionsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);


        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        String toEncrytedString = SharedPreference.getUser(this).id;

        setContentView(R.layout.activity_checkin_webview);

        String type = getIntent().getStringExtra("type");
//        if(type == null) finish();
        switch (type){
            case SEVEN_FISH_QIYU:
                leftLimit = 48; // letter '0'
                rightLimit = 57; // letter '9'
                toEncrytedString = getQiyuString();
                title = getString(R.string.customer_service);
                url = BuildConfig.CUSTOMER_SERVICE_URL;
                key = "CA2F594A50414398";


                createPermissionListeners();
                Dexter.continuePendingRequestsIfPossible(permissionsListener);
                //Ask for permission before everything start
                Dexter.checkPermissions(permissionsListener, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);


                break;
            case SERVICE_EVALUTION:
                title = getString(R.string.service_evaluation);
                url = BuildConfig.SERVICE_EVA_URL;
                break;
            case PERFORMANCE_ASSESS:
            default:
                title = getString(R.string.performance_assessment);
                url = BuildConfig.PERFORM_ASSESS_URL;
                break;
        }


        setToolbar();

        ll = (LinearLayout)findViewById(R.id.ll);
//        webview = new BTWebView(this);
        webView = new WebView(this);


        //辅助WebView处理图片上传操作
        webView.setWebChromeClient(new MyChromeWebClient());


        int targetStringLength = 16;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedIVString = buffer.toString();

        Log.d("CBC", "id: "+SharedPreference.getUser(this).id);
        Log.d("CBC", "iv: "+generatedIVString+", "+Base64.encodeToString(generatedIVString.getBytes(), Base64.DEFAULT));
        Log.d("CBC", "key: "+key);


        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        ll.addView(webview, param);
        ll.addView(webView, param);

        String companyCode = "ctf";
        if(BuildConfig.FLAVOR_release_type.equals("CNStaging")||BuildConfig.FLAVOR_release_type.equals("HKStaging")){
            companyCode = "ctfuat";
        }

        String desId = "";
        try {
            desId = KeyTools.performanceDesEncryptId(key, toEncrytedString, generatedIVString);
        }catch (Exception e){
            e.printStackTrace();
            finish();
        }
        decodeCBC(desId, generatedIVString);
        desId = KeyTools.toUrlEncode(desId);

        String ivUrl = Base64.encodeToString(generatedIVString.getBytes(), Base64.DEFAULT);
        ivString = KeyTools.toUrlEncode(ivUrl.replace("\n", "").replace("\r", ""));
//        webview.setData(BuildConfig.PERFORM_ASSESS_URL + "?iv="+ivString+"&id="+ desId);

        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(true);
        }
        webView.setWebViewClient(new MyWebClient(null, webView));
        webView.setBackgroundColor(0);
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);


        webView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == 0 && keyCode == 4) {
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }

                    return true;
                } else {
                    return false;
                }
            }
        });


        Map<String,String> extraHeaders = new HashMap<String, String>();
        extraHeaders.put("x-client-id", SharedPreference.getUUID(this));
        extraHeaders.put("x-ctf-app-id", "ctf-smart-talent");
        extraHeaders.put("x-client-os-platform", "android");
        extraHeaders.put("Authorization", "Bearer " + SharedPreference.getToken(this));

        if(type.equals(SEVEN_FISH_QIYU)){
            Log.d("WebView cbc", url + "?sr="+generatedIVString+"&key="+ desId);
            webView.loadUrl(url + "?sr=" + generatedIVString + "&key=" + desId, extraHeaders);
        }else {
            Log.d("WebView cbc", url + "?iv="+ivString+"&id="+ desId);
            webView.loadUrl(url + "?iv=" + ivString + "&id=" + desId, extraHeaders);
        }

    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        ImageView doneBtn = (ImageView)findViewById(R.id.done_btn);

        toolbarTitle.setText(title);
        toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.toolbar_bg));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notificationBtn.setVisibility(View.GONE);
        backBtn.setOnClickListener(new WidgetUtils.BackButtonListener(this));
        backBtn.setVisibility(View.VISIBLE);
        notificationBtn.setVisibility(View.GONE);
        doneBtn.setVisibility(View.GONE);
    }

    private void decodeCBC(String text, String ivString){
        try {

            IvParameterSpec iv = new IvParameterSpec(ivString.getBytes());
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decode(text, Base64.DEFAULT));
            String decryted = new String(original);

            Log.d("CBC", "decryted: "+decryted);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getQiyuString(){
        Calendar calendar = Calendar.getInstance();
        String timestamp = "";

        if(Build.VERSION.SDK_INT > 24) {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
            timestamp = format1.format(calendar.getTime());
        }else{
            timestamp = calendar.get(Calendar.YEAR)+""+(calendar.get(Calendar.MONTH)+1)+""+calendar.get(Calendar.DAY_OF_MONTH)+""+calendar.get(Calendar.HOUR)+""+calendar.get(Calendar.MINUTE)+""+calendar.get(Calendar.SECOND);
        }

        timestamp = String.valueOf(calendar.getTime().getTime());

        String groupKey = SharedPreference.getUser(this).id+timestamp;
        Log.d("CBC", "Hash: "+ timestamp + " ; "+ SharedPreference.getUser(this).id + timestamp + " ; " + KeyTools.MD5(groupKey));
        return SharedPreference.getUser(this).id + "," + timestamp + "," + KeyTools.MD5(groupKey);
    }

    private static boolean isExternalStorageDisable() {
        return !Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static String getExternalPicturesPath() {
        if (isExternalStorageDisable()) return "";
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    private File fileUri = new File(getExternalPicturesPath() + File.separator + SystemClock.currentThreadTimeMillis() + ".jpg");
    private Uri imageUri;

    //自定义 WebChromeClient 辅助WebView处理图片上传操作【<input type=file> 文件上传标签】
    public class MyChromeWebClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
//            if (newProgress == 100) {
//                progressBar.setVisibility(View.INVISIBLE);
//            } else {
//                if (View.INVISIBLE == progressBar.getVisibility()) {
//                    progressBar.setVisibility(View.VISIBLE);
//                }
//                progressBar.setProgress(newProgress);
//            }
            super.onProgressChanged(view, newProgress);
        }



        // For Android 5.0+
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            mUploadCallbackAboveL = filePathCallback;
            boolean videoFlag = false;
            if(Build.VERSION.SDK_INT >= 21) {
                for (String type : fileChooserParams.getAcceptTypes()) {
                    if (type.contains("video")) {
                        videoFlag = true;
                    }
                }
            }
            if (videoFlag) {
                recordVideo();
            } else {
                takePhoto();
            }
            return true;
        }
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        imageUri = Uri.fromFile(fileUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            imageUri = FileProvider.getUriForFile(this, getPackageName(), fileUri);//通过FileProvider创建一个content类型的Uri
            imageUri = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    fileUri);
        }

        Intent intentCamera = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //将拍照结果保存至photo_file的Uri中，不保留在相册中
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);


        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");


        Intent chooserIntent = Intent.createChooser(photoPickerIntent, "");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{intentCamera});

        startActivityForResult(chooserIntent, PHOTO_REQUEST);

//        PhotoUtils.takePicture(this, imageUri, PHOTO_REQUEST);
//        PhotoUtils.openPic(this, PHOTO_REQUEST);
    }

    /**
     * 录像
     */
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        //限制时长
//        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);

        Intent videoPicketIntent = new Intent(Intent.ACTION_GET_CONTENT);
        videoPicketIntent.setType("video/*");

        Intent chooserIntent = Intent.createChooser(videoPicketIntent, "");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{intent});

        //开启摄像机
//        startActivityForResult(intent, VIDEO_REQUEST);
        startActivityForResult(chooserIntent, VIDEO_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST) {
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
        } else if (requestCode == VIDEO_REQUEST) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) {
                return;
            }

            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                if (resultCode == RESULT_OK) {
                    mUploadCallbackAboveL.onReceiveValue(new Uri[]{result});
                    mUploadCallbackAboveL = null;
                } else {
                    mUploadCallbackAboveL.onReceiveValue(new Uri[]{});
                    mUploadCallbackAboveL = null;
                }

            } else if (mUploadMessage != null) {
                if (resultCode == RESULT_OK) {
                    mUploadMessage.onReceiveValue(result);
                    mUploadMessage = null;
                } else {
                    mUploadMessage.onReceiveValue(Uri.EMPTY);
                    mUploadMessage = null;
                }

            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != PHOTO_REQUEST || mUploadCallbackAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            Log.d("WebViewActivity", "onActivityResultAboveL: "+data);
            if (data == null) {
//                compressImage(imageUri);
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

//    void compressImage(Uri sourceuri)
//    {
//        String sourceFilename= sourceuri.getPath();
//        String destinationFilename = sourceuri.getPath();
//
//        File file = new File(fileUri.getPath());
//        File compressedImageFile = new Compressor.Builder(this)
//                .setMaxWidth(1500)
//                .setMaxHeight(1500)
//                .setQuality(100)
//                .setCompressFormat(Bitmap.CompressFormat.JPEG)
//                .build()
//                .compressToFile(file);
//
//        BufferedInputStream bis = null;
//        BufferedOutputStream bos = null;
//
//        try {
//            bis = new BufferedInputStream(new FileInputStream(compressedImageFile));
//            bos = new BufferedOutputStream(new FileOutputStream(fileUri.getPath(), false));
//            byte[] buf = new byte[1024];
//            bis.read(buf);
//            do {
//                bos.write(buf);
//            } while(bis.read(buf) != -1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (bis != null) bis.close();
//                if (bos != null) bos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private void createPermissionListeners() {
        MultiplePermissionsListener feedbackViewMultiplePermissionListener = new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()){

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        webview.destroy();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
////        webview.setResult(requestCode, resultCode, data);
//    }

}
