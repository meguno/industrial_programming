import Bicycle.Bicycle;
import Bicycle.BicycleListStorage;
import Bicycle.BicycleMapStorage;
import abstractClasses.abstractStorage;
import org.junit.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UnitTests {
	private abstractStorage<Bicycle> listStorage;
	private abstractStorage<Bicycle> mapStorage;
	private SimpleDateFormat dateFormat;
	private Date testDate;

	@Before
	public void setUp() throws Exception {
		listStorage = new BicycleListStorage();
		mapStorage = new BicycleMapStorage();
		dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		testDate = dateFormat.parse("15.05.2023");
	}

	@Test
	public void testBicycleCreation() {
		Bicycle bike = new Bicycle(1, "Mountain", "Rockrider", "Aluminum",
				26, 21, testDate, 450.0);

		assertEquals(1, bike.getId());
		assertEquals("Mountain", bike.getType());
		assertEquals("Rockrider", bike.getModel());
		assertEquals("Aluminum", bike.getFrameMaterial());
		assertEquals(26, bike.getWheelSize());
		assertEquals(21, bike.getGears());
		assertEquals(testDate, bike.getManufactureDate());
		assertEquals(450.0, bike.getPrice(), 0.001);
	}

	@Test
	public void testAddBicycleToListStorage() {
		Bicycle bike = new Bicycle(1, "Mountain", "Rockrider", "Aluminum",
				26, 21, testDate, 450.0);

		listStorage.add(bike);
		assertEquals(1, listStorage.size());

		Bicycle found = listStorage.findById(1);
		assertNotNull(found);
		assertEquals("Rockrider", found.getModel());
	}

	@Test
	public void testAddBicycleToMapStorage() {
		Bicycle bike = new Bicycle(1, "Mountain", "Rockrider", "Aluminum",
				26, 21, testDate, 450.0);

		mapStorage.add(bike);
		assertEquals(1, mapStorage.size());

		Bicycle found = mapStorage.findById(1);
		assertNotNull(found);
		assertEquals("Rockrider", found.getModel());
	}

	@Test
	public void testDuplicateIdPrevention() {
		Bicycle bike1 = new Bicycle(1, "Mountain", "Rockrider", "Aluminum",
				26, 21, testDate, 450.0);
		Bicycle bike2 = new Bicycle(1, "City", "CityBike", "Steel",
				28, 7, testDate, 250.0);

		listStorage.add(bike1);
		listStorage.add(bike2); // Должно быть предотвращено

		assertEquals(1, listStorage.size());
	}

	@Test
	public void testRemoveBicycle() {
		Bicycle bike = new Bicycle(1, "Mountain", "Rockrider", "Aluminum",
				26, 21, testDate, 450.0);

		listStorage.add(bike);
		assertEquals(1, listStorage.size());

		listStorage.remove(1);
		assertEquals(0, listStorage.size());

		Bicycle found = listStorage.findById(1);
		assertNull(found);
	}

	@Test
	public void testUpdateBicycle() {
		Bicycle bike = new Bicycle(1, "Mountain", "Rockrider", "Aluminum",
				26, 21, testDate, 450.0);

		listStorage.add(bike);

		Bicycle updatedBike = new Bicycle(1, "Mountain", "Rockrider Pro", "Carbon",
				27, 24, testDate, 650.0);

		listStorage.update(1, updatedBike);

		Bicycle found = listStorage.findById(1);
		assertNotNull(found);
		assertEquals("Rockrider Pro", found.getModel());
		assertEquals("Carbon", found.getFrameMaterial());
		assertEquals(650.0, found.getPrice(), 0.001);
	}

	@Test
	public void testFindNonExistentBicycle() {
		Bicycle found = listStorage.findById(999);
		assertNull(found);
	}

	@Test
	public void testGetAllBicycles() {
		Bicycle bike1 = new Bicycle(1, "Mountain", "Rockrider", "Aluminum",
				26, 21, testDate, 450.0);
		Bicycle bike2 = new Bicycle(2, "City", "CityBike", "Steel",
				28, 7, testDate, 250.0);

		listStorage.add(bike1);
		listStorage.add(bike2);

		List<Bicycle> allBikes = listStorage.getAllAsList();
		assertEquals(2, allBikes.size());
	}

	@Test
	public void testClearStorage() {
		Bicycle bike1 = new Bicycle(1, "Mountain", "Rockrider", "Aluminum",
				26, 21, testDate, 450.0);
		Bicycle bike2 = new Bicycle(2, "City", "CityBike", "Steel",
				28, 7, testDate, 250.0);

		listStorage.add(bike1);
		listStorage.add(bike2);
		assertEquals(2, listStorage.size());

		listStorage.clear();
		assertEquals(0, listStorage.size());
	}

	@Test
	public void testBicycleAccessories() {
		Bicycle bike = new Bicycle(1, "Mountain", "Rockrider", "Aluminum",
				26, 21, testDate, 450.0);

		bike.addAccessory("Bell");
		bike.addAccessory("Lights");

		List<String> accessories = bike.getAccessories();
		assertEquals(2, accessories.size());
		assertTrue(accessories.contains("Bell"));
		assertTrue(accessories.contains("Lights"));

		bike.removeAccessory("Bell");
		assertEquals(1, bike.getAccessories().size());
	}

	@Test
	public void testBicycleToString() {
		Bicycle bike = new Bicycle(1, "Mountain", "Rockrider", "Aluminum",
				26, 21, testDate, 450.0);

		String str = bike.toString();
		assertNotNull(str);
		assertTrue(str.contains("Rockrider"));
		assertTrue(str.contains("450.00"));
	}

	@Test
	public void testIteratorFunctionality() {
		Bicycle bike1 = new Bicycle(1, "Mountain", "Rockrider", "Aluminum",
				26, 21, testDate, 450.0);
		Bicycle bike2 = new Bicycle(2, "City", "CityBike", "Steel",
				28, 7, testDate, 250.0);

		listStorage.add(bike1);
		listStorage.add(bike2);

		int count = 0;
		for (Bicycle bike : listStorage) {
			count++;
		}

		assertEquals(2, count);
	}

	@Test
	public void testExistsMethod() {
		Bicycle bike = new Bicycle(1, "Mountain", "Rockrider", "Aluminum",
				26, 21, testDate, 450.0);

		listStorage.add(bike);

		assertTrue(listStorage.exists(1));
		assertFalse(listStorage.exists(999));
	}

	@After
	public void tearDown() {
		listStorage.clear();
		mapStorage.clear();
	}
}