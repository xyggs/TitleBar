package com.xygg.titlebar

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.content.res.ComplexColorCompat
import com.xygg.library.TitleBar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        TitleBar.config.setBackgroundColor(Color.WHITE)
            .setShowBorder(true)
            .setBorderWidth(this,1f)
            .setTitleTextColor(ContextCompat.getColor(this,R.color.colorAccent))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}