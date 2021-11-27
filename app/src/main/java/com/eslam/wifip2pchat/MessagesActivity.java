package com.eslam.wifip2pchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.eslam.wifip2pchat.databinding.ActivityMessagesBinding;

public class MessagesActivity extends AppCompatActivity {

    private ActivityMessagesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_messages);
    }
}