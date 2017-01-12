import io.netty.util.internal.StringUtil;

/**
 * Created by arttsiom.chuiko on 12/01/2017.
 */
public class Test {

    public static void main(String[] args) {
        System.out.println(removeSlashesAtBothEnds("constant1/constant2?foo=bar"));
    }

    public static String removeSlashesAtBothEnds(final String path)
    {

        if (path.isEmpty())
        {
            return path;
        }

        int beginIndex = 0;
        while (beginIndex < path.length() && path.charAt(beginIndex) == '/')
        {
            beginIndex++;
        }
        if (beginIndex == path.length())
        {
            return StringUtil.EMPTY_STRING;
        }

        int endIndex = path.length() - 1;
        while (endIndex > beginIndex && path.charAt(endIndex) == '/')
        {
            endIndex--;
        }

        return path.substring(beginIndex, endIndex + 1);
    }
}
