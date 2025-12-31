package com.mathinskills.calculator.pdf

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import com.mathinskills.calculator.data.entity.CalculationEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

// ---------------------- 텍스트 정렬 헬퍼 ----------------------

private fun Canvas.drawTextCenteredInRect(
    text: String,
    left: Float,
    right: Float,
    baselineY: Float,
    paint: Paint
) {
    val textWidth = paint.measureText(text)
    val x = left + (right - left - textWidth) / 2f
    drawText(text, x, baselineY, paint)
}

private fun Canvas.drawTextRightInRect(
    text: String,
    right: Float,
    baselineY: Float,
    paint: Paint
) {
    val textWidth = paint.measureText(text)
    val x = right - textWidth - 4f
    drawText(text, x, baselineY, paint)
}

// ---------------------- PDF 생성 ----------------------

fun createTuitionPdf(
    data: List<CalculationEntity>,
    selectedOffice: String,
    outputFile: File
) {
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = document.startPage(pageInfo)
    val canvas = page.canvas

    val pageWidth = pageInfo.pageWidth.toFloat()

    val paint = Paint().apply {
        color = Color.BLACK
        textSize = 11f
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        isAntiAlias = true
    }

    val bold = Paint(paint).apply {
        typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
    }

    val linePaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }

    val tableLeft = 40f
    val tableRight = 555f
    val rowHeight = 25f

    // ----------------------------- 제목 -----------------------------
    bold.textSize = 18f
    val title = "교습비등 변경등록(신고) 내역서"
    val titleX = (pageWidth - bold.measureText(title)) / 2f
    canvas.drawText(title, titleX, 60f, bold)

    // ----------------------------- 교습비 -----------------------------
    var y = 100f
    paint.textSize = 13f
    canvas.drawText("☐ 교습비", tableLeft, y, paint)

    var currentY = y + 25f

    // ----------------------------- 1행 4칸 헤더 -----------------------------
    val headerRowHeight = 25f

    val hCols = floatArrayOf(
        tableLeft,              // 학원명 label
        tableLeft + 120f,       // 학원명 값
        tableLeft + 340f,       // 설립자 label
        tableLeft + 440f,       // 설립자 값
        tableRight              // End
    )

    canvas.drawRect(tableLeft, currentY, tableRight, currentY + headerRowHeight, linePaint)
    for (i in 1 until hCols.size) {
        canvas.drawLine(hCols[i], currentY, hCols[i], currentY + headerRowHeight, linePaint)
    }

    val centerHeader = currentY + headerRowHeight / 2f + 4f
    paint.textSize = 11f

    canvas.drawTextCenteredInRect("학원(교습소)명", hCols[0], hCols[1], centerHeader, paint)
    canvas.drawTextCenteredInRect("수학의기술삼각산점학원", hCols[1], hCols[2], centerHeader, paint)
    canvas.drawTextCenteredInRect("설립 · 운영자", hCols[2], hCols[3], centerHeader, paint)
    canvas.drawTextCenteredInRect("김 재 국", hCols[3], hCols[4], centerHeader, paint)

    currentY += headerRowHeight

    // ----------------------------- 교습비 헤더 2줄 -----------------------------
    // col[i] ~ col[i+1] 이 하나의 셀
    val cols = floatArrayOf(
        tableLeft,               // 0
        tableLeft + 80f,         // 1 교습과정
        tableLeft + 160f,        // 2 교습과목
        tableLeft + 210f,        // 3 1회
        tableLeft + 260f,        // 4 1주
        tableLeft + 310f,        // 5 1달
        tableLeft + 400f,        // 6 총(분)
        tableLeft + 470f,        // 7 분당단가
        tableRight               // 8 교습비
    )

    val headerTop = currentY
    val headerBottom = currentY + rowHeight * 2

    // 바깥 테두리
    canvas.drawRect(tableLeft, headerTop, tableRight, headerBottom, linePaint)

    // 세로선 (상단 전체)
    // 0~1, 1~2, (2~6 묶음), 6~7, 7~8
    canvas.drawLine(cols[1], headerTop, cols[1], headerBottom, linePaint)
    canvas.drawLine(cols[2], headerTop, cols[2], headerBottom, linePaint)
    canvas.drawLine(cols[6], headerTop, cols[6], headerBottom, linePaint)
    canvas.drawLine(cols[7], headerTop, cols[7], headerBottom, linePaint)

    // 상·하단 구분선
    canvas.drawLine(cols[2], headerTop + rowHeight, cols[6], headerTop + rowHeight, linePaint)

    // 하단(1회/1주/1달/총) 세로선
    canvas.drawLine(cols[3], headerTop + rowHeight, cols[3], headerBottom, linePaint)
    canvas.drawLine(cols[4], headerTop + rowHeight, cols[4], headerBottom, linePaint)
    canvas.drawLine(cols[5], headerTop + rowHeight, cols[5], headerBottom, linePaint)

    val centerHeaderUpper = headerTop + rowHeight / 2f + 2f
    val centerHeaderLower = headerTop + rowHeight + rowHeight / 2f + 4f

    bold.textSize = 11f

    // 상단 텍스트
    canvas.drawTextCenteredInRect("총교습시간(분/월)", cols[2], cols[6], centerHeaderUpper, paint)

    // 하단 텍스트
    canvas.drawTextCenteredInRect("교습과정", cols[0], cols[1], centerHeaderLower, paint)
    canvas.drawTextCenteredInRect("교습과목(반)", cols[1], cols[2], centerHeaderLower, paint)
    canvas.drawTextCenteredInRect("분당단가", cols[6], cols[7], centerHeaderLower, paint)
    canvas.drawTextCenteredInRect("교습비", cols[7], cols[8], centerHeaderLower, paint)

    // 총교습시간 하단 작은 글씨
    paint.textSize = 10f
    val subY = headerTop + rowHeight * 2 - 8f
    canvas.drawTextCenteredInRect("1회(분)", cols[2], cols[3], subY, paint)
    canvas.drawTextCenteredInRect("1주(회)", cols[3], cols[4], subY, paint)
    canvas.drawTextCenteredInRect("1달", cols[4], cols[5], subY, paint)
    canvas.drawTextCenteredInRect("총(분)", cols[5], cols[6], subY, paint)

    currentY = headerBottom

    // ----------------------------- 데이터 행 -----------------------------
    paint.textSize = 10.5f

    data.forEach { item ->
        val centerRow = currentY + rowHeight / 2f + 4f

        val totalMinutes = item.minutesPerClass * item.lessonsPerMonth * 4
        val perMinute =
            if (totalMinutes > 0) item.tuitionFee / totalMinutes.toDouble() else 0.0

        // 가로선
        canvas.drawLine(tableLeft, currentY, tableRight, currentY, linePaint)
        // 세로선
        for (x in cols) {
            canvas.drawLine(x, currentY, x, currentY + rowHeight, linePaint)
        }

        canvas.drawTextCenteredInRect(item.courseType ?: "", cols[0], cols[1], centerRow, paint)
        canvas.drawTextCenteredInRect(item.subject, cols[1], cols[2], centerRow, paint)
        canvas.drawTextCenteredInRect("${item.minutesPerClass}", cols[2], cols[3], centerRow, paint)
        canvas.drawTextCenteredInRect("${item.lessonsPerMonth}", cols[3], cols[4], centerRow, paint)
        canvas.drawTextCenteredInRect("4", cols[4], cols[5], centerRow, paint)
        canvas.drawTextCenteredInRect("$totalMinutes", cols[5], cols[6], centerRow, paint)

        canvas.drawTextRightInRect(String.format("%.2f", perMinute), cols[7], centerRow, paint)
        canvas.drawTextRightInRect(
            String.format("%,d", item.tuitionFee),
            cols[8],
            centerRow,
            paint
        )

        currentY += rowHeight
    }

    // 마지막 데이터 행의 아래 가로선
    canvas.drawLine(tableLeft, currentY, tableRight, currentY, linePaint)

    // ----------------------------- 변경 사항 -----------------------------
    val changeHeight = 25f
    val centerChange = currentY + changeHeight / 2f + 4f

    canvas.drawLine(tableLeft, currentY, tableLeft, currentY + changeHeight, linePaint)
    canvas.drawLine(tableRight, currentY, tableRight, currentY + changeHeight, linePaint)
    canvas.drawLine(tableLeft, currentY + changeHeight, tableRight, currentY + changeHeight, linePaint)

    bold.textSize = 11f
    canvas.drawText("※ 변경 사항 :", tableLeft + 10f, centerChange, bold)

    // 전체 테두리(교습비 표)
    canvas.drawRect(tableLeft, headerTop, tableRight, currentY + changeHeight, linePaint)

    currentY += changeHeight + 8f

    // ----------------------------- 총교습시간 계산방법 -----------------------------
    paint.textSize = 11f
    canvas.drawText(
        "※ 총교습시간 계산방법 = 1회 교습시간(분) × 주당 교습 횟수 × 4.2",
        tableLeft + 5f,
        currentY + 20f,
        paint
    )

    currentY += 40f

    // ----------------------------- 기타경비 -----------------------------
