package de.netzkronehd.chatfilter.utils;

import java.util.Arrays;

public class Utils {

    public static String getArgsAsText(String[] args, int from) {
        return String.join(" ", Arrays.copyOfRange(args, from, args.length));
    }

}
