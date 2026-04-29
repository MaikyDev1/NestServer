package net.maikydev.nestserver.ducket.ducklet;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.maikydev.duckycore.data.json.DuckyJson;
import net.maikydev.duckycore.data.json.objects.JsonEntity;
import net.maikydev.nestserver.ducket.exchange.DuckletRequest;
import net.maikydev.nestserver.ducket.exchange.DuckletResponse;
import net.maikydev.nestserver.ducket.HttpMethod;
import net.maikydev.nestserver.ducket.annotations.AnnotationLibrary;
import net.maikydev.nestserver.ducket.annotations.RequestParam;
import net.maikydev.nestserver.ducket.annotations.RequestUrlParam;
import net.maikydev.nestserver.ducket.exception.DuckletHandlerException;
import net.maikydev.nestserver.ducket.utils.Mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;

@Getter
@Setter
public class DuckletEndpoint {

    private HttpMethod httpMethod;
    private Mapping mapping;
    private Method method;

    protected DuckletResponse handle(Object parent, DuckletRequest request) throws DuckletHandlerException {
        Parameter[] params = method.getParameters();
        Object[] processedParams = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            Annotation annotation = Arrays.stream(param.getAnnotations()).findFirst().orElse(null);
            System.out.println(AnnotationLibrary.getFromAnnotation(annotation));
            switch (AnnotationLibrary.getFromAnnotation(annotation)) {
                case REQUEST_PARAM -> {
                    if (request.getHttpParams() == null) {
                        processedParams[i] = null;
                    } else {
                        String paramValue = param.getAnnotation(RequestParam.class).value();
                        processedParams[i] = request.getHttpParams().getOrDefault(paramValue, null);
                    }
                }
                case REQUEST_BODY -> {
                    if (request.getHttpBody() != null && param.getType() == JsonEntity.class) {
                        try {
                            processedParams[i] = DuckyJson.serialization(request.getHttpBody());
                        } catch (Exception e) {
                            processedParams[i] = null;
                        }
                    } else {
                        processedParams[i] = null;
                    }
                }
                case REQUEST_URL_PARAM -> {
                    if (request.getTagLines() == null) {
                        processedParams[i] = null;
                    } else {
                        String tagLineName = param.getAnnotation(RequestUrlParam.class).value();
                        processedParams[i] = request.getTagLines().getOrDefault(tagLineName, null);
                    }
                }
                case null, default -> {
                    throw new DuckletHandlerException("Under " + method.getName() + " we found some unsupported parameters! Cancelling!");
                }
            }
        }
        try {
            return (DuckletResponse) method.invoke(parent, processedParams);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new DuckletHandlerException("Under " + method.getName() + " we could not invoke the method. " + e.getMessage() + "! Cancelling!");
        }

    }

}