// ----------------------------- 기타경비 -----------------------------

    currentY += 10f // 표 간 여백

    paint.textSize = 13f
    canvas.drawText("☐ 기타경비", tableLeft, currentY, paint)

    currentY += 15f

    val etcRowHeight = 25f
    val etcTop = currentY
    val etcBottom = etcTop + etcRowHeight * 2

// 🔥 6칸 균등 분할
    val etcTitles = listOf("모의고사비", "재료비", "피복비", "급식비", "기숙사비", "차량비")
    val cellWidth = (tableRight - tableLeft) / etcTitles.size.toFloat()

// 셀 경계 좌표 자동 생성
    val etcCols = FloatArray(etcTitles.size + 1) { i ->
        tableLeft + (cellWidth * i)
    }

// 테두리
    canvas.drawRect(tableLeft, etcTop, tableRight, etcBottom, linePaint)
    canvas.drawLine(tableLeft, etcTop + etcRowHeight, tableRight, etcTop + etcRowHeight, linePaint)

// 세로줄 / 텍스트 배치
    val centerEtc = etcTop + etcRowHeight / 2f + 4f
    paint.textSize = 11f

    etcTitles.forEachIndexed { index, title ->
        canvas.drawLine(etcCols[index], etcTop, etcCols[index], etcBottom, linePaint)

        canvas.drawTextCenteredInRect(
            title,
            etcCols[index],
            etcCols[index + 1],
            centerEtc,
            paint
        )
    }

// 마지막 세로줄
    canvas.drawLine(etcCols.last(), etcTop, etcCols.last(), etcBottom, linePaint)

    currentY = etcBottom + 50f

    // ----------------------------- 서명 -----------------------------
    var yPos = etcTop + 90f
    fun drawCenteredLine(text: String, yBaseline: Float, p: Paint = paint) {
        val x = (pageWidth - p.measureText(text)) / 2f
        canvas.drawText(text, x, yBaseline, p)
    }

    paint.textSize = 12f
    drawCenteredLine("위와 같이 교습비 변경등록을 신청합니다.", yPos)

    val today = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA).format(Date())
    yPos += 30f
    drawCenteredLine(today, yPos)

    yPos += 30f
    drawCenteredLine("설립·운영자    김 재 국        (서명 또는 인)", yPos)

    yPos += 40f
    bold.textSize = 13f
    drawCenteredLine("${selectedOffice} 교육장 귀하", yPos, bold)


    // ----------------------------- 저장 -----------------------------
    document.finishPage(page)
    document.writeTo(FileOutputStream(outputFile))
    document.close()
}
