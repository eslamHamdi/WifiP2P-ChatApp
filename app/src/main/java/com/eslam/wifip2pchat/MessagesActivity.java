package com.eslam.wifip2pchat;


import static com.eslam.wifip2pchat.Constants.socket;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import com.eslam.wifip2pchat.databinding.ActivityMessagesBinding;
import com.eslam.wifip2pchat.model.MessageModel;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MessagesActivity extends AppCompatActivity {

    private ActivityMessagesBinding binding;
    private Boolean status;
    private String hostAddress;
    private ServerClass serverClass;
    private ClientClass clientClass;
    private MessagesAdapter adapter;
    private Vibrator vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_messages);
       status = this.getIntent().getBooleanExtra("status",false);
        vibe = (Vibrator) MessagesActivity.this.getSystemService(Context.VIBRATOR_SERVICE);

       if(status)
       {

           serverClass = new ServerClass();
           serverClass.start();
           observeSentMessages_ServerSide();
           observeRecievedMessages_ServerSide();
           Log.e(null, "onCreate: messages "+status );
       }else
       {
           Log.e(null, "onCreate: messages "+status );
           hostAddress = this.getIntent().getStringExtra("hostAddress");
           Log.e(null, "onCreate: messages "+hostAddress );
           clientClass = new ClientClass(hostAddress);
           clientClass.start();
           observeSentMessages_ClientSide();
           observeRecievedMessages_ClientSide();
       }



       adapter = new MessagesAdapter();
       binding.recycler.setAdapter(adapter);







        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                String msg = Objects.requireNonNull(binding.messageField.getText()).toString();
                binding.messageField.setText("");
                vibe.vibrate(80);

                executor.execute(new Runnable() {
                    @Override
                    public void run() {

                        if (status)
                        {

                            serverClass.write(msg);

                        }else{

                            clientClass.write(msg);
                        }

                    }
                });
            }
        });
    }

    private void observeRecievedMessages_ClientSide() {
        clientClass.message_Recieved.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s != null)
                {
                    MessageModel model = new MessageModel(s,"received");
                    adapter.buildList(model);
                    vibe.vibrate(100);
                }


            }
        });
    }

    private void observeSentMessages_ClientSide() {
        clientClass.message_Sent.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s != null)
                { MessageModel model = new MessageModel(s,"sent");
                    adapter.buildList(model);
                    vibe.vibrate(100);
                }

            }
        });
    }

    private void observeRecievedMessages_ServerSide() {

        serverClass.message_Recieved.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s != null)
                {
                    MessageModel model = new MessageModel(s,"received");
                    adapter.buildList(model);
                    vibe.vibrate(100);
                }

            }
        });
    }

    private void observeSentMessages_ServerSide() {

        serverClass.message_Sent.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s != null)
                {
                    MessageModel model = new MessageModel(s,"sent");
                    adapter.buildList(model);
                    vibe.vibrate(100);
                }


            }
        });
    }


    void socketRelease()
    {

        try {
            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();
            if (status)
            {
                serverClass.interrupt();
            }else
            {
                clientClass.interrupt();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onDestroy() {
        socketRelease();
        super.onDestroy();
    }
}