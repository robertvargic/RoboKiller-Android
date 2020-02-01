package co.teltech.callblocker.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import co.teltech.callblocker.R;
import co.teltech.callblocker.client.RestClient;
import co.teltech.callblocker.client.constants.ResponseConstants;
import co.teltech.callblocker.client.models.CheckNumberRequest;
import co.teltech.callblocker.client.models.CheckNumberResponse;
import co.teltech.callblocker.constants.PrefsKeys;
import co.teltech.callblocker.dto.BlockedCall;
import co.teltech.callblocker.dto.ListNumber;
import co.teltech.callblocker.events.NewBlockedOrSuspiciousCallEvent;
import co.teltech.callblocker.utils.BlockListUtil;
import co.teltech.callblocker.utils.PermissionUtils;
import co.teltech.callblocker.utils.PrefsUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by tomislavtusek on 16/08/2018.
 */

public class CallBlockerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        boolean blockCalls = PermissionUtils.areCallPermissionsGranted(context) && PrefsUtils.getBoolean(context, PrefsKeys.BLOCK_CALLS_ENABLED);

        if (!blockCalls) {
            return;
        }

        Bundle bundle = intent.getExtras();
        String state = bundle.getString(TelephonyManager.EXTRA_STATE);
        final String incomingNumber = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

        if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
            if (incomingNumber != null) {
                String selectedFilterType = PrefsUtils.getSelectedFilterType(context);
                if (selectedFilterType.equalsIgnoreCase(PrefsKeys.BLOCK_CALLS_TYPE_SPAM_ONLY)) {
                    /**
                     * This takes time so it should be better to implement periodical refresh
                     * of SPAM and SUSPICIOUS numbers. The lists could be pulled from the server
                     * and kept locally so this check can be done faster.
                     */
                    CheckNumberRequest checkNumberRequest = new CheckNumberRequest(incomingNumber);
                    Call<CheckNumberResponse> checkNumberRequestCall = RestClient.getApiService(context).checkNumber(checkNumberRequest);
                    checkNumberRequestCall.enqueue(new Callback<CheckNumberResponse>() {
                        @Override
                        public void onResponse(Call<CheckNumberResponse> call, Response<CheckNumberResponse> response) {
                            CheckNumberResponse checkNumberResponse = response.body();
                            if (checkNumberResponse.getNumberType().equalsIgnoreCase(ResponseConstants.NUMBER_TYPE_SPAM)) {
                                endCall(context);
                                saveCallToBlockedCallsList(context, incomingNumber);
                            } else if (checkNumberResponse.getNumberType().equalsIgnoreCase(ResponseConstants.NUMBER_TYPE_SUSPICIOUS)) {
                                Toast toast = Toast.makeText(context,context.getString(R.string.toast_suspicious_call), Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                saveCallToSuspiciousCallsList(context, incomingNumber);
                            } else {
                                checkCallerId(context, incomingNumber);
                            }
                        }

                        @Override
                        public void onFailure(Call<CheckNumberResponse> call, Throwable t) {
                            Logger.e(t, t.getMessage());

                            // Since the API endpoint doesn't exist, this is used for testing purposes only.
                            String numberType = ResponseConstants.NUMBER_TYPE_OK;
                            if (incomingNumber.equalsIgnoreCase("4259501212")) {
                                numberType = ResponseConstants.NUMBER_TYPE_SUSPICIOUS;
                            } else if (incomingNumber.equalsIgnoreCase("2539501212")) {
                                numberType = ResponseConstants.NUMBER_TYPE_SPAM;
                            }

                            CheckNumberResponse checkNumberResponse = new CheckNumberResponse(numberType);
                            if (checkNumberResponse.getNumberType().equalsIgnoreCase(ResponseConstants.NUMBER_TYPE_SPAM)) {
                                endCall(context);
                                saveCallToBlockedCallsList(context, incomingNumber);
                            } else if (checkNumberResponse.getNumberType().equalsIgnoreCase(ResponseConstants.NUMBER_TYPE_SUSPICIOUS)) {
                                Toast toast = Toast.makeText(context,context.getString(R.string.toast_suspicious_call), Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                saveCallToSuspiciousCallsList(context, incomingNumber);
                            } else {
                                checkCallerId(context, incomingNumber);
                            }
                        }
                    });
                } else if (selectedFilterType.equalsIgnoreCase(PrefsKeys.BLOCK_CALLS_TYPE_NOT_IN_CONTACTS)) {
                    if (PermissionUtils.areContactsPermissionsGranted(context) && !isNumberInContactList(context, incomingNumber)) {
                        endCall(context);
                        saveCallToBlockedCallsList(context, incomingNumber);
                    } else {
                        checkCallerId(context, incomingNumber);
                    }
                } else if (selectedFilterType.equalsIgnoreCase(PrefsKeys.BLOCK_CALLS_TYPE_BLACKLIST)) {
                    List<ListNumber> blacklist = BlockListUtil.loadBlackList(context);
                    if (BlockListUtil.isNumberInList(blacklist, incomingNumber)) {
                        endCall(context);
                        saveCallToBlockedCallsList(context, incomingNumber);
                    } else {
                        checkCallerId(context, incomingNumber);
                    }
                } else if (selectedFilterType.equalsIgnoreCase(PrefsKeys.BLOCK_CALLS_TYPE_WHITELIST)) {
                    List<ListNumber> whitelist = BlockListUtil.loadWhitelist(context);
                    if (!BlockListUtil.isNumberInList(whitelist, incomingNumber)) {
                        endCall(context);
                        saveCallToBlockedCallsList(context, incomingNumber);
                    } else {
                        checkCallerId(context, incomingNumber);
                    }
                }
            }
        }
    }

    private void checkCallerId(Context context, String incomingNumber) {
        /**
         * Unfortunately, wasn't able to find a Free CNAM Lookup service.
         * Found some trials but couldn't register for them with my Gmail account (OpenCNAM),
         * or couldn't finish the registration for the free version with my Croatian phone number (TrueCNAM).
         */
    }

    private void saveCallToBlockedCallsList(Context context, String incomingNumber) {
        BlockedCall blockedCall = new BlockedCall(incomingNumber, new Date());
        List<BlockedCall> blockedCalls = BlockListUtil.loadBlockedCallsList(context);
        blockedCalls.add(0, blockedCall);
        BlockListUtil.saveBlockedCallsList(context, blockedCalls);

        EventBus.getDefault().post(new NewBlockedOrSuspiciousCallEvent());
    }

    private void saveCallToSuspiciousCallsList(Context context, String incomingNumber) {
        BlockedCall suspiciousCall = new BlockedCall(incomingNumber, new Date());
        List<BlockedCall> suspiciousCalls = BlockListUtil.loadSuspiciousCallsList(context);
        suspiciousCalls.add(0, suspiciousCall);
        BlockListUtil.saveSuspiciousCallsList(context, suspiciousCalls);

        EventBus.getDefault().post(new NewBlockedOrSuspiciousCallEvent());
    }

    private void endCall(Context context) {
        Toast toast = Toast.makeText(context, context.getString(R.string.toast_blocking_call), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
            telecomManager.endCall();
        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            try {
                Class theClass = Class.forName(tm.getClass().getName());
                Method m = theClass.getDeclaredMethod("getITelephony");
                m.setAccessible(true);
                com.android.internal.telephony.ITelephony service = (ITelephony)m.invoke(tm);
                service.endCall();
            } catch (Exception e) {
                Log.e("CALL_BLOCKER", "Exception while blocking a call", e);
            }
        }
    }

    private boolean isNumberInContactList(Context context, String number) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] phoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(lookupUri, phoneNumberProjection, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return true;
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return false;
    }

}
