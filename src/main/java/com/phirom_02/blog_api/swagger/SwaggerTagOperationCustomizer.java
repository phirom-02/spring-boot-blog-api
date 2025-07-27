package com.phirom_02.blog_api.swagger;

import io.swagger.v3.oas.models.Operation;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
public class SwaggerTagOperationCustomizer implements OperationCustomizer {


    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        SwaggerTag methodTag = handlerMethod.getMethodAnnotation(SwaggerTag.class);
        if (methodTag != null) {
            operation.addTagsItem(methodTag.name());
        } else {
            SwaggerTag classTag = handlerMethod.getBeanType().getAnnotation(SwaggerTag.class);
            if (classTag != null) {
                operation.addTagsItem(classTag.name());
            }
        }
        return operation;
    }
}
