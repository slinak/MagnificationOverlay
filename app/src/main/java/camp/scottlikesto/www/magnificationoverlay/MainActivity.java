package camp.scottlikesto.www.magnificationoverlay;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import java.util.ArrayList;

/*
new Scale(4, 5, "mm")
new Scale(10, 2, "mm")
new Scale(40, .5, "mm")
μ
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

        } else {
            requestPermission();
        }

        camera = getCameraInstance();

        cameraPreview = new CameraPreview(this, camera);
        cameraPreview.setKeepScreenOn(true);
        scales = new ArrayList<Scale>();
        c = this;

        captureImage = (Button) findViewById(R.id.capture_image);
        setMagnification = (Button) findViewById(R.id.set_magnification);
        magnificationValue = (Spinner) findViewById(R.id.magnification_value);

        scales.add(new Scale(4, 5, "mm"));
        scales.add(new Scale(10, 2, "mm"));
        scales.add(new Scale(40, .5, "mm"));

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

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
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
