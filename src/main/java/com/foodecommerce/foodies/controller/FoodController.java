package com.foodecommerce.foodies.controller;

import com.foodecommerce.foodies.io.FoodRequest;
import com.foodecommerce.foodies.io.FoodResponse;
import com.foodecommerce.foodies.service.FoodService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.thirdparty.jackson.core.JsonProcessingException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;


@RestController
@RequestMapping("/api/foods")
@AllArgsConstructor
@CrossOrigin("*")
public class FoodController {

    private final FoodService foodService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public List<FoodResponse> fetchFood() {
        return foodService.fetchFood();
    }

    @PostMapping
    public FoodResponse createFood(@RequestPart("food") String foodString,
                                   @RequestPart("file") MultipartFile file) throws JsonProcessingException {
            FoodRequest foodRequest = objectMapper.readValue(foodString, FoodRequest.class);
            return foodService.addFood(foodRequest, file);
    }

    @GetMapping("/{id}")
    public FoodResponse fetchFoodById(@PathVariable String id) {
        return foodService.fetchFoodById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFoodById(@PathVariable String id) {
        foodService.deleteFoodById(id);
    }
}
