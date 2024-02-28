package app.service.servicerequest.attributeresponse;

import app.model.service.servicedefinition.AttributeDataType;

/**
 * Represents the response a user gave to a ServiceDefinitionAttribute
 * @param <T> The type of response value
 */
public interface AttributeResponse<T> {

  /**
   * @return the AttributeDataType
   */
  AttributeDataType getDataType();

  /**
   * @return the description from the corresponding ServiceDefinitionAttribute (ie: the question the user saw)
   */
  String getDescription();
  /**
   * @return the serviceCode of the corresponding service
   */
  String getServiceCode();

  /**
   * @return The value that the user responded with.
   */
  T getValue();

  int getOrder();
}
