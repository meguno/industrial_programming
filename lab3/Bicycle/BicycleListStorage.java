package Bicycle;

import abstractClasses.abstractStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.Iterator;

public class BicycleListStorage extends abstractStorage<Bicycle> {
    private List<Bicycle> bicycles;
    
    public BicycleListStorage() {
        bicycles = new ArrayList<>();
    }
    
    @Override
    public void add(Bicycle bike) {
        if (exists(bike.getId())) {
            System.out.println("Велосипед с ID " + bike.getId() + " уже существует!");
            return;
        }
        bicycles.add(bike);
    }
    
    @Override
    public void remove(int id) {
        boolean removed = bicycles.removeIf(bike -> bike.getId() == id);
        if (removed) {
            System.out.println("Велосипед с ID " + id + " удален");
        } else {
            System.out.println("Велосипед с ID " + id + " не найден");
        }
    }
    
    @Override
    public void update(int id, Bicycle newBike) {
        for (int i = 0; i < bicycles.size(); i++) {
            if (bicycles.get(i).getId() == id) {
                bicycles.set(i, newBike);
                System.out.println("Велосипед с ID " + id + " обновлен");
                return;
            }
        }
        System.out.println("Велосипед с ID " + id + " не найден для обновления");
    }
    
    @Override
    public Bicycle findById(int id) {
        return bicycles.stream()
            .filter(bike -> bike.getId() == id)
            .findFirst()
            .orElse(null);
    }
    
    @Override
    public List<Bicycle> getAll() {
        return new ArrayList<>(bicycles);
    }

	@Override
    public List<Bicycle> get_All() {
        return new ArrayList<>(bicycles);
    }

	@Override
    public List<Bicycle> getAllAsList() {
        return new ArrayList<>(bicycles);
    }
    
    @Override
    public List<Integer> getAllIds() {
        return bicycles.stream().map(Bicycle::getId).collect(Collectors.toList());
    }
    
    @Override
    public void displayAll() {
        if (bicycles.isEmpty()) {
            System.out.println("Нет велосипедов в базе");
            return;
        }
        bicycles.forEach(Bicycle::displayInfo);
    }
    
    @Override
    public int size() {
        return bicycles.size();
    }
    
    @Override
    public void clear() {
        bicycles.clear();
        System.out.println("Все велосипеды удалены из хранилища");
    }
    
    public void sortByPrice() {
        bicycles.sort((b1, b2) -> Double.compare(b1.getPrice(), b2.getPrice()));
    }
    
    public void sortByDate() {
        bicycles.sort((b1, b2) -> {
            if (b1.getManufactureDate() == null && b2.getManufactureDate() == null) return 0;
            if (b1.getManufactureDate() == null) return -1;
            if (b2.getManufactureDate() == null) return 1;
            return b1.getManufactureDate().compareTo(b2.getManufactureDate());
        });
    }

    @Override
    public Iterator<Bicycle> iterator() {
        return bicycles.iterator();
    }

    public void displayReverseWithIterator() {
        ListIterator<Bicycle> iterator = bicycles.listIterator(bicycles.size());
    
        System.out.println("=== Велосипеды (обратный порядок) ===");
        while (iterator.hasPrevious()) {
            Bicycle bike = iterator.previous();
            bike.displayInfo();
        }
    }

    public void displayWithCustomIterator() {
        System.out.println("=== Велосипеды с использованием итератора ===");
        Iterator<Bicycle> iterator = iterator();
        while (iterator.hasNext()) {
            Bicycle bike = iterator.next();
            System.out.printf("ID: %d | %s %s | Цена: $%.2f%n",
                bike.getId(), bike.getType(), bike.getModel(), bike.getPrice());
        }
    }
}