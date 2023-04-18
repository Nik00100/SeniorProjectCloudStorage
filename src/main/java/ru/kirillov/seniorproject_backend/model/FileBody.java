package ru.kirillov.seniorproject_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileBody {
    @JsonProperty("filename")
    @NotNull(message = "Invalid name: not Null")
    @NotEmpty(message = "Invalid name: not Empty")
    @NotBlank(message = "Invalid name: Must not contain only spaces")
    private String fileName;
}
