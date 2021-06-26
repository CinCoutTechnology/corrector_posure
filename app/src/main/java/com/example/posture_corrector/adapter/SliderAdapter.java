package com.example.posture_corrector.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.posture_corrector.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private final List<Bitmap> sliderItems;
    private final ViewPager2 viewPager2;
    private final Context context;

    public SliderAdapter(List<Bitmap> sliderItems, ViewPager2 viewPager2, Context context) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
        this.context = context;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.slide_item_container,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {

        holder.setImageView(sliderItems.get(position), context);
        if (position == sliderItems.size() - 2) {
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {

        private final RoundedImageView imageView;


        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlider);

        }
        void setImageView(Bitmap sliderItem, Context context) {

            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.fro);
            Glide.with(context)
                    .load(sliderItem)
                    .apply(requestOptions)
                    .into(imageView);
        }
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //noinspection CollectionAddedToSelf
            sliderItems.addAll(sliderItems);
            notifyDataSetChanged();
        }
    };
}
