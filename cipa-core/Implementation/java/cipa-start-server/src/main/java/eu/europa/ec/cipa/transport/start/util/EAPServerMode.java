package eu.europa.ec.cipa.transport.start.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.id.IHasID;
import com.phloc.commons.lang.EnumHelper;

public enum EAPServerMode implements IHasID <String> {
  /**
   * full production mode with SMP and DNS usage
   */
  PRODUCTION,
  /**
   * Standalone mode the AP accepts the message with no recipient check (url and
   * certificate)
   */
  DEVELOPMENT_STANDALONE,
  /**
   * In this mode the AP will call the SMP directly without DNS lookup
   */
  DEVELOPMENT_DIRECT_SMP;

  @Nonnull
  @Nonempty
  public String getID () {
    return name ();
  }

  @Nullable
  public static EAPServerMode getFromIDOrNull (@Nullable final String sID) {
    return EnumHelper.getFromIDCaseInsensitiveOrNull (EAPServerMode.class, sID);
  }
}
