package com.thinkstu

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {
    // 结果集
    private var editText: TextView? = null

    //清除
    private var clear = false


    // 初始化
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Thread.setDefaultUncaughtExceptionHandler(MyExceptionHandler(this))

        //  数字0-9、运算符
        val numberButtonIds = arrayOf(
            R.id.btn0,
            R.id.btn1,
            R.id.btn2,
            R.id.btn3,
            R.id.btn4,
            R.id.btn5,
            R.id.btn6,
            R.id.btn7,
            R.id.btn8,
            R.id.btn9,
            R.id.btn_add,
            R.id.btn_sub,
            R.id.btn_mul,
            R.id.btn_div,
            R.id.btn_dot,
            R.id.btn_consequent,
            R.id.btn_sqrt,
            R.id.btn_del,
            R.id.btn_neg
        )
        numberButtonIds.forEach { e -> run { findViewById<View>(e).setOnClickListener(listener) } }
        editText = findViewById(R.id.result) // 结果集
    }

    private var listener: View.OnClickListener = View.OnClickListener { view ->

        // 读取每个按钮的点击的内容，获取文本内容
        var input = editText!!.text.toString()

        when (view.id) {
            // 如果按下的是数字键或者小数点
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn_dot -> {
                if (clear) {
                    clear = false
                    editText!!.text = "" //赋值为空
                }
                editText!!.text = input + (view as Button).text //结果集就为本身
            }

            // 如果按下的是运算符
            R.id.btn_add, R.id.btn_sub, R.id.btn_mul, R.id.btn_div -> {
                if (clear) {
                    clear = false
                    input = ""
                    editText!!.text = ""
                }
                editText!!.text = input + (view as Button).text
            }

            // 如果是阶乘
            R.id.btn_sqrt -> {
                if (clear) {
                    clear = false
                    input = ""
                    editText!!.text = ""
                }
                editText!!.text = input + (view as Button).text
                result //调用处理结果集的方法
            }

            // 负数按钮
            R.id.btn_neg -> {
                if (clear) {
                    clear = false
                    input = ""
                    editText!!.text = ""
                }
                val temp = input
                val last = input.substring(0, temp.length)
                editText!!.text = "-$last"
            }

            R.id.btn_del -> if (clear) {
                clear = false
                editText!!.text = ""
                // 如果获取到的内容为空
            } else if (input != null || input != "") {
                if (input.length - 1 < 0) {
                    editText!!.text = input.substring(0, 0)
                    // 结果集为空
                } else {
                    // 结果集为空
                    editText!!.text = input.substring(0, input.length - 1)
                }
            }
            // 当按下等于号的时候，调用 result 方法
            R.id.btn_consequent -> result
        }
    }

    // 求结果
    private val result: Unit
        private get() {
            // 获取文本框的内容
            val exp = editText!!.text.toString()

            if (clear) {
                clear = false
                return
            }
            clear = true
            var result = 0.0
            // lastIndexOfAny() 方法返回指定字符在此字符串中最后一次出现处的索引
            val symbolIndex = exp.lastIndexOfAny(charArrayOf('+', '-', '*', '/', '√'))

            // 进行截取运算符前的数字
            val pre = exp.substring(0, symbolIndex)
            // 运算符
            val op = when {
                "+" in exp -> "+"
                "-" in exp -> "-"
                "*" in exp -> "*"
                "/" in exp -> "/"
                else -> "√"
            }

            // 运算符后的数字
            val post = exp.substring(symbolIndex + 1, exp.length)
            // 负数与平方根符号不能同时存在，正则表达式
            if (exp.matches(Regex(".*-.*√.*"))) {
                Msg.short(this, "负数不能开平方根！")
                result = 0.0 //则结果是0
                editText!!.text = ""
            }

            //如果包含小数点的运算
            if (pre.isNotEmpty() && post.isNotEmpty() && op != "√") {
                //判断是否为纯符号、多符号
                val isNums = post.matches(Regex("[0-9]+"))
                if (!isNums) {
                    Msg.short(this, "表达式有误！")
                } else {
                    val d1 = pre.toDouble()
                    val d2 = post.toDouble()
                    result = when (op) {
                        "+" -> d1 + d2
                        "-" -> d1 - d2
                        "*" -> d1 * d2
                        "/" -> if (d2 == 0.0) {
                            Msg.short(this, "除数不能为0！")
                            0.0
                        } else {
                            d1 / d2
                        }

                        else -> 0.0
                    }
                }
                if (!pre.contains(".") && !post.contains(".") && op != "/" && op != "!") { //如果是整数类型
                    //判断是否为纯符号、多符号
                    val isNums = post.matches(Regex("[0-9]+"))
                    if (!isNums) {
                        Msg.short(this, "表达式有误！")
                    } else {
                        val r = result.toInt() //都是整形
                        editText!!.text = r.toString() + ""
                    }
                } else {
                    editText!!.text = result.toString() + ""
                }
            } else if (pre.isNotEmpty() && post.isEmpty()) { //如果是只输入运算符前的数
                if (op != "√") {
                    editText!!.text = exp //直接返回当前文本框的内容
                    Msg.short(this, "表达式不完整！")
                } else {
                    val d1 = pre.toDouble()
                    result = sqrt(d1)
                    editText!!.text = result.toString() + ""
                }
            } else if (pre.isEmpty() && post.isNotEmpty()) { //如果是只输入运算符后面的数
                if (!post.matches(Regex("[0-9]+"))) {
                    Msg.short(this, "表达式有误！")
                } else {
                    val d2 = post.toDouble()
                    //运算符前没有输入数字
                    result = when (op) {
                        "+" -> 0 + d2
                        "-" -> 0 - d2
                        "*" -> 0.0
                        "/" -> 0.0
                        else -> {
                            result
                        }
                    }
                    if (!pre.contains(".") && !post.contains(".")) {
                        val r = result.toInt()
                        editText!!.text = r.toString() + ""
                    } else {
                        editText!!.text = result.toString() + ""
                    }
                }
            } else {
                editText!!.text = ""
            }
        }
}