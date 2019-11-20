package com.google.ar.sceneform.samples.augmentedimage;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class StarterActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 100;
    private static final int PICK_VIDEO_REQUEST = 102;

    ImageView iv;
    Uri selectedImageUri, selectedVideoUri;
    ImageVideoMapping map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Button button = findViewById(R.id.test);
        button.setOnClickListener(v -> {
           startActivity(new Intent(this, AugmentedImageActivity.class));
        });
        setSupportActionBar(toolbar);
        iv = findViewById(R.id.picture);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });
        map = ImageVideoMapping.getInstance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == PICK_IMAGE_REQUEST) {
                    selectedImageUri = data.getData();
                    // Get the path from the Uri
                    final String path = getPathFromURI(selectedImageUri);
                    if (path != null) {
                        File f = new File(path);
                        selectedImageUri = Uri.fromFile(f);
                    }
                    // Set the image in ImageView
                    iv.setImageURI(selectedImageUri);
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);

                    intent.setType("video/*");
                    startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
                } else if ((requestCode == PICK_VIDEO_REQUEST)) {
                    selectedVideoUri = data.getData();
                    // Get the path from the Uri
                    final String path = getPathFromURI(selectedVideoUri);
                    if (path != null) {
                        File f = new File(path);
                        selectedVideoUri = Uri.fromFile(f);
                    }
                    // Set the image in ImageView
                    System.out.println(selectedImageUri.toString() + " " + selectedVideoUri.toString());
                    map.addImageVideo(selectedImageUri.toString(), selectedVideoUri.toString());
//                    map.printMap();
                }
            }
        } catch (Exception e) {
            Log.e("FileSelectorActivity", "File select error", e);
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}
