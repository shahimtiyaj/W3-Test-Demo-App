package engineers.w3.testdemoapp.model;

/**
 * Created by Md. Imtiyaj on 1/14/2019.
 */

public class Item {
    private String item;
    private String image;

    /*
    default constructor
     */
    public Item() {
    }

    public Item(String item, String image) {
        this.item = item;
        this.image = image;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
