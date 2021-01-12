/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public final class Pic {

    @Property()
    private final String PicId;

    @Property()
    private final String eventId;

    @Property()
    private final String hashContext;

    @Property()
    private final String picPath;

    @Property()
    private final String createDate;

    public String getPicId() {
        return PicId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getHashContext() {
        return hashContext;
    }

    public String getPicPath() {
        return picPath;
    }

    public String getCreateDate() {
        return createDate;
    }

    public Pic(
            @JsonProperty("picId") final String picId,
            @JsonProperty("eventId") final String eventId,
            @JsonProperty("hashContext") final String hashContext,
            @JsonProperty("picPath") final String picPath,
            @JsonProperty("createDate") final String createDate
    ) {
        PicId = picId;
        this.eventId = eventId;
        this.hashContext = hashContext;
        this.picPath = picPath;
        this.createDate = createDate;
    }
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        if (!super.equals(object)) {
            return false;
        }
        Pic pic = (Pic) object;
        return java.util.Objects.equals(PicId, pic.PicId) && java.util.Objects.equals(eventId, pic.eventId) && java.util.Objects.equals(hashContext, pic.hashContext) && java.util.Objects.equals(picPath, pic.picPath) && java.util.Objects.equals(createDate, pic.createDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), PicId, eventId, hashContext, picPath, createDate);
    }

    @Override
    public String toString() {
        return "Pic{"
                + "PicId='" + PicId + '\''
                + ", eventId='" + eventId + '\''
                + ", hashContext='" + hashContext + '\''
                + ", picPath='" + picPath + '\''
                + ", createDate='" + createDate + '\''
                + '}';
    }
}
