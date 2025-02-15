package com.example.currencyexchangedemo.presentation.view

import android.annotation.SuppressLint
import android.icu.util.Currency
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchangedemo.databinding.CurrencyItemBinding
import com.example.currencyexchangedemo.presentation.model.CurrencyModel

class CurrencyGridAdapter :
    ListAdapter<CurrencyModel, CurrencyGridAdapter.ViewHolder>(CurrencyDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(CurrencyItemBinding.inflate(inflater))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: CurrencyItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("DefaultLocale", "SetTextI18n")
        fun bind(model: CurrencyModel) {
            binding.code.text = "${model.name} (${model.code})"
            binding.value.text = String.format(
                "%s %.2f",
                Currency.getInstance(model.code).symbol,
                (model.rate * model.amount)
            )
        }
    }

    object CurrencyDiffCallback : DiffUtil.ItemCallback<CurrencyModel>() {
        override fun areItemsTheSame(oldItem: CurrencyModel, newItem: CurrencyModel): Boolean {
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(oldItem: CurrencyModel, newItem: CurrencyModel): Boolean {
            return oldItem == newItem
        }
    }
}
