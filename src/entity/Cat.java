package entity;

/**
 * @author BuyWatermelon
 */
public class Cat extends Pet {

    /**
     * 猫的名字
     */
    private String catName;

    /**
     * 猫的年龄
     */
    private Integer catAge;

    /**
     * 构造函数
     *
     * @param catName
     * @param catAge
     */
    public Cat(String catName, Integer catAge) {
        this.catName = catName;
        this.catAge = catAge;
    }
}
