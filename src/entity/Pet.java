package entity;

/**
 * @author BuyWatermelon
 */
public class Pet {

    /**
     * 所领养宠物种类
     */
    private String category;

    /**
     * 所领养宠物总的计数，即当前领养宠物为第几只被领养的宠物
     */
    private int count;

    /**
     * 所领养宠物分类别计数，即当前领养宠物为第几只(猫，狗，鹦鹉，小鸡)
     */
    private int categoryCount;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(int categoryCount) {
        this.categoryCount = categoryCount;
    }
}
