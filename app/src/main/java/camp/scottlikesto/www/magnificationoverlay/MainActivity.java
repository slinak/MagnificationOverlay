package camp.scottlikesto.www.magnificationoverlay;

import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
    private Camera camera;
    private CameraPreview cameraPreview;
    private ScaleView scalePreview;
    //private Button takePictureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        camera = getCameraInstance();

        cameraPreview = new CameraPreview(this, camera);
        scalePreview = new ScaleView(this);

        FrameLayout layout = (FrameLayout) findViewById(R.id.screen_preview);
        layout.addView(cameraPreview);
        layout.addView(scalePreview);
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            Log.d(null, "Camera is not available - " + e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }
}
