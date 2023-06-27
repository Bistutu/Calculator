package com.thinkstu

import android.content.Context
import android.os.Looper
import android.widget.Toast
import java.util.logging.Handler
import kotlin.system.exitProcess

class MyExceptionHandler(private val myContext: Context) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        android.os.Handler(Looper.getMainLooper()).post {
            Toast.makeText(myContext, "发生未知错误", Toast.LENGTH_LONG).show()
        }

        try {
            Thread.sleep(2000) // 让 Toast 有足够的时间显示出来
        } catch (ie: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        exitProcess(2)
    }
}
