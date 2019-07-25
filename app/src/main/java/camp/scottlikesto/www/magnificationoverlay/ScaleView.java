package camp.scottlikesto.www.magnificationoverlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceView;

public class ScaleView extends SurfaceView{
    private int magnification;

    private Paint halfLine = new Paint(), mainHorizontalLine = new Paint(), endLine = new Paint();

    private Canvas canvas;
    private Scale scale;

    public ScaleView (Context context, Scale _scale) {
        super(context);
        scale = _scale;
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas _canvas) {
        canvas = _canvas;
        super.onDraw(canvas);



        mainHorizontalLine.setStyle(Paint.Style.STROKE);
        mainHorizontalLine.setColor(Color.RED);
        mainHorizontalLine.setStrokeWidth(10);

        endLine.setStyle(Paint.Style.STROKE);
        endLine.setColor(Color.RED);
        endLine.setStrokeWidth(10);

        halfLine.setStyle(Paint.Style.STROKE);
        halfLine.setColor(Color.RED);
        halfLine.setStrokeWidth(5);

        int x0 = canvas.getWidth()/6;
        int y0 = canvas.getHeight()/5;
        int dx = getLineLengthFromMeasurement((float)scale.length);
        int dy = 40;

        //draw main horizontal line
        canvas.drawLine(x0, y0, x0 + dx, y0, mainHorizontalLine);

        //draw line at start and end of mainLine
        canvas.drawLine(x0, y0 + dy, x0, y0 - dy, endLine);
        canvas.drawLine(x0 + dx, y0 + dy, x0 + dx, y0 - dy, endLine);

        //draw line at half point of mainLine
        canvas.drawLine((x0 + dx/2), y0 + dy/2, (x0 + dx/2), y0 - dy/2, halfLine);

        mainHorizontalLine.setTextSize(72);
        canvas.drawText(scale.length + scale.unit, canvas.getWidth()/4,canvas.getHeight()/4, mainHorizontalLine );
        //canvas.drawRect(x0-dx, y0-dy, x0+dx, y0+dy, paint);
    }

    public int getLineLengthFromMeasurement(float millimeters) {
        int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, millimeters, getResources().getDisplayMetrics());
        Log.w("YOYOYO", "Pixels: " + px);
        return px;
    }
}
