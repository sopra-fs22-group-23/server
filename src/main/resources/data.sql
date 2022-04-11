-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 11, 2022 at 05:40 PM
-- Server version: 10.4.22-MariaDB
-- PHP Version: 8.1.2

--
-- Database: `testdbsopra`
--

--
-- Dumping data for table `user`
--
INSERT INTO `user` (`id`, `biography`, `birthday`, `email`, `name`, `password`, `status`, `token`, `username`) VALUES
    (1, NULL, NULL, NULL, 'test', '098f6bcd4621d373cade4e832627b4f6', 0, '1', 'test'),
    (4, NULL, NULL, NULL, 'secondUser', '098f6bcd4621d373cade4e832627b4f6', 0, '2', 'secondUser');


--
-- Dumping data for table `event`
--
INSERT INTO `event` (`id`, `description`, `event_date`, `event_location`, `picture`, `status`, `title`, `type`) VALUES
    (2, 'string', '2022-04-11', NULL, 'string', 0, 'string', 0);

--
-- Dumping data for table `eventuser`
--

INSERT INTO `eventuser` (`event_user_id`, `creation_date`, `role`, `status`, `event_id`, `user_id`) VALUES
    (3, '2022-04-11', 0, 2, 2, 1);

--
-- Dumping data for table `hibernate_sequence`
--
--
-- INSERT INTO `hibernate_sequence` (`next_val`) VALUES
--     (4);




-- COMMIT;


