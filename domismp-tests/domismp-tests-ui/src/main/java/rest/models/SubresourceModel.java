package rest.models;

import utils.Generator;

public class SubresourceModel {
    private String identifierScheme;
    private String identifierValue;
    private Object actionMessage;
    private int index;
    private String subresourceId;
    private String subresourceTypeIdentifier;
    private int status;

    public static SubresourceModel generatePublicSubResource() {
        SubresourceModel subresourceModel = new SubresourceModel();
        subresourceModel.identifierValue = ("AUT_subIdentifier_" + Generator.randomAlphaNumericValue(4)).toLowerCase();
        subresourceModel.identifierScheme = ("AUT_subrScheme_" + Generator.randomAlphaNumericValue(4)).toLowerCase();
        return subresourceModel;
    }

    public String getIdentifierScheme() {
        return identifierScheme;
    }

    public String getIdentifierValue() {
        return identifierValue;
    }

    public Object getActionMessage() {
        return actionMessage;
    }

    public int getIndex() {
        return index;
    }

    public String getSubresourceId() {
        return subresourceId;
    }

    public String getSubresourceTypeIdentifier() {
        return subresourceTypeIdentifier;
    }

    public int getStatus() {
        return status;
    }


}
