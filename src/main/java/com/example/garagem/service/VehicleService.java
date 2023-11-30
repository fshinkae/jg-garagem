package com.example.garagem.service;

import com.example.garagem.dto.VehicleDTO;
import com.example.garagem.model.User;
import com.example.garagem.model.Vehicle;
import com.example.garagem.model.VehicleImages;
import com.example.garagem.repository.UserRepository;
import com.example.garagem.repository.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VehicleService {

	private final VehicleRepository vehicleRepository;
	private final UserRepository userRepository;

	@Autowired
	public VehicleService(VehicleRepository vehicleRepository,
												UserRepository userRepository) {
		this.vehicleRepository = vehicleRepository;
		this.userRepository = userRepository;
	}

	public Vehicle saveVehicle(Vehicle vehicle) {
		return vehicleRepository.save(vehicle);
	}

	public List<VehicleDTO> getAllVehicleDTO() {
		List<Vehicle> vehicles = vehicleRepository.findAll();
		List<VehicleDTO> vehicleList = new ArrayList<>();

		for (Vehicle vehicle : vehicles) {
			if (vehicle.getUser() != null) {
				VehicleDTO vehicleDTO = convertToDTO(vehicle);
				vehicleList.add(vehicleDTO);
			}
		}

		return vehicleList;
	}


	public Optional<Vehicle> getVehicleById(Long id) {
		return vehicleRepository.findById(id);
	}

	public void deleteVehicle(Long id) {
		vehicleRepository.deleteById(id);
	}

	public List<VehicleDTO> filterVehicles(Specification<Vehicle> spec) {
		List<Vehicle> filteredVehicles = vehicleRepository.findAll(spec);
		return filteredVehicles.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	private VehicleDTO convertToDTO(Vehicle vehicle) {
		VehicleDTO vehicleDTO = new VehicleDTO();
		vehicleDTO.setId(vehicle.getId());
		vehicleDTO.setVehicleType(vehicle.getVehicleType());
		vehicleDTO.setMake(vehicle.getMake());
		vehicleDTO.setModel(vehicle.getModel());
		vehicleDTO.setYear(vehicle.getYear());
		vehicleDTO.setColor(vehicle.getColor());
		vehicleDTO.setPrice(vehicle.getPrice());
		vehicleDTO.setMileage(vehicle.getMileage());
		vehicleDTO.setFuelType(vehicle.getFuelType());
		vehicleDTO.setTransmission(vehicle.getTransmission());
		vehicleDTO.setEngineSize(vehicle.getEngineSize());
		vehicleDTO.setUser_id(vehicle.getUser().getId());
		vehicleDTO.setDescription(vehicle.getDescription());

		return vehicleDTO;
	}

	public void uploadImage(Long vehicleId, MultipartFile file) {
		Optional<Vehicle> vehicleOptional = vehicleRepository.findById(vehicleId);

		if (vehicleOptional.isPresent()) {
			Vehicle vehicle = vehicleOptional.get();

			try {
				byte[] imageData = saveImage(file);
				VehicleImages image = new VehicleImages();
				image.setImageData(imageData);
				image.setVehicle(vehicle);
				vehicle.getImages().add(image);

				vehicleRepository.save(vehicle);
			} catch (IOException e) {
				throw new RuntimeException("Erro ao salvar a imagem: " + e.getMessage());
			}
		} else {
			throw new EntityNotFoundException("Veículo não encontrado");
		}
	}

	private byte[] saveImage(MultipartFile file) throws IOException {
		if (file != null && !file.isEmpty()) {
			try {
				byte[] imageBytes = file.getBytes();
				return imageBytes;
			} catch (IOException e) {
				throw new IOException("Falha ao salvar a imagem: " + e.getMessage());
			}
		}
		return null;
	}

	private String generateUniqueFileName(String originalFileName) {
		String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
		return UUID.randomUUID().toString() + extension;
	}
}
