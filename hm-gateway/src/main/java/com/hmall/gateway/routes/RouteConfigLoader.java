package com.hmall.gateway.routes;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;


@Component
@RequiredArgsConstructor
public class RouteConfigLoader {

    private final NacosConfigManager configManager;

    private final RouteDefinitionWriter routeDefinitionWriter;

    private final static String DATA_ID="gateway-routes.json";
    private final static String GROUP="DEFAULT_GROUP";

    private Set<String> routeIds=new HashSet<>();
    @PostConstruct
    public void initRouteConfigListener() throws NacosException {
        // 1.注册监听器并首次拉取配置
        String configInfo = configManager.getConfigService()
                .getConfigAndSignListener(DATA_ID, GROUP, 5000, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        updateConfigInfo(configInfo);
                    }
                });
        // 2.首次启动时，更新一次配置
        updateConfigInfo(configInfo);
    }

    private void updateConfigInfo(String configAndSignListener) {
        List<RouteDefinition> routeDefinitions = JSONUtil.toList(configAndSignListener, RouteDefinition.class);
        for (String routeId : routeIds) {
            routeDefinitionWriter.delete(Mono.just(routeId)).subscribe();
        }
        routeIds.clear();
        if(routeDefinitions==null||routeDefinitions.isEmpty()){
            //无新路由
            return;
        }
        for (RouteDefinition routeDefinition : routeDefinitions) {
            routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
            routeIds.add(routeDefinition.getId());
        }




    }

}
