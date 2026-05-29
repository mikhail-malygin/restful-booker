package models.lombok;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBookingDTO {

    private Integer bookingid;
    private Booking booking;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Booking {
        private String firstname;
        private String lastname;
        private Integer totalprice;
        private Boolean depositpaid;
        private BookingDates bookingdates;
        private String additionalneeds;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class BookingDates {
            private String checkin;
            private String checkout;
        }
    }

}
