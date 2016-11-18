package timber.log;

import java.util.List;

import timber.log.BaseTree;
import timber.log.Timber;

/**
 * Created by Michael on 11.11.2016.
 */

public class Lumberjack {

    final static ThreadLocal<String> explicitTag = new ThreadLocal<>();

    /** Set a one-time call stack correction for use on the next logging call. */
    public static void callStackCorrection(Integer correction) {
        List<Timber.Tree> forest = Timber.forest();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, count = forest.size(); i < count; i++) {
            if (forest.get(i) instanceof BaseTree)
                ((BaseTree)forest.get(i)).callStackCorrection.set(correction);
        }
    }
}
