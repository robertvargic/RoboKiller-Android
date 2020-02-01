package co.teltech.callblocker.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.teltech.callblocker.R;
import co.teltech.callblocker.constants.PrefsKeys;
import co.teltech.callblocker.dto.BlockedCall;
import co.teltech.callblocker.events.NewBlockedOrSuspiciousCallEvent;
import co.teltech.callblocker.utils.BlockListUtil;
import co.teltech.callblocker.utils.PermissionUtils;
import co.teltech.callblocker.utils.PrefsUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by tomislavtusek on 16/08/2018.
 */

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CALLS = 1;
    private static final int PERMISSION_REQUEST_CONTACTS = 2;
    private static final int PERMISSION_ANSWER_PHONE_CALLS = 3;

    @BindView(R.id.switchBlockCalls) Switch switchBlockCalls;
    @BindView(R.id.filterOptionsContainer) LinearLayout filterOptionsContainer;
    @BindView(R.id.radioOptionGroup) RadioGroup radioOptionGroup;
    @BindView(R.id.radioOptionSpamOnly) RadioButton radioOptionSpamOnly;
    @BindView(R.id.radioOptionNotContacts) RadioButton radioOptionNotContacts;
    @BindView(R.id.radioOptionBlacklist) RadioButton radioOptionBlacklist;
    @BindView(R.id.radioOptionWhitelist) RadioButton radioOptionWhitelist;
    @BindView(R.id.buttonEditBlacklist) Button buttonEditBlacklist;
    @BindView(R.id.buttonEditWhitelist) Button buttonEditWhitelist;
    @BindView(R.id.counterBlockedCalls) TextView counterBlockedCalls;
    @BindView(R.id.counterSuspiciousCalls) TextView counterSuspiciousCalls;

    private String filterTypeAfterContactsPermissionGranted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        switchBlockCalls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean enabled) {
                if (!enabled) {
                    PrefsUtils.putBoolean(MainActivity.this, PrefsKeys.BLOCK_CALLS_ENABLED, false);
                } else {
                    if (PermissionUtils.areCallPermissionsGranted(MainActivity.this)) {
                        PrefsUtils.putBoolean(MainActivity.this, PrefsKeys.BLOCK_CALLS_ENABLED, true);
                    } else {
                        compoundButton.setChecked(false);
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG}, PERMISSION_REQUEST_CALLS);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ANSWER_PHONE_CALLS}, PERMISSION_ANSWER_PHONE_CALLS);
                        }
                    }
                }

                toggleFilterOptionsContainer();
            }
        });

        radioOptionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.radioOptionSpamOnly:
                        buttonEditBlacklist.setVisibility(View.GONE);
                        buttonEditWhitelist.setVisibility(View.GONE);
                        PrefsUtils.putString(MainActivity.this, PrefsKeys.BLOCK_CALLS_TYPE, PrefsKeys.BLOCK_CALLS_TYPE_SPAM_ONLY);
                        break;
                    case R.id.radioOptionNotContacts:
                        buttonEditBlacklist.setVisibility(View.GONE);
                        buttonEditWhitelist.setVisibility(View.GONE);
                        if (PermissionUtils.areContactsPermissionsGranted(MainActivity.this)) {
                            PrefsUtils.putString(MainActivity.this, PrefsKeys.BLOCK_CALLS_TYPE, PrefsKeys.BLOCK_CALLS_TYPE_NOT_IN_CONTACTS);
                        } else {
                            setSelectedFilterRadio();
                            filterTypeAfterContactsPermissionGranted = PrefsKeys.BLOCK_CALLS_TYPE_NOT_IN_CONTACTS;
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CONTACTS);
                        }
                        break;
                    case R.id.radioOptionBlacklist:
                        if (PermissionUtils.areContactsPermissionsGranted(MainActivity.this)) {
                            buttonEditBlacklist.setVisibility(View.VISIBLE);
                            buttonEditWhitelist.setVisibility(View.GONE);
                            PrefsUtils.putString(MainActivity.this, PrefsKeys.BLOCK_CALLS_TYPE, PrefsKeys.BLOCK_CALLS_TYPE_BLACKLIST);
                        } else {
                            setSelectedFilterRadio();
                            filterTypeAfterContactsPermissionGranted = PrefsKeys.BLOCK_CALLS_TYPE_BLACKLIST;
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CONTACTS);
                        }
                        break;
                    case R.id.radioOptionWhitelist:
                        if (PermissionUtils.areContactsPermissionsGranted(MainActivity.this)) {
                            buttonEditBlacklist.setVisibility(View.GONE);
                            buttonEditWhitelist.setVisibility(View.VISIBLE);
                            PrefsUtils.putString(MainActivity.this, PrefsKeys.BLOCK_CALLS_TYPE, PrefsKeys.BLOCK_CALLS_TYPE_WHITELIST);
                        } else {
                            setSelectedFilterRadio();
                            filterTypeAfterContactsPermissionGranted = PrefsKeys.BLOCK_CALLS_TYPE_WHITELIST;
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CONTACTS);
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!PermissionUtils.areCallPermissionsGranted(this)) {
            PrefsUtils.putBoolean(this, PrefsKeys.BLOCK_CALLS_ENABLED, false);
            switchBlockCalls.setChecked(false);
        } else {
            switchBlockCalls.setChecked(PrefsUtils.getBoolean(this, PrefsKeys.BLOCK_CALLS_ENABLED));
        }

        toggleFilterOptionsContainer();

        refreshCounters();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewBlockedOrSuspiciousCall(NewBlockedOrSuspiciousCallEvent event) {
        refreshCounters();
    }

    private void refreshCounters() {
        List<BlockedCall> blockedCalls = BlockListUtil.loadBlockedCallsList(this);
        List<BlockedCall> suspiciousCalls = BlockListUtil.loadSuspiciousCallsList(this);

        counterBlockedCalls.setText(Integer.toString(blockedCalls.size()));
        counterSuspiciousCalls.setText(Integer.toString(suspiciousCalls.size()));
    }

    private void toggleFilterOptionsContainer() {
        filterOptionsContainer.setVisibility(switchBlockCalls.isChecked() ? View.VISIBLE : View.GONE);

        setSelectedFilterRadio();
    }

    private void setSelectedFilterRadio() {
        String selectedFilterType = PrefsUtils.getSelectedFilterType(this);
        if (selectedFilterType.equalsIgnoreCase(PrefsKeys.BLOCK_CALLS_TYPE_SPAM_ONLY)) {
            radioOptionSpamOnly.setChecked(true);
        } else if (selectedFilterType.equalsIgnoreCase(PrefsKeys.BLOCK_CALLS_TYPE_NOT_IN_CONTACTS)) {
            radioOptionNotContacts.setChecked(true);
        } else if (selectedFilterType.equalsIgnoreCase(PrefsKeys.BLOCK_CALLS_TYPE_BLACKLIST)) {
            radioOptionBlacklist.setChecked(true);
        } else if (selectedFilterType.equalsIgnoreCase(PrefsKeys.BLOCK_CALLS_TYPE_WHITELIST)) {
            radioOptionWhitelist.setChecked(true);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CALLS:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            switchBlockCalls.setChecked(false);
                            return;
                        }
                    }
                    switchBlockCalls.setChecked(true);
                } else {
                    switchBlockCalls.setChecked(false);
                }
                return;
            case PERMISSION_REQUEST_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (filterTypeAfterContactsPermissionGranted != null) {
                        if (filterTypeAfterContactsPermissionGranted.equalsIgnoreCase(PrefsKeys.BLOCK_CALLS_TYPE_NOT_IN_CONTACTS)) {
                            radioOptionNotContacts.setChecked(true);
                        } else if (filterTypeAfterContactsPermissionGranted.equalsIgnoreCase(PrefsKeys.BLOCK_CALLS_TYPE_BLACKLIST)) {
                            radioOptionBlacklist.setChecked(true);
                        } else if (filterTypeAfterContactsPermissionGranted.equalsIgnoreCase(PrefsKeys.BLOCK_CALLS_TYPE_WHITELIST)) {
                            radioOptionWhitelist.setChecked(true);
                        }
                    }
                }
                return;
            case PERMISSION_ANSWER_PHONE_CALLS:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    switchBlockCalls.setChecked(false);
                }
        }
    }

    public void openListEditor(View v) {
        String type = "";
        switch (v.getId()) {
            case R.id.buttonEditBlacklist:
                type = ListEditorActivity.EXTRA_LIST_TYPE_BLACKLIST;
                break;
            case R.id.buttonEditWhitelist:
                type = ListEditorActivity.EXTRA_LIST_TYPE_WHITELIST;
                break;
        }

        Intent intent = new Intent(this, ListEditorActivity.class);
        intent.putExtra(ListEditorActivity.EXTRA_LIST_TYPE, type);
        startActivity(intent);
    }
    
    public void openCallsList(View v) {
        String type = "";
        switch (v.getId()) {
            case R.id.blockedCallsCard:
                type = CallsListActivity.EXTRA_LIST_TYPE_BLOCKED;
                break;
            case R.id.suspiciousCallsCard:
                type = CallsListActivity.EXTRA_LIST_TYPE_SUSPICIOUS;
                break;
        }
        Intent intent = new Intent(this, CallsListActivity.class);
        intent.putExtra(CallsListActivity.EXTRA_LIST_TYPE, type);
        startActivity(intent);
    }

}
