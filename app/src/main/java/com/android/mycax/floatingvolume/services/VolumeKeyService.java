package com.android.mycax.floatingvolume.services;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.WindowManager;
import com.android.mycax.floatingvolume.utils.ExpandedVolumeDialog;

import java.util.Objects;

public class VolumeKeyService extends AccessibilityService {
    public VolumeKeyService() {
    }
    
    private ExpandedVolumeDialog expandedVolumeDialog;

    @Override
    public boolean onKeyEvent(KeyEvent event) {


        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

                expandedVolumeDialog = new ExpandedVolumeDialog(this);
                final DisplayMetrics metrics = new DisplayMetrics();
                final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

                windowManager.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
                windowManager.setType(WindowManager.LayoutParams.TYPE_VOLUME_OVERLAY);

                
                Objects.requireNonNull(windowManager).getDefaultDisplay().getMetrics(metrics);
                final LayoutInflater inflater = LayoutInflater.from(this);

                expandedVolumeDialog.expandView(inflater, metrics);
            }
            return true;
        } else {
            return super.onKeyEvent(event);
        }
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

}
