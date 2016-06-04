package net.nonylene.gorokus.layout

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

@Suppress("unused")
class FABRecyclerViewScrollBehavior : FloatingActionButton.Behavior {

    constructor(): super()

    constructor(context: Context, attributeSet: AttributeSet): this()

    override fun layoutDependsOn(parent: CoordinatorLayout?, child: FloatingActionButton?, dependency: View?): Boolean {
        return dependency is RecyclerView
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout?, child: FloatingActionButton?, directTargetChild: View?, target: View?, nestedScrollAxes: Int): Boolean {
        return true
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout?, child: FloatingActionButton, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
        if (dyConsumed > 0 && child.visibility == View.VISIBLE) {
            child.hide();
        } else if (dyConsumed < 0 && child.visibility != View.VISIBLE) {
            child.show();
        }
    }
}
