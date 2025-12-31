package com.mathinskills.calculator.util

object LessonFeeUtil {

    /**
     * 분당 단가 계산 함수
     *
     * @param lessonMinutes 1회 수업 시간 (분 단위)
     * @param lessonsPerWeek 주당 수업 횟수
     * @param weeksPerMonth 월 기준 주차 수
     * @param totalFee 교습비
     * @param standardRate 기준 단가 (분당)
     * @return true = 가능, false = 재조정 필요
     */
    fun isFeeValid(
        lessonMinutes: Int,
        lessonsPerWeek: Int,
        weeksPerMonth: Double,
        totalFee: Int,
        standardRate: Int
    ): Boolean {
        val totalMinutes = lessonMinutes * lessonsPerWeek * weeksPerMonth
        if (totalMinutes.toInt() == 0) return false  // 0으로 나누는 경우 방지

        val perMinuteFee = totalFee.toDouble() / totalMinutes
        return perMinuteFee <= standardRate.toDouble()
    }

    fun calculatePerMinuteFee(
        lessonMinutes: Int,
        lessonsPerWeek: Int,
        weeksPerMonth: Int,
        totalFee: Int
    ): Double {
        val totalMinutes = lessonMinutes * lessonsPerWeek * weeksPerMonth
        if (totalMinutes == 0) return 0.0
        return totalFee.toDouble() / totalMinutes
    }
}
