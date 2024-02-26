package com.emenjivar.luminar.ext

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

val Context.settingsIntent: Intent
    get() = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        val uri = Uri.fromParts("package", this@settingsIntent.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        data = uri
    }
