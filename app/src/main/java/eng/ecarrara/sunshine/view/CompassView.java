package eng.ecarrara.sunshine.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import eng.ecarrara.sunshine.R;
import eng.ecarrara.sunshine.Utility;

/**
 * Created by ecarrara on 03/12/2014.
 */
public class CompassView extends View {
    private static final String LOG_TAG = CompassView.class.getSimpleName();

    private Paint mCompassPaint;
    private Paint mNeedlePaint;
    private float mDegrees = 0;
    private Bitmap mCompassBitmap;
    private Bitmap mNeedleBitmap;

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mCompassPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCompassPaint.setStyle(Paint.Style.FILL);

        mNeedlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNeedlePaint.setStyle(Paint.Style.FILL);

        mCompassBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.compass);
        mNeedleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.needle);
    }

    public CompassView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int myHeight = hSpecSize;

        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int myWidth = wSpecSize;

        if(hSpecMode == MeasureSpec.EXACTLY) {
            myHeight = hSpecSize;
        } else if(hSpecMode == MeasureSpec.AT_MOST) {
            if(hSpecSize > mCompassBitmap.getHeight()) {
                myHeight = mCompassBitmap.getHeight();
            }
        } else {
            myHeight = mCompassBitmap.getHeight();
        }

        if(wSpecMode == MeasureSpec.EXACTLY) {
            myWidth = wSpecSize;
        } else if(wSpecMode == MeasureSpec.AT_MOST) {
            if(wSpecSize > mCompassBitmap.getWidth()) {
                myWidth = mCompassBitmap.getWidth();
            }
        } else {
            myWidth = mCompassBitmap.getWidth();
        }

        mCompassBitmap = mCompassBitmap.createScaledBitmap(mCompassBitmap, myWidth, myHeight, true);
        prepareNeedleBitmap();

        setMeasuredDimension(myWidth, myHeight);
    }

    private void prepareNeedleBitmap() {

        final int currentNeedleHeight = mNeedleBitmap.getHeight();
        final int currentNeedleWidth = mNeedleBitmap.getWidth();
        int needleHeight = Math.round(mCompassBitmap.getHeight() * 0.4f); //80% of the compass height
        float needleHeightRatio = (float) needleHeight / (float) currentNeedleHeight; //calculate the ratio of sizing based on height
        int needleWidth = Math.round(currentNeedleWidth * needleHeightRatio);

        mNeedleBitmap = mNeedleBitmap.createScaledBitmap(mNeedleBitmap, needleWidth, needleHeight,
                true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int needlePositionHeight = (canvas.getHeight() / 2) - (mNeedleBitmap.getHeight() / 2);
        int needlePositionWidth = (canvas.getWidth() / 2) - (mNeedleBitmap.getWidth() / 2);

        canvas.drawBitmap(mCompassBitmap, 0, 0, mCompassPaint);
        canvas.drawBitmap(
                mNeedleBitmap,
                rotateBitmap(mNeedleBitmap, needlePositionWidth, needlePositionHeight),
                mNeedlePaint);
    }

    private Matrix rotateBitmap(Bitmap bitmap, int x, int y) {
        Matrix matrix = new Matrix();
        matrix.postRotate(mDegrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        matrix.postTranslate(x, y);
        return matrix;
    }

    public void setDegrees(float degrees) {
        mDegrees = degrees;
        AccessibilityManager accessibilityManager =
                (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager.isEnabled()) {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
        }
        invalidate();
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        event.getText().add(Utility.getAccessibleWindDirection(getContext(), mDegrees));
        return true;
    }
}
