package net.nonylene.gorokus

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.nonylene.gorokus.databinding.RecyclerItemCategoryBinding
import net.nonylene.gorokus.databinding.RecyclerItemGorokuBinding
import net.nonylene.gorokus.model.Category
import net.nonylene.gorokus.model.Goroku

class GorokuRecyclerAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_CATEGORY = 0
    private val TYPE_TEXT = 1

    var categoryList: List<Category> = listOf()
    var gorokuList: List<Goroku> = listOf()

    var gorokuListener: ((goroku: Goroku) -> Unit)? = null
    var categoryListener: ((category: Category) -> Unit)? = null

    override fun getItemCount() = categoryList.size + gorokuList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val posInList = getPositionInList(position)
        when (holder) {
            is CategoryItemViewHolder -> holder.bind(categoryList[posInList])
            is GorokuItemViewHolder -> holder.bind(gorokuList[posInList])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return when (viewType) {
            TYPE_TEXT -> GorokuItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_category, parent, false))
            TYPE_CATEGORY -> CategoryItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_category, parent, false))
            else -> null
        }
    }

    // bit shift!
    override fun getItemId(position: Int): Long {
        val posInList = getPositionInList(position)
        return when (getItemViewType(position)) {
            TYPE_CATEGORY -> categoryList[posInList].id.toLong()
            TYPE_TEXT -> gorokuList[posInList].id.toLong() shl 32
            else -> 0
        }
    }

    private fun getPositionInList(position: Int): Int {
        return when(getItemViewType(position)) {
            TYPE_CATEGORY -> position
            TYPE_TEXT -> position - categoryList.size
            else -> position
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position < categoryList.size -> TYPE_CATEGORY
            else -> TYPE_TEXT
        }
    }

    inner class GorokuItemViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        val binding: RecyclerItemGorokuBinding

        init {
            binding = DataBindingUtil.bind(itemView)
        }

        fun bind(goroku: Goroku) {
            GorokuRecyclerAdapterSupport.setGorokuToBinding(binding, goroku)
            binding.root.setOnClickListener {
                gorokuListener?.invoke(goroku)
            }
        }
    }

    inner class CategoryItemViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        val binding: RecyclerItemCategoryBinding

        init {
            binding = DataBindingUtil.bind(itemView)
        }

        fun bind(category: Category) {
            GorokuRecyclerAdapterSupport.setCategoryToBinding(binding, category)
            binding.root.setOnClickListener {
                categoryListener?.invoke(category)
            }
        }
    }
}
