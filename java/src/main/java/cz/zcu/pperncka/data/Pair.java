package cz.zcu.pperncka.data;

/**
 * A simple record representing a pair of objects.
 * @param first first object
 * @param second second object
 * @param <X> type of first object
 * @param <Y> type of second object
 */
public record Pair<X, Y>(X first, Y second) {}
