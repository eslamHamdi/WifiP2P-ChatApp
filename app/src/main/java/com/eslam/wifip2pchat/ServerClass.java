package com.eslam.wifip2pchat;

import static com.eslam.wifip2pchat.Constants.socket;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.nio.CharBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public  class ServerClass extends Thread {

    ServerSocket serverSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    BufferedReader dataIn;
    PrintWriter dataOut;
    MutableLiveData<String> message_Sent = new MutableLiveData<String>();
    MutableLiveData<String> message_Recieved = new MutableLiveData<String>();
    int recieved_bytes = 0;


    void write(String msg)
    {
        try {
            outputStream.write(msg.getBytes());
           // dataOut.write(msg);
            message_Sent.postValue(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (NullPointerException e)
        {
            Log.e(null, "write:  " + e.getLocalizedMessage());
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(8888);
            socket = serverSocket.accept();
            inputStream = socket.getInputStream();
           // dataIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //dataOut = new PrintWriter(socket.getOutputStream(),true);
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

                int sent_bytes;

                while (socket != null)
                {



                    try {
                        if (inputStream != null)
                        {
                            recieved_bytes = inputStream.read(buffer);

                        }



                        if (recieved_bytes > 0)
                        {
                            int finalBytes = recieved_bytes;
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
