package data;

import lombok.Data;
import lombok.Getter;
import models.lombok.BookingDTO;
import net.datafaker.Faker;

import java.util.concurrent.TimeUnit;

@Data
public class BookingDataGenerator {

    private final Faker faker = new Faker();
    private final String datePattern = "yyyy-MM-dd";
    private final String firstName = faker.name().firstName();
    private final String lastName = faker.name().lastName();
    @Getter
    private final int numberOfBookings = faker.number().numberBetween(2, 5);

    public BookingDTO generateDataForBooking() {

        return BookingDTO.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.number().numberBetween(50, 2000))
                .depositpaid(faker.bool().bool())
                .additionalneeds(faker.food().dish())
                .bookingdates(BookingDTO.Bookingdates.builder()
                        .checkin(faker.timeAndDate().future(60, 30, TimeUnit.DAYS, datePattern))
                        .checkout(faker.timeAndDate().future(70, 31, TimeUnit.DAYS, datePattern))
                        .build())
                .build();
    }

    public BookingDTO generateDataBookingForOneUser() {

        return BookingDTO.builder()
                .firstname(firstName)
                .lastname(lastName)
                .totalprice(faker.number().numberBetween(50, 2000))
                .depositpaid(faker.bool().bool())
                .additionalneeds(faker.food().dish())
                .bookingdates(BookingDTO.Bookingdates.builder()
                        .checkin(faker.timeAndDate().future(60, 30, TimeUnit.DAYS, datePattern))
                        .checkout(faker.timeAndDate().future(70, 31, TimeUnit.DAYS, datePattern))
                        .build())
                .build();
    }


}
