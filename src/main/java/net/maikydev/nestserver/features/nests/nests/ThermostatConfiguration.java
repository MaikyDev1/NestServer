package net.maikydev.nestserver.features.nests.nests;

import net.maikydev.duckycore.data.json.objects.JsonObject;
import net.maikydev.duckycore.data.yaml.YamlConfig;

public record ThermostatConfiguration(String functionalityType, double energyConsumptionPerHour, String energyConsumptionMeasureUnit, double energyConsumptionPerUnit) {

    public static ThermostatConfiguration wrapFromConfig(YamlConfig config, String path) {
        return new ThermostatConfiguration(
                config.contains(path + ".functionality_type") ? config.getString(path + ".functionality_type") : "nan",
                config.contains(path + ".energy_consumption.per_hour") ? Double.parseDouble(config.getAny(path + ".energy_consumption.per_hour").toString()) : 0,
                config.contains(path + ".energy_consumption.measure_unit") ? config.getString(path + ".energy_consumption.measure_unit") : "nan",
                config.contains(path + ".energy_consumption.price_per_unit") ? (double) config.getAny(path + ".energy_consumption.price_per_unit") : 0
        );
    }

    public JsonObject toJson() {
        JsonObject o = JsonObject.newJsonObject();
        if (functionalityType != null) o.addNewField("functionality_type", functionalityType);
        JsonObject energyConsumption = JsonObject.newJsonObject();
            energyConsumption.addNewField("per_hour", energyConsumptionPerHour)
                .addNewField("measure_unit", energyConsumptionMeasureUnit)
                .addNewField("price_per_unit", energyConsumptionPerUnit);
        return o.addNewField("energy_consumption", energyConsumption);
    }
}
