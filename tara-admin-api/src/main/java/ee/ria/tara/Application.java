package ee.ria.tara;


import ee.ria.tara.configuration.HomeController;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = HomeController.class)})
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        configureApplication(new SpringApplicationBuilder())
                .run(args);
    }

    private static SpringApplicationBuilder configureApplication(SpringApplicationBuilder builder) {
        return builder.sources(Application.class)
                .bannerMode(Banner.Mode.OFF);
    }

}
