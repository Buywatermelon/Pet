package observer;

import adopter.PetAdopter;
import entity.Pet;
import sort.QuickSort;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * 宠物计数相关类
 * 监听者，每调用宠物工厂接口一次都会更新计数
 *
 * @author BuyWatermelon
 */
public final class PetCounter implements Observer {

    /**
     * 被领养的宠物总数
     */
    private AtomicInteger total;

    /**
     * 每种宠物被领养的总数
     */
    private Map<String, AtomicInteger> categoryTotal;

    /**
     * 使用变量缓存已创建的实例
     */
    private static PetCounter instance;

    /**
     * 私有构造器，单例类，不允许通过构造方法创建对象
     */
    private PetCounter() {
    }

    /**
     * 提供静态方法，返回PetCount对象
     *
     * @return
     */
    public static PetCounter getInstance() {
        if (instance == null) {
            instance = new PetCounter();
        }

        return instance;
    }

    /**
     * 加入待通知观察者
     * 考虑到观察者可能有多个因此使用可变参数
     * 在服务端初始化时执行，因此只会执行一次
     * 在加入待通知观察者的同时初始化宠物计数相关变量
     *
     * @param observable
     */
    public void setObservable(Observable... observable) {

        this.total = new AtomicInteger(0);

        this.categoryTotal = new ConcurrentHashMap(4);

        categoryTotal.put("Dog", new AtomicInteger(0));

        categoryTotal.put("Cat", new AtomicInteger(0));

        categoryTotal.put("Parrot", new AtomicInteger(0));

        categoryTotal.put("Chicken", new AtomicInteger(0));

        Arrays.stream(observable).forEach(o -> o.addObserver(this));
    }

    /**
     * 主题事件触发时调用
     *
     * @param observable
     * @param object
     */
    @Override
    public synchronized void update(Observable observable, Object object) {
        // 传递对象对PetAdopter类时，查询宠物受欢迎程度
        if (object instanceof PetAdopter) {
            ((PetAdopter) object).setPetCountList(queryPopularity());
        } else {
            adoptPet(object.getClass().getSimpleName(), (Pet) object);
        }
    }

    /**
     * 领养宠物主题事件
     */
    private void adoptPet(String category, Pet pet) {

        pet.setCategory(category);

        this.total.getAndIncrement();

        pet.setCount(this.total.get());

        categoryTotal.get(category).getAndIncrement();

        pet.setCategoryCount(categoryTotal.get(category).get());

        System.out.println(Thread.currentThread().getName() + "您领养的宠物为" + category + " , 被领养宠物总数为：" + this.total
                + " ，该种宠物被领养总数为：" + categoryTotal.get(category));
    }


    /**
     * 查询宠物受欢迎程度
     * 因为需要写数组的排序，因此先将map中的value转化为int[]
     * 然后进行排序，通过value再从map中找到相应的key
     */
    public String queryPopularity() {

        Object[] objects = categoryTotal.values().toArray();

        int[] popularity = new int[4];

        for (int i = 0; i < popularity.length; i++) {
            popularity[i] = ((AtomicInteger) objects[i]).get();
        }

        QuickSort.quickSort(popularity);

        System.out.println(Thread.currentThread().getName() + "\n" + mapValueToKey(popularity));

        return mapValueToKey(popularity);
    }

    /**
     * 通过已排序的popularity数组
     * 反向取key，并进行拼接
     * 对于数量重复的情况通过设置repeat数解决
     */
    private String mapValueToKey(int[] popularity) {

        StringBuffer queryPopularity = new StringBuffer();

        int repeat = 1;

        // 通过value反向取key，对于一个value对应多个key的情况
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
