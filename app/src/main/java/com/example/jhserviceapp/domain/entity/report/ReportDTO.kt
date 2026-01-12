package com.example.jhserviceapp.domain.entity.report

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.jhserviceapp.domain.entity.Report


@Entity(tableName = "reports")
data class ReportDTO(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("reportId")
    override val id: Long? = null,
    @ColumnInfo("numberLoader")
    override val numberLoader: String,
    @ColumnInfo("date")
    override val date: Long,
    @ColumnInfo("hours")
    override val hours: Int,
    @ColumnInfo("description")
    override val description: String,
    @ColumnInfo("userName")
    override val userName: String,
    @ColumnInfo("userCode")
    override val userNumber: String,
    @ColumnInfo("placeOfOperations")
    override val placeOfOperations: String,
    @ColumnInfo("typeOfOperations")
    override val typeOfOperations: String,
    @ColumnInfo("internalComments")
    override val internalComments: String
) : Report