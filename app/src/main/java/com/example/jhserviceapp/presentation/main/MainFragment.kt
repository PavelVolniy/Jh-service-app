package com.example.jhserviceapp.presentation.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.jhserviceapp.App
import com.example.jhserviceapp.R
import com.example.jhserviceapp.domain.entity.report.ReportWithArticleAndCount
import com.example.jhserviceapp.presentation.addreport.AddReportWithArticleViewModel
import com.example.jhserviceapp.presentation.util.FormatDateUtil
import javax.inject.Inject

class MainFragment : Fragment() {


    @Inject
    lateinit var reportViewModel: ReportViewModel

    @Inject
    lateinit var addReportViewModel: AddReportWithArticleViewModel

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as App).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MainView(
                    onFilteredTextChanged = { reportViewModel.filterListByRequest(it) },
                    listReport = reportViewModel.listReports.collectAsState().value,
                    onClickShare = { itemReport -> copyToClipboard(itemReport) },
                    onClickSettings = {
//                        findNavController().navigate(R.id.fromMainPageToSettingsPage)
                        findNavController().navigate(R.id.fromMainPageToWaybill)
                    },
                    onSwipeToDelete = { reportViewModel.deleteReport(it) },
                    onClickAdd = { findNavController().navigate(R.id.fromMainPageToCreateReportPage) },
                    onClickSaveReportWithArticleAndCount = {
                        addReportViewModel.addReport(it)
                        reportViewModel.updateData()
                    }
                )
            }
        }
    }

    private fun copyToClipboard(onSharedItem: ReportWithArticleAndCount) {
        Log.e("report", onSharedItem.toString())
        val articleList = StringBuilder()
        onSharedItem.articles.forEach {
            val count: Double = (it.articleCount.articleCountId / 10.0)
            articleList.append("${it.articleDTO.articleId} ${it.articleDTO.articleName} $count\n")
        }
        val systemService =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val stringItem = StringBuilder()
        stringItem.append("дата: ${FormatDateUtil.getDateToStringYYYYMMDD(onSharedItem.report.date)}\n")
        stringItem.append("погрузчик: ${onSharedItem.report.numberLoader} часы: ${onSharedItem.report.hours}\n\n")
        if (articleList.isNotEmpty()) {
            stringItem.append("артикулы:\n$articleList\n")
        }
        onSharedItem.report.placeOfOperations.let { if (it.isNotEmpty()) stringItem.append("Организация: $it\n") }
        stringItem.append("${onSharedItem.report.description}\n")
        onSharedItem.report.internalComments.let { if (it.isNotEmpty()) stringItem.append(it) }

        val clip = ClipData.newPlainText("item", stringItem)
        systemService.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "item was copy", Toast.LENGTH_SHORT).show()
    }


}
