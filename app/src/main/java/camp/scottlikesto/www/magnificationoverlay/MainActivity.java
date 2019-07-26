package camp.scottlikesto.www.magnificationoverlay;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.PixelCopy;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.Manifest.permission.READ_CONTACTS;

//4x=0.48cm 10x=0.19cm, 40X=0.045cm

/*
DisplayMetrics{density=2.75, width=2028, height=1080, scaledDensity=2.75, xdpi=440.0, ydpi=440.0}

1dpi = 1px = 25.4mm

2.75dpi = 1px = 9.24mm
 */

public class MainActivity extends AppCompatActivity {
    private Camera camera;
    private CameraPreview cameraPreview;
    private ScaleView scalePreview;
    private Button captureImage, setMagnification;
    private Spinner magnificationValue;
    private Context c;
    private ArrayList<Scale> scales;
    private static final int PERMISSION_REQUEST_CODE = 200;
    FrameLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (checkPermission()) {
            startCameraPreview();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void startCameraPreview() {
        camera = getCameraInstance();

        cameraPreview = new CameraPreview(this, camera);
        cameraPreview.setKeepScreenOn(true);
        scales = new ArrayList<Scale>();
        c = this;

        captureImage = (Button) findViewById(R.id.capture_image);
        setMagnification = (Button) findViewById(R.id.set_magnification);
        magnificationValue = (Spinner) findViewById(R.id.magnification_value);

        scales.add(new Scale(4, 480, "mm"));
        scales.add(new Scale(10, 190, "mm"));
        scales.add(new Scale(40, 45, "mm"));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.magnification_resources, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        magnificationValue.setAdapter(adapter);

        layout = (FrameLayout) findViewById(R.id.screen_preview);

        layout.addView(cameraPreview);

        setMagnification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pretty terrible hack to ensure any previously drawn scaleViews are deleted
                try {
                    ScaleView oldView = layout.findViewById(R.id.scale_view_id);
                    layout.removeView(oldView);
                } catch (Exception e) {
                    Log.w("YOYOYO", "Exception: " + e.getMessage());
                }

                scalePreview = new ScaleView(c, scales.get(magnificationValue.getSelectedItemPosition()));
                scalePreview.setId(R.id.scale_view_id);
                scalePreview.setZOrderOnTop(true);

                layout.addView(scalePreview);
            }
        });

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();


            }
        });
    }

    private void takePicture() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final HandlerThread handler = new HandlerThread("PixelCopier");
            handler.start();
            String fileName = generateFilename();

            final Bitmap bitmap = Bitmap.createBitmap(cameraPreview.getWidth(), cameraPreview.getHeight(), Bitmap.Config.ARGB_8888);

            PixelCopy.request(getWindow(), bitmap, (copyResult) -> {
                if (copyResult == PixelCopy.SUCCESS) {
                    try {
                        processBitmapToFile(bitmap, fileName);
                    } catch (IOException e) {
                        Log.w("YOYOYO", "IOException: " + e.getMessage());
                        return;
                    }
                } else {
                    Log.w("YOYOYO", "Failed to CopyPixels");
                }
                handler.quitSafely();
            }, new Handler(handler.getLooper()));
            /*
            PixelCopy.request(cameraPreview, bitmap, (copyResult) -> {
                if (copyResult == PixelCopy.SUCCESS) {
                    try {
                        processBitmapToFile(bitmap, fileName);
                    } catch (IOException e) {
                        Log.w("YOYOYO", "IOException: " + e.getMessage());
                        return;
                    }
                } else {
                    Log.w("YOYOYO", "Failed to CopyPixels");
                }
                handler.quitSafely();
            }, new Handler(handler.getLooper()));
            */
        }
    }

    private void processBitmapToFile(Bitmap bitmap, String filename) throws IOException {
        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }

        try (FileOutputStream outStream = new FileOutputStream(filename)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            Log.w("YOYOYO", "Filename: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateFilename() {
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "MagnificationOverlay/" + String.format("%d.jpg", System.currentTimeMillis()) + "_screenshot.jpg";
    }

    public boolean checkPermission() {
        int cameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return cameraPermission == PackageManager.PERMISSION_GRANTED && writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.w("YOYOYO", "Grant Results Length: " + grantResults.length);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraPermission && writeExternalStoragePermission ) {
                        startCameraPreview();
                    }
                    else {
                        //do nothing atm
                    }
                }
                break;
        }
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            //camera not available
        }
        return c;
    }
}
