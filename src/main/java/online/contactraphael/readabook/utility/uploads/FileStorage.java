package online.contactraphael.readabook.utility.uploads;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public record FileStorage(String directory) {

}
