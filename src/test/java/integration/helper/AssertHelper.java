package integration.helper;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

public class AssertHelper {
    
    public static void assertTimeBetween(String message, LocalDateTime begin, LocalDateTime time, LocalDateTime end) {
        assertThat(message, time, anyOf(equalTo(begin), greaterThan(begin)));
        assertThat(message, time, anyOf(equalTo(end), lessThan(end)));
    }
}
