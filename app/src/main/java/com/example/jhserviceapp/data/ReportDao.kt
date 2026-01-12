package com.example.jhserviceapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.jhserviceapp.domain.entity.Report
import com.example.jhserviceapp.domain.entity.article.ArticleCount
import com.example.jhserviceapp.domain.entity.article.ArticleCountCrossRef
import com.example.jhserviceapp.domain.entity.article.ArticleDTO
import com.example.jhserviceapp.domain.entity.report.ReportArticleCrossRef
import com.example.jhserviceapp.domain.entity.report.ReportDTO
import com.example.jhserviceapp.domain.entity.report.ReportWithArticleAndCount


@Dao
interface ReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addReport(report: ReportDTO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addArticles(articles: List<ArticleDTO>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addArticle(articles: ArticleDTO)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addArticleCounts(counts: List<ArticleCount>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addArticleCount(counts: ArticleCount)

    @Insert
    suspend fun addArticleWithCountCrossRef(articleCountCrossRef: List<ArticleCountCrossRef>)

    @Insert
    suspend fun addReportWithArticleCrossRef(reportArticleCrossRef: List<ReportArticleCrossRef>)

    @Transaction
    @Query("SELECT * FROM reports ORDER BY reportId DESC LIMIT 20")
    suspend fun getLast20Reports(): List<ReportWithArticleAndCount>

    @Transaction
    @Query("SELECT * FROM reports ORDER BY reportId DESC")
    suspend fun getAllReports(): List<ReportWithArticleAndCount>

    @Transaction
    @Query("SELECT * FROM reports WHERE description LIKE :request ORDER BY reportId DESC")
    suspend fun getReportByDescription(request: String): List<ReportWithArticleAndCount>

    @Transaction
    @Query("SELECT * FROM reports WHERE numberLoader LIKE :request ORDER BY reportId DESC")
    suspend fun getReportBySerialNumber(request: String): List<ReportWithArticleAndCount>

    @Query("SELECT * FROM reports WHERE reportId = :id")
    suspend fun getReportById(id: Long): ReportWithArticleAndCount

    @Query("SELECT * FROM articles")
    suspend fun getArticles(): List<ArticleDTO>

    @Query("SELECT * FROM article_count")
    suspend fun getArtCount(): List<ArticleCount>

    @Query("SELECT * FROM articles WHERE isDefault = 1")
    suspend fun getDefaultArticles(): List<ArticleDTO>

    @Query("DELETE FROM report_article_cross_ref WHERE reportId = :refId")
    suspend fun deleteReportArticleCrossRef(refId: Long)

    @Query("SELECT * FROM report_article_cross_ref WHERE reportId = :refId")
    suspend fun getReportArticleCrossRef(refId: Long): List<ReportArticleCrossRef>

    @Query("SELECT reportId FROM reports ORDER BY reportId DESC LIMIT 1")
    suspend fun getLastReportId(): Long

    @Delete
    suspend fun deleteReport(reportDTO: ReportDTO)

    @Delete
    suspend fun deleteArticles(articles: List<ArticleDTO>)

    @Query("DELETE FROM article_count_cross_ref WHERE byReportId = :refId")
    suspend fun delArticleWithCountCrossRef(refId: Long)

}