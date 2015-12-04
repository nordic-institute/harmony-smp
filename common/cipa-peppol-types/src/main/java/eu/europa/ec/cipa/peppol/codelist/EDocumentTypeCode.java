
package eu.europa.ec.cipa.peppol.codelist;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.name.IHasDisplayName;


/**
 * This file is generated from Genericode file DocumentTypeCode.gc. Do NOT edit!
 * 
 */
public enum EDocumentTypeCode
    implements IHasID<String> , IHasDisplayName
{
    QUERY("21", "Query"),
    RESPONSE_TO_QUERY("22", "Response to query"),
    INQUIRY("251", "Inquiry"),
    STATUS_INFORMATION("23", "Status information"),
    ORDER("220", "Order"),
    RESPONSE_TO_REGISTRATION("301", "Response to registration"),
    COMMERCIAL_INVOICE("380", "Commercial invoice"),
    RELATED_DOCUMENT("916", "Related document"),
    CREDIT_NOTE_RELATED_TO_GOODS_OR_SERVICES("81", "Credit note related to goods or services");
    private final String m_sID;
    private final String m_sDisplayName;

    private EDocumentTypeCode(
        @Nonnull
        @Nonempty
        final String sID,
        @Nonnull
        final String sDisplayName) {
        m_sID = sID;
        m_sDisplayName = sDisplayName;
    }

    @Nonnull
    @Nonempty
    public String getID() {
        return m_sID;
    }

    @Nonnull
    public String getDisplayName() {
        return m_sDisplayName;
    }

    @Nullable
    public static EDocumentTypeCode getFromIDOrNull(
        @Nullable
        final String sID) {
        return EnumHelper.getFromIDOrNull(EDocumentTypeCode.class, sID);
    }

    @Nullable
    public static String getDisplayNameFromIDOrNull(
        @Nullable
        final String sID) {
        final EDocumentTypeCode eValue = EDocumentTypeCode.getFromIDOrNull(sID);
        return ((eValue == null)?null:eValue.getDisplayName());
    }
}