    package com.example;

    import android.app.Activity;
    import android.content.Intent;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.net.Uri;
    import android.os.Bundle;
    import android.os.Environment;
    import android.provider.MediaStore;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.Toast;

    import java.io.File;
    import java.io.FileNotFoundException;
    import java.io.IOException;
    import java.io.InputStream;

    public class MyActivity extends Activity {

        private Button btn;
        private ImageView imageView;

        private static final File photoPath = new File(Environment.getExternalStorageDirectory(), "camera.jpg");

        private static final int CAMERA = 1;

        /**
         * Called when the activity is first created.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);

            findViews();
            setListeners();
        }

        private void setListeners() {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoPath));
                    startActivityForResult(intent, CAMERA);
                }
            });
        }

        private void findViews() {
            btn = (Button) findViewById(R.id.btn);
            imageView = (ImageView) findViewById(R.id.imageView);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CAMERA) {
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = getCameraBitmap(data);
                        if (bitmap == null) {
                            Toast.makeText(MyActivity.this, "Can't get bitmap from camera", Toast.LENGTH_LONG).show();
                        } else {
                            imageView.setImageBitmap(bitmap);
                        }
                    } catch (IOException e) {
                        Toast.makeText(MyActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        public Bitmap getCameraBitmap(Intent data) throws IOException {
            if (data == null) {
                // try solution 1
                try {
                    return MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(photoPath));
                } catch (FileNotFoundException e) {
                    return BitmapFactory.decodeFile(photoPath.getAbsolutePath());
                }
            } else {
                Uri image = data.getData();
                if (image != null) {
                    // try solution 3
                    InputStream inputStream = getContentResolver().openInputStream(image);
                    return BitmapFactory.decodeStream(inputStream);
                } else {
                    // try solution 4
                    return (Bitmap) data.getExtras().get("data");
                }
            }
        }
    }
