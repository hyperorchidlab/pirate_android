package com.hop.pirate.service;

import android.app.IntentService;
import android.content.Intent;

import androidLib.AndroidLib;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MicroChainService extends IntentService{

    public MicroChainService() {
        super("MicroChainService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AndroidLib.startProtocol();
    }

}
