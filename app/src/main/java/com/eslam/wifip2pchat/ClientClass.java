package com.eslam.wifip2pchat;

import static com.eslam.wifip2pchat.Constants.socket;

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
    MutableLiveData<String> message_Sent = new MutableLiveData<String>();
    MutableLiveData<String> message_Recieved = new MutableLiveData<String>();
    byte[] buffer;

    public ClientClass(String hostAddress)
    {
        hostAdd = hostAddress;
        socket = new Socket();
        buffer = new byte[1024];
    }



    void write(String msg)
    {
        try {
            outputStream.write(msg.getBytes());
            message_Sent.postValue(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(hostAdd,8888),1000);
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
                                    message_Recieved.postValue(new String(buffer,0,finalBytes));
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
