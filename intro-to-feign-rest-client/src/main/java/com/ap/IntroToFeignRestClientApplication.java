package com.ap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;

@SpringBootApplication
@RestController
public class IntroToFeignRestClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntroToFeignRestClientApplication.class, args);
	}

	@RequestMapping("/books/{isbn}")
	public String getBook(@PathVariable String isbn){
		String responce = "{\n" +
				"    \"book\": {\n" +
				"      \"isbn\": \"1447264533\",\n" +
				"      \"author\": \"Margaret Mitchell\",\n" +
				"      \"title\": \"Gone with the Wind\",\n" +
				"      \"synopsis\": null,\n" +
				"      \"language\": null\n" +
				"    },\n" +
				"    \"links\": [\n" +
				"      {\n" +
				"        \"rel\": \"self\",\n" +
				"        \"href\": \"http://localhost:8081/api/books/1447264533\"\n" +
				"      }\n" +
				"    ]\n" +
				"  }";
		return responce;
	}
}
