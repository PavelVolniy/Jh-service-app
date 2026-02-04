package com.example.jhserviceapp.presentation.addreport

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.jhserviceapp.App
import com.example.jhserviceapp.R
import com.example.jhserviceapp.presentation.scan.Permissions
import javax.inject.Inject


class AddReportFragment : Fragment() {
    private var permissionGranted = false
    private val launcher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        permissionGranted = Permissions.checkPermissionsCamera(requireContext())
    }

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var addReportWithArticleViewModel: AddReportWithArticleViewModel

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
//                requestPermission()
                AddReportView(
                    onClickSave = {
                        addReportWithArticleViewModel.addReport(it)
                        findNavController().navigate(R.id.fromCreateReportPageToMainPage)
                    },
                    onClickCancel = {
                        findNavController().navigate(R.id.fromCreateReportPageToMainPage)
                    })
            }
        }

    }

    private fun requestPermission() {
        if (!permissionGranted) {
            launcher.launch(Permissions.REQUIRED_PERMISSIONS_CAMERA)
        }
    }
}