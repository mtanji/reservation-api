package com.volcano.visit.reservation;

import java.io.IOException;
import java.net.InetSocketAddress;
import net.spy.memcached.MemcachedClient;
//import net.rubyeye.xmemcached.MemcachedClient;

public class MemcachedJava {

    public static void main(String[] args) throws IOException {
        // Connecting to Memcached server on localhost
        MemcachedClient mcc = new MemcachedClient(new
                InetSocketAddress("192.168.99.101", 11211));
        System.out.println("Connection to server sucessfully");
        System.out.println("set status:" + mcc.set("sample_key", 900, "memcached"));

        // Get value from cache
        System.out.println("Get from Cache:" + mcc.get("sample_key"));
        System.out.println("Get from Cache:" + mcc.get("sample_key"));
        System.out.println("Get from Cache:" + mcc.get("sample_key"));
        System.out.println("Get from Cache:" + mcc.get("sample_key"));

        mcc.delete("17942.Occupancy");
    }
}
