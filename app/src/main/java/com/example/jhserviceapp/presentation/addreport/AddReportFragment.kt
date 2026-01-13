package com.example.jhserviceapp.presentation.addreport

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.jhserviceapp.App
import com.example.jhserviceapp.R
import com.example.jhserviceapp.databinding.AddReportFragmentBinding
import com.example.jhserviceapp.presentation.adapters.AddArticleAdapter
import javax.inject.Inject

const val CHAR_COUNT_8 = 8
const val CHAR_COUNT_16 = 16

class AddReportFragment : Fragment() {

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var addReportWithArticleViewModel: AddReportWithArticleViewModel

    private val adapter = AddArticleAdapter {
        addReportWithArticleViewModel.removeArticleRow(it)
    }

    private val filterMax8 = InputFilter.LengthFilter(CHAR_COUNT_8)
    private val filterMax16 = InputFilter.LengthFilter(CHAR_COUNT_16)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as App).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AddReportView(
                    onClickSave = {
                        findNavController().navigate(R.id.fromCreateReportPageToMainPage)
                    },
                    onClickCancel = {
                        findNavController().navigate(R.id.fromCreateReportPageToMainPage)
                    })
            }
        }

    }


//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        with(binding) {
//            loaderNumber.requestFocus()
//            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.showSoftInput(loaderNumber, InputMethodManager.SHOW_IMPLICIT)
//            saveButton.isVisible = false
//            val listTypesOfOperations = resources.getStringArray(R.array.typesOfOperations)
//            if (binding.radioGroup.childCount == 0) {
//                for (i in listTypesOfOperations.indices) {
//                    val button = RadioButton(requireContext())
//                    button.text = listTypesOfOperations[i]
//                    button.id = i
//                    binding.radioGroup.addView(button)
//                }
//            }
//            radioGroup.check(radioGroup.getChildAt(0).id)
//            loaderNumber.inputType = InputType.TYPE_CLASS_NUMBER
//            loaderNumber.filters = emptyArray()
//            loaderNumber.filters += filterMax8
//            hours.hint = "0000"
//            dateButton.text = FormatDateUtil.getDateToStringDDMMYY(System.currentTimeMillis())
//            fnButton.setOnClickListener {
//                loaderNumber.setText(getString(R.string.fn_inject_text_to_loader_number_field))
//                loaderNumber.setSelection(binding.loaderNumber.length())
//            }
//            addDefaultArtButton.setOnClickListener {
//                addReportWithArticleViewModel.addDefaultArticlesToArticleList()
//            }
//            editLoaderFieldButton.setOnClickListener {
//                loaderNumber.inputType = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
//                loaderNumber.filters = emptyArray()
//                loaderNumber.filters += filterMax16
//            }
//
//            dateButton.setOnClickListener {
//                showDatePickerDialog()
//            }
//            saveButton.setOnClickListener {
//                val checkedRadioButtonId = radioGroup.checkedRadioButtonId
//                viewLifecycleOwner.lifecycleScope.launch {
//                    sendDataToVM(checkedRadioButtonId)
//                }
//                findNavController().navigate(R.id.fromCreateReportPageToMainPage)
//            }
//            cancelButton.setOnClickListener {
//                sharedPref.edit().putString(App.EDITABLE_REPORT, null).apply()
//                findNavController().navigate(R.id.fromCreateReportPageToMainPage)
//            }
//            loaderNumber.addTextChangedListener {
//                fnButton.isClickable = it?.length == 0
//                saveButton.isVisible = it?.length!! >= CHAR_COUNT_8
//                if (it.length == CHAR_COUNT_8) {
//                    hours.requestFocus()
//                }
//            }
//            addArticleButton.setOnClickListener {
//                addReportWithArticleViewModel.addNewArticleRow()
//                articleRecyclerView.layoutManager?.smoothScrollToPosition(
//                    articleRecyclerView,
//                    null,
//                    adapter.itemCount
//                )
//            }
//        }
//        addReportWithArticleViewModel.articles.onEach {
//            adapter.submitList(it)
//        }.launchIn(viewLifecycleOwner.lifecycleScope)
//        addReportWithArticleViewModel.editableReport.onEach {
//            it?.let { it1 -> setEditableData(it1) }
//        }.launchIn(viewLifecycleOwner.lifecycleScope)
//    }

    private fun AddReportFragmentBinding.sendDataToVM(
        checkedRadioButtonId: Int
    ) {
        addReportWithArticleViewModel.addReport(
            numberLoader = loaderNumber.text.toString(),
            date = System.currentTimeMillis(),
            hours = if (hours.text.isNotEmpty()) hours.text.toString().toInt()
            else 0,
            description = description.text.toString(),
            userName = sharedPref.getString(App.USER_NAME, "").toString(),
            placeOfOperations = placeOfOperations.text.toString(),
            typeOfOperations = (radioGroup.getChildAt(checkedRadioButtonId)
                    as RadioButton).text.toString(),
            internalComments = internalComments.text.toString(),
            userNumber = sharedPref.getString(App.USER_NUMBER, "0000")
                .toString()
        )
    }

//    private fun showDatePickerDialog() {
//        val datePicker = MaterialDatePicker.Builder.datePicker()
//            .setTitleText(getString(R.string.select_data_dialog_date_picker))
//            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
//            .build()
//        datePicker.show(parentFragmentManager, "date")
//        datePicker.addOnPositiveButtonClickListener {
//            binding.dateButton.text = datePicker.selection?.let { it1 ->
//                FormatDateUtil.getDateToStringDDMMYY(
//                    it1
//                )
//            }
//        }
//    }

//    private fun setEditableData(report: ReportWithArticleAndCount) {
//        with(binding) {
//            loaderNumber.setText(report.report.numberLoader)
//            hours.setText(report.report.hours.toString())
//            description.setText(report.report.description)
//            internalComments.setText(report.report.internalComments)
//            dateButton.text = FormatDateUtil.getDateToStringDDMMYY(report.report.date)
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
//        _binding = null
    }
}