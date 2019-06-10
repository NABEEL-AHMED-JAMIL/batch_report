package com.ballistic.batch_report.util;

import com.ballistic.batch_report.model.ApplicationDecorator;
import com.ballistic.batch_report.model.ResponseObject;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import java.io.IOException;
import java.lang.reflect.Field;

@Component
@Scope("prototype")
@SuppressWarnings({ "unchecked", "resource" })
public class CommonUtil {

    public static final Logger logger = LogManager.getLogger(CommonUtil.class);

    public Object populateDataBeanFromJSON(Class<?> clazz, ApplicationDecorator decorator, String json) {
        Object object = null;
        ObjectMapper mapper = new ObjectMapper().configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        try {
            object = clazz.newInstance();
            if(StringUtils.isNotBlank(json) && !json.equals("\"\"")) {
                object = mapper.readValue(json, clazz);
            }
        } catch(JsonParseException ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
            logger.error(ex.getLocalizedMessage() + " <==================> " + json);
            decorator.setResponseMessage("Invalid JSON");
            decorator.setReturnCode((new ReturnConstants()).ReturnCodeFailure);
            decorator.getErrors().add("Provided input is not in valid JSON form");
        } catch(JsonMappingException ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
            logger.error(ex.getLocalizedMessage() + " <==================> " + json);
            decorator.setResponseMessage("Invalid JSON");
            decorator.setReturnCode((new ReturnConstants()).ReturnCodeFailure);
            decorator.getErrors().add("Provided input is not mapping with JSON properties");
        } catch(IOException ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
            logger.error(ex.getLocalizedMessage() + " <==================> " + json);
            decorator.setResponseMessage("Invalid JSON");
            decorator.setReturnCode((new ReturnConstants()).ReturnCodeFailure);
            decorator.getErrors().add("JSON input/output error");
        } catch(Exception ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
            logger.error(ex.getLocalizedMessage() + " <==================> " + json);
            decorator.setResponseMessage("Invalid JSON");
            decorator.setReturnCode((new ReturnConstants()).ReturnCodeFailure);
            decorator.getErrors().add("Invalid JSON");
        }
        return object;
    }

    public <T> T populateDataBeanFromJSONArray(final TypeReference<T> typeReference, ApplicationDecorator decorator, String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return (T) mapper.readValue(json, typeReference);
        } catch(JsonParseException ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
            decorator.setResponseMessage("Invalid JSON");
            decorator.setReturnCode((new ReturnConstants()).ReturnCodeFailure);
            decorator.getErrors().add("Provided input is not in valid JSON form");
        } catch(JsonMappingException ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
            decorator.setResponseMessage("Invalid JSON");
            decorator.setReturnCode((new ReturnConstants()).ReturnCodeFailure);
            decorator.getErrors().add("Provided input is not mapping with properties");
        } catch(IOException ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
            decorator.setResponseMessage("Invalid JSON");
            decorator.setReturnCode((new ReturnConstants()).ReturnCodeFailure);
            decorator.getErrors().add("JSON input/output error");
        } catch(Exception ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
            decorator.setResponseMessage("Invalid JSON");
            decorator.setReturnCode((new ReturnConstants()).ReturnCodeFailure);
            decorator.getErrors().add("Invalid JSON");
        }
        return null;
    }

    public String populateJSONFromDataBean(Object object) {
        String json = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            json = mapper.writeValueAsString(object);
        } catch(JsonGenerationException ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
        } catch(JsonMappingException ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
        } catch(IOException ex) {
            logger.error("*********** Exception ***********", ex.getMessage());
        }
        return json;
    }

    public ResponseObject responseToClient(ApplicationDecorator decorator) {
        ResponseObject responseObject = new ResponseObject();
        if((decorator.getReturnCode() != null && decorator.getReturnCode() == 1005) ||
            !CollectionUtils.isEmpty(decorator.getErrors())) {
            decorator.getResponseMap().put("errors", decorator.getErrors());
            responseObject.setReturnType(decorator.getFailure());
            responseObject.setReturnCode(decorator.getReturnCode());
            responseObject.setReturnMessage(decorator.getResponseMessage());
            responseObject.setReturnData(decorator.getResponseMap());
            responseObject.setQueryTimeInMilli(decorator.getQueryTime());
        } else {
            if(!MapUtils.isEmpty(decorator.getInfo())) { decorator.getResponseMap().put("info", decorator.getInfo()); }
            decorator.getResponseMap().put("response", decorator.getDataBean());
            responseObject.setReturnType(decorator.getSuccess());
            responseObject.setReturnCode(decorator.getReturnCode());
            responseObject.setReturnMessage(decorator.getResponseMessage());
            responseObject.setReturnData(decorator.getResponseMap());
            responseObject.setQueryTimeInMilli(decorator.getQueryTime());
        }
        return responseObject;
    }

    public String getId(Object obj) throws Exception {
        String id = null;
        Class<?> c = obj.getClass();
        Field idField = c.getDeclaredField("uuId");
        if(!idField.isAccessible()) {
            idField.setAccessible(true);
        }
        Object fieldObj = idField.get(obj);
        if(fieldObj != null) {
            Class<?> fieldClass = fieldObj.getClass();
            String name = fieldClass.getName();
            name = name.substring(name.lastIndexOf(".") + 1);
            if(!name.equalsIgnoreCase("String")) {
                obj = idField.get(obj);
                id = this.getId(obj);
            } else {
                id = (String) fieldObj;
            }
        }
        return id;
    }

}
