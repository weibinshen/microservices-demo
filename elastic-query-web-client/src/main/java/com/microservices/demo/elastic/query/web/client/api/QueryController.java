package com.microservices.demo.elastic.query.web.client.api;

import com.microservices.demo.elastic.query.web.client.model.ElasticQueryWebClientRequestModel;
import com.microservices.demo.elastic.query.web.client.model.ElasticQueryWebClientResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

// This is the REST controller for the web client
// Here however, we do not use @RestController,
// because to work with Thymeleaf, we shouldn't convert the response to JSON.
@Controller
public class QueryController {

    private static final Logger LOG = LoggerFactory.getLogger(QueryController.class);

    @GetMapping("")
    public String index() {
        return "index";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }

    @GetMapping("/home")
    public String home(Model model) {
        // the input model needs to be populated in this mapped responder
        // And then the populated attributes will be read by thymeleaf templates
        model.addAttribute("elasticQueryWebClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());
        // Return the name of the template that the model is intended for.
        return "home";
    }

    @PostMapping("/query-by-text")
    public String queryByText(@Valid ElasticQueryWebClientRequestModel requestModel,
                              Model model) {
        LOG.info("Querying with text {}", requestModel.getText());
        List<ElasticQueryWebClientResponseModel> responseModels = new ArrayList<>();
        responseModels.add(ElasticQueryWebClientResponseModel.builder()
                .id("1")
                .text(requestModel.getText())
                .build());
        model.addAttribute("elasticQueryWebClientResponseModels", responseModels);
        model.addAttribute("searchText", requestModel.getText());
        model.addAttribute("elasticQueryWebClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());
        // Return the name of the template that the model is intended for.
        return "home";
    }

}
