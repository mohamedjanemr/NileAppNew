package com.swadallail.nileapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.swadallail.nileapp.ImgView.DisplayImage;
import com.swadallail.nileapp.R;
import com.swadallail.nileapp.data.MessageResponse;
import com.swadallail.nileapp.modelviews.MessageViewModel;
import com.swadallail.nileapp.modelviews.SendData;
import com.swadallail.nileapp.orderpro.OrderProgress;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyHolder> {

    ArrayList<MessageViewModel> messages;
    Context context;
    int isMin;
    View itemView;


    public MessageAdapter(Context context, ArrayList<MessageViewModel> messages) {
        this.context = context;
        this.messages = messages;
    }

    public MessageAdapter(Context context, ArrayList<MessageViewModel> messages, int isMine) {
        this.context = context;
        this.messages = messages;
        this.isMin = isMine;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_other, parent, false);
                break;
            case 1:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_mine, parent, false);
                break;
        }
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        if (!messages.get(position).images.equals("")) {
            char first = messages.get(position).images.charAt(0);
            if (first == 'h') {
                Log.e("image22222", messages.get(position).images);
                Picasso.get().load(messages.get(position).images).into(holder.messageimg);
            } else {
                byte[] decodedString = Base64.decode(messages.get(position).images, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.messageimg.setImageBitmap(decodedByte);
            }
            holder.messageimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in = new Intent(context, DisplayImage.class);
                    in.putExtra("img", messages.get(position).images);
                    context.startActivity(in);
                }
            });

        } else {
            holder.messageimg.setVisibility(View.GONE);
        }
        holder.from.setText(messages.get(position).from);
        holder.content.setText(messages.get(position).content);
        /*if (!messages.get(position).imagemine.equals("")) {

        } else {
            holder.messageimg.setVisibility(View.GONE);
        }*/

    }


    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isMine;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView avatar, messageimg;
        TextView from, content;

        public MyHolder(@NonNull View v) {
            super(v);
            messageimg = v.findViewById(R.id.image);
            avatar = (ImageView) v.findViewById(R.id.imgMessageAvatar);
            from = (TextView) v.findViewById(R.id.txtMessageOwner);
            content = (TextView) v.findViewById(R.id.txtMessageContent);
        }
    }
}

/*class ViewHolder {
    ImageView avatar, messageimg;
    TextView from, content;

    ViewHolder(View v) {
        messageimg = v.findViewById(R.id.image);
        avatar = (ImageView) v.findViewById(R.id.imgMessageAvatar);
        from = (TextView) v.findViewById(R.id.txtMessageOwner);
        content = (TextView) v.findViewById(R.id.txtMessageContent);
    }*/

