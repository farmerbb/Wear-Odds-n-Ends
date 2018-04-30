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

import android.content.Intent
import android.os.Bundle
import android.content.pm.PackageManager
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.wearable.activity.WearableActivity
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: WearableActivity() {

    lateinit var sharedPrefs: SharedPreferences

    inner class PreferenceAdapter(val dataset: List<Preference>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                = object: RecyclerView.ViewHolder(View.inflate(this@MainActivity, R.layout.row, null)) {}

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val pref = dataset[position]
            val view = holder.itemView

            val prefLabel = view.findViewById<TextView>(R.id.pref_label)
            prefLabel.setText(pref.stringResId)

            val prefSwitch = view.findViewById<Switch>(R.id.pref_switch)
            prefSwitch.isChecked = sharedPrefs.getBoolean(pref.key, pref.defaultValue)
            prefSwitch.setOnCheckedChangeListener { _, isChecked ->
                pref.onPrefChanged(isChecked)
                sharedPrefs.edit().putBoolean(pref.key, isChecked).apply()
            }
        }

        override fun getItemCount() = dataset.size
    }

    inner class TopBottomPaddingDecoration: RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
            val padding = resources.getDimensionPixelSize(R.dimen.top_bottom_recyclerview_padding)

            when(parent.getChildAdapterPosition(view)) {
                0 -> outRect.top = padding
                parent.adapter.itemCount -1 -> outRect.bottom = padding
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPrefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        if(sharedPrefs.getBoolean("return_to_home_screen_enabled", false))
            startForegroundService(Intent(this, NotificationService::class.java))

        recyclerView.setHasFixedSize(false)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(TopBottomPaddingDecoration())
        recyclerView.adapter = PreferenceAdapter(arrayListOf(
                Preference(
                        "seconds_complication_enabled",
                        R.string.enable_seconds_complication,
                        true,
                        { enabled -> onSecondsComplicationPrefChanged(enabled) }
                ),
                Preference(
                        "no_data_complication_enabled",
                        R.string.enable_no_data_complication,
                        false,
                        { enabled -> onNoDataComplicationPrefChanged(enabled) }
                ),
                Preference(
                        "return_to_home_screen_enabled",
                        R.string.enable_return_to_home_screen,
                        false,
                        { enabled -> onReturnToHomeScreenPrefChanged(enabled) }
                ),
                Preference(
                        "offset_seconds",
                        R.string.offset_seconds,
                        false,
                        { }
                )
        ))
    }

    class Preference(val key: String,
                     val stringResId: Int,
                     val defaultValue: Boolean,
                     val onPrefChanged: (Boolean) -> Unit)

    private fun onSecondsComplicationPrefChanged(enabled: Boolean) =
            packageManager.setComponentEnabledSetting(
                    ComponentName(this, SecondsProviderService::class.java),
                    if(enabled)
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    else
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
            )

    private fun onNoDataComplicationPrefChanged(enabled: Boolean) =
            packageManager.setComponentEnabledSetting(
                    ComponentName(this, NoDataProviderService::class.java),
                    if(enabled)
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    else
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
            )

    private fun onReturnToHomeScreenPrefChanged(enabled: Boolean) {
        if(enabled)
            startForegroundService(Intent(this, NotificationService::class.java))
        else
            stopService(Intent(this, NotificationService::class.java))
    }
}