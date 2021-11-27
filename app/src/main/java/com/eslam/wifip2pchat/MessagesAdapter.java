package com.eslam.wifip2pchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private ArrayList<MessageModel> modelList;
    private View view;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==R.layout.sent_message)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_message,parent,false);

        }else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recieved_message,parent,false);
        }

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MessageModel messageModel = modelList.get(position);
        holder.bind(messageModel);

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

        }

        void bind(MessageModel message)
        {
            if (message.getType().equals("sent"))
            {
                textView = itemView.findViewById(R.id.sent_message);
                textView.setText(message.getBody());
                textView.setHint("Sent");
            }else
            {
                textView = itemView.findViewById(R.id.recieved_message);
                textView.setText(message.getBody());
                textView.setHint("Received");
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        //super.getItemViewType(position);

        if (modelList.get(position).getType().equals("sent"))
        {
            return  R.layout.sent_message;
        }else{
            return R.layout.recieved_message;
        }


    }
}
