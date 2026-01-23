package pattern;

import Bicycle.Bicycle;
import java.util.List;

public abstract class DataWriterDecorator implements DataWriter {
	protected DataWriter decoratedWriter;

	public DataWriterDecorator(DataWriter writer) {
		this.decoratedWriter = writer;
	}

	@Override
	public void write(List<Bicycle> bicycles, String filename) {
		decoratedWriter.write(bicycles, filename);
	}
}