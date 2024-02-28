package app.service.servicerequest.attributeresponse;

import app.model.service.servicedefinition.ServiceDefinition;
import app.model.service.servicedefinition.ServiceDefinitionAttribute;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.*;

@Singleton
public class AttributeResponseFactory {


    @Inject
    ObjectMapper objectMapper;

    boolean isFormUrlEncodedAttributeResponse(String maybeFormUrlEncodedRes){
        return maybeFormUrlEncodedRes.startsWith("attribute[");
    }

    String extractAttributeCode(String attributeKey){
        return attributeKey.substring(attributeKey.indexOf("[") + 1, attributeKey.indexOf("]"));
    }


    Map<String, List<String>> aggregateAttributeResponses(Map<String, String> requestBody){
        Map<String, List<String>> attributeCodeResponseValueMap = new HashMap<>();

        for (Map.Entry<String, String> entry : requestBody.entrySet()){
            if (!isFormUrlEncodedAttributeResponse(entry.getKey())) continue;
            String attributeCode = extractAttributeCode(entry.getKey());
            List<String> responses;
            if (attributeCodeResponseValueMap.containsKey(attributeCode)){
                responses = attributeCodeResponseValueMap.get(attributeCode);
            } else {
                responses = new ArrayList<>();
            }
            responses.add(entry.getValue());
            attributeCodeResponseValueMap.put(attributeCode, responses);
        }

        return attributeCodeResponseValueMap;
    }

    // todo where does validation logic live? Perhaps we validate in the constructor of each AttributeResponse
    List<AttributeResponse<?>> createResponses(ServiceDefinition serviceDefinition, Map<String, String> requestBody) {
        Map<String, ServiceDefinitionAttribute> serviceCodeServiceDefinitionAttributeMap = new HashMap<>();
        for (ServiceDefinitionAttribute attribute : serviceDefinition.getAttributes()){
            serviceCodeServiceDefinitionAttributeMap.put(attribute.getCode(), attribute);
        }

        Map<String, List<String>> aggregatedResponses = aggregateAttributeResponses(requestBody);

        List<AttributeResponse<?>> responseList = new ArrayList<>();
        for (Map.Entry<String, List<String>> attributeRes : aggregatedResponses.entrySet()){

        }
        return  List.of();
    }

    public static AttributeResponse<?> create(ServiceDefinitionAttribute sda, List<String> answers){
        switch(sda.getDatatype()) {
            case MULTIVALUELIST:
                return new MultivalueListAttributeResponse(sda, answers);
            case SINGLEVALUELIST:
                return new SingleValueListAttributeResponse(sda, answers);
            default:
                throw new UnsupportedOperationException("No implementation exists for datatype");

        }
    }

}
