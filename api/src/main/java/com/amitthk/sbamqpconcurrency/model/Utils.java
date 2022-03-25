package com.amitthk.sbamqpconcurrency.model;

import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Utils {
    public static String getStrackTraceAsString(Exception exc) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter( writer );
        exc.printStackTrace( printWriter );
        printWriter.flush();
        return writer.toString();
    }

    public static <T> CompletableFuture<List<T>> allOf(List<CompletableFuture<T>> futuresList) {
        CompletableFuture<Void> allFuturesResult =
                CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[futuresList.size()]));
        return allFuturesResult.thenApply(v ->
                futuresList.stream().
                        map(future -> future.join()).
                        collect(Collectors.<T>toList())
        );
    }

    public static <T> CompletableFuture<List<T>> join(List<CompletableFuture<T>> executionPromises) {
        CompletableFuture<Void> joinedPromise = CompletableFuture.allOf(executionPromises.toArray(CompletableFuture[]::new));
        return joinedPromise.thenApply(voit -> executionPromises.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }
}
