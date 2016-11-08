package timber.log;

/**
 * Created by flisar on 20.10.2016.
 */

import android.util.Log;

import com.michaelflisar.lumberjack.L;
import com.michaelflisar.lumberjack.LogUtil;

public class ConsoleTree extends BaseTree
{
    public ConsoleTree(boolean combineTags, boolean withLink)
    {
        super(combineTags, withLink);
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {

        if (mStackData != null && mWithLink)
            message = mStackData.appendLink(message);

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