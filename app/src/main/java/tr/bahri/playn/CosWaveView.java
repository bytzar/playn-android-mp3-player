package tr.bahri.playn;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CosWaveView extends View {
    private Paint paint = new Paint();
    private float phase = 0;

    public CosWaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setStrokeWidth(10); // Increase the stroke width
        paint.setColor(Color.parseColor("#bae0ca")); // Change the color to bae0ca
    }

    // ... rest of your code ...

    public float getPhase() {
        return phase;
    }

    public void setPhase(float phase) {
        this.phase = phase;
        invalidate(); // Redraw the view with the new phase
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float heightMultiplierForStroke = 0.8f; // Adjust this value to fit the wave within the constraints
        float verticalOffset = paint.getStrokeWidth() / 2; // Offset to ensure the stroke doesn't go outside the view
        float frequency = 200.0f; // Adjust this value to change the frequency higher better

        float lastY = (float) ((getHeight() / 2) * heightMultiplierForStroke + Math.cos(phase) * (getHeight() / 2) * heightMultiplierForStroke) + verticalOffset;

        for (int i = 1; i < getWidth(); i++) {
            float y = (float) ((getHeight() / 2) * heightMultiplierForStroke + Math.cos((i + phase) / frequency) * (getHeight() / 2) * heightMultiplierForStroke) + verticalOffset;
            if (lastY != y)
            {
                canvas.drawLine(i - 1, lastY, i, y, paint);
                lastY = y;
            }
        }
    }



}
