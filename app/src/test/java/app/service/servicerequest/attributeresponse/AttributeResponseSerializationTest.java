package app.service.servicerequest.attributeresponse;

import app.model.service.servicedefinition.AttributeDataType;
import app.model.service.servicedefinition.ServiceDefinitionAttribute;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class AttributeResponseSerializationTest {
    // todo can likely move object mapper and TypeReference into static method of BaseAttributeResponse
    ObjectMapper mapper = new ObjectMapper();
    TypeReference<List<BaseAttributeResponse<?>>> baseAttributeResponseTypeReference = new TypeReference<>() {
    };
    String singleValueListJson = createSampleJson(AttributeDataType.SINGLEVALUELIST, "[\"Broken step\"]");
    String multiValueListJson = createSampleJson(AttributeDataType.MULTIVALUELIST, "[\"Broken step\",\"Other\"]");

    String createSampleJson(AttributeDataType dataType, String value) {
        return String.format("{\"dataType\":\"%s\",\"serviceCode\":\"001\",\"description\":\"What was the problem?\",\"value\":%s,\"order\":1}", dataType, value);
    }

    @Test
    void testDeserialization() throws JsonProcessingException {
        String listJson = "[" + singleValueListJson + "," + multiValueListJson + "]";
        List<BaseAttributeResponse<?>> responseList = mapper.readValue(listJson, baseAttributeResponseTypeReference);

        assertEquals(SingleValueListAttributeResponse.class, responseList.get(0).getClass());
        assertEquals(MultivalueListAttributeResponse.class, responseList.get(1).getClass());
    }

    @Test
    void testSerialization() throws JsonProcessingException {
        ServiceDefinitionAttribute sda = new ServiceDefinitionAttribute();
        sda.setDatatype(AttributeDataType.MULTIVALUELIST);
        sda.setCode("001");
        sda.setDescription("What was the problem?");
        sda.setAttributeOrder(1);

        MultivalueListAttributeResponse multivalueListAttributeResponse = new MultivalueListAttributeResponse(sda, List.of("Broken step", "Other"));
        sda.setDatatype(AttributeDataType.SINGLEVALUELIST);
        SingleValueListAttributeResponse singleValueListAttributeResponse = new SingleValueListAttributeResponse(sda, List.of("Broken step"));

        assertEquals(multiValueListJson, mapper.writeValueAsString(multivalueListAttributeResponse));
        assertEquals(singleValueListJson, mapper.writeValueAsString(singleValueListAttributeResponse));
    }
}