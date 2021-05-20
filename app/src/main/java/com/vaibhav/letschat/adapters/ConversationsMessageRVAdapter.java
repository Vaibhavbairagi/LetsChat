package com.vaibhav.letschat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.twilio.conversations.Conversation;
import com.vaibhav.letschat.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ConversationsMessageRVAdapter extends RecyclerView.Adapter<ConversationsMessageRVAdapter.MessagesItemViewHolder> {

    ArrayList<Conversation> conversations;
    Context context;

    public ConversationsMessageRVAdapter(ArrayList<Conversation> conversations, Context context) {
        this.conversations = conversations;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MessagesItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ConversationsMessageRVAdapter.MessagesItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class MessagesItemViewHolder extends RecyclerView.ViewHolder {

        public MessagesItemViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
