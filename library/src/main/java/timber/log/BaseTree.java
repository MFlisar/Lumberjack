package timber.log;

import android.util.Log;

import com.michaelflisar.lumberjack.L;
import com.michaelflisar.lumberjack.LogUtil;

/**
 * Created by flisar on 08.11.2016.
 */

public abstract class BaseTree extends Timber.Tree
{
    private static final int CALL_STACK_INDEX = 7;

    protected static final int MAX_LOG_LENGTH = 4000;

    protected final boolean mCombineTags;
    protected final boolean mWithLink;
    protected LogUtil.StackData mStackData = null;

    public BaseTree(boolean combineTags, boolean withLink)
    {
        mCombineTags = combineTags;
        mWithLink = withLink;
    }

    @Override
    final String getTag()
    {
        String tag = super.getTag();
        if (tag != null && !mCombineTags) {
            return tag;
        }

        // DO NOT switch this to Thread.getCurrentThread().getStackTrace(). The test will pass
        // because Robolectric runs them on the JVM but on Android the elements are different.
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace.length <= CALL_STACK_INDEX) {
            throw new IllegalStateException(
                    "Synthetic stacktrace didn't have enough elements: are you using proguard?");
        }
        mStackData = LogUtil.getStackData(CALL_STACK_INDEX);
        String stackTag = mStackData.getStackTag();

        if (tag == null)
            return stackTag;

        return L.getFormatter().combineTagAndGroup(stackTag, tag);
    }
}