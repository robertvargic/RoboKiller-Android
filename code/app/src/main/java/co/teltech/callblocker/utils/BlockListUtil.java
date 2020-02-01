package co.teltech.callblocker.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import co.teltech.callblocker.constants.PrefsKeys;
import co.teltech.callblocker.dto.BlockedCall;
import co.teltech.callblocker.dto.ListNumber;

/**
 * Created by tomislavtusek on 17/08/2018.
 */

public class BlockListUtil {

    public static List<ListNumber> loadBlackList(Context context) {
        List<ListNumber> blacklist =  DataPersistUtil.load(context, PrefsKeys.FILE_NAME_BLACKLIST, List.class);
        if (blacklist == null) {
            blacklist = new ArrayList<>();
        }
        return blacklist;
    }

    public static void saveBlackList(Context context, List<ListNumber> blacklist) {
        DataPersistUtil.store(context, PrefsKeys.FILE_NAME_BLACKLIST, blacklist);
    }

    public static List<ListNumber> loadWhitelist(Context context) {
        List<ListNumber> whitelist = DataPersistUtil.load(context, PrefsKeys.FILE_NAME_WHITELIST, List.class);
        if (whitelist == null) {
            whitelist = new ArrayList<>();
        }
        return whitelist;
    }

    public static void saveWhitelist(Context context, List<ListNumber> whitelist) {
        DataPersistUtil.store(context, PrefsKeys.FILE_NAME_WHITELIST, whitelist);
    }

    public static List<BlockedCall> loadBlockedCallsList(Context context) {
        List<BlockedCall> blockedCalls = DataPersistUtil.load(context, PrefsKeys.FILE_NAME_BLOCKED_CALLS_LIST, List.class);
        if (blockedCalls == null) {
            blockedCalls = new ArrayList<>();
        }
        return blockedCalls;
    }

    public static void saveBlockedCallsList(Context context, List<BlockedCall> blockedCalls) {
        DataPersistUtil.store(context, PrefsKeys.FILE_NAME_BLOCKED_CALLS_LIST, blockedCalls);
    }

    public static List<BlockedCall> loadSuspiciousCallsList(Context context) {
        List<BlockedCall> suspiciousCalls = DataPersistUtil.load(context, PrefsKeys.FILE_NAME_SUSPICIOUS_CALLS_LIST, List.class);
        if (suspiciousCalls == null) {
            suspiciousCalls = new ArrayList<>();
        }
        return suspiciousCalls;
    }

    public static void saveSuspiciousCallsList(Context context, List<BlockedCall> suspiciousCalls) {
        DataPersistUtil.store(context, PrefsKeys.FILE_NAME_SUSPICIOUS_CALLS_LIST, suspiciousCalls);
    }

    public static boolean isNumberInList(List<ListNumber> list, String incomingNumber) {
        for (ListNumber listNumber : list) {
            if (listNumber.getNumber().equalsIgnoreCase(incomingNumber)) {
                return true;
            }
        }
        return false;
    }

}
