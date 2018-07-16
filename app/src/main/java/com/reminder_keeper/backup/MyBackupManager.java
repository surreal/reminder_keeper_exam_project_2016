package com.reminder_keeper.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.reminder_keeper.activities.MainActivity;

import java.io.IOException;

public class MyBackupManager extends BackupAgentHelper
{
    @Override
    public void onCreate()
    { super.onCreate();
        FileBackupHelper fileBackupHelper = new FileBackupHelper(this, "../databases/remindersDB.db");
        addHelper("remindersDataBase", fileBackupHelper);
    }
    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException
    { super.onBackup(oldState, data, newState); }
    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException
    { super.onRestore(data, appVersionCode, newState); }
}
