package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.MaskokiRepository
import android.content.Context

class MaskokiViewModelFactory(
    private val repository: MaskokiRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MaskokiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MaskokiViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
