package rj.treemanager;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@OpenAPIDefinition(info = @Info(
        title = "TreeManager Application",
        description = "Application for management of tree structure holding numeric values")
)
public class TreeManagerBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TreeManagerBeApplication.class, args);
    }

}
