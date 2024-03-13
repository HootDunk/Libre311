// Copyright 2023 Libre311 Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package app.service.service;

import app.dto.group.GroupDTO;
import app.dto.group.CreateUpdateGroupDTO;
import app.dto.service.CreateServiceDTO;
import app.dto.service.ServiceDTO;
import app.dto.service.UpdateServiceDTO;
import app.dto.servicedefinition.*;
import app.model.jurisdiction.Jurisdiction;
import app.model.jurisdiction.JurisdictionRepository;
import app.model.service.Service;
import app.model.service.ServiceRepository;
import app.model.service.group.ServiceGroup;
import app.model.service.group.ServiceGroupRepository;
import app.model.servicedefinition.*;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class ServiceService {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceService.class);
    private final ServiceRepository serviceRepository;
    private final JurisdictionRepository jurisdictionRepository;
    private final ServiceGroupRepository serviceGroupRepository;
    private final ServiceDefinitionRepository serviceDefinitionRepository;
    private final ServiceDefinitionAttributeRepository serviceDefinitionAttributeRepository;
    private final AttributeValueRepository attributeValueRepository;

    public ServiceService(ServiceRepository serviceRepository, JurisdictionRepository jurisdictionRepository, ServiceGroupRepository serviceGroupRepository, ServiceDefinitionRepository serviceDefinitionRepository, ServiceDefinitionAttributeRepository serviceDefinitionAttributeRepository, AttributeValueRepository attributeValueRepository) {
        this.serviceRepository = serviceRepository;
        this.jurisdictionRepository = jurisdictionRepository;
        this.serviceGroupRepository = serviceGroupRepository;
        this.serviceDefinitionRepository = serviceDefinitionRepository;
        this.serviceDefinitionAttributeRepository = serviceDefinitionAttributeRepository;
        this.attributeValueRepository = attributeValueRepository;
    }

    public Page<ServiceDTO> findAll(Pageable pageable, String jurisdictionId) {
        Page<Service> servicePage = serviceRepository.findAllByJurisdictionId(jurisdictionId, pageable);

        return servicePage.map(ServiceDTO::new);
    }

    public ServiceDefinitionDTO getServiceDefinition(String serviceCode, String jurisdictionId) {
        Optional<Service> serviceOptional = serviceRepository.findByServiceCodeAndJurisdictionId(serviceCode, jurisdictionId);

        if (serviceOptional.isEmpty()) {
            LOG.error("Service not found.");
            return null;
        }
        Service service = serviceOptional.get();
        if (service.getServiceDefinition() == null) {
            LOG.error("Service Definition is null.");
            return null;
        }

        return convertToDTO(service.getServiceDefinition());
    }

    public ServiceDTO createService(CreateServiceDTO serviceDTO, String jurisdictionId) {
        Jurisdiction jurisdiction = jurisdictionRepository.findById(jurisdictionId).get();
        if (serviceCodeAlreadyExists(serviceDTO.getServiceCode(), jurisdiction)) {
            return null;
        }

        Service service = new Service();

        if (groupDoesNotExists(serviceDTO.getGroupId(), jurisdiction, service)) return null;

        service.setJurisdiction(jurisdiction);
        service.setServiceCode(serviceDTO.getServiceCode());
        service.setServiceName(serviceDTO.getServiceName());
        service.setDescription(serviceDTO.getDescription());

        return new ServiceDTO(serviceRepository.save(service));
    }

    public ServiceDTO updateService(Long serviceId, UpdateServiceDTO serviceDTO, String jurisdictionId) {

        Optional<Service> serviceOptional = serviceRepository.findByIdAndJurisdictionId(serviceId, jurisdictionId);
        if (serviceOptional.isEmpty()) {
            LOG.error("Service not found.");
            return null;
        }

        Service service = serviceOptional.get();
        if (serviceDTO.getServiceCode() != null) {
            if (serviceCodeAlreadyExists(serviceDTO.getServiceCode(), service.getJurisdiction())) {
                return null;
            }
            service.setServiceCode(serviceDTO.getServiceCode());
        }

        if (serviceDTO.getGroupId() != null) {
            if (groupDoesNotExists(serviceDTO.getGroupId(), service.getJurisdiction(), service)) return null;
        }

        if (serviceDTO.getDescription() != null) {
            service.setDescription(serviceDTO.getDescription());
        }
        if (serviceDTO.getServiceName() != null) {
            service.setServiceName(serviceDTO.getServiceName());
        }

        return new ServiceDTO(serviceRepository.update(service));
    }

    private boolean groupDoesNotExists(Long serviceDTO, Jurisdiction jurisdiction, Service service) {
        Optional<ServiceGroup> serviceGroupOptional = serviceGroupRepository.findByIdAndJurisdiction(serviceDTO, jurisdiction);
        if (serviceGroupOptional.isPresent()) {
            service.setServiceGroup(serviceGroupOptional.get());
        } else {
            LOG.error("Group not found.");
            return true;
        }
        return false;
    }

    private boolean serviceCodeAlreadyExists(String serviceCode, Jurisdiction jurisdiction) {
        boolean serviceCodeAlreadyExists = serviceRepository.existsByServiceCodeAndJurisdiction(serviceCode, jurisdiction);
        if (serviceCodeAlreadyExists) {
            LOG.error("Service with code {} already exists.", serviceCode);
            return true;
        }
        return false;
    }

    public List<GroupDTO> getListGroups(String jurisdictionId) {
        return serviceGroupRepository.findAllByJurisdictionId(jurisdictionId).stream()
                .map(GroupDTO::new).collect(Collectors.toList());
    }

    public GroupDTO createGroup(CreateUpdateGroupDTO requestDTO, String jurisdictionId) {
        Jurisdiction jurisdiction = jurisdictionRepository.findById(jurisdictionId).get();
        if (groupAlreadyExists(requestDTO.getName(), jurisdiction)) {
            return null;
        }

        ServiceGroup group = new ServiceGroup();
        group.setJurisdiction(jurisdiction);
        group.setName(requestDTO.getName());

        return new GroupDTO(serviceGroupRepository.save(group));
    }

    public GroupDTO updateGroup(Long groupId, CreateUpdateGroupDTO requestDTO) {
        Optional<ServiceGroup> groupOptional = serviceGroupRepository.findById(groupId);
        if (groupOptional.isEmpty()) {
            LOG.error("Group not found.");
            return null;
        }

        ServiceGroup group = groupOptional.get();
        if (requestDTO.getName() != null) {
            if (groupAlreadyExists(requestDTO.getName(), group.getJurisdiction())) {
                return null;
            }
            group.setName(requestDTO.getName());
        }

        return new GroupDTO(serviceGroupRepository.update(group));

    }

    private boolean groupAlreadyExists(String name, Jurisdiction jurisdiction) {
        boolean serviceCodeAlreadyExists = serviceGroupRepository.existsByNameAndJurisdiction(name, jurisdiction);
        if (serviceCodeAlreadyExists) {
            LOG.error("Group with name {} already exists.", name);
            return true;
        }
        return false;
    }

    public void deleteGroup(Long groupId) {
        Optional<ServiceGroup> groupOptional = serviceGroupRepository.findById(groupId);
        if (groupOptional.isPresent()) {
            ServiceGroup serviceGroup = groupOptional.get();
            if (serviceRepository.countByServiceGroup(serviceGroup) == 0) {
                serviceGroupRepository.delete(serviceGroup);
            }
        } else {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Cannot delete Group with existing Service associations.");
        }
    }

    @Transactional
    public ServiceDefinitionDTO addServiceDefinitionAttributeToServiceDefinition(Long serviceId, CreateServiceDefinitionAttributeDTO serviceDefinitionAttributeDTO, String jurisdictionId) {
        Optional<Service> serviceOptional = serviceRepository.findByIdAndJurisdictionId(serviceId, jurisdictionId);
        if (serviceOptional.isEmpty()) {
            LOG.error("Service not found.");
            return null;
        }
        Service service = serviceOptional.get();

        ServiceDefinition serviceDefinition;
        if (service.getServiceDefinition() == null) {
            ServiceDefinition newServiceDefinition = new ServiceDefinition();
            newServiceDefinition.setService(service);
            serviceDefinition = serviceDefinitionRepository.save(newServiceDefinition);

            addAttributeToServiceDefinition(serviceDefinitionAttributeDTO, serviceDefinition);
        } else {
            serviceDefinition = service.getServiceDefinition();
            addAttributeToServiceDefinition(serviceDefinitionAttributeDTO, serviceDefinition);
        }

        service.setServiceDefinition(serviceDefinition);
        serviceRepository.update(service);

        return convertToDTO(serviceDefinition);
    }

    private ServiceDefinitionDTO convertToDTO(ServiceDefinition serviceDefinition) {
        ServiceDefinitionDTO serviceDefinitionDTO = new ServiceDefinitionDTO(serviceDefinition.getId(), serviceDefinition.getService().getServiceCode());

        if (serviceDefinition.getAttributes() != null) {
            serviceDefinitionDTO.setAttributes(serviceDefinition.getAttributes().stream().map(serviceDefinitionAttributeEntity -> {
                ServiceDefinitionAttributeDTO serviceDefinitionAttributeDTO = new ServiceDefinitionAttributeDTO(
                        serviceDefinitionAttributeEntity.getId(),
                        serviceDefinitionAttributeEntity.getCode(),
                        serviceDefinitionAttributeEntity.isVariable(),
                        serviceDefinitionAttributeEntity.getDatatype(),
                        serviceDefinitionAttributeEntity.isRequired(),
                        serviceDefinitionAttributeEntity.getDescription(),
                        serviceDefinitionAttributeEntity.getAttributeOrder(),
                        serviceDefinitionAttributeEntity.getDatatypeDescription());

                Set<AttributeValue> attributeValues = serviceDefinitionAttributeEntity.getAttributeValues();
                if (attributeValues != null) {
                    serviceDefinitionAttributeDTO.setValues(attributeValues.stream()
                            .map(attributeValueEntity -> new AttributeValueDTO(
                                    attributeValueEntity.getId().toString(),
                                    attributeValueEntity.getValueName()))
                            .collect(Collectors.toList()));
                }

                return serviceDefinitionAttributeDTO;
            }).collect(Collectors.toList()));
        }

        return serviceDefinitionDTO;
    }

    public ServiceDefinitionDTO updateServiceDefinitionAttribute(Long attributeId, UpdateServiceDefinitionAttributeDTO serviceDefinitionAttributeDTO) {
        Optional<ServiceDefinitionAttribute> serviceDefinitionAttributeEntityOptional = serviceDefinitionAttributeRepository.findById(attributeId);
        if (serviceDefinitionAttributeEntityOptional.isEmpty()) {
            LOG.error("Attribute not found.");
            return null;
        }
        ServiceDefinitionAttribute serviceDefinitionAttribute = serviceDefinitionAttributeEntityOptional.get();

        ServiceDefinitionAttribute patch = patchServiceDefinitionAttribute(serviceDefinitionAttribute, serviceDefinitionAttributeDTO);

        return convertToDTO(patch.getServiceDefinition());
    }

    @Transactional
    public ServiceDefinitionAttribute patchServiceDefinitionAttribute(ServiceDefinitionAttribute serviceDefinitionAttribute, UpdateServiceDefinitionAttributeDTO serviceDefinitionAttributeDTO) {

        if (serviceDefinitionAttributeDTO.getCode() != null) {
            serviceDefinitionAttribute.setCode(serviceDefinitionAttributeDTO.getCode());
        }
        if (serviceDefinitionAttributeDTO.isVariable() != null) {
            serviceDefinitionAttribute.setVariable(serviceDefinitionAttributeDTO.isVariable());
        }
        if (serviceDefinitionAttributeDTO.getDatatype() != null) {
            serviceDefinitionAttribute.setRequired(serviceDefinitionAttributeDTO.isRequired());
        }
        if (serviceDefinitionAttributeDTO.getDatatype() != null) {
            serviceDefinitionAttribute.setDatatype(serviceDefinitionAttributeDTO.getDatatype());
        }
        if (serviceDefinitionAttributeDTO.getDescription() != null) {
            serviceDefinitionAttribute.setDescription(serviceDefinitionAttributeDTO.getDescription());
        }
        if (serviceDefinitionAttributeDTO.getAttributeOrder() != null) {
            serviceDefinitionAttribute.setAttributeOrder(serviceDefinitionAttributeDTO.getAttributeOrder());
        }
        if (serviceDefinitionAttributeDTO.getDatatypeDescription() != null) {
            serviceDefinitionAttribute.setDatatypeDescription(serviceDefinitionAttributeDTO.getDatatypeDescription());
        }

        // add new values
        if (serviceDefinitionAttributeDTO.getValues() != null) {
            // drop all attribute values
            attributeValueRepository.deleteAllByServiceDefinitionAttribute(serviceDefinitionAttribute);

            serviceDefinitionAttributeDTO.getValues().forEach(attributeValueDTO -> {
                AttributeValue savedValue = attributeValueRepository.save(new AttributeValue(serviceDefinitionAttribute, attributeValueDTO.getName()));
                serviceDefinitionAttribute.addAttributeValue(savedValue);
            });
        }

        return serviceDefinitionAttributeRepository.update(serviceDefinitionAttribute);
    }

    public void removeServiceDefinitionAttributeFromServiceDefinition(Long attributeId) {
        Optional<ServiceDefinitionAttribute> serviceDefinitionAttribute = serviceDefinitionAttributeRepository.findById(attributeId);
        if (serviceDefinitionAttribute.isEmpty()) {
            LOG.error("Attribute not found.");
        }
        ServiceDefinitionAttribute serviceDefinitionAttributeEntity = serviceDefinitionAttribute.get();
        serviceDefinitionAttributeRepository.delete(serviceDefinitionAttributeEntity);
    }

    @Transactional
    public void addAttributeToServiceDefinition(CreateServiceDefinitionAttributeDTO serviceDefinitionAttributeDTO, ServiceDefinition savedSD) {
        ServiceDefinitionAttribute serviceDefinitionAttribute = new ServiceDefinitionAttribute();
        serviceDefinitionAttribute.setServiceDefinition(savedSD);
        serviceDefinitionAttribute.setCode(serviceDefinitionAttributeDTO.getCode());
        serviceDefinitionAttribute.setVariable(serviceDefinitionAttributeDTO.isVariable());
        serviceDefinitionAttribute.setDatatype(serviceDefinitionAttributeDTO.getDatatype());
        serviceDefinitionAttribute.setRequired(serviceDefinitionAttributeDTO.isRequired());
        serviceDefinitionAttribute.setDescription(serviceDefinitionAttributeDTO.getDescription());
        serviceDefinitionAttribute.setAttributeOrder(serviceDefinitionAttributeDTO.getAttributeOrder());
        serviceDefinitionAttribute.setDatatypeDescription(serviceDefinitionAttributeDTO.getDatatypeDescription());

        ServiceDefinitionAttribute savedSDA = serviceDefinitionAttributeRepository.save(serviceDefinitionAttribute);

        if (serviceDefinitionAttributeDTO.getValues() != null) {
            serviceDefinitionAttributeDTO.getValues().forEach(attributeValueDTO -> {
                AttributeValue savedValue = attributeValueRepository.save(new AttributeValue(savedSDA, attributeValueDTO.getName()));
                savedSDA.addAttributeValue(savedValue);
            });
        }

        serviceDefinitionAttributeRepository.update(savedSDA);

        savedSD.addAttribute(savedSDA);
        serviceDefinitionRepository.update(savedSD);
    }
}
