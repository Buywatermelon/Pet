package adopter;

import entity.*;
import java.lang.reflect.Method;
import java.util.Observable;
import java.util.concurrent.Callable;

/**
 * 宠物领养者
 *
 * @author BuyWatermelon
 */
public class PetAdopter extends Observable implements Callable<String> {

    /**
     * 操作变量，用以指示线程执行哪个操作
     * String线程安全
     * 虽然每一次请求都要进行赋值操作，但是对于存放在常量池中有相同的字符串时并不会新生成字符串对象
     */
    private String operation;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * 用以赋值list后，PetCounter生成的排序后的计数语句
     */
    private String petCountList;

    public String getPetCountList(){
        return petCountList;
    }

    public void setPetCountList(String petCountList){
        this.petCountList = petCountList;
    }

    /**
     * 使用变量缓存已创建的实例
     */
    private static PetAdopter instance;

    /**
     * 私有构造器，单例类，不允许通过构造方法创建对象
     */
    private PetAdopter() {
    }

    /**
     * 提供静态方法，返回PetAdopter对象
     *
     * @return
     */
    public static PetAdopter getInstance() {
        if (instance == null) {
            instance = new PetAdopter();
        }

        return instance;
    }

    public String adoptDog() {

        Dog dog = new Dog();

        setChanged();

        notifyObservers(dog);

        return "OK";
    }

    public String adoptCat() {

        Cat cat = new Cat("喵喵喵", 3);

        setChanged();

        notifyObservers(cat);

        return "OK";
    }

    public String adoptParrot() {

        Parrot parrot = new Parrot();

        setChanged();

        notifyObservers(parrot);

        return "OK";
    }

    public String adoptChicken() {

        Chicken chicken = new Chicken();

        setChanged();

        notifyObservers(chicken);

        return "OK";
    }

    public String queryPopularity() {

        setChanged();

        notifyObservers(this);

        return petCountList;
    }

    /**
     * 线程执行方法
     *
     * @return
     * @throws Exception
     */
    @Override
    public String call() throws Exception {

        Method method = this.getClass().getMethod(operation);

        method.setAccessible(true);

        return (String) method.invoke(this);
    }

}
