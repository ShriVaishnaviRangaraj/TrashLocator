package com.trashlocator.ui.picsview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.trashlocator.R;

public class UploadedPicsAdapter  extends FirebaseRecyclerAdapter<UploadedPicsViewModel,UploadedPicsAdapter.UploadedPicsViewHolder> {

    public UploadedPicsAdapter(FirebaseRecyclerOptions<UploadedPicsViewModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UploadedPicsAdapter.UploadedPicsViewHolder holder, int position, @NonNull UploadedPicsViewModel model) {

        if (!((Activity) holder.itemView.getContext()).isFinishing()){
            Glide.with(holder.itemView.getContext()).load(model.getImagelink()).placeholder(R.mipmap.placeholder_foreground).into(holder.uploadedImage);
        }
    }

    @NonNull
    @Override
    public UploadedPicsAdapter.UploadedPicsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.uploadedpics_items_layout,parent,false);
        return new UploadedPicsViewHolder(view);
    }

    class UploadedPicsViewHolder extends RecyclerView.ViewHolder{

        ImageView uploadedImage;
        public UploadedPicsViewHolder(@NonNull View itemView) {
            super(itemView);

            //Image
            uploadedImage=itemView.findViewById(R.id.uploadedPics_imageView);
        }
    }
}
