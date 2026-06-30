package com.foodecommerce.foodies.service;

import com.foodecommerce.foodies.io.FoodRequest;
import com.foodecommerce.foodies.io.FoodResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FoodService {

    String uploadFile(MultipartFile file);

    FoodResponse addFood(FoodRequest foodRequest, MultipartFile file);

    List<FoodResponse> fetchFood();

    FoodResponse fetchFoodById(String id);

    FoodResponse updateFood(FoodRequest foodRequest);

    boolean deletedFile(String fileName);

    void deleteFoodById(String id);
}
