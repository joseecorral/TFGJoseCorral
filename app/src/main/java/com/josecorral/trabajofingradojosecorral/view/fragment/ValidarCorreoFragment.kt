package com.josecorral.trabajofingradojosecorral.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.josecorral.trabajofingradojosecorral.databinding.FragmentValidarCorreoBinding

class ValidarCorreoFragment : Fragment() {

    private lateinit var binding: FragmentValidarCorreoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentValidarCorreoBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}