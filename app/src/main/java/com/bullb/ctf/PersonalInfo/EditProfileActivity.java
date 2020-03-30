package com.bullb.ctf.PersonalInfo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bullb.ctf.BuildConfig;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Utils.SharedUtils;
import com.bullb.ctf.Utils.WidgetUtils;
import com.bullb.ctf.Widget.ProfileImageView;
import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private final int CAMERA_REQUEST = 111;
    private final int GALLERY_REQUEST = 112;
    private ProfileImageView profileImage;
    private MultiplePermissionsListener galleryPermissionsListener;
    private String mCurrentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        createPermissionListeners();
        Dexter.continuePendingRequestsIfPossible(galleryPermissionsListener);
        setToolbar();
        initUi();


    }

    private void initUi(){
        if (Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        profileImage = (ProfileImageView)findViewById(R.id.profile_imageview);

        profileImage.setImage(ServerPreference.getServerUrl(this) +"profile.jpg");

        profileImage.setOnClickListener(this);
    }

    private void createPermissionListeners() {
        MultiplePermissionsListener feedbackViewMultiplePermissionListener = new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()){
                    MaterialDialog dialog = new MaterialDialog.Builder(EditProfileActivity.this)
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
                                                File photoFile = null;
                                                try {
                                                    photoFile = createImageFile(EditProfileActivity.this);
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
                                            break;
                                    }
                                }
                            })
                            .titleColor(ContextCompat.getColor(EditProfileActivity.this,R.color.colorPrimary))
                            .negativeText(R.string.cancel)
                            .negativeColor(ContextCompat.getColor(EditProfileActivity.this,R.color.colorPrimary))
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
        new AlertDialog.Builder(this).setTitle(R.string.alert_permission_setting)
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
            profileImage.setImage(SharedUtils.convertMediaUriToPath(Uri.parse(mCurrentPhotoPath), this));
//            uploadProfile(SharedUtils.convertMediaUriToPath(Uri.parse(mCurrentPhotoPath), getActivity()));
        }
        else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null){
            profileImage.setImage(SharedUtils.convertMediaUriToPath(data.getData(), this));

//            uploadProfile(SharedUtils.convertMediaUriToPath(data.getData(), getActivity()));
        }
    }



    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        RelativeLayout notificationBtn = (RelativeLayout)findViewById(R.id.notification_btn);
        ImageView backBtn = (ImageView)findViewById(R.id.back_btn);
        TextView toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        ImageView doneBtn = (ImageView)findViewById(R.id.done_btn);

        toolbarTitle.setText(R.string.personal_info);
        toolbar.setBackground(ContextCompat.getDrawable(this, R.drawable.toolbar_bg));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notificationBtn.setOnClickListener(new WidgetUtils.NotificationButtonListener(this));
        backBtn.setOnClickListener(new WidgetUtils.BackButtonListener(this));
        backBtn.setVisibility(View.VISIBLE);
        notificationBtn.setVisibility(View.GONE);
        doneBtn.setVisibility(View.VISIBLE);
        doneBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.done_btn:
                finish();
                break;
            case R.id.profile_imageview:
                Dexter.checkPermissions(galleryPermissionsListener, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
                break;
        }
    }
}
