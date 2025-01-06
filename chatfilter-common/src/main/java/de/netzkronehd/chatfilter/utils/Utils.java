package de.netzkronehd.chatfilter.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Utils {

    public static String getArgsAsText(String[] args, int from) {
        return String.join(" ", Arrays.copyOfRange(args, from, args.length));
    }

    public static <T> List<T> getPage(List<T> list, int page, int pageSize) {
        final int fromIndex = (page - 1) * pageSize;
        if (fromIndex >= list.size()) {
            return Collections.emptyList();
        }
        final int toIndex = Math.min(fromIndex + pageSize, list.size());
        return list.subList(fromIndex, toIndex);
    }

    public static int getPages(int size, int pageSize) {
        return (int) Math.ceil((double) size / pageSize);
    }

}
