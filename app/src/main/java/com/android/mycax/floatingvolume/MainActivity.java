package com.android.mycax.floatingvolume;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import com.android.mycax.floatingvolume.utils.AppUtils;
import com.basel.DualButton.DualButton;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatPreferenceActivity implements SwitchPreference.OnPreferenceChangeListener, DualButton.OnDualClickListener {
    private DualButton FloatingService;
    private SwitchPreference bounceEffect;
    private static final String PREF_ENABLE_DARK_MODE = "enable_dark_mode_switch";
    private static final String PREF_ENABLE_BOUNCE = "enable_bounce_effect";
    private static final String PREF_DISABLE_FIXED_UI = "disable_fixed_ui";
    private static final int OVERLAY_PERMISSION_REQUEST = 1;
    private static final int NOTIFICATION_POLICY_PERMISSION_REQUEST = 2;
    private AppUtils utils;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_ENABLE_DARK_MODE, false) ? R.style.AppTheme_Dark : R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        utils = new AppUtils(this);
        FloatingService = findViewById(R.id.dualBtn);

        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Settings.canDrawOverlays(this) && Objects.requireNonNull(notificationManager).isNotificationPolicyAccessGranted()) {
            initializeView();
        } else {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST);
            }
            if (!Objects.requireNonNull(notificationManager).isNotificationPolicyAccessGranted()) {
                Intent intent = new Intent(
                        android.provider.Settings
                                .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

                startActivityForResult(intent, NOTIFICATION_POLICY_PERMISSION_REQUEST);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
        SwitchPreference darkMode = (SwitchPreference) findPreference(PREF_ENABLE_DARK_MODE);
        darkMode.setOnPreferenceChangeListener(this);
        bounceEffect = (SwitchPreference) findPreference(PREF_ENABLE_BOUNCE);
        if (!PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_DISABLE_FIXED_UI, false)) {
            bounceEffect.setEnabled(false);
        }
        SwitchPreference disableFixedUI = (SwitchPreference) findPreference(PREF_DISABLE_FIXED_UI);
        disableFixedUI.setOnPreferenceChangeListener(this);
    }

    private void initializeView() {
        FloatingService.setDualClickListener(this);
    }

    @Override
    public void onClickFirst(Button btn) {
        utils.manageService(true);
    }

    @Override
    public void onClickSecond(Button btn) {
        utils.manageService(false);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object object) {
        if (preference == findPreference(PREF_ENABLE_DARK_MODE)) {
            finish();
            final Intent intent = getIntent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (preference == findPreference(PREF_DISABLE_FIXED_UI)) {
            bounceEffect.setEnabled(!PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean(PREF_DISABLE_FIXED_UI, false));
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQUEST || requestCode == NOTIFICATION_POLICY_PERMISSION_REQUEST) {
            if (Settings.canDrawOverlays(this) && Objects.requireNonNull(notificationManager).isNotificationPolicyAccessGranted()) {
                initializeView();
            } else {
                Toast.makeText(this, R.string.app_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
