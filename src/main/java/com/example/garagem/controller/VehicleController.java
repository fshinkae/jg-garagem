package com.example.garagem.controller;

import com.example.garagem.dto.VehicleDTO;
import com.example.garagem.model.User;
import com.example.garagem.model.Vehicle;
import com.example.garagem.model.VehicleImages;
import com.example.garagem.service.AuthServiceImpl;
import com.example.garagem.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

	private final VehicleService vehicleService;
	private final AuthServiceImpl userService;

	@Autowired
	public VehicleController(VehicleService vehicleService, AuthServiceImpl userService) {
		this.vehicleService = vehicleService;
		this.userService = userService;
	}

	@PostMapping("/add")
	public ResponseEntity<Vehicle> createVehicle(
			@RequestBody Vehicle vehicle, Authentication authentication) {
		String email = authentication.getName();
		Optional<User> user = userService.searchUser(email);
		vehicle.setUser(user.get());
		Vehicle savedVehicle = vehicleService.saveVehicle(vehicle);
		return new ResponseEntity<>(savedVehicle, HttpStatus.CREATED);
	}

	@GetMapping("/all")
	public ResponseEntity<List<VehicleDTO>> getAllVehicles() {
		List<VehicleDTO> vehicles = vehicleService.getAllVehicleDTO();
		return new ResponseEntity<>(vehicles, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Vehicle> getVehicleById(
			@PathVariable Long id) {
		Optional<Vehicle> vehicle = vehicleService.getVehicleById(id);
		return vehicle.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Vehicle> updateVehicle(
			@PathVariable Long id,
			@RequestBody Vehicle vehicle) {
		Optional<Vehicle> existingVehicle = vehicleService.getVehicleById(id);
		if (existingVehicle.isPresent()) {
			Vehicle savedVehicle = vehicleService.updateVehicle(id, vehicle);
			return new ResponseEntity<>(savedVehicle, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteVehicle(
			@PathVariable Long id) {
		Optional<Vehicle> existingVehicle = vehicleService.getVehicleById(id);
		if (existingVehicle.isPresent()) {
			vehicleService.deleteVehicle(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/filter")
	public List<VehicleDTO> filterVehicles(
			@RequestParam(name = "make", required = false) String make) {
		Specification<Vehicle> spec = Specification.where(null);

		if (make != null) {
			spec = spec.and((root, query, criteriaBuilder) ->
					criteriaBuilder.equal(root.get("make"), make));
		}
		return vehicleService.filterVehicles(spec);
	}

	@PostMapping("/uploadImage/{vehicleId}")
	public ResponseEntity<String> uploadImage(
			@PathVariable Long vehicleId,
			@RequestParam("file") MultipartFile file) {
		try {
			vehicleService.uploadImage(vehicleId, file);
			return ResponseEntity.ok("Imagem salva com sucesso");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro ao salvar a imagem: " + e.getMessage());
		}
	}

	@GetMapping("/images/{vehicleId}")
	public ResponseEntity<List<byte[]>> getAllVehicleImages(@PathVariable Long vehicleId) {
		Optional<Vehicle> vehicleOptional = vehicleService.getVehicleById(vehicleId);

		if (vehicleOptional.isPresent()) {
			Vehicle vehicle = vehicleOptional.get();
			List<VehicleImages> images = vehicle.getImages();

			if (images != null && !images.isEmpty()) {
				List<byte[]> imageDataList = images.stream()
						.map(VehicleImages::getImageData)
						.collect(Collectors.toList());
				return ResponseEntity.ok(imageDataList);
			} else {
				return ResponseEntity.notFound().build();
			}
		} else {
			return ResponseEntity.notFound().build();
		}
	}

}
