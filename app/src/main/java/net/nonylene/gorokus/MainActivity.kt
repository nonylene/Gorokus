package net.nonylene.gorokus

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmConfiguration

import net.nonylene.gorokus.databinding.MainActivityBinding
import net.nonylene.gorokus.model.*

val MUSHROOM_ACTION = "com.adamrocker.android.simeji.ACTION_INTERCEPT"

class MainActivity : AppCompatActivity() {

    private val adapter = GorokuRecyclerAdapter()
    private var category: Category? = null
        set(value) {
            if (value != null) {
                title = getString(R.string.app_name) + " - " + value.name
            } else {
                title = getString(R.string.app_name)
            }
            field = value
        }

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

        category = realm.where(Category::class.java).equalTo("id", 1).findFirst()

        // initialize test data
        if (realm.where(Goroku::class.java).findAll().isEmpty()) {
            realm.executeTransactionAsync(Realm.Transaction { realm ->
                val root = realm.where(Category::class.java).equalTo("id", 1).findFirst()
                root.gorokus.add(
                        realm.createObject(Goroku::class.java).apply {
                            id = newGorokuId(realm)
                            text = "Piyo"
                            kana = "kana,kanatest"
                        }
                )
                root.gorokus.add(
                        realm.createObject(Goroku::class.java).apply {
                            id = newGorokuId(realm)
                            text = "Hoge2"
                            kana = "kana2,nonylene"
                        }
                )
                val a = realm.createObject(Category::class.java).apply {
                    id = newCategoryId(realm)
                    name = "Hoge"
                }
                root.categories.add(a)
                a.gorokus.add(
                        realm.createObject(Goroku::class.java).apply {
                            id = newGorokuId(realm)
                            text = "Hoge3"
                            kana = "kana4,test"
                        }
                )
            }, Realm.Transaction.OnSuccess {
                adapter.categoryList = realm.where(Category::class.java).findAll()
                adapter.notifyDataSetChanged()
            })
        }

        adapter.setHasStableIds(true)
        adapter.categoryListener = { category ->
            this@MainActivity.category = category
            binding.searchEditText.setText(null)
        }
        adapter.gorokuListener = { goroku ->
            // or copy to clipboard?
            realm.executeTransaction {
                goroku.count++
            }
            if (intent.action?.equals(MUSHROOM_ACTION) ?: false) {
                setResult(RESULT_OK, Intent().putExtra("replace_key", goroku.text))
                finish()
            } else {
                (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).primaryClip =
                        ClipData.newPlainText("Copied goroku", goroku.text)
                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            }
        }

        with(binding.recyclerView) {
            adapter = this@MainActivity.adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
        adapter.categoryList = realm.where(Category::class.java).findAll()
        adapter.notifyDataSetChanged()

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isEmpty()) {
                    adapter.gorokuList = category!!.gorokus
                } else {
                    adapter.gorokuList = findAllChildGorokus(category!!).filter { it.includes(s.toString()) }
                }
                adapter.categoryList = findAllChildCategories(category!!).filter { it.name.contains(s.toString(), true) }
                adapter.notifyDataSetChanged()
            }

        })
        binding.searchEditText.setText(intent.getStringExtra("replace_key"))
    }
}
