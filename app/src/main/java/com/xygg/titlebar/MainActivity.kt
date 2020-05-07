package com.xygg.titlebar

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ComplexColorCompat
import com.xygg.library.TitleBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        TitleBar.config.setBackgroundColor(ContextCompat.getColor(this,android.R.color.white))
            .setShowBorder(true)
            .setUseRipple(true)
            .setCenterTitle(true)
            .setBorderWidth(this,1f)
            .setTitleTextColor(ContextCompat.getColor(this,R.color.black23))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolBar.registerListener {
            onMenuListener {

            }
            onBackListener {

            }
        }
    }
}