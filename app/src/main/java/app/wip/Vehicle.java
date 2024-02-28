package app.wip;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ElectricVehicle.class, name = "ELECTRIC_VEHICLE"),
        @JsonSubTypes.Type(value = FuelVehicle.class, name = "FUEL_VEHICLE")
})
public class Vehicle {

    public String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
