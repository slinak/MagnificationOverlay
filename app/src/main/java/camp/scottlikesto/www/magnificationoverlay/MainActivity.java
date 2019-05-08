package camp.scottlikesto.www.magnificationoverlay;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
    private Camera camera;
    private CameraPreview cameraPreview;
    private ScaleView scalePreview;
    private Button captureImage, setMagnification;
    FrameLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        camera = getCameraInstance();

        cameraPreview = new CameraPreview(this, camera);
        scalePreview = new ScaleView(this, new Scale(10, 15, "mm"));

        captureImage = (Button) findViewById(R.id.capture_image);
        setMagnification = (Button) findViewById(R.id.set_magnification);

        layout = (FrameLayout) findViewById(R.id.screen_preview);
        layout.addView(cameraPreview);


        setMagnification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.addView(scalePreview);
            }
        });

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture(null, null, null);
            }
        });
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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Log.d("Tag", "Within activity result");
        }
    }
}
