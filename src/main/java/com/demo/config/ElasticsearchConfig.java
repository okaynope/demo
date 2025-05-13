package com.demo.config;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;


@Configuration
public class ElasticsearchConfig {
    // 集群地址
    @Value("${spring.elasticsearch.uris}")
    private String uris;
//    @Value("${elasticsearch.username}")
//    private String username;
//    @Value("${elasticsearch.password}")
//    private String password;
//    // 设置连接超时。默认值为10秒
//    @Value("${elasticsearch.connectTimeout}")
//    private Long connectTimeout;
//    // 设置套接字超时。默认值为5秒
//    @Value("${elasticsearch.socketTimeout}")
//    private Long socketTimeout;
    @Bean
    RestHighLevelClient client() {
        ClientConfiguration.TerminalClientConfigurationBuilder terminalClientConfigurationBuilder = ClientConfiguration.builder()
                .connectedTo(uris);           // 集群地址
//				.usingSsl()                   // （可选）启用SSL
//				.withProxy("localhost:8888")  // （可选）设置代理
//			.withPathPrefix("ela") // （可选）设置路径前缀，通常在不同的群集将某个反向代理置于后面时使用
//				.withDefaultHeaders()         // （可选）设置标题
//        // 设置连接超时。默认值为10秒
//        if(connectTimeout != null && connectTimeout != 0L) {
//            terminalClientConfigurationBuilder.withConnectTimeout(connectTimeout);
//        }
//        // 设置套接字超时。默认值为5秒
//        if(socketTimeout != null && socketTimeout != 0L) {
//            terminalClientConfigurationBuilder.withSocketTimeout(socketTimeout);
//        }
//        // 添加基本​​身份验证
//        if(StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
//            terminalClientConfigurationBuilder.withBasicAuth(username, password);
//        }
        ClientConfiguration clientConfiguration = terminalClientConfigurationBuilder.build();
        return RestClients.create(clientConfiguration).rest();
    }
}
