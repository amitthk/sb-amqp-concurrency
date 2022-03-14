package com.amitthk.sbamqpconcurrency.model;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Utils {
    public static String printStrackTrace(Exception exc) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter( writer );
        exc.printStackTrace( printWriter );
        printWriter.flush();
        return writer.toString();
    }
}
