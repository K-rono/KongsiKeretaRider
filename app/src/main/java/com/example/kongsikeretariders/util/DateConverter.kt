package com.example.kongsikeretariders.util

import java.text.SimpleDateFormat
import java.util.Locale

object DateConverter {
    fun millisToFormattedDate(date: Long): String {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return simpleDateFormat.format(date)
    }
}