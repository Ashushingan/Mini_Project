package com.example.multicamstreamer;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ClientFeedAdapter extends RecyclerView.Adapter<ClientFeedAdapter.FeedViewHolder> {

    private final List<Bitmap> clientBitmaps;

    public ClientFeedAdapter(List<Bitmap> clientBitmaps) {
        this.clientBitmaps = clientBitmaps;
    }

    public void updateFeed(int index, Bitmap bitmap) {
        if (index < clientBitmaps.size()) {
            clientBitmaps.set(index, bitmap);
            notifyItemChanged(index);
        }
    }

    public int addClient(Bitmap initialBitmap) {
        clientBitmaps.add(initialBitmap);
        notifyItemInserted(clientBitmaps.size() - 1);
        return clientBitmaps.size() - 1;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_client_feed, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        holder.imageView.setImageBitmap(clientBitmaps.get(position));
    }

    @Override
    public int getItemCount() {
        return clientBitmaps.size();
    }

    static class FeedViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        FeedViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.clientImageView);
        }
    }
}
