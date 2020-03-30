package com.bullb.ctf.Utils;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bullb.ctf.API.ErrorUtil;
import com.bullb.ctf.API.Response.ErrorResponse;
import com.bullb.ctf.BuildConfig;
import com.bullb.ctf.Login.VerificationActivity;
import com.bullb.ctf.Login.LoginActivity;
import com.bullb.ctf.R;
import com.bullb.ctf.ServerPreference;
import com.bullb.ctf.Setting.SettingActivity;
import com.bullb.ctf.TargetManagement.BreakDown.BreakDownActivity;
import com.bullb.ctf.Widget.CalendarView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Response;


/**
 * Created by oscar on 9/3/16.
 */
public class SharedUtils {
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getCampaignBannerHeight(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return (size.x * 160/375);
    }

    public static int getBannerHeight(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return (size.x * 1/2);
    }

    public static void hideKeyboard(Context context, View layout) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    public static String generateRandomPassword(Context context) {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public static boolean isCurrentYear(String year, boolean isNextMonth) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        if (isNextMonth){
            calendar.add(Calendar.MONTH,1);
        }
        int curYear = calendar.get(java.util.Calendar.YEAR);
        if (Integer.valueOf(year) == curYear) {
            return true;
        }
        return false;
    }




    public static boolean isCurrentYear(String fromDate, String dataDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = sdf.parse(fromDate);

        try {
            dataDate = dataDate.substring(0, 10);
        }catch (Exception e){
            throw new ParseException("dataDate format incorrect", 0);
        }
        java.util.Date date = sdf.parse(dataDate);

        if (startDate == null || date == null){
            throw new ParseException("null date", 0);
        }

        if (date.before(startDate)){
            return false;
        }else{
            return true;
        }
    }

    public static boolean isSameYear(String fromDate, String dataDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = sdf.parse(fromDate);

        try {
            dataDate = dataDate.substring(0, 10);
        }catch (Exception e){
            throw new ParseException("dataDate format incorrect", 0);
        }
        java.util.Date date = sdf.parse(dataDate);

        if (startDate == null || date == null){
            throw new ParseException("null date", 0);
        }

        if (dataDate.substring(0,4).equals(fromDate.substring(0,4))){
            return true;
        }else{
            return false;
        }
    }


    public static String addCommaToNum(double num){
        return addCommaToNum("",num, "");
    }

    public static String addCommaToNum(String prefix, double num){
        return addCommaToNum(prefix,num, "");
    }

    public static String addCommaToNum(double num, String append){
        return addCommaToNum("",num, append);
    }

    public static String addCommaToNum(String prefix, double num, String append){
        if (Double.isNaN(num)) {
            return "-";
        }
        if (Double.isInfinite(num)){
            return "-";
        }
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        String yourFormattedString = formatter.format(round(num,0));


        return prefix + yourFormattedString + append;
    }


