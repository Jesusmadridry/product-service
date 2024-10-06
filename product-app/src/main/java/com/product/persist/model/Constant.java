package com.product.persist.model;

public final class Constant {
    private Constant() {}
    public static final String AUDIT_SCHEMA = "audit_product";
    public static final String AUDIT_SCHEMA_GENERATOR = "audit_generator";


    public static final String ERROR_MESSAGE_SERVICE = "There was an error trying to create the product";
    public static final String MANDATORY_FIELD = "Product Flow is mandatory to process the request";

    public static final String EXISTING_PRODUCT_MESSAGE = "This product is already created";
    public static final String NEW_PRODUCT_MESSAGE = "This product was created successfully";
    public static final String PRODUCT_UPDATE_MESSAGE = "This product was successfully updated";
    public static final String PRODUCT_NOT_FOUND_MESSAGE = "This product was not found";
    public static final String PRODUCT_DELETE_MESSAGE = "This product was successfully deleted";
}
