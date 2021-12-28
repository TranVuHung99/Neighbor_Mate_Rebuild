package com.example.neighbormaterebuild.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageUtil {
    public static Bitmap decodeUri(Context context, Uri uri,
                                   final int requiredSize, final int rotate) throws FileNotFoundException {
        Bitmap bitmap = null;

        try {
            String path = getPathFromUri(context, uri);

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            // Adjust extents
            int sourceWidth, sourceHeight;
            if (rotate == 90 || rotate == 270) {
                sourceWidth = options.outHeight;
                sourceHeight = options.outWidth;
            } else {
                sourceWidth = options.outWidth;
                sourceHeight = options.outHeight;
            }

            // Calculate the maximum required scaling ratio if required and load the bitmap
            if (sourceWidth > requiredSize || sourceHeight > requiredSize) {
                float widthRatio = (float)sourceWidth / (float)requiredSize;
                float heightRatio = (float)sourceHeight / (float)requiredSize;
                float maxRatio = Math.max(widthRatio, heightRatio);
                options.inJustDecodeBounds = false;
                options.inSampleSize = (int)maxRatio;
                bitmap = BitmapFactory.decodeFile(path, options);
            } else {
                bitmap = BitmapFactory.decodeFile(path);
            }

            // Rotate the bitmap if required
            if (rotate > 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }

            // Re-scale the bitmap if necessary
            sourceWidth = bitmap.getWidth();
            sourceHeight = bitmap.getHeight();
            if (sourceWidth != requiredSize || sourceHeight != requiredSize) {
                float widthRatio = (float)sourceWidth / (float)requiredSize;
                float heightRatio = (float)sourceHeight / (float)requiredSize;
                float maxRatio = Math.max(widthRatio, heightRatio);
                sourceWidth = (int)((float)sourceWidth / maxRatio);
                sourceHeight = (int)((float)sourceHeight / maxRatio);
                bitmap = Bitmap.createScaledBitmap(bitmap, sourceWidth, sourceHeight, true);
            }
        } catch (Exception e) {
        }
        return bitmap;

    }

    public static Bitmap decodeUri(Context context, Uri uri,
                                   final int requiredSize) throws FileNotFoundException {
        return decodeUri(context, uri, requiredSize, 0);
    }

    // Get Original image path
    public static String getPathFromUri(final Context context, final Uri uri) {
        File file = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        boolean success = false;
        try {
            String extension = getImageExtension(uri);
            inputStream = context.getContentResolver().openInputStream(uri);
            file = File.createTempFile("image_picker", extension, context.getCacheDir());
            file.deleteOnExit();
            outputStream = new FileOutputStream(file);
            if (inputStream != null) {
                copy(inputStream, outputStream);
                success = true;
            }
        } catch (IOException ignored) {
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException ignored) {
            }
            try {
                if (outputStream != null) outputStream.close();
            } catch (IOException ignored) {
                // If closing the output stream fails, we cannot be sure that the
                // target file was written in full. Flushing the stream merely moves
                // the bytes into the OS, not necessarily to the file.
                success = false;
            }
        }
        return success ? file.getPath() : null;
    }

    public static String getFileType(String path) {
        String extension = null;

        try {

            if (path != null && path.lastIndexOf(".") != -1) {
                extension = path.substring(path.lastIndexOf(".") + 1);
            }
        } catch (Exception e) {
            extension = null;
        }

        if (extension == null || extension.isEmpty()) {
            extension = "jpg";
        }

        return extension;
    }

    /**
     * @return extension of image with dot, or default .jpg if it none.
     */
    private static String getImageExtension(Uri uriImage) {
        String extension = null;

        try {
            String imagePath = uriImage.getPath();
            if (imagePath != null && imagePath.lastIndexOf(".") != -1) {
                extension = imagePath.substring(imagePath.lastIndexOf(".") + 1);
            }
        } catch (Exception e) {
            extension = null;
        }

        if (extension == null || extension.isEmpty()) {
            //default extension for matches the previous behavior of the plugin
            extension = "jpg";
        }

        return "." + extension;
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        final byte[] buffer = new byte[4 * 1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        out.flush();
    }

    public static File resizeImageFile(Context context, Uri imageUri){
        try {
            ExifInterface exif = new ExifInterface(getPathFromUri(context,imageUri));
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            int rotate = 0;
            //Log.d("Resize image", "orientation: " + orientation);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                rotate = 90;
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                rotate = 180;
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                rotate = 270;
            }

            Bitmap bitmap = decodeUri(context, imageUri, 1000, rotate);

            File outputFile = File.createTempFile("upload-", ".png");
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            bitmap.compress(Bitmap.CompressFormat.PNG, 95 , outputStream);

            //Log.e("Resize image", "success resize: " + outputFile.getAbsolutePath() + ", with: " + bitmap.getWidth() + ", height: " + bitmap.getHeight());

            return outputFile;
        } catch (Exception e) {
            //Log.e("Resize image", "error: " + e.getMessage());
            return null;
        }
    }
}
