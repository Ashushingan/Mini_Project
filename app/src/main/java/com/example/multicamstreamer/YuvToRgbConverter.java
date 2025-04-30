package com.example.multicamstreamer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

import java.nio.ByteBuffer;

public class YuvToRgbConverter {
    private RenderScript rs;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    private Allocation inAllocation;
    private Allocation outAllocation;

    public YuvToRgbConverter(Context context) {
        rs = RenderScript.create(context);
    }

    public YuvToRgbConverter() {
        // Empty constructor (for now)
    }

    public void yuvToRgb(Image image, Bitmap output) {
        if (rs == null) {
            throw new IllegalStateException("RenderScript is not initialized. Use the constructor with Context.");
        }
        if (yuvToRgbIntrinsic == null) {
            yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
        }

        if (inAllocation == null) {
            Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs))
                    .setX(image.getWidth() * image.getHeight() * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8);
            inAllocation = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

            Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs))
                    .setX(image.getWidth())
                    .setY(image.getHeight());
            outAllocation = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
        }

        ByteBuffer yuvBuffer = ImageUtils.imageToByteBuffer(image);
        inAllocation.copyFrom(yuvBuffer.array());

        yuvToRgbIntrinsic.setInput(inAllocation);
        yuvToRgbIntrinsic.forEach(outAllocation);

        outAllocation.copyTo(output);
    }
}
