-- Cria a tabela users
CREATE TABLE IF NOT EXISTS users (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     email VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL
    );

-- Cria a tabela roles
CREATE TABLE IF NOT EXISTS roles (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL
    );

-- Cria a tabela users_roles para representar o relacionamento MUITOS para MUITOS
CREATE TABLE IF NOT EXISTS users_roles (
                                           user_id INT,
                                           role_id INT,
                                           PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES roles (id)
    );

-- Cria a tabela vehicles
CREATE TABLE IF NOT EXISTS vehicle (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       vehicle_type VARCHAR(255),
    make VARCHAR(255),
    model VARCHAR(255),
    year INT,
    color VARCHAR(255),
    price DOUBLE,
    mileage DOUBLE,
    fuel_type VARCHAR(255),
    transmission VARCHAR(255),
    engine_size DOUBLE,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users (id)
    );

-- Cria a tabela vehicle_images
CREATE TABLE IF NOT EXISTS vehicle_images (
    id INT AUTO_INCREMENT PRIMARY KEY,
    image_data LONGBLOB, -- Usando LONGBLOB para armazenar dados de imagem binária
    vehicle_id INT,
    FOREIGN KEY (vehicle_id) REFERENCES vehicle (id)
    );