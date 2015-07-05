package com.github.gabrielruiu.spring.boot.yahoo;

import com.github.gabrielruiu.springsocial.yahoo.api.Yahoo;
import com.github.gabrielruiu.springsocial.yahoo.connect.YahooConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.social.SocialWebAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.web.GenericConnectionStatusView;
import org.springframework.web.servlet.View;

/**
 * @author Gabriel Mihai Ruiu (gabriel.ruiu@mail.com)
 */
@Configuration
@ConditionalOnClass({ SocialConfigurerAdapter.class, YahooConnectionFactory.class })
@ConditionalOnProperty(prefix = "spring.social.yahoo", value = "app-id")
@AutoConfigureBefore(SocialWebAutoConfiguration.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class YahooAutoConfiguration {

    @Configuration
    @EnableSocial
    @EnableConfigurationProperties(YahooProperties.class)
    @ConditionalOnWebApplication
    protected static class YahooAutoConfigurationAdapter extends SocialConfigurerAdapter {

        @Autowired
        private YahooProperties properties;

        @Bean
        @ConditionalOnMissingBean(Yahoo.class)
        @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
        public Yahoo yahoo(ConnectionRepository repository) {
            Connection<Yahoo> connection = repository.findPrimaryConnection(Yahoo.class);

            if (connection != null) {
                return connection.getApi();
            }
            return null;
        }

        @Bean(name = { "connect/yahooConnect", "connect/yahooConnected" })
        @ConditionalOnProperty(prefix = "spring.social", value = "auto-connection-views")
        public View yahooConnectView() {
            return new GenericConnectionStatusView("yahoo", "Yahoo");
        }

        @Override
        public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
            connectionFactoryConfigurer.addConnectionFactory(new YahooConnectionFactory(this.properties.getAppId(), this.properties.getAppSecret()));
        }
    }
}
