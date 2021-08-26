package com.bdilab.colosseum.config;

import org.springframework.context.annotation.Configuration;
//import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Created by hh on 2019/03/19.
 * 使用Redis作为Session数据的分布式缓存，多台Tomcat可使用同一台Redis作为Session数据的共享存储
 * 在访问Session时，直接在Controller中将HttpSession作为一个参数自定注入，Spring会自动从Redis获取Session数据
 * @EnableRedisHttpSession(maxInactiveIntervalInSeconds = 600) maxInactiveIntervalInSeconds单位为秒，默认1800秒
 */
@Configuration
//@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 600)
public class HttpSessionConfig {

    /**
     * `@EnableRedisHttpSession` 这个注解创建了一个名为 springSessionRepositoryFilter
     * 的 bean，负责替换 httpSession,同由 redis 提供缓存支持。
     * 为了做到全部替换，我们要确保Servlet容器(Tomcat)对于某个请求都使用这个Filter,这个由SpringBoot负责。
     *
     * `maxInactiveIntervalInSeconds`:设置Session失效时间
     * 使用Redis Session之后，原Boot的server.session.timeout属性不再生效
     */

}
