package com.hop.pirate.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.HybridBinarizer;
import com.hop.pirate.Constants;
import com.hop.pirate.HopApplication;
import com.hop.pirate.PirateException;
import com.hop.pirate.R;
import com.hop.pirate.activity.RechargePacketsActivity;
import com.hop.pirate.callback.AlertDialogOkCallBack;
import com.hop.pirate.callback.SaveQRCodeCallBack;
import com.hop.pirate.fragment.TabPacketsMarketFragment;
import com.hop.pirate.fragment.TabWalletFragment;
import com.hop.pirate.model.bean.ExtendToken;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.interfaces.OnInputDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.InputInfo;
import com.kongzue.dialog.v3.InputDialog;
import com.kongzue.dialog.v3.MessageDialog;

import java.io.File;
import java.io.OutputStream;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import pub.devrel.easypermissions.EasyPermissions;


public final class Utils {

    private static final int RC_LOCAL_MEMORY_PERM = 123;
    private static final int RC_CAMERA_PERM = 124;
    public static final int RC_SELECT_FROM_GALLARY = 125;
    private static final String DEFAULT_ETH_ADDRESS = "0x0000000000000000000000000000000000000000";
    public static final double COIN_DECIMAL = 1e18;


    private static Context appContext = HopApplication.getAppContext();
    private static SharedPreferences sharedPref = appContext.getSharedPreferences("pirateaManager", Context.MODE_PRIVATE);

    public static String ConvertCoin(double coinV) {
        return String.format(Locale.CHINA, "%.4f ", coinV / COIN_DECIMAL);
    }


    public static String ConvertBandWidth(double packetsV) {
        if (packetsV > 1e12) {
            return String.format(Locale.CHINA, "%.2f T", packetsV / 1e12);
        } else if (packetsV > 1e9) {
            return String.format(Locale.CHINA, "%.2f G", packetsV / 1e9);
        } else if (packetsV > 1e6) {
            return String.format(Locale.CHINA, "%.2f M", packetsV / 1e6);
        } else if (packetsV > 1e3) {
            return String.format(Locale.CHINA, "%.2f K", packetsV / 1e3);
        } else {
            return String.format(Locale.CHINA, "%.2f B", packetsV);
        }
    }

    public static void saveData(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String key, String defaultVal) {
        return sharedPref.getString(key, defaultVal);
    }

    public static void saveBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(String key, Boolean defaultVal) {
        return sharedPref.getBoolean(key, defaultVal);
    }

    public static void clearSharedPref() {
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.clear();
        edit.commit();
    }

    public static void clearAllData(Context context) {
        TabPacketsMarketFragment.isSyncAllPools = false;
        RechargePacketsActivity.isInitSysSeting = false;
        TabWalletFragment.initsyncPoolsAndUserData = false;
        clearSharedPref();
    }


    public static void showOkAlert(AppCompatActivity context, int tittleID, int messageId, final AlertDialogOkCallBack callBack) {
        MessageDialog.show(context, context.getString(tittleID), context.getString(messageId), context.getString(R.string.sure)).setOnOkButtonClickListener(new OnDialogButtonClickListener() {

            @Override
            public boolean onClick(BaseDialog baseDialog, View v) {
                if (callBack != null) {
                    callBack.onClickOkButton("");
                }
                return false;
            }
        });
    }

    public static void showOkOrCancelAlert(AppCompatActivity context, int tittleID, int messageId, final AlertDialogOkCallBack callBack) {
        MessageDialog.show(context, context.getString(tittleID), context.getString(messageId), context.getString(R.string.sure), context.getString(R.string.cancel)).setOnOkButtonClickListener(new OnDialogButtonClickListener() {

            @Override
            public boolean onClick(BaseDialog baseDialog, View v) {
                if (callBack != null) {
                    callBack.onClickOkButton("");
                }
                return false;
            }
        }).setOnCancelButtonClickListener(new OnDialogButtonClickListener() {
            @Override
            public boolean onClick(BaseDialog baseDialog, View v) {
                if (callBack != null) {
                    callBack.onClickCancelButton();
                }
                return false;
            }
        });
    }


    private static Toast toast = null;

