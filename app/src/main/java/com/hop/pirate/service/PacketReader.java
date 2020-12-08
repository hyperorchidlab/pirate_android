package com.hop.pirate.service;

import android.util.Log;

import com.hop.pirate.HopApplication;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import androidLib.AndroidLib;

class PacketReader implements Runnable {
    private final FileInputStream mReader;
    private static final int MAX_PACKET_SIZE = Short.MAX_VALUE;
    private final ByteBuffer readerBuf = ByteBuffer.allocate(MAX_PACKET_SIZE);
    HopApplication app = HopApplication.getApplication();

    PacketReader( FileInputStream fi) {
        mReader = fi;
    }

    @Override
    public void run() {
        try {
            while (app.isRunning()) {
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
            if (HopApplication.getApplication().isRunning()) {
                HopApplication.getApplication().setRunning(false);
                HopService.stop();
            }
        }


        Log.w("HopService", "------>Packet reading thread exit......");
    }
}