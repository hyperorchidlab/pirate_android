package com.hop.pirate.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;

import com.google.zxing.integration.android.IntentIntegrator;
import com.hop.pirate.R;
import com.hop.pirate.activity.ScanActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class AccountUtils {


    public static void showImportQRChoice(final Activity context) {
        final String[] listItems = {context.getString(R.string.scanning_qr_code), context.getString(R.string.read_album), context.getString(R.string.cancel)};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle(context.getString(R.string.select_import_mode));

        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (0 == i) {
                    if (!Utils.checkCamera(context)) {
                        return;
                    }

                    IntentIntegrator ii = new IntentIntegrator(context);
                    ii.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                    ii.setCaptureActivity(ScanActivity.class);
                    ii.setPrompt(context.getString(R.string.scan_pirate_account_qr));
                    ii.setCameraId(0);
                    ii.setBarcodeImageEnabled(true);
                    ii.initiateScan();

                } else if (1 == i) {
                    if (!Utils.checkStorage(context)) {
                        return;
                    }

                    openAlbum(context);
                }
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private static void openAlbum(Activity activity) {
        Intent albumIntent = new Intent();
        albumIntent.addCategory(Intent.CATEGORY_OPENABLE);
        albumIntent.setType("image/*");
        if (Build.VERSION.SDK_INT < 19) {
            albumIntent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            albumIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }

        activity.startActivityForResult(albumIntent, Utils.RC_SELECT_FROM_GALLARY);
    }

    public static String loadWallet(Context context) {
        File file = new File(Utils.getBaseDir(context) + "/wallet.json");
        if (!file.exists()) {
            return "";
        }
        FileInputStream fileInputStream = null;

        byte[] bytes = new byte[1024];
        try {
            fileInputStream = new FileInputStream(file);
            StringBuffer stringBuffer = new StringBuffer();
            int read = -1;
            while ((read = fileInputStream.read(bytes)) != -1) {
                stringBuffer.append(new String(bytes, 0, read));
            }
            fileInputStream.read();
            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                if(fileInputStream!= null){
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