    public static void toastTips(String msg) {
        Context context = HopApplication.getAppContext();
        try {
            if (toast != null) {
                toast.setText(msg);
            } else {
                toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            }
            toast.show();
        } catch (Exception e) {
            Looper.prepare();
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }


    public static void toastException(Context context, Throwable err, int errCode) {

        if (err instanceof TimeoutException) {
            toastTips(context.getString(R.string.request_time_out));
        } else if (err instanceof PirateException) {
            toastTips(err.getMessage());
        } else {
            int requestErrorId = 0;
            switch (errCode) {
                case Constants.REQUEST_CREATE_ACCOUNT_ERROR:
                    requestErrorId = R.string.create_account_failed;
                    break;
                case Constants.REQUEST_IMPORT_ACCOUNT_ERROR:
                    requestErrorId = R.string.password_error;
                    break;
                case Constants.REQUEST_PACKETS_MARKET_ERROR:
                case Constants.REQUEST_OWN_MINE_POOL_ERROR:
                case Constants.REQUEST_MINE_MACHINE_ERROR:
                case Constants.REQUEST_WALLET_INFO_ERROR:
                case Constants.REQUEST_BUY_TESPER_TOKEN_ERROR:
                case Constants.REQUEST_SUPPORT_COINS_ERROR:
                    requestErrorId = R.string.get_data_failed;
                    break;
                case Constants.REQUEST_TRANSFER_ERROR:
                    requestErrorId = R.string.transfer_fail;
                    break;
                case Constants.REQUEST_FREE_ETH_ERROR:
                case Constants.REQUEST_FREE_HOP_ERROR:
                    requestErrorId = R.string.apply_fail;
                    break;
                case Constants.REQUEST_RECHARGE_ERROR:
                    requestErrorId = R.string.recharge_failed;
                    break;
                case Constants.REQUEST_OPEN_WALLET_ERROR:
                    requestErrorId = R.string.password_error;
                    break;
                default:
                    requestErrorId = R.string.get_data_failed;
                    break;
            }
            toastTips(context.getString(requestErrorId));
        }


    }


    public static void showPassWord(AppCompatActivity context, final AlertDialogOkCallBack callBack) {
        InputDialog.build(context)
                //.setButtonTextInfo(new TextInfo().setFontColor(Color.GREEN))
                .setTitle(R.string.tips).setMessage(R.string.enter_password)
                .setOkButton(R.string.sure, new OnInputDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v, String inputStr) {
                        callBack.onClickOkButton(inputStr);
                        return false;
                    }
                })
                .setCancelButton(R.string.cancel)
                .setHintText(context.getString(R.string.enter_ethereum_password))
                .setInputInfo(new InputInfo().setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                )
                .setCancelable(false)
                .show();

    }


    public static void copyToMemory(AppCompatActivity context, String src) {
        ClipboardManager clipboard = (ClipboardManager) appContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("pirate memory string", src);
        clipboard.setPrimaryClip(clip);
        MessageDialog.show(context, appContext.getString(R.string.copy_success), src);
    }

    public static Bitmap QRStr2Bitmap(String data) {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap bitmap = barcodeEncoder.encodeBitmap(data,
                    BarcodeFormat.QR_CODE,
                    600,
                    600);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveStringQRCode(ContentResolver cr, String data, String fileName, SaveQRCodeCallBack callBack) {

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(data,
                    BarcodeFormat.QR_CODE,
                    400,
                    400);
            saveImageToLocal(cr, bitmap, fileName, callBack);
        } catch (Exception e) {
            if (callBack != null) {
                callBack.save(e.getLocalizedMessage());
            }
        }
    }

    private static void saveImageToLocal(ContentResolver cr, Bitmap bitmap, String fileName, SaveQRCodeCallBack callBack) {

        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

            Uri path = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            OutputStream imageOut = cr.openOutputStream(path);
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
            } finally {
                imageOut.close();
            }

            if (callBack != null) {
                callBack.save(appContext.getString(R.string.save_to_album));
            }
        } catch (Exception e) {
            if (callBack != null) {
                callBack.save(e.getLocalizedMessage());
            }
        }
    }


    public static String parseQRCodeFile(Uri uri, ContentResolver cr) throws Exception {
        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

        return parseQRcodeFromBitmap(bitmap);
    }

    private static String parseQRcodeFromBitmap(Bitmap bitmap) throws Exception {
        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        BinaryBitmap bb = new BinaryBitmap(new HybridBinarizer(source));
        Map<DecodeHintType, Object> hints = new LinkedHashMap<DecodeHintType, Object>();
        hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
        Reader reader = new MultiFormatReader();
        Result r = reader.decode(bb, hints);

        return r.getText();
    }

    public static boolean checkStorage(Activity ctx) {
        if (!EasyPermissions.hasPermissions(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(
                    ctx,
                    ctx.getString(R.string.rationale_extra_write),
                    Utils.RC_LOCAL_MEMORY_PERM,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

    public static boolean checkCamera(Activity ctx) {
        if (!EasyPermissions.hasPermissions(ctx, Manifest.permission.CAMERA)) {
            EasyPermissions.requestPermissions(
                    ctx,
                    ctx.getString(R.string.camera),
                    Utils.RC_CAMERA_PERM,
                    Manifest.permission.CAMERA);
            return false;
        }
        return true;
    }

    public static String TokenNameFormat(Context context, int strId) {
        String text = context.getResources().getString(strId);
        return String.format(text, ExtendToken.CurSymbol);
    }

    public static String getBaseDir(Context context) {
        String baseDir = context.getFilesDir().getAbsolutePath();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory() + File.separator + context.getPackageName();
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            baseDir = path;
        }
        return baseDir;
    }

    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return "V" + packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "V0.0";
    }


    public static boolean isNetworkAvailable(AppCompatActivity activity) {
        Context context = activity.getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    public static boolean checkVPN() {
        List<String> networkList = new ArrayList<>();
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp()) {
                    networkList.add(networkInterface.getName());
                }
            }
        } catch (Exception ex) {
            return false;
        }
        return networkList.contains("tun0") || networkList.contains("ppp0");
    }

    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            lastClickTime = curClickTime;
            return true;
        }

        return false;
    }

    public static void openAppDownloadPage(Context context) {
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("http://d.7short.com/zy7s"));
        context.startActivity(it);

    }
}
