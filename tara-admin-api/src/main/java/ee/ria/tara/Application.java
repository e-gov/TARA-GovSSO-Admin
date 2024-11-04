package ee.ria.tara;

import co.elastic.apm.attach.ElasticApmAttacher;
import ee.ria.tara.configuration.HomeController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = HomeController.class)})
public class Application {

    public static void main(String[] args) {
        ElasticApmAttacher.attach();
        SpringApplication.run(Application.class, args);
    }

}
