package app.service.servicerequest.attributeresponse;

import app.model.service.servicedefinition.ServiceDefinitionAttribute;

import java.util.List;

public class SingleValueListAttributeResponse extends BaseAttributeResponse<List<String>> {
    public SingleValueListAttributeResponse(ServiceDefinitionAttribute sda, List<String> answers) {
        super(sda);
        this.value = answers;
    }
    public SingleValueListAttributeResponse() {
    }
}
