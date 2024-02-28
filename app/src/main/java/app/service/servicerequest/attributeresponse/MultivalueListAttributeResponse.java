package app.service.servicerequest.attributeresponse;

import app.model.service.servicedefinition.ServiceDefinitionAttribute;
import io.micronaut.core.annotation.Introspected;

import java.util.List;

@Introspected
public class MultivalueListAttributeResponse extends BaseAttributeResponse<List<String>> {

    public MultivalueListAttributeResponse(ServiceDefinitionAttribute sda, List<String> answers) {
        super(sda);
        this.value = answers;
    }

    public MultivalueListAttributeResponse() {
    }

}
