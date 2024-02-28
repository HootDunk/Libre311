package app;

import app.wip.ElectricVehicle;
import app.wip.Vehicle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class SerializationWIP {

    @Inject
    ObjectMapper objectMapper;


    @Test
    public void whenDeserializingPolymorphic_thenCorrect() throws JsonProcessingException {
        String json = "{\"type\":\"ELECTRIC_VEHICLE\",\"autonomy\":\"500\",\"chargingTime\":\"200\"}";

        ObjectMapper mapper = new ObjectMapper();

        Vehicle vehicle = mapper.readerFor(Vehicle.class).readValue(json);

        assertEquals(ElectricVehicle.class, vehicle.getClass());

//        try {
//            // Serialize the object to JSON and print the result
//            String jsonString = objectMapper.writeValueAsString(vehicle);
//            assertEquals(json, jsonString);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    @Test
    public void whenDeserializingPolymorphicArray_thenCorrect() throws JsonProcessingException {
        String json = "[{\"type\":\"ELECTRIC_VEHICLE\",\"autonomy\":\"500\",\"chargingTime\":\"200\"}, {\"type\":\"ELECTRIC_VEHICLE\",\"autonomy\":\"500\",\"chargingTime\":\"200\"}]";

        ObjectMapper mapper = new ObjectMapper();

        // Use TypeReference to provide type information for the generic list
        List<Vehicle> vehicles = mapper.readValue(json, new TypeReference<List<Vehicle>>() {});

        // Now vehicles is a List<Vehicle>, and you can check its elements
        assertEquals(ElectricVehicle.class, vehicles.get(0).getClass());
        assertEquals(ElectricVehicle.class, vehicles.get(1).getClass());
    }


}
