package abstractClasses;

import java.util.Date;

public abstract class abstractTransport {
	protected int id;
	protected String type;
	protected String model;
	protected String frameMaterial;
	protected int wheelSize;
	protected int gears;
	protected Date manufactureDate;
	protected double price;

	public abstractTransport(int id, String type, String model, String frameMaterial, int wheelSize, int gears,
			Date manufactureDate, double price) {
		this.id = id;
		this.type = type;
		this.model = model;
		this.frameMaterial = frameMaterial;
		this.wheelSize = wheelSize;
		this.gears = gears;
		this.manufactureDate = manufactureDate;
		this.price = price;
	}

	public abstract void displayInfo();
}