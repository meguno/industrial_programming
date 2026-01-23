package pattern;

import Bicycle.Bicycle;
import java.util.List;

public interface DataWriter {
	void write(List<Bicycle> bicycles, String filename);
}