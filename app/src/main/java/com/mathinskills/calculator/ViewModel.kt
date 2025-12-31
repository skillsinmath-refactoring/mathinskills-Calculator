package com.mathinskills.calculator

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathinskills.calculator.data.RetrofitClient.retrofit
import com.mathinskills.calculator.data.api.RegionStandardApi
import com.mathinskills.calculator.data.api.UnitCalcuLateApi
import com.mathinskills.calculator.data.entity.CalculationEntity
import com.mathinskills.calculator.data.request.CalculateReq
import com.mathinskills.calculator.data.room.AppDatabase
import com.mathinskills.calculator.model.RegionStandardItem
import com.mathinskills.calculator.model.SubjectItem
import kotlinx.coroutines.launch
import retrofit2.HttpException
/***
 * 각 교육청 고유 id
 * 서울북부교육지원청 - 1
 * 서울동부교육지원청 - 2
 * 서울서부교육지원청 - 3
 * 서울남부교육지원청 - 4
 * 서울중부교육지원청 - 5
 * 서울강동송파교육지원청 - 6
 * 서울강서양천교육지원청 - 7
 * 서울강남서초교육지원청 - 8
 * 서울동작관악교육지원청 - 9
 * 서울성동광진교육지원청 - 10
 * 서울성북강북교육지원청 - 11
 * 서울영등포교육지원청 - 12
 */
class ViewModel(application: Application) : ViewModel() {
    private val db = AppDatabase.getDatabase(application)
    private val calcDao = db.calculationDao()
    private val regionDao = db.regionStandardDao()

    var calculateList = MutableLiveData<List<CalculationEntity>>(mutableListOf())

    var educationOfficeList =  MutableLiveData<List<SubjectItem>>()
    var regionStandardList =  MutableLiveData<List<RegionStandardItem>>()
    private val regionStandardApi = retrofit.create(RegionStandardApi::class.java)// 특정 교육청 분당단가 호출
    val isLoading = MutableLiveData<Boolean>(false)


    init {
        fetchCalculations()
        fetchRegionStandard("1")
    }

    fun fetchRegionStandard(regionCode: String? = null) {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                regionStandardApi.getRegionStandard(regionCode).let { response ->
                    val itemList = response.map { item ->
                        RegionStandardItem(
                            id = item.id,
                            regionCode = item.regionCode,
                            educationOffice = item.educationOffice,
                            courseType = item.courseType,
                            standardPrice = item.standardPrice,
                            effectiveDate = item.effectiveDate,
                            sourceUrl = item.sourceUrl
                        )
                    }
                    regionStandardList.postValue(itemList)
                }
                Log.d("API_CALL_LOG", "API 호출 성공: ${regionStandardList.value}")
            } catch (e: Exception) {
                // TODO: 오류 처리
                Log.e("${e.message}", e.message ?: "API 호출 실패")
            }
            finally {
                isLoading.postValue(false)
            }
        }
    }


    fun fetchCalculations() {
        viewModelScope.launch {
            isLoading.postValue(true)
            calculateList.postValue(

                calcDao.getAll())
            isLoading.postValue(false)
        }
    }
    fun saveCalculation(item: SubjectItem) {
        viewModelScope.launch {
            try {
                val roundedStandard = String.format("%.2f", item.myCalculate).toDouble()

                val entity = CalculationEntity(
                    courseType = item.courseType,
                    educationOffice = item.educationOffice,
                    subject = item.subject,
                    minutesPerClass = item.onceMinutes,
                    lessonsPerWeek = item.weekTimes,
                    lessonsPerMonth = item.monthTimes.toDouble(),
                    tuitionFee = item.tuition_fee,
                    unitPrice = item.standardRate,
                    standardPriceAtCalc = roundedStandard,
                    isValid = item.rate
                )

                calcDao.insert(entity)

                fetchCalculations()
            } catch (e: Exception) {
                Log.e("ROOM_ERROR", "저장 실패: ${e.message}")
            }
        }
    }

    fun deleteCalculation(recordId: Int) {
        viewModelScope.launch {
            try {
                calcDao.deleteById(recordId)
                fetchCalculations()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            fetchCalculations()
        }
    }

    fun setEducationOfficeList(office: String) {
       viewModelScope.launch {

       }
    }
}