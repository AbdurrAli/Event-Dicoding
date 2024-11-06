package com.example.dicodingevent.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.dicodingevent.databinding.FragmentNetworkStatusBinding

class NetworkStatusFragment : Fragment() {

    // Using nullable binding to prevent memory leaks
    private var _binding: FragmentNetworkStatusBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initializing view binding with layout inflater
        _binding = FragmentNetworkStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up an onClickListener for the "Check Connection" button
        binding.btnCheckConnection.setOnClickListener {
            // Use popBackStack to return to the previous fragment when the button is clicked
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the binding reference to prevent memory leaks
        _binding = null
    }
}
