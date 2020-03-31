package com.bullb.ctf.PersonalInfo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bullb.ctf.API.ApiService;
import com.bullb.ctf.API.Response.BaseResponse;
import com.bullb.ctf.API.ServiceGenerator;
import com.bullb.ctf.BuildConfig;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.R;
import com.bullb.ctf.SelfManagement.SelfManagementActivity;
import com.bullb.ctf.Utils.ImageCache;
import com.bullb.ctf.Utils.KeyTools;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.joooonho.SelectableRoundedImageView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PersonalInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private SelectableRoundedImageView profileImage, supervisorProfileImage;
    private TextView userNameText, userPositionText, userEntranceDateText, userEmailText, userPhoneText, userAddressText, userDepartmentText;
    private TextView supervisorNameText, supervisorPositionText, supervisorDepartmentText;
    private ImageView editProfileBtn;
    private LinearLayout userPhoneContainer, userAddressContainer, userEmailContainer;
    private View superviserContainer, middleLine;
    private final int PHONE =0, ADDRESS= 1, EMAIL = 2;

    private final int CAMERA_REQUEST = 111;
    private final int GALLERY_REQUEST = 112;
    private MultiplePermissionsListener galleryPermissionsListener;
    private String mCurrentPhotoPath;

    private ApiService apiService;
    private ApiService imageApiService;
    private Call<BaseResponse> getSupervisorTask;
    private Call<BaseResponse> uploadProfileTask;
    private Call<BaseResponse> setPersonalInfoTask;
    private Call<BaseResponse> getUserTask;

    private User user;
    private KeyTools keyTools;
    private AVLoadingIndicatorView progress;
    private User supervisor;
    private RelativeLayout loadingLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        createPermissionListeners();
        Dexter.continuePendingRequestsIfPossible(galleryPermissionsListener);
        user = SharedPreference.getUser(this);
        supervisor = SharedPreference.getSupervisor(this);
        keyTools = KeyTools.getInstance(this);
        apiService = ServiceGenerator.createService(ApiService.class, this);
        imageApiService = ServiceGenerator.createServiceForImage(ApiService.class, this);
        initUi();

        if (supervisor == null) {
            if (user.supervisor_id != null) {

                getSupervisor();
            }
            else{
                superviserContainer.setVisibility(View.GONE);
                middleLine.setVisibility(View.GONE);
            }
        }
    }

    private void initUi(){
        profileImage = (SelectableRoundedImageView) findViewById(R.id.profile_imageview);
        userNameText = (TextView)findViewById(R.id.user_name_text);
        userPositionText = (TextView)findViewById(R.id.user_position_text);
        userPhoneText = (TextView)findViewById(R.id.user_tele_text);
        userAddressText = (TextView)findViewById(R.id.user_address_text);
        userEmailText = (TextView)findViewById(R.id.user_email_text);
        userEntranceDateText = (TextView)findViewById(R.id.user_entrance_date_text);
        supervisorProfileImage = (SelectableRoundedImageView)findViewById(R.id.supervisor_profile_imageview);
        supervisorNameText = (TextView)findViewById(R.id.supervisor_name_text);
        supervisorPositionText = (TextView)findViewById(R.id.supervisor_position_text);
        supervisorDepartmentText = (TextView)findViewById(R.id.supervisor_department_text);
        editProfileBtn = (ImageView)findViewById(R.id.edit_profile_btn);
        userPhoneContainer = (LinearLayout)findViewById(R.id.user_tele_container);
        userAddressContainer = (LinearLayout)findViewById(R.id.user_address_container);
        userEmailContainer  = (LinearLayout)findViewById(R.id.user_email_container);
        userDepartmentText  = (TextView) findViewById(R.id.user_department_text);
        progress = (AVLoadingIndicatorView)findViewById(R.id.progress);
        loadingLayout = (RelativeLayout)findViewById(R.id.loading_layout);
        superviserContainer = findViewById(R.id.supervisor_info_container);
        middleLine = findViewById(R.id.hr);

        if (Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setToolbar();

        Glide.with(this)
                .load(user.getIconUrl())
                .apply(new RequestOptions().placeholder(R.drawable.user_placeholder))
                .into(profileImage);

        userNameText.setText(user.name);
        userPositionText.setText(user.title);
        userEntranceDateText.setText(getString(R.string.entry_date) + ": " + user.entry_date);
        userDepartmentText.setText(getString(R.string.department_name) + ": " + user.getLongDepartmentName());
        userEmailText.setText(getString(R.string.email) + ": " + user.email);
        userAddressText.setText(getString(R.string.address) + ": " + user.getAddress());
        userPhoneText.setText(getString(R.string.phone) + ": " + user.phone);

        if (supervisor != null)
            setSupervisor();

        ((ImageView)findViewById(R.id.user_address_edit_icon)).setVisibility(View.GONE);
        ((ImageView)findViewById(R.id.user_email_edit_icon)).setVisibility(View.GONE);
        ((ImageView)findViewById(R.id.user_tele_edit_icon)).setVisibility(View.GONE);
        if (SharedUtils.appIsHongKong()){
            ((ImageView)findViewById(R.id.edit_profile_btn)).setVisibility(View.GONE);
        }

        editProfileBtn.setOnClickListener(this);
        userAddressContainer.setOnClickListener(this);
        userEmailContainer.setOnClickListener(this);
        userPhoneContainer.setOnClickListener(this);


    }

    private void setSupervisor(){
        supervisorNameText.setText(supervisor.name);
        supervisorPositionText.setText(supervisor.title);
        supervisorDepartmentText.setText(getString(R.string.department_name) + ": " + supervisor.getLongDepartmentName());

        Glide.with(this)
                .load(supervisor.getIconUrl())
                .apply(new RequestOptions().placeholder(R.drawable.user_placeholder))
                .into(supervisorProfileImage);
    }

    private void getSupervisor(){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getSupervisor();
            }
        };

        progress.setVisibility(View.VISIBLE);
        getSupervisorTask = apiService.getUserTask("Bearer " + SharedPreference.getToken(this), user.supervisor_id);
        getSupervisorTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful()){
                    String data = keyTools.decryptData(response.body().iv,response.body().data);
                    User sup = new Gson().fromJson(data, User.class);
                    SharedPreference.setSupervisor(sup, PersonalInfoActivity.this);
                    supervisor = sup;
                    setSupervisor();
                } else{
                    SharedUtils.handleServerError(PersonalInfoActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                progress.setVisibility(View.GONE);
                SharedUtils.networkErrorDialogWithRetry(PersonalInfoActivity.this, retry);
            }
        });
    }


    private void uploadProfile(final String filePath){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadProfile(filePath);
            }
        };

        byte fileBytes[] = null;
        File file = new File(filePath);

        File compressedImageFile = new Compressor.Builder(this)
                .setMaxWidth(512)
                .setMaxHeight(512)
                .setQuality(100)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .build()
                .compressToFile(file);


        String imageString = null;
        try {
            fileBytes = FileUtils.readFileToByteArray(compressedImageFile);
            imageString = Base64.encodeToString(fileBytes, Base64.DEFAULT);
            Log.d("image", imageString);

        } catch (IOException e) {
            e.printStackTrace();
        }


        if (imageString == null || imageString.isEmpty()){
            Log.d("debug", "null bytes");
        }
        else{
             Map<String, String> dataMap = new HashMap<>();
            dataMap = keyTools.encrypt(fileBytes);
            dataMap.put("device_key", keyTools.getPublicKey());
            SharedUtils.loading(loadingLayout, true);
            uploadProfileTask = imageApiService.uploadProfileTask("Bearer " + SharedPreference.getToken(this),dataMap);
            uploadProfileTask.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    if (response.isSuccessful()) {
                        getUser();
                    }
                    else{
                        SharedUtils.loading(loadingLayout, false);
                        SharedUtils.handleServerError(PersonalInfoActivity.this, response);
                    }

                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    SharedUtils.loading(loadingLayout, false);
                    SharedUtils.networkErrorDialogWithRetry(PersonalInfoActivity.this, retry);

                }
            });

        }
    }

    private void setPersonalInfo(final int type, final String value){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setPersonalInfo(type, value);
            }
        };

        Map<String, String> dataMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        try {
            if (type == EMAIL) {
                jsonObject.put("email", value);
            }else if (type == PHONE){
                jsonObject.put("phone", value);
            }else if (type == ADDRESS){
                jsonObject.put("address", value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("data json", jsonObject.toString());
        dataMap = keyTools.encrypt(jsonObject.toString());
        SharedUtils.loading(loadingLayout, true);

        setPersonalInfoTask = apiService.setPersonalInfoTask("Bearer " + SharedPreference.getToken(this), user.id, dataMap);
        setPersonalInfoTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                SharedUtils.loading(loadingLayout, false);
                if (response.isSuccessful()){
                    if (type == EMAIL){
                        userEmailText.setText(value);
                        user.email = value;
                    }else if (type == PHONE){
                        userPhoneText.setText(value);
                        user.phone = value;
                    } else if (type == ADDRESS){
                        //update
                        userAddressText.setText(value);
                        user.address = value;
                    }
                    SharedPreference.setUser(user, PersonalInfoActivity.this);
                }else{
                    SharedUtils.handleServerError(PersonalInfoActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                SharedUtils.loading(loadingLayout, false);
                SharedUtils.networkErrorDialogWithRetry(PersonalInfoActivity.this, retry);
            }
        });

    }


    private void getUser(){
        final DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getUser();
            }
        };
        SharedUtils.loading(loadingLayout, true);

        getUserTask = apiService.getCurrentUserTask("Bearer " + SharedPreference.getToken(this));
        getUserTask.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                SharedUtils.loading(loadingLayout, false);
                if (response.isSuccessful()){
                    String data = keyTools.decryptData(response.body().iv,response.body().data);
                    User user = new Gson().fromJson(data, User.class);
                    SharedPreference.setUser(user, PersonalInfoActivity.this);
                    Glide.with(PersonalInfoActivity.this)
                            .load(user.getIconUrl())
                            .apply(new RequestOptions().placeholder(R.drawable.user_placeholder))
                            .into(profileImage);
                } else{
                    SharedUtils.networkErrorDialogWithRetryUncancellable(PersonalInfoActivity.this, retry);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                SharedUtils.loading(loadingLayout, false);
                SharedUtils.networkErrorDialogWithRetryUncancellable(PersonalInfoActivity.this, retry);
            }
        });
    }


    private void createPermissionListeners() {
        MultiplePermissionsListener feedbackViewMultiplePermissionListener = new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()){
                    MaterialDialog dialog = new MaterialDialog.Builder(PersonalInfoActivity.this)
                            .title(R.string.alert_title_please_select)
                            .items(R.array.alert_choose_photo_arr)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    switch (which){
                                        case 0:
                                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                            photoPickerIntent.setType("image/*");
                                            startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                                            dialog.dismiss();
                                            break;
                                        case 1:
                                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            // Ensure that there's a camera activity to handle the intent
                                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                                // Create the File where the photo should go
                                                if (android.os.Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
                                                    Uri photoURI = null;
                                                    try {
                                                        photoURI = FileProvider.getUriForFile(PersonalInfoActivity.this,
                                                                BuildConfig.APPLICATION_ID + ".provider",
                                                                createImageFile(PersonalInfoActivity.this));
                                                    } catch (IOException ex) {
                                                        // Error occurred while creating the File
                                                    }
                                                    // Continue only if the File was successfully created
                                                    if (photoURI != null) {
                                                        Log.d("photoURI", photoURI.toString());
                                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                                        startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                                                    }
                                                }
                                                else{
                                                    File photoFile = null;
                                                    try {
                                                        photoFile = createImageFile(PersonalInfoActivity.this);
                                                    } catch (IOException ex) {
                                                        // Error occurred while creating the File
                                                    }
                                                    // Continue only if the File was successfully created
                                                    if (photoFile != null) {
                                                        Uri photoURI = Uri.fromFile(photoFile);
                                                        Log.d("photoURI", photoURI.toString());
                                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                                        startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                                                    }
                                                }
                                            }
                                            break;
                                    }
                                }
                            })
                            .titleColor(ContextCompat.getColor(PersonalInfoActivity.this,R.color.colorPrimary))
                            .negativeText(R.string.cancel)
                            .negativeColor(ContextCompat.getColor(PersonalInfoActivity.this,R.color.colorPrimary))
                            .show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                showPermissionRationale(token);
            }
        };


        galleryPermissionsListener =
                new CompositeMultiplePermissionsListener(feedbackViewMultiplePermissionListener,
                        SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with((ViewGroup)findViewById(R.id.root),
                                R.string.alert_permission_change_profile)
                                .withOpenSettingsButton(R.string.setting)
                                .build());
    }

    public void showPermissionRationale(final PermissionToken token) {
        new android.app.AlertDialog.Builder(this).setTitle(R.string.alert_permission_setting)
                .setMessage(R.string.alert_permission_change_profile)
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


    private File createImageFile(Context context) throws IOException {
        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
        String imageFileName = "profile";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
//            Glide.with(PersonalInfoActivity.this).load(SharedUtils.convertMediaUriToPath(Uri.parse(mCurrentPhotoPath), this)).dontAnimate().into(profileImage);
            uploadProfile(SharedUtils.convertMediaUriToPath(Uri.parse(mCurrentPhotoPath), this));
        }
        else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null){
//            Glide.with(PersonalInfoActivity.this).load(SharedUtils.convertMediaUriToPath(data.getData(), this)).dontAnimate().into(profileImage);
            uploadProfile(SharedUtils.convertMediaUriToPath(data.getData(), this));
        }
    }

    @Override
    public void onClick(View view) {

        if(SharedUtils.appIsHongKong()){

        }else {
            switch (view.getId()) {

                case R.id.edit_profile_btn:
                    Dexter.checkPermissions(galleryPermissionsListener, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
                    break;
                case R.id.user_tele_container:
                    showInputDialog(PHONE);
                    break;
                case R.id.user_address_container:
                    showInputDialog(ADDRESS);
                    break;
                case R.id.user_email_container:
                    showInputDialog(EMAIL);
                    break;
            }
        }
    }

    private void showInputDialog(final int type){
        String hint;
        String title;
        String message;
        if (type == PHONE){
            hint = user.phone;
            title = getString(R.string.dialog_title_change_phone);
            message = getString(R.string.dialog_message_change_phone);
        }else if (type == ADDRESS){
            hint = user.address;
            title = getString(R.string.dialog_title_change_address);
            message = getString(R.string.dialog_message_change_address);
        }
        else{
            hint = user.email;
            title = getString(R.string.dialog_title_change_email);
            message = getString(R.string.dialog_message_change_email);
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View view = getLayoutInflater().inflate(R.layout.input_dialog, null);
        dialogBuilder.setView(view);

        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        TextView titleText = (TextView)view.findViewById(R.id.tv_header);
        TextView messageText = (TextView)view.findViewById(R.id.dialog_msg);
        final EditText inputEditText = (EditText)view.findViewById(R.id.et_pw);


        inputEditText.setText(hint);
        inputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        titleText.setText(title);
        messageText.setText(message);

        Button cancel = (Button)view.findViewById(R.id.cancel);
        Button ok = (Button)view.findViewById(R.id.ok);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedUtils.hideKeyboard(PersonalInfoActivity.this, view);
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedUtils.hideKeyboard(PersonalInfoActivity.this, view);
                switch (type){
                    case PHONE:
                        setPersonalInfo(PHONE, inputEditText.getText().toString());
                        break;
                    case ADDRESS:
                        setPersonalInfo(ADDRESS, inputEditText.getText().toString());
                        break;
                    case EMAIL:
                        setPersonalInfo(EMAIL, inputEditText.getText().toString());
                        break;
                }

                dialog.dismiss();
            }
        });
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);

        toolbarTitle.setText(R.string.personal_info);
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
        if (getSupervisorTask != null){
            getSupervisorTask.cancel();
        }
        if (uploadProfileTask != null){
            uploadProfileTask.cancel();
        }
        if (setPersonalInfoTask != null){
            setPersonalInfoTask.cancel();
        }
        if (getUserTask != null){
            getUserTask.cancel();
        }
        super.onDestroy();
    }
}
