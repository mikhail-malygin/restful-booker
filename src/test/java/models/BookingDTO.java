package models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingDTO(
        String firstname,
        String lastname,
        Integer totalprice,
        Boolean depositpaid,
        Bookingdates bookingdates,
        String additionalneeds) {

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Bookingdates(
            String checkin,
            String checkout) {
    }
}
