package net.nonylene.gorokus;

import net.nonylene.gorokus.databinding.RecyclerItemCategoryBinding;
import net.nonylene.gorokus.databinding.RecyclerItemGorokuBinding;
import net.nonylene.gorokus.model.Category;
import net.nonylene.gorokus.model.Goroku;

// support class for suppress error on intellij / kotlin
// "cannot find class" warnings occurred in intellij / kotlin.
// delegate to java static method
// todo: fix this
public class GorokuRecyclerAdapterSupport {

    public static void setCategoryToBinding(RecyclerItemCategoryBinding binding, Category category) {
       binding.setCategory(category);
    }

    public static void setGorokuToBinding(RecyclerItemGorokuBinding binding, Goroku category) {
        binding.setGoroku(category);
    }
}
