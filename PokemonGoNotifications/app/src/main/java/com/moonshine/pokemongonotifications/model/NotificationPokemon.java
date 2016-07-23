package com.moonshine.pokemongonotifications.model;

/**
 * Created by jaapmanenschijn on 23/07/16.
 */
public class NotificationPokemon {

    private Long id;
    private int imageId;
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
