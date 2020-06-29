package com.hop.pirate.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.HybridBinarizer;
import com.hop.pirate.Constants;
import com.hop.pirate.PError;
import com.hop.pirate.model.bean.ExtendToken;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.kongzue.dialog.v2.MessageDialog;
import com.hop.pirate.HopApplication;
import com.hop.pirate.R;
import com.hop.pirate.callback.AlertDialogOkCallBack;
import com.hop.pirate.callback.SaveQRCodeCallBack;

import java.io.File;
import java.io.OutputStream;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import pub.devrel.easypermissions.EasyPermissions;


public final class Utils {

    public static final int RC_LOCAL_MEMORY_PERM = 123;
    public static final int RC_CAMERA_PERM = 124;
    public static final int RC_SELECT_FROM_GALLARY = 125;
    public static final int RC_VPN_RIGHT = 126;
    public static final String DEFAULT_ETH_ADDRESS = "0x0000000000000000000000000000000000000000";
    public static final String KEY_FOR_IMTOKEN_PASSWORD = "key_for_ethereum_password";
    public static final double CoinDecimal = 1e18;

    public static final String EthScanBaseUrl = "https://cn.etherscan.com/tx/";

    static Context appContext = HopApplication.getAppContext();
    static SharedPreferences sharedPref = appContext.getSharedPreferences("pirateaManager", Context.MODE_PRIVATE);

    public static String ConvertCoin(double coinV) {
        return String.format(Locale.CHINA, "%.4f ", coinV / CoinDecimal);
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


    public static void ShowOkOrCancelAlert(Context context, String tittle, String message, final AlertDialogOkCallBack callBack) {

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(tittle);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.sure),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callBack.OkClicked("");
                    }
                });

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    static Toast toast = null;

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
        int requestErrorId = 0;
        if (err instanceof TimeoutException) {
            requestErrorId = R.string.request_time_out;
        } else if (err instanceof PError) {
            toastTips(err.getMessage());
        } else {
            switch (errCode) {
                case Constants.REQUEST_CREATE_ACCOUNT_ERROR:
                    requestErrorId = R.string.create_account_failed;
                    break;
                case Constants.REQUEST_IMPORT_ACCOUNT_ERROR:
                    requestErrorId = R.string.import_account_failed;
                    break;
                case Constants.REQUEST_PACKETS_MARKET_ERROR:
                    requestErrorId = R.string.import_account_failed;
                    break;
                case Constants.REQUEST_OWNE_MINE_POOL_ERROR:
                case Constants.REQUEST_MINE_MACHINE_ERROR:
                case Constants.REQUEST_WALLET_INFO_ERROR:
                case Constants.REQUEST_BYTESPERTOKEN_ERROR:
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
                    requestErrorId = R.string.password_eror;
                    break;
                default :
                    requestErrorId = R.string.get_data_failed;
                    break;
            }
        }

        toastTips(context.getString(requestErrorId));
    }

    public static void showDoublePassWord(final Context context, final AlertDialogOkCallBack callBack) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.create_account, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);

        final EditText passwordET1 = (EditText) promptView.findViewById(R.id.accPassword1);
        final EditText passwordET2 = (EditText) promptView.findViewById(R.id.accPassword2);
        final String[] passwords = {""};

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(context.getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String pwd1 = passwordET1.getText().toString();
                        String pwd2 = passwordET2.getText().toString();
                        if (!pwd1.equals(pwd2)) {
                            Utils.toastTips(context.getString(R.string.twice_password_not_same));
                            return;
                        }
                        dialog.dismiss();
                        callBack.OkClicked(pwd1);
                    }
                }).setNegativeButton(context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public static void showPassWord(Context context, final AlertDialogOkCallBack callBack) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View passwordView = layoutInflater.inflate(R.layout.layout_ethereum_password, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(passwordView);
        final EditText passwordET = (EditText) passwordView.findViewById(R.id.passwordToUnlock);


        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(context.getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        callBack.OkClicked(passwordET.getText().toString());
                    }
                }).setNegativeButton(context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        alertDialogBuilder.create().show();
    }

    public static void showpiratePassWord(Context context, final AlertDialogOkCallBack callBack) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View passwordView = layoutInflater.inflate(R.layout.layout_pirate_password, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(passwordView);
        final EditText passwordET = (EditText) passwordView.findViewById(R.id.passwordToUnlock);
        final CheckBox rememberPasswordCb = (CheckBox) passwordView.findViewById(R.id.rememberPasswordCb);

        String password = Utils.getString(KEY_FOR_IMTOKEN_PASSWORD, "");
        passwordET.setText(password);
        passwordET.setSelection(password.length());
        if (!TextUtils.isEmpty(password)) {
            rememberPasswordCb.setChecked(true);
        }
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(context.getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (rememberPasswordCb.isChecked()) {
                            Utils.saveData(KEY_FOR_IMTOKEN_PASSWORD, passwordET.getText().toString());
                        } else {
                            Utils.saveData(KEY_FOR_IMTOKEN_PASSWORD, "");
                        }

                        callBack.OkClicked(passwordET.getText().toString());
                    }
                }).setNegativeButton(context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        alertDialogBuilder.create().show();
    }

    public static void CopyToMemory(Context context, String src) {
        ClipboardManager clipboard = (ClipboardManager) appContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("pirate memory string", src);
        clipboard.setPrimaryClip(clip);
        MessageDialog.show(context, "", appContext.getString(R.string.copy_success), appContext.getString(R.string.sure), null);
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

    public static void SaveStringQRCode(ContentResolver cr, String data, String fileName, SaveQRCodeCallBack callBack) {

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

    public static void saveImageToLocal(ContentResolver cr, Bitmap bitmap, String fileName, SaveQRCodeCallBack callBack) {

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


    public static String ParseQRCodeFile(Uri uri, ContentResolver cr) throws Exception {
        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

        return ParseQRcodeFromBitmap(bitmap);
    }

    public static String ParseQRcodeFromBitmap(Bitmap bitmap) throws Exception {
        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        BinaryBitmap bb = new BinaryBitmap(new HybridBinarizer(source));
        Reader reader = new MultiFormatReader();
        Result r = reader.decode(bb);

        return r.getText();
    }

    public static boolean validEthAddress(String ethAddr) {
        return !ethAddr.equals("") && !ethAddr.equals(DEFAULT_ETH_ADDRESS);
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

    public static String getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return "V"+packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "V0.0";
    }
}
