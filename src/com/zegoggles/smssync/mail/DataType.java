package com.zegoggles.smssync.mail;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import com.zegoggles.smssync.R;

public enum DataType {
    SMS     (R.string.sms,      R.string.sms_with_field,     PreferenceKeys.IMAP_FOLDER,          Defaults.SMS_FOLDER,     PreferenceKeys.BACKUP_SMS,      true,  PreferenceKeys.RESTORE_SMS,     PreferenceKeys.MAX_SYNCED_DATE_SMS,      -1),
    MMS     (R.string.mms,      R.string.mms_with_field,     PreferenceKeys.IMAP_FOLDER,          Defaults.SMS_FOLDER,     PreferenceKeys.BACKUP_MMS,      false, null,                           PreferenceKeys.MAX_SYNCED_DATE_MMS,      Build.VERSION_CODES.ECLAIR),
    CALLLOG (R.string.calllog,  R.string.call_with_field,    PreferenceKeys.IMAP_FOLDER_CALLLOG,  Defaults.CALLLOG_FOLDER, PreferenceKeys.BACKUP_CALLLOG,  false, PreferenceKeys.RESTORE_CALLLOG, PreferenceKeys.MAX_SYNCED_DATE_CALLLOG,  -1),
    WHATSAPP(R.string.whatsapp, R.string.whatsapp_with_field,PreferenceKeys.IMAP_FOLDER_WHATSAPP, Defaults.WHATAPP_FOLDER, PreferenceKeys.BACKUP_WHATSAPP, false, null,                           PreferenceKeys.MAX_SYNCED_DATE_WHATSAPP, -1);

    public final int resId;
    public final int withField;
    public final String backupEnabledPreference;
    public final String restoreEnabledPreference;
    public final String folderPreference;
    public final String defaultFolder;
    public final int minSdkVersion;
    public final boolean backupEnabledByDefault;
    private final String maxSyncedPreference;

    private DataType(int resId,
                     int withField,
                     String folderPreference,
                     String defaultFolder,
                     String backupEnabledPreference,
                     boolean backupEnabledByDefault,
                     String restoreEnabledPreference,
                     String maxSyncedPreference,
                     int minSdkVersion) {
        this.resId = resId;
        this.withField = withField;
        this.folderPreference = folderPreference;
        this.defaultFolder = defaultFolder;
        this.backupEnabledPreference = backupEnabledPreference;
        this.backupEnabledByDefault = backupEnabledByDefault;
        this.restoreEnabledPreference = restoreEnabledPreference;
        this.maxSyncedPreference = maxSyncedPreference;
        this.minSdkVersion = minSdkVersion;
    }

    public boolean isBackupEnabled(Context context) {
        //noinspection SimplifiableIfStatement
        if (minSdkVersion > 0 && Build.VERSION.SDK_INT > minSdkVersion) {
            return false;
        } else {
            return prefs(context)
                    .getBoolean(backupEnabledPreference, backupEnabledByDefault);
        }
    }

    public void setBackupEnabled(Context context, boolean enabled) {
        prefs(context)
                .edit()
                .putBoolean(backupEnabledPreference, enabled)
                .commit();
    }

    public boolean isRestoreEnabled(Context context) {
        return restoreEnabledPreference != null &&
                prefs(context).getBoolean(restoreEnabledPreference, false);
    }

    public String getFolder(Context context) {
        return prefs(context).getString(folderPreference, defaultFolder);
    }

    public long getMaxSyncedDate(Context context) {
        return prefs(context).getLong(maxSyncedPreference, Defaults.MAX_SYNCED_DATE);
    }

    public boolean setMaxSyncedDate(Context context, long max) {
        return prefs(context).edit().putLong(maxSyncedPreference, max).commit();
    }

    private SharedPreferences prefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static long getMostRecentSyncedDate(Context ctx) {
        return Math.max(Math.max(
                SMS.getMaxSyncedDate(ctx),
                CALLLOG.getMaxSyncedDate(ctx)),
                MMS.getMaxSyncedDate(ctx) * 1000);
    }

    public static void clearLastSyncData(Context ctx) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        for (DataType type : values()) {
            editor.remove(type.maxSyncedPreference);
        }
        editor.commit();
    }

    public static class PreferenceKeys {
        public static final String IMAP_FOLDER = "imap_folder";
        public static final String IMAP_FOLDER_CALLLOG = "imap_folder_calllog";
        public static final String IMAP_FOLDER_WHATSAPP = "imap_folder_whatsapp";

        public static final String BACKUP_SMS = "backup_sms";
        public static final String BACKUP_MMS = "backup_mms";
        public static final String BACKUP_CALLLOG = "backup_calllog";
        public static final String BACKUP_WHATSAPP = "backup_whatsapp";

        public static final String RESTORE_SMS = "restore_sms";
        public static final String RESTORE_CALLLOG = "restore_calllog";

        public static final String MAX_SYNCED_DATE_SMS = "max_synced_date";
        public static final String MAX_SYNCED_DATE_MMS = "max_synced_date_mms";
        public static final String MAX_SYNCED_DATE_CALLLOG = "max_synced_date_calllog";
        public static final String MAX_SYNCED_DATE_WHATSAPP = "max_synced_date_whatsapp";
    }

    public static class Defaults {
        public static final long   MAX_SYNCED_DATE = -1;
        public static final String SMS_FOLDER     = "SMS";
        public static final String CALLLOG_FOLDER = "Call log";
        public static final String WHATAPP_FOLDER = "WhatsApp";
    }
}
