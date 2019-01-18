package timber.log

import com.michaelflisar.lumberjack.data.StackData


/**
 * Created by flisar on 17.01.2019.
 */

abstract class BaseTree : Timber.Tree() {

    companion object {
        internal val CALL_STACK_INDEX = 6
    }

    internal override fun getTag(): String? {
        // 1) return default tag if one exists
        val tag = super.getTag()
        if (tag != null) {
            return tag
        }

        // 2) create a custom tag for the logs => we use the
        val stackData = StackData.create(CALL_STACK_INDEX)
        return "[${stackData.getStackTag()}]"
    }

    protected fun formatLine(tag: String?, message: String) = "[$tag]: $message"
}