package com.pourya.wardrobe.ui.storage

import androidx.navigation.NavDirections
import com.pourya.wardrobe.R

object StorageFragmentDirections {
    fun actionStorageFragmentToItemFragment(storageId: Long): NavDirections {
        return object : NavDirections {
            override val actionId: Int = R.id.action_storageFragment_to_itemFragment
            override val arguments: android.os.Bundle = android.os.Bundle().apply {
                putLong("storageId", storageId)
            }
        }
    }
}
