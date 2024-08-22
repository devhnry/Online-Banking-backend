package org.henry.onlinebankingsystemp.dto;

public record LoginRequestDto(
    String email,
    String password
){
    public static void validate(LoginRequestDto dto) {
        if (dto.email == null || !dto.email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("A valid email is required.");
        }
        if (dto.password == null || dto.password.length() < 8) {
            throw new IllegalArgumentException("Password is required");
        }
    }
}
