/* Copyright 2018 Braden Farmer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.farmerbb.wearoddsnends

import android.content.ComponentName
import android.os.Handler
import android.os.PowerManager
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.ComplicationManager
import android.support.wearable.complications.ComplicationProviderService
import android.support.wearable.complications.ComplicationText
import android.support.wearable.complications.ProviderUpdateRequester

import java.time.LocalDateTime
import java.util.Locale
import android.app.ActivityManager
import java.time.ZoneOffset

class SecondsProviderService: ComplicationProviderService() {

    override fun onComplicationUpdate(complicationId: Int, type: Int, manager: ComplicationManager) {
        val sharedPrefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val offset = if(sharedPrefs.getBoolean("offset_seconds", false)) 1L else 0
        val now = LocalDateTime.now().plusSeconds(offset)
        val weekday = resources.getStringArray(R.array.days_of_week)[now.dayOfWeek.ordinal]

        val seconds = ":${String.format(Locale.US, "%02d", now.second)}"
        val data = ComplicationData.Builder(type)
                .setShortText(ComplicationText.plainText(seconds))
                .setShortTitle(ComplicationText.plainText(weekday))
                .build()

        manager.updateComplicationData(complicationId, data)

        val nextUpdate = now.withNano(0).plusSeconds(1).toEpochSecond(ZoneOffset.UTC)
        val nowEpoch = now.toEpochSecond(ZoneOffset.UTC)
        if(shouldRequestUpdate()) {
            Handler().postDelayed({
                val provider = ComponentName(this, javaClass)
                val requester = ProviderUpdateRequester(this, provider)
                requester.requestUpdateAll()
            }, nextUpdate - nowEpoch)
        }
    }

    @Suppress("deprecation")
    private fun shouldRequestUpdate(): Boolean {
        val powerManager = getSystemService(PowerManager::class.java)
        val activityManager = getSystemService(ActivityManager::class.java)

        return if(activityManager.getRunningServices(Int.MAX_VALUE).any {
            NotificationService::class.java.name == it.service.className
        }) powerManager.isInteractive else true
    }
}