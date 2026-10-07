package com.example.jhserviceapp.presentation

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.jhserviceapp.R
import com.example.jhserviceapp.databinding.StartFragmentBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StartFragment : Fragment() {
    private var _binding: StartFragmentBinding? = null
    private val binding get() = _binding!!
    private var animation: ObjectAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StartFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            animation = ObjectAnimator.ofPropertyValuesHolder(
                binding.manImage,
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 1500f, -1000f)
            ).apply {
                duration = 3000
                interpolator = AccelerateInterpolator()
                start()
            }
            delay(2500)
            findNavController().navigate(R.id.fromStartPageToMainPage)
        }

    }

    override fun onDestroyView() {
        animation?.cancel()
        animation = null
        _binding = null
        super.onDestroyView()
    }
}