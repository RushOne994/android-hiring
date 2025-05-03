package com.example.myapplication.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.model.GenderTypes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    var binding: ActivityMainBinding? = null

    val viewModel: MainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initViews()
        observeViewModel()
    }

    private fun initViews() {
        binding?.cvMale?.setOnClickListener {
            viewModel.handleAction(MainActions.SelectGender(GenderTypes.Male))
        }

        binding?.cvFemale?.setOnClickListener {
            viewModel.handleAction(MainActions.SelectGender(GenderTypes.Female))
        }

        val ages = mutableListOf<Int>().also { list ->
            repeat(100) {
                list.add(it)
            }
        }
        val agesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ages)
        // Определяем разметку для использования при выборе элемента
        agesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.spinner?.adapter = agesAdapter
        binding?.spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = parent.getItemAtPosition(position) as Int
                viewModel.handleAction(MainActions.SelectAge(item))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding?.button1?.setOnClickListener {
            viewModel.handleAction(MainActions.OnSubmit)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.screenState.collect {
                    when (it.selectedGender) {
                        GenderTypes.Male -> {
                            binding?.cvMale?.setStrokeColor(ColorStateList.valueOf(getColor(R.color.blue_500)))
                            binding?.cvFemale?.setStrokeColor(ColorStateList.valueOf(Color.TRANSPARENT))
                        }

                        GenderTypes.Female -> {
                            binding?.cvMale?.setStrokeColor(ColorStateList.valueOf(Color.TRANSPARENT))
                            binding?.cvFemale?.setStrokeColor(ColorStateList.valueOf(getColor(R.color.pink_500)))
                        }

                        null -> {
                            binding?.cvMale?.setStrokeColor(ColorStateList.valueOf(Color.TRANSPARENT))
                            binding?.cvFemale?.setStrokeColor(ColorStateList.valueOf(Color.TRANSPARENT))
                        }
                    }
                    binding?.button1?.isEnabled = it.isSubmitEnabled
                    binding?.tvResult?.text = it.result.toString()
                }
            }
        }
    }
}