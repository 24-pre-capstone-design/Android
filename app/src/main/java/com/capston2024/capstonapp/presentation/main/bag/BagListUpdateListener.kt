package com.capston2024.capstonapp.presentation.main.bag

import com.capston2024.capstonapp.data.Bag

interface BagListUpdateListener {
    fun onBagListUpdated(updatedList: MutableList<Bag>)
}
