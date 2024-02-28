package app.service.servicerequest;

import app.dto.servicerequest.PostRequestServiceRequestDTO;
import app.dto.servicerequest.ServiceRequestDTO;
import app.model.service.Service;
import app.model.service.servicedefinition.ServiceDefinitionAttribute;
import app.model.servicerequest.ServiceRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class ServiceRequestTransformer {
  ObjectMapper objectMapper;

  public ServiceRequestTransformer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  ServiceRequest fill(Service service, PostRequestServiceRequestDTO dto){
    ServiceRequest serviceRequest = new ServiceRequest();
    serviceRequest.setService(service);
    serviceRequest.setJurisdiction(service.getJurisdiction());
    serviceRequest.setLatitude(dto.getLatitude());
    serviceRequest.setLongitude(dto.getLongitude());
    serviceRequest.setAddressString(dto.getAddressString());
    serviceRequest.setAddressId(dto.getAddressId());
    serviceRequest.setEmail(dto.getEmail());
    serviceRequest.setDeviceId(dto.getDeviceId());
    serviceRequest.setAccountId(dto.getAccountId());
    serviceRequest.setFirstName(dto.getFirstName());
    serviceRequest.setLastName(dto.getLastName());
    serviceRequest.setPhone(dto.getPhone());
    serviceRequest.setDescription(dto.getDescription());
    serviceRequest.setMediaUrl(dto.getMediaUrl());
    return serviceRequest;
  }

//  ServiceRequestDTO toDTO(ServiceRequest serviceRequest) {
//    ServiceRequestDTO serviceRequestDTO = new ServiceRequestDTO(serviceRequest);
//
//    ObjectMapper objectMapper = new ObjectMapper();
//    String attributesJson = serviceRequest.getAttributesJson();
//    if (attributesJson != null) {
//      try {
//        ServiceDefinitionAttribute[] serviceDefinitionAttributes = objectMapper.readValue(attributesJson, ServiceDefinitionAttribute[].class);
//        serviceRequestDTO.setSelectedValues(List.of(serviceDefinitionAttributes));
//      } catch (JsonProcessingException e) {
//        throw new RuntimeException(e);
//      }
//    }
//
//    return serviceRequestDTO;
//  }

}
