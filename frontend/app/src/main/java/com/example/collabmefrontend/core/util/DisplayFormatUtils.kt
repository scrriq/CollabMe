package com.example.collabmefrontend.core.util

/**
 * Преобразует ISO-дату/дату-время с бэкенда (например 2025-05-03T12:00:00Z) в dd-MM-yyyy.
 */
fun formatApiDateTime(raw: String): String {
    if (raw.isBlank()) return raw

    val datePart = raw.substringBefore('T').trim()
    val parts = datePart.split('-')
    if (parts.size != 3) return raw

    val (year, month, day) = parts
    if (year.length != 4 || month.length != 2 || day.length != 2) return raw

    return "$day-$month-$year"
}

fun buildAuthorDisplayName(firstName: String, lastName: String): String {
    val first = firstName.trim()
    val last = lastName.trim()
    return when {
        first.isNotEmpty() && last.isNotEmpty() -> "$first $last"
        first.isNotEmpty() -> first
        last.isNotEmpty() -> last
        else -> ""
    }
}
