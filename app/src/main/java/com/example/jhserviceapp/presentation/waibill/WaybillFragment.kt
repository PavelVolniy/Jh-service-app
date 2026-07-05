package com.example.jhserviceapp.presentation.waibill

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.jhserviceapp.presentation.util.ParseDate


class WaybillFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val test = ParseDate.getDateFromReceiptString("t=20250412&sdf3as53d4as341")
                Log.e("date", test)
                val test2 = ParseDate.getDateMillisFromReceiptString("t=20250514&sdf3as53d4as341")
                Log.e("dateMillis", test2.toString())
                WayBillView(
                    onclickBack = { findNavController().popBackStack() }
                )
            }
        }
    }
}