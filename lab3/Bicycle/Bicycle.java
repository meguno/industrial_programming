package Bicycle;

import abstractClasses.abstractTransport;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Bicycle extends abstractTransport {
    private List<String> accessories;

    public Bicycle(int id, String type, String model,
            String frameMaterial, int wheelSize,
            int gears, Date manufactureDate, double price) {
        super(id, type, model, frameMaterial, wheelSize, gears, manufactureDate, price);
        this.accessories = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFrameMaterial() {
        return frameMaterial;
    }

    public void setFrameMaterial(String frameMaterial) {
        this.frameMaterial = frameMaterial;
    }

    public int getWheelSize() {
        return wheelSize;
    }

    public void setWheelSize(int wheelSize) {
        this.wheelSize = wheelSize;
    }

    public int getGears() {
        return gears;
    }

    public void setGears(int gears) {
        this.gears = gears;
    }

    public Date getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(Date manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<String> getAccessories() {
        return accessories;
    }

    public void addAccessory(String accessory) {
        accessories.add(accessory);
    }

    public void removeAccessory(String accessory) {
        accessories.remove(accessory);
    }

    @Override
    public void displayInfo() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        return String.format(
                "Bicycle #%d: %s %s\n" +
                        "Material: %s, Wheel size: %d\", transfers amount: %d\n" +
                        "Release date: %s, Price: $%.2f\n" +
                        "Complectation: %s\n",
                id, type, model, frameMaterial, wheelSize, gears,
                manufactureDate, price, accessories);
    }

}
