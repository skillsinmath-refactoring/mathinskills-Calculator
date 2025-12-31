package com.mathinskills.calculator

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.mathinskills.calculator.databinding.ActivityMainBinding
import com.mathinskills.calculator.model.SubjectItem
import com.mathinskills.calculator.pdf.createTuitionPdf
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ViewModel
    private lateinit var adapter: CalculateAdapter
    private lateinit var binding: ActivityMainBinding
    private val pref by lazy { getSharedPreferences("settings", MODE_PRIVATE) }

    private lateinit var courseTypeList: List<String>
    private val eduOfficeList = listOf("4", "8", "12") // 월 선택 드롭다운

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        loadSavedEducationOffice()
        setContentView(binding.root)
        window.statusBarColor = Color.parseColor("#F0F3F5")
        // 시스템 바 패딩
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )
            insets
        }

        // 처음엔 삭제 버튼 숨김
        binding.btnDel.visibility = View.GONE

        viewModel = ViewModel(application)
        setBanner()
        setEduOffice()
        setupDropdowns()
        setupButtons()
        initViewModel()
        remoteConfig()

    }


    private fun setBanner() = with(binding) {
        val remoteConfig = Firebase.remoteConfig
        val bannerImageUrl = remoteConfig.getString("banner_image_url")
        val bannerTargetUrl = remoteConfig.getString("banner_url")

        Glide.with(bannerImage.context)
            .load(bannerImageUrl)
            .into(bannerImage)

        bannerImage.setOnClickListener {
            if (bannerTargetUrl.isNotEmpty() && bannerTargetUrl.startsWith("http")) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(bannerTargetUrl))
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("RemoteConfig", "Failed to open URL: ${e.message}")
                    Toast.makeText(this@MainActivity, "잘못된 URL입니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.w("RemoteConfig", "Invalid or empty URL: $bannerTargetUrl")
            }
        }
    }
    private fun remoteConfig(){
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0 // 1분마다 업데이트 확인
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    setBanner()
                } else {
                    // fetch 실패해도 기본값으로 배너 표시
                    setBanner()
                }
            }
    }

    private fun setEduOffice() = with(binding) {
        educationOffice.setOnClickListener {
            val eduOffices = arrayOf(
                "서울북부교육지원청",
                "서울동부교육지원청",
                "서울서부교육지원청",
                "서울남부교육지원청",
                "서울중부교육지원청",
                "서울강동송파교육지원청",
                "서울강서양천교육지원청",
                "서울강남서초교육지원청",
                "서울동작관악교육지원청",
                "서울성동광진교육지원청",
                "서울성북강북교육지원청",
                "서울영등포교육지원청"
            )
            var selectedOffice = ""
            var selectedOfficeId = 1

            val builder = android.app.AlertDialog.Builder(this@MainActivity)
            builder.setTitle("교육청 선택")
            builder.setSingleChoiceItems(eduOffices, -1) { _, which ->
                selectedOffice = eduOffices[which]
                selectedOfficeId = which + 1
            }
            builder.setPositiveButton("확인") { dialog, _ ->
                if (selectedOffice.isNotEmpty()) {
                    educationOffice.text = "$selectedOffice >"
                    viewModel.fetchRegionStandard(selectedOfficeId.toString())
                    pref.edit().putString("selectedOffice", selectedOffice).apply()
                    pref.edit().putString("selectedOfficeId", selectedOfficeId.toString()).apply()
                }
                dialog.dismiss()
                // 교육청 다시 선택하면 삭제 버튼 숨김
                btnDel.visibility = View.GONE
            }
            builder.setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }

            builder.create().show()
        }
    }

    private fun setupDropdowns() = with(binding) {
        val monthAdapter = ArrayAdapter(
            this@MainActivity,
            android.R.layout.simple_dropdown_item_1line,
            eduOfficeList
        )
        (autoCompleteMonth as? AutoCompleteTextView)?.apply {
            setAdapter(monthAdapter)
            setText("달 선택", false)

            post {
                try {
                    val popupField = AutoCompleteTextView::class.java.getDeclaredField("mPopup")
                    popupField.isAccessible = true
                    val popup = popupField.get(this)
                    if (popup is android.widget.ListPopupWindow) {
                        val density = resources.displayMetrics.density
                        popup.height = (density * 48 * 3).toInt()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        (autoCompleteCourseType as? AutoCompleteTextView)?.post {
            try {
                val popupField = AutoCompleteTextView::class.java.getDeclaredField("mPopup")
                popupField.isAccessible = true
                val popup = popupField.get(autoCompleteCourseType)
                if (popup is android.widget.ListPopupWindow) {
                    val density = resources.displayMetrics.density
                    popup.height = (density * 48 * 4).toInt()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupButtons() = with(binding) {

        btnPdf.setOnClickListener {
            // 현재 리스트 가져오기
            val dataList = viewModel.calculateList.value ?: emptyList()

            if (dataList.isEmpty()) {
                Toast.makeText(this@MainActivity, "PDF로 저장할 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedOfficeName = educationOffice.text.toString().replace(" >", "").trim()
            // 저장 경로 지정
            val fileName = "tuition_report_${System.currentTimeMillis()}.pdf"
            val pdfFile = File(getExternalFilesDir(null), fileName)

            // PDF 생성
            createTuitionPdf(dataList,
                selectedOfficeName,
                pdfFile,
                )
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = FileProvider.getUriForFile(
                this@MainActivity,
                "${packageName}.provider",
                pdfFile
            )
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
            Toast.makeText(this@MainActivity, "PDF가 생성되었습니다: ${pdfFile.absolutePath}", Toast.LENGTH_LONG).show()
        }

        // 삭제 버튼: 삭제 + 초기화 다이얼로그 재사용
        btnDel.setOnClickListener {
            showResetDialog {
                viewModel.deleteCalculation(adapter.getCurrentId())
                resetScreen()
            }
        }

        btnSave.setOnClickListener {
            val subject = editSubject.text.toString()
            val onceMinutes = editOnceMinutes.text.toString().toIntOrNull()
            val weekTimes = editWeekCount.text.toString().toIntOrNull()
            val tuitionFee = editFee.text.toString().toIntOrNull()

            if (subject.isBlank() || onceMinutes == null || weekTimes == null || tuitionFee == null) {
                textStatus.text = "모든 항목을 입력해주세요."
                return@setOnClickListener
            }

            val course = autoCompleteCourseType.text.toString()
            val standardRate = standardPrice.tag?.toString()?.toDoubleOrNull() ?: 0.0
            val month = autoCompleteMonth.text.toString().toDoubleOrNull() ?: 0.0
            val currentId = if (::adapter.isInitialized && adapter.ItemId != -1) {
                adapter.getCurrentId()
            } else {
                0
            }
            viewModel.saveCalculation(
                SubjectItem(
                    id = currentId,
                    courseType = course,
                    educationOffice = educationOffice.text.toString().dropLast(7),
                    subject = subject,
                    onceMinutes = onceMinutes,
                    weekTimes = weekTimes,
                    tuition_fee = tuitionFee,
                    monthTimes = month,
                    rate = calculateTuitionFee(tuitionFee.toDouble(), standardRate),
                    standardRate = standardRate,
                    myCalculate = if (onceMinutes > 0 && weekTimes > 0 && month > 0) {
                        tuitionFee.toDouble() / (onceMinutes * weekTimes * month)
                    } else 0.0
                )
            )
        }

        editFee.doOnTextChanged { _, _, _, _ -> validateAndUpdateUI() }
        editOnceMinutes.doOnTextChanged { _, _, _, _ -> validateAndUpdateUI() }
        editWeekCount.doOnTextChanged { _, _, _, _ -> validateAndUpdateUI() }
        (autoCompleteMonth as? AutoCompleteTextView)?.setOnItemClickListener { _, _, _, _ ->
            validateAndUpdateUI()
        }
        (autoCompleteCourseType as? AutoCompleteTextView)?.setOnItemClickListener { _, _, _, _ ->
            validateAndUpdateUI()
        }

        // 추가 버튼: 같은 초기화 다이얼로그 재사용
        btnAdd.setOnClickListener {
            showResetDialog {
                resetScreen()
            }
        }
    }

    private fun initViewModel() = with(binding) {

        viewModel.calculateList.observe(this@MainActivity) { newList ->
            adapter = CalculateAdapter(newList) { item ->
                editSubject.setText(item.subject)
                editFee.setText(item.tuitionFee.toString())
                editOnceMinutes.setText(item.minutesPerClass.toString())
                editWeekCount.setText(item.lessonsPerWeek.toString())
                myPrice.text = item.standardPriceAtCalc.toString()
                standardPrice.text = item.unitPrice.toString()
                textStatus.text =
                    if (calculateTuitionFee(item.standardPriceAtCalc, item.unitPrice)) "적정" else "부적정"

                editSubject.isEnabled = false
                editFee.isEnabled = false
                editOnceMinutes.isEnabled = false
                editWeekCount.isEnabled = false
                btnSave.isEnabled = false
                btnDel.visibility = View.VISIBLE
                (autoCompleteCourseType as? AutoCompleteTextView)?.setText(item.courseType, false)
                (autoCompleteMonth as? AutoCompleteTextView)?.setText(
                    item.lessonsPerMonth.toString(),
                    false
                )
                changePriceText(item.standardPriceAtCalc.toString(), item.unitPrice.toString())
            }
            recyclerTable.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        viewModel.regionStandardList.observe(this@MainActivity) { list ->
            Log.e("DROPDOWN_DATA", "리스너에서 받은 데이터: $list")

            if (list.isEmpty()) {
                Toast.makeText(
                    this@MainActivity,
                    "해당 교육청의 분당 단가 데이터가 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            courseTypeList = list?.map { it.courseType ?: "미선택" }
                ?.toMutableList()
                ?.apply { add("교습과정 선택") }
                ?: listOf("교습과정 선택")

            val courseAdapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_dropdown_item_1line,
                courseTypeList
            )
            (autoCompleteCourseType as? AutoCompleteTextView)?.setAdapter(courseAdapter)
            autoCompleteCourseType.setText("교습과정 선택", false)

            autoCompleteCourseType.setOnItemClickListener { _, _, position, _ ->
                val selectedCourseType = courseTypeList[position]
                val matchedItem = list?.find { it.courseType == selectedCourseType }
                if (matchedItem != null) {
                    standardPrice.tag = matchedItem.standardPrice.toString()
                } else {
                    standardPrice.tag = null
                }
                validateAndUpdateUI()
            }

            Log.d("DROPDOWN_DATA", "들어온 데이터: $list")
        }
    }

    private fun validateAndUpdateUI() = with(binding) {
        val tuitionFee = editFee.text?.toString()?.toDoubleOrNull()
        val onceMinutes = editOnceMinutes.text?.toString()?.toIntOrNull()
        val weekTimes = editWeekCount.text?.toString()?.toIntOrNull()
        val month = autoCompleteMonth.text.toString().toDoubleOrNull()
        val standard = standardPrice.tag?.toString()?.toDoubleOrNull()

        if (tuitionFee != null && onceMinutes != null && weekTimes != null && month != null && standard != null) {
            val myCalculate = calculatePerMinuteFee(tuitionFee, onceMinutes, weekTimes, month)
            standardPrice.text = "$standard"
            changePriceText(myCalculate.toString(), standard.toString())
        } else {
            resetPriceText()
        }
    }

    fun calculateTuitionFee(calculationFee: Double, standardFee: Double): Boolean =
        calculationFee <= standardFee

    private fun resetPriceText() = with(binding) {
        myPrice.text = ""
        standardPrice.text = ""
        textStatus.text = ""
    }

    fun calculatePerMinuteFee(
        tuitionFee: Double,
        onceMinutes: Int,
        weekTimes: Int,
        month: Double
    ): Double {
        val denom = onceMinutes.toDouble() * weekTimes.toDouble() * month
        return if (denom > 0.0 && denom.isFinite()) {
            val result = tuitionFee / denom
            if (result.isFinite()) result else 0.0
        } else 0.0
    }

    private fun changePriceText(fee: String, standardFee: String) = with(binding) {
        val feeDouble = fee.toDoubleOrNull() ?: Double.NaN
        val standardDouble = standardFee.toDoubleOrNull() ?: Double.NaN

        if (!feeDouble.isFinite() || !standardDouble.isFinite()) {
            myPrice.text = "-"
            standardPrice.text = "-"
            textStatus.text = "계산 오류"
            Log.w("CALC", "Invalid value detected: fee=$feeDouble, standard=$standardDouble")
            return@with
        }

        val feeFormatted = String.format("%.2f", feeDouble)
        val standardFormatted = String.format("%.2f", standardDouble)

        myPrice.text = feeFormatted
        standardPrice.text = standardFormatted

        val isProper = calculateTuitionFee(feeDouble, standardDouble)
        textStatus.text = if (isProper) "적정" else "부적정"

        val colorRes = if (isProper)
            R.color.text_color_pass   // 적정일 때
        else
            R.color.text_color        // 고가일 때

        textStatus.setTextColor(ContextCompat.getColor(this@MainActivity, colorRes))
        textStatus.setTypeface(textStatus.typeface, Typeface.BOLD)
    }

    // 🔹 화면 전체 초기화 공통 함수
    private fun resetScreen() = with(binding) {
        btnSave.isEnabled = true
        editSubject.isEnabled = true
        editFee.isEnabled = true
        editOnceMinutes.isEnabled = true
        editWeekCount.isEnabled = true

        editSubject.text.clear()
        editFee.text.clear()
        editOnceMinutes.text.clear()
        editWeekCount.text.clear()

        textStatus.text = ""
        (autoCompleteCourseType as? AutoCompleteTextView)?.setText("교습과정 선택", false)
        (autoCompleteMonth as? AutoCompleteTextView)?.setText("달 선택", false)
        resetPriceText()

        btnDel.visibility = View.GONE
    }

    private fun showResetDialog(onConfirmed: () -> Unit) {
        val builder = android.app.AlertDialog.Builder(this@MainActivity)
        builder.setTitle("확인")
        builder.setMessage("입력한 내용을 지우시겠습니까?")

        builder.setPositiveButton("확인") { dialog, _ ->
            onConfirmed()
            dialog.dismiss()
            Toast.makeText(this@MainActivity, "입력 내용이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun loadSavedEducationOffice() {
        val savedOffice = pref.getString("selectedOffice", null)
        val savedOfficeId = pref.getString("selectedOfficeId", null)

        if (!savedOffice.isNullOrEmpty() && !savedOfficeId.isNullOrEmpty()) {
            binding.educationOffice.text = "$savedOffice >"
            viewModel.fetchRegionStandard(savedOfficeId)  // 자동으로 해당 교육청 데이터 로드
        }
    }
}
