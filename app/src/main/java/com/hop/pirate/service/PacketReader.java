package com.hop.pirate.service;

import android.content.Context;
import android.util.Log;

import com.hop.pirate.HopApplication;
import com.hop.pirate.util.Utils;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import androidLib.AndroidLib;

class PacketReader implements Runnable {
    private FileInputStream mReader;
    private static final int MAX_PACKET_SIZE = Short.MAX_VALUE;
    private ByteBuffer readerBuf = ByteBuffer.allocate(MAX_PACKET_SIZE);
    private Context mContext;

    PacketReader(Context context, FileInputStream fi) {
        mContext = context;
        mReader = fi;
    }

    @Override
    public void run() {
        try {
            while (Utils.getApplication(mContext).isRunning()) {
                int length = mReader.read(readerBuf.array());
                if (length == 0) {
                    Thread.sleep(HopService.IDLE_INTERVAL_MS);
                    continue;
                }
                readerBuf.limit(length);
                byte[] dst = Arrays.copyOf(readerBuf.array(), length);

                AndroidLib.inputPacket(dst);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (Utils.getApplication(mContext).isRunning()) {
                Utils.getApplication(mContext).setRunning(false);
                HopService.stop();
            }
        }


        Log.w("HopService", "------>Packet reading thread exit......");
    }
}