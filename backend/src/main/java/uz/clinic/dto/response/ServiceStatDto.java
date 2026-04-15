package uz.clinic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceStatDto {
    private String name;
    private long count;
}