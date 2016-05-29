package net.nonylene.gorokus

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.Realm
import io.realm.RealmConfiguration

import net.nonylene.gorokus.databinding.MainActivityBinding
import net.nonylene.gorokus.databinding.RecyclerItemCategoryBinding
import net.nonylene.gorokus.databinding.RecyclerItemGorokuBinding
import net.nonylene.gorokus.model.Category
import net.nonylene.gorokus.model.Goroku
import net.nonylene.gorokus.model.newCategoryId

class MainActivity : AppCompatActivity() {

    private val adapter = GorokuRecyclerAdapter()

    /**
     * call binding after [onCreate] due to lazy initialize
     */
    val binding: MainActivityBinding by lazy {
        DataBindingUtil.setContentView<MainActivityBinding>(this, R.layout.main_activity)
    }

    val realm by lazy {
        Realm.getInstance(RealmConfiguration.Builder(this).build())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm.executeTransactionAsync(Realm.Transaction { realm ->
            val category = realm.createObject(Category::class.java)
            category.id = newCategoryId(realm)
            category.name = "Hoge"
            val category2 = realm.createObject(Category::class.java)
            category2.id = newCategoryId(realm)
            category2.name = "HogePiyo"
            val category3 = realm.createObject(Category::class.java)
            category3.id = newCategoryId(realm)
            category3.name = "HogePoPoPoe"
        }, Realm.Transaction.OnSuccess {
            adapter.categoryList = realm.where(Category::class.java).findAll()
            adapter.notifyDataSetChanged()
        })

        adapter.setHasStableIds(true)
        with(binding.recyclerView) {
            adapter = this@MainActivity.adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
        adapter.notifyDataSetChanged()
    }

    class GorokuRecyclerAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val TYPE_CATEGORY = 0
        private val TYPE_TEXT = 1

        var categoryList: List<Category> = listOf()
        var gorokuList: List<Goroku> = listOf()

        override fun getItemCount() = categoryList.size + gorokuList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            val posInList = getPositionInList(position)
            // "cannot find class" warnings occurred in intellij / kotlin.
            // delegate to java static method
            // todo: fix this
            when (holder) {
                is CategoryItemViewHolder -> GorokuRecyclerAdapterSupport.setCategoryToBinding(holder.binding, categoryList[posInList])
                is GorokuItemViewHolder -> GorokuRecyclerAdapterSupport.setGorokuToBinding(holder.binding, gorokuList[posInList])
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

        class GorokuItemViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

            val binding: RecyclerItemGorokuBinding

            init {
                binding = DataBindingUtil.bind(itemView)
            }
        }

        class CategoryItemViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

            val binding: RecyclerItemCategoryBinding

            init {
                binding = DataBindingUtil.bind(itemView)
            }
        }
    }
}