    public static void loading(final View view, boolean visible){
        if (visible){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            Log.d("debug", "show");
            ViewPropertyAnimator.animate(view).alpha(1).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).setDuration(300).start();
        } else {
            Log.d("debug", "dismiss");
            view.setOnClickListener(null);
            ViewPropertyAnimator.animate(view).alpha(0).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).setDuration(300).start();
        }
    }

    public static boolean isCurrentMonth(int year, int month){
        java.util.Calendar cal = java.util.Calendar.getInstance();
        if (year == cal.get(java.util.Calendar.YEAR) && month == cal.get(java.util.Calendar.MONTH)){
            return true;
        }
        else{
            return false;
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double roundDown(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.DOWN);
        return bd.doubleValue();
    }


    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static double formatDouble(double num){
        if (Double.isNaN(num)){
            return 0;
        }
        else if (Double.isInfinite(num)){
            return 0;
        }
        else
            return num;
    }


    public static String convertMediaUriToPath(final Uri uri, final Context context) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvidere
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static void serverErrorDialogWithRetry(Context context, DialogInterface.OnClickListener retryOnClickListener) {
        errorDialogWithRetry(context, context.getResources().getString(R.string.error_server), retryOnClickListener);
    }


    public static void networkErrorDialogWithRetry(Context context, DialogInterface.OnClickListener retryOnClickListener) {
        errorDialogWithRetry(context, context.getResources().getString(R.string.error_network), retryOnClickListener);
    }

    public static void networkErrorDialogWithRetryUncancellable(Context context, DialogInterface.OnClickListener retryOnClickListener) {
        errorDialogWithRetryUncancellable(context, context.getResources().getString(R.string.error_network), retryOnClickListener);
    }


    public static void errorDialogWithRetryUncancellable(Context context, String title, DialogInterface.OnClickListener retryOnClickListener) {
        try {
            new android.support.v7.app.AlertDialog.Builder(context)
                    .setMessage(title)
                    .setCancelable(false)
                    .setPositiveButton(R.string.retry, retryOnClickListener)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            dialog.dismiss();
                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void errorDialogWithRetry(Context context, String title, DialogInterface.OnClickListener retryOnClickListener) {
        try {
            new android.support.v7.app.AlertDialog.Builder(context)
                    .setMessage(title)
                    .setPositiveButton(R.string.retry, retryOnClickListener)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            dialog.dismiss();
                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getActionBarSize(Context context) {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = context.obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    public static void popupDialog(Context context, int title, int message, int positiveText, int negativeText, DialogInterface.OnClickListener onClickListener) {
        new android.support.v7.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, onClickListener)
                .setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    public static void handleServerError(final Context context, Response<?> response) {
        try {
            ErrorResponse errorResponse = ErrorUtil.parseError(response);
            if (errorResponse.error.message != null) {
                if (response.code() == 401) {
                    if (((context instanceof LoginActivity) || (context instanceof VerificationActivity))){
                        Toast.makeText(context, context.getString(R.string.error_code) + ":" + String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        SharedPreference.logout(context);
                    }
                    return;
                }
                else if (response.code() == 403){
                    new AlertDialog.Builder(context)
                            .setMessage(R.string.error_user_block)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreference.logout(context);
                                }
                            })
                            .show();
                }
                else if (response.code() == 422){
                    if (((context instanceof LoginActivity) || (context instanceof VerificationActivity))){
                        switch (errorResponse.error.code){
                            case 1:
                                Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_invalid_user), Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_expired_user), Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_invalid_password), Toast.LENGTH_SHORT).show();
                                break;
                            case 4:
                                Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_invalid_verification), Toast.LENGTH_SHORT).show();
                                break;
//                            default:
//                                Toast.makeText(context, context.getString(R.string.error_code) + ":" + String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                            default:
                                Toast.makeText(context, errorResponse.error.message, Toast.LENGTH_SHORT).show();

                        }
                    } else  if (context instanceof SettingActivity){
                        Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_invalid_current_password), Toast.LENGTH_SHORT).show();
                    }  else {
                        if(SharedUtils.serverIsHongKong(context)) {
                            Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_unprocessable_entity), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context.getApplicationContext(), errorResponse.error.message, Toast.LENGTH_SHORT).show();
                        }
                    }
                    return;
                }else if(response.code() == 409){
                    Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_db_busy), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, context.getString(R.string.error_code) + ":" + String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            SharedUtils.serverError(context, response.code());
        }
    }

    public static void serverError(Context context, int errorCode) {
        Toast.makeText(context, context.getString(R.string.error_server) + ":"  + String.valueOf(errorCode), Toast.LENGTH_SHORT).show();
    }


    public static void definedError(Context context, int errorCode) {
        Toast.makeText(context, context.getString(R.string.error_code) + ":"  + String.valueOf(errorCode), Toast.LENGTH_SHORT).show();
    }


    public static String getMonthForAPI(CalendarView c) {
        Log.d("year: ", String.valueOf(c.getYear() + ", month: " + c.getMonth()));
        String year = String.valueOf(c.getYear());
        String month = String.valueOf(c.getMonth()+1);
        if (month.length() < 2) month = "0" + month;

        return year + "-" + month + "-01";
    }

    public static String getLastMonthForAPI(CalendarView c) {
        Log.d("year: ", String.valueOf(c.getYear() + ", month: " + c.getMonth()));
        int intYear = c.getYear();
        int intMonth = c.getMonth();
        if (intMonth ==0){
            intYear --;
            intMonth = 12;
        }
        String year = String.valueOf(intYear);
        String month = String.valueOf(intMonth);
        if (month.length() < 2) month = "0" + month;

        return year + "-" + month + "-01";
    }

    public static String getNextMonthText(Context context) {
        Calendar cal = Calendar.getInstance();
        int month = (cal.get(Calendar.MONTH) + 1)%12;
        String[] monthArray = context.getResources().getStringArray(R.array.month_arr);
        return  monthArray[month];

    }

    public static String getThisMonthText(Context context) {
        Calendar cal = Calendar.getInstance();
        int month = (cal.get(Calendar.MONTH))%12;
        String[] monthArray = context.getResources().getStringArray(R.array.month_arr);
        return  monthArray[month];

    }


    public static String getFirstDay(Calendar cal){
        String from_date = null;
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf(cal.get(Calendar.MONTH) +1);


        if (month.length() < 2) {
            month = "0" + month;
        }
        from_date = year + "-" + month + "-01";

        return from_date;

    }


    public static String getLastDay(Calendar cal){
        String from_date = null;
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf(cal.get(Calendar.MONTH) +1);
        String lastDay = String.valueOf(cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));


        if (month.length() < 2) {
            month = "0" + month;
        }
        from_date = year + "-" + month + "-" + lastDay;

        return from_date;

    }



    public static String getIconPath(Context context, String id){
        return ServerPreference.getServerUrl(context) + "storage/users/icons/" + id + ".png" ;
    }

    public static boolean appIsHongKong(){
        if (BuildConfig.FLAVOR_release_type.equals("HKProduction")||BuildConfig.FLAVOR_release_type.equals("HKStaging")){
            return true;
        }else{
            return false;
        }
    }

    public static boolean serverIsHongKong(Context context){
        if(ServerPreference.getServerVersion(context).equals(ServerPreference.SERVER_VERSION_HK)) {
            return true;
        }else{
            return false;
        }
    }

    public static String getStartDate(Calendar cal) {
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf(cal.get(Calendar.MONTH)+1);

        if (month.length() < 2) {
            month = "0" + month;
        }
        return year + "-" + month + "-01";
    }

}


