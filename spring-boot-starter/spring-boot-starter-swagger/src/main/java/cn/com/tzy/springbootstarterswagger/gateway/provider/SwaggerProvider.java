package cn.com.tzy.springbootstarterswagger.gateway.provider;

import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
public class SwaggerProvider implements SwaggerResourcesProvider {

    public static final String API_URI = "/v2/api-docs";

    public static Map<String, String> moduleMap = new HashMap<>();

    static {
        moduleMap.put("WebApi", "webapi");
        moduleMap.put("App", "app");
    }

    @Override
    public List<SwaggerResource> get() {
        List resources = new ArrayList<>();
        moduleMap.forEach((k, v) -> {
            resources.add(swaggerResource(k, v));
        });
        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation("/"+location + API_URI + "?group="+ location);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }
}