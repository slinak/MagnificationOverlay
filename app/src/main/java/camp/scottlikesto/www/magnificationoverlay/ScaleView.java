package camp.scottlikesto.www.magnificationoverlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceView;

public class ScaleView extends SurfaceView{
    private Paint paint = new Paint();

    public ScaleView (Context context) {
        super(context);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
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
        canvas.drawText("10μm", canvas.getWidth()/4,canvas.getHeight()/4, paint );
        //canvas.drawRect(x0-dx, y0-dy, x0+dx, y0+dy, paint);
    }
}
