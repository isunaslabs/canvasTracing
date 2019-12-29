package com.isunaslabs.imageeditor.model;

import android.graphics.Bitmap;

public class ImageToTrace {
    private Bitmap imageBitmap;

    public ImageToTrace(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }
}
