package com.eslam.wifip2pchat;

import static com.eslam.wifip2pchat.Constants.socket;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.MutableLiveData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientClass extends Thread{

    String hostAdd;
    private InputStream inputStream;
    private OutputStream outputStream;
    MutableLiveData<String> message_Sent = new MutableLiveData<String>();
    MutableLiveData<String> message_Recieved = new MutableLiveData<String>();
    byte[] buffer;
    BufferedReader dataIn;
    PrintWriter dataOut;

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
            //dataOut.write(msg);
            message_Sent.postValue(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(hostAdd,8888),1000);
            inputStream = socket.getInputStream();
           outputStream = socket.getOutputStream();
            //dataIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //dataOut = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                int bytes = 0;

                while (socket != null)
                {
                    try {
                        if (inputStream != null) {
                            bytes = inputStream.read(buffer);
                            //dataIn.read(CharBuffer.wrap(message_Recieved.getValue()));
                        }
                        if (bytes > 0)
                        {
                            int finalBytes = bytes;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    message_Recieved.postValue(new String(buffer,0,finalBytes));
                                }
                            });
                        } } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
