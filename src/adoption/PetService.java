package adoption;

import sort.QuickSort;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author BuyWatermelon
 */
public class PetService {

    private ConcurrentHashMap<String, AtomicInteger> categoryTotal = new ConcurrentHashMap<>();

    public String adoptPet(String category) {
        new Pet(category);
        categoryTotal.computeIfPresent(category, (k,v) -> new AtomicInteger(v.incrementAndGet()));
        categoryTotal.computeIfAbsent(category, k -> new AtomicInteger(1));
        System.out.println(categoryTotal.values());
        return "OK";
    }

    public String queryPopularity() {
        Object[] objects = categoryTotal.values().toArray();
        int[] popularity = new int[categoryTotal.size()];
        for (int i = 0; i < popularity.length; i++) {
            popularity[i] = ((AtomicInteger) objects[i]).get();
        }
        QuickSort.quickSort(popularity);
        return mapValueToKey(popularity);
    }

    private String mapValueToKey(int[] popularity) {
        StringBuffer queryPopularity = new StringBuffer();
        int repeat = 1;
        for (int i = popularity.length - 1; i >= 0; i -= repeat) {
            int val = popularity[i];
            List<String> keyList = categoryTotal.entrySet()
                    .stream()
                    .filter(kvEntry -> Objects.equals(kvEntry.getValue().get(), val))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            keyList.forEach(key ->
                    queryPopularity.append(key).append(": ").append(val).append("\n")
            );
            repeat = keyList.size();
        }
        return queryPopularity.toString() + "OK";
    }
}
