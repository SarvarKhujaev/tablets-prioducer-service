package com.example.tabletsproducerservice.entity.patrulDataSet.patrulRequests;

import java.util.UUID;

// используется когда патрульный хочвте поменять свое фото
public final class PatrulImageRequest {
    public String getNewImage() {
        return this.newImage;
    }

    public void setNewImage( final String newImage ) {
        this.newImage = newImage;
    }

    public UUID getPatrulUUID() {
        return this.patrulUUID;
    }

    public void setPatrulUUID( final UUID patrulUUID ) {
        this.patrulUUID = patrulUUID;
    }

    private String newImage;
    private UUID patrulUUID;

    public PatrulImageRequest () {}
}
