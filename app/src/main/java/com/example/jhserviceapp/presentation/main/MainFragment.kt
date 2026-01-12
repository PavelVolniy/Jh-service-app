package com.example.jhserviceapp.presentation.main

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.jhserviceapp.App
import com.example.jhserviceapp.R
import com.example.jhserviceapp.databinding.MainFragmentBinding
import com.example.jhserviceapp.domain.entity.report.ReportWithArticleAndCount
import com.example.jhserviceapp.presentation.adapters.ReportAdapter
import com.example.jhserviceapp.presentation.util.FormatDateUtil
import javax.inject.Inject

class MainFragment : Fragment() {
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private val adapter = ReportAdapter(
        { onLongClick ->
            onLongClick.report.id?.let {
                sharedPreferences.edit().putString(App.EDITABLE_REPORT, it.toString()).apply()
            }
            findNavController().navigate(R.id.fromMainPageToCreateReportPage)
        },
        { onSharedItem -> copyToClipboard(onSharedItem) })

    @Inject
    lateinit var reportViewModel: ReportViewModel

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as App).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
//        _binding = MainFragmentBinding.inflate(layoutInflater)
//        binding.reportRecyclerView.adapter = adapter
//        binding.reportRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        setupSwipeListener(binding.reportRecyclerView)
//        setHasOptionsMenu(true)
//        return binding.root
        return ComposeView(requireContext()).apply {
            setContent {
                MainView(
                    onFilteredTextChanged = { reportViewModel.filterListByRequest(it) },
                    listReport = reportViewModel.listReports.collectAsState().value
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> findNavController().navigate(R.id.fromMainPageToSettingsPage)
        }
        return super.onOptionsItemSelected(item)
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        with(binding) {
//            addReportButton.setOnClickListener {
//                findNavController().navigate(R.id.fromMainPageToCreateReportPage)
//            }
//
//            filterRequest.addTextChangedListener {
//                if (!it.isNullOrBlank()) {
//                    viewLifecycleOwner.lifecycleScope.launch {
//                        reportViewModel.filterListByRequest(filterRequest.text.toString())
//                    }
//                } else {
//                    viewLifecycleOwner.lifecycleScope.launch {
//                        reportViewModel.updateData()
//                    }
//                }
//            }
//
//        }
//        reportViewModel.listReports.onEach {
//            adapter.submitList(it)
//        }.launchIn(viewLifecycleOwner.lifecycleScope)
//
//    }


    private fun setupSwipeListener(recyclerView: RecyclerView) {
        val callBack = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = adapter.currentList[viewHolder.absoluteAdapterPosition]
                val builderDialog = AlertDialog.Builder(requireContext())
                builderDialog.setMessage(getString(R.string.question_delete_dialog))
                    .setPositiveButton("OK") { dialog, which ->
                        reportViewModel.removeReport(item)
                        adapter.notifyItemRemoved(viewHolder.absoluteAdapterPosition)
                    }
                    .setNegativeButton("NOT") { dialog, which ->
                        dialog.cancel()
                        adapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                    }
                val dialog = builderDialog.create()
                dialog.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(callBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
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
        stringItem.append("date: ${FormatDateUtil.getDateToStringDDMMYY(onSharedItem.report.date)}\n")
        stringItem.append("loader: ${onSharedItem.report.numberLoader} hours: ${onSharedItem.report.hours}\n\n")
        if (articleList.isNotEmpty()) {
            stringItem.append("articles:\n$articleList\n")
        }
        stringItem.append("${onSharedItem.report.description}\n")
        stringItem.append(onSharedItem.report.internalComments)

        val clip = ClipData.newPlainText("item", stringItem)
        systemService.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "item was copy", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
