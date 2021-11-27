package com.eslam.wifip2pchat;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientClass extends Thread{

    String hostAdd;
    private InputStream inputStream;
    private OutputStream outputStream;
    Socket socket;
    MutableLiveData<String> message = new MutableLiveData<String>();

    public ClientClass(InetAddress hostAddress)
    {
        hostAdd = hostAddress.getHostAddress();
        socket = new Socket();
    }

    void socketRelease()
    {

    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(hostAdd,8888),500);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                int bytes;

                while (socket != null)
                {
                    try {
                        bytes = inputStream.read(buffer);
                        if (bytes > 0)
                        {
                            int finalBytes = bytes;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                  message.postValue(new String(buffer,0,finalBytes));
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
