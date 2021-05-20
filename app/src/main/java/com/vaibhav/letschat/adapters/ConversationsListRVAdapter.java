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
import androidx.recyclerview.widget.RecyclerView;

import com.twilio.conversations.CallbackListener;
import com.twilio.conversations.Conversation;
import com.twilio.conversations.Message;
import com.vaibhav.letschat.R;
import com.vaibhav.letschat.listeners.OnConversationClickedListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ConversationsListRVAdapter extends RecyclerView.Adapter<ConversationsListRVAdapter.ConversationsItemViewHolder> {

    ArrayList<Conversation> conversations;
    Context context;
    int lastPosition = -1;
    OnConversationClickedListener onConversationClickedListener;

    public ConversationsListRVAdapter(ArrayList<Conversation> conversations, Context context, OnConversationClickedListener onConversationClickedListener) {
        this.conversations = conversations;
        this.context = context;
        this.onConversationClickedListener = onConversationClickedListener;
    }

    @NonNull
    @NotNull
    @Override
    public ConversationsItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ConversationsItemViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ConversationsListRVAdapter.ConversationsItemViewHolder holder, int position) {

        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = position;

        Conversation c = conversations.get(position);
        //TODO: display required data
        holder.nameTV.setText(c.getFriendlyName());
        if (c.getLastMessageIndex() != null) {
            holder.lastMsgTimeTV.setVisibility(View.VISIBLE);
            c.getMessageByIndex(c.getLastMessageIndex(), new CallbackListener<Message>() {
                @Override
                public void onSuccess(Message result) {
                    holder.lastMsgTV.setText(result.getMessageBody());
                }
            });
            Date lmsgDate = c.getLastMessageDate();
            SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
            String lastMsgTime = sdf.format(lmsgDate);
            holder.lastMsgTimeTV.setText(lastMsgTime);
            c.getUnreadMessagesCount(new CallbackListener<Long>() {
                @Override
                public void onSuccess(Long result) {
                    if (result != null && result > 0) {
                        holder.unreadMsgCountTV.setVisibility(View.VISIBLE);
                        holder.unreadMsgCountTV.setText(String.valueOf(result));
                    } else {
                        holder.unreadMsgCountTV.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            holder.unreadMsgCountTV.setVisibility(View.GONE);
            holder.lastMsgTimeTV.setVisibility(View.GONE);
        }
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConversationClickedListener.onConversationClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ConversationsItemViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    static class ConversationsItemViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout parent;
        TextView nameTV, lastMsgTV, lastMsgTimeTV, unreadMsgCountTV;
        ImageView profileIV;

        public ConversationsItemViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.rl_conv_li_parent);
            nameTV = itemView.findViewById(R.id.tv_conv_name);
            lastMsgTV = itemView.findViewById(R.id.tv_conv_last_msg);
            profileIV = itemView.findViewById(R.id.iv_conv_img);
            lastMsgTimeTV = itemView.findViewById(R.id.tv_last_msg_time);
            unreadMsgCountTV = itemView.findViewById(R.id.tv_conv_unread_msg_count);
        }
    }
}
