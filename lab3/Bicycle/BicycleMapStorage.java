package Bicycle;

import abstractClasses.abstractStorage;
import java.util.*;

public class BicycleMapStorage extends abstractStorage<Bicycle> {
    private SortedMap<Integer, Bicycle> bicycles;
    
    public BicycleMapStorage() {
        bicycles = new TreeMap<>();
    }
    
    @Override
    public void add(Bicycle bike) {
        if (bicycles.containsKey(bike.getId())) {
            System.out.println("Велосипед с ID " + bike.getId() + " уже существует!");
            return;
        }
        bicycles.put(bike.getId(), bike);
    }
    
    @Override
    public void remove(int id) {
        if (bicycles.remove(id) != null) {
            System.out.println("Велосипед с ID " + id + " удален");
        } else {
            System.out.println("Велосипед с ID " + id + " не найден");
        }
    }
    
    @Override
    public void update(int id, Bicycle newBike) {
        if (bicycles.containsKey(id)) {
            bicycles.put(id, newBike);
            System.out.println("Велосипед с ID " + id + " обновлен");
        } else {
            System.out.println("Велосипед с ID " + id + " не найден для обновления");
        }
    }
    
    @Override
    public Bicycle findById(int id) {
        return bicycles.get(id);
    }
    
    @Override
    public List<Bicycle> getAll() {
        return new ArrayList<>(bicycles.values());
    }

	@Override
    public List<Bicycle> get_All() {
        return new ArrayList<>(bicycles.values());
    }
    
    @Override
    public List<Integer> getAllIds() {
        return new ArrayList<>(bicycles.keySet());
    }

	@Override
	public List<Bicycle> getAllAsList() {
		return new ArrayList<>(bicycles.values());
	}
    
    @Override
    public void displayAll() {
        if (bicycles.isEmpty()) {
            System.out.println("Нет велосипедов в базе");
            return;
        }
        bicycles.values().forEach(Bicycle::displayInfo);
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
    
    public Map<Integer, Bicycle> getSortedById() {
        return Collections.unmodifiableSortedMap(bicycles);
    }
    
    public List<Bicycle> getSortedByPrice() {
        List<Bicycle> sorted = new ArrayList<>(bicycles.values());
        sorted.sort(Comparator.comparingDouble(Bicycle::getPrice));
        return sorted;
    }
    
    public Bicycle findMostExpensive() {
        return bicycles.values().stream()
            .max(Comparator.comparingDouble(Bicycle::getPrice))
            .orElse(null);
    }
    
    public Bicycle findCheapest() {
        return bicycles.values().stream()
            .min(Comparator.comparingDouble(Bicycle::getPrice))
            .orElse(null);
    }

    @Override
    public Iterator<Bicycle> iterator() {
        return bicycles.values().iterator();
    }

    public void displayWithKeyIterator() {
        System.out.println("=== Велосипеды (по ID) ===");
        Iterator<Integer> keyIterator = bicycles.keySet().iterator();
        while (keyIterator.hasNext()) {
            Integer id = keyIterator.next();
            Bicycle bike = bicycles.get(id);
            System.out.printf("ID: %d -> %s %s%n", id, bike.getType(), bike.getModel());
        }
    }

    public void displayWithEntryIterator() {
        System.out.println("=== Велосипеды (EntrySet) ===");
        Iterator<Map.Entry<Integer, Bicycle>> iterator = bicycles.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Bicycle> entry = iterator.next();
            System.out.printf("Ключ: %d | Значение: %s%n", 
                entry.getKey(), entry.getValue().getModel());
        }
    }
}