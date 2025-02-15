package com.example.currencyexchangedemo.presentation.view

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.currencyexchangedemo.databinding.ActivityMainBinding
import com.example.currencyexchangedemo.presentation.viewmodel.MainViewModel
import com.example.currencyexchangedemo.presentation.state.ViewState
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val mainViewModel: MainViewModel by viewModels()

    private val currencyGridAdapter = CurrencyGridAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        attachObservers()

        binding.currencyRecycler.layoutManager = GridLayoutManager(this@MainActivity, 2)
        binding.currencyRecycler.adapter = currencyGridAdapter

        binding.valueInput.editText?.addTextChangedListener { editable ->
            if (binding.valueInput.hasFocus()) {
                mainViewModel.updateAmountToConvert(editable?.toString())
            }
        }

        (binding.currencySelector.editText as? MaterialAutoCompleteTextView)?.let { actv ->
            actv.setOnClickListener {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(actv.windowToken, 0)
            }

            actv.setOnItemClickListener { _, _, position, _ ->
                val selectedCurrency = actv.adapter.getItem(position).toString()
                mainViewModel.updateBaseCurrency(selectedCurrency)
                binding.valueInput.editText?.text?.clear()
            }
        }
    }

    private fun attachObservers() {
        mainViewModel.mainViewState.onEach {
            binding.progressBar.isVisible = it.isLoading
            if (it.errorMsg.isNotEmpty()) {
                showError(it.errorMsg)
            }
            updateUi(it)
        }.launchIn(lifecycleScope)
    }

    private fun updateUi(viewState: ViewState) {
        val array = viewState.countryCodes.toTypedArray()
        (binding.currencySelector.editText as? MaterialAutoCompleteTextView)?.apply {
            setSimpleItems(array)
            val item = array.find { it.contains(viewState.base) }
            if (item != null) {
                setText(adapter.getItem(array.indexOf(item)).toString(), false)
            }
        }
        binding.valueInput.editText?.apply {
            if (text.toString() != viewState.amount) {
                setText(viewState.amount)
            }
        }
        currencyGridAdapter.submitList(viewState.rates)
    }

    private fun showError(msg: String) {
        AlertDialog.Builder(this)
            .setMessage(msg)
            .setNeutralButton("Okay") { dialogInterface, _ ->
                dialogInterface.dismiss()
                finish()
            }
            .show()
    }
}
