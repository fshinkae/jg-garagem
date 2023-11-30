package com.example.garagem.dto;

import com.example.garagem.model.VehicleImages;
import lombok.Data;

import java.util.List;

@Data
public class VehicleDTO {
	private Long id;
	private String vehicleType;
	private String make;
	private String model;
	private Integer year;
	private String color;
	private Double price;
	private Double mileage;
	private String fuelType;
	private String transmission;
	private Double engineSize;
	private Long user_id;
	private String description;
}
