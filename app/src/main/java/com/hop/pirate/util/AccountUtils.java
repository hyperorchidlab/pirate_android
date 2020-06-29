package com.hop.pirate.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;

import com.google.zxing.integration.android.IntentIntegrator;
import com.hop.pirate.R;
import com.hop.pirate.activity.ScanActivity;
import com.hop.pirate.callback.AlertDialogOkCallBack;

public class AccountUtils {

    public static void importEthAddress(final Activity context,String ethAddress) {
        AlertDialogOkCallBack callBack = new AlertDialogOkCallBack() {
            @Override
            public void OkClicked(String parameter) {
                showImportQRChoice(context);
            }
        };

        if (!ethAddress.equals("")) {
            Utils.ShowOkOrCancelAlert(context, context.getString(R.string.sure_replace),
                    context.getString(R.string.save_ethereum_account), callBack);
            return;
        }
        showImportQRChoice(context);
    }

    public static void showImportQRChoice(final Activity context){
        final String[] listItems = {context.getString(R.string.scanning_qr_code),context.getString(R.string.read_album), context.getString(R.string.cancel)};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle(context.getString(R.string.select_import_mode));

        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (0 == i){
                    if (!Utils.checkCamera(context)){
                        return;
                    }

                    IntentIntegrator ii = new IntentIntegrator(context);
                    ii.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                    ii.setCaptureActivity(ScanActivity.class);
                    ii.setPrompt(context.getString(R.string.sacn_porton_account_qr));
                    ii.setCameraId(0);
                    ii.setBarcodeImageEnabled(true);
                    ii.initiateScan();

                }else if (1 == i){
                    if (!Utils.checkStorage(context)){
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
    public static void openAlbum(Activity activity) {
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


}
