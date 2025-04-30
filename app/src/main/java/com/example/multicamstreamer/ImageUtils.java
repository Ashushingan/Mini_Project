package com.example.multicamstreamer;

import android.graphics.ImageFormat;
import android.media.Image;
import java.nio.ByteBuffer;

public class ImageUtils {

    public static ByteBuffer imageToByteBuffer(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8);
        for (Image.Plane plane : planes) {
            buffer.put(plane.getBuffer());
        }
        buffer.rewind();
        return buffer;
    }
}
