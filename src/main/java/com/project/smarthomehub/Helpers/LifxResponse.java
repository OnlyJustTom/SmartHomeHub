package com.project.smarthomehub.Helpers;

import java.time.OffsetDateTime;

//All the data returned by the Lifx API
public class LifxResponse {

    private String id;
    private String uuid;
    private String label;
    private boolean connected;
    private String power;
    private Color color;
    private double brightness;
    private Group group;
    private Location location;
    private Product product;
    private OffsetDateTime lastSeen;
    private double secondsSinceSeen;


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public boolean isConnected() { return connected; }
    public void setConnected(boolean connected) { this.connected = connected; }

    public String getPower() { return power; }
    public void setPower(String power) { this.power = power; }

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }

    public double getBrightness() { return brightness; }
    public void setBrightness(double brightness) { this.brightness = brightness; }

    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public OffsetDateTime getLastSeen() { return lastSeen; }
    public void setLastSeen(OffsetDateTime lastSeen) { this.lastSeen = lastSeen; }

    public double getSecondsSinceSeen() { return secondsSinceSeen; }
    public void setSecondsSinceSeen(double secondsSinceSeen) { this.secondsSinceSeen = secondsSinceSeen; }
}

class Color {

    private double hue;
    private double saturation;
    private int kelvin;


    public double getHue() { return hue; }
    public void setHue(double hue) { this.hue = hue; }

    public double getSaturation() { return saturation; }
    public void setSaturation(double saturation) { this.saturation = saturation; }

    public int getKelvin() { return kelvin; }
    public void setKelvin(int kelvin) { this.kelvin = kelvin; }
}

class Group {

    private String id;
    private String name;


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

class Location {

    private String id;
    private String name;


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

class Product {

    private String name;
    private String identifier;
    private String company;
    private Capabilities capabilities;


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public Capabilities getCapabilities() { return capabilities; }
    public void setCapabilities(Capabilities capabilities) { this.capabilities = capabilities; }
}

class Capabilities {

    private boolean hasColor;
    private boolean hasVariableColorTemp;
    private boolean hasIr;
    private boolean hasHev;
    private boolean hasChain;
    private boolean hasMatrix;
    private boolean hasMultizone;
    private int minKelvin;
    private int maxKelvin;


    public boolean isHasColor() { return hasColor; }
    public void setHasColor(boolean hasColor) { this.hasColor = hasColor; }

    public boolean isHasVariableColorTemp() { return hasVariableColorTemp; }
    public void setHasVariableColorTemp(boolean hasVariableColorTemp) { this.hasVariableColorTemp = hasVariableColorTemp; }

    public boolean isHasIr() { return hasIr; }
    public void setHasIr(boolean hasIr) { this.hasIr = hasIr; }

    public boolean isHasHev() { return hasHev; }
    public void setHasHev(boolean hasHev) { this.hasHev = hasHev; }

    public boolean isHasChain() { return hasChain; }
    public void setHasChain(boolean hasChain) { this.hasChain = hasChain; }

    public boolean isHasMatrix() { return hasMatrix; }
    public void setHasMatrix(boolean hasMatrix) { this.hasMatrix = hasMatrix; }

    public boolean isHasMultizone() { return hasMultizone; }
    public void setHasMultizone(boolean hasMultizone) { this.hasMultizone = hasMultizone; }

    public int getMinKelvin() { return minKelvin; }
    public void setMinKelvin(int minKelvin) { this.minKelvin = minKelvin; }

    public int getMaxKelvin() { return maxKelvin; }
    public void setMaxKelvin(int maxKelvin) { this.maxKelvin = maxKelvin; }
}
