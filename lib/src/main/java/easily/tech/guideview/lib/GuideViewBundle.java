package easily.tech.guideview.lib;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;

import static easily.tech.guideview.lib.GuideViewBundle.TransparentOutline.TYPE_OVAL;

/**
 * A configuration about the guide view
 * You are supposed to build a {@link GuideViewBundle} instance using {@link Builder} to show a guideView on screen
 * <p>
 * Created by lemon on 2018/4/16.
 */
public class GuideViewBundle {

    /**
     * direction between the hintView and targetView
     */
    public interface Direction {
        /**
         * the hintView will align left to the targetView,which top align with it meanwhile
         */
        int LEFT = 0x0001;
        /**
         * the hintView will align right to the targetView,which top align with it meanwhile
         */
        int RIGHT = 0x0002;
        /**
         * the hintView will align top to the targetView,which left align with it meanwhile
         */
        int TOP = 0x0003;
        /**
         * the hintView will align bottom to the targetView,which left align with it meanwhile
         */
        int BOTTOM = 0x0004;
    }

    public interface GuideViewHideListener {
        void onGuideViewHide();
    }


    /**
     * Transparent focus area outline type
     */
    public interface TransparentOutline {
        int TYPE_OVAL = 0;
        int TYPE_RECT = 1;
    }

    private Builder config;

    private GuideViewBundle(@NonNull Builder config) {
        this.config = config;
    }

    public View getTargetView() {
        return config.targetView;
    }

    public View getHintView() {
        return config.hintView;
    }

    public int getTransparentSpaceLeft() {
        return config.transparentSpaceLeft;
    }

    public int getTransparentSpaceRight() {
        return config.transparentSpaceRight;
    }

    public int getTransparentSpaceTop() {
        return config.transparentSpaceTop;
    }

    public int getTransparentSpaceBottom() {
        return config.transparentSpaceBottom;
    }

    public int getHintViewMarginLeft() {
        return config.hintViewMarginLeft;
    }

    public int getHintViewMarginRight() {
        return config.hintViewMarginRight;
    }

    public int getHintViewMarginTop() {
        return config.hintViewMarginTop;
    }

    public int getHintViewMarginBottom() {
        return config.hintViewMarginBottom;
    }


    public RelativeLayout.LayoutParams getHintViewParams() {
        return config.hintViewParams;
    }

    public int getMaskColor() {
        return config.maskColor;
    }

    public boolean isHasTransparentLayer() {
        return config.hasTransparentLayer;
    }

    public int getHintViewDirection() {
        return config.hintViewDirection;
    }

    public int getOutlineType() {
        return config.outlineType;
    }

    public boolean isDismissOnClicked() {
        return config.isDismissOnClicked;
    }

    public boolean isTargetViewClickAble() {
        return config.isTargetViewClickable;
    }

    public boolean isDismissOnTouchInTargetView() {
        return config.isDismissOnClickTargetView;
    }

    public boolean condition() {
        return config.condition;
    }

    public GuideViewHideListener getGuideViewHideListener() {
        return config.guideViewHideListener;
    }

    public static class Builder {

        private static int MASK_LAYER_COLOR = 0xd9000000;
        // the guide hint view will align to
        private View targetView;
        // the assist guidView
        private View hintView;
        // default,the transparent area is fix the size of the targetView,if you need more space ,set this
        private int transparentSpaceLeft;
        private int transparentSpaceRight;
        private int transparentSpaceTop;
        private int transparentSpaceBottom;
        // default,the hint view is align the targetView based on the Direction
        private int hintViewMarginLeft;
        private int hintViewMarginRight;
        private int hintViewMarginTop;
        private int hintViewMarginBottom;

        private RelativeLayout.LayoutParams hintViewParams;
        // whether a transparent focus area is needed.If false only the mask background will be draw
        private boolean hasTransparentLayer = true;
        // whether click the whole screen can dismissed the guideView.If false,you need to handle the click and dismiss event yourself
        private boolean isDismissOnClicked = true;

        // set a condition,whether the added GuideViewBundle can be shown,default is true
        private boolean condition = true;

        private GuideViewHideListener guideViewHideListener;


        private boolean isDismissOnClickTargetView=true;

        private boolean isTargetViewClickable;


        private int hintViewDirection;

        private int outlineType = TYPE_OVAL;
        private int maskColor = MASK_LAYER_COLOR;

        public Builder setTargetView(View targetView) {
            this.targetView = targetView;
            return this;
        }

        public Builder setHintView(View hintView) {
            this.hintView = hintView;
            return this;
        }

        public Builder setTransparentSpace(int left, int top, int right, int bottom) {
            this.transparentSpaceLeft = left;
            this.transparentSpaceTop = top;
            this.transparentSpaceRight = right;
            this.transparentSpaceBottom = bottom;
            return this;
        }

        public Builder setHintViewMargin(int left, int top, int right, int bottom) {
            this.hintViewMarginLeft = left;
            this.hintViewMarginTop = top;
            this.hintViewMarginRight = right;
            this.hintViewMarginBottom = bottom;
            return this;
        }

        public Builder setHintViewParams(RelativeLayout.LayoutParams hintViewParams) {
            this.hintViewParams = hintViewParams;
            return this;
        }

        public Builder setHasTransparentLayer(boolean hasTransparentLayer) {
            this.hasTransparentLayer = hasTransparentLayer;
            return this;
        }

        public Builder setDismissOnClicked(boolean dismissOnClicked) {
            isDismissOnClicked = dismissOnClicked;
            return this;
        }

        public Builder setHintViewDirection(int hintViewDirection) {
            this.hintViewDirection = hintViewDirection;
            return this;
        }

        public Builder setOutlineType(int outlineType) {
            this.outlineType = outlineType;
            return this;
        }

        public Builder setMaskColor(int maskColor) {
            this.maskColor = maskColor;
            return this;
        }

        public Builder setTargetViewClickable(boolean targetViewClickAble) {
            isTargetViewClickable = targetViewClickAble;
            return this;
        }

        public Builder setDismissOnTouchInTargetView(boolean dismissOnTouchInTargetView) {
            isDismissOnClickTargetView = dismissOnTouchInTargetView;
            return this;
        }



        public Builder condition(boolean condition) {
            this.condition = condition;
            return this;
        }

        public Builder setGuideViewHideListener(GuideViewHideListener guideViewHideListener) {
            this.guideViewHideListener = guideViewHideListener;
            return this;
        }


        public GuideViewBundle build() {
            return new GuideViewBundle(this);
        }
    }

}
