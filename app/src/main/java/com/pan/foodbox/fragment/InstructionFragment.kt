package com.pan.foodbox.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pan.foodbox.databinding.FragmentInstructionBinding
import com.pan.foodbox.models.RestaurantMenu


class InstructionFragment : Fragment() {

    private lateinit var restaurantMenu: RestaurantMenu
    private var _binding: FragmentInstructionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentInstructionBinding.inflate(inflater, container, false)
        val instruction = arguments?.getParcelable<RestaurantMenu>("list").let {
            restaurantMenu = it!!
        }

        instruction.apply {
            binding.instructionId.loadUrl(restaurantMenu.instruction)
        }

        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}