package com.tvsm.iqubeindia.presentation.ui.utils

import android.util.Log
import java.time.LocalDate
import java.time.YearMonth
import java.time.Year
import java.util.*
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter


object Common {
    fun convertSecondsToHoursAndMinutes(seconds: Int): Pair<Int, Int> {
        val hours = seconds / 3600
        val minutes = (hours % 3600) / 60
        Log.d("Utils.Common", "$seconds seconds is $hours hours and $minutes mins")
        return Pair(hours, minutes)
    }
    fun convertMinutesToHoursAndMinutes(minutes: Int): Pair<Int, Int> {
        if (minutes < 60) {
            return Pair(0, minutes)
        }

        val hours = minutes / 60
        val remainingMinutes = minutes % 60

        return Pair(hours, remainingMinutes)
    }
    fun getSyncText(dateTime: String): String {
        // Define the date format of the input string
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")  // Parse the input time in UTC

        // Log the incoming dateTime string
        Log.d("SyncDebug", "Input dateTime: $dateTime")

        // Parse the input date string into a Date object
        val parsedDate: Date = format.parse(dateTime) ?: return "Invalid date format"

        // Convert Date to Instant and then to LocalDateTime in UTC
        val parsedLocalDateTime = parsedDate.toInstant()
            .atZone(ZoneId.of("UTC"))
            .toLocalDateTime()

        // Log the parsed UTC LocalDateTime
        Log.d("SyncDebug", "Parsed UTC LocalDateTime: $parsedLocalDateTime")

        // Convert LocalDateTime to system default timezone
        val now = LocalDateTime.now()
        val zoneId = ZoneId.systemDefault()
        val parsedDateTimeInLocal = parsedLocalDateTime
            .atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(zoneId)
            .toLocalDateTime()

        // Log the converted LocalDateTime in the system's default timezone (e.g., IST)
        Log.d("SyncDebug", "Converted LocalDateTime in system timezone (IST): $parsedDateTimeInLocal")


        // Calculate time differences
        val daysAgo = Period.between(parsedDateTimeInLocal.toLocalDate(), now.toLocalDate()).days
        val hoursAgo = Duration.between(parsedDateTimeInLocal, now).toHours() % 24
        val minsAgo = Duration.between(parsedDateTimeInLocal, now).toMinutes() % 60

        // Log time differences
        Log.d("SyncDebug", "Days ago: $daysAgo")
        Log.d("SyncDebug", "Hours ago: $hoursAgo")
        Log.d("SyncDebug", "Minutes ago: $minsAgo")

        // Generate sync text based on time differences
        return when {
            Year.now().value == parsedDateTimeInLocal.year -> {
                if (YearMonth.now().monthValue == parsedDateTimeInLocal.monthValue) {
                    when {
                        daysAgo >= 2 -> "Synced on ${parsedDateTimeInLocal.dayOfMonth}/${parsedDateTimeInLocal.monthValue.toString().padStart(2, '0')}"
                        daysAgo == 1 -> "Synced yesterday"
                        daysAgo == 0 -> {
                            when {
                                hoursAgo > 0 -> "Synced $hoursAgo hr${if (hoursAgo > 1) "s" else ""} ago"
                                minsAgo > 1 -> "Synced $minsAgo mins ago"
                                minsAgo.toInt() == 1 -> "Synced 1 min ago"
                                minsAgo in 0..0 -> "Synced few seconds ago" // Explicitly handle 0
                                else -> "" // Handle any other unexpected cases
                            }
                        }
                        else -> "Synced on ${parsedDateTimeInLocal.dayOfMonth}/${parsedDateTimeInLocal.monthValue.toString().padStart(2, '0')}"
                    }
                } else {
                    "Synced on ${parsedDateTimeInLocal.dayOfMonth}/${parsedDateTimeInLocal.monthValue.toString().padStart(2, '0')}"
                }
            }
            else -> "Synced on ${parsedDateTimeInLocal.dayOfMonth}/${parsedDateTimeInLocal.monthValue.toString().padStart(2, '0')}"
        }
    }

    fun isDateTimePacketFresh(dateTime: String): Boolean {
        if (dateTime.length > 1) {
            val year: String = dateTime.substring(0, 4)
            val month: String = dateTime.substring(5, 7)
            val day: String = dateTime.substring(8, 10)
            val hour: String = dateTime.substring(11, 13)
            val min: String = dateTime.substring(14, 16)

            // Define time zone for IST
            val istZoneId = ZoneId.of("Asia/Kolkata")
            val utcZoneId = ZoneId.of("UTC")

            // Get the current time in IST
            val nowInIst = LocalDateTime.now(istZoneId)
            println("isDateTimePacketFresh - Current IST Time: $nowInIst")

            // Convert the current IST time to UTC
            val nowInUtc = nowInIst.atZone(istZoneId).withZoneSameInstant(utcZoneId).toLocalDateTime()
            println("isDateTimePacketFresh - Current UTC Time: $nowInUtc")

            // Parse the input dateTime string into a LocalDateTime in UTC
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val parsedDateTime = LocalDateTime.parse(dateTime, formatter)
            println("isDateTimePacketFresh - Parsed UTC Time: $parsedDateTime")

            // Calculate the differences
            val daysAgo = nowInUtc.toLocalDate().dayOfMonth - parsedDateTime.toLocalDate().dayOfMonth
            val hoursAgo = nowInUtc.hour - parsedDateTime.hour
            val minsAgo = nowInUtc.minute - parsedDateTime.minute

            // Log the time differences
            println("isDateTimePacketFresh - Days Ago: $daysAgo")
            println("isDateTimePacketFresh - Hours Ago: $hoursAgo")
            println("isDateTimePacketFresh - Minutes Ago: $minsAgo")

            // Check if the datetime packet is fresh
            return (Year.now().value.toString() == year) &&
                    (YearMonth.now().monthValue == month.toInt()) &&
                    (daysAgo == 0) &&
                    (hoursAgo == 0) &&
                    (minsAgo <= 30)
        } else {
            return false
        }
    }
}