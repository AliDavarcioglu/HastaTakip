package com.example.hastatakip


import android.content.Context
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant


class HealthConnectManager (private val context: Context) {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    val permissions = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class),

        HealthPermission.getReadPermission(BodyTemperatureRecord::class),
        HealthPermission.getWritePermission(BodyTemperatureRecord::class),

        HealthPermission.getReadPermission(BloodPressureRecord::class),
        HealthPermission.getWritePermission(BloodPressureRecord::class),

        HealthPermission.getReadPermission(OxygenSaturationRecord::class),
        HealthPermission.getWritePermission(OxygenSaturationRecord::class),

        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),
    )

    suspend fun hasAllPermissions(permissions: Set<String>): Boolean {
        return healthConnectClient.permissionController.getGrantedPermissions().containsAll(permissions)
    }

    fun requestPermissionsActivityContract(): ActivityResultContract<Set<String>, Set<String>> {
        return PermissionController.createRequestPermissionResultContract()
    }




    suspend fun readHeartRateData():String? {
        var data = "Değer Yok"
        // Kalp atış hızı verilerini zaman aralığı kısıtlaması olmadan çek
        val timeRange = TimeRangeFilter.between(Instant.EPOCH, Instant.now())

        val request = ReadRecordsRequest(
            recordType = HeartRateRecord::class,
            timeRangeFilter = timeRange
        )

        val response = healthConnectClient.readRecords(request)

        // Çekilen verilerden en sonuncusunu al
        val latestRecord = response.records.maxByOrNull { it.endTime }

        // Eğer veri varsa en son veriyi işleyin
        latestRecord?.let {
            val heartRateSamples = it.samples // Kalp atış hızı verileri listesi
            val lastSample = heartRateSamples.last()
            val heartRate = lastSample.beatsPerMinute // Kalp atış hızı (bpm)

            data=heartRate.toString()

        }

        return data
    }



    suspend fun readBodyTemperatureData():String?{
        var data = "Değer Yok"

        val timeRange = TimeRangeFilter.between(Instant.EPOCH, Instant.now())

        val request = ReadRecordsRequest(
            recordType = BodyTemperatureRecord::class,
            timeRangeFilter = timeRange
        )

        val response = healthConnectClient.readRecords(request)

        val latestRecord = response.records.maxByOrNull { it.time }

        latestRecord?.let {
            val bodyTemp  = it.temperature.inCelsius

            data= bodyTemp.toString()
        }
        return data
    }


    suspend fun readHypotensionData():String?{
        var hypotensionData = "Değer Yok"

        val timeRange = TimeRangeFilter.between(Instant.EPOCH, Instant.now())

        val request = ReadRecordsRequest(
            recordType = BloodPressureRecord::class,
            timeRangeFilter = timeRange
        )
        val response = healthConnectClient.readRecords(request)

        val latestRecord = response.records.maxByOrNull { it.time }

        latestRecord?.let {
            val hypo = it.diastolic.inMillimetersOfMercury.toInt()
            hypotensionData = hypo.toString()
        }
        return hypotensionData
    }


    suspend fun readHypertensionData():String?{
        var hypertensionData = "Değer Yok"

        val timeRange = TimeRangeFilter.between(Instant.EPOCH, Instant.now())

        val request = ReadRecordsRequest(
            recordType = BloodPressureRecord::class,
            timeRangeFilter = timeRange
        )
        val response = healthConnectClient.readRecords(request)

        val latestRecord = response.records.maxByOrNull { it.time }

        latestRecord?.let {
           val hyper = it.systolic.inMillimetersOfMercury.toInt()
           hypertensionData = hyper.toString()
        }
        return hypertensionData
    }

    suspend fun readOxygenSaturation():String?{
        var data = "Veri Yok"

        val timeRange = TimeRangeFilter.between(Instant.EPOCH, Instant.now())

        val request = ReadRecordsRequest(
            recordType = OxygenSaturationRecord::class,
            timeRangeFilter = timeRange
        )
        val response = healthConnectClient.readRecords(request)

        val latestRecord = response.records.maxByOrNull { it.time }

        latestRecord?.let {
            val saturation = it.percentage.toString()
            data = saturation
        }
        return data


    }



}
