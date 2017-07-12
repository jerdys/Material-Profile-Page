package io.github.jerdys.materialprofile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class ProfileActivity extends AppCompatActivity {
    private final int CAMERA_RESULT = 0;
    private final int CAMERA_REQUEST = 1;
    private File output = null;
    private Uri uri;
    private ImageView profilePhoto;
    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_PROFILE_PHOTO = "settings"; //TODO
    private SharedPreferences preferences;
    private String photosDirectory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePhoto = (ImageView) findViewById(R.id.profile_photo);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        setSupportActionBar(toolbar);

        builder.setTitle("Attention")
                .setMessage("If you want to set a photo, please take it in landscape mode.\n" +
                        "Portrait mode is not supported yet.")
                .setCancelable(true)
                .setPositiveButton
                        ("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;

                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (photoFile == null) {
                        Uri photoURI = FileProvider.getUriForFile(ProfileActivity.this,
                                "com.androidveteran.android.materialprofile",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    }

                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                }
            }


        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            profilePhoto.setImageBitmap(photo);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(APP_PREFERENCES_PROFILE_PHOTO, "");
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (preferences.contains(APP_PREFERENCES_PROFILE_PHOTO)) {

        }
    }
/*

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_RESULT && resultCode == RESULT_OK) {

            //finish();

            Bitmap thumbnailBitmap = (Bitmap) data.getExtras().get("data");
            profilePhoto.setImageBitmap(thumbnailBitmap);
        }
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void galleryAddPhoto() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(photosDirectory);
        Uri contentURI = Uri.fromFile(file);
        mediaScanIntent.setData(contentURI);
        this.sendBroadcast(mediaScanIntent);
    }

    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        }
        else {
            return null;
        }

        return mediaFile;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                image = File.createTempFile(
                        imageFileName,
                        ".jpg",
                        storageDirectory);

        photosDirectory = image.getAbsolutePath();
        return image;
    }
}
