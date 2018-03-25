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

import org.joda.time.DateTime

import java.util.Locale

class SecondsProviderService: ComplicationProviderService() {

    override fun onComplicationUpdate(complicationId: Int, type: Int, manager: ComplicationManager) {
        val now = DateTime.now().plusSeconds(1)
        val weekday = resources.getStringArray(R.array.days_of_week)[now.dayOfWeek - 1]

        val seconds = ":${String.format(Locale.US, "%02d", now.secondOfMinute)}"
        val data = ComplicationData.Builder(type)
                .setShortText(ComplicationText.plainText(seconds))
                .setShortTitle(ComplicationText.plainText(weekday))
                .build()

        manager.updateComplicationData(complicationId, data)

        val delay = now.withMillisOfSecond(0).millis - now.minusSeconds(1).millis
        val pms = getSystemService(PowerManager::class.java)

        if(pms.isInteractive) {
            Handler().postDelayed({
                val provider = ComponentName(this, javaClass)
                val requester = ProviderUpdateRequester(this, provider)
                requester.requestUpdateAll()
            }, delay)
        }
    }
}