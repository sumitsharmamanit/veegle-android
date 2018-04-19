package com.datingapp.util;


import android.view.View;

public class OnItemClickListener implements View.OnClickListener {
    private int position;
    private int parentId = 0;
    private OnItemClickCallback onItemClickCallback;
    private OnItemClickCallbackParentPosition onItemClickCallbackParentPosition;

    public OnItemClickListener(int position, OnItemClickCallback onItemClickCallback) {
        this.position = position;
        this.onItemClickCallback = onItemClickCallback;
    }

    public OnItemClickListener(int position, int parentId, OnItemClickCallbackParentPosition onItemClickCallbackParentPosition) {
        this.position = position;
        this.parentId = parentId;
        this.onItemClickCallbackParentPosition = onItemClickCallbackParentPosition;
    }

    @Override
    public void onClick(View view) {
        if (parentId == 0) {
            onItemClickCallback.onItemClicked(view, position);
        } else {
            onItemClickCallbackParentPosition.onItemClicked(view, position, parentId);
        }
    }

    public interface OnItemClickCallback {
        void onItemClicked(View view, int position);
    }

    public interface OnItemClickCallbackParentPosition {
        void onItemClicked(View view, int position, int parentId);
    }
}
