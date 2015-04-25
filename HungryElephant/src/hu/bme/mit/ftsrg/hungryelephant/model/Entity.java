package hu.bme.mit.ftsrg.hungryelephant.model;

public interface Entity<T, ID> extends Comparable<T> {
	ID getId();

	String toString();

	default public int compareTo(T o) {
		if (o == null) {
			return -1;
		} else {
			return toString().compareTo(o.toString());
		}
	}
}
