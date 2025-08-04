package org.app.inflo.core.utils

interface TimeUtils {
    /**
     * Formats milliseconds to DD/MM/YYYY format
     * @param millis The time in milliseconds
     * @return Formatted date string in DD/MM/YYYY format
     */
    fun ddMMyyyy(millis: Long): String
} 