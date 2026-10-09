package com.pourya.wardrobe.ui.item

import androidx.navigation.NavDirections
import com.pourya.wardrobe.R

// ---- Args ----
class ItemFragmentArgs(val storageId: Long) {
    companion object {
        fun fromBundle(bundle: android.os.Bundle): ItemFragmentArgs {
            return ItemFragmentArgs(bundle.getLong("storageId", -1L))
        }
    }
}

// This extension makes navArgs() work
fun androidx.fragment.app.Fragment.itemFragmentArgs(): ItemFragmentArgs {
    return ItemFragmentArgs.fromBundle(requireArguments())
}

// ---- Directions ----
object ItemFragmentDirections {
    fun actionItemFragmentToItemDetailFragment(itemId: Long): NavDirections {
        return object : NavDirections {
            override val actionId: Int = R.id.action_itemFragment_to_itemDetailFragment
            override val arguments: android.os.Bundle = android.os.Bundle().apply {
                putLong("itemId", itemId)
            }
        }
    }
}

// ---- Detail Args ----
class ItemDetailFragmentArgs(val itemId: Long) {
    companion object {
        fun fromBundle(bundle: android.os.Bundle): ItemDetailFragmentArgs {
            return ItemDetailFragmentArgs(bundle.getLong("itemId", -1L))
        }
    }
}
