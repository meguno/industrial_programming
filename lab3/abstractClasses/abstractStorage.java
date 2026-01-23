package abstractClasses;

import java.util.Iterator;
import java.util.List;

public abstract class abstractStorage<T> implements Iterable<T> {
	public abstract void add(T item);

	public abstract void remove(int id);

	public abstract T findById(int id);

	public abstract void displayAll();

	public abstract int size();

	public abstract void update(int id, T newItem);

	public abstract List<T> get_All();

	public abstract void clear();

	public abstract List<Integer> getAllIds();

	public abstract Iterable<T> getAll();

	public abstract List<T> getAllAsList();

	public boolean exists(int id) {
		return findById(id) != null;
	}

	public abstract Iterator<T> iterator();

	public void displayWithIterator() {
		Iterator<T> iterator = iterator();
		int count = 0;

		while (iterator.hasNext()) {
			T item = iterator.next();
			System.out.println(item);
			count++;
		}

		if (count == 0) {
			System.out.println("Zero elements");
		}
	}

	public void displayFormat() {
		Iterator<T> iterator = iterator();
		int ind = 1;

		System.out.println("=== Elements list (" + size() + ") ===");
		while (iterator.hasNext()) {
			T item = iterator.next();
			System.out.println(ind + ". " + item.toString());
			ind++;
		}
	}
}