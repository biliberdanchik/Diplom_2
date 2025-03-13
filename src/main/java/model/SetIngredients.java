package model;

import java.util.List;
import java.util.Random;

public class SetIngredients {
    private List<Ingredient> data;

    public SetIngredients(List<Ingredient> data) {
        this.data = data;
    }

    public List<Ingredient> getData() {
        return data;
    }

    public void setData(List<Ingredient> data) {
        this.data = data;
    }

    public String chooseRandomIngredient() {
        Random random = new Random();
        return getData().get(random.nextInt(data.size())).get_id();
    }
}
