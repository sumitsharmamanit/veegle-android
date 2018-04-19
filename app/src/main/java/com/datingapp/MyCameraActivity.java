package com.datingapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.datingapp.util.CommonUtils;
import com.datingapp.util.Constant;
import com.datingapp.util.ManageSession;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MyCameraActivity extends Activity implements View.OnClickListener {
    private Intent intent;
    private Bitmap bitmap;
    //    private Utilities utilities;
    private Context context;
    private ManageSession appSession;
    private File photoFile;
    private String picturePath = "", image = "", cropPicturePath = "";
    private Uri cameraUri = null;
    public static String type = "";
    private File videoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_dialog_chooser);
        this.setFinishOnTouchOutside(true);
        videoFile = CommonUtils.createVideoFile(".mp4");
        context = this;
//        utilities = Utilities.getInstance(context);
        appSession = new ManageSession(context);


        if (type.equals("1")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        return;
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        cameraUri = FileProvider.getUriForFile(context,
                                getApplicationContext().getPackageName() + ".provider", photoFile);
                        appSession.setImageUri(cameraUri);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
                        startActivityForResult(intent, Constant.CAMERA);
                    }
                }
            } else {

                intent = new Intent();
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String fileName = "IMAGE_" + System.currentTimeMillis() + ".jpg";
                cameraUri = Uri.fromFile(getNewFile(Constant.IMAGE_DIRECTORY, fileName));
                appSession.setImageUri(cameraUri);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, Constant.CAMERA);
            }
        } else if (type.equals("2")) {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(intent, ""),
                    Constant.GALLERY);
        } else {
            dailogImageChooser(context, "Choose Image");
        }
    }

    public void handleCamera() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        return;
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {

                        cameraUri = FileProvider.getUriForFile(context,
                                getApplicationContext().getPackageName() + ".provider", photoFile);


                        appSession.setImageUri(cameraUri);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
                        startActivityForResult(intent, Constant.CAMERA);
                    }
                }
            } else {

                intent = new Intent();
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String fileName = "IMAGE_" + System.currentTimeMillis() + ".jpg";
                cameraUri = Uri.fromFile(getNewFile(Constant.IMAGE_DIRECTORY, fileName));
                appSession.setImageUri(cameraUri);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, Constant.CAMERA);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleGallery() {
        try {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");

            //  intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"video/*"});
            startActivityForResult(Intent.createChooser(intent, ""),
                    Constant.GALLERY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dailogImageChooser(final Context context, String header) {

        TextView tvHeader = (TextView) findViewById(R.id.tv_header);
        TextView tvGallery = (TextView) findViewById(R.id.tv_gallery);
        TextView tvCamera = (TextView) findViewById(R.id.tv_camera);
        TextView tvTakeVideo = findViewById(R.id.tv_take_video);
        findViewById(R.id.tv_upload_video).setOnClickListener(this);
        findViewById(R.id.tv_gif).setOnClickListener(this);
        appSession = new ManageSession(context);
        tvHeader.setText(header);
        tvCamera.setOnClickListener(this);
        tvGallery.setOnClickListener(this);
        tvTakeVideo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_camera:
                handleCamera();
                break;
            case R.id.tv_gallery:
                handleGallery();
                break;
            case R.id.tv_take_video:
                handleVideoCapture();
                break;
            case R.id.tv_upload_video:
                handleVideoGallery();
                break;
            case R.id.tv_gif:
                handleGIF();
                break;
        }
    }


    public void handleGIF() {
        try {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/gif");
            startActivityForResult(Intent.createChooser(intent, ""),
                    Constant.GIF_GALLERY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleVideoGallery() {
        try {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            intent.setType("video/*");

            //  intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"video/*"});
            startActivityForResult(Intent.createChooser(intent, ""),
                    Constant.VIDEO_GALLERY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleVideoCapture() {
        try {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                takeVideoIntent.putExtra(
                        MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", videoFile));
            } else
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file:" + videoFile.getAbsolutePath()));

            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takeVideoIntent, Constant.RequestTakeVideo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );


        return image;
    }


    private Uri getTempUri() {
        return Uri.fromFile(getTempFile());
    }

    private File getTempFile() {
        String imageName = "CROP_" + System.currentTimeMillis() + ".jpg";
        File tempFile = getNewFile(Constant.IMAGE_DIRECTORY_CROP, imageName);
        cropPicturePath = tempFile.getPath();
        appSession = new ManageSession(context);
        appSession.setCropImagePath(tempFile.getPath());
        return tempFile;
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
            cropIntent.putExtra("outputFormat",
                    Bitmap.CompressFormat.JPEG.toString());

            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            startActivityForResult(cropIntent, Constant.CROP);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context,
                    getString(R.string.crop_action_support), Toast.LENGTH_SHORT)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context,
                    getString(R.string.crop_action_support), Toast.LENGTH_SHORT)
                    .show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        appSession = new ManageSession(context);
        Intent intentMessage = new Intent();
        if (requestCode != Constant.ACTIVITY_RESULT && resultCode != RESULT_OK) {

            // put the message in Intent
            intentMessage.putExtra("image", image);
            // Set The Result in Intent
            setResult(0, intentMessage);
            // finish The activity
            finish();

            type = "";
        }

        if (requestCode == Constant.GIF_GALLERY && resultCode == RESULT_OK) {
            try {
                String videoPath = "";
                Uri uriImage = data.getData();
                if (uriImage != null) {
                    videoPath = getAbsolutePath(uriImage);
                    if (videoPath == null || videoPath.equals(""))
                        videoPath = uriImage.getPath();
                    if (videoPath!=null && (videoPath.endsWith(".gif") || videoPath.endsWith(".GIF"))) {
                        Intent intent = new Intent();
                        intent.putExtra(Constant.GIF, videoPath);
                        setResult(2, intent);
                        finish();
                    } else
                        CommonUtils.toast(getString(R.string.msg_invalid_gif), this, Toast.LENGTH_LONG);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (requestCode == Constant.VIDEO_GALLERY && resultCode == RESULT_OK)

        {
            try {
                String videoPath = "";
                Uri uriImage = data.getData();
                if (uriImage != null) {
                    videoPath = getAbsolutePath(uriImage);
                    if (videoPath == null || videoPath.equals(""))
                        videoPath = uriImage.getPath();
                    Intent intent = new Intent();
                    intent.putExtra(Constant.video, videoPath);
                    setResult(2, intent);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == Constant.RequestTakeVideo && resultCode == RESULT_OK)

        {
            if (videoFile != null) {
                Log.d("path", videoFile.getAbsolutePath().toString());
                intentMessage.putExtra(Constant.video, videoFile.getAbsolutePath());
                setResult(Constant.RequestTakeVideo, intentMessage);
                finish();
                // return;
            }

        }

        if (requestCode == Constant.CROP && resultCode == Activity.RESULT_OK)

        {
            type = "";
            try {
                if (cropPicturePath == null || cropPicturePath.equals("")
                        || !new File(cropPicturePath).isFile())
                    cropPicturePath = appSession.getCropImagePath();

                if (cropPicturePath == null || cropPicturePath.equals("")
                        || !new File(cropPicturePath).isFile())
                    cropPicturePath = picturePath;

                if (cropPicturePath == null || cropPicturePath.equals("")
                        || !new File(cropPicturePath).isFile())
                    cropPicturePath = appSession.getImagePath();

                if (cropPicturePath != null && !cropPicturePath.equals("")
                        && new File(cropPicturePath).isFile()) {
                    if (bitmap != null)
                        bitmap.recycle();

//                    bitmap = new Compressor(this).compressToBitmap(new File(cropPicturePath));

                    bitmap = decodeFile(new File(cropPicturePath),
                            640, 640); //640
                    cropPicturePath = getFilePath(bitmap, context, cropPicturePath);

                    image = cropPicturePath;
                    // Intent intentMessage = new Intent();
                    // put the message in Intent

                    intentMessage.putExtra("image", image);

                    // Set The Result in Intent

                    setResult(2, intentMessage);

                    // finish The activity
                    finish();
                } else {
                    Toast.makeText(context,
                            getString(R.string.crop_action_error),
                            Toast.LENGTH_LONG).show();
                    //    Intent intentMessage = new Intent();

                    // put the message in Intent
                    intentMessage.putExtra("image", image);
                    // Set The Result in Intent
                    setResult(0, intentMessage);
                    // finish The activity
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context,
                        getString(R.string.crop_action_error),
                        Toast.LENGTH_LONG).show();
                Toast.makeText(context,
                        getString(R.string.crop_action_error),
                        Toast.LENGTH_LONG).show();
                //   Intent intentMessage = new Intent();

                // put the message in Intent
                intentMessage.putExtra("image", image);
                // Set The Result in Intent
                setResult(0, intentMessage);
                // finish The activity
                finish();
            }
        } else if (resultCode != Activity.RESULT_CANCELED)

        {
            type = "";
            if (requestCode == Constant.GALLERY) {
                try {
                    Uri uriImage = data.getData();
                    if (uriImage != null) {
                        picturePath = getAbsolutePath(uriImage);
                        if (picturePath == null || picturePath.equals(""))
                            picturePath = uriImage.getPath();

                        appSession.setImagePath(picturePath);
                        Cursor cursor = context
                                .getContentResolver()
                                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        new String[]{MediaStore.Images.Media._ID},
                                        MediaStore.Images.Media.DATA + "=? ",
                                        new String[]{picturePath}, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                            uriImage = Uri.parse("content://media/external/images/media/" + id);
                        }
                        performCrop(uriImage);

                    } else {
                        Toast.makeText(context,
                                getString(R.string.gallery_pick_error),
                                Toast.LENGTH_LONG).show();
                        Toast.makeText(context,
                                getString(R.string.crop_action_error),
                                Toast.LENGTH_LONG).show();
                        //        Intent intentMessage = new Intent();

                        // put the message in Intent
                        intentMessage.putExtra("image", image);
                        // Set The Result in Intent
                        setResult(0, intentMessage);
                        // finish The activity
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context,
                            getString(R.string.gallery_pick_error),
                            Toast.LENGTH_LONG).show();
                    Toast.makeText(context,
                            getString(R.string.crop_action_error),
                            Toast.LENGTH_LONG).show();
                    //   Intent intentMessage = new Intent();

                    // put the message in Intent
                    intentMessage.putExtra("image", image);
                    // Set The Result in Intent
                    setResult(0, intentMessage);
                    // finish The activity
                    finish();
                }
            } else if (requestCode == Constant.CAMERA) {
                try {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    Uri uri = Uri.parse(photoFile.getAbsolutePath());
                        picturePath = photoFile.getAbsolutePath();
                        appSession.setImagePath(picturePath);
                        cropPicturePath = picturePath;
                        Log.i(getClass().getName(), "Nougat Path >>>>>>>" + cropPicturePath);

                       /* Intent intentMessage = new Intent();

                        // put the message in Intent
                        intentMessage.putExtra("image", cropPicturePath);
                        // Set The Result in Intent
                        setResult(2, intentMessage);
                        // finish The activity
                        finish();*/

                        Cursor cursor = context
                                .getContentResolver()
                                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        new String[]{MediaStore.Images.Media._ID},
                                        MediaStore.Images.Media.DATA + "=? ",
                                        new String[]{picturePath}, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int id = cursor
                                    .getInt(cursor
                                            .getColumnIndex(MediaStore.MediaColumns._ID));
                            cameraUri = Uri
                                    .parse("content://media/external/images/media/"
                                            + id);
                        }
                        performCrop(cameraUri);
                    } else {

                        if (cameraUri == null)
                            cameraUri = appSession.getImageUri();
                        if (cameraUri != null) {
                            picturePath = getAbsolutePath(cameraUri);
                            if (picturePath == null || picturePath.equals(""))
                                picturePath = cameraUri.getPath();
                            appSession.setImagePath(picturePath);

                            Log.i(getClass().getName(), "Simple Path >>>>>>>" + picturePath);
                            Cursor cursor = context
                                    .getContentResolver()
                                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            new String[]{MediaStore.Images.Media._ID},
                                            MediaStore.Images.Media.DATA + "=? ",
                                            new String[]{picturePath}, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                int id = cursor
                                        .getInt(cursor
                                                .getColumnIndex(MediaStore.MediaColumns._ID));
                                cameraUri = Uri
                                        .parse("content://media/external/images/media/"
                                                + id);
                            }
                            performCrop(cameraUri);
                        } else {
                            Toast.makeText(context,
                                    getString(R.string.camera_capture_error),
                                    Toast.LENGTH_LONG).show();
                            Toast.makeText(context,
                                    getString(R.string.crop_action_error),
                                    Toast.LENGTH_LONG).show();
                            //     Intent intentMessage = new Intent();
//
                            // put the message in Intent
                            intentMessage.putExtra("image", image);
                            // Set The Result in Intent
                            setResult(0, intentMessage);
                            // finish The activity
                            finish();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context,
                            getString(R.string.camera_capture_error),
                            Toast.LENGTH_LONG).show();
                    Toast.makeText(context,
                            getString(R.string.crop_action_error),
                            Toast.LENGTH_LONG).show();
                    //    Intent intentMessage = new Intent();

                    // put the message in Intent
                    intentMessage.putExtra("image", image);
                    // Set The Result in Intent
                    setResult(0, intentMessage);
                    // finish The activity
                    finish();
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intentMessage = new Intent();
        // put the message in Intent
        intentMessage.putExtra("image", image);
        // Set The Result in Intent
        setResult(0, intentMessage);
        // finish The activity
        finish();
    }

    /**
     * This method used to create new file if not exist .
     */
    public File getNewFile(String directoryName, String imageName) {
        String root = Environment.getExternalStorageDirectory()
                + directoryName;
        File file;
        if (isSDCARDMounted()) {
            new File(root).mkdirs();
            file = new File(root, imageName);
        } else {
            file = new File(context.getFilesDir(), imageName);
        }
        return file;
    }

    public boolean isSDCARDMounted() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public Bitmap decodeFile(File f, int REQUIRED_WIDTH,
                             int REQUIRED_HEIGHT) {
        try {
            ExifInterface exif = new ExifInterface(f.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            int angle = 0;

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }

            Matrix mat = new Matrix();
            mat.postRotate(angle);
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            // Find the correct scale value. It should be the power of 2.
            int REQUIRED_SIZE = 100; // 70
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            if (width_tmp > height_tmp) {
                REQUIRED_SIZE = REQUIRED_HEIGHT;
                REQUIRED_HEIGHT = REQUIRED_WIDTH;
                REQUIRED_WIDTH = REQUIRED_SIZE;
            }
            while (true) {
                if (width_tmp / 2 < REQUIRED_WIDTH
                        && height_tmp / 2 < REQUIRED_HEIGHT)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            o2.inPurgeable = true;
            Bitmap correctBmp = BitmapFactory.decodeStream(new FileInputStream(
                    f), null, o2);
            correctBmp = Bitmap.createBitmap(correctBmp, 0, 0,
                    correctBmp.getWidth(), correctBmp.getHeight(), mat, true);
            return correctBmp;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeFile(f.getAbsolutePath());
    }

    public String getFilePath(Bitmap bitmap, Context context, String path) {
        //  File cacheDir;
        File file;

        try {

            if (bitmap != null) {
                file = new File(path);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                FileOutputStream fo;

                fo = new FileOutputStream(file);
                fo.write(bytes.toByteArray());
                fo.close();

                return file.getAbsolutePath();
            }

        } catch (Exception e1) {
            e1.printStackTrace();

        } catch (Error e1) {
            e1.printStackTrace();
        }

        return path;
    }


}
