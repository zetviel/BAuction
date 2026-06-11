package org.by1337.bauction.util.auction;

import org.by1337.blib.util.NameKey;
import org.by1337.bauction.db.kernel.SellItem;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Objects;

public class Sorting implements Comparable<Sorting> {
    private final SortingType type;
    private final String value;
    private final String selectedName;
    private final String unselectedName;
    private final int priority;
    private final NameKey nameKey;

    public Sorting(SortingType type, String value, String selectedName, String unselectedName, int priority, NameKey nameKey) {
        this.type = type;
        this.value = value;
        this.selectedName = selectedName;
        this.unselectedName = unselectedName;
        this.priority = priority;
        this.nameKey = nameKey;
    }

    public SortingType type() {
        return type;
    }

    public String value() {
        return value;
    }

    public String selectedName() {
        return selectedName;
    }

    public String unselectedName() {
        return unselectedName;
    }

    public int priority() {
        return priority;
    }

    public NameKey nameKey() {
        return nameKey;
    }

    @Override
    public int compareTo(@NotNull Sorting o) {
        return Integer.compare(priority, o.priority());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sorting)) return false;
        Sorting sorting = (Sorting) o;
        return priority == sorting.priority && type == sorting.type && Objects.equals(value, sorting.value) && Objects.equals(selectedName, sorting.selectedName) && Objects.equals(unselectedName, sorting.unselectedName) && Objects.equals(nameKey, sorting.nameKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value, selectedName, unselectedName, priority, nameKey);
    }

    @Override
    public String toString() {
        return "Sorting{" +
                "type=" + type +
                ", value='" + value + '\'' +
                ", selectedName='" + selectedName + '\'' +
                ", unselectedName='" + unselectedName + '\'' +
                ", priority=" + priority +
                '}';
    }

    public Comparator<SellItem> getComparator(){
        if (type == Sorting.SortingType.COMPARE_MAX) {
            switch (value){
                case "{price}":
                    return (item, item1) -> Double.compare(item1.getPrice(), item.getPrice());
                case "{price_for_one}":
                    return (item, item1) -> Double.compare(item1.getPriceForOne(), item.getPriceForOne());
                case "{sale_time}":
                    return (item, item1) -> Double.compare((double) item1.getTimeListedForSale() / 1000, (double) item.getTimeListedForSale() / 1000);
                default:
                    throw new IllegalArgumentException("unknown sorting type: " + this);
            }
        } else {
            switch (value){
                case "{price}":
                    return Comparator.comparingDouble(SellItem::getPrice);
                case "{price_for_one}":
                    return Comparator.comparingDouble(SellItem::getPriceForOne);
                case "{sale_time}":
                    return Comparator.comparingDouble((SellItem item) -> (double) item.getTimeListedForSale() / 1000);
                default:
                    throw new IllegalArgumentException("unknown sorting type: " + this);
            }
        }
    }

    public static enum SortingType {
        COMPARE_MAX,
        COMPARE_MIN;

        public static SortingType getByOrdinal(int x) {
            for (SortingType sortingType : values()) {
                if (sortingType.ordinal() == x) {
                    return sortingType;
                }
            }
            return COMPARE_MAX;
        }
    }
}