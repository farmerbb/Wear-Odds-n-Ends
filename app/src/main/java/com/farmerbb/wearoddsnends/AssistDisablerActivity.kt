package com.farmerbb.wearoddsnends

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle

class AssistDisablerActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.extras?.let {
            val pkg = "com.google.android.googlequicksearchbox"
            val component = "com.google.android.apps.gsa.binaries.clockwork.assistant.AssistantActivity"

            val intent = Intent(Intent.ACTION_ASSIST)
            intent.component = ComponentName.unflattenFromString("$pkg/$component")
            intent.putExtras(it)

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) { /* Gracefully fail */ }
        }

        finish()
    }
}
