package camp.scottlikesto.www.magnificationoverlay;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
    static Camera cameraDevice;
    SurfaceHolder holder;
    public ScaleView scale;


    public CameraPreview(Context context, Camera camera){
        super(context);
        cameraDevice = camera;
        scale = new ScaleView(context);

        holder = getHolder();
        holder.addCallback(this);

        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            cameraDevice.setPreviewDisplay(holder);
            cameraDevice.startPreview();

        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        //nothing atm
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (holder.getSurface() == null) {
            //if preview surface does not exist
            return;
        }

        //stop preview before making changes
        try {
            cameraDevice.stopPreview();
        } catch (Exception e) {
            //ignore
        }

        //set preview size and make any resize, rotate, or reformatting changes

        try {
            cameraDevice.setPreviewDisplay(holder);
            cameraDevice.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}
