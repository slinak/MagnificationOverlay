package camp.scottlikesto.www.magnificationoverlay;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import java.util.ArrayList;

import static android.Manifest.permission.READ_CONTACTS;

//4x=0.48cm 10x=0.19cm, 40X=0.045cm

/*
        scales.add(new Scale(4, 5, "mm"));
        scales.add(new Scale(10, 2, "mm"));
        scales.add(new Scale(40, .5, "mm"));


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


        //startCameraPreview();

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

        scales.add(new Scale(4, 48, "mm"));
        scales.add(new Scale(10, 19, "mm"));
        scales.add(new Scale(40, 4.5, "mm"));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.magnification_resources, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        magnificationValue.setAdapter(adapter);

        layout = (FrameLayout) findViewById(R.id.screen_preview);

        layout.addView(cameraPreview);

        setMagnification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scalePreview = new ScaleView(c, scales.get(magnificationValue.getSelectedItemPosition()));

                layout.addView(scalePreview);
            }
        });

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture(cameraPreview.shutterCallback, cameraPreview.rawCallback, cameraPreview.jpegCallback);
            }
        });
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
