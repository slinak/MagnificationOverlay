package camp.scottlikesto.www.magnificationoverlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.SurfaceView;

public class ScaleView extends SurfaceView{
    private int magnification;
    private Paint paint = new Paint();
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

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);

        int x0 = canvas.getWidth()/2;
        int y0 = canvas.getHeight()/2;
        int dx = canvas.getHeight()/3;
        int dy = canvas.getHeight()/3;

        canvas.drawLine(canvas.getWidth()/4, canvas.getHeight()/5, canvas.getWidth()/2, canvas.getHeight()/5, paint);

        paint.setTextSize(72);
        canvas.drawText(scale.length + scale.unit, canvas.getWidth()/4,canvas.getHeight()/4, paint );
        //canvas.drawRect(x0-dx, y0-dy, x0+dx, y0+dy, paint);
    }

    public void drawLineFromMagnificiation(int magnification) {
        paint.setColor(Color.WHITE);
        canvas.drawLine(canvas.getWidth()/4, canvas.getHeight()/5, canvas.getWidth()/2, canvas.getHeight()/5, paint);
    }
}
