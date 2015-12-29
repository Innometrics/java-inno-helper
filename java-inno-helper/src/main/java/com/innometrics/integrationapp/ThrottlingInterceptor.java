package com.innometrics.integrationapp;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ThrottlingInterceptor implements Interceptor {
    private long lastRequest = 0L;
    private final long maxRequestSpeed;
    private final Lock requestLock = new ReentrantLock();

    public ThrottlingInterceptor(long maxRequestSpeed) {
        this.maxRequestSpeed = maxRequestSpeed;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        requestLock.lock();
        try {
            long curTime = System.currentTimeMillis();
            long diff = curTime - lastRequest;
            if (diff < maxRequestSpeed)
                try {
                    Thread.sleep(maxRequestSpeed - diff);
                }
                catch (InterruptedException e) {
                    System.out.println("Failed to intercept");
                    e.printStackTrace();
                }
            lastRequest = System.currentTimeMillis();
            return chain.proceed(chain.request());
        }
        finally {
            requestLock.unlock();
        }
    }
}