package easily.tech.guideview.lib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import static easily.tech.guideview.lib.GuideViewBundle.Direction.BOTTOM;
import static easily.tech.guideview.lib.GuideViewBundle.Direction.LEFT;
import static easily.tech.guideview.lib.GuideViewBundle.Direction.RIGHT;
import static easily.tech.guideview.lib.GuideViewBundle.Direction.TOP;
import static easily.tech.guideview.lib.GuideViewBundle.TransparentOutline.TYPE_OVAL;
import static easily.tech.guideview.lib.GuideViewBundle.TransparentOutline.TYPE_RECT;

/**
 * The real guideView item
 * It's response to:
 * 1. draw the mask background and the transparent focus area
 * 2. draw the hint guide view
 * This class is not visible outside the lib,And you don't need to care about what it is anymore
 * <p>
 * Created by lemon on 2018/4/16.
 */
@SuppressLint("ViewConstructor")
final class GuideView extends RelativeLayout {

    private boolean hasAddHintView = false;
    public boolean isShowing = false;
    private int[] targetViewLocation = new int[2];
    private int targetViewWidth;
    private int targetViewHeight;
    private int screenWidth;
    private int screenHeight;
    // paint for drawing the background mask
    private Paint backgroundPaint;
    // paint for drawing the transparent focus area
    private Paint transparentPaint;
    private GuideViewBundle bundle;
    private FrameLayout decorView;

    GuideView(Context context, GuideViewBundle bundle) {
        super(context);
        this.bundle = bundle;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        backgroundPaint = new Paint();
        transparentPaint = new Paint();
        backgroundPaint.setColor(bundle.getMaskColor());
        decorView = (FrameLayout) ((Activity) getContext()).getWindow().getDecorView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bundle == null) {
            return;
        }
        drawBackGround(canvas);
    }

    private void drawBackGround(Canvas canvas) {
        Bitmap bitmap;
        //先绘制遮罩层
        if (canvas.getWidth() == 0 || canvas.getHeight() == 0) {
            bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas backGround = new Canvas(bitmap);
        backGround.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);
        PorterDuffXfermode mDrawMode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        transparentPaint.setXfermode(mDrawMode);
        transparentPaint.setAntiAlias(true);
        if (bundle.isHasTransparentLayer()) {
            int extraHeight = Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT ? Utils.getStatusBarHeight(getContext()) : 0;
            float left = targetViewLocation[0] - bundle.getTransparentSpaceLeft();
            float top = targetViewLocation[1] - bundle.getTransparentSpaceTop() - extraHeight;
            float right = targetViewLocation[0] + targetViewWidth + bundle.getTransparentSpaceRight();
            float bottom = targetViewLocation[1] + targetViewHeight + bundle.getTransparentSpaceBottom() - extraHeight;
            RectF rectF = new RectF(left, top, right, bottom);
            switch (bundle.getOutlineType()) {
                case TYPE_OVAL:
                    backGround.drawOval(rectF, transparentPaint);
                    break;
                case TYPE_RECT:
                    backGround.drawRect(rectF, transparentPaint);
                    break;
                default:
                    backGround.drawOval(rectF, transparentPaint);
            }
        }
        canvas.drawBitmap(bitmap, 0, 0, backgroundPaint);
    }


    private void addHintView() {
        if (hasAddHintView || bundle.getHintView() == null) {
            return;
        }

        RelativeLayout.LayoutParams params = bundle.getHintViewParams() == null ? new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) : bundle.getHintViewParams();
        int left, top, right, bottom;
        left = top = right = bottom = 0;
        int gravity = Gravity.TOP | Gravity.START;
        int viewHeight = decorView.getHeight();
        // below android 4.4,it can not set the status bar transparent,so we need the calculate the height of it to settle the hintView on correct position
        int extraHeight = Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT ? Utils.getStatusBarHeight(getContext()) : 0;
        switch (bundle.getHintViewDirection()) {
            case LEFT:
                gravity = Gravity.END;
                top = targetViewLocation[1] + bundle.getHintViewMarginTop();
                right = screenWidth - targetViewLocation[0] + bundle.getHintViewMarginRight() + bundle.getTransparentSpaceLeft() ;
                break;
            case RIGHT:
                gravity = Gravity.START;
                top = targetViewLocation[1] + bundle.getHintViewMarginTop();
                left = targetViewLocation[0] + targetViewWidth + bundle.getHintViewMarginLeft() + bundle.getTransparentSpaceRight() ;
                break;
            case TOP:
                gravity = Gravity.BOTTOM;
                bottom = viewHeight - targetViewLocation[1] + bundle.getHintViewMarginBottom() + bundle.getTransparentSpaceTop();
                left = targetViewLocation[0] + bundle.getHintViewMarginLeft();
                break;
            case BOTTOM:
                gravity = Gravity.TOP;
                top = targetViewLocation[1] + targetViewHeight + bundle.getHintViewMarginTop() + bundle.getTransparentSpaceBottom() - extraHeight;
                left = targetViewLocation[0] + bundle.getHintViewMarginLeft();
                break;
        }
        setGravity(gravity);
        params.setMargins(left, top, right, bottom);
        if (bundle.getHintView().getParent() != null) {
            bundle.getHintView().setLayoutParams(params);
        } else {
            this.addView(bundle.getHintView(), params);
        }
        hasAddHintView = true;
    }

    private boolean getTargetViewPosition() {
        View targetView = bundle.getTargetView();
        if (targetView.getWidth() > 0 && targetView.getHeight() > 0) {
            targetView.getLocationInWindow(targetViewLocation);
            targetViewWidth = targetView.getWidth();
            targetViewHeight = targetView.getHeight();
            if (targetViewLocation[0] >= 0 && targetViewLocation[1] > 0) {
                return true;
            }
        }
        return false;
    }

    public void show() {
        if (bundle.getTargetView() == null) {
            return;
        }
        // add hintView in the post callback ,make sure that the targetView had been measured
        bundle.getTargetView().post(new Runnable() {
            @Override
            public void run() {
                showReal();
            }
        });
    }


    private void showReal() {
        boolean hasMeasure = getTargetViewPosition();
        if (isShowing || !hasMeasure) {
            return;
        }
        addHintView();
        this.setBackgroundColor(Color.TRANSPARENT);
        // set the container background to transparent
        if (getParent() != null && getParent() instanceof View) {
            ((View) getParent()).setBackgroundColor(Color.TRANSPARENT);
        }
        isShowing = true;
    }

    public void hide() {
        this.removeAllViews();
        if (getParent() != null && getParent() instanceof ViewGroup) {
            ((ViewGroup) getParent()).removeView(this);
        }
    }
}
