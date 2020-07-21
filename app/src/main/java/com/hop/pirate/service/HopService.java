package com.hop.pirate.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.VpnService;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.hop.pirate.R;
import com.hop.pirate.activity.MainActivity;
import com.hop.pirate.event.EventStopHopService;
import com.hop.pirate.event.EventVPNClosed;
import com.hop.pirate.event.EventVPNOpen;
import com.hop.pirate.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

import androidLib.AndroidLib;

public class HopService extends VpnService implements androidLib.VpnDelegate, Handler.Callback {
    public static boolean IsRunning = false;

    public static final long IDLE_INTERVAL_MS = TimeUnit.MILLISECONDS.toMillis(100);
    public static final String TAG = "HopService";
    private static final String LOCAL_IP = "10.8.0.2";

    private ParcelFileDescriptor mInterface;
    private PendingIntent mConfigureIntent;
    private FileOutputStream mVpnOutputStream;
    private Handler mHandler;

    @Override
    public void onCreate() {
        mHandler = new Handler(this);
        mConfigureIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startNotification();
        new Thread(new Runnable() {
            @Override
            public void run() {
                establishVPN();
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId) {
        NotificationChannel chan = new NotificationChannel(channelId,
                "com.hop.pirate.service", NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(chan);
    }

    void startNotification() {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        builder.setContentIntent(mConfigureIntent)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.notification_icon))
                .setContentTitle(getString(R.string.notification_title))
                .setSmallIcon(R.drawable.notification_icon)
                .setContentText(getString(R.string.notification_title))
                .setWhen(System.currentTimeMillis());

        String channelId = "com.hop.pirate.hop.service.channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelId);
            builder.setChannelId(channelId);
        }

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;
        startForeground(110, notification);
    }

    public void disconnectVPN() {
        try {
            mVpnOutputStream.close();
            mVpnOutputStream = null;

            if (mInterface != null) {
                mInterface.close();
                mInterface = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe
    public void stopHopService(EventStopHopService eventStopHopService){
        AndroidLib.stopVpn();
        stopSelf();
    }

    public void establishVPN() {
        try {
            VpnService.Builder builder = new Builder();
            builder.addAddress(LOCAL_IP, 32);
            builder.addRoute("0.0.0.0", 0);
            builder.setConfigureIntent(mConfigureIntent);
            mInterface = builder.establish();

            FileInputStream inputStream = new FileInputStream(mInterface.getFileDescriptor());
            mVpnOutputStream = new FileOutputStream(mInterface.getFileDescriptor());
            AndroidLib.startVPN(LOCAL_IP + ":41080", SysConf.CurPoolAddress, SysConf.CurMinerID, this);
            IsRunning = true;
            new Thread(new PacketReader(inputStream)).start();

            EventBus.getDefault().post(new EventVPNOpen());
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(0);
            vpnClosed();
        }
    }

    @Override
    public boolean byPass(int fd) {
        Log.i(TAG, "bypass: fd=" + fd);
        return this.protect(fd);
    }

    @Override
    public void log(String str) {
        Log.i(TAG, str);
    }

    @Override
    public void vpnClosed() {
        IsRunning = false;
        disconnectVPN();
        EventBus.getDefault().post(new EventVPNClosed());
        stopSelf();
    }

    @Override
    public long write(byte[] b) throws Exception {
        mVpnOutputStream.write(b);
        return b.length;
    }


    public static void Stop() {
        Log.w(TAG, "stop service in android");
        AndroidLib.stopVpn();
    }

    @Override
    public boolean handleMessage(Message msg) {
        Utils.toastTips(getString(R.string.init_service_fail));
        return false;
    }
}