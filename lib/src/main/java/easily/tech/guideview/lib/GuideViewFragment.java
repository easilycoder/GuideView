package easily.tech.guideview.lib;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * show the guidView using {@link DialogFragment},it has such features:
 * 1.the guideView can be dismiss,when clicked the back-event key;
 * 2.control the guideView as a series
 * <p>
 * Created by lemon on 2018/4/16.
 */
public class GuideViewFragment extends DialogFragment {

    private List<GuideViewBundle> guideViewBundles = new ArrayList<>();
    private FrameLayout flContainer;
    private GuideViewBundle currentBundle;
    private GuideView currentGuideView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.GuideViewDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        flContainer = (FrameLayout) inflater.inflate(R.layout.layout_guide_container, container, false);
        return flContainer;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        Window window = dialog == null ? null : dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        showGuideView();
    }

    public void setGuideViewBundles(List<GuideViewBundle> guideViewBundles) {
        if (guideViewBundles == null || guideViewBundles.isEmpty()) {
            return;
        }
        this.guideViewBundles = guideViewBundles;
    }


    /**
     * when the guideView is clicked (or the item in guideView is clicked for next),this method need to be called
     * if the GuideView is allowed to dismissOnClicked (by setting {@link easily.tech.guideview.lib.GuideViewBundle.Builder#setDismissOnClicked(boolean)}),this method will be called automatically
     * otherwise,you need to handle it yourself.
     */
    public void onNext() {
        showGuideView();
    }

    /**
     * whether there is another guiView to show on next click
     *
     * @return
     */
    public boolean hasNext() {
        return guideViewBundles != null && !guideViewBundles.isEmpty();
    }

    private void showGuideView() {
        // remove the current guideView before showing next one
        if (currentGuideView != null && currentGuideView.isShowing) {
            // set the container background as the mask color,when the next guideView show,it will reset to transparent
            // in order to keep reduce the blinking in the interval
            flContainer.setBackgroundColor(currentBundle == null ? Color.TRANSPARENT : currentBundle.getMaskColor());
            currentGuideView.hide();
        }
        // if there is no available guideView,just dismiss the whole dialogFragment
        if (guideViewBundles == null || guideViewBundles.isEmpty()) {
            dismiss();
            return;
        }
        currentBundle = guideViewBundles.remove(0);
        if (currentBundle == null) {
            dismiss();
            return;
        }
        GuideView guideView = new GuideView(getContext(), currentBundle);
        wrapClickListener(guideView);
        guideView.setTargetViewClickListener(new GuideView.TargetViewClickListener() {
            @Override
            public void onGuideViewClicked() {
                if (currentBundle != null && currentBundle.isDismissOnTouchInTargetView()) {
                    dismiss();
                }
            }
        });
        flContainer.addView(guideView);
        guideView.show();
        currentGuideView = guideView;
    }

    private void wrapClickListener(View guideView) {
        if (currentBundle == null || !currentBundle.isDismissOnClicked()) {
            return;
        }
        guideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNext();
            }
        });
    }

    @Override
    public void dismiss() {
        if (getContext() instanceof Activity && !((Activity) getContext()).isFinishing() && getDialog() != null && getDialog().isShowing()) {
            if (flContainer != null) {
                flContainer.removeAllViews();
                currentBundle = null;
                currentGuideView = null;
            }
            super.dismiss();
        }
    }

    public static class Builder {

        private List<GuideViewBundle> guideViewBundles = new ArrayList<>();
        private boolean cancelable;

        public Builder addGuidViewBundle(GuideViewBundle bundle) {
            if (bundle == null) {
                return this;
            }
            guideViewBundles.add(bundle);
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public GuideViewFragment build() {
            GuideViewFragment fragment = new GuideViewFragment();
            fragment.setGuideViewBundles(guideViewBundles);
            fragment.setCancelable(cancelable);
            return fragment;
        }
    }

}
