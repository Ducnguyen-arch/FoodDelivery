package com.foodecommerce.foodies.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodResponse {

    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private String category;

}
