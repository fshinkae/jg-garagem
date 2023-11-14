package com.example.garagem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Vehicle {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@ManyToOne
	@JoinColumn(name = "user_id")
	@JsonIgnore
	private User user;

	@JsonIgnore
	@OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<VehicleImages> images;
}
