package com.foodecommerce.foodies.service;

import com.foodecommerce.foodies.entity.FoodEntity;
import com.foodecommerce.foodies.io.FoodRequest;
import com.foodecommerce.foodies.io.FoodResponse;
import com.foodecommerce.foodies.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class FoodServiceImpl implements FoodService {

    @Autowired
    private  S3Client s3Client;

    @Autowired
    private  FoodRepository foodRepository;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file) {
        String fileNameExtension = Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        String key = UUID.randomUUID().toString() + "." + fileNameExtension;
        try {

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .acl("public-read")
                    .contentType(file.getContentType())
                    .build();
            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            if (response.sdkHttpResponse().isSuccessful()) {
                return "http://" + bucketName + ".s3.amazonaws.com/" + key;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed");
            }
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while uploading file ", ex);
        }
    }

    @Override
    public FoodResponse addFood(FoodRequest foodRequest, MultipartFile file) {
        FoodEntity newFoodEntity = convertToEntity(foodRequest);
        String imageUrl = uploadFile(file);
        newFoodEntity.setImageUrl(imageUrl);
        foodRepository.save(newFoodEntity);
        return convertToResponse(newFoodEntity);

    }

    private FoodEntity convertToEntity(FoodRequest foodRequest) {
        return FoodEntity.builder()
                .name(foodRequest.getName())
                .description(foodRequest.getDescription())
                .category(foodRequest.getCategory())
                .price(foodRequest.getPrice())
                .build();
    }

    private FoodResponse convertToResponse(FoodEntity foodEntity) {
        return FoodResponse.builder()
                .id(foodEntity.getId())
                .name(foodEntity.getName())
                .category(foodEntity.getCategory())
                .description(foodEntity.getDescription())
                .price(foodEntity.getPrice())
                .imageUrl(foodEntity.getImageUrl())
                .build();
    }


    @Override
    public List<FoodResponse> fetchFood() {
        List<FoodEntity> databaseFoodEntries =  foodRepository.findAll();
       return databaseFoodEntries.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public FoodResponse fetchFoodById(String id) {
       FoodEntity isExistFood = foodRepository.findById(id)
               .orElseThrow(() -> new RuntimeException ("Food is not found id: {}"+ id));
       return convertToResponse(isExistFood);
    }


    @Override
    public FoodResponse updateFood(FoodRequest foodRequest) {
        return null;
    }

    @Override
    public boolean deletedFile(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
        return true;
    }

    @Override
    public void deleteFoodById(String id) {
        FoodResponse response = fetchFoodById(id);
        String imageUrl = response.getImageUrl();
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        boolean isFileDeleted = deletedFile(fileName);

        if (isFileDeleted) {
            foodRepository.deleteById(response.getId());
        }


    }
}
