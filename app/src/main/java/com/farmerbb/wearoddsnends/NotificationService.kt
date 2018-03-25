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

import android.app.Service
import android.content.*
import android.support.v4.app.NotificationCompat
import android.support.wearable.complications.ProviderUpdateRequester
import android.app.NotificationChannel
import android.app.NotificationManager

class NotificationService: Service() {
    private val screenOffReceiver: BroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(homeIntent)
        }
    }

    private val screenOnReceiver: BroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val provider = ComponentName(context, SecondsProviderService::class.java)
            val requester = ProviderUpdateRequester(context, provider)
            requester.requestUpdateAll()
        }
    }

    override fun onCreate() {
        val filter1 = IntentFilter()
        filter1.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenOffReceiver, filter1)

        val filter2 = IntentFilter()
        filter2.addAction(Intent.ACTION_SCREEN_ON)
        registerReceiver(screenOnReceiver, filter2)

        val name = getString(R.string.app_name)
        val importance = NotificationManager.IMPORTANCE_MIN

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(NotificationChannel("channel", name, importance))

        val builder = NotificationCompat.Builder(this, "channel")
                .setSmallIcon(R.drawable.ic_watch_black_24dp)
                .setContentTitle(name)
                .setContentText(getString(R.string.service_is_running))
                .setShowWhen(false)
                .setOngoing(true)

        startForeground(12345, builder.build())
    }

    override fun onDestroy() {
        unregisterReceiver(screenOffReceiver)
        unregisterReceiver(screenOnReceiver)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int) = Service.START_STICKY
    override fun onBind(arg0: Intent) = null
}
