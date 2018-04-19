package com.datingapp.util;

import android.app.Activity;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.datingapp.adapter.ChatAdapter;

import java.io.File;
import java.io.IOException;

public class CommonUtils {

    public static String folder_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Veegle/video/";

    public static void toast(String message, Context context, int length) {
        Toast.makeText(context, message, length).show();
    }

    public static File createVideoFile(String extension) {
        try {

            String fileName = System.currentTimeMillis() + "";
            File directory = getFileDirectory();
            return File.createTempFile(fileName, extension, directory);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static File IsFileExist(String fileName) {
        try {
            createDirectory();
            File file = new File(folder_path + fileName);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void createDirectory() {
        try {
            File file = new File(folder_path);
            if (!file.exists())
                file.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getMimeType(String path) {
        String ext = MimeTypeMap.getFileExtensionFromUrl(path);
        if (ext != null) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        }
        return "";
    }


    public static File createFile(String name, String ext) throws IOException {
        return java.io.File.createTempFile(name, ext);
    }

    public static String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        //  int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf
                // .append(String.format("%02d", hours))
                // .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

    public static String getDuration(String filePath) {
        try {
            String out = "";
            String txtTime = "";
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(filePath);
            String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            Long dur = Long.parseLong(duration);
            String seconds = String.valueOf(((dur % 60000) / 1000));

            String minutes = String.valueOf((dur / 60000));
            if (seconds.length() == 1) {
                txtTime = "0" + minutes + ":" + "0" + seconds;
            } else {
                txtTime = "0" + minutes + ":" + seconds;
            }
            metaRetriever.release();
            return txtTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    static File getFileDirectory() {
        if (Environment.isExternalStorageEmulated()) {

            File path = new File(folder_path);

            if (!path.exists()) {
                path.mkdirs();
                Log.d("FailedToCreate", "true");
            }

            return path;
        }
        return null;
    }

    public static void loadVideoThumb(Context context, ImageView iv, File path) {
        try {
            Glide.with(context).
                    load(Uri.fromFile(path)).thumbnail(0.1f).into(iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void GIFFinished( final File path, final int position, final ChatAdapter adapter){
        for (int i = 0; i < adapter.chatMessageDTOS.size(); i++) {
            if (path.getName().equals(adapter.chatMessageDTOS.get(i).getOrig_name())) {
                adapter.chatMessageDTOS.get(i).setState(Constant.pauseGIF);
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    public static void loadGIF(final Context context, ImageView iv, final File path, final int position, final ChatAdapter adapter) {
        try {
            Glide.with(context).asGif()
                    .load(Uri.fromFile(path))
                    .listener(new RequestListener<GifDrawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                            GIFFinished(path,position,adapter);
                            if(e!=null) e.printStackTrace();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(final GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                            // if (resource instanceof GifDrawable) {

                            resource.setLoopCount(1);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (true) {
                                        if (!resource.isRunning()) {
                                            Log.e("GIFFinished", "true");
                                            ((Activity) context).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    GIFFinished(path,position,adapter);
                                                }
                                            });
                                            break;
                                        }
                                    }
                                }
                            }).start();
//
                            return false;
                        }
                    })
                    .into(iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void loadVideoThumbFromURL(Context context, ImageView iv, String path) {
        try {
            Glide.with(context).
                    load(path).apply(new RequestOptions().frame(1 * 1000)).thumbnail(0.1f).into(iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
