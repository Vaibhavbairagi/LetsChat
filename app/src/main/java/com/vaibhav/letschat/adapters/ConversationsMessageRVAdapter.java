package com.vaibhav.letschat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.twilio.conversations.Conversation;
import com.twilio.conversations.Message;
import com.vaibhav.letschat.R;
import com.vaibhav.letschat.utils.ConversationsPreferences;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ConversationsMessageRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Message> messages;
    Context context;

    int lastPosition = -1;

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    public ConversationsMessageRVAdapter(ArrayList<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getAuthor().equals(ConversationsPreferences.getInstance().getCurrentUserId())) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }


    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sent_msg_text_list_item, parent, false);
            return new SentMessageItemViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.received_msg_text_list_item, parent, false);
            return new ReceivedMessageItemViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = position;
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageItemViewHolder) holder).bind(messages.get(position), position);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageItemViewHolder) holder).bind(messages.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull @NotNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    class SentMessageItemViewHolder extends RecyclerView.ViewHolder {

        TextView messageTV, timeTV, dateTV;
        CardView msgContainerCV;

        public SentMessageItemViewHolder(View itemView) {
            super(itemView);

            messageTV = itemView.findViewById(R.id.tv_smtli_msg);
            timeTV = itemView.findViewById(R.id.tv_smtli_msg_time);
            dateTV = itemView.findViewById(R.id.tv_smtli_date_msg);
            msgContainerCV = itemView.findViewById(R.id.cv_smtli_msg);
        }

        void bind(Message message, int cListPosition) {
            //todo: perform all required actions related to sent message
            //todo: handle different media types
            messageTV.setText(message.getMessageBody());

            Date msgDateTime = message.getDateCreatedAsDate();
            SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
            String time = tf.format(msgDateTime);
            timeTV.setText(time);

            long msgIndex = message.getMessageIndex();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            if (msgIndex == 0) {
                String date = df.format(msgDateTime);
                dateTV.setVisibility(View.VISIBLE);
                dateTV.setText(date);
            } else if (cListPosition > 0) {
                Date prevMsgDate = messages.get(cListPosition - 1).getDateCreatedAsDate();
                String date = df.format(msgDateTime);
                String prevDate = df.format(prevMsgDate);
                if (!date.equals(prevDate)) {
                    dateTV.setVisibility(View.VISIBLE);
                    dateTV.setText(date);
                } else {
                    dateTV.setVisibility(View.GONE);
                }
            } else {
                dateTV.setVisibility(View.GONE);
            }
        }
    }

    class ReceivedMessageItemViewHolder extends RecyclerView.ViewHolder {

        TextView messageTV, timeTV, dateTV;
        CardView msgContainerCV;

        public ReceivedMessageItemViewHolder(View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.tv_rmtli_msg);
            timeTV = itemView.findViewById(R.id.tv_rmtli_msg_time);
            dateTV = itemView.findViewById(R.id.tv_rmtli_date_msg);
            msgContainerCV = itemView.findViewById(R.id.cv_rmtli_msg);
        }

        void bind(Message message, int cListPosition) {
            //todo: perform all required actions related to received message
            //todo: handle different media types
            messageTV.setText(message.getMessageBody());

            Date msgDateTime = message.getDateCreatedAsDate();
            SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
            String time = tf.format(msgDateTime);
            timeTV.setText(time);

            long msgIndex = message.getMessageIndex();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            if (msgIndex == 0) {
                String date = df.format(msgDateTime);
                dateTV.setVisibility(View.VISIBLE);
                dateTV.setText(date);
            } else if (cListPosition > 0) {
                Date prevMsgDate = messages.get(cListPosition - 1).getDateCreatedAsDate();
                String date = df.format(msgDateTime);
                String prevDate = df.format(prevMsgDate);
                if (!date.equals(prevDate)) {
                    dateTV.setVisibility(View.VISIBLE);
                    dateTV.setText(date);
                } else {
                    dateTV.setVisibility(View.GONE);
                }
            } else {
                dateTV.setVisibility(View.GONE);
            }
        }
    }

}
