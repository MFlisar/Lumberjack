package timber.log;

/**
 * Created by flisar on 20.10.2016.
 */

import android.util.Log;

import com.michaelflisar.lumberjack.L;
import com.michaelflisar.lumberjack.LogUtil;

public class DebugTree extends Timber.Tree
{
    private static final int MAX_LOG_LENGTH = 4000;
    private static final int CALL_STACK_INDEX = 7;

    private final boolean mCombineTags;

    public DebugTree(boolean combineTags)
    {
        mCombineTags = combineTags;
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
        String stackTag = LogUtil.getStackTag(CALL_STACK_INDEX);

        if (tag == null)
            return stackTag;

        return "<" + tag + "> " + stackTag;
    }

    /**
     * Break up {@code message} into maximum-length chunks (if needed) and send to either
     * {@link Log#println(int, String, String) Log.println()} or
     * {@link Log#wtf(String, String) Log.wtf()} for logging.
     *
     * {@inheritDoc}
     */
    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (message.length() < MAX_LOG_LENGTH) {
            logLine(priority, tag, message);
            return;
        }

        // Split by line, then ensure each line can fit into Log's maximum length.
        for (int i = 0, length = message.length(); i < length; i++) {
            int newline = message.indexOf('\n', i);
            newline = newline != -1 ? newline : length;
            do {
                int end = Math.min(newline, i + MAX_LOG_LENGTH);
                String part = message.substring(i, end);
                L.getFormatter().formatLine(tag, part);
                i = end;
            } while (i < newline);
        }
    }

    private void logLine(int priority, String tag, String message)
    {
        String logMessage = L.getFormatter().formatLine(tag, message);

        if (priority == Log.ASSERT) {
            Log.wtf(tag, logMessage);
        } else {
            Log.println(priority, tag, logMessage);
        }
    }
}