package br.com.devops.restaurantregistration.model;

/**
 * Created by adercio on 23/12/15.
 */
public enum Type {
    CASTER("Caster"), FAST_FOOD("Fast Food"), DOMICILE("Domicile"), DONT_KNOW("Dont Know");

    private final String description;

    Type(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
