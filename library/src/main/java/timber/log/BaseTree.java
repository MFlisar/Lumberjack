package timber.log;

import com.michaelflisar.lumberjack.L;
import com.michaelflisar.lumberjack.LogUtil;
import com.michaelflisar.lumberjack.filter.ILogFilter;

/**
 * Created by flisar on 08.11.2016.
 */

public abstract class BaseTree extends Timber.Tree {
    final ThreadLocal<Integer> callStackCorrection = new ThreadLocal<>();
    private static final int CALL_STACK_INDEX = 8;
    protected static final int MAX_LOG_LENGTH = 4000;
    protected final boolean mCombineTags;
    protected final boolean mWithLink;
    protected LogUtil.StackData mStackData = null;
    protected ILogFilter mFilter;

    public BaseTree(boolean combineTags, boolean withLink, ILogFilter filter) {
        mCombineTags = combineTags;
        mWithLink = withLink;
        mFilter = filter;
    }

    @Override
    final String getTag() {
        String tag = super.getTag();
        if (tag != null && !mCombineTags) {
            return tag;
        }

        Integer stackCorrection = getCallStackCorrection();
        if (stackCorrection == null)
            stackCorrection = 0;

        // DO NOT switch this to Thread.getCurrentThread().getStackTrace(). The test will pass
        // because Robolectric runs them on the JVM but on Android the elements are different.
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace.length <= CALL_STACK_INDEX + stackCorrection) {
            throw new IllegalStateException(
                    "Synthetic stacktrace didn't have enough elements: are you using proguard?");
        }
        mStackData = LogUtil.getStackData(CALL_STACK_INDEX + stackCorrection);
        String stackTag = mStackData.getStackTag();

        if (tag == null)
            return stackTag;

        return L.getFormatter().combineTagAndGroup(stackTag, tag);
    }

    @Override
    protected final void log(int priority, String tag, String message, Throwable t) {

        // check if logger is ready
        if (!isReady())
            return;

        // check the filter, if one is provided
        String realTag = tag;
        if (mCombineTags)
            realTag = L.getFormatter().extractGroupFromTag(tag);
        if (mFilter != null && !mFilter.valid(realTag, priority))
            return;

        // log
        doLog(priority, tag, message, t);
    }

    protected boolean isReady() {
        return true;
    }

    protected abstract void doLog(int priority, String tag, String message, Throwable t);

    private Integer getCallStackCorrection() {
        Integer correction = callStackCorrection.get();
        if (correction != null) {
            callStackCorrection.remove();
        }
        return correction;
    }
}