package com.neatorobotics.android.slide.framework.transport;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;

public interface Transport {
    public void send(byte[] data) throws IOException;

    public void read(byte[] data) throws IOException;

    public DatagramPacket readDatagram() throws IOException;

    public void close();

    public int getVersion();

    public boolean isConnected();

    InputStream getInputStream();
}
