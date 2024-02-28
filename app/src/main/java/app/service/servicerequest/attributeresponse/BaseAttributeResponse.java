package app.service.servicerequest.attributeresponse;

import app.model.service.servicedefinition.AttributeDataType;
import app.model.service.servicedefinition.ServiceDefinitionAttribute;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.micronaut.core.annotation.Introspected;


@Introspected
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "dataType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultivalueListAttributeResponse.class, name = "multivaluelist"),
        @JsonSubTypes.Type(value = SingleValueListAttributeResponse.class, name = "singlevaluelist"),
})
public class BaseAttributeResponse<T> implements AttributeResponse<T> {

    public BaseAttributeResponse(ServiceDefinitionAttribute sda) {
        this.dataType = sda.getDatatype();
        this.serviceCode = sda.getCode();
        this.description = sda.getDescription();
        this.order = sda.getAttributeOrder();
    }

    public BaseAttributeResponse() {
    }

    AttributeDataType dataType;
    // the corresponding service code from the ServiceDefinitionAttribute
    String serviceCode;
    // the question that the user was presented with
    String description;
    // the human-readable answer that the user gave
    T value;

    int order;

    public void setOrder(int order) {
        this.order = order;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public AttributeDataType getDataType() {
        return dataType;
    }

    public void setDataType(AttributeDataType dataType) {
        this.dataType = dataType;
    }


    @Override
    public String getServiceCode() {
        return serviceCode;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public int getOrder() {
        return order;
    }
}
