package ua.ukrpost.dto.error;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationErrorDto {
    private List<FieldErrorDto> fieldErrors = new ArrayList<>();

    public void addFieldError(String path, String message) {
        FieldErrorDto error = new FieldErrorDto(path, message);
        fieldErrors.add(error);
    }
}
