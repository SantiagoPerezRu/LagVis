-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 09-05-2025 a las 15:21:59
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `lagvis2`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `comunidades`
--

CREATE TABLE `comunidades` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `comunidades`
--

INSERT INTO `comunidades` (`id`, `nombre`) VALUES
(1, 'Andalucía'),
(2, 'Aragón'),
(3, 'Asturias'),
(12, 'Canarias'),
(4, 'Cantabria'),
(6, 'Castilla y León'),
(5, 'Castilla-La Mancha'),
(7, 'Cataluña'),
(14, 'Comunidad de Madrid'),
(8, 'Comunidad Valenciana'),
(9, 'Extremadura'),
(10, 'Galicia'),
(11, 'Illes Balears'),
(13, 'La Rioja'),
(16, 'Navarra'),
(17, 'País Vasco'),
(15, 'Región de Murcia');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `convenios`
--

CREATE TABLE `convenios` (
  `id` int(11) NOT NULL,
  `sector_id` int(11) NOT NULL,
  `comunidad_id` int(11) NOT NULL,
  `salario_minimo` decimal(10,2) NOT NULL,
  `dias_libres` int(11) NOT NULL,
  `detalles` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `favoritos`
--

CREATE TABLE `favoritos` (
  `id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `convenio_id` int(11) DEFAULT NULL,
  `noticia_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `noticias`
--

CREATE TABLE `noticias` (
  `id` int(11) NOT NULL,
  `titulo` varchar(255) NOT NULL,
  `contenido` text NOT NULL,
  `fecha_publicacion` date NOT NULL,
  `fuente` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sectores`
--

CREATE TABLE `sectores` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `sectores`
--

INSERT INTO `sectores` (`id`, `nombre`) VALUES
(2, 'Construcción'),
(1, 'Hostelería'),
(3, 'Informática');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL,
  `uid` varchar(255) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `apellido2` varchar(100) DEFAULT NULL,
  `edad` date NOT NULL,
  `sector_id` int(11) DEFAULT NULL,
  `comunidad_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `uid`, `nombre`, `apellido`, `apellido2`, `edad`, `sector_id`, `comunidad_id`) VALUES
(3, 'qrgnWyiPXQZFB5xWnSsxosWvclV2', 'Santiago', 'Perez', 'Diaz', '0000-00-00', 1, 14),
(4, 'ykrbGGQk7iPpltzupBtYJYAItLJ3', 'coloma', 'diaz', 'rubin', '0000-00-00', 1, 14);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `comunidades`
--
ALTER TABLE `comunidades`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `convenios`
--
ALTER TABLE `convenios`
  ADD PRIMARY KEY (`id`),
  ADD KEY `sector_id` (`sector_id`),
  ADD KEY `comunidad_id` (`comunidad_id`);

--
-- Indices de la tabla `favoritos`
--
ALTER TABLE `favoritos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `usuario_id` (`usuario_id`),
  ADD KEY `convenio_id` (`convenio_id`),
  ADD KEY `noticia_id` (`noticia_id`);

--
-- Indices de la tabla `noticias`
--
ALTER TABLE `noticias`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `sectores`
--
ALTER TABLE `sectores`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uid` (`uid`),
  ADD KEY `sector_id` (`sector_id`),
  ADD KEY `comunidad_id` (`comunidad_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `comunidades`
--
ALTER TABLE `comunidades`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT de la tabla `convenios`
--
ALTER TABLE `convenios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `favoritos`
--
ALTER TABLE `favoritos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `noticias`
--
ALTER TABLE `noticias`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `sectores`
--
ALTER TABLE `sectores`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `convenios`
--
ALTER TABLE `convenios`
  ADD CONSTRAINT `convenios_ibfk_1` FOREIGN KEY (`sector_id`) REFERENCES `sectores` (`id`),
  ADD CONSTRAINT `convenios_ibfk_2` FOREIGN KEY (`comunidad_id`) REFERENCES `comunidades` (`id`);

--
-- Filtros para la tabla `favoritos`
--
ALTER TABLE `favoritos`
  ADD CONSTRAINT `favoritos_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  ADD CONSTRAINT `favoritos_ibfk_2` FOREIGN KEY (`convenio_id`) REFERENCES `convenios` (`id`),
  ADD CONSTRAINT `favoritos_ibfk_3` FOREIGN KEY (`noticia_id`) REFERENCES `noticias` (`id`);

--
-- Filtros para la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`sector_id`) REFERENCES `sectores` (`id`),
  ADD CONSTRAINT `usuarios_ibfk_2` FOREIGN KEY (`comunidad_id`) REFERENCES `comunidades` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
