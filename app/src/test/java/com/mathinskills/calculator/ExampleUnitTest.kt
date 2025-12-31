package com.mathinskills.calculator

import android.view.LayoutInflater
import android.widget.TextView
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}


class ClassTestPIXEL6PRO {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6_PRO.copy(softButtons = false)
    )
    @Test
    fun snapshot_item_calculate() {
        val context = paparazzi.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_calculate, null, false)

        // 필요 시 TextView 등 설정도 가능
        view.findViewById<TextView>(R.id.tv_course)?.text = "초등부"
        view.findViewById<TextView>(R.id.tv_subject)?.text = "수학"
        view.findViewById<TextView>(R.id.tv_onceMinutes)?.text = "130"
        view.findViewById<TextView>(R.id.tv_weekTimes)?.text = "2"
        view.findViewById<TextView>(R.id.tv_rate)?.text = "168원"
        view.findViewById<TextView>(R.id.tv_standardRate)?.text = "169원"

        paparazzi.snapshot(view, "item_calculate")
    }
}