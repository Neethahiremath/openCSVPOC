package com.opencsv.poc.model;


import com.opencsv.bean.CsvBindByPosition;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Animal {

    @CsvBindByPosition(position = 0)
    private String animalId;
    @CsvBindByPosition(position = 1)
    private String animalName;

}
