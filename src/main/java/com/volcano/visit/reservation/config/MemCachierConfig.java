package com.volcano.visit.reservation.config;

//import com.google.code.ssm.CacheFactory;

import com.google.code.ssm.CacheFactory;
import com.google.code.ssm.config.AbstractSSMConfiguration;
import com.google.code.ssm.config.DefaultAddressProvider;
import com.google.code.ssm.providers.CacheConfiguration;
import com.google.code.ssm.providers.xmemcached.MemcacheClientFactoryImpl;
import com.google.code.ssm.providers.xmemcached.XMemcachedConfiguration;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemCachierConfig extends AbstractSSMConfiguration {

    @Value("${reservation.memcached.ip}")
    private String memcachedIp;

    @Value("${reservation.memcached.port}")
    private String memcachedPort;


    // Heroku config
//    @Bean
//    @Override
//    public CacheFactory defaultMemcachedClient() {
//        String serverString = "192.168.99.101 11211";//System.getenv("MEMCACHIER_SERVERS").replace(",", " ");
//        List<InetSocketAddress> servers = AddrUtil.getAddresses(serverString);
//        AuthInfo authInfo = AuthInfo.plain(System.getenv("MEMCACHIER_USERNAME"),
//                System.getenv("MEMCACHIER_PASSWORD"));
//        Map<InetSocketAddress, AuthInfo> authInfoMap =
//                new HashMap<>();
//        for(InetSocketAddress server : servers) {
//            authInfoMap.put(server, authInfo);
//        }
//
//        final XMemcachedConfiguration conf = new XMemcachedConfiguration();
//        conf.setUseBinaryProtocol(true);
//        conf.setAuthInfoMap(authInfoMap);
//
//        final CacheFactory cf = new CacheFactory();
//        cf.setCacheClientFactory(new MemcacheClientFactoryImpl());
//        cf.setAddressProvider(new DefaultAddressProvider(serverString));
//        cf.setConfiguration(conf);
//        return cf;
//    }

    // SSM configuration
    //https://github.com/ragnor/simple-spring-memcached
    @Bean
    @Override
    public CacheFactory defaultMemcachedClient() {
        final CacheConfiguration conf = new CacheConfiguration();
        conf.setConsistentHashing(true);
        final CacheFactory cf = new CacheFactory();
        cf.setCacheClientFactory(new com.google.code.ssm.providers.xmemcached.MemcacheClientFactoryImpl());
        cf.setAddressProvider(new DefaultAddressProvider(memcachedIp + ":" + memcachedPort));
        cf.setConfiguration(conf);
        return cf;
    }


    // xmemcached configuration
    // https://www.javacodegeeks.com/2013/06/simple-spring-memcached-spring-caching-abstraction-and-memcached.html

}
